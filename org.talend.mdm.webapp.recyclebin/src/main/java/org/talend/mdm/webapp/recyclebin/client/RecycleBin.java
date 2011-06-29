package org.talend.mdm.webapp.recyclebin.client;

import org.talend.mdm.webapp.recyclebin.client.mvc.RecycleBinController;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RecycleBin implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side RecycleBin service.
     */
    public static final String RECYCLEBIN_SERVICE = "RecycleBinService"; //$NON-NLS-1$   

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // log setting
        Log.setUncaughtExceptionHandler();

        Registry.register(RECYCLEBIN_SERVICE, GWT.create(RecycleBinService.class));

        // add controller to dispatcher
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new RecycleBinController());
        dispatcher.dispatch(RecycleBinEvents.InitFrame);
    }
}
