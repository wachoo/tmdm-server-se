package com.amalto.webapp.v3.itemsbrowser.bean;

public class Restriction {

	public Restriction(String name, String value) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
		this.value = value;
	}
	public Restriction() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String name;
	private String value;
	
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
