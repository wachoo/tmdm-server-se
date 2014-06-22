package com.amalto.webapp.v3.itemsbrowser.bean;

public class WhereCriteria {
	public WhereCriteria(){}
	
	Criteria[] criterias;
	public Criteria[] getCriterias() {
		return criterias;
	}
	public void setCriterias(Criteria[] criterias) {
		this.criterias = criterias;
	}
	public WhereCriteria(Criteria[] criterias){
		this.criterias = criterias;
		
	}
}
