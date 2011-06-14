// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.general.client.message;

import java.util.HashMap;
import java.util.Map;

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
