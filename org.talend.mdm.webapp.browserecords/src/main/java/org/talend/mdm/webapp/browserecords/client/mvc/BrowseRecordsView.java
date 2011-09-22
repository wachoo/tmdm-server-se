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

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.creator.CellEditorCreator;
import org.talend.mdm.webapp.browserecords.client.creator.CellRendererCreator;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.ViewUtil;
import org.talend.mdm.webapp.browserecords.client.widget.BreadCrumb;
import org.talend.mdm.webapp.browserecords.client.widget.GenerateContainer;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsSearchContainer;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.creator.FieldCreator;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyListWindow;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyTreeDetail;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.SimpleTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
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

    public static final String ITEMS_SEARCH_CONTAINER = "itemsSearchContainer";//$NON-NLS-1$

    private ItemsSearchContainer itemsSearchContainer = null;

    public BrowseRecordsView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == BrowseRecordsEvents.InitFrame) {
            onInitFrame(event);
        } else if (event.getType() == BrowseRecordsEvents.InitSearchContainer) {
            onInitSearchContainer(event);
        } else if (event.getType() == BrowseRecordsEvents.GetView) {
            onGetView(event);
        } else if (event.getType() == BrowseRecordsEvents.SearchView) {
            onSearchView(event);
        } else if (event.getType() == BrowseRecordsEvents.CreateForeignKeyView) {
            onCreateForeignKeyView(event);
        } else if (event.getType() == BrowseRecordsEvents.SelectForeignKeyView) {
            onSelectForeignKeyView(event);
        } else if (event.getType() == BrowseRecordsEvents.ViewItem) {
            onViewItem(event);
        } else if (event.getType() == BrowseRecordsEvents.ViewForeignKey) {
            onViewForeignKey(event);
        } else if (event.getType() == BrowseRecordsEvents.UpdatePolymorphism) {
            onUpdatePolymorphism(event);
        } else if(event.getType() == BrowseRecordsEvents.ExecuteVisibleRule) {
        	onExecuteVisibleRule(event);
        }
    }

    private void onExecuteVisibleRule(AppEvent event) {
    	ItemsDetailPanel detailPanel = itemsSearchContainer.getItemsDetailPanel();
        ItemPanel itemPanel = (ItemPanel) detailPanel.getTabPanelById("itemView").getWidget(0);
        itemPanel.handleEvent(event);
	}

	private void onUpdatePolymorphism(AppEvent event) {
        itemsSearchContainer.getToolBar().getItemPanel().handleEvent(event);
    }

    private void onViewForeignKey(AppEvent event) {
        ForeignKeyModel model = event.getData();
        ForeignKeyTreeDetail tree = new ForeignKeyTreeDetail(model, false);
        itemsSearchContainer.getItemsDetailPanel().addTabItem(model.getViewBean().getBindingEntityModel().getConceptName(), tree,
                ItemsDetailPanel.MULTIPLE, model.getViewBean().getDescription());
    }

    private void onViewItem(AppEvent event) {
        ItemBean item = (ItemBean) event.getData();
        itemsSearchContainer = Registry.get(BrowseRecordsView.ITEMS_SEARCH_CONTAINER);
        ItemsDetailPanel detailPanel = itemsSearchContainer.getItemsDetailPanel();
        detailPanel.clearContent();
        detailPanel.initBanner(item.getDisplayPKInfo(), item.getDescription());
        String operation = ItemDetailToolBar.VIEW_OPERATION;
        if (item.getSmartViewMode().equals(ItemBean.PERSOMODE))
            operation = ItemDetailToolBar.PERSONALEVIEW_OPERATION;
        else if (item.getSmartViewMode().equals(ItemBean.SMARTMODE))
            operation = ItemDetailToolBar.SMARTVIEW_OPERATION;
        ItemPanel itemPanel = new ItemPanel(item, operation);
        detailPanel.addTabItem(item.getConcept(), itemPanel, ItemsDetailPanel.SINGLETON, "itemView"); //$NON-NLS-1$

        // show breadcrumb
        if (itemsSearchContainer.getRightContainer().getItemCount() > 1)
            itemsSearchContainer.getRightContainer().getItem(0).removeFromParent();
        if (item != null) {
            List<String> breads = new ArrayList<String>();
            breads.add(BreadCrumb.DEFAULTNAME);
            breads.add(item.getConcept());
            breads.add(item.getIds());
            itemsSearchContainer.getRightContainer().insert(new BreadCrumb(breads), 0);
            itemsSearchContainer.getRightContainer().layout(true);
        }
    }

    private void onSelectForeignKeyView(AppEvent event) {
        ViewBean viewBean = event.getData();
        ForeignKeyListWindow fkListWindow = (ForeignKeyListWindow) event.getSource();
        fkListWindow.show(viewBean);
    }

    private void onCreateForeignKeyView(AppEvent event) {
        ViewBean viewBean = event.getData();
        ForeignKeyTreeDetail tree = new ForeignKeyTreeDetail(viewBean, true);
        itemsSearchContainer.getItemsDetailPanel().addTabItem(viewBean.getBindingEntityModel().getConceptName(), tree,
                ItemsDetailPanel.MULTIPLE, viewBean.getDescription());

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
        itemsSearchContainer = new ItemsSearchContainer();
        GenerateContainer.getContentPanel().add(itemsSearchContainer);
        GenerateContainer.getContentPanel().layout(true);
        Registry.register(ITEMS_SEARCH_CONTAINER, itemsSearchContainer);

    }

    protected void onGetView(final AppEvent event) {
        ViewBean viewBean = event.getData();

        itemsSearchContainer = Registry.get(ITEMS_SEARCH_CONTAINER);
        itemsSearchContainer.getToolBar().updateToolBar(viewBean);
        // itemsSearchContainer.getItemsListPanel().resetShowItemTimes();
    }

    protected void onSearchView(final AppEvent event) {
        ViewBean viewBean = event.getData();
        EntityModel entityModel = viewBean.getBindingEntityModel();

        // TODO update columns
        itemsSearchContainer = Registry.get(ITEMS_SEARCH_CONTAINER);
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

        itemsSearchContainer.getItemsListPanel().updateGrid(sm, ccList);
        // itemsSearchContainer.getItemsListPanel().resetShowItemTimes();
        // TODO in the view of ViewItemForm binding

    }

}
