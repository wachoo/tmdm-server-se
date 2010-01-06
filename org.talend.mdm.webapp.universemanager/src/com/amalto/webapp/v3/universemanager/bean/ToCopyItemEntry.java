package com.amalto.webapp.v3.universemanager.bean;

public class ToCopyItemEntry {
	
	private String localClusterName;
	
	private String conceptName;
	
	private String localRevisonId;
	
	public ToCopyItemEntry(String localClusterName, String conceptName,
			String localRevisonId) {
		super();
		this.localClusterName = localClusterName;
		this.conceptName = conceptName;
		this.localRevisonId = localRevisonId;
	}

	public String getLocalClusterName() {
		return localClusterName;
	}

	public void setLocalClusterName(String localClusterName) {
		this.localClusterName = localClusterName;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public String getLocalRevisonId() {
		return localRevisonId;
	}

	public void setLocalRevisonId(String localRevisonId) {
		this.localRevisonId = localRevisonId;
	}
	
     public boolean equals(Object anObject) {
		
		boolean result=false;
		
		if(anObject instanceof ToCopyItemEntry){
			ToCopyItemEntry toCopyItemEntry=(ToCopyItemEntry)anObject;
			String token=this.localClusterName+"_"+this.conceptName;
			result=token.equals(toCopyItemEntry.getLocalClusterName()+"_"+toCopyItemEntry.getConceptName());
		}
		
		return result;
		
	}

	public String toString() {
		return this.localClusterName+"@"+this.conceptName+"@"+this.localRevisonId;
	}
     
}
