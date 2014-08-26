package com.amalto.webapp.core.util.dwr;

import java.util.HashMap;

public abstract class ExtJSFormResponse {
	
	protected boolean success = false;
	protected HashMap<String, String> errors = new HashMap<String, String>();

    protected Object data;
	protected String message="";
	
	
	protected ExtJSFormResponse(
		boolean success,
		Object data,
		String message,
		HashMap<String, String> fieldErrors) {
		super();
		this.success = success;
		this.data = data;
		this.message = message;
		this.errors = fieldErrors;
	}

}
