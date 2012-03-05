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
// ============================================================================package
package org.talend.mdm.webapp.journal.client;

import org.talend.mdm.webapp.journal.client.mvc.JournalController;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class Journal implements EntryPoint {

    public static final String JOURNAL_SERVICE = "JournalService"; //$NON-NLS-1$

    public static final String JOURNAL_ID = "Journal"; //$NON-NLS-1$

    public void onModuleLoad() {
        if (GWT.isScript()) {
            XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$
            Log.setUncaughtExceptionHandler();
            Registry.register(JOURNAL_SERVICE, GWT.create(JournalService.class));

            Dispatcher dispatcher = Dispatcher.get();
            dispatcher.addController(new JournalController());
        } else {
            Log.setUncaughtExceptionHandler();
            Registry.register(JOURNAL_SERVICE, GWT.create(JournalService.class));

            Dispatcher dispatcher = Dispatcher.get();
            dispatcher.addController(new JournalController());
            
            GenerateContainer.generateContentPanel();
            onModuleRender();
            GenerateContainer.getContentPanel().setSize(Window.getClientWidth(), Window.getClientHeight());
            RootPanel.get().add(GenerateContainer.getContentPanel());
        }
    }

    private void onModuleRender() {
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.dispatch(JournalEvents.InitFrame);
    }
}