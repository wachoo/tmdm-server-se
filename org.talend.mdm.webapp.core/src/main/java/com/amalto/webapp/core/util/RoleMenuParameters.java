package com.amalto.webapp.core.util;

import java.io.StringReader;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import com.amalto.core.objects.marshalling.MarshallingException;
import com.amalto.core.objects.marshalling.MarshallingFactory;

public class RoleMenuParameters {
    public String getParentID() {
        String parentID = "";
        return parentID;}

    public int getPosition() {
        int position = 0;
        return position;}

    // TODO: change this method signature to do not expose Castor Exception anymore
    public static RoleMenuParameters unmarshalMenuParameters(String parameters) throws ValidationException ,MarshalException{
        try {
            return MarshallingFactory.getInstance().getUnmarshaller(RoleMenuParameters.class).unmarshal(new StringReader(parameters));
        } catch (MarshallingException e) {
            throw new MarshalException(e);
        }
	}

}
