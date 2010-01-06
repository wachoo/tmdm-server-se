package com.amalto.webapp.v3.workflow.tasks.bean;

public class DataFieldValueVO {
	
	private String name;
	//private Object value;
	private String value;
	
	private String dataTypeClassName;
	private String isActivitVariable;
	
	//private String processInstanceUUIDValue;//redundancy
	//private String taskUUIDValue;//redundancy
	
	public DataFieldValueVO() {
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getDataTypeClassName() {
		return dataTypeClassName;
	}


	public void setDataTypeClassName(String dataTypeClassName) {
		this.dataTypeClassName = dataTypeClassName;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getIsActivitVariable() {
		return isActivitVariable;
	}


	public void setIsActivitVariable(String isActivitVariable) {
		this.isActivitVariable = isActivitVariable;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!o.getClass().equals(this.getClass())) {
			return false;
		}
		DataFieldValueVO other = (DataFieldValueVO) o;
		return other.getName().equals(this.getName());
	}

}
