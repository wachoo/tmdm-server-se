package com.amalto.core.objects.transformers.util;

import java.io.Serializable;


public class TransformerVariablesMapping implements Serializable {
	String pipelineVariable;
	String pluginVariable;
	TypedContent hardCoding;
	
	public TypedContent getHardCoding() {
		return hardCoding;
	}
	public void setHardCoding(TypedContent hardCoding) {
		this.hardCoding = hardCoding;
	}
	public String getPipelineVariable() {
		return pipelineVariable;
	}
	public void setPipelineVariable(String pipelineVariable) {
		this.pipelineVariable = pipelineVariable;
	}
	public String getPluginVariable() {
		return pluginVariable;
	}
	public void setPluginVariable(String pluginVariable) {
		this.pluginVariable = pluginVariable;
	}
	
	
}
