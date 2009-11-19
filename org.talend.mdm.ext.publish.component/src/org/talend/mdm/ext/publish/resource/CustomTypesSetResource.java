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
import org.talend.mdm.ext.publish.util.DAOFactory;
import org.talend.mdm.ext.publish.util.DomainObjectsDAO;
import org.xml.sax.SAXException;

import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


public class CustomTypesSetResource extends BaseResource {
	
	String customTypesSetName;
	DomainObjectsDAO domainObjectsDAO = null;
	
    public CustomTypesSetResource(Context context, Request request,Response response) {
        super(context, request, response);

        this.customTypesSetName = getAttributeInUrl("customTypesSetName");
        this.domainObjectsDAO=DAOFactory.getUniqueInstance().getDomainObjectDAO();
		
    }

    @Override
    protected Representation getResourceRepresent(Variant variant)throws ResourceException{
    	// Generate the right representation according to its media type.
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
	    	DomRepresentation representation=null;
			try {
				representation = new DomRepresentation(MediaType.TEXT_XML,Util.parse(domainObjectsDAO.getResource(customTypesSetName)));
				representation.getDocument().normalize();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (XtentisException e) {
				e.printStackTrace();
			}
			
	        return representation;
	        }
        return null;
    }
        
}
