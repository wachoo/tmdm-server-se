package com.amalto.webapp.v3.workflow.tasks.bean;

import java.io.Serializable;
import java.util.Set;

import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;

import com.amalto.webapp.v3.workflow.tasks.util.HiddenPropertiesReader;

public class DataFieldDefinitionVO implements Comparable<DataFieldDefinitionVO>{
	
	private String dataTypeClassName;
	
	private boolean enumeration;
	
	private Set<String> enumerationValues;
	
	private Serializable initialValue;
	
	private String name;
	
	private String label;
	
	private boolean activityVariable;
	
	private boolean hidden;
	
	private String processInstanceUUIDValue;//redundancy
	
	private String activityUUIDValue;//redundancy

	public String getDataTypeClassName() {
		return dataTypeClassName;
	}

	public void setDataTypeClassName(String dataTypeClassName) {
		this.dataTypeClassName = dataTypeClassName;
	}

	public boolean isEnumeration() {
		return enumeration;
	}

	public void setEnumeration(boolean enumeration) {
		this.enumeration = enumeration;
	}

	public Set<String> getEnumerationValues() {
		return enumerationValues;
	}

	public void setEnumerationValues(Set<String> enumerationValues) {
		this.enumerationValues = enumerationValues;
	}

	public Serializable getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(Serializable initialValue) {
		this.initialValue = initialValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isActivityVariable() {
		return activityVariable;
	}

	public void setActivityVariable(boolean isActivityVariable) {
		this.activityVariable = isActivityVariable;
	}
	
    public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean isHidden) {
		this.hidden = isHidden;
	}

	public String getProcessInstanceUUIDValue() {
		return processInstanceUUIDValue;
	}

	public void setProcessInstanceUUIDValue(String processInstanceUUIDValue) {
		this.processInstanceUUIDValue = processInstanceUUIDValue;
	}

	public String getActivityUUIDValue() {
		return activityUUIDValue;
	}

	public void setActivityUUIDValue(String activityUUIDValue) {
		this.activityUUIDValue = activityUUIDValue;
	}

	public void load(DataFieldDefinition dataFieldDefinition,boolean isActivityVariable,String processInstanceUUIDValue,String activityUUIDValue) {
		
		this.setDataTypeClassName(dataFieldDefinition.getDataTypeClassName());
		this.setEnumeration(dataFieldDefinition.isEnumeration());
		this.setEnumerationValues(dataFieldDefinition.getEnumerationValues());
		this.setInitialValue(dataFieldDefinition.getInitialValue());
		this.setName(dataFieldDefinition.getName());
		this.setLabel(dataFieldDefinition.getLabel());
		this.setActivityVariable(isActivityVariable);
		this.setProcessInstanceUUIDValue(processInstanceUUIDValue);
		this.setActivityUUIDValue(activityUUIDValue);
		this.setHidden(HiddenPropertiesReader.isHidden(getName()));
		
	}
    
    /* 
     * compare happened in the same process
     * 
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!o.getClass().equals(this.getClass())) {
			return false;
		}
		DataFieldDefinitionVO other = (DataFieldDefinitionVO) o;
		return other.getName().equals(this.getName());
	}


	public int compareTo(DataFieldDefinitionVO o) {
		
		 if (o == null) {
			return 0;
		 }
		 
//		 if (!o.getClass().equals(this.getClass())) {
//			return 0;
//		 }
	
		 DataFieldDefinitionVO other=(DataFieldDefinitionVO)o;
		 
		 if (this.name==null||other.name==null) {
			return 0;
		 }
		 
		 return this.name.compareTo(other.name);
	}
    

}
