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

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ItemsSearchContainer extends LayoutContainer {

    private ItemsListPanel itemsListPanel;

    private ItemsDetailPanel itemsDetailPanel;

    private ItemsToolBar toolbar;

    public ItemsSearchContainer() {

        setLayout(new BorderLayout());
        setBorders(false);

        toolbar = new ItemsToolBar();
        add(toolbar, new BorderLayoutData(LayoutRegion.NORTH, 30));

        itemsListPanel = new ItemsListPanel();
        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 400);
        westData.setSplit(true);
        westData.setMargins(new Margins(0, 5, 0, 0));
        add(itemsListPanel, westData);
        itemsListPanel.addListener(Events.Resize, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                itemsListPanel.layoutGrid();
            }
        });

        itemsDetailPanel = new ItemsDetailPanel();
        itemsDetailPanel.setHeaderVisible(false);
        add(itemsDetailPanel, new BorderLayoutData(LayoutRegion.CENTER));
    }

    public ItemsListPanel getItemsListPanel() {
        return itemsListPanel;
    }

    public ItemsDetailPanel getItemsDetailPanel() {
        return itemsDetailPanel;
    }

    public ItemsToolBar getToolBar() {
        return toolbar;
    }

}
