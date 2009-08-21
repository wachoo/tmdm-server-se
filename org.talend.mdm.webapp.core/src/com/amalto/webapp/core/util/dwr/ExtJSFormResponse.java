package com.amalto.webapp.core.util.dwr;

import java.util.HashMap;

/**
 * For use with DWRAction.js in order to use the ExtJS Form Facility
 * The two derived classes {@link ExtJSFormSuccessResponse} and {@link ExtJSFormFailureResponse} are the wrappers
 * that all DWR Java Methods must return.
 * 
 * @author bgrieder
 *
 */
public abstract class ExtJSFormResponse {
	
	protected boolean success = false;
	protected HashMap<String, String> errors = new HashMap<String, String>();;
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
	
	public boolean isSuccess() {
		return success;
	}
	protected void setSuccess(
		boolean success) {
		this.success = success;
	}
	
	protected HashMap<String, String> getErrors() {
		return errors;
	}
	protected void setErrors(
		HashMap<String, String> fieldErrors) {
		this.errors = fieldErrors;
	}

	protected Object getData() {
		return data;
	}
	protected void setData(
		Object data) {
		this.data = data;
	}

	protected String getMessage() {
		return message;
	}
	protected void setMessage(
		String message) {
		this.message = message;
	}

}
