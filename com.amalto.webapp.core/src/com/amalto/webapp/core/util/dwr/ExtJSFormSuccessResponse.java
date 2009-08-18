package com.amalto.webapp.core.util.dwr;

import java.util.HashMap;


public class ExtJSFormSuccessResponse extends ExtJSFormResponse{

	public ExtJSFormSuccessResponse(String message) {
		super(true,null,message,null);
	}
	
	public ExtJSFormSuccessResponse(String message, Object data) {
		super(
			true,
			data == null ? new HashMap<String, String>() : data,
			message,
			null
		);
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message == null ? "" : message;
	}
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data == null ? new HashMap<String, String>() : data;
	}
	
}
