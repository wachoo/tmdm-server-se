package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.io.Serializable;

public class Restriction implements Serializable {

	private String name;
	
	private String value;
	
	public Restriction(){
		
	}
	
	public Restriction(String name, String value){
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
