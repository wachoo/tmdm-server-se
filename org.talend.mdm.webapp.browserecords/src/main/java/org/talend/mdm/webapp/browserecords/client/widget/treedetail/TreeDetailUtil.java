// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.BreadCrumbModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsView;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.LabelUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.BreadCrumb;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.browserecords.client.widget.TabItemListener;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TreeDetail tool class
 */
public class TreeDetailUtil {

    public static Widget createWidget(final ItemNodeModel itemNode, final ViewBean viewBean, Map<String, Field<?>> fieldMap,
            ClickHandler h, ItemsDetailPanel itemsDetailPanel) {
        return createWidget(itemNode, viewBean, fieldMap, h, null, itemsDetailPanel);
    }

    public static Widget createWidget(final ItemNodeModel itemNode, final ViewBean viewBean, Map<String, Field<?>> fieldMap,
            ClickHandler h, String operation, final ItemsDetailPanel itemsDetailPanel) {

        HorizontalPanel hp = new HorizontalPanel();
        // create Field
        String xPath = itemNode.getBindingPath();
        String typePath = itemNode.getTypePath();
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath);
        String dynamicLabel = typeModel.getLabel(Locale.getLanguage());
        HTML label = new HTML();
        String html = itemNode.getLabel();

        if (LabelUtil.isDynamicLabel(dynamicLabel)) {
            if (itemNode.getDynamicLabel() != null && !"".equals(itemNode.getDynamicLabel())) { //$NON-NLS-1$
                html = itemNode.getDynamicLabel();
            } else {
                html = LabelUtil.getNormalLabel(html);
            }
        }

        if (itemNode.isKey() || typeModel.getMinOccurs() >= 1)
            html = html + "<span style=\"color:red\"> *</span>"; //$NON-NLS-1$

        if (null != itemNode.getDescription() && (itemNode.getDescription().trim().length() > 0) && xPath.indexOf("/") > -1) { //$NON-NLS-1$
            html = html + "<img style='margin-left:16px;' src='/talendmdm/secure/img/genericUI/information_icon.png' title='" + LabelUtil.convertSpecialHTMLCharacter(itemNode.getDescription()) + "' />"; //$NON-NLS-1$ //$NON-NLS-2$         
        }
        label.setHTML(html);
        hp.add(label);
        if (typeModel.isSimpleType()
                || (!typeModel.isSimpleType() && ((ComplexTypeModel) typeModel).getReusableComplexTypes().size() > 0)) {

            if (typeModel.getType().equals(DataTypeConstants.AUTO_INCREMENT)
                    && ItemDetailToolBar.DUPLICATE_OPERATION.equals(operation)) {
                itemNode.setObjectValue(""); //$NON-NLS-1$
            }

            Field<?> field = TreeDetailGridFieldCreator.createField(itemNode, typeModel, Locale.getLanguage(), fieldMap,
                    operation, itemsDetailPanel);
            field.setWidth(200);
            field.addListener(Events.Blur, new Listener<FieldEvent>() {

                public void handleEvent(FieldEvent be) {
                    AppEvent app = new AppEvent(BrowseRecordsEvents.ExecuteVisibleRule);
                    ItemNodeModel parent = CommonUtil.recrusiveRoot(itemNode);
                    // maybe need other methods to get entire tree
                    if (parent == null || parent.getChildCount() == 0) {
                        return;
                    }

                    app.setData(parent);
                    app.setData("viewBean", viewBean); //$NON-NLS-1$
                    app.setData(BrowseRecordsView.ITEMS_DETAIL_PANEL, itemsDetailPanel);
                    Dispatcher.forwardEvent(app);
                }
            });
            hp.add(field);

        }

