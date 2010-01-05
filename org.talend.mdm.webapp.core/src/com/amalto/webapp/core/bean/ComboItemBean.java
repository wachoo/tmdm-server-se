package com.amalto.webapp.core.bean;

public class ComboItemBean {
	
	private String value;
	
	private String text;
	
	public ComboItemBean(String value, String text) {
		super();
		this.text = text;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
