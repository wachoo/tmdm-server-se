package com.amalto.webapp.v3.universemanager.bean;

public class StoredProcedureEntry extends ObjectRevisionEntry{
	
	public StoredProcedureEntry() {
		
	}
	
	public StoredProcedureEntry(String localRevisionID,boolean isChecked) {
		super();
		//this.storedProcedureName = storedProcedureName;
		this.localRevisionID = localRevisionID;
		this.isChecked = isChecked;
	}

	//private String storedProcedureName;
	
	private String localRevisionID;
	
	private boolean isChecked;


//	public String getStoredProcedureName() {
//		return storedProcedureName;
//	}
//
//	public void setStoredProcedureName(String storedProcedureName) {
//		this.storedProcedureName = storedProcedureName;
//	}

	public String getLocalRevisionID() {
		return cleanRevisionName(localRevisionID);
	}

	public void setLocalRevisionID(String localRevisionID) {
		this.localRevisionID = localRevisionID;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
}
