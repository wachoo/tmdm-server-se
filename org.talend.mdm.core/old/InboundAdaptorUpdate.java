package com.amalto.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class InboundAdaptorUpdate implements Serializable{
	private String xpath;
	private Collection keys;
	
	
	/**
	 * 
	 */
	public InboundAdaptorUpdate() {
		this.keys = new ArrayList();
		this.xpath = null;
	}
	/**
	 * @param keys
	 * @param xpath
	 */
	public InboundAdaptorUpdate(String xpath,Collection keys) {
		super();
		// TODO Auto-generated constructor stub
		this.keys = keys;
		this.xpath = xpath;
	}
	public Collection getKeys() {
		return keys;
	}
	public void setKeys(Collection keys) {
		this.keys = keys;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	
	

}
