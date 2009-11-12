package org.talend.mdm.test.util;

import urn_com_amalto_xtentis_webservice.XtentisBindingStub;
import urn_com_amalto_xtentis_webservice.XtentisPort;
import urn_com_amalto_xtentis_webservice.XtentisServiceLocator;


public class Util {
	
	public static XtentisPort getPort(String URL, String Universe, String User, String PASSWORD) throws Exception{		
        System.out.println("Getting Port... ");
		// Init Web Service calling
		XtentisServiceLocator xtentisService = new XtentisServiceLocator();
		xtentisService.setXtentisPortEndpointAddress(URL);
		XtentisPort xtentisWS = xtentisService.getXtentisPort();    
		XtentisBindingStub stub = (XtentisBindingStub)xtentisWS;

		// Authentification
		if(Universe == null || Universe.trim().length()==0 )
			stub.setUsername(User);
		else
			stub.setUsername(Universe +"/" +User);
		stub.setPassword(PASSWORD);
		
		return stub;
	}

}
