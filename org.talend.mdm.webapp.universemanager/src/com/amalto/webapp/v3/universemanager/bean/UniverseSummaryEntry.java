package com.amalto.webapp.v3.universemanager.bean;

public class UniverseSummaryEntry {
	
	public UniverseSummaryEntry() {
		
	}
	
	public UniverseSummaryEntry(String objectName, String revisionID) {
		super();
		this.objectName = objectName;
		this.revisionID = revisionID;
	}

	private String objectName;
	
	private String revisionID;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getRevisionID() {
		return revisionID;
	}

	public void setRevisionID(String revisionID) {
		this.revisionID = revisionID;
	}

}
