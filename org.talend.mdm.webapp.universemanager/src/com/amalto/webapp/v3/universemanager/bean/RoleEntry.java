package com.amalto.webapp.v3.universemanager.bean;

public class RoleEntry extends ObjectRevisionEntry{
	
	public RoleEntry() {
		
	}
	
	public RoleEntry(String roleName, String localRevisionID) {
		super();
		this.roleName = roleName;
		this.localRevisionID = localRevisionID;
	}

	private String roleName;
	
	private String localRevisionID;

	
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getLocalRevisionID() {
		return cleanRevisionName(localRevisionID);
	}

	public void setLocalRevisionID(String localRevisionID) {
		this.localRevisionID = localRevisionID;
	}

	public boolean equals(Object anObject) {
		
		boolean result=false;
		
		if(anObject instanceof RoleEntry){
			RoleEntry roleEntry=(RoleEntry)anObject;
			result=this.roleName.equals(roleEntry.getRoleName());
		}
		
		return result;
		
	}
	
}
