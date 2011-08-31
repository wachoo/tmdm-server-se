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
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.ViewUtil;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsSearchContainer;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.creator.FieldCreator;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.SimpleTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class BrowseRecordsView extends View {

    public static final String ITEMS_SEARCH_CONTAINER = "itemsSearchContainer";//$NON-NLS-1$

    private Viewport container = new Viewport() {

        public void onAttach() {
            monitorWindowResize = true;
            Window.enableScrolling(true);
            setSize(Window.getClientWidth(), Window.getClientHeight());
            super.onAttach();
            GXT.hideLoadingPanel("loading");//$NON-NLS-1$            
        }

        protected void onWindowResize(int width, int height) {
            setSize(width, height);
            this.doLayout(true);
        }
    };

    private ItemsSearchContainer itemsSearchContainer = null;

    public BrowseRecordsView(Controller controller) {
        super(controller);
    }

    protected void handleEvent(AppEvent event) {
        if (event.getType() == BrowseRecordsEvents.InitFrame) {
            onInitFrame(event);
        } else if (event.getType() == BrowseRecordsEvents.InitSearchContainer) {
            onInitSearchContainer(event);
        } else if (event.getType() == BrowseRecordsEvents.GetView) {
            onGetView(event);
        } else if (event.getType() == BrowseRecordsEvents.SearchView) {
            onSearchView(event);
        }
    }

    private void onInitFrame(AppEvent event) {
        if (Log.isInfoEnabled())
            Log.info("Init frame... ");//$NON-NLS-1$

        container.setLayout(new FitLayout());
        container.setStyleAttribute("height", "100%");//$NON-NLS-1$ //$NON-NLS-2$       
        Dispatcher.forwardEvent(BrowseRecordsEvents.InitSearchContainer);
    }

    protected void onInitSearchContainer(AppEvent ae) {
        // create search panel
        if (Log.isInfoEnabled())
            Log.info("Init items-search-container... ");//$NON-NLS-1$
        itemsSearchContainer = new ItemsSearchContainer();
        container.add(itemsSearchContainer);
        RootPanel.get().add(container);
        Registry.register(ITEMS_SEARCH_CONTAINER, itemsSearchContainer);

    }

    protected void onGetView(final AppEvent event) {
        ViewBean viewBean = event.getData();

        itemsSearchContainer = Registry.get(ITEMS_SEARCH_CONTAINER);
        itemsSearchContainer.getToolBar().updateToolBar(viewBean);
        itemsSearchContainer.getItemsListPanel().resetShowItemTimes();
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
                Field<?> field = FieldCreator.createField((SimpleTypeModel) typeModel, null, false, Locale
.getLanguage());

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
        itemsSearchContainer.getItemsListPanel().resetShowItemTimes();
        // TODO in the view of ViewItemForm binding

    }

}
