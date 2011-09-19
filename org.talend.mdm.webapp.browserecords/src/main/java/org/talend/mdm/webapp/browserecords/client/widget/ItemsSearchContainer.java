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
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ItemsSearchContainer extends LayoutContainer {

    private ItemsListPanel itemsListPanel;

    private ItemsDetailPanel itemsDetailPanel;

    private ItemsToolBar toolbar;

    private LayoutContainer rightContainer;

    private BorderLayoutData northData;

    public ItemsSearchContainer() {
        setLayout(new BorderLayout());
        setBorders(false);

        ContentPanel topPanel = new ContentPanel();
        topPanel.setHeaderVisible(false);
        toolbar = new ItemsToolBar();
        topPanel.add(toolbar);
        topPanel.add(toolbar.getAdvancedPanel());
        northData = new BorderLayoutData(LayoutRegion.NORTH);
        northData.setSize(30);
        northData.setSplit(true);
        add(topPanel, northData);
        toolbar.initContainer();

        // add(toolbar.getAdvancedPanel(), northData);

        itemsListPanel = new ItemsListPanel(toolbar);
        // itemsListPanel.layoutGrid();
        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 400);
        westData.setSplit(true);
        westData.setMargins(new Margins(0, 5, 0, 0));
        westData.setFloatable(true);
        westData.setMaxSize(800);
        add(itemsListPanel, westData);

        rightContainer = new LayoutContainer();
        rightContainer.setLayout(new FitLayout());
        itemsDetailPanel = new ItemsDetailPanel();
        itemsDetailPanel.setHeaderVisible(false);
        rightContainer.add(itemsDetailPanel);

        add(rightContainer, new BorderLayoutData(LayoutRegion.CENTER));
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

    public LayoutContainer getRightContainer() {
        return rightContainer;
    }

    public void resizeTop(float size) {
        northData.setSize(size);
        this.layout(true);
    }

}
