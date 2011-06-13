package org.talend.mdm.webapp.general.client.message;

import com.google.gwt.core.client.JavaScriptObject;

public class JavaScriptFunction {

	JavaScriptObject fn;

	
	public JavaScriptFunction(JavaScriptObject fn){
		this.fn = fn;
	}
	
	public native void executeFn(String msgId, Object data)/*-{
		var fn = this.@org.talend.mdm.webapp.general.client.message.JavaScriptFunction::fn;
		fn(msgId, data);
	}-*/;
}
