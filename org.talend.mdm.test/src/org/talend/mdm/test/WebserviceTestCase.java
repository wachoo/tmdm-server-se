package org.talend.mdm.test;


import java.rmi.RemoteException;

import org.talend.mdm.test.util.Util;

import talend.mdm.test.MDMTestCase;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSGetUniversePKs;
import urn_com_amalto_xtentis_webservice.WSPing;
import urn_com_amalto_xtentis_webservice.WSPutItem;
import urn_com_amalto_xtentis_webservice.WSString;
import urn_com_amalto_xtentis_webservice.WSUniversePK;
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
   
   public void testGetUniversePks() {
		
		try {
			WSUniversePK[] pks=defaultPort.getUniversePKs(new WSGetUniversePKs(".*"));
			for (int i = 0; i < pks.length; i++) {
				WSUniversePK pk=pks[i];
				System.out.println(pk.getPk());
			}
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
   
   public void testBatchInsert() {
   	
   	for (int i = 0; i < 10000; i++) {
   		
   		String xmlString="<User><id>"+i+"</id><name>Newton"+i+"</name><country>UK</country><state>new</state></User>";
   		WSPutItem wsPutItem=new WSPutItem(
	                   new WSDataClusterPK("User"),
	                   xmlString,
	                   new WSDataModelPK("User"),
	                   new Boolean(false)
	                   );
   		
   		try {
				defaultPort.putItem(wsPutItem);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			System.out.println("Inserted item "+i);
		}
		
	}
	
	//TODO: Add more test case here
    
    

}
