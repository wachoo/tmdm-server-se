package org.talend.mdm.webapp.browserecords.client.widget;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBaseModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsView;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ItemDetailToolBar extends ToolBar {

    public final static String CREATE_OPERATION = "CREATE"; //$NON-NLS-1$

    public final static String VIEW_OPERATION = "VIEW"; //$NON-NLS-1$

    private final Button saveButton = new Button(MessagesFactory.getMessages().save_btn());

    private final Button saveAndCloseButton = new Button(MessagesFactory.getMessages().save_close_btn());

    private final Button deleteButton = new Button(MessagesFactory.getMessages().delete_btn());

    private final Button deplicateButton = new Button(MessagesFactory.getMessages().deplicate_btn());

    private final Button joumalButton = new Button(MessagesFactory.getMessages().joumal_btn());

    private final Button refreshButton = new Button();

    private ComboBox<ItemBaseModel> workFlowCombo = new ComboBox<ItemBaseModel>();

    private ItemBean itemBean;

    private String operation;

    private BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private ItemsSearchContainer container = Registry.get(BrowseRecordsView.ITEMS_SEARCH_CONTAINER);

    public ItemDetailToolBar() {
        this.setBorders(false);
    }

    public ItemDetailToolBar(ItemBean itemBean, String operation) {
        this();
        this.itemBean = itemBean;
        this.operation = operation;
        initToolBar();
    }

    private void initToolBar() {
        if (operation.equalsIgnoreCase(ItemDetailToolBar.VIEW_OPERATION)) {
            this.addSaveButton();
            this.addSeparator();
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

                ItemPanel itemPanel = new ItemPanel(itemBean, ItemDetailToolBar.CREATE_OPERATION);
                String title = itemBean.getConcept() + " " + itemBean.getIds(); //$NON-NLS-1$
                detailPanel.addTabItem(title, itemPanel, ItemsDetailPanel.MULTIPLE, title);
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
                initJournal(itemBean.getIds(), itemBean.getConcept());
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
        add(new FillToolItem());
        ListStore<ItemBaseModel> workFlowList = new ListStore<ItemBaseModel>();
        workFlowCombo.setStore(workFlowList);
        add(workFlowCombo);
    }

    private void addSeparator() {
        add(new SeparatorToolItem());
    }

    public void updateToolBar() {

    }

    private native boolean initJournal(String id, String concept)/*-{
        $wnd.amalto.updatereport.UpdateReport.init();
        return true;
    }-*/;

}
