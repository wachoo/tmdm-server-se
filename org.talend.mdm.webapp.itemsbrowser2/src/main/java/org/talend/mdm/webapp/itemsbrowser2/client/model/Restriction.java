package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Restriction implements Serializable, IsSerializable {

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
