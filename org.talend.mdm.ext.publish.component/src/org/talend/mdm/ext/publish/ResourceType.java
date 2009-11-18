package org.talend.mdm.ext.publish;



public enum ResourceType {
	
	DATAMODEL("dataModels"),
	DATAMODELTYPES("dataModelTypes");
	
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
