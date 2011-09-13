// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class ItemsDetailPanel extends ContentPanel {
    
    public final static String SINGLETON = "SINGLETON"; //$NON-NLS-1$
    public final static String MULTIPLE = "MULTIPLE"; //$NON-NLS-1$
    private TabPanel tabPanel = new TabPanel();  

    private ContentPanel banner = new ContentPanel();
    Text textTitle = new Text();
    Text textDesc = new Text();
    
    public ItemsDetailPanel() {
        super();
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setLayout(new RowLayout(Orientation.VERTICAL));
        this.setWidth(800);
        this.setHeight(500);
        this.initPanel();
    }    
    
    private void initPanel(){    
        // tabPanel.setWidth(450);
        banner.setHeaderVisible(false);
        banner.setHeight("60px"); //$NON-NLS-1$
        banner.setStyleName("banner"); //$NON-NLS-1$
        textTitle.setStyleName("Title"); //$NON-NLS-1$
        textDesc.setStyleName("Description"); //$NON-NLS-1$
        banner.add(textTitle);
        banner.add(textDesc);
        add(banner);
        
        tabPanel.setAutoHeight(true);
        add(tabPanel);        
    }

    public void initBanner(String title, String description) {
        textTitle.setText(title);
        textDesc.setText(description);
    }

    public void addTabItem(String title, ContentPanel panel, String pattern, String id){
        if(pattern.equalsIgnoreCase(ItemsDetailPanel.MULTIPLE)){
            TabItem newTab = new TabItem(title);
            newTab.setId(id);
            newTab.setClosable(true);
            newTab.addStyleName("pad-text");   //$NON-NLS-1$
            newTab.add(panel);
            tabPanel.add(newTab);
            tabPanel.setSelection(newTab);
        }else{
            TabItem newTab = tabPanel.getItemByItemId(id);
            if(newTab == null){
                newTab = new TabItem(title); 
                newTab.setId(id);
                newTab.setClosable(true);
                newTab.addStyleName("pad-text");   //$NON-NLS-1$
                newTab.add(panel);
                tabPanel.add(newTab);
                tabPanel.setSelection(newTab);
            }else{
                newTab.removeAll();
                newTab.add(panel);
                newTab.layout(true);
            }
        }        
    }
    
    public void clearContent() {
        tabPanel.removeAll();
    }

    public void closeCurrentTab(){
        TabItem itemTab = tabPanel.getSelectedItem();
        tabPanel.remove(itemTab);
    }
    
    public TabItem getTabPanelById(String id){
        return tabPanel.getItemByItemId(id);
    }

    public TabPanel getTabPanel() {
        return tabPanel;
    }

}
