package org.talend.mdm.ext.publish;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.talend.mdm.ext.publish.filter.AccessControlFilter;
import org.talend.mdm.ext.publish.resource.DataModelResource;
import org.talend.mdm.ext.publish.resource.DataModelTypesResource;
import org.talend.mdm.ext.publish.resource.DataModelsResource;

public class ServerServletApplication extends Application {


    public ServerServletApplication() {
        super();
    }

    public ServerServletApplication(Context context) {
        super(context);
    }

    @Override
    public Restlet createRoot() {

    	// Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        Router router = new Router(getContext());
        
        // Defines a route for the resource "list of dataModels"
        router.attach("/"+ResourceType.DATAMODEL.getName(), DataModelsResource.class);
        // Defines a route for the resource "dataModel"
        router.attach("/"+ResourceType.DATAMODEL.getName()+"/{dataModelName}", DataModelResource.class);
        
        router.attach("/"+ResourceType.DATAMODEL.getName()+"/{dataModelName}/types", DataModelTypesResource.class);


         //creates the filter and add it in front of the router
        AccessControlFilter accessControlFilter = new AccessControlFilter();
        accessControlFilter.setNext(router);

        return accessControlFilter;
    }

}
