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
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ItemsListPanel extends ContentPanel {

    ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    RpcProxy<PagingLoadResult<ItemBean>> proxy = new RpcProxy<PagingLoadResult<ItemBean>>() {

        public void load(Object loadConfig, AsyncCallback<PagingLoadResult<ItemBean>> callback) {
            QueryModel qm = new QueryModel();
            toolBar.setQueryModel(qm);
            qm.setPagingLoadConfig((PagingLoadConfig) loadConfig);
            service.queryItemBean(qm, callback);
        }
    };

    PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);

    final ListStore<ItemBean> store = new ListStore<ItemBean>(loader);

    private Grid<ItemBean> grid;

    private RowEditor<ItemBean> re;

    ContentPanel gridContainer;

    private final static int PAGE_SIZE = 10;

    ItemsToolBar toolBar;

    public ItemsListPanel() {
        setLayout(new FitLayout());
        setHeaderVisible(false);
        addToolBar();
        loader.addLoadListener(new LoadListener() {

            public void loaderLoad(LoadEvent le) {
                grid.getSelectionModel().select(0, false);
            }
        });
    }

    private void addToolBar() {
        toolBar = new ItemsToolBar();
        setTopComponent(toolBar);
    }

    public ItemsToolBar getToolBar() {
        return toolBar;
    }

    public void updateGrid(List<ColumnConfig> columnConfigList) {
        if (gridContainer != null)
            remove(gridContainer);

        ColumnModel cm = new ColumnModel(columnConfigList);
        gridContainer = new ContentPanel(new FitLayout());
        PagingToolBar pagingBar = new PagingToolBar(PAGE_SIZE);
        pagingBar.bind(loader);
        gridContainer.setBottomComponent(pagingBar);
        grid = new Grid<ItemBean>(store, cm);
        re = new RowEditor<ItemBean>();
        grid.getView().setForceFit(true);
        if (cm.getColumnCount() > 0) {
            grid.setAutoExpandColumn(cm.getColumn(0).getHeader());
        }
        grid.addListener(Events.OnMouseOver, new Listener<GridEvent<ItemBean>>() {

            public void handleEvent(GridEvent<ItemBean> be) {
                ItemBean item = grid.getStore().getAt(be.getRowIndex());
                grid.getView().getRow(item).getStyle().setCursor(Style.Cursor.POINTER);
            }
        });
        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ItemBean>() {

            public void selectionChanged(SelectionChangedEvent<ItemBean> se) {
                ItemBean item = se.getSelectedItem();
                showItem(item, ItemsView.TARGET_IN_SEARCH_TAB);
            }
        });
        grid.addListener(Events.OnDoubleClick, new Listener<GridEvent<ItemBean>>() {

            public void handleEvent(GridEvent<ItemBean> be) {
                ItemBean item = grid.getSelectionModel().getSelectedItem();
                showItem(item, ItemsView.TARGET_IN_NEW_TAB);
            }
        });
        grid.addListener(Events.Attach, new Listener<GridEvent<ItemBean>>() {

            public void handleEvent(GridEvent<ItemBean> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                config.setLimit(PAGE_SIZE);
                loader.load(config);
            }
        });
        grid.setLoadMask(true);
        grid.addPlugin(re);
        grid.setAriaIgnore(true);
        grid.setAriaDescribedBy("abcdefg");
        grid.setAriaLabelledBy(this.getHeader().getId() + "-label");

        gridContainer.add(grid);
        hookContextMenu();

        add(gridContainer);

        this.doLayout();

    }

    private void hookContextMenu() {

        Menu contextMenu = new Menu();

        MenuItem openInWindow = new MenuItem();
        openInWindow.setText(MessagesFactory.getMessages().openitem_window());
        // openInWindow.setIcon(Resources.ICONS.add());
        openInWindow.addSelectionListener(new SelectionListener<MenuEvent>() {

            public void componentSelected(MenuEvent ce) {
                ItemBean m = grid.getSelectionModel().getSelectedItem();
                showItem(m, ItemsView.TARGET_IN_NEW_WINDOW);
            }
        });
        contextMenu.add(openInWindow);

        MenuItem openInTab = new MenuItem();
        openInTab.setText(MessagesFactory.getMessages().openitem_tab());
        // openInWindow.setIcon(Resources.ICONS.add());
        openInTab.addSelectionListener(new SelectionListener<MenuEvent>() {

            public void componentSelected(MenuEvent ce) {
                ItemBean m = grid.getSelectionModel().getSelectedItem();
                showItem(m, ItemsView.TARGET_IN_NEW_TAB);
            }
        });
        contextMenu.add(openInTab);

        grid.setContextMenu(contextMenu);

    }

    public ListStore<ItemBean> getStore() {
        return store;
    }

    public Grid<ItemBean> getGrid() {
        return grid;
    }

    private void showItem(ItemBean item, String itemsFormTarget) {
        AppEvent evt = new AppEvent(ItemsEvents.ViewItemForm, item);
        evt.setData(ItemsView.ITEMS_FORM_TARGET, itemsFormTarget);
        Dispatcher.forwardEvent(evt);
    }
}
