package org.talend.mdm.ext.publish.resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.talend.mdm.ext.publish.util.DAOFactory;
import org.talend.mdm.ext.publish.util.DomainObjectsDAO;

import com.amalto.core.util.XtentisException;

/**
 * Resource which has only one representation.
 * 
 */
public class CustomTypesSetsResource extends BaseResource {
	
	DomainObjectsDAO domainObjectsDAO = null;
	List<String> namesList = null;
	
    public CustomTypesSetsResource(Context context, Request request,Response response) {
    	
        super(context, request, response);
        // Allow modifications of this resource via POST requests.
        setModifiable(true);
        
        domainObjectsDAO=DAOFactory.getUniqueInstance().getDomainObjectDAO();
        
        //get resource
        namesList=new ArrayList<String>();
		try {
			String[] names = domainObjectsDAO.getAllPKs();
			if(names!=null&&names.length>0)namesList=Arrays.asList(names);
		} catch (XtentisException e1) {
			e1.printStackTrace();
		}
            
    }
    
    
    /**
     * Handle POST requests: create a new item.
     */
    @Override
    public void acceptRepresentation(Representation entity)
            throws ResourceException {

        Form form = new Form(entity);
        String domainObjectName = form.getFirstValue("domainObjectName");
        String domainObjectContent = form.getFirstValue("domainObjectContent");//TODO CHANGE TO FILE

        // Check that the domainObject is not already registered.
	    if (namesList.contains(domainObjectName)) {
	         generateErrorRepresentation("The Domain Object name " + domainObjectName + " already exists.", "1", getResponse());
	    } else {
            // Register the new domainObject
	    	domainObjectsDAO.putResource(domainObjectName, domainObjectContent);

            // Set the response's status and entity
            getResponse().setStatus(Status.SUCCESS_CREATED);
            Representation rep = new StringRepresentation("Domain Object created",MediaType.TEXT_PLAIN);
            // Indicates where is located the new resource.
            rep.setIdentifier(getRequest().getResourceRef().getIdentifier()+ "/" + domainObjectName);
            getResponse().setEntity(rep);
        }
    }

	@Override
	protected Representation getResourceRepresent(Variant variant)throws ResourceException {
		
		// Generate the right representation according to its media type.
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
	        return generateListRepresentation(namesList);
	    }
        return null;
	}

    
}
