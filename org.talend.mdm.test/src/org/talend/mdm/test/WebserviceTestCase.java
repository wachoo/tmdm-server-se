package org.talend.mdm.test;


import java.rmi.RemoteException;

import org.talend.mdm.test.util.Util;

import talend.mdm.test.MDMTestCase;
import urn_com_amalto_xtentis_webservice.WSPing;
import urn_com_amalto_xtentis_webservice.WSString;
import urn_com_amalto_xtentis_webservice.WSWorkflowGetProcessDefinitions;
import urn_com_amalto_xtentis_webservice.WSWorkflowProcessDefinitionUUID;
import urn_com_amalto_xtentis_webservice.XtentisPort;



public class WebserviceTestCase extends MDMTestCase{
	
	private static final String URL="http://localhost:8080/talend/TalendPort";
	private static final String User="admin";
	private static final String PASSWORD="talend";
	private static final String Universe="";

	private static XtentisPort defaultPort=null;
	static{
		try {
			defaultPort=(XtentisPort) Util.getPort(URL,Universe, User, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testPing() {
		
		String msg="Hello MDM";
		try {
			WSString echo=defaultPort.ping(new WSPing(msg));
			assertEquals(msg, echo.getValue());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
    public void testWorkflowGetProcessDefinitions() {
		

		try {
			WSWorkflowProcessDefinitionUUID[] processes=defaultPort.workflowGetProcessDefinitions(new WSWorkflowGetProcessDefinitions(".*"));
			for (int i = 0; i < processes.length; i++) {
				System.out.print(processes[i].getProcessName());
				System.out.print(",");
				System.out.print(processes[i].getProcessVersion());
				System.out.println();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	//TODO: Add more test case here

}
