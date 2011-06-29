package org.talend.mdm.webapp.gwt.message.client;

import com.google.gwt.core.client.JavaScriptObject;


public interface MessageCallback {

    public void handler(String msgId, JavaScriptObject data);
}
