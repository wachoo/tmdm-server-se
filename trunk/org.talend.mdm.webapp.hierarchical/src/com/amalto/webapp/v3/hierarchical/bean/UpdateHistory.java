package com.amalto.webapp.v3.hierarchical.bean;

import java.util.HashSet;
import java.util.Iterator;

public class UpdateHistory {
	
	private DataObjectContext dataObjectContext;
	
	private HashSet<UpdateRecordItem> UpdateRecordItemsMap;
	
	public UpdateHistory() {
		this.UpdateRecordItemsMap = new HashSet<UpdateRecordItem>();
	}

	public UpdateHistory(DataObjectContext dataObjectContext) {
		super();
		this.dataObjectContext = dataObjectContext;
		this.UpdateRecordItemsMap = new HashSet<UpdateRecordItem>();
	}
	
	private HashSet<UpdateRecordItem> getUpdateRecordItemsMap() {
		if(this.UpdateRecordItemsMap==null)return new HashSet<UpdateRecordItem>();
		return this.UpdateRecordItemsMap;
	}
	
	public void logChange(String keys,String xpath,String newValue) {
		
		if(xpath==null||xpath.length()==0)return;
		
		UpdateRecordItem updateRecordItem=new UpdateRecordItem(keys,xpath,newValue);
		if(getUpdateRecordItemsMap().contains(updateRecordItem)){
			getUpdateRecordItemsMap().remove(updateRecordItem);
		}
		getUpdateRecordItemsMap().add(updateRecordItem);

	}
	
	public boolean isEmpty() {
		if(getUpdateRecordItemsMap().size()==0)return true;
		return false;
	}
	
	public Iterator<UpdateRecordItem> iterator() {
		
		return getUpdateRecordItemsMap().iterator();

	}
	
    public int size() {
		
		return getUpdateRecordItemsMap().size();

	}

	public DataObjectContext getDataObjectContext() {
		return dataObjectContext;
	}
	
	
	
}
