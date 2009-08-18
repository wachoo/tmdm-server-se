package com.amalto.workbench.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashSet;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

public class RoleMenuParameters {
	private String parentID="";
	private int position=0;
	public String getParentID() {return parentID;}
	public void setParentID(String parentID) {this.parentID = parentID;}
	public int getPosition() {return position;}
	public void setPosition(int position) {this.position = position;}
	
	public static RoleMenuParameters unmarshalMenuParameters(
			LinkedHashSet<String> parameters
	) throws ValidationException ,MarshalException{
		return
			(RoleMenuParameters)Unmarshaller.unmarshal(
				RoleMenuParameters.class, 
				new InputSource(
						new StringReader(parameters.iterator().next())
				)
			);
	}
	
	public LinkedHashSet<String> marshalMenuParameters() {
		LinkedHashSet<String> parameters = new LinkedHashSet<String>();
		try {
			StringWriter sw = new StringWriter();
			Marshaller.marshal(this, sw);
			parameters.add(sw.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parameters;
	}
}
