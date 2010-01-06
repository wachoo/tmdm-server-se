package com.amalto.webapp.v3.workflow.tasks.schema;

import java.util.Map;

/**
 * @author starkey
 * 
 * Annotation Info For an xsd element
 *
 */
public class VAnnotationInfo {
	
	private Map<String,String> labelMap;
	
	private String accessRightToken;
	
	public VAnnotationInfo() {
		
	}

	public Map<String, String> getLabelMap() {
		return labelMap;
	}

	public void setLabelMap(Map<String, String> labelMap) {
		this.labelMap = labelMap;
	}

	public String getAccessRightToken() {
		return accessRightToken;
	}

	public void setAccessRightToken(String accessRightToken) {
		this.accessRightToken = accessRightToken;
	}
	
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("LabelMap:")
		  .append(labelMap)
		  .append(";")
		  .append("AccessRightToken:")
		  .append(accessRightToken);
		return sb.toString();
	}

}
