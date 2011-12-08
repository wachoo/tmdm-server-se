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
package org.talend.mdm.webapp.browserecords.client.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.creator.CellEditorCreator;
import org.talend.mdm.webapp.browserecords.client.creator.CellRendererCreator;
import org.talend.mdm.webapp.browserecords.client.model.BreadCrumbModel;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.util.ViewUtil;
import org.talend.mdm.webapp.browserecords.client.widget.BreadCrumb;
import org.talend.mdm.webapp.browserecords.client.widget.GenerateContainer;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsMainTabPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsSearchContainer;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.creator.FieldCreator;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyListWindow;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class BrowseRecordsView extends View {

    public static final String ITEMS_FORM_TARGET = "items_form_target";//$NON-NLS-1$

    public static final String TARGET_IN_NEW_TAB = "target_in_new_tab";//$NON-NLS-1$

    public static final String DEFAULT_ITEMVIEW = "itemView"; //$NON-NLS-1$

    public static final String ITEMS_DETAIL_PANEL = "itemsDetailPanel"; //$NON-NLS-1$

    public BrowseRecordsView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        int eventTypeCode = event.getType().getEventCode();
        switch (eventTypeCode) {
        case BrowseRecordsEvents.InitFrameCode:
            onInitFrame(event);
            break;
        case BrowseRecordsEvents.InitSearchContainerCode:
            onInitSearchContainer(event);
            break;
        case BrowseRecordsEvents.GetViewCode:
            onGetView(event);
            break;
        case BrowseRecordsEvents.SearchViewCode:
            onSearchView(event);
            break;
        case BrowseRecordsEvents.CreateForeignKeyViewCode:
            onCreateForeignKeyView(event);
            break;
        case BrowseRecordsEvents.SelectForeignKeyViewCode:
            onSelectForeignKeyView(event);
            break;
        case BrowseRecordsEvents.ViewItemCode:
            onViewItem(event);
            break;
        case BrowseRecordsEvents.ViewForeignKeyCode:
            onViewForeignKey(event);
            break;
        case BrowseRecordsEvents.UpdatePolymorphismCode:
            onUpdatePolymorphism(event);
            break;
        case BrowseRecordsEvents.ExecuteVisibleRuleCode:
            onExecuteVisibleRule(event);
            break;
        default:
            break;
        }
    }

    private void onExecuteVisibleRule(AppEvent event) {
        ItemsDetailPanel detailPanel = event.getData(BrowseRecordsView.ITEMS_DETAIL_PANEL);
        ItemPanel itemPanel = detailPanel.getCurrentItemPanel();
        if (itemPanel != null) {
            itemPanel.onExecuteVisibleRule((List<VisibleRuleResult>) event.getData());
        }
    }

    private void onUpdatePolymorphism(AppEvent event) {
        ItemsDetailPanel detailPanel = event.getData(BrowseRecordsView.ITEMS_DETAIL_PANEL);
        ItemPanel itemPanel = detailPanel.getCurrentItemPanel();
        if (itemPanel != null) {
            itemPanel.onUpdatePolymorphism((ComplexTypeModel) event.getData());
        }
    }

    private void onViewForeignKey(AppEvent event) {
        ForeignKeyModel model = event.getData();
        ItemsDetailPanel detailPanel = event.getData(BrowseRecordsView.ITEMS_DETAIL_PANEL);
        ItemBean fkItemBean = model.getItemBean();
        ViewBean fkViewBean = model.getViewBean();
        detailPanel.clearContent();
        detailPanel.clearBanner();
        String pkInfo = fkItemBean.getDisplayPKInfo().equals(fkItemBean.getLabel()) ? null : fkItemBean.getDisplayPKInfo();
        detailPanel.appendBreadCrumb(fkItemBean.getConcept(), fkItemBean.getLabel(), fkItemBean.getIds(), pkInfo);
        ItemPanel itemPanel = new ItemPanel(fkViewBean, fkItemBean, ItemDetailToolBar.VIEW_OPERATION, detailPanel);
        // detailPanel.setId(fkItemBean.getIds());
        detailPanel.initBanner(fkItemBean.getPkInfoList(), fkItemBean.getDescription());
        detailPanel.addTabItem(fkItemBean.getLabel(), itemPanel, ItemsDetailPanel.SINGLETON, fkItemBean.getIds());
    }

    private void onViewItem(AppEvent event) {

        ItemBean item = (ItemBean) event.getData();
        String itemConcept = null;
        String itemLabel = null;
        String itemDisplayPKInfo = null;
        String itemIds = null;
        List<String> itemPkInfoList = null;
        String itemDescription = null;

        String operation = ItemDetailToolBar.VIEW_OPERATION;
        String smartViewMode = item.getSmartViewMode();
        if (smartViewMode.equals(ItemBean.PERSOMODE))
            operation = ItemDetailToolBar.PERSONALEVIEW_OPERATION;
        else if (smartViewMode.equals(ItemBean.SMARTMODE))
            operation = ItemDetailToolBar.SMARTVIEW_OPERATION;

        ViewBean viewBean = (ViewBean) BrowseRecords.getSession().get(UserSession.CURRENT_VIEW);
        List<BreadCrumbModel> breads = new ArrayList<BreadCrumbModel>();

        // show breadcrumb
        // ItemsDetailPanel.getInstance().clearBreadCrumb();

        if (item != null) {
            itemConcept = item.getConcept();
            itemLabel = item.getLabel();
            itemDisplayPKInfo = item.getDisplayPKInfo();
            itemIds = item.getIds();
            itemPkInfoList = item.getPkInfoList();
            itemDescription = item.getDescription();

            breads.add(new BreadCrumbModel("", BreadCrumb.DEFAULTNAME, null, null, false)); //$NON-NLS-1$           
            breads.add(new BreadCrumbModel(itemConcept, itemLabel, itemIds, itemDisplayPKInfo.equals(itemLabel) ? null
                    : itemDisplayPKInfo, true));
        }

        ItemsMainTabPanel itemsMainTabPanel = ItemsMainTabPanel.getInstance();

        if (TARGET_IN_NEW_TAB.equals(event.getData(BrowseRecordsView.ITEMS_FORM_TARGET))) {
            // Open in a new tab in ItemsMainTabPanel

            ItemsDetailPanel itemsDetailPanel = this.buildNewItemsDetailPanel(viewBean, item, operation, itemIds, itemPkInfoList,
                    itemDescription, itemLabel, breads);

            itemsMainTabPanel.addMainTabItem(itemLabel + " " + itemIds, //$NON-NLS-1$ 
                    itemsDetailPanel, itemIds);

        } else {
            // Open in default tab in ItemsMainTabPanel, which may or may not already exist

            TabItem defaultTabItem = itemsMainTabPanel.getItemByItemId(DEFAULT_ITEMVIEW);

            if (defaultTabItem != null) {
                // Default tab exists in ItemsMainTabPanel
                // Reuse the default tab

                defaultTabItem.setText(itemConcept + " " + itemIds); //$NON-NLS-1$

                ItemsDetailPanel itemsDetailPanel = (ItemsDetailPanel) defaultTabItem.getItemByItemId(DEFAULT_ITEMVIEW);

                if (itemsDetailPanel == null) {
                    // ItemsDetailPanel does not exist in default tab of ItemsMainTabPanel
                    // Create new ItemDetailPanel within the default tab

                    defaultTabItem.removeAll();
                    defaultTabItem.add(this.buildNewItemsDetailPanel(viewBean, item, operation, DEFAULT_ITEMVIEW, itemPkInfoList,
                            itemDescription, itemLabel != null ? itemLabel : itemConcept, breads));
                } else {
                    // ItemsDetailPanel exists in default tab of ItemsMainTabPanel
                    // Reuse the ItemsDetailPanel

                    itemsDetailPanel.clearAll();
                    itemsDetailPanel.initBanner(itemPkInfoList, itemDescription);

                    ItemPanel itemPanel = new ItemPanel(viewBean, item, operation, itemsDetailPanel);
                    itemsDetailPanel.addTabItem(itemLabel != null ? itemLabel : itemConcept, itemPanel,
                            ItemsDetailPanel.SINGLETON, DEFAULT_ITEMVIEW);

                    itemsDetailPanel.initBreadCrumb(new BreadCrumb(breads, itemsDetailPanel));
                }

                itemsMainTabPanel.setSelection(defaultTabItem);
            } else {
                // Default tab does not exist in ItemsMainTabPanel, create it

                TabItem tabItem = this.buildNewItemsMainTabPanelTabItem(itemConcept, itemIds, this.buildNewItemsDetailPanel(
                        viewBean, item, operation, DEFAULT_ITEMVIEW, itemPkInfoList, itemDescription,
                        itemLabel != null ? itemLabel : itemConcept, breads));
                itemsMainTabPanel.insert(tabItem, 0);
                itemsMainTabPanel.setSelection(tabItem);
            }
        }
    }

    private TabItem buildNewItemsMainTabPanelTabItem(String itemConcept, String itemIds, ItemsDetailPanel itemsDetailPanel) {
        TabItem tabItem = new TabItem(itemConcept + " " + itemIds); //$NON-NLS-1$
        tabItem.setId(DEFAULT_ITEMVIEW);
        tabItem.setClosable(true);
        tabItem.setLayout(new FitLayout());
        tabItem.add(itemsDetailPanel);

        return tabItem;
    }

    private ItemsDetailPanel buildNewItemsDetailPanel(ViewBean viewBean, ItemBean item, String operation, String itemIds,
            List<String> itemPkInfoList, String itemDescription, String itemLabel, List<BreadCrumbModel> breads) {
        ItemsDetailPanel itemsDetailPanel = new ItemsDetailPanel();
        ItemPanel itemPanel = new ItemPanel(viewBean, item, operation, itemsDetailPanel);

        itemsDetailPanel.setId(itemIds);

        itemsDetailPanel.initBanner(itemPkInfoList, itemDescription);

        itemsDetailPanel.addTabItem(itemLabel, itemPanel, ItemsDetailPanel.SINGLETON, itemIds);

        itemsDetailPanel.initBreadCrumb(new BreadCrumb(breads, itemsDetailPanel));

        return itemsDetailPanel;
    }

    private void onSelectForeignKeyView(AppEvent event) {
        EntityModel entityModel = event.getData();
        ForeignKeyListWindow fkListWindow = (ForeignKeyListWindow) event.getSource();
        fkListWindow.show(entityModel);
    }

    private void onCreateForeignKeyView(AppEvent event) {
        ViewBean viewBean = event.getData();
        ItemsDetailPanel detailPanel = event.getData(BrowseRecordsView.ITEMS_DETAIL_PANEL);
        // ForeignKeyTreeDetail tree = new ForeignKeyTreeDetail(viewBean, true, detailPanel);
        detailPanel.clearContent();
        ItemPanel itemPanel = new ItemPanel(viewBean, new ItemBean(viewBean.getBindingEntityModel().getConceptName(), "", ""), //$NON-NLS-1$//$NON-NLS-2$
                ItemDetailToolBar.CREATE_OPERATION, detailPanel);
        detailPanel.addTabItem(viewBean.getBindingEntityModel().getConceptLabel(), itemPanel, ItemsDetailPanel.MULTIPLE,
                viewBean.getDescription());

    }

    private void onInitFrame(AppEvent event) {
        if (Log.isInfoEnabled())
            Log.info("Init frame... ");//$NON-NLS-1$
        GenerateContainer.getContentPanel().setLayout(new FitLayout());
        GenerateContainer.getContentPanel().setStyleAttribute("height", "100%");//$NON-NLS-1$ //$NON-NLS-2$       
        Dispatcher.forwardEvent(BrowseRecordsEvents.InitSearchContainer);
    }

    protected void onInitSearchContainer(AppEvent ae) {
        // create search panel
        if (Log.isInfoEnabled())
            Log.info("Init items-search-container... ");//$NON-NLS-1$
        GenerateContainer.getContentPanel().add(ItemsSearchContainer.getInstance());
        GenerateContainer.getContentPanel().layout(true);
    }

    protected void onGetView(final AppEvent event) {
        ViewBean viewBean = event.getData();
        ItemsToolBar.getInstance().updateToolBar(viewBean);
        // itemsSearchContainer.getItemsListPanel().resetShowItemTimes();
    }

    protected void onSearchView(final AppEvent event) {
        ViewBean viewBean = event.getData();
        EntityModel entityModel = viewBean.getBindingEntityModel();

        // TODO update columns
        List<ColumnConfig> ccList = new ArrayList<ColumnConfig>();
        CheckBoxSelectionModel<ItemBean> sm = new CheckBoxSelectionModel<ItemBean>();
        sm.setSelectionMode(SelectionMode.MULTI);
        ccList.add(sm.getColumn());
        List<String> viewableXpaths = viewBean.getViewableXpaths();
        Map<String, TypeModel> dataTypes = entityModel.getMetaDataTypes();
        List<String> keys = Arrays.asList(entityModel.getKeys());
        for (String xpath : viewableXpaths) {
            TypeModel typeModel = dataTypes.get(xpath);

            ColumnConfig cc = new ColumnConfig(xpath, typeModel == null ? xpath : ViewUtil.getViewableLabel(Locale.getLanguage(),
                    typeModel), 200);
            if (typeModel instanceof SimpleTypeModel && !keys.contains(xpath)) {
                Field<?> field = FieldCreator.createField((SimpleTypeModel) typeModel, null, false, Locale.getLanguage());

                CellEditor cellEditor = CellEditorCreator.createCellEditor(field);
                if (cellEditor != null) {
                    cc.setEditor(cellEditor);
                }
            }

            if (typeModel != null) {
                GridCellRenderer<ModelData> renderer = CellRendererCreator.createRenderer(typeModel, xpath);
                if (renderer != null) {
                    cc.setRenderer(renderer);
                }
            }

            ccList.add(cc);
        }

        ItemsListPanel.getInstance().updateGrid(sm, ccList);
        // itemsSearchContainer.getItemsListPanel().resetShowItemTimes();
        // TODO in the view of ViewItemForm binding

    }

}
