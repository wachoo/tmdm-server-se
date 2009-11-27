package org.talend.mdm.workflow.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

import urn_com_amalto_xtentis_webservice.XtentisPort;
import urn_com_amalto_xtentis_webservice.XtentisService;

public abstract class TalendMDMAdapter {
	
	private static final String DEFAULT_USERNAME="admin";
	private static final String DEFAULT_PASSWORD="talend";
	//connection
	private String url;
	//authentification
	private String username;
	private String password;
	private String universe;
	
	//port
	protected XtentisPort port=null;
	
	public TalendMDMAdapter(String url,String universe) {
		super();
		this.url = url;
		this.universe = universe;
		this.username = DEFAULT_USERNAME;
		this.password = DEFAULT_PASSWORD;
		getPort();
	}
	
	public TalendMDMAdapter(String url, String username, String password, String universe) {
		super();
		this.url = url;
		this.username = username;
		this.password = password;
		this.universe = universe;
		getPort();
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
	
	private void getPort() {
		
		String endpointAddress = url;// Endpoint Address

		URL wsdlLocation = null;
        try {
        	wsdlLocation = new URL(endpointAddress + "?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the WSDL location from URL: " + url + "?wsdl");
            // e.printStackTrace();
        }
		
		XtentisService service = new XtentisService(wsdlLocation);

		port = service.getXtentisPort();

		//authentication
		BindingProvider stub = (BindingProvider) port;
		
		if (universe == null || universe.trim().length() == 0)
			stub.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,username);
		else
			stub.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,universe + "/" + username);

		stub.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,password);
		
	}
	
	

}
