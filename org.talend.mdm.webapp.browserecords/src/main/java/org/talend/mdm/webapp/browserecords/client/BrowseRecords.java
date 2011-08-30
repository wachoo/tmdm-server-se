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
package org.talend.mdm.webapp.browserecords.client;

import org.talend.mdm.webapp.browserecords.client.layout.columns.ColumnLayoutPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BrowseRecords implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side BrowseRecords service.
     */
    public static final String BROWSERECORDS_SERVICE = "BrowseRecordsService"; //$NON-NLS-1$   

    public void onModuleLoad() {
        Window.alert("hello good");
        ColumnLayoutPanel clp = new ColumnLayoutPanel(3);
        clp.setSize("700", "600");
        RootPanel.get().add(clp);
    }
}
