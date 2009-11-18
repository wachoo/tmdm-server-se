package com.amalto.core.plugin.base.workflowtrigger.agent;

public class WorkflowVariableSet {
	
	protected Object parseType(String variableValue, String variableType) {
		Object toSetVariableValue=null;
		
		//TODO support date here
		if(variableType.equals("String")){
			toSetVariableValue=variableValue;
		}else if(variableType.equals("Boolean")){
			toSetVariableValue = variableValue.equals("true");
		}else if(variableType.equals("Float")){
			toSetVariableValue=Float.valueOf(variableValue);
		}else if(variableType.equals("Integer")){
			toSetVariableValue=Integer.valueOf(variableValue);
		}else{
			toSetVariableValue=variableValue;
		}
		
		return toSetVariableValue;
	}

}
