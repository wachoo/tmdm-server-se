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
package org.talend.mdm.webapp.browserecords.client.widget;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.BrowseStagingRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.CloseStagingTabPostDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.NoOpPostDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.PostDeleteAction;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * created by yjli on 2014-1-28 Detailled comment
 * 
 */
public class StagingItemDetailToolBar extends ItemDetailToolBar {

    private final BrowseRecordsMessages msg = org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory.getMessages();

    public StagingItemDetailToolBar() {
        this.setBorders(false);
        this.setLayout(new ToolBarExLayout());
    }

    public StagingItemDetailToolBar(ItemsDetailPanel itemsDetailPanel) {
        this();
        this.itemsDetailPanel = itemsDetailPanel;
    }

    public StagingItemDetailToolBar(ItemBean itemBean, String operation, ViewBean viewBean, ItemsDetailPanel itemsDetailPanel) {
        this(itemsDetailPanel);
        this.itemBean = itemBean;
        this.operation = operation;
        this.viewBean = viewBean;
        initToolBar();
    }

    public StagingItemDetailToolBar(ItemBean itemBean, String operation, ViewBean viewBean, ItemsDetailPanel itemsDetailPanel,
            boolean openTab) {
        this(itemsDetailPanel);
        this.itemBean = itemBean;
        this.operation = operation;
        this.viewBean = viewBean;
        this.openTab = openTab;
        initToolBar();
    }

    public StagingItemDetailToolBar(ItemBean itemBean, String operation, boolean isFkToolBar, ViewBean viewBean,
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
        this.addDeleteButton();
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
        addDeleteButton();
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

    @Override
    protected void addDeleteButton() {
        if (deleteButton == null) {
            deleteButton = new Button(msg.mark_as_deleted());
            deleteButton.setId("deleteButton"); //$NON-NLS-1$
            deleteButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));

            deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), msg.mark_deleted_confirm(),
                            new Listener<MessageBoxEvent>() {

                                @Override
                                public void handleEvent(MessageBoxEvent be) {
                                    if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                        deleteRecord();
                                    }
                                }
                            });
                }
            });
        }
        add(deleteButton);
    }

    @Override
    protected PostDeleteAction buildPostDeleteAction() {
        return new CloseStagingTabPostDeleteAction(StagingItemDetailToolBar.this, NoOpPostDeleteAction.INSTANCE);
    }

    @Override
    protected BrowseRecordsServiceAsync getBrowseRecordsService() {
        return (BrowseStagingRecordsServiceAsync) Registry.get(BrowseRecords.BROWSESTAGINGRECORDS_SERVICE);
    }

    @Override
    protected Boolean isStaging() {
        return true;
    }
}
