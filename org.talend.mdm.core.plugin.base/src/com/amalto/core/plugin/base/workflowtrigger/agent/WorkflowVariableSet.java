package com.amalto.core.plugin.base.workflowtrigger.agent;

public class WorkflowVariableSet {
	
	protected Object parseType(String variableValue, String variableType) {
		Object toSetVariableValue=null;
		
		//TODO support date here
		if(variableType.equals("String")){
			if(variableValue==null)variableValue="";//default
			toSetVariableValue=variableValue;
		}else if(variableType.equals("Boolean")){
			if(variableValue==null)variableValue="false";//default
			toSetVariableValue = variableValue.equals("true");
		}else if(variableType.equals("Float")){
			if(variableValue==null)variableValue="0.0";//default
			toSetVariableValue=Float.valueOf(variableValue);
		}else if(variableType.equals("Integer")){
			if(variableValue==null)variableValue="0";//default
			toSetVariableValue=Integer.valueOf(variableValue);
		}else{
			if(variableValue==null)variableValue="";//default
			toSetVariableValue=variableValue;
		}
		
		return toSetVariableValue;
	}

}
