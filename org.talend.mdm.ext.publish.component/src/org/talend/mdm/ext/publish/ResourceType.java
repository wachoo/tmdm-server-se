package org.talend.mdm.ext.publish;



public enum ResourceType {
	
	DATAMODELS("dataModels"),
	DATAMODELSTYPES("dataModelsTypes"),
	CUSTOMTYPESSETS("customTypesSets"),
	PICTURES("pictures");
	
	ResourceType(String name) {
		this.name = name;
	}

	ResourceType() {
	};
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
