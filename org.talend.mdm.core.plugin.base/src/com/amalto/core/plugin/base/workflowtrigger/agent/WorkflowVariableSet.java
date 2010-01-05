package com.amalto.core.plugin.base.workflowtrigger.agent;

public class WorkflowVariableSet {
	
	public static final String STRING_TYPE="String";
	public static final String BOOLEAN_TYPE="Boolean";
	public static final String FLOAT_TYPE="Float";
	public static final String INTEGER_TYPE="Integer";
	
	protected Object parseType(String variableValue, String variableType) {
		Object toSetVariableValue=null;
		
		//TODO support date here
		if(variableType.equals(STRING_TYPE)){
			if(variableValue==null)variableValue="";//default
			toSetVariableValue=variableValue;
		}else if(variableType.equals(BOOLEAN_TYPE)){
			if(variableValue==null)variableValue="false";//default
			toSetVariableValue = variableValue.equals("true");
		}else if(variableType.equals(FLOAT_TYPE)){
			if(variableValue==null)variableValue="0.0";//default
			toSetVariableValue=Float.valueOf(variableValue);
		}else if(variableType.equals(INTEGER_TYPE)){
			if(variableValue==null)variableValue="0";//default
			toSetVariableValue=Integer.valueOf(variableValue);
		}else{
			if(variableValue==null)variableValue="";//default
			toSetVariableValue=variableValue;
		}
		
		return toSetVariableValue;
	}

}
