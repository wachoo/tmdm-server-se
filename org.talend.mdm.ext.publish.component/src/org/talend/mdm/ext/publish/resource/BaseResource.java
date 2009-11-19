package org.talend.mdm.ext.publish.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

/**
 * Base resource class that supports common behaviours or attributes shared by
 * all resources.
 * 
 */
public abstract class BaseResource extends Resource {

	public BaseResource(Context context, Request request, Response response) {
		super(context, request, response);
		
		// This representation has only one type of representation.
        getVariants().add(new Variant(MediaType.TEXT_XML));
	}
	
	/**
     * Returns a full representation for a given variant.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException{
    	Representation resourceRepresent=getResourceRepresent(variant);
    	
    	//set characterSet
    	if(resourceRepresent!=null)resourceRepresent.setCharacterSet(CharacterSet.UTF_8);
    	return resourceRepresent;
    }
    
    protected abstract Representation getResourceRepresent(Variant variant) throws ResourceException;
		

	protected String getAttributeInUrl(String attributeKey) {
		return getAttributeInUrl(attributeKey, true);
	}

	protected String getAttributeInUrl(String attributeKey, boolean decode) {
		
		String attribute = null;
		Object getted = getRequest().getAttributes().get(attributeKey);
		if (getted != null) {
			attribute = (String) getted;
		}
		if (attribute != null && decode) {
			try {
				attribute = URLDecoder.decode(attribute, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				org.apache.log4j.Logger
						.getLogger(this.getClass())
						.warn(
								"Unable to decode the string with the UTF-8 character set.",
								uee);
			}
		}
		
		return attribute;

	}

	/**
	 * Generate an XML representation of an error response.
	 * 
	 * @param errorMessage
	 *            the error message.
	 * @param errorCode
	 *            the error code.
	 */
	protected void generateErrorRepresentation(String errorMessage,
			String errorCode, Response response) {
		generateErrorRepresentation(errorMessage, errorCode, response,
				Status.CLIENT_ERROR_NOT_FOUND);
	}

	protected void generateErrorRepresentation(String errorMessage,
			String errorCode, Response response, Status status) {
		// This is an error
		response.setStatus(status);
		// Generate the output representation
		try {
			DomRepresentation representation = new DomRepresentation(
					MediaType.TEXT_XML);
			// Generate a DOM document representing the list of
			Document d = representation.getDocument();

			Element eltError = d.createElement("error");
			d.appendChild(eltError);

			Element eltCode = d.createElement("code");
			eltCode.appendChild(d.createTextNode(errorCode));
			eltError.appendChild(eltCode);

			Element eltMessage = d.createElement("message");
			eltMessage.appendChild(d.createTextNode(errorMessage));
			eltError.appendChild(eltMessage);

			response.setEntity(representation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected Representation generateListRepresentation(List<String> inputList) {
		
		DomRepresentation representation=null;
		
		try {	
			representation = new DomRepresentation(MediaType.TEXT_XML);
			Document d = representation.getDocument();

			Element listElement = d.createElement("list");
			d.appendChild(listElement);

			if(inputList!=null){
				for (Iterator<String> iterator = inputList.iterator(); iterator.hasNext();) {
					String entry =  iterator.next();
					if(entry!=null){
						Element entryElement = d.createElement("entry");
						entryElement.appendChild(d.createTextNode(entry));
						listElement.appendChild(entryElement);
					}
				}
			}
			
			d.normalize();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return representation;
	}
	
    protected Representation generateMapRepresentation(Map<String,String> inputMap) {
		
		DomRepresentation representation=null;
		
		try {	
			representation = new DomRepresentation(MediaType.TEXT_XML);
			Document d = representation.getDocument();

			Element listElement = d.createElement("list");
			d.appendChild(listElement);

			if(inputMap!=null){
				
				for (Iterator<String> iterator = inputMap.keySet().iterator(); iterator.hasNext();) {
					String entryName =  iterator.next();
					
					Element entryElement = d.createElement("entry");
					listElement.appendChild(entryElement);
					
					Element entryNameElement = d.createElement("name");
					entryNameElement.appendChild(d.createTextNode(entryName));
					entryElement.appendChild(entryNameElement);
					
					Element entryUriElement = d.createElement("uri");
					entryUriElement.appendChild(d.createTextNode(inputMap.get(entryName)));
					entryElement.appendChild(entryUriElement);
				}
			}
			
			d.normalize();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return representation;
	}

	/**
	 * @param dataModelName
	 * @return
	 */
	protected DataModelPOJO getDataModel(String dataModelName) {
		DataModelPOJO dataModelPOJO = null;
		try {
			// use local bean without security check
			dataModelPOJO = Util.getDataModelCtrlLocal().getDataModel(
					new DataModelPOJOPK(dataModelName));
		} catch (XtentisException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		return dataModelPOJO;
	}

}
