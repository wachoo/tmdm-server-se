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
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Window;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ItemsSearchContainer extends LayoutContainer {

    private ItemsListPanel itemsListPanel;

    private ItemsFormPanel itemsFormPanel;

    public ItemsSearchContainer() {

        setLayout(new BorderLayout());
        setBorders(false);

        itemsListPanel = new ItemsListPanel();
        add(itemsListPanel, new BorderLayoutData(LayoutRegion.CENTER));
        itemsListPanel.addListener(Events.Resize, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                itemsListPanel.layoutGrid();
            }
        });

        itemsFormPanel = new ItemsFormPanel();
        itemsFormPanel.setHeaderVisible(false);
        BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH);
        southData.setSplit(true);
        //southData.setCollapsible(true);
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