        if (typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1) {
            Image addNodeImg = new Image("/talendmdm/secure/img/genericUI/add.png"); //$NON-NLS-1$
            addNodeImg.getElement().setId("Add"); //$NON-NLS-1$
            addNodeImg.setTitle(MessagesFactory.getMessages().clone_title());
            addNodeImg.getElement().getStyle().setMarginLeft(5D, Unit.PX);
            addNodeImg.getElement().getStyle().setMarginTop(5D, Unit.PX);
            if(!typeModel.isReadOnly())
                addNodeImg.addClickHandler(h);
            Image removeNodeImg = new Image("/talendmdm/secure/img/genericUI/delete.png"); //$NON-NLS-1$
            removeNodeImg.getElement().setId("Remove"); //$NON-NLS-1$
            removeNodeImg.setTitle(MessagesFactory.getMessages().remove_title());
            removeNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
            addNodeImg.getElement().getStyle().setMarginTop(5D, Unit.PX);
            if(!typeModel.isReadOnly())
                removeNodeImg.addClickHandler(h);

            hp.add(addNodeImg);
            hp.setCellVerticalAlignment(addNodeImg, VerticalPanel.ALIGN_BOTTOM);
            hp.add(removeNodeImg);
            hp.setCellVerticalAlignment(removeNodeImg, VerticalPanel.ALIGN_BOTTOM);
            if (!typeModel.isSimpleType() && itemNode.getParent() != null) {
                Image cloneNodeImg = new Image("/talendmdm/secure/img/genericUI/add-group.png"); //$NON-NLS-1$
                cloneNodeImg.getElement().setId("Clone"); //$NON-NLS-1$
                cloneNodeImg.setTitle(MessagesFactory.getMessages().deepclone_title());
                cloneNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
                if(!typeModel.isReadOnly())
                    cloneNodeImg.addClickHandler(h);
                hp.add(cloneNodeImg);
                hp.setCellVerticalAlignment(cloneNodeImg, VerticalPanel.ALIGN_BOTTOM);
            }
        }

        hp.setCellWidth(label, "200px"); //$NON-NLS-1$

        hp.setVisible(typeModel.isVisible());

