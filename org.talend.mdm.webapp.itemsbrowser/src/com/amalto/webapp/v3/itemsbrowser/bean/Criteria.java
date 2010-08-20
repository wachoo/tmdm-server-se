package com.amalto.webapp.v3.itemsbrowser.bean;

public class Criteria {
	private String Field;
	private String Operator;
	private String  Value;
	private String Join;
	
	public Criteria() {
		super();
	}
	
	
	public String getField() {
		return Field;
	}
	public void setField(String field) {
		Field = field;
	}
	public String getOperator() {
		return Operator;
	}
	public void setOperator(String operator) {
		Operator = operator;
	}
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}
	public String getJoin() {
		return Join;
	}
	public void setJoin(String join) {
		Join = join;
	}
	
	

}
