package org.talend.mdm.ext.publish.test;

import java.io.IOException;

import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;

public class ResourceClientMain {
	
	// Handle it using an HTTP client connector
	private static final Client client = new Client(Protocol.HTTP);
	private static final String dmuri="http://localhost:8080/pubcomponent/secure/customTypesSets";
	private static final String picuri="http://localhost:8080/pubcomponent/secure/pictures";
	private static final ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,"admin", "talend"); 
    
	    

    public static void main(String[] args) throws IOException {
    	     
        //list
        //listDomainObject();
    	listPictures();
    	
        // Create
        //createDomainObject("TestDO1","<Test>Hello</Test>");

        //get
        //getDomainObject("TestDO1");
    }
    
    public static void getDomainObject(String domainObjectName) {
    	Request request = new Request(Method.GET,dmuri+"/"+domainObjectName); 
    	request.setChallengeResponse(authentication);
    	
    	//request and print response
        final Response response = client.handle(request);

        final Representation output = response.getEntity();
        try {
			output.write(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void listDomainObject() {
    	
    	Request request = new Request(Method.GET,dmuri); 
    	request.setChallengeResponse(authentication);
    	
    	//request and print response
        final Response response = client.handle(request);

        final Representation output = response.getEntity();
        try {
			output.write(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}   	
    }

    
    public static void createDomainObject(String name,String content) {
        // Gathering informations into a Web form.
        Form form = new Form();
        form.add("domainObjectName", name);
        form.add("domainObjectContent", content);
        Representation rep = form.getWebRepresentation();
        
        Request request = new Request(Method.POST,dmuri,rep); 
        request.setChallengeResponse(authentication);
        
        final Response response = client.handle(request);

        final Representation output = response.getEntity();
        try {
			output.write(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void listPictures() {
    	
    	Request request = new Request(Method.GET,picuri); 
    	request.setChallengeResponse(authentication);
    	
    	//request and print response
        final Response response = client.handle(request);

        final Representation output = response.getEntity();
        try {
			output.write(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}   	
    }

   

}
