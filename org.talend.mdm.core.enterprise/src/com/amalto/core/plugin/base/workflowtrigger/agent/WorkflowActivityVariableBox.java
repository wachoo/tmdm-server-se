package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.util.HashMap;
import java.util.Map;

public class WorkflowActivityVariableBox extends WorkflowVariableSet{
	
	private String activityId;
	
	private Map<String,Object> variables;
	
	public WorkflowActivityVariableBox(String activityId) {
		this.activityId=activityId;
		this.variables=new HashMap<String, Object>();
	}
	
	public void addVariable(String variableId,String variableValue,String variableType) {
		
		variables.put(variableId, parseType(variableValue, variableType));

	}
	
	
	public Map<String, Object> getVariables() {
		return variables;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WorkflowActivityVariableBox){
			WorkflowActivityVariableBox box=(WorkflowActivityVariableBox)obj;
			return this.activityId.equals(box.activityId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.activityId.hashCode();
	}


}
