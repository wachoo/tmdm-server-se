package org.talend.mdm.ext.publish.resource;
import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.talend.mdm.ext.publish.util.DAOFactory;
import org.talend.mdm.ext.publish.util.PicturesDAO;

import com.amalto.core.util.XtentisException;

/**
 * Resource which has only one representation.
 * 
 */
public class PicturesResource extends BaseResource {
	
	PicturesDAO picturesDAO = null;
	Map<String,String> nameURLMap = null;
	
    public PicturesResource(Context context, Request request,Response response) {
    	
        super(context, request, response);
        
        picturesDAO=DAOFactory.getUniqueInstance().getPicturesDAO();
        
        //get resource
        nameURLMap=new HashMap<String,String>();
		try {
			String[] pks = picturesDAO.getAllPKs();
			if(pks!=null&&pks.length>0){
				for (int i = 0; i < pks.length; i++) {
					String pk=pks[i];
					String uri = parsePath(pk);
					nameURLMap.put(pk, uri);
				}
			}
		} catch (XtentisException e1) {
			e1.printStackTrace();
		}
            
    }

	private String parsePath(String pk) {
		String path="/imageserver/upload";
		if(pk.indexOf("-")==-1)pk="-"+pk;
		String[] pkParts=pk.split("-");
		String catalog=pkParts[0];
		String file=pkParts[1];
		if(!catalog.equals("")&&!catalog.equals("/")&&!catalog.equals("//"))path=path+"/"+catalog;
		path=path+"/"+file;
		return path;
	}
    
	@Override
	protected Representation getResourceRepresent(Variant variant)throws ResourceException {
		
		// Generate the right representation according to its media type.
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
	        return generateMapRepresentation(nameURLMap);
	    }
        return null;
	}
	
    
}
