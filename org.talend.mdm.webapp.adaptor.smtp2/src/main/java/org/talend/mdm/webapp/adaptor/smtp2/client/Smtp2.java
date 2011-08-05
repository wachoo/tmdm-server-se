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

package org.talend.mdm.webapp.adaptor.smtp2.client;

import org.talend.mdm.webapp.adaptor.smtp2.client.mvc.Smtp2Controller;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Smtp2 implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side Smtp2 service.
     */
    public static final String Smtp2_SERVICE = "Smtp2Service";
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // log setting
        // Log.setUncaughtExceptionHandler();

        Registry.register(Smtp2_SERVICE, GWT.create(Smtp2Service.class));

        // add controller to dispatcher
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new Smtp2Controller());
        dispatcher.dispatch(Smtp2Events.InitFrame);
    }
}
