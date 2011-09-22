// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.Window;

public class ItemPanel extends ContentPanel {

    private final TreeDetail tree = new TreeDetail();

    private ContentPanel smartPanel = new ContentPanel() {

        @Override
        public void onAttach() {
            Window.enableScrolling(true);
            setSize(Window.getClientWidth(), Window.getClientHeight());
            super.onAttach();
        }
    };

    private ItemDetailToolBar toolBar;

    private ItemBean item;

    private String operation;

    public ItemPanel() {

    }

    public ItemPanel(ItemBean item, String operation) {

        this.item = item;
        this.toolBar = new ItemDetailToolBar(item, operation);
        this.operation = operation;
        this.initUI();
    }

    private void initUI() {
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setTopComponent(toolBar);
        if (ItemDetailToolBar.CREATE_OPERATION.equals(operation)) {
            tree.initTree(null);
        } else if (ItemDetailToolBar.VIEW_OPERATION.equals(operation)) {
            tree.initTree(item);
        } else if (ItemDetailToolBar.DUPLICATE_OPERATION.equals(operation)) {
            tree.initTree(item);
        } else if (ItemDetailToolBar.PERSONALEVIEW_OPERATION.equals(operation)
                || ItemDetailToolBar.SMARTVIEW_OPERATION.equals(operation)) {
            tree.initTree(item);
        } else {
            tree.initTree(null);
        }

        tree.expand();
        this.add(tree);
        // smartPanel.setLayout(new FitLayout());
        smartPanel.setVisible(false);
        smartPanel.setHeaderVisible(false);
        this.add(smartPanel);
        // this.setBottomComponent(new PagingToolBarEx(50));
    }

    public void handleEvent(AppEvent event) {
        if (event.getType() == BrowseRecordsEvents.UpdatePolymorphism) {
            tree.handleEvent(event);
        } else if(event.getType() == BrowseRecordsEvents.ExecuteVisibleRule) {
        	tree.handleEvent(event);
        }
    }

    public ItemBean getItem() {
        return item;
    }

    public TreeDetail getTree() {
        return tree;
    }

    public String getOperation() {
        return operation;
    }

    public void refreshTree() {
        tree.refreshTree(item);
    }

    public ContentPanel getSmartPanel() {
        return smartPanel;
    }

}
