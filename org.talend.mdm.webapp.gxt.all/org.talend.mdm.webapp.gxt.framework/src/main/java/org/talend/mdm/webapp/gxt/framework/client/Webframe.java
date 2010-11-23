package org.talend.mdm.webapp.gxt.framework.client;

import org.talend.mdm.webapp.gxt.framework.client.mvc.AppController;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Webframe implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {


        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new AppController());
        dispatcher.dispatch(AppEvents.InitFrame);

    }

    

}
