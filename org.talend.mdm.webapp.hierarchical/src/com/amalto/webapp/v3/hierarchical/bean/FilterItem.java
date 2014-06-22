package com.amalto.webapp.v3.hierarchical.bean;

public class FilterItem{
	
	private String fieldLabel;
	private String fieldPath;
	private String operator;
	private String value;
	
	
	public FilterItem() {

	}

	public String getFieldLabel() {
		return fieldLabel;
	}


	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}


	public String getFieldPath() {
		return fieldPath;
	}


	public void setFieldPath(String fieldPath) {
		this.fieldPath = fieldPath;
	}


	public String getOperator() {
		return operator;
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}
	
}
