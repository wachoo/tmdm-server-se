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
// ============================================================================package
package org.talend.mdm.webapp.journal.client;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Journal implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side Journal service.
     */
    public static final String Journal_SERVICE = "JournalService";

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // log setting
        Log.setUncaughtExceptionHandler();

        Registry.register(Journal_SERVICE, GWT.create(JournalService.class));

    }
}
