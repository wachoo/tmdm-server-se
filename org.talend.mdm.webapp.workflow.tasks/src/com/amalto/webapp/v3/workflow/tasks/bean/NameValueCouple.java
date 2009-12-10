package com.amalto.webapp.v3.workflow.tasks.bean;

public class NameValueCouple {
	
	private String name;
	
	private String value;
	
	public NameValueCouple() {
		
	}
	

	public NameValueCouple(String name, String value) {
		super();
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
	
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!o.getClass().equals(this.getClass())) {
			return false;
		}
		NameValueCouple other = (NameValueCouple) o;
		return other.getName().equals(this.getName());
	}
	

}
