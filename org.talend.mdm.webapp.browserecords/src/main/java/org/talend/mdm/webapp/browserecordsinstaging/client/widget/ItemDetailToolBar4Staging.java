// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecordsinstaging.client.widget;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

public class ItemDetailToolBar4Staging extends ItemDetailToolBar {

    public ItemDetailToolBar4Staging() {
        this.setBorders(false);
        this.setLayout(new ToolBarExLayout());
    }

    public ItemDetailToolBar4Staging(ItemsDetailPanel itemsDetailPanel) {
        this();
        this.itemsDetailPanel = itemsDetailPanel;
    }

    public ItemDetailToolBar4Staging(ItemBean itemBean, String operation, ViewBean viewBean, ItemsDetailPanel itemsDetailPanel) {
        this(itemsDetailPanel);
        this.itemBean = itemBean;
        this.operation = operation;
        this.viewBean = viewBean;
        initToolBar();
    }

    public ItemDetailToolBar4Staging(ItemBean itemBean, String operation, ViewBean viewBean, ItemsDetailPanel itemsDetailPanel,
            boolean openTab) {
        this(itemsDetailPanel);
        this.itemBean = itemBean;
        this.operation = operation;
        this.viewBean = viewBean;
        this.openTab = openTab;
        initToolBar();
    }

    public ItemDetailToolBar4Staging(ItemBean itemBean, String operation, boolean isFkToolBar, ViewBean viewBean,
            ItemsDetailPanel itemsDetailPanel) {
        this(itemsDetailPanel);
        this.itemBean = itemBean;
        this.operation = operation;
        this.isFkToolBar = isFkToolBar;
        this.viewBean = viewBean;
        initToolBar();
    }

    @Override
    protected void initViewToolBar() {
        if (!operation.equalsIgnoreCase(ItemDetailToolBar.VIEW_OPERATION)) {
            addPersonalViewButton();
            this.addSeparator();
        }
        this.addSaveButton();
        this.addSeparator();
        this.addSaveQuitButton();
        this.addSeparator();
        this.addDeleteMenu();
        this.addSeparator();
        this.addDuplicateButton();
        this.addSeparator();
        this.addFreshButton();
        if (this.openTab) {
            this.addSeparator();
            this.addOpenTabButton(false);
        }
        this.addOpenTaskButton();
        checkEntitlement(viewBean);
    }

    @Override
    protected void initSmartViewToolBar() {
        addGeneratedViewButton();
        addSeparator();
        addSmartViewCombo();
        addDeleteMenu();
        addSeparator();
        addPrintButton();
        addSeparator();
        this.addDuplicateButton();
        this.addSeparator();
        this.addFreshButton();
        if (this.openTab) {
            this.addSeparator();
            this.addOpenTabButton(true);
        }
    }

    @Override
    protected void initCreateToolBar() {
        this.addSaveButton();
        this.addSeparator();
        this.addSaveQuitButton();
    }
}
