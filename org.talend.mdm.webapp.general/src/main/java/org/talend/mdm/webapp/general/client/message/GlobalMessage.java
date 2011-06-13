package org.talend.mdm.webapp.general.client.message;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

public class GlobalMessage {
	Map<String, JavaScriptFunction> messages = new HashMap<String, JavaScriptFunction>();
	
	private static GlobalMessage instance;
	private GlobalMessage(){}
	
	public static GlobalMessage getInstance(){
		if (instance == null){
			instance = new GlobalMessage();
		}
		return instance;
	}
	
	public void registerGlobalMessageHandler(String msgId, JavaScriptFunction fn){
		messages.put(msgId, fn);
	}
	
	public void notifyMessage(String msgId, Object data){
		JavaScriptFunction fn = messages.get(msgId);
		if (fn != null){
			fn.executeFn(msgId, data);
		}
	}
}
