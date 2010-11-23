package org.talend.mdm.webapp.gxt.itemsbrowser2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface Itemsbrowser2ServiceAsync {

    void pingMDM(String input, AsyncCallback<String> callback);
}
