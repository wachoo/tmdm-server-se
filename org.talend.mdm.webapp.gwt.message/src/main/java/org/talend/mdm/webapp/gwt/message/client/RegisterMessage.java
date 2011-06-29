package org.talend.mdm.webapp.gwt.message.client;

import com.google.gwt.core.client.JavaScriptObject;


public class RegisterMessage {

    public native static void registerGlobalMessage(String msgId, MessageCallback callback)/*-{
        $wnd.top.General.registerGlobalMessage(msgId, function(msgId, data){
            callback.@org.talend.mdm.webapp.gwt.message.client.MessageCallback::handler(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msgId, data);
        });
    }-*/;
    
    public native static void notifyGlobalMessage(String msgId, JavaScriptObject data)/*-{
        $wnd.top.General.notifyGlobalMessage(msgId, data);
    }-*/;
}

