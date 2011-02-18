package org.talend.mdm.webapp.itemsbrowser2.client;

import org.talend.mdm.webapp.itemsbrowser2.client.boundary.InBoundService;
import org.talend.mdm.webapp.itemsbrowser2.client.boundary.OutBoundService;
import org.talend.mdm.webapp.itemsbrowser2.client.util.UserSession;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Itemsbrowser2 implements EntryPoint {
    
    public static final String ITEMS_SERVICE = "ItemsService"; //$NON-NLS-1$
    
    public static final String USER_SESSION = "UserSession"; //$NON-NLS-1$
    
    public static boolean IS_SCRIPT = true;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        
        //log setting
        Log.setUncaughtExceptionHandler();
        
        //TODO: register bound service
        registerInBoundServices();
        //retrieveDataFromOutBound();
        
        //register user session
        Registry.register(USER_SESSION, new UserSession());
         
        //register service
        ItemsServiceAsync service = ItemsService.Util.getInstance();
        ServiceDefTarget endpoint = (ServiceDefTarget) service;
        String moduleRelativeURL = ITEMS_SERVICE;
        endpoint.setServiceEntryPoint(moduleRelativeURL);
        Registry.register(ITEMS_SERVICE, service);
        
        //add controller to dispatcher
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new ItemsController());
        
        //first time do not render
        if(RootPanel.get(ItemsView.ROOT_DIV)!=null)onModuleRender();
        

    }

    public static void onModuleRender() {
        Dispatcher dispatcher = Dispatcher.get();
        //dispatch a event
        dispatcher.dispatch(ItemsEvents.InitFrame);
    }

    private void registerInBoundServices() {
        InBoundService.renderUI();
    }

    private void retrieveDataFromOutBound() {
        String test=OutBoundService.getCurrentDataCluster();
        Info.display("Info","Current data cluster: "+test);
    }
    
    /**
     * DOC HSHU Comment method "getSession".
     */
    public static UserSession getSession() {
        
        return Registry.get(Itemsbrowser2.USER_SESSION);

    }
   
}
