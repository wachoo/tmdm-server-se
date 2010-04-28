package org.talend.mdm.service.calljob;


public class ContextParam {
	String name;
	String value;
	boolean isItemXML;

	public ContextParam(String key, String value, boolean isItemXML){
		this.name=key;
		this.value=value;
		this.isItemXML=isItemXML;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public boolean isItemXML() {
		return isItemXML;
	}

	public void setItemXML(boolean isItemXML) {
		this.isItemXML = isItemXML;
	}
}
