package org.talend.mdm.webapp.browserecords.client.widget;

import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


public class ItemDetailToolBar  extends ToolBar{
    
    private final Button saveButton = new Button(MessagesFactory.getMessages().save_btn());
    
    private final Button deleteButton = new Button(MessagesFactory.getMessages().delete_btn());
    
    private final Button deplicateButton = new Button(MessagesFactory.getMessages().deplicate_btn());
    
    private final Button joumalButton = new Button(MessagesFactory.getMessages().joumal_btn());
    
    private final Button refreshButton = new Button();
    
    public ItemDetailToolBar() {
        // init user saved model
        //userCluster = BrowseRecords.getSession().getAppHeader().getDatacluster();
        this.setBorders(false);
        initToolBar();
    }
    
    private void initToolBar(){     
        saveButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        saveButton.setToolTip(MessagesFactory.getMessages().save_tip());
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
                
            }
        });
        delete_SendToTrash.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
        deleteMenu.add(delete_SendToTrash);
        
        MenuItem delete_Delete = new MenuItem(MessagesFactory.getMessages().delete_btn()); 
        deleteMenu.add(delete_Delete);
        delete_Delete.addSelectionListener(new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {
                
            }
        });
       
        delete_Delete.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
        
        deleteButton.setMenu(deleteMenu);        
        
        add(deleteButton);    
        add(new SeparatorToolItem());        
        deplicateButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                
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
    }   
    
    public void updateToolBar(){
        
    }
    

}
