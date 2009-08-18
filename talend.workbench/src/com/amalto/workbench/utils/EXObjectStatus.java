package com.amalto.workbench.utils;

public enum EXObjectStatus {
	NEW(true),
	ACTIVE(true),
	DEACTIVE(true);
	boolean editable=true;
	
	
	EXObjectStatus(boolean editable){
		this.editable=editable;
	}
	
}
