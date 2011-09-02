package org.talend.mdm.webapp.browserecords.client.widget;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;

import com.extjs.gxt.ui.client.widget.ContentPanel;


public class ItemPanel extends ContentPanel {
    
    private ItemDetailToolBar toolBar;
    private ItemBean item;
    
    public ItemPanel(){
        
    }
    
    public ItemPanel(ItemBean item){
        this.item = item;
        this.toolBar = new ItemDetailToolBar();
        this.initUI();
    }
    
    private void initUI(){
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setTopComponent(toolBar);
    }
}
