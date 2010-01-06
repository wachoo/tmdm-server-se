package com.amalto.webapp.v3.universemanager.bean;

public class TransformEntry extends ObjectRevisionEntry{
	
	public TransformEntry() {
		
	}
	
	public TransformEntry(String transformName, String localRevisionID) {
		super();
		this.transformName = transformName;
		this.localRevisionID = localRevisionID;
	}

	private String transformName;
	
	private String localRevisionID;

	public String getTransformName() {
		return transformName;
	}

	public void setTransformName(String transformName) {
		this.transformName = transformName;
	}

	public String getLocalRevisionID() {
		return cleanRevisionName(localRevisionID);
	}

	public void setLocalRevisionID(String localRevisionID) {
		this.localRevisionID = localRevisionID;
	}
	
}
