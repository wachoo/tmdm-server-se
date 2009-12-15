package org.talend.mdm.workflow.client;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import urn_com_amalto_xtentis_webservice.XtentisPort;
import urn_com_amalto_xtentis_webservice.XtentisService;

public abstract class TalendMDMAdapter {
	
	protected static final long TIMEOUT_THRESHOLD = 30*1000;//set request timeout is 30 seconds
	
	protected static final String DEFAULT_USERNAME="admin";
	protected static final String DEFAULT_PASSWORD="talend";
	//connection
	private String url;
	//authentification
	private String username;
	private String password;
	private String universe;
	
	//port
	protected XtentisPort port=null;
	
	public TalendMDMAdapter() {

	}
	
	public TalendMDMAdapter(String url,String universe) {
		super();
		this.url = url;
		this.universe = universe;
		this.username = DEFAULT_USERNAME;
		this.password = DEFAULT_PASSWORD;
		initPort();
	}
	
	public TalendMDMAdapter(String url, String username, String password, String universe) {
		super();
		this.url = url;
		this.username = username;
		this.password = password;
		this.universe = universe;
		initPort();
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUniverse() {
		return universe;
	}
	public void setUniverse(String universe) {
		this.universe = universe;
	}
	
	protected void initPort() {
		
		String endpointAddress = url;// Endpoint Address

		URL wsdlLocation = null;
		
//		//get wsdl from remote
//        try {
//        	wsdlLocation = new URL(endpointAddress + "?wsdl");
//        } catch (MalformedURLException e) {
//            System.err.println("Can not initialize the WSDL location from URL: " + url + "?wsdl");
//        }
		
		//get wsdl from local
		wsdlLocation = getClass().getClassLoader().getResource("wsdl/webservices.wsdl");
		XtentisService service = new XtentisService(wsdlLocation);

		port = service.getXtentisPort();

		BindingProvider stub = (BindingProvider) port;
		
		//dynamic set endpointAddress
		stub.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endpointAddress);
		
		//authentication
		if (universe == null || universe.trim().length() == 0)
			stub.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,username);
		else
			stub.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,universe + "/" + username);

		stub.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,password);
		
		
		// FIXME:cxf java.lang.ClassCastException: org.jboss.ws.core.jaxws.client.ClientProxy cannot be cast to org.apache.cxf.frontend.ClientProxy
		// setting the timeout  
//		Client c = org.apache.cxf.frontend.ClientProxy.getClient(port);  
//		HTTPConduit httpConduit = (HTTPConduit) c.getConduit();  
//		httpConduit.getClient().setReceiveTimeout(TIMEOUT_THRESHOLD);
		
	}
	
	

}
