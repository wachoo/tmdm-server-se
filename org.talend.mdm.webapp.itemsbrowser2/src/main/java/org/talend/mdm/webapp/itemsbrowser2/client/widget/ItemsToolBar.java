package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.creator.ItemCreator;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.client.util.Locale;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.AdvancedSearchPanel;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.SimpleCriterionPanel;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
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
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
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
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ItemsToolBar extends ToolBar {

    private final static int PAGE_SIZE = 10;

    boolean isSimple;

    static String userCluster = null;

    SimpleCriterionPanel simplePanel;

    AdvancedSearchPanel advancedPanel;

    ComboBox<ItemBaseModel> entityCombo = new ComboBox<ItemBaseModel>();

    final Button searchBut = new Button(MessagesFactory.getMessages().search_btn());

    final Button advancedBut = new Button(MessagesFactory.getMessages().advsearch_btn());

    final Button managebookBtn = new Button();

    final Button bookmarkBtn = new Button();

    final Window winAdvanced = new Window();

    Button createBtn = new Button(MessagesFactory.getMessages().create_btn());

    Button menu = new Button(MessagesFactory.getMessages().delete_btn());

    ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    ItemsToolBar instance = this;

    public ItemsToolBar() {
        // init user saved model
        userCluster = Itemsbrowser2.getSession().getAppHeader().getDatacluster();
        initToolBar();
    }

    public void setQueryModel(QueryModel qm) {
        qm.setDataClusterPK(userCluster);
        qm.setView(Itemsbrowser2.getSession().getCurrentView());
        qm.setModel(Itemsbrowser2.getSession().getCurrentEntityModel());
        if (isSimple)
            qm.setCriteria(simplePanel.getCriteria().toString());
        else
            qm.setCriteria(advancedPanel.getCriteria());
    }

    public void updateToolBar(ViewBean viewBean) {
        simplePanel.updateFields(viewBean);
        if (advancedPanel != null)
            advancedPanel.setView(viewBean);
        searchBut.setEnabled(true);
        advancedBut.setEnabled(true);
        managebookBtn.setEnabled(true);
        bookmarkBtn.setEnabled(true);
        createBtn.setEnabled(true);
        menu.setEnabled(true);
    }

    private void initToolBar() {
        createBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Create()));
        createBtn.setEnabled(false);
        add(createBtn);
        createBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                String concept = CommonUtil.getConceptFromBrowseItemView(entityCombo.getValue().get("value").toString());//$NON-NLS-1$

                EntityModel entityModel = (EntityModel) Itemsbrowser2.getSession().getCurrentEntityModel();
                ItemBean item = ItemCreator.createDefaultItemBean(concept, entityModel);

                AppEvent evt = new AppEvent(ItemsEvents.ViewItemForm, item);
                evt.setData(ItemsView.ITEMS_FORM_TARGET, ItemsView.TARGET_IN_NEW_TAB);
                Dispatcher.forwardEvent(evt);
            }

        });

        menu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
        Menu sub = new Menu();
        MenuItem delMenu = new MenuItem(MessagesFactory.getMessages().delete_btn());
        delMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));

        // TODO duplicate with recordToolbar
        delMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

            public void componentSelected(MenuEvent ce) {
                MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages().delete_confirm(),
                        new Listener<MessageBoxEvent>() {

                            public void handleEvent(MessageBoxEvent be) {
                                if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                    final ItemsListPanel list = (ItemsListPanel) instance.getParent();
                                    if (list.getGrid() != null) {
                                        service.deleteItemBean(list.getGrid().getSelectionModel().getSelectedItem(),
                                                new AsyncCallback<ItemResult>() {

                                                    public void onFailure(Throwable arg0) {

                                                    }

                                                    public void onSuccess(ItemResult arg0) {
                                                        if (arg0.getStatus() == ItemResult.SUCCESS) {
                                                            list.getStore().getLoader().load();
                                                            MessageBox.alert(MessagesFactory.getMessages().info_title(), arg0
                                                                    .getDescription(), null);
                                                        } else if (arg0.getStatus() == ItemResult.FAILURE) {
                                                            MessageBox.alert(MessagesFactory.getMessages().error_title(), arg0
                                                                    .getDescription(), null);
                                                        }
                                                    }

                                                });
                                    }

                                }
                            }
                        });

            }
        });
        sub.add(delMenu);
        MenuItem trashMenu = new MenuItem(MessagesFactory.getMessages().trash_btn());
        trashMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
        trashMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

            public void componentSelected(MenuEvent ce) {
                final MessageBox box = MessageBox.prompt(MessagesFactory.getMessages().path(), MessagesFactory.getMessages()
                        .path_desc(), new Listener<MessageBoxEvent>() {

                    public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
                            final ItemsListPanel list = (ItemsListPanel) instance.getParent();
                            if (list.getGrid() != null) {
                                service.logicalDeleteItem(list.getGrid().getSelectionModel().getSelectedItem(), "/", //$NON-NLS-1$
                                        new AsyncCallback<ItemResult>() {

                                            public void onFailure(Throwable arg0) {

                                            }

                                            public void onSuccess(ItemResult arg0) {
                                                if (arg0.getStatus() == ItemResult.SUCCESS) {
                                                    list.getStore().getLoader().load();
                                                    MessageBox.alert(MessagesFactory.getMessages().info_title(), arg0
                                                            .getDescription(), null);
                                                } else if (arg0.getStatus() == ItemResult.FAILURE) {
                                                    MessageBox.alert(MessagesFactory.getMessages().error_title(), arg0
                                                            .getDescription(), null);
                                                }
                                            }

                                        });

                            }
                        }
                    }
                });
                box.getTextBox().setValue("/"); //$NON-NLS-1$
            }
        });
        sub.add(trashMenu);
        menu.setMenu(sub);
        menu.setEnabled(false);
        add(menu);

        add(new FillToolItem());

        // add entity combo
        HorizontalPanel entityPanel = new HorizontalPanel();
        final ListStore<ItemBaseModel> list = new ListStore<ItemBaseModel>();

        entityCombo.setAutoWidth(true);
        entityCombo.setEmptyText(MessagesFactory.getMessages().empty_entity());
        entityCombo.setStore(list);
        entityCombo.setDisplayField("name");//$NON-NLS-1$
        entityCombo.setValueField("value");//$NON-NLS-1$
        entityCombo.setTriggerAction(TriggerAction.ALL);

        entityCombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

            public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                String viewPk = se.getSelectedItem().get("value").toString();//$NON-NLS-1$
                Dispatcher.forwardEvent(ItemsEvents.GetView, viewPk);
            }

        });
        entityPanel.add(entityCombo);
        add(entityPanel);
        simplePanel = new SimpleCriterionPanel(null, null);
        add(simplePanel);

        // add simple search button
        searchBut.setEnabled(false);
        searchBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                isSimple = true;
                String viewPk = entityCombo.getValue().get("value");//$NON-NLS-1$
                Dispatcher.forwardEvent(ItemsEvents.SearchView, viewPk);
            }

        });
        add(searchBut);

        add(new SeparatorToolItem());

        // add advanced search button
        advancedBut.setEnabled(false);
        advancedBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                // show advanced Search panel
                showAdvancedWin(instance, null);
            }

        });
        add(advancedBut);

        add(new SeparatorToolItem());

        // add bookmark management button
        managebookBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Display()));
        managebookBtn.setEnabled(false);
        managebookBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                final Window winBookmark = new Window();
                winBookmark.setHeading(MessagesFactory.getMessages().bookmarkmanagement_heading());
                winBookmark.setAutoHeight(true);
                winBookmark.setAutoWidth(true);
                winBookmark.setModal(true);
                FormPanel content = new FormPanel();
                FormData formData = new FormData("-10");//$NON-NLS-1$
                content.setFrame(false);
                content.setLayout(new FitLayout());
                content.setBodyBorder(false);
                content.setHeaderVisible(false);
                content.setSize(400, 350);
                winBookmark.add(content);

                // display bookmark grid
                RpcProxy<PagingLoadResult<ItemBaseModel>> proxyBookmark = new RpcProxy<PagingLoadResult<ItemBaseModel>>() {

                    public void load(Object loadConfig, AsyncCallback<PagingLoadResult<ItemBaseModel>> callback) {
                        service.querySearchTemplates(entityCombo.getValue().get("value").toString(), false, //$NON-NLS-1$
                                (PagingLoadConfig) loadConfig, callback);
                    }
                };

                // loader
                final PagingLoader<PagingLoadResult<ItemBaseModel>> loaderBookmark = new BasePagingLoader<PagingLoadResult<ItemBaseModel>>(
                        proxyBookmark);
                loaderBookmark.setRemoteSort(true);

                ListStore<ItemBaseModel> store = new ListStore<ItemBaseModel>(loaderBookmark);
                store.setDefaultSort("name", SortDir.ASC); //$NON-NLS-1$

                final PagingToolBar pagetoolBar = new PagingToolBar(PAGE_SIZE);
                pagetoolBar.bind(loaderBookmark);

                List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
                columns.add(new ColumnConfig("name", MessagesFactory.getMessages().bookmark_heading(), 200)); //$NON-NLS-1$
                ColumnConfig colEdit = new ColumnConfig("value", MessagesFactory.getMessages().bookmark_edit(), 100); //$NON-NLS-1$
                colEdit.setRenderer(new GridCellRenderer<ItemBaseModel>() {

                    @SuppressWarnings("deprecation")
                    public Object render(final ItemBaseModel model, String property, ColumnData config, int rowIndex,
                            int colIndex, ListStore<ItemBaseModel> store, Grid<ItemBaseModel> grid) {
                        Image image = new Image();
                        image.setResource(Icons.INSTANCE.Edit());
                        image.addMouseOverHandler(new MouseOverHandler() {

                            public void onMouseOver(MouseOverEvent arg0) {

                            }
                        });
                        image.addClickListener(new ClickListener() {

                            public void onClick(Widget arg0) {
                                // edit the bookmark
                                if (advancedPanel == null) {
                                    advancedPanel = new AdvancedSearchPanel(simplePanel.getView());
                                }
                                service.getCriteriaByBookmark(model.get("value").toString(), new AsyncCallback<String>() { //$NON-NLS-1$

                                            public void onFailure(Throwable arg0) {

                                            }

                                            public void onSuccess(String arg0) {
                                                showAdvancedWin(instance, arg0);
                                                winBookmark.close();
                                            }

                                        });
                            }

                        });
                        return image;
                    }

                });
                columns.add(colEdit);

                ColumnConfig colDel = new ColumnConfig("value", MessagesFactory.getMessages().bookmark_del(), 100); //$NON-NLS-1$
                colDel.setRenderer(new GridCellRenderer<ItemBaseModel>() {

                    @SuppressWarnings("deprecation")
                    public Object render(final ItemBaseModel model, String property, ColumnData config, int rowIndex,
                            int colIndex, ListStore<ItemBaseModel> store, Grid<ItemBaseModel> grid) {
                        Image image = new Image();
                        image.setResource(Icons.INSTANCE.remove());
                        image.addClickListener(new ClickListener() {

                            public void onClick(Widget arg0) {
                                MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                                        .bookmark_DelMsg(), new Listener<MessageBoxEvent>() {

                                    public void handleEvent(MessageBoxEvent be) {
                                        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                            // delete the bookmark
                                            service.deleteSearchTemplate(model.get("value").toString(), //$NON-NLS-1$
                                                    new AsyncCallback<String>() {

                                                        public void onFailure(Throwable arg0) {
                                                        }

                                                        public void onSuccess(String arg0) {
                                                            loaderBookmark.load();
                                                        }

                                                    });
                                        }
                                    }
                                });
                            }

                        });
                        return image;
                    }

                });

                columns.add(colDel);

                ColumnModel cm = new ColumnModel(columns);

                final Grid<ItemBaseModel> bookmarkgrid = new Grid<ItemBaseModel>(store, cm);
                if (cm.getColumnCount() > 0) {
                    bookmarkgrid.setAutoExpandColumn(cm.getColumn(0).getHeader());
                }
                bookmarkgrid.getView().setForceFit(true);
                bookmarkgrid.addListener(Events.Attach, new Listener<GridEvent<ItemBaseModel>>() {

                    public void handleEvent(GridEvent<ItemBaseModel> be) {
                        PagingLoadConfig config = new BasePagingLoadConfig();
                        config.setOffset(0);
                        config.setLimit(PAGE_SIZE);
                        loaderBookmark.load(config);
                    }
                });

                bookmarkgrid.addListener(Events.OnDoubleClick, new Listener<GridEvent<ItemBaseModel>>() {

                    public void handleEvent(final GridEvent<ItemBaseModel> be) {
                        service.getviewItemsCriterias(entityCombo.getValue().get("value").toString(), //$NON-NLS-1$
                                new AsyncCallback<List<ItemBaseModel>>() {

                                    public void onFailure(Throwable arg0) {
                                    }

                                    public void onSuccess(List<ItemBaseModel> arg0) {
                                        // only the shared bookmark could be search
                                        Iterator i = arg0.iterator();
                                        while (i.hasNext()) {
                                            if (((ItemBaseModel) i.next()).get("value").equals( //$NON-NLS-1$
                                                    be.getModel().get("value").toString())) { //$NON-NLS-1$
                                                service.getCriteriaByBookmark(be.getModel().get("value").toString(), //$NON-NLS-1$
                                                        new AsyncCallback<String>() {

                                                            public void onFailure(Throwable arg0) {

                                                            }

                                                            public void onSuccess(String arg0) {
                                                                isSimple = false;
                                                                if (advancedPanel == null) {
                                                                    advancedPanel = new AdvancedSearchPanel(simplePanel.getView());
                                                                }
                                                                advancedPanel.setCriteria(arg0);
                                                                String viewPk = entityCombo.getValue().get("value"); //$NON-NLS-1$
                                                                Dispatcher.forwardEvent(ItemsEvents.SearchView, viewPk);
                                                                winBookmark.close();
                                                            }

                                                        });
                                            }
                                        }
                                    }

                                });
                    }
                });

                bookmarkgrid.setLoadMask(true);
                bookmarkgrid.setBorders(false);

                content.setBottomComponent(pagetoolBar);
                content.add(bookmarkgrid, formData);

                winBookmark.show();
            }

        });
        add(managebookBtn);

        // add bookmark save button
        bookmarkBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        bookmarkBtn.setEnabled(false);
        bookmarkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                final Window winBookmark = new Window();
                winBookmark.setHeading(MessagesFactory.getMessages().bookmark_heading());
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
                cb.setFieldLabel(MessagesFactory.getMessages().bookmark_shared());
                content.add(cb);

                final TextField bookmarkfield = new TextField();
                bookmarkfield.setFieldLabel(MessagesFactory.getMessages().bookmark_name());
                Validator validator = new Validator() {

                    public String validate(Field<?> field, String value) {
                        if (field == bookmarkfield) {
                            if (bookmarkfield.getValue() == null || bookmarkfield.getValue().toString().trim().equals("")) //$NON-NLS-1$
                                return MessagesFactory.getMessages().required_field();
                        }

                        return null;
                    }
                };
                bookmarkfield.setValidator(validator);
                content.add(bookmarkfield);

                Button btn = new Button(MessagesFactory.getMessages().ok_btn());
                btn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        service.isExistCriteria(entityCombo.getValue().get("value").toString(), bookmarkfield.getValue() //$NON-NLS-1$
                                .toString(), new AsyncCallback<Boolean>() {

                            public void onFailure(Throwable arg0) {
                                MessageBox.alert(MessagesFactory.getMessages().warning_title(), MessagesFactory.getMessages()
                                        .bookmark_existMsg(), null);
                            }

                            public void onSuccess(Boolean arg0) {
                                if (!arg0) {
                                    service.saveCriteria(entityCombo.getValue().get("value").toString(), bookmarkfield.getValue() //$NON-NLS-1$
                                            .toString(), cb.getValue(), advancedPanel.getCriteria(), new AsyncCallback<String>() {

                                        public void onFailure(Throwable arg0) {
                                            MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory
                                                    .getMessages().bookmark_saveFailed(), null);
                                        }

                                        public void onSuccess(String arg0) {
                                            if (arg0.equals("OK")) { //$NON-NLS-1$
                                                MessageBox.alert(MessagesFactory.getMessages().info_title(), MessagesFactory
                                                        .getMessages().bookmark_saveSuccess(), null);
                                                // cbloader.load();
                                                winBookmark.close();
                                            } else
                                                MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory
                                                        .getMessages().bookmark_saveFailed(), null);
                                        }

                                    });
                                } else {
                                    MessageBox.alert(MessagesFactory.getMessages().warning_title(), MessagesFactory.getMessages()
                                            .bookmark_existMsg(), null);
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
        add(bookmarkBtn);

        service.getViewsList(Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader()),
                new AsyncCallback<List<ItemBaseModel>>() {

                    public void onFailure(Throwable arg0) {
                    }

                    public void onSuccess(List<ItemBaseModel> arg0) {
                        list.removeAll();
                        list.add(arg0);
                    }
                });
    }

    private void showAdvancedWin(ToolBar toolBar, String criteria) {
        if (winAdvanced.getItemByItemId("advancedPanel") == null) { //$NON-NLS-1$
            // avoid show this window multi-times
            winAdvanced.setId("advancedWin"); //$NON-NLS-1$
            winAdvanced.setHeaderVisible(false);
            winAdvanced.setClosable(false);
            winAdvanced.setFrame(false);
            winAdvanced.setWidth(toolBar.getWidth());
            winAdvanced.setAutoHeight(true);
            advancedPanel = new AdvancedSearchPanel(simplePanel.getView());
            advancedPanel.setItemId("advancedPanel"); //$NON-NLS-1$
            advancedPanel.setButtonAlign(HorizontalAlignment.CENTER);

            winAdvanced.add(advancedPanel);
            Button searchBtn = new Button(MessagesFactory.getMessages().search_btn());
            searchBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                public void componentSelected(ButtonEvent ce) {
                    if (advancedPanel.getCriteria() == null || advancedPanel.getCriteria().equals("")) //$NON-NLS-1$
                        MessageBox.alert(MessagesFactory.getMessages().warning_title(), MessagesFactory.getMessages()
                                .search_expression_notempty(), null);
                    else {
                        isSimple = false;
                        String viewPk = entityCombo.getValue().get("value"); //$NON-NLS-1$
                        Dispatcher.forwardEvent(ItemsEvents.SearchView, viewPk);
                        winAdvanced.close();
                    }
                }

            });
            advancedPanel.addButton(searchBtn);
            Button cancelBtn = new Button(MessagesFactory.getMessages().cancel_btn());
            cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                public void componentSelected(ButtonEvent ce) {
                    winAdvanced.close();
                }

            });
            advancedPanel.addButton(cancelBtn);
            winAdvanced.show();
            winAdvanced.alignTo(toolBar.getElement(), "tl", new int[] { 0, 35 }); //$NON-NLS-1$
        } else if (!winAdvanced.isVisible()) {
            winAdvanced.show();
        }

        // set criteria
        if (criteria != null)
            advancedPanel.setCriteria(criteria);
        else
            advancedPanel.cleanCriteria();
    }
}
