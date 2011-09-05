package org.talend.mdm.webapp.browserecords.client.widget;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyTreeDetail;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.ContentPanel;



public class ItemPanel extends ContentPanel {
    
    private final ForeignKeyTreeDetail tree = new ForeignKeyTreeDetail();
    
    private ItemDetailToolBar toolBar;
    private ItemBean item;
    private String operation;
    
    public ItemPanel(){
        
    }
    
    public ItemPanel(ItemBean item, String operation){
        
        this.item = item;
        this.toolBar = new ItemDetailToolBar(item, operation);
        this.operation = operation;        
        this.initUI();
    }
    
    @SuppressWarnings("static-access")
    private void initUI(){
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setTopComponent(toolBar);     
        tree.setViewBean((ViewBean)this.getSession().get(UserSession.CURRENT_VIEW));
        this.add(tree);
    }
    
    private static BrowseRecordsServiceAsync getItemService() {

        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        return service;

    }

    public static UserSession getSession() {
        return Registry.get(BrowseRecords.USER_SESSION);

    }
}
