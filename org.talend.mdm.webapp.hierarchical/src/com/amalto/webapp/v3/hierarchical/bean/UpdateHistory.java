package com.amalto.webapp.v3.hierarchical.bean;

import java.util.HashSet;
import java.util.Iterator;

public class UpdateHistory {
	
	private DataObjectContext dataObjectContext;
	
	private HashSet<UpdateRecordItem> UpdateRecordItemsMap;

	public UpdateHistory(DataObjectContext dataObjectContext) {
		super();
		this.dataObjectContext = dataObjectContext;
		this.UpdateRecordItemsMap = new HashSet<UpdateRecordItem>();
	}
	
	public void logChange(String keys,String xpath,String newValue) {
		
		UpdateRecordItem updateRecordItem=new UpdateRecordItem(keys,xpath,newValue);
		this.UpdateRecordItemsMap.add(updateRecordItem);

	}
	
	public boolean isEmpty() {
		if(this.UpdateRecordItemsMap.size()==0)return true;
		return false;
	}
	
	public Iterator<UpdateRecordItem> iterator() {
		
		return this.UpdateRecordItemsMap.iterator();

	}

	public DataObjectContext getDataObjectContext() {
		return dataObjectContext;
	}
	
	
	
	
}
