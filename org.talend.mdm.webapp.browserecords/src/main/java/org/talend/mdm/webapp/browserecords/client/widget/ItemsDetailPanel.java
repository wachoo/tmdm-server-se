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

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class ItemsDetailPanel extends ContentPanel {
    
    private TabPanel tabPanel = new TabPanel();  
    
    public ItemsDetailPanel() {
        super();
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setLayout(new FitLayout());
        this.setWidth(800);
        this.setHeight(500);
        this.initPanel();
    }    
    
    private void initPanel(){    
        tabPanel.setWidth(450);  
        tabPanel.setAutoHeight(true);  
                
        add(tabPanel);        
    }
    
    public void addTabItem(String title, ContentPanel panel){
        TabItem newTab = new TabItem(title); 
        newTab.setClosable(true);
        newTab.addStyleName("pad-text");   //$NON-NLS-1$
        newTab.add(panel);
        tabPanel.add(newTab);
    }
}
