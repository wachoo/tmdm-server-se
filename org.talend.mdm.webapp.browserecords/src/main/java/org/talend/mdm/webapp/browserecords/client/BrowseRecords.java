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

import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsController;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BrowseRecords implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side BrowseRecords service.
     */
    public static final String BROWSERECORDS_SERVICE = "BrowseRecordsService"; //$NON-NLS-1$

    public static final String USER_SESSION = "UserSession"; //$NON-NLS-1$


    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // log setting
        Log.setUncaughtExceptionHandler();

        Registry.register(BROWSERECORDS_SERVICE, GWT.create(BrowseRecordsService.class));

        // register user session
        Registry.register(USER_SESSION, new UserSession());

        // add controller to dispatcher
        final Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new BrowseRecordsController());

        // init app-header
        getItemService().getAppHeader(new AsyncCallback<AppHeader>() {

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
            }

            public void onSuccess(AppHeader header) {
                if (header.getDatacluster() == null || header.getDatamodel() == null) {
                    Window.alert(MessagesFactory.getMessages().data_model_not_specified());
                    return;
                }
                getSession().put(UserSession.APP_HEADER, header);

                dispatcher.dispatch(BrowseRecordsEvents.InitFrame);
            }

        });
    }

    private static BrowseRecordsServiceAsync getItemService() {

        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        return service;

    }

    public static UserSession getSession() {

        return Registry.get(BrowseRecords.USER_SESSION);

    }
}
