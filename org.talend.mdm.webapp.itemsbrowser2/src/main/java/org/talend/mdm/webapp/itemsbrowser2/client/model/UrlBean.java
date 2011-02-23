package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.io.Serializable;

public class UrlBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String address;
	
	public UrlBean(){}
	
	public UrlBean(String name, String address){
		this.name = name;
		this.address = address;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
}
