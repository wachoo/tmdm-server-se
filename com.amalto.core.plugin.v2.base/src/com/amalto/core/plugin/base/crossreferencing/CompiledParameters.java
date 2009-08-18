package com.amalto.core.plugin.base.crossreferencing;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;


public class CompiledParameters implements Serializable {
	private static final long serialVersionUID = 2908739898798787L;
	
	private String parameters;
	
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String xml) {
		this.parameters = xml;
	}
	
	
	public String serialize() throws IOException{
		return parameters;
	}
	
	public static CompiledParameters deserialize(String xml) 
		throws IOException,
						ClassNotFoundException,
						TransformerException,
						ParserConfigurationException,
						SAXException
	{
		CompiledParameters parameters = new CompiledParameters();
		parameters.setParameters(xml);
	
		return parameters;
	}
	
}
