package com.amalto.core.util;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class RoleInstance {
	private boolean isWriteable = false;
	private boolean isReadOnly;
	private HashSet<String> parameters = new LinkedHashSet<String>();
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
	public boolean isReadOnly() {
		return isReadOnly;
	}
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	
}
