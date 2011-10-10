package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
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
            service.saveItem(itemBean.getConcept(), itemBean.getIds(), CommonUtil.toXML(model, viewBean), isCreate,
                    new SessionAwareAsyncCallback<String>() {

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
            MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().save_btn() + " " //$NON-NLS-1$
                    + MessagesFactory.getMessages().message_fail(), null);
        }
    }

    private static void displayForeignKey(boolean isCreateForeignKey, final String foreignKeyName, final String ids) {
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
}
