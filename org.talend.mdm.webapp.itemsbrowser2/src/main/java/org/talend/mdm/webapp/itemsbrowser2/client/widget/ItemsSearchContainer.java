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
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class ItemsSearchContainer extends LayoutContainer {
    
    private ItemsListPanel itemsListPanel;
    
    private ItemsFormPanel itemsFormPanel;

    public ItemsSearchContainer() {
        
        setLayout(new BorderLayout());
        
        itemsListPanel = new ItemsListPanel();
        add(itemsListPanel, new BorderLayoutData(LayoutRegion.CENTER));
        
        itemsFormPanel = new ItemsFormPanel();
        BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, .5f, 200, 1000);
        southData.setSplit(true);  
        southData.setMargins(new Margins(5, 0, 0, 0));
        add(itemsFormPanel, southData);
        
    }

    public ItemsListPanel getItemsListPanel() {
        return itemsListPanel;
    }

    
    public ItemsFormPanel getItemsFormPanel() {
        return itemsFormPanel;
    }

}
