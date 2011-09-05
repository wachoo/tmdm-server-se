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


public class ItemDetailToolBar  extends ToolBar{
    
    private final Button saveButton = new Button(MessagesFactory.getMessages().save_btn());
    
    private final Button saveAndCloseButton = new Button(MessagesFactory.getMessages().save_close_btn());
    
    private final Button deleteButton = new Button(MessagesFactory.getMessages().delete_btn());
    
    private final Button deplicateButton = new Button(MessagesFactory.getMessages().deplicate_btn());
    
    private final Button joumalButton = new Button(MessagesFactory.getMessages().joumal_btn());
    
    private final Button refreshButton = new Button();
    
    private ComboBox<ItemBaseModel> workFlowCombo = new ComboBox<ItemBaseModel>();
    
    private ItemBean itemBean;
    
    private BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
    
    public ItemDetailToolBar() {
        // init user saved model
        //userCluster = BrowseRecords.getSession().getAppHeader().getDatacluster();
        this.setBorders(false);
        initToolBar();
    }
    
    public ItemDetailToolBar(ItemBean itemBean){
        this();
        this.itemBean = itemBean;    
    }
    
    private void initToolBar(){     
        saveButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        saveButton.setToolTip(MessagesFactory.getMessages().save_tip());
        
        saveAndCloseButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.save_and_close()));
        saveAndCloseButton.setToolTip(MessagesFactory.getMessages().save_close_tip());
        
        deleteButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
        
        deplicateButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.duplicate()));
        deplicateButton.setToolTip(MessagesFactory.getMessages().deplicate_tip());
        
        joumalButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.journal()));
        joumalButton.setToolTip(MessagesFactory.getMessages().joumal_tip());
        
        refreshButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refreshToolbar()));
        refreshButton.setToolTip(MessagesFactory.getMessages().refresh_tip());
        
        saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                
            }
        });
        add(saveButton);    
        add(new SeparatorToolItem());  
        
        Menu deleteMenu = new Menu();
        MenuItem delete_SendToTrash = new MenuItem(MessagesFactory.getMessages().trash_btn()); 
        delete_SendToTrash.addSelectionListener(new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {
                final MessageBox box = MessageBox.prompt("Path", "Please input the path to delete the record(s): ");  
                box.getTextBox().setValue("/");
                box.addCallback(new Listener<MessageBoxEvent>() {                    
                    public void handleEvent(MessageBoxEvent be) {
                        String url = be.getValue();
                        service.logicalDeleteItem(itemBean, url, new AsyncCallback<ItemResult>() {
                            
                            public void onSuccess(ItemResult arg0) {
                                
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
                service.deleteItemBean(itemBean, new AsyncCallback<ItemResult>() {
                    
                    public void onSuccess(ItemResult arg0) {
                        
                    }
                    
                    public void onFailure(Throwable arg0) {
                        
                    }
                });
            }
        });
       
        delete_Delete.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
        
        deleteButton.setMenu(deleteMenu);        
        
        add(deleteButton);    
        add(new SeparatorToolItem());        
        deplicateButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                ItemsSearchContainer itemsSearchContainer = Registry.get(BrowseRecordsView.ITEMS_SEARCH_CONTAINER);
                ItemsDetailPanel detailPanel = itemsSearchContainer.getItemsDetailPanel();
                
                ItemPanel itemPanel = new ItemPanel(itemBean, "duplicate");
                String title = itemBean.getConcept() + " " + itemBean.getIds();
                detailPanel.addTabItem(title, itemPanel, ItemsDetailPanel.MULTIPLE, title);
            }

        });
        add(deplicateButton);
        add(new SeparatorToolItem());
        
        joumalButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                
            }

        });
        add(joumalButton);
        add(new SeparatorToolItem());        
        
        refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                
            }

        });
        add(refreshButton);
        add(new FillToolItem());  
        
        ListStore<ItemBaseModel> workFlowList = new ListStore<ItemBaseModel>();
        workFlowCombo.setStore(workFlowList);
        add(workFlowCombo);
    }   
    
    public void updateToolBar(){
        
    }
    
}
