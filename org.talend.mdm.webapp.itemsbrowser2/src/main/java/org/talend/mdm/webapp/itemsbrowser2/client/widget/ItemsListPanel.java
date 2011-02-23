// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.AdvancedSearchPanel;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.SimpleCriterionPanel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
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
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

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
                qm.setCriteria(simplePanel.getCriteria().toString());
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

            @Override
            public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
                String viewPk = se.getSelectedItem().get("value").toString();
                service.getView(viewPk, new AsyncCallback<ViewBean>() {

                    public void onFailure(Throwable arg0) {

                    }

                    public void onSuccess(ViewBean arg0) {
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

            @Override
            public void componentSelected(ButtonEvent ce) {
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

                ComboBox<BaseModel> cbBookmark = new ComboBox<BaseModel>();
                final ListStore<BaseModel> bookStore = new ListStore<BaseModel>();
                cbBookmark.setWidth(120);
                cbBookmark.setEmptyText("Select a Bookmark");
                cbBookmark.setStore(bookStore);
                cbBookmark.setDisplayField("name");
                cbBookmark.setValueField("value");
                cbBookmark.setTriggerAction(TriggerAction.ALL);
                service.getviewItemsCriterias(entityCombo.getValue().get("value").toString(),
                        new AsyncCallback<List<BaseModel>>() {

                            public void onFailure(Throwable arg0) {

                            }

                            public void onSuccess(List<BaseModel> arg0) {
                                bookStore.removeAll();
                                bookStore.add(arg0);
                            }

                        });

                cbBookmark.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

                    public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
                        if (se.getSelectedItem() != null) {
                            service.getCriteriaByBookmark(se.getSelectedItem().get("value").toString(),
                                    new AsyncCallback<String>() {

                                        public void onFailure(Throwable arg0) {

                                        }

                                        public void onSuccess(String arg0) {
                                            advancedPanel.setCriteria(arg0);
                                        }

                                    });
                        }
                    }

                });
                winAdvanced.getButtonBar().add(cbBookmark);

                Button bookmarkBtn = new Button("Bookmark this Search");
                bookmarkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        // TODO Auto-generated method stub
                        final Window winBookmark = new Window();
                        winBookmark.setHeading("Bookmark");
                        winBookmark.setAutoHeight(true);
                        winBookmark.setAutoWidth(true);
                        FormPanel content = new FormPanel();
                        content.setFrame(false);
                        content.setBodyBorder(false);
                        content.setHeaderVisible(false);
                        content.setButtonAlign(HorizontalAlignment.CENTER);
                        content.setLabelWidth(100);
                        content.setFieldWidth(200);
                        final CheckBox cb = new CheckBox();
                        cb.setFieldLabel("Shared");
                        content.add(cb);

                        final TextField bookmarkfield = new TextField();
                        bookmarkfield.setFieldLabel("Boommark Name");
                        Validator validator = new Validator() {

                            public String validate(Field<?> field, String value) {
                                if (field == bookmarkfield) {
                                    if (bookmarkfield.getValue() == null || bookmarkfield.getValue().toString().trim().equals(""))
                                        return "This field is required";
                                }

                                return null;
                            }
                        };
                        bookmarkfield.setValidator(validator);
                        content.add(bookmarkfield);

                        Button btn = new Button("OK");
                        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                            public void componentSelected(ButtonEvent ce) {
                                // TODO Auto-generated method stub
                                service.isExistCriteria(entityCombo.getValue().get("value").toString(), bookmarkfield.getValue()
                                        .toString(), new AsyncCallback<Boolean>() {

                                    public void onFailure(Throwable arg0) {
                                        MessageBox.alert("Status", "This Bookmark already exist,please enter other name!", null);
                                    }

                                    public void onSuccess(Boolean arg0) {
                                        if (!arg0) {
                                            service.saveCriteria(entityCombo.getValue().get("value").toString(), bookmarkfield
                                                    .getValue().toString(), cb.getValue(), advancedPanel.getCriteria(),
                                                    new AsyncCallback<String>() {

                                                        public void onFailure(Throwable arg0) {
                                                            MessageBox.alert("Save", "Save Bookmark failed!", null);
                                                        }

                                                        public void onSuccess(String arg0) {
                                                            if (arg0.equals("OK")) {
                                                                MessageBox.alert("Save", "Save Bookmark Successfully!", null);
                                                                winBookmark.close();
                                                            } else
                                                                MessageBox.alert("Save", "Save Bookmark failed!", null);
                                                        }

                                                    });
                                        } else {
                                            MessageBox.alert("Status", "This Bookmark already exist,please enter other name!",
                                                    null);
                                        }
                                    }

                                });
                            }
                        });
                        winBookmark.addButton(btn);

                        winBookmark.add(content);
                        winBookmark.show();
                    }

                });
                winAdvanced.addButton(bookmarkBtn);

                Button managebookBtn = new Button("Manage Bookmarks");
                managebookBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        final Window winBookmark = new Window();
                        winBookmark.setHeading("Manage Search Bookmarks");
                        winBookmark.setAutoHeight(true);
                        winBookmark.setAutoWidth(true);
                        FormPanel content = new FormPanel();
                        FormData formData = new FormData("-10");
                        content.setFrame(false);
                        content.setLayout(new FitLayout());
                        content.setBodyBorder(false);
                        content.setHeaderVisible(false);
                        content.setSize(300, 350);
                        winBookmark.add(content);

                        // display bookmark grid
                        RpcProxy<PagingLoadResult<BaseModel>> proxyBookmark = new RpcProxy<PagingLoadResult<BaseModel>>() {

                            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<BaseModel>> callback) {
                                service.querySearchTemplates(entityCombo.getValue().get("value").toString(), false,
                                        (PagingLoadConfig) loadConfig, callback);
                            }
                        };

                        // loader
                        final PagingLoader<PagingLoadResult<BaseModel>> loaderBookmark = new BasePagingLoader<PagingLoadResult<BaseModel>>(
                                proxyBookmark);
                        loaderBookmark.setRemoteSort(true);

                        ListStore<BaseModel> store = new ListStore<BaseModel>(loaderBookmark);
                        store.setDefaultSort("name", SortDir.ASC);

                        final PagingToolBar toolBar = new PagingToolBar(PAGE_SIZE);
                        toolBar.bind(loaderBookmark);

                        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
                        columns.add(new ColumnConfig("name", "Bookmarks", 200));
                        ColumnConfig colImg = new ColumnConfig("value", "Delete", 100);
                        colImg.setRenderer(new GridCellRenderer() {

                            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                                    ListStore store, Grid grid) {
                                // TODO Auto-generated method stub
                                Image image = new Image();
                                image.setResource(Icons.INSTANCE.remove());
                                image.addClickListener(new ClickListener() {

                                    public void onClick(Widget arg0) {
                                        MessageBox.confirm("Confirm", "Do you really want to remove this Bookmark?",
                                                new Listener<MessageBoxEvent>() {

                                                    public void handleEvent(MessageBoxEvent be) {
                                                        // TODO Auto-generated method stub
                                                        if (be.getButtonClicked().getText().equals("Yes")) {
                                                            // delete the bookmark
                                                            // service.deleteSearchTemplate(, callback)
                                                        }
                                                    }
                                                });
                                    }

                                });
                                return image;
                            }

                        });

                        columns.add(colImg);

                        ColumnModel cm = new ColumnModel(columns);

                        final Grid<BaseModel> bookmarkgrid = new Grid<BaseModel>(store, cm);
                        bookmarkgrid.getView().setForceFit(true);
                        bookmarkgrid.addListener(Events.Attach, new Listener<GridEvent<BaseModel>>() {

                            public void handleEvent(GridEvent<BaseModel> be) {
                                PagingLoadConfig config = new BasePagingLoadConfig();
                                config.setOffset(0);
                                config.setLimit(PAGE_SIZE);
                                loaderBookmark.load(config);
                            }
                        });

                        bookmarkgrid.setLoadMask(true);
                        bookmarkgrid.setBorders(false);

                        content.setBottomComponent(toolBar);
                        content.add(bookmarkgrid, formData);

                        winBookmark.show();
                    }

                });
                winAdvanced.addButton(managebookBtn);

                // Button cleanBtn = new Button("Clean");
                // cleanBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
                //
                // public void componentSelected(ButtonEvent ce) {
                // advancedPanel.cleanCriteria();
                // }
                //
                // });
                // winAdvanced.addButton(cleanBtn);

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

        service.getViewsList("en", new AsyncCallback<List<BaseModel>>() {

            public void onFailure(Throwable arg0) {
                // TODO Auto-generated method stub
            }

            public void onSuccess(List<BaseModel> arg0) {
                list.removeAll();
                list.add(arg0);
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
