package org.talend.mdm.webapp.gxt.framework.client;

import org.talend.mdm.webapp.gxt.framework.shared.NavFolder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface AppService extends RemoteService {
    
    public static class Util {

        public static AppServiceAsync getInstance() {

            return GWT.create(AppService.class);
        }
    }

    String sayHello(String name) throws IllegalArgumentException;
    
    NavFolder getMenus(String language) throws Exception;
}
