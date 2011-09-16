package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.List;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBaseModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsView;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyTreeDetail;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
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
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class ItemDetailToolBar extends ToolBar {

    public final static String CREATE_OPERATION = "CREATE"; //$NON-NLS-1$

    public final static String VIEW_OPERATION = "VIEW"; //$NON-NLS-1$
    
    public final static String DUPLICATE_OPERATION = "DUPLICATE_OPERATION"; //$NON-NLS-1$

    private final Button saveButton = new Button(MessagesFactory.getMessages().save_btn());

    private final Button saveAndCloseButton = new Button(MessagesFactory.getMessages().save_close_btn());

    private final Button deleteButton = new Button(MessagesFactory.getMessages().delete_btn());

    private final Button deplicateButton = new Button(MessagesFactory.getMessages().deplicate_btn());

    private final Button joumalButton = new Button(MessagesFactory.getMessages().joumal_btn());

    private final Button refreshButton = new Button();
    
    private final Button launchProcessButton = new Button();

    private ComboBox<ItemBaseModel> workFlowCombo = new ComboBox<ItemBaseModel>();

    private ItemBean itemBean;

    private String operation;

    private boolean isFkToolBar;

    private BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private ItemsSearchContainer container = Registry.get(BrowseRecordsView.ITEMS_SEARCH_CONTAINER);
    
    private ItemBaseModel selectItem;
    
    public ItemDetailToolBar() {
        this.setBorders(false);
    }

    public ItemDetailToolBar(ItemBean itemBean, String operation) {
        this();
        this.itemBean = itemBean;
        this.operation = operation;
        initToolBar();
    }

    public ItemDetailToolBar(ItemBean itemBean, String operation, boolean isFkToolBar) {
        this();
        this.itemBean = itemBean;
        this.operation = operation;
        this.isFkToolBar = isFkToolBar;
        initToolBar();

    }

    private void initToolBar() {
        if (operation.equalsIgnoreCase(ItemDetailToolBar.VIEW_OPERATION)) {
            this.addSaveButton();
            this.addSeparator();
            if (isFkToolBar) {
                this.addSaveQuitButton();
                this.addSeparator();
            }
            this.addDeleteMenu();
            this.addSeparator();
            this.addDuplicateButton();
            this.addSeparator();
            this.addJournalButton();
            this.addSeparator();
            this.addFreshButton();
            this.addWorkFlosCombo();
        } else if (operation.equalsIgnoreCase(ItemDetailToolBar.CREATE_OPERATION)) {
            this.addSaveButton();
            this.addSeparator();
            this.addSaveQuitButton();
            this.addWorkFlosCombo();
        }
    }

    private void addSaveButton() {
        saveButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        saveButton.setToolTip(MessagesFactory.getMessages().save_tip());

        saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                // TODO the following code need to be refactor, it is the demo code
                TabPanel tabPanel = container.getItemsDetailPanel().getTabPanel();
                TabItem tabItem = (TabItem) tabPanel.getSelectedItem();
                Widget widget = tabItem.getWidget(0);
                Dispatcher dispatch = Dispatcher.get();
                AppEvent app = new AppEvent(BrowseRecordsEvents.SaveItem);
                ItemNodeModel model = null;
                if (widget instanceof ItemPanel) {// save primary key
                    ItemPanel itemPanel = (ItemPanel) tabItem.getWidget(0);
                    model = (ItemNodeModel) itemPanel.getTree().getTree().getItem(0).getUserObject();
                    app.setData("ItemBean", itemPanel.getItem()); //$NON-NLS-1$
                    app.setData("isCreate", itemPanel.getOperation().equals(ItemDetailToolBar.CREATE_OPERATION) ? true : false); //$NON-NLS-1$
                } else if (widget instanceof ForeignKeyTreeDetail) { // save foreign key
                    ForeignKeyTreeDetail fkDetail = (ForeignKeyTreeDetail) tabItem.getWidget(0);
                    model = fkDetail.getRootModel();
                    app.setData(
                            "ItemBean", fkDetail.isCreate() ? new ItemBean(fkDetail.getViewBean().getBindingEntityModel().getConceptName(), "", "") : itemBean); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    app.setData("isCreate", fkDetail.isCreate()); //$NON-NLS-1$
                }
                app.setData(model);
                dispatch.dispatch(app);
                
            }
        });
        add(saveButton);
    }

    private void addSaveQuitButton() {
        saveAndCloseButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.save_and_close()));
        saveAndCloseButton.setToolTip(MessagesFactory.getMessages().save_close_tip());

        saveAndCloseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                saveButton.fireEvent(Events.Select);
                ItemsSearchContainer itemsSearchContainer = Registry.get(BrowseRecordsView.ITEMS_SEARCH_CONTAINER);
                ItemsDetailPanel detailPanel = itemsSearchContainer.getItemsDetailPanel();
                detailPanel.closeCurrentTab();
            }
        });
        add(saveAndCloseButton);
    }

    private void addDeleteMenu() {
        deleteButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));

        Menu deleteMenu = new Menu();
        MenuItem delete_SendToTrash = new MenuItem(MessagesFactory.getMessages().trash_btn());
        delete_SendToTrash.addSelectionListener(new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {
                final MessageBox box = MessageBox.prompt(MessagesFactory.getMessages().path(), MessagesFactory.getMessages()
                        .path_desc());
                box.getTextBox().setValue("/"); //$NON-NLS-1$
                box.addCallback(new Listener<MessageBoxEvent>() {

                    public void handleEvent(MessageBoxEvent be) {
                        String url = be.getValue();
                        service.logicalDeleteItem(itemBean, url, new AsyncCallback<ItemResult>() {

                            public void onSuccess(ItemResult arg0) {
                                ItemsListPanel listPanel = container.getItemsListPanel();
                                listPanel.refreshGrid();
                                container.getItemsDetailPanel().closeCurrentTab();
                            }

                            public void onFailure(Throwable arg0) {

                            }
                        });
                    }
                });
            }
        });
        delete_SendToTrash.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
        deleteMenu.add(delete_SendToTrash);

        MenuItem delete_Delete = new MenuItem(MessagesFactory.getMessages().delete_btn());
        deleteMenu.add(delete_Delete);
        delete_Delete.addSelectionListener(new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {
                MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages().delete_confirm(),
                        new Listener<MessageBoxEvent>() {

                            public void handleEvent(MessageBoxEvent be) {
                                if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                    service.deleteItemBean(itemBean, new AsyncCallback<ItemResult>() {

                                        public void onFailure(Throwable arg0) {

                                        }

                                        public void onSuccess(ItemResult arg0) {
                                            ItemsListPanel listPanel = container.getItemsListPanel();
                                            listPanel.refreshGrid();
                                            container.getItemsDetailPanel().closeCurrentTab();
                                        }

                                    });
                                }
                            }
                        });

            }
        });

        delete_Delete.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));

        deleteButton.setMenu(deleteMenu);

        add(deleteButton);
    }

    private void addDuplicateButton() {
        deplicateButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.duplicate()));
        deplicateButton.setToolTip(MessagesFactory.getMessages().deplicate_tip());
        deplicateButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                ItemsSearchContainer itemsSearchContainer = Registry.get(BrowseRecordsView.ITEMS_SEARCH_CONTAINER);
                ItemsDetailPanel detailPanel = itemsSearchContainer.getItemsDetailPanel();
                String title = itemBean.getConcept() + " " + itemBean.getIds(); //$NON-NLS-1$
                if(isFkToolBar){
                    ForeignKeyTreeDetail fkTree = (ForeignKeyTreeDetail) detailPanel.getTabPanel().getSelectedItem().getWidget(0);
                    ForeignKeyTreeDetail duplicateFkTree = new ForeignKeyTreeDetail(fkTree.getFkModel(), true);
                    detailPanel.addTabItem(title, duplicateFkTree, ItemsDetailPanel.MULTIPLE, title);
                } else {
                    ItemPanel itemPanel = new ItemPanel(itemBean, ItemDetailToolBar.CREATE_OPERATION);
                    detailPanel.addTabItem(title, itemPanel, ItemsDetailPanel.MULTIPLE, title);
                }

            }

        });
        add(deplicateButton);
    }

    private void addJournalButton() {
        joumalButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.journal()));
        joumalButton.setToolTip(MessagesFactory.getMessages().joumal_tip());

        joumalButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                String ids = itemBean.getIds();
                if(ids.indexOf("@") != -1){ //$NON-NLS-1$
                    ids = ids.replaceAll("@", "."); //$NON-NLS-1$ //$NON-NLS-2$
                }
                initJournal(ids, itemBean.getConcept());
            }

        });
        add(joumalButton);
    }

    private void addFreshButton() {
        refreshButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refreshToolbar()));
        refreshButton.setToolTip(MessagesFactory.getMessages().refresh_tip());
        refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {

            }

        });
        add(refreshButton);
    }

    private void addWorkFlosCombo() {
        service.getRunnableProcessList(itemBean.getConcept(), Locale.getLanguage(), new AsyncCallback<List<ItemBaseModel>>() {
            
            public void onSuccess(List<ItemBaseModel> processList) {
                add(new FillToolItem());
                ListStore<ItemBaseModel> workFlowList = new ListStore<ItemBaseModel>();
                workFlowList.add(processList);
                workFlowCombo.setStore(workFlowList);
                workFlowCombo.setDisplayField("value");//$NON-NLS-1$
                workFlowCombo.setValueField("key");//$NON-NLS-1$
                workFlowCombo.setTypeAhead(true);
                workFlowCombo.setTriggerAction(TriggerAction.ALL);
                workFlowCombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {
                    
                    @Override
                    public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                        selectItem = se.getSelectedItem();
                    }
                });
                add(workFlowCombo);
                launchProcessButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.launch_process()));
                launchProcessButton.setToolTip(MessagesFactory.getMessages().launch_process_tooltip());
                launchProcessButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                       if(selectItem == null){
                           MessageBox.alert(MessagesFactory.getMessages().warning_title(), "Please select a process first!", null); //$NON-NLS-1$
                           return;
                       }
                       final MessageBox waitBar = MessageBox.wait("Processing", "Processing, please wait...", "Processing..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       String[] ids = itemBean.getIds().split("@"); //$NON-NLS-1$
                       
                       service.processItem(itemBean.getConcept(), ids, (String)selectItem.get("key"), new AsyncCallback<String>() { //$NON-NLS-1$
                           public void onSuccess(String result) {
                               waitBar.close();
                               if(result.indexOf("Ok") >= 0){ //$NON-NLS-1$
                                   MessageBox.alert("Status", "Process done!", null); //$NON-NLS-1$ //$NON-NLS-2$
                               }else{
                                   MessageBox.alert("Status", "Process failed!", null); //$NON-NLS-1$ //$NON-NLS-2$
                               }
                           }
                           
                           public void onFailure(Throwable arg0) {
                               
                           }
                      });
                    }
                });
                add(launchProcessButton);
            }
            
            public void onFailure(Throwable arg0) {
                
            }
        });
    }

    private void addSeparator() {
        add(new SeparatorToolItem());
    }

    public void updateToolBar() {

    }

    private native boolean initJournal(String ids, String concept)/*-{
        $wnd.amalto.updatereport.UpdateReport.browseUpdateReportWithSearchCriteria(concept, ids, true);
        return true;
    }-*/;

}
