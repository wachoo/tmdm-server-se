package org.talend.mdm.webapp.gxt.itemsbrowser2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Itemsbrowser2Service")
public interface Itemsbrowser2Service extends RemoteService {
    
    public static class Util {

        public static Itemsbrowser2ServiceAsync getInstance() {

            return GWT.create(Itemsbrowser2Service.class);
        }
    }

    String pingMDM(String input) throws Exception;

}
