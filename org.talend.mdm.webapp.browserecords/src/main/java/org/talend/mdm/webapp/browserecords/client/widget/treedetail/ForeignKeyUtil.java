package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC peili.liang class global comment. Detailled comment : support the foreignKey's link and create function.
 */
public class ForeignKeyUtil {

    public static void checkChange(final boolean isCreateForeignKey, final String foreignKeyName, final String ids) {

        Widget widget = ItemsDetailPanel.getInstance().getTabPanel().getItem(0).getWidget(0);
        final ItemNodeModel root;
        if (widget instanceof ItemPanel)
            root = (ItemNodeModel) ((ItemPanel) widget).getTree().getTree().getItem(0).getUserObject();
        else
            root = ((ForeignKeyTreeDetail) widget).getRootModel();
        if (isChangeValue(root)) {
            MessageBox msgBox = MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                    .msg_confirm_save_tree_detail(root.getLabel()), new Listener<MessageBoxEvent>() {

                public void handleEvent(MessageBoxEvent be) {
                    if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                        saveItem(root, isCreateForeignKey, foreignKeyName, ids);
                    } else if (Dialog.NO.equals(be.getButtonClicked().getItemId())) {
                        displayForeignKey(isCreateForeignKey, foreignKeyName, ids);
                    }
                }
            });
            msgBox.getDialog().setWidth(550);
            msgBox.getDialog().setButtons(MessageBox.YESNOCANCEL);
        } else {
            displayForeignKey(isCreateForeignKey, foreignKeyName, ids);
        }

    }

    private static void saveItem(ItemNodeModel model, final boolean isCreateForeignKey, final String foreignKeyName,
            final String ids) {
        final Widget widget = ItemsDetailPanel.getInstance().getTabPanel().getItem(0).getWidget(0);
        ViewBean viewBean = null;
        ItemBean itemBean = null;
        boolean isCreate = false;
        boolean validateSuccess = false;
        if (widget instanceof ItemPanel) {// save primary key
            viewBean = (ViewBean) BrowseRecords.getSession().get(UserSession.CURRENT_VIEW);
            ItemPanel itemPanel = (ItemPanel) widget;
            if (itemPanel.getTree().validateTree()) {
                validateSuccess = true;
                itemBean = itemPanel.getItem();
                isCreate = itemPanel.getOperation().equals(ItemDetailToolBar.CREATE_OPERATION) ? true : false;
            }

        } else if (widget instanceof ForeignKeyTreeDetail) { // save foreign key
            ForeignKeyTreeDetail fkDetail = (ForeignKeyTreeDetail) widget;
            if (fkDetail.validateTree()) {
                validateSuccess = true;
                itemBean = fkDetail.isCreate() ? new ItemBean(fkDetail.getViewBean().getBindingEntityModel().getConceptName(),
                        "", "") : itemBean; //$NON-NLS-1$ //$NON-NLS-2$
                isCreate = fkDetail.isCreate();
            }
        }
        final boolean isCreated = isCreate;
        if (validateSuccess) {
            BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
            service.saveItem(itemBean.getConcept(), itemBean.getIds(), CommonUtil.toXML(model, viewBean), isCreate, Locale
                    .getLanguage(), new SessionAwareAsyncCallback<String>() {

                        @Override
                        protected void doOnFailure(Throwable caught) {
                            String err = caught.getMessage();
                            if (err != null) {
                                if (err.indexOf("ERROR_3:") == 0) { //$NON-NLS-1$
                                    // add for before saving transformer check
                                    MessageBox.alert(MessagesFactory.getMessages().error_title(), err.substring(8), null);
                                } else
                                    MessageBox.alert(MessagesFactory.getMessages().error_title(),
                                            CommonUtil.pickOutISOMessage(err), null);
                            } else
                                super.doOnFailure(caught);
                        }

                        public void onSuccess(String result) {
                            MessageBox.alert(MessagesFactory.getMessages().info_title(), MessagesFactory.getMessages()
                                    .save_success(), null);
                            if (widget instanceof ItemPanel && isCreated) {
                                ItemsListPanel.getInstance().lastPage();
                            }
                            displayForeignKey(isCreateForeignKey, foreignKeyName, ids);
                        }
                    });
        } else {
            MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().save_error(), null);
        }
    }

    public static void displayForeignKey(boolean isCreateForeignKey, final String foreignKeyName, final String ids) {
        Dispatcher dispatch = Dispatcher.get();
        AppEvent event = new AppEvent(BrowseRecordsEvents.CreateForeignKeyView, foreignKeyName);
        if (!isCreateForeignKey) {
            event = new AppEvent(BrowseRecordsEvents.ViewForeignKey);
            event.setData("ids", ids); //$NON-NLS-1$ 
            event.setData("concept", foreignKeyName); //$NON-NLS-1$
        }
        dispatch.dispatch(event);
    }

    private static boolean isChangeValue(ItemNodeModel model) {
        if (model.isChangeValue())
            return true;
        for (ModelData node : model.getChildren()) {
            if (isChangeValue((ItemNodeModel) node))
                return true;
        }
        return false;
    }

    public static List<ItemNodeModel> getAllForeignKeyModelParent(ViewBean viewBean, ItemNodeModel parent) {
        List<ItemNodeModel> list = new ArrayList<ItemNodeModel>();
        for (ModelData child : parent.getChildren()) {
            ItemNodeModel childModel = (ItemNodeModel) child;
            TypeModel tm = viewBean.getBindingEntityModel().getMetaDataTypes().get(childModel.getBindingPath());
            if (tm.getForeignkey() != null && !list.contains(parent))
                list.add(parent);
            else if (tm.getForeignkey() == null)
                list.addAll(getAllForeignKeyModelParent(viewBean, childModel));
        }
        return list;
    }

    public static String transferXpathToLabel(TypeModel fkTypeModel, ViewBean pkViewBean) {
        String xp = fkTypeModel.getXpath();
        StringBuffer sb = new StringBuffer();
        // a/b/c/d
        Stack<String> stack = new Stack<String>();
        do {

            TypeModel tm = pkViewBean.getBindingEntityModel().getMetaDataTypes().get(xp);
            if (tm != null)
                stack.push(tm.getLabel(Locale.getLanguage()));
            xp = xp.substring(0, xp.lastIndexOf("/")); //$NON-NLS-1$

        } while (xp.indexOf("/") != -1); //$NON-NLS-1$
        boolean flag = true;

        while (!stack.isEmpty()) {
            if (flag)
                flag = false;
            else
                sb.append("/"); //$NON-NLS-1$
            sb.append(stack.pop());
        }
        return sb.toString();
    }
}
