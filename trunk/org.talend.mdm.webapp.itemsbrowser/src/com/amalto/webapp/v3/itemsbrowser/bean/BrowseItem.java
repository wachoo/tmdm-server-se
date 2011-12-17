package com.amalto.webapp.v3.itemsbrowser.bean;


public class BrowseItem {
	private String CriteriaName;
	private String ViewPK;
	private String owner;
	private Boolean shared;
	private WhereCriteria whereCriteria;
	
	public String getCriteriaName() {
		return CriteriaName;
	}

	public void setCriteriaName(String criteriaName) {
		CriteriaName = criteriaName;
	}

	public String getViewPK() {
		return ViewPK;
	}

	public void setViewPK(String viewPK) {
		ViewPK = viewPK;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	

	public WhereCriteria getWhereCriteria() {
		return whereCriteria;
	}

	public void setWhereCriteria(WhereCriteria whereCriteria) {
		this.whereCriteria = whereCriteria;
	}

	public BrowseItem() {
		super();
	}
public String marshal2String() {
		
	    String marshaledItem = 
	 "<BrowseItem>"+
		"<CriteriaName>"+this.CriteriaName +"</CriteriaName>"+
		"<ViewPK>"+this.ViewPK +"</ViewPK>"+
		"<Owner>"+this.owner +"</Owner>"+
		"<Shared>"+this.shared +"</Shared>"+
		getWhereCriteriaString()+
	"</BrowseItem>";
		return marshaledItem;

	}

 private String getWhereCriteriaString(){
	 StringBuffer whereCriteria = new StringBuffer("<WhereCriteria>");
	 Criteria[] criterias = this.whereCriteria.getCriterias();
	 for(int i=0;i<criterias.length;i++){
		 Criteria criteria = criterias[i];
		 whereCriteria.append("<Criteria>");
		 whereCriteria.append("<Field>"+criteria.getField()+"</Field>");
		 whereCriteria.append("<Operator>"+criteria.getOperator()+"</Operator>");
		 whereCriteria.append("<Value>"+criteria.getValue()+"</Value>");
		 if(criteria.getJoin()!=null)
			 whereCriteria.append("<Join>"+criteria.getJoin()+"</Join>");
		 else
			 whereCriteria.append("<Join></Join>");
		 whereCriteria.append("</Criteria>");
	 }
	 whereCriteria.append("</WhereCriteria>");
	 return whereCriteria.toString();
 }

}
