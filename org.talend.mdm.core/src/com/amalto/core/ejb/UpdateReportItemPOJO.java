package com.amalto.core.ejb;

import org.apache.commons.lang.StringEscapeUtils;

public class UpdateReportItemPOJO {
	
	private String path;
	private String oldValue;
	private String newValue;

	public UpdateReportItemPOJO(String path, String oldValue, String newValue) {
		super();
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.path = path;
	}
	public UpdateReportItemPOJO() {
		super();
	}
	
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
	
	public String serialize() {
		String item=
		"<Item>\n"+
	    "   <path>"+StringEscapeUtils.escapeXml(this.getPath())+"</path>\n"+
	    "   <oldValue>"+StringEscapeUtils.escapeXml(this.getOldValue())+"</oldValue>\n"+
	    "   <newValue>"+StringEscapeUtils.escapeXml(this.getNewValue())+"</newValue>\n"+
	    "</Item>\n";
		
		return item;
	}
	
	
}
