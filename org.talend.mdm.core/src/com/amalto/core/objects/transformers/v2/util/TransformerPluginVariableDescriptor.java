package com.amalto.core.objects.transformers.v2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TransformerPluginVariableDescriptor {
	private String variableName;
	private HashMap<String,String> descriptions= new HashMap<String, String>();
	private ArrayList<Pattern> contentTypesRegex;
	private boolean mandatory=false;
	private ArrayList<Pattern> possibleValuesRegex = null;
	
	public ArrayList<Pattern> getContentTypesRegex() {
		return contentTypesRegex;
	}
	public void setContentTypesRegex(ArrayList<Pattern> contentTypes) {
		this.contentTypesRegex = contentTypes;
	}
	public HashMap<String, String> getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(HashMap<String, String> descriptions) {
		this.descriptions = descriptions;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String name) {
		this.variableName = name;
	}
	public ArrayList<Pattern> getPossibleValuesRegex() {
		return possibleValuesRegex;
	}
	public void setPossibleValuesRegex(ArrayList<Pattern> possibleValues) {
		this.possibleValuesRegex = possibleValues;
	}

	
	
	
}
