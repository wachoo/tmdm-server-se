package org.talend.mdm.webapp.browserecords.client.widget;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;

import com.extjs.gxt.ui.client.widget.ContentPanel;


public class ItemPanel extends ContentPanel {
    
    private ItemDetailToolBar toolBar;
    private ItemBean item;
    private String type;
    
    public ItemPanel(){
        
    }
    
    public ItemPanel(ItemBean item, String type){
        this.item = item;
        this.toolBar = new ItemDetailToolBar(item);
        this.type = type;
        this.initUI();
    }
    
    private void initUI(){
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setTopComponent(toolBar);
    }
}
