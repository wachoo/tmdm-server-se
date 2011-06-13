package org.talend.mdm.webapp.general.client;

import java.util.List;

import org.talend.mdm.webapp.general.model.MenuBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GeneralServiceAsync {

    void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

    void getMenus(String language, AsyncCallback<List<MenuBean>> callback);

    void getMsg(AsyncCallback<String> callback);
}
