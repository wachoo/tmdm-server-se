package com.amalto.connector.mail;

/**
 * 
 * @author jfeltesse
 */
public class InvalidXMLValueException extends Exception {
	
	
	private static final long serialVersionUID = 1171024694429921852L;
	
	
	public InvalidXMLValueException() {
		super();
	}
	
	/**
	 * @param message
	 */
	public InvalidXMLValueException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public InvalidXMLValueException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param cause
	 */
	public InvalidXMLValueException(Throwable cause) {
		super(cause);
	}
	
	
}
