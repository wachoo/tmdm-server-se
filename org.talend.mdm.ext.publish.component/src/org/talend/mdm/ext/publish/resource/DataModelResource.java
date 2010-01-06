package org.talend.mdm.ext.publish.resource;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.xml.sax.SAXException;

import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.util.Util;

/**
 * Resource which has only one representation.
 * 
 */
public class DataModelResource extends BaseResource {
	
	String dataModelName;
	DataModelPOJO dataModelPOJO=null;
	
    public DataModelResource(Context context, Request request,
            Response response) {
        super(context, request, response);
        // Get the "dataModelName" attribute value taken from the URI template
        // /dataModels/{dataModelName}.
        this.dataModelName = getAttributeInUrl("dataModelName");
        //System.out.println(this.dataModelName);
        this.dataModelPOJO=getDataModel(dataModelName);
		
    }

    @Override
    protected Representation getResourceRepresent(Variant variant)throws ResourceException{
    	// Generate the right representation according to its media type.
        if (MediaType.TEXT_XML.equals(variant.getMediaType())&&dataModelPOJO!=null) {
	    	DomRepresentation representation=null;
			try {
				representation = new DomRepresentation(
				        MediaType.TEXT_XML,Util.parse(dataModelPOJO.getSchema()));
				representation.getDocument().normalize();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			
	        return representation;
	        }
        return null;
    }
        
}
