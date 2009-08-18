package com.amalto.core.util;

import java.util.HashSet;

public class RoleInstance {
	private boolean isWriteable = false;
	private HashSet<String> parameters = new HashSet<String>();
	public boolean isWriteable() {
		return isWriteable;
	}
	public void setWriteable(boolean isWriteable) {
		this.isWriteable = isWriteable;
	}
	public HashSet<String> getParameters() {
		return parameters;
	}
	public void setParameters(HashSet<String> parameters) {
		this.parameters = parameters;
	}
	
}
