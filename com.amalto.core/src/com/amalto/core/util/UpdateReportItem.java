package com.amalto.core.util;

public class UpdateReportItem {

	public UpdateReportItem(String path, String oldValue, String newValue) {
		super();
		// TODO Auto-generated constructor stub
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.path = path;
	}
	public UpdateReportItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	String path;
	String oldValue;
	String newValue;
	
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
