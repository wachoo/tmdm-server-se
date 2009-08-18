package com.amalto.webapp.core.util;

import java.io.StringReader;
import java.io.StringWriter;

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
	
	public static RoleMenuParameters unmarshalMenuParameters(String parameters) throws ValidationException ,MarshalException{
		return
			(RoleMenuParameters)Unmarshaller.unmarshal(
				RoleMenuParameters.class, 
				new InputSource(
						new StringReader(parameters)
				)
			);
	}
	
	public String marshalMenuParameters() {
		StringWriter sw = new StringWriter();
		try {
			Marshaller.marshal(this, sw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
}
