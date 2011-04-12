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
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.util.UserSession;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
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
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ItemsListPanel extends ContentPanel {

    ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    RpcProxy<PagingLoadResult<ItemBean>> proxy = new RpcProxy<PagingLoadResult<ItemBean>>() {

        public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ItemBean>> callback) {
            QueryModel qm = new QueryModel();
            toolBar.setQueryModel(qm);
            qm.setPagingLoadConfig((PagingLoadConfig) loadConfig);
            int pageSize = (Integer) pagingBar.getPageSize();
            qm.getPagingLoadConfig().setLimit(pageSize);
            service.queryItemBeans(qm, new AsyncCallback<ItemBasePageLoadResult<ItemBean>>() {

                public void onSuccess(ItemBasePageLoadResult<ItemBean> result) {
                    callback.onSuccess(new BasePagingLoadResult<ItemBean>(result.getData(), result.getOffset(), result
                            .getTotalLength()));
                }

                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }
            });
        }
    };

    ModelKeyProvider<ItemBean> keyProvidernew = new ModelKeyProvider<ItemBean>(){
        public String getKey(ItemBean model) {
            return model.getIds();
        }
    };
    
    PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);

    final ListStore<ItemBean> store = new ListStore<ItemBean>(loader);

    private Grid<ItemBean> grid;

    private RowEditor<ItemBean> re;

    ContentPanel gridContainer;

    private ContentPanel panel;

    private final static int PAGE_SIZE = 10;

    PagingToolBarEx pagingBar = null;

    ItemsToolBar toolBar;

    public ItemsListPanel() {
        setLayout(new FitLayout());
        setHeaderVisible(false);
        addToolBar();
        initPanel();

        store.setKeyProvider(keyProvidernew);
        
        loader.setRemoteSort(true);
        loader.addLoadListener(new LoadListener() {
            public void loaderLoad(LoadEvent le) {
                if (store.getModels().size() > 0){
                    grid.getSelectionModel().select(0, false);
                } else {
                    toolBar.searchBut.setEnabled(true);
                }
            }
        });
    }

    private void initPanel() {
        panel = new ContentPanel();
        Html promptMsg = new Html("<div class=\"promptMsg\">"+MessagesFactory.getMessages().search_initMsg()+"</div>");//$NON-NLS-1$ //$NON-NLS-2$
        panel.setBodyBorder(false);
        panel.setBorders(false);
        panel.setHeaderVisible(false);
        panel.add(promptMsg);
        add(panel);
    }

    private void addToolBar() {
        toolBar = new ItemsToolBar();
        setTopComponent(toolBar);
        add(toolBar.getAdvancedPanel());
    }

    public ItemsToolBar getToolBar() {
        return toolBar;
    }

    public void updateGrid(CheckBoxSelectionModel<ItemBean> sm, List<ColumnConfig> columnConfigList) {
        toolBar.searchBut.setEnabled(false);
        if (gridContainer != null && this.findItem(gridContainer.getElement()) != null)
            remove(gridContainer);
        if (panel != null && this.findItem(panel.getElement()) != null)
            remove(panel);

        ColumnModel cm = new ColumnModel(columnConfigList);
        gridContainer = new ContentPanel(new FitLayout());
        gridContainer.setBodyBorder(false);
        gridContainer.setHeaderVisible(false);
        pagingBar = new PagingToolBarEx(PAGE_SIZE);
        pagingBar.setHideMode(HideMode.VISIBILITY);
        pagingBar.setVisible(false);
        pagingBar.bind(loader);
        gridContainer.setBottomComponent(pagingBar);
        grid = new Grid<ItemBean>(store, cm);
        grid.setSelectionModel(sm);
        grid.setStateful(true);
        re = new SaveRowEditor();
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
                final ItemBean item = se.getSelectedItem();
                if (item != null) {
                    gridContainer.setEnabled(false);
                    EntityModel entityModel = (EntityModel) Itemsbrowser2.getSession().get(UserSession.CURRENT_ENTITY_MODEL);
                    service.getItem(item, entityModel, new AsyncCallback<ItemBean>() {
                        public void onFailure(Throwable caught) {}
                        public void onSuccess(ItemBean result) {
                            item.copy(result);
                            showItem(result, ItemsView.TARGET_IN_SEARCH_TAB);
                        }
                    });

                }
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
                int pageSize = (Integer) pagingBar.getPageSize();
                config.setLimit(pageSize);
                loader.load(config);
                pagingBar.setVisible(true);
            }
        });
        grid.setLoadMask(true);
        grid.addPlugin(re);
        grid.addPlugin(sm);
        grid.setAriaIgnore(true);
        grid.setAriaDescribedBy("abcdefg");//$NON-NLS-1$
        grid.setAriaLabelledBy(this.getHeader().getId() + "-label");//$NON-NLS-1$

        gridContainer.add(grid);
        gridContainer.setHeight(this.getHeight() - toolBar.getHeight() - toolBar.getAdvancedPanel().getHeight());
        hookContextMenu();

        add(gridContainer);

        this.doLayout();
    }

    public void layoutGrid() {
        this.layout(true);
        if (gridContainer != null){
            Element parent = DOM.getParent(gridContainer.getElement());
            gridContainer.setSize(parent.getOffsetWidth(), parent.getOffsetHeight());
        }
    }
    
    private void hookContextMenu() {

        Menu contextMenu = new Menu();

        MenuItem openInWindow = new MenuItem();
        openInWindow.setText(MessagesFactory.getMessages().openitem_window());
        openInWindow.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.openWin()));
        openInWindow.addSelectionListener(new SelectionListener<MenuEvent>() {

            public void componentSelected(MenuEvent ce) {
                // TODO check dirty status
                ItemBean m = grid.getSelectionModel().getSelectedItem();
                showItem(m, ItemsView.TARGET_IN_NEW_WINDOW);
            }
        });

        MenuItem openInTab = new MenuItem();
        openInTab.setText(MessagesFactory.getMessages().openitem_tab());
        openInTab.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.openTab()));
        openInTab.addSelectionListener(new SelectionListener<MenuEvent>() {

            public void componentSelected(MenuEvent ce) {
                ItemBean m = grid.getSelectionModel().getSelectedItem();
                showItem(m, ItemsView.TARGET_IN_NEW_TAB);
            }
        });

        MenuItem editRow = new MenuItem();
        editRow.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Edit()));
        editRow.setText(MessagesFactory.getMessages().edititem());
        editRow.addSelectionListener(new SelectionListener<MenuEvent>() {

            public void componentSelected(MenuEvent ce) {
                int rowIndex = grid.getStore().indexOf(grid.getSelectionModel().getSelectedItem());
                re.startEditing(rowIndex, true);
            }
        });

        contextMenu.add(editRow);
        contextMenu.add(openInTab);
        contextMenu.add(openInWindow);

        grid.setContextMenu(contextMenu);

    }

    public ListStore<ItemBean> getStore() {
        return store;
    }

    public Grid<ItemBean> getGrid() {
        return grid;
    }
    
    public void setEnabledGridSearchButton(boolean enabled){
        gridContainer.setEnabled(enabled);
        toolBar.searchBut.setEnabled(enabled);
    }
    
    public void refresh(String ids, final boolean refreshItemForm){
        if (grid != null){
            final ListStore<ItemBean> store = grid.getStore();
            final ItemBean itemBean = store.findModel(ids);
            if (itemBean != null){
                EntityModel entityModel = (EntityModel) Itemsbrowser2.getSession().get(UserSession.CURRENT_ENTITY_MODEL);
                service.getItem(itemBean, entityModel, new AsyncCallback<ItemBean>() {
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.getMessage());
                    }
                    public void onSuccess(ItemBean result) {
                        Record record = store.getRecord(itemBean);
                        itemBean.copy(result);
                        record.commit(false);
                        
                        if (refreshItemForm){
                            ItemBean m = grid.getSelectionModel().getSelectedItem();
                            showItem(m, ItemsView.TARGET_IN_SEARCH_TAB);
                        }
                    }
                });
            } else {
                ButtonEvent be = new ButtonEvent(toolBar.searchBut);
                toolBar.searchBut.fireEvent(Events.Select, be);
            }
        } else {
            ButtonEvent be = new ButtonEvent(toolBar.searchBut);
            toolBar.searchBut.fireEvent(Events.Select, be);
        }
    }

    private void showItem(ItemBean item, String itemsFormTarget) {
        AppEvent evt = new AppEvent(ItemsEvents.ViewItemForm, item);
        evt.setData(ItemsView.ITEMS_FORM_TARGET, itemsFormTarget);
        Dispatcher.forwardEvent(evt);
    }

}
