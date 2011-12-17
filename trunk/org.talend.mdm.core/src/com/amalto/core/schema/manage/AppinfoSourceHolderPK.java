package com.amalto.core.schema.manage;

public class AppinfoSourceHolderPK {
	
	private String dataModelName;
	
	private String conceptName;

	public AppinfoSourceHolderPK(String dataModelName, String conceptName) {
		super();
		this.dataModelName = dataModelName;
		this.conceptName = conceptName;
	}

	public String getDataModelName() {
		return dataModelName;
	}

	public void setDataModelName(String dataModelName) {
		this.dataModelName = dataModelName;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}
	
	@Override
	public String toString() {
		return this.dataModelName+"."+this.conceptName;
	}

}
