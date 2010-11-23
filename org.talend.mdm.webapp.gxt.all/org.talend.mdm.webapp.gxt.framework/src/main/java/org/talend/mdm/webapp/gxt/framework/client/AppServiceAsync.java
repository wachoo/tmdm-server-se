package org.talend.mdm.webapp.gxt.framework.client;

import org.talend.mdm.webapp.gxt.framework.shared.NavFolder;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AppServiceAsync {

    void sayHello(String name, AsyncCallback<String> callback) throws IllegalArgumentException;

    void getMenus(String language, AsyncCallback<NavFolder> callback);
}
