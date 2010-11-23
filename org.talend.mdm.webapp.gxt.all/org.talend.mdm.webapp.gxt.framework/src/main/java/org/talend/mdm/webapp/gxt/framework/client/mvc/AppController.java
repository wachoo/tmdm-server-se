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
package org.talend.mdm.webapp.gxt.framework.client.mvc;

import org.talend.mdm.webapp.gxt.framework.client.AppEvents;
import org.talend.mdm.webapp.gxt.framework.client.AppHeader;
import org.talend.mdm.webapp.gxt.framework.client.AppService;
import org.talend.mdm.webapp.gxt.framework.client.AppServiceAsync;
import org.talend.mdm.webapp.gxt.framework.shared.NavFolder;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class AppController extends Controller {

    public static final String APP_SERVICE = "AppService"; //$NON-NLS-1$

    private AppServiceAsync service;

    private AppView appView;

    private NavFolderView navFolderView;

    public AppController() {
        registerEventTypes(AppEvents.InitFrame);
        registerEventTypes(AppEvents.InitSideBar);
        registerEventTypes(AppEvents.Error);
        registerEventTypes(AppEvents.LoadMenus);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.InitFrame) {
            onInitFrame(event);
        } else if (type == AppEvents.InitSideBar) {
            onInitSideBar(event);
        } else if (type == AppEvents.Error) {
            onError(event);
        } else if (type == AppEvents.LoadMenus) {
            onLoadMenus(event);
        }
    }

    @Override
    public void initialize() {
        
        Log.setUncaughtExceptionHandler();
        Log.info("Initializing...");
        GXT.setDefaultTheme(Theme.BLUE, true);
        registerAppService();

        Registry.register(AppHeader.APP_HEADER, new AppHeader());
        
        appView = new AppView(this);
        navFolderView = new NavFolderView(this);
    }

    private void onInitFrame(final AppEvent event) {
        Log.info("Drawing frame...");
        forwardToView(appView, event);
    }
    
    private void onInitSideBar(final AppEvent event) {
        Log.info("Drawing sidebar...");
        forwardToView(navFolderView, event);
    }

    private void registerAppService() {
        service = AppService.Util.getInstance();
        ServiceDefTarget endpoint = (ServiceDefTarget) service;
        String moduleRelativeURL = APP_SERVICE;
        endpoint.setServiceEntryPoint(moduleRelativeURL);
        Registry.register(APP_SERVICE, service);
    }

    protected void onError(AppEvent ae) {
        Log.error("error: " + ae.<Object> getData()); //$NON-NLS-1$
        MessageBox.alert("Error", ae.<Object> getData().toString(), null);
    }

    protected void onLoadMenus(final AppEvent ae) {
        Log.info("Loading menus...");
        // get data
        service.getMenus("en", new AsyncCallback<NavFolder>() { //$NON-NLS-1$

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(AppEvents.Error, caught);
                    }

                    public void onSuccess(NavFolder result) {
                        ae.setData(result);
                        // fowardToView
                        forwardToView(navFolderView, ae);
                    }
                });

    }
}
