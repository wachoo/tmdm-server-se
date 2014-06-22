package com.amalto.webapp.v3.hierarchical.bean;

public class DataObjectContext {
	
	private String revision;
	
	private String dataCluster;
	
	private String dataModel;
	
	private String concept;
	
	
	public DataObjectContext(String dataCluster,String dataModel, String concept) {
		super();
		this.dataCluster = dataCluster;
		this.dataModel = dataModel;
		this.concept = concept;
	}

	public DataObjectContext(String revision, String dataCluster,String dataModel, String concept) {
		super();
		this.revision = revision;
		this.dataCluster = dataCluster;
		this.dataModel = dataModel;
		this.concept = concept;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getDataCluster() {
		return dataCluster;
	}

	public void setDataCluster(String dataCluster) {
		this.dataCluster = dataCluster;
	}
	
	public String getDataModel() {
		return dataModel;
	}

	public void setDataModel(String dataModel) {
		this.dataModel = dataModel;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

}
