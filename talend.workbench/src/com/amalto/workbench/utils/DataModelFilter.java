package com.amalto.workbench.utils;


public class DataModelFilter {
	String role;
	boolean readOnly;
	boolean writeAccess;
	boolean hiddenAccess;
	boolean all;
	public DataModelFilter(String role, boolean readOnly, boolean writeAccess, boolean hiddenAccess,boolean all){
		this.role=role;
		this.readOnly=readOnly;
		this.writeAccess=writeAccess;
		this.hiddenAccess=hiddenAccess;
		this.all=all;
	}
	
	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isWriteAccess() {
		return writeAccess;
	}
	public void setWriteAccess(boolean writeAccess) {
		this.writeAccess = writeAccess;
	}
	public boolean isHiddenAccess() {
		return hiddenAccess;
	}
	public void setHiddenAccess(boolean hiddenAccess) {
		this.hiddenAccess = hiddenAccess;
	}
	
}
