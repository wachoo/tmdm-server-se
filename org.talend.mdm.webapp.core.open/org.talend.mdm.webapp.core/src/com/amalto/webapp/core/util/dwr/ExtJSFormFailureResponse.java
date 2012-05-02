package com.amalto.webapp.core.util.dwr;

import java.util.HashMap;

public class ExtJSFormFailureResponse extends ExtJSFormResponse{
	
	
	public ExtJSFormFailureResponse(
		String message,
		HashMap<String, String> fieldErrors
	) {
		super(
			false,
			null,
			message == null ? "": message,
			fieldErrors==null ? new HashMap<String, String>() : fieldErrors
		);
	}
	
	public ExtJSFormFailureResponse(String message) {
		super(false,null,message,new HashMap<String, String>());
	}
	
	public HashMap<String, String> getErrors() {
		return errors;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(
		String message) {
		this.message = message == null ? "" : message;
	}

	
}
