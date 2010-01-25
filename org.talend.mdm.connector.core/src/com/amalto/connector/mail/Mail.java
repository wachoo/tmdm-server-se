package com.amalto.connector.mail;

import java.util.ArrayList;


/**
 * @author jfeltesse
 *
 */
public abstract class Mail {
	
	public static final String DEFAULT_CONTENT_TYPE = "text/xml";
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	protected ArrayList parts;
	
	/**
	 * Adds a part to the parts array
	 * 
	 * @param mbp
	 */
	public void addPart(MailPart mbp) {
		parts.add(mbp);
	}
	
	/**
	 * Adds a part to the parts array
	 * 
	 * @param mbp
	 */
	public void removePart(int index) {
		parts.remove(index);
	}
	
	/**
	 * Retrieves the part at the given index
	 * 
	 * @param index
	 * @return
	 */
	public MailPart getPart(int index) {
		return (MailPart) parts.get(index);
	}
	
	/**
	 * Gives the number of parts
	 * 
	 * @return the number of parts
	 */
	public int getNbParts() {
		return parts.size();
	}
	
	
	
}
