package com.amalto.webapp.v3.hierarchical.bean;

public class UpdateRecordItem {
	
	private String keys;
	
	private String xpath;
	
	private String oldValue;
	
	private String newValue;

	public UpdateRecordItem(String keys,String xpath, String oldValue, String newValue) {
		super();
		this.keys = keys;
		this.xpath = xpath;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public UpdateRecordItem(String keys,String xpath, String newValue) {
		super();
		this.keys = keys;
		this.xpath = xpath;
		this.newValue = newValue;
	}
	
	public String getUid() {
		return this.keys+"/"+this.xpath;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}
	
	@Override
	public int hashCode() {
		return getUid().hashCode();
	}
	
	@Override
	public String toString() {
		String valuePair=(oldValue==null?"":oldValue)+","+(newValue==null?"":newValue);
		return getUid() + ":"+valuePair;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UpdateRecordItem){
			return this.getUid().equals(((UpdateRecordItem)obj).getUid());
		}
		return false;
	}

}
