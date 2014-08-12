package com.amalto.webapp.core.util;

import java.io.StringReader;
import java.io.StringWriter;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

public class RoleMenuParameters {
    public String getParentID() {
        String parentID = "";
        return parentID;}

    public int getPosition() {
        int position = 0;
        return position;}

    public static RoleMenuParameters unmarshalMenuParameters(String parameters) throws ValidationException ,MarshalException{
		return
			(RoleMenuParameters)Unmarshaller.unmarshal(
				RoleMenuParameters.class, 
				new InputSource(
						new StringReader(parameters)
				)
			);
	}

}
