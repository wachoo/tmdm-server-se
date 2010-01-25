/*
 * Created on 9 août 2005
 *
 */
package com.amalto.core.util;

import java.io.Serializable;
/**
 * @author bgrieder
 *
 */
public class XSDKey  implements Serializable {
	String selector;
	String[] fields;
	
	/**
	 * @param selector
	 * @param fields
	 */
	public XSDKey(String selector, String[] fields) {
		super();
		this.selector = selector;
		this.fields = fields;
	}
	public String[] getFields() {
		return fields;
	}
	public void setFields(String[] fields) {
		this.fields = fields;
	}
	public String getSelector() {
		return selector;
	}
	public void setSelector(String selector) {
		this.selector = selector;
	}
}
