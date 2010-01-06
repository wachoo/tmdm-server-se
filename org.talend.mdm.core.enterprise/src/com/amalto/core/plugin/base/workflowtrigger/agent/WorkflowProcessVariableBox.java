package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.util.HashMap;
import java.util.Map;

public class WorkflowProcessVariableBox extends WorkflowVariableSet{
	
	
	private Map<String,Object> variables;
	
	public WorkflowProcessVariableBox() {
		this.variables=new HashMap<String, Object>();
	}
	
	public void addVariable(String variableId,String variableValue,String variableType) {
		
		variables.put(variableId, parseType(variableValue, variableType));

	}
	
	public Map<String, Object> getVariables() {
		return variables;
	}
	
	public boolean isEmpty() {
		return variables.isEmpty();
	}

}