        return hp;
    }
    
    public static void initItemsDetailPanelById(final String fromWhichApp, String ids, final String concept,
            final Boolean isFkToolBar, final Boolean isHierarchyCall) {
        initItemsDetailPanelById(fromWhichApp, ids, concept, isFkToolBar, isHierarchyCall, ItemDetailToolBar.VIEW_OPERATION);

    }

    public static void initItemsDetailPanelById(final String fromWhichApp, String ids, final String concept,
            final Boolean isFkToolBar, final Boolean isHierarchyCall, final String operation) {
        String[] idArr = ids.split(","); //$NON-NLS-1$
        final ItemsDetailPanel panel = new ItemsDetailPanel();
        final BrowseRecordsServiceAsync brService = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        brService.getItemBeanById(concept, idArr, Locale.getLanguage(), new SessionAwareAsyncCallback<ItemBean>() {

            public void onSuccess(final ItemBean item) {
                brService.getView("Browse_items_" + concept, Locale.getLanguage(), new SessionAwareAsyncCallback<ViewBean>() { //$NON-NLS-1$

                            public void onSuccess(ViewBean viewBean) {
                                ItemPanel itemPanel = new ItemPanel(viewBean, item, operation, panel);
                                itemPanel.getToolBar().setOutMost(true);
                                itemPanel.getToolBar().setFkToolBar(isFkToolBar);
                                itemPanel.getToolBar().setHierarchyCall(isHierarchyCall);

                                List<BreadCrumbModel> breads = new ArrayList<BreadCrumbModel>();
                                if (item != null) {
                                    breads.add(new BreadCrumbModel("", BreadCrumb.DEFAULTNAME, null, null, false)); //$NON-NLS-1$
                                    breads.add(new BreadCrumbModel(item.getConcept(), item.getLabel(), item.getIds(), item
                                            .getDisplayPKInfo().equals(item.getLabel()) ? null : item.getDisplayPKInfo(), true));
                                }

                                panel.setId(item.getIds());
                                panel.initBanner(item.getPkInfoList(), item.getDescription());
                                panel.addTabItem(item.getLabel(), itemPanel, ItemsDetailPanel.SINGLETON, item.getIds());
                                panel.initBreadCrumb(new BreadCrumb(breads, panel));

                                TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(item.getConcept());

                                String tabItemId = fromWhichApp + typeModel.getLabel(Locale.getLanguage())
                                        + " " + panel.getItemId(); //$NON-NLS-1$
                                panel.setHeading(tabItemId);
                                panel.setItemId(tabItemId);
                                renderTreeDetailPanel(tabItemId, panel);
                            }

                        });
            }
        });
    }

    public static void initItemsDetailPanelByItemPanel(ViewBean viewBean, ItemBean itemBean, boolean isFkToolBar,
            boolean isHierarchyCall) {

        final ItemsDetailPanel itemsDetailPanel = new ItemsDetailPanel();

        ItemPanel itemPanel = new ItemPanel(viewBean, itemBean, ItemDetailToolBar.DUPLICATE_OPERATION, itemsDetailPanel);
        itemPanel.getToolBar().setOutMost(true);
        itemPanel.getToolBar().setFkToolBar(isFkToolBar);
        itemPanel.getToolBar().setHierarchyCall(isHierarchyCall);
        
        List<BreadCrumbModel> breads = new ArrayList<BreadCrumbModel>();
        if (itemBean != null) {
            breads.add(new BreadCrumbModel("", BreadCrumb.DEFAULTNAME, null, null, false)); //$NON-NLS-1$
            breads.add(new BreadCrumbModel(itemBean.getConcept(), itemBean.getLabel(), itemBean.getIds(), itemBean
                    .getDisplayPKInfo().equals(itemBean.getLabel()) ? null : itemBean.getDisplayPKInfo(), true));
        }

        itemsDetailPanel.setId(itemBean.getIds());
        itemsDetailPanel.initBanner(itemBean.getPkInfoList(), itemBean.getDescription());
        itemsDetailPanel.addTabItem(itemBean.getLabel(), itemPanel, ItemsDetailPanel.SINGLETON, itemBean.getIds());
        itemsDetailPanel.initBreadCrumb(new BreadCrumb(breads, itemsDetailPanel));

        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(itemBean.getConcept());
        String tabItemId = typeModel.getLabel(Locale.getLanguage()) + " " + new Date().getTime(); //$NON-NLS-1$
        itemsDetailPanel.setHeading(typeModel.getLabel(Locale.getLanguage()));
        itemsDetailPanel.setItemId(tabItemId);
        renderTreeDetailPanel(tabItemId, itemsDetailPanel);

    }

    /**
     * MessageBox will be displayed when item is updated <br>
     * <li>message info: Current record {0} has been modified. Are you sure you want to close the tab now ?
     */
    public static void checkRecord(final TabItem item, final ItemsDetailPanel itemsDetailPanel, final TabItemListener listener,
            final JavaScriptObject handler) {
        boolean isChangeCurrentRecord;
        if (itemsDetailPanel != null && itemsDetailPanel.getCurrentItemPanel() != null) {
            ItemPanel itemPanel = itemsDetailPanel.getCurrentItemPanel();
            final ItemDetailToolBar toolBar = itemPanel.getToolBar();
            if (itemPanel.getOperation().equals(ItemDetailToolBar.VIEW_OPERATION)
                    || itemPanel.getOperation().equals(ItemDetailToolBar.CREATE_OPERATION)
                    || itemPanel.getOperation().equals(ItemDetailToolBar.DUPLICATE_OPERATION)) {
                ItemNodeModel root = (ItemNodeModel) itemPanel.getTree().getTree().getItem(0).getUserObject();
                isChangeCurrentRecord = root != null ? TreeDetailUtil.isChangeValue(root) : false;
                if (isChangeCurrentRecord) {
                    MessageBox msgBox = MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory
                            .getMessages().msg_confirm_close_tab(root.getLabel()), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                                if (listener != null)
                                    closeTabItem(item, toolBar, listener);
                                else
                                    closeOutTabItem(itemsDetailPanel.getItemId(), handler);
                            } else
                                return;
                        }
                    });
                    msgBox.getDialog().setWidth(550);
                    return;
                } else {
                    if (listener != null)
                        closeTabItem(item, toolBar, listener);
                    else
                        closeOutTabItem(itemsDetailPanel.getItemId(), handler);
                }
            } else {
                if (listener != null)
                    closeTabItem(item, toolBar, listener);
                else
                    closeOutTabItem(itemsDetailPanel.getItemId(), handler);
            }
        }
    }

    private static void closeTabItem(TabItem item, ItemDetailToolBar toolBar, TabItemListener listener) {
        if (toolBar != null && !toolBar.isOutMost() && !toolBar.isHierarchyCall() && !toolBar.isFkToolBar()) {
            ItemsListPanel.getInstance().deSelectCurrentItem();
        }
        listener.isConfirmedTabClose = true;
        item.close();
    }

    public native static void closeOutTabItem(String itemId, JavaScriptObject removeTabEvent)/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        tabPanel.un("beforeremove", removeTabEvent);
        tabPanel.remove(itemId);
    }-*/;

    public native static void renderTreeDetailPanel(String itemId, ItemsDetailPanel detailPanel)/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();  
        var panel = @org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetailUtil::transferTreeDetailPanel(Lorg/talend/mdm/webapp/browserecords/client/widget/ItemsDetailPanel;)(detailPanel);
        var removeTabEvent = function(tabPanel, tabItem){
        if(itemId == tabItem.getId()){
        @org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetailUtil::checkRecord(Lcom/extjs/gxt/ui/client/widget/TabItem;Lorg/talend/mdm/webapp/browserecords/client/widget/ItemsDetailPanel;Lorg/talend/mdm/webapp/browserecords/client/widget/TabItemListener;Lcom/google/gwt/core/client/JavaScriptObject;)(null,detailPanel,null,removeTabEvent);
        return false;
        }else{
        return true;
        }
        };
        tabPanel.on("beforeremove", removeTabEvent);
        tabPanel.add(panel);
        tabPanel.setSelection(itemId);
    }-*/;

    private native static JavaScriptObject transferTreeDetailPanel(ItemsDetailPanel itemDetailPanel)/*-{
        var panel = {
        // imitate extjs's render method, really call gxt code.
        render : function(el){
        var rootPanel = @com.google.gwt.user.client.ui.RootPanel::get(Ljava/lang/String;)(el.id);
        rootPanel.@com.google.gwt.user.client.ui.RootPanel::add(Lcom/google/gwt/user/client/ui/Widget;)(itemDetailPanel);
        },
        // imitate extjs's setSize method, really call gxt code.
        setSize : function(width, height){
        itemDetailPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel::setSize(II)(width, height);
        },
        // imitate extjs's getItemId, really return itemId of ContentPanel of GXT.
        getItemId : function(){
        return itemDetailPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel::getItemId()();
        },
        // imitate El object of extjs
        getEl : function(){
        var el = itemDetailPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel::getElement()();
        return {dom : el};
        },
        // imitate extjs's doLayout method, really call gxt code.
        doLayout : function(){
        return itemDetailPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel::doLayout()();
        },
        title : function(){
        return itemDetailPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel::getHeading()();
        }
        };
        return panel;
    }-*/;

    public static boolean isChangeValue(ItemNodeModel model) {
        if (model.isChangeValue())
            return true;
        for (ModelData node : model.getChildren()) {
            if (isChangeValue((ItemNodeModel) node))
                return true;
        }
        return false;
    }
}
