// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingareacontrol.client.view;

import org.talend.mdm.webapp.stagingareacontrol.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.stagingareacontrol.client.i18n.StagingareaMessages;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.ContentPanel;

public abstract class AbstractView extends Composite {

    protected final ContentPanel mainPanel;

    protected final StagingareaMessages messages;

    public AbstractView() {
        messages = MessagesFactory.getMessages();
        mainPanel = new ContentPanel();
        mainPanel.setHeaderVisible(false);

        initComponents();
        initLayout();
        registerEvent();
        this.initComponent(mainPanel);
    }

    protected void initComponents() {

    }

    protected void initLayout() {

    }

    protected void registerEvent() {

    }

}
