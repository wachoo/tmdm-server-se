package com.amalto.core.plugin.base.workflowtrigger;

import java.io.Serializable;

public class VariableParameter implements Serializable{
	
	private String scope;
	
	private String activityId;
	
	private String name;
	
	private String type;
	
	private boolean fromItem;
	
	private String xpath;
	
	private String value;
	
	public VariableParameter() {
		
	}

	public VariableParameter(String scope, String activityId, String name,
			String type, boolean fromItem, String xpath, String value) {
		super();
		this.scope = scope;
		this.activityId = activityId;
		this.name = name;
		this.type = type;
		this.fromItem = fromItem;
		this.xpath = xpath;
		this.value = value;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFromItem() {
		return fromItem;
	}

	public void setFromItem(boolean fromItem) {
		this.fromItem = fromItem;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
