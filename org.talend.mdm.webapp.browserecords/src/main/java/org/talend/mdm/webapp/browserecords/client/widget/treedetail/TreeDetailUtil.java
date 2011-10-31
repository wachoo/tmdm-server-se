// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
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
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
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
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(xPath);
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

        if (null != itemNode.getDescription() && (itemNode.getDescription().trim().length() > 0) && xPath.indexOf("/") > -1) //$NON-NLS-1$
            html = html
                    + "<img style='margin-left:16px;' src='/talendmdm/secure/img/genericUI/information_icon.gif' title='" + itemNode.getDescription() + "' />"; //$NON-NLS-1$ //$NON-NLS-2$
        label.setHTML(html);
        hp.add(label);
        if (typeModel.isSimpleType()
                || (!typeModel.isSimpleType() && ((ComplexTypeModel) typeModel).getReusableComplexTypes().size() > 0)) {

            Field<?> field = TreeDetailGridFieldCreator.createField(itemNode, typeModel, Locale.getLanguage(), fieldMap,
                    operation, itemsDetailPanel);
            field.setWidth(200);
            field.addListener(Events.Focus, new Listener<FieldEvent>() {

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

        if ((typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1) && typeModel.getForeignkey() == null) {
            Image addNodeImg = new Image("/talendmdm/secure/img/genericUI/add.png"); //$NON-NLS-1$
            addNodeImg.getElement().setId("Add"); //$NON-NLS-1$
            addNodeImg.setTitle(MessagesFactory.getMessages().clone_title());
            addNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
            addNodeImg.addClickHandler(h);
            Image removeNodeImg = new Image("/talendmdm/secure/img/genericUI/delete.png"); //$NON-NLS-1$
            removeNodeImg.getElement().setId("Remove"); //$NON-NLS-1$
            removeNodeImg.setTitle(MessagesFactory.getMessages().remove_title());
            removeNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
            removeNodeImg.addClickHandler(h);

            hp.add(addNodeImg);
            hp.add(removeNodeImg);
            if (!typeModel.isSimpleType() && itemNode.getParent() != null) {
                Image cloneNodeImg = new Image("/talendmdm/secure/img/genericUI/add-group.png"); //$NON-NLS-1$
                cloneNodeImg.getElement().setId("Clone"); //$NON-NLS-1$
                cloneNodeImg.setTitle(MessagesFactory.getMessages().deepclone_title());
                cloneNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
                cloneNodeImg.addClickHandler(h);
                hp.add(cloneNodeImg);
            }
        }

        hp.setCellWidth(label, "200px"); //$NON-NLS-1$

        hp.setVisible(typeModel.isVisible());

        return hp;
    }

    public static void initItemsDetailPanelById(String ids, final String concept) {
        String[] idArr = ids.split(","); //$NON-NLS-1$
        final ItemsDetailPanel panel = new ItemsDetailPanel();
        final BrowseRecordsServiceAsync brService = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        brService.getItemBeanById(concept, idArr, Locale.getLanguage(), new SessionAwareAsyncCallback<ItemBean>() {

            public void onSuccess(final ItemBean item) {
                brService.getView("Browse_items_" + concept, Locale.getLanguage(), new SessionAwareAsyncCallback<ViewBean>() { //$NON-NLS-1$

                            public void onSuccess(ViewBean viewBean) {
                                ItemPanel itemPanel = new ItemPanel(viewBean, item, ItemDetailToolBar.VIEW_OPERATION, panel);
                                itemPanel.getToolBar().setOutMost(true);
                                Map<String, String> breads = new LinkedHashMap<String, String>();
                                if (item != null) {
                                    breads.put(BreadCrumb.DEFAULTNAME, null);
                                    breads.put(item.getConcept(), null);
                                    breads.put(item.getIds(), item.getConcept());
                                }

                                panel.setId(item.getIds());
                                panel.initBanner(item.getPkInfoList(), item.getDescription());
                                panel.addTabItem(item.getConcept(), itemPanel, ItemsDetailPanel.SINGLETON, item.getIds());
                                panel.initBreadCrumb(new BreadCrumb(breads, panel));

                                TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(item.getConcept());
                                String tabItemId = typeModel.getLabel(Locale.getLanguage()) + " " + panel.getItemId(); //$NON-NLS-1$
                                panel.setItemId(tabItemId);
                                renderTreeDetailPanel(tabItemId, panel);
                            }

                        });
            }
        });
    }

    private native static void renderTreeDetailPanel(String itemId, ItemsDetailPanel detailPanel)/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();  
        var panel = @org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetailUtil::transferTreeDetailPanel(Lorg/talend/mdm/webapp/browserecords/client/widget/ItemsDetailPanel;)(detailPanel);
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
        }
        };
        return panel;
    }-*/;
}
