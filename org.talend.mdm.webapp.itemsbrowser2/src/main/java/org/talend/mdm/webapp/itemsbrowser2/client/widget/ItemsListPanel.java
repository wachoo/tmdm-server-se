/*
 * Ext GWT - Ext for GWT Copyright(c) 2007-2009, Ext JS, LLC. licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.AdvancedSearchPanel;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.SimpleCriterionPanel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ItemsListPanel extends ContentPanel {

    // add simple search criteria
    boolean isSimple;

    SimpleCriterionPanel simplePanel;

    AdvancedSearchPanel advancedPanel;

    ComboBox<BaseModel> entityCombo = new ComboBox<BaseModel>();

    ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    RpcProxy<PagingLoadResult<ItemBean>> proxy = new RpcProxy<PagingLoadResult<ItemBean>>() {

        @Override
        public void load(Object loadConfig, AsyncCallback<PagingLoadResult<ItemBean>> callback) {
            QueryModel qm = new QueryModel();
            qm.setDataClusterPK("DStar");
            qm.setViewPK(entityCombo.getValue().get("value").toString());
            if (isSimple)
                qm.setCriteria(simplePanel.getCriteria());
            else
                qm.setCriteria(advancedPanel.getCriteria());
            qm.setPagingLoadConfig((PagingLoadConfig) loadConfig);
            service.queryItemBean(qm, callback);
        }
    };

    PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);

    final ListStore<ItemBean> store = new ListStore<ItemBean>(loader);

    private Grid<ItemBean> grid;

    ContentPanel gridContainer;

    private final static int PAGE_SIZE = 10;

    final Button searchBut = new Button("Search");

    final Button advancedBut = new Button("Advanced Search");

    public ItemsListPanel() {
        setLayout(new FitLayout());
        setHeaderVisible(false);
        addToolBar();

    }

    private void addToolBar() {
        final ToolBar toolBar = new ToolBar();
        Button create = new Button("Create");
        create.setIcon(IconHelper.createStyle("icon-email-add"));
        toolBar.add(create);

        Button reply = new Button("Reply");
        reply.setIcon(IconHelper.createStyle("icon-email-reply"));
        toolBar.add(reply);

        toolBar.add(new FillToolItem());

        // add entity combo
        HorizontalPanel entityPanel = new HorizontalPanel();
        final ListStore<BaseModel> list = new ListStore<BaseModel>();

        entityCombo.setWidth(100);
        entityCombo.setEmptyText("Select an Entity...");
        entityCombo.setStore(list);
        entityCombo.setDisplayField("name");
        entityCombo.setValueField("value");
        entityCombo.setTriggerAction(TriggerAction.ALL);

        entityCombo.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

            public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
                String viewPk = se.getSelectedItem().get("value").toString();
                // TODO Auto-generated method stub
                service.getView(viewPk, new AsyncCallback<ViewBean>() {

                    public void onFailure(Throwable arg0) {
                        // TODO Auto-generated method stub
                        // MessageBox.prompt("faluire", arg0.getMessage());

                    }

                    public void onSuccess(ViewBean arg0) {
                        // TODO Auto-generated method stub
                        simplePanel.updateFields(arg0);
                        searchBut.setEnabled(true);
                        advancedBut.setEnabled(true);
                    }

                });
            }

        });
        entityPanel.add(entityCombo);
        toolBar.add(entityPanel);
        simplePanel = new SimpleCriterionPanel(null, null);
        toolBar.add(simplePanel);

        // add simple search button
        searchBut.setEnabled(false);
        searchBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                // TODO
                isSimple = true;
                String viewPk = entityCombo.getValue().get("value");
                Dispatcher.forwardEvent(ItemsEvents.GetView, viewPk);
            }

        });
        toolBar.add(searchBut);

        // add advanced search button
        advancedBut.setEnabled(false);
        advancedBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                // show advanced Search panel
                final Window winAdvanced = new Window();
                winAdvanced.setBodyBorder(false);
                winAdvanced.setClosable(false);
                winAdvanced.setModal(true);
                winAdvanced.setWidth(toolBar.getWidth());
                advancedPanel = new AdvancedSearchPanel(simplePanel.getView());

                winAdvanced.add(advancedPanel);
                Button searchBtn = new Button("Search");
                searchBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        isSimple = false;
                        String viewPk = entityCombo.getValue().get("value");
                        Dispatcher.forwardEvent(ItemsEvents.GetView, viewPk);
                        winAdvanced.close();
                    }

                });
                winAdvanced.addButton(searchBtn);

                Button cancelBtn = new Button("Cancel");
                cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        winAdvanced.close();
                    }

                });
                winAdvanced.addButton(cancelBtn);
                winAdvanced.show();
                winAdvanced.alignTo(toolBar.getElement(), "tl", new int[] { 0, 40 });
            }

        });
        toolBar.add(advancedBut);

        service.getViewsList("en", new AsyncCallback<Map<String, String>>() {

            public void onFailure(Throwable arg0) {
                // TODO Auto-generated method stub
            }

            public void onSuccess(Map<String, String> arg0) {
                BaseModel field;
                for (String key : arg0.keySet()) {
                    field = new BaseModel();
                    field.set("name", arg0.get(key));
                    field.set("value", key);
                    list.add(field);
                }
            }
        });

        setTopComponent(toolBar);
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
        grid.getView().setForceFit(true);
        grid.addListener(Events.OnMouseOver, new Listener<GridEvent<ItemBean>>() {

            public void handleEvent(GridEvent<ItemBean> be) {
                ItemBean item = grid.getStore().getAt(be.getRowIndex());
                grid.getView().getRow(item).getStyle().setCursor(Style.Cursor.POINTER);
            }
        });
        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ItemBean>() {

            public void selectionChanged(SelectionChangedEvent<ItemBean> se) {
                ItemBean m = se.getSelectedItem();
                showItem(m, ItemsView.TARGET_IN_SEARCH_TAB);

            }
        });
        grid.addListener(Events.OnDoubleClick, new Listener<GridEvent<ItemBean>>() {

            public void handleEvent(GridEvent<ItemBean> be) {
                ItemBean item = grid.getStore().getAt(be.getRowIndex());
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
        gridContainer.add(grid);
        hookContextMenu();

        add(gridContainer);

        this.doLayout();

    }

    private void hookContextMenu() {

        Menu contextMenu = new Menu();

        MenuItem openInWindow = new MenuItem();
        openInWindow.setText("Open Item in New Window");
        // openInWindow.setIcon(Resources.ICONS.add());
        openInWindow.addSelectionListener(new SelectionListener<MenuEvent>() {

            public void componentSelected(MenuEvent ce) {
                ItemBean m = grid.getSelectionModel().getSelectedItem();
                showItem(m, ItemsView.TARGET_IN_NEW_WINDOW);
            }
        });
        contextMenu.add(openInWindow);

        MenuItem openInTab = new MenuItem();
        openInTab.setText("Open Item in New Tab");
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
