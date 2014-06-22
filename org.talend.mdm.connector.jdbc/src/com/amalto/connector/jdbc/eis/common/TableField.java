package com.amalto.connector.jdbc.eis.common;

public class TableField {
	
	private String fieldName;
	
	private String fieldValue;
	
	private boolean isKey;
	

	public TableField(String fieldName, String fieldValue) {
		super();
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public TableField(String fieldName, String fieldValue, boolean isKey) {
		super();
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.isKey = isKey;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}
	
	

}
