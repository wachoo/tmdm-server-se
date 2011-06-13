package org.talend.mdm.webapp.general.client.message;

import com.google.gwt.core.client.JavaScriptObject;

public class PublicMessageService {
	
	public static native void registerMessageService()/*-{
		$wnd.OverallFrame = {
			registerGlobalMessage : function(msgId, handler){//  msgConfig.id, msgConfig.handler
				@org.talend.mdm.webapp.general.client.message.PublicMessageService::registerGlobalMessageHandler(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(msgId, handler);
			},
			notifyGlobalMessage : function(msgId, data){
				@org.talend.mdm.webapp.general.client.message.PublicMessageService::notifyMessage(Ljava/lang/String;Ljava/lang/Object;)(msgId, data);
			}
		};
	}-*/;
	
	static void registerGlobalMessageHandler(String msgId, JavaScriptObject handler){
		
		JavaScriptFunction fsFn = new JavaScriptFunction(handler);
		GlobalMessage.getInstance().registerGlobalMessageHandler(msgId, fsFn);
	}
	
	static void notifyMessage(String msgId, Object data){
		GlobalMessage.getInstance().notifyMessage(msgId, data);
	}
	
}
