/**
 * 
 */
package com.amalto.core.objects.synchronization.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.Stub;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amalto.core.webservice.WSBoolean;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSDeleteSynchronizationItem;
import com.amalto.core.webservice.WSExistsSynchronizationItem;
import com.amalto.core.webservice.WSGetSynchronizationItem;
import com.amalto.core.webservice.WSGetSynchronizationItemPKs;
import com.amalto.core.webservice.WSItemPK;
import com.amalto.core.webservice.WSPing;
import com.amalto.core.webservice.WSPutSynchronizationItem;
import com.amalto.core.webservice.WSSynchronizationItem;
import com.amalto.core.webservice.WSSynchronizationItemPK;
import com.amalto.core.webservice.WSSynchronizationItemRemoteInstances;
import com.amalto.core.webservice.WSSynchronizationItemStatus;
import com.amalto.core.webservice.XtentisPort;

/**
 * @author bgrieder
 *
 */
public class SynchronizationItemTest {

	WSSynchronizationItemPK[] pks = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//set-up
        try {
	        pks = addTestSynchronizationItems();
        } catch (RemoteException e) {
	        e.printStackTrace();
	        fail("Could not add test items: "+e.getMessage());
        }
        System.out.println("TEST Set-up: added two test items");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		//clean up
		try {
	        getWSPort().deleteSynchronizationItem(new WSDeleteSynchronizationItem(pks[0]));
	        getWSPort().deleteSynchronizationItem(new WSDeleteSynchronizationItem(pks[1]));
        } catch (RemoteException e) {
        }
        System.out.println("TEST Clean-up: removed two test tems");
	}
	
	
	/**
	 * Test method for {@link com.amalto.core.objects.synchronization.ejb.SynchronizationItemCtrlBean#putSynchronizationItem(com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO)}.
	 */
	@Test
	public void testPutSynchronizationItem() {

		assertTrue("Two items were added", pks.length == 2);
		assertTrue("First Item OK", pks[0].getIds()[0].equals("") && pks[0].getIds()[1].equals("TEST.Test.HEAD.MANUAL"));
		assertTrue("Second Item OK", pks[1].getIds()[0].equals("LocalRevisionID") && pks[1].getIds()[1].equals("TEST.Test.REV1.RESOLVED"));
		
	}

	/**
	 * Test method for {@link com.amalto.core.objects.synchronization.ejb.SynchronizationItemCtrlBean#getSynchronizationItem(com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK)}.
	 */
	@Test
	public void testGetSynchronizationItem() {
		
        try {
	        
        	WSSynchronizationItem wsItem1 = getWSPort().getSynchronizationItem(new WSGetSynchronizationItem(pks[0]));
	        assertTrue("Get Item 1 ", wsItem1.getLocalRevisionID().equals("") && wsItem1.getWsItemPK().getIds()[0].equals("HEAD") && wsItem1.getWsItemPK().getIds()[1].equals("MANUAL"));
	        
	        WSSynchronizationItem wsItem2 = getWSPort().getSynchronizationItem(new WSGetSynchronizationItem(pks[1]));
	        assertTrue("Get Item 2 ", wsItem2.getLocalRevisionID().equals("LocalRevisionID") && wsItem2.getWsItemPK().getIds()[0].equals("REV1") && wsItem2.getWsItemPK().getIds()[1].equals("RESOLVED"));
	        
        } catch (RemoteException e) {
	        e.printStackTrace();
	        fail ("testGetSynchronizationItem "+e.getMessage());
        }
        

        
	}

	/**
	 * Test method for {@link com.amalto.core.objects.synchronization.ejb.SynchronizationItemCtrlBean#existsSynchronizationItem(com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK)}.
	 */
	@Test
	public void testExistsSynchronizationItem() {
        try {
	        
        	WSBoolean wsBoolean1 = getWSPort().existsSynchronizationItem(new WSExistsSynchronizationItem(pks[0]));
	        assertTrue("Get Item 1 ", wsBoolean1.is_true());
	        
        	WSBoolean wsBoolean2 = getWSPort().existsSynchronizationItem(new WSExistsSynchronizationItem(pks[1]));
	        assertTrue("Get Item 2 ", wsBoolean2.is_true());
	        
        } catch (RemoteException e) {
	        e.printStackTrace();
	        fail ("testGetSynchronizationItem "+e.getMessage());
        }
        
	}

	/**
	 * Test method for {@link com.amalto.core.objects.synchronization.ejb.SynchronizationItemCtrlBean#removeSynchronizationItem(com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK)}.
	 */
	@Test
	public void testRemoveSynchronizationItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.amalto.core.objects.synchronization.ejb.SynchronizationItemCtrlBean#getSynchronizationItemPKs(java.lang.String)}.
	 */
	@Test
	public void testGetSynchronizationItemPKs() {
		try {
	        WSSynchronizationItemPK[] pks = getWSPort().getSynchronizationItemPKs(
	        	new WSGetSynchronizationItemPKs(".*?TEST\\.Test.*")
	        ).getWsSynchronizationItemPK();
	        assertNotNull(pks);
	        
	        assertTrue("At least two items were added", pks.length >= 2);
			assertTrue("First Item OK", pks[0].getIds()[0].equals("") && pks[0].getIds()[1].equals("TEST.Test.HEAD.MANUAL"));
			assertTrue("Second Item OK", pks[1].getIds()[0].equals("LocalRevisionID") && pks[1].getIds()[1].equals("TEST.Test.REV1.RESOLVED"));
	        
        } catch (RemoteException e) {
        	e.printStackTrace();
	        fail("testGetSynchronizationItemPKs: "+e.getMessage());
        }
	}

	/**
	 * Test method for {@link com.amalto.core.objects.synchronization.ejb.SynchronizationItemCtrlBean#resolveSynchronization(com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK, java.lang.String)}.
	 */
	@Test
	public void testResolveSynchronization() {
		fail("Not yet implemented");
	}
	
	
	
	/**
	 * Adds two test items
	 * @return
	 * 		The {@link WSSynchronizationItemPK}s
	 * @throws RemoteException
	 */
	private WSSynchronizationItemPK[] addTestSynchronizationItems() throws RemoteException {
		
		WSSynchronizationItemPK pk1 = getWSPort().putSynchronizationItem(new WSPutSynchronizationItem(new WSSynchronizationItem(
			new WSItemPK(new WSDataClusterPK("TEST"),"Test",new String[] {"HEAD","MANUAL"}),
			null,
			"TestPlan",
			WSSynchronizationItemStatus.MANUAL,
			null,
			new WSSynchronizationItemRemoteInstances[] {
				new WSSynchronizationItemRemoteInstances(
					"Remote System 1",
					"Revision 1",
					"<Test><id1>HEAD</id1><id2>MANUAL</id2><content>blah</content></Test>",
					Calendar.getInstance()
				),
				new WSSynchronizationItemRemoteInstances(
					"Remote System 2",
					"Revision a",
					"<Test><id1>HEAD</id1><id2>MANUAL</id2><content>blah 2</content></Test>",
					Calendar.getInstance()
				)
			}
		)));
		
		
		WSSynchronizationItemPK pk2 = getWSPort().putSynchronizationItem(new WSPutSynchronizationItem(new WSSynchronizationItem(
			new WSItemPK(new WSDataClusterPK("TEST"),"Test",new String[] {"REV1","RESOLVED"}),
			"LocalRevisionID",
			"OtherTestPlan",
			WSSynchronizationItemStatus.RESOLVED,
			"<Test><id1>REV1</id1><id2>RESOLVED</id2><content>Resolved Content</content></Test>",
			new WSSynchronizationItemRemoteInstances[] {
				new WSSynchronizationItemRemoteInstances(
					"Remote System 1",
					"Revision 1",
					"<Test><id1>REV1</id1><id2>RESOLVED</id2><content>resolved blah</content></Test>",
					Calendar.getInstance()
				),
				new WSSynchronizationItemRemoteInstances(
					"Remote System 2",
					"Revision a",
					"<Test><id1>REV1</id1><id2>RESOLVED</id2><content>resolved blah 2</content></Test>",
					Calendar.getInstance()
				)
			}
		)));
		
		return new WSSynchronizationItemPK[] {pk1, pk2};
	}
	
	
	
	private static XtentisPort getWSPort() {
		
		Logger.getLogger("org").setLevel(Level.INFO);
		Logger.getLogger("org.jboss.ws").setLevel(Level.INFO);
		
		String endpointAddress = "http://localhost:8080/talend/TalendPort?WSDL";
		String username = "admin";
		String password = "xtentis"; 
		
		XtentisPort endpoint = null;
		
		try {
            
	        QName serviceQname = new QName("urn-com-amalto-xtentis-webservice", "XtentisService");
	        QName portQname = new QName("urn-com-amalto-xtentis-webservice", "XtentisPort");

	        URL wsdlURL = new URL("file:///Users/bgrieder/Documents/workspace/com.amalto.core/src/META-INF/wsdl/webservices.wsdl");
	        URL mappingURL = new URL("file:///Users/bgrieder/Documents/workspace/com.amalto.core/src/META-INF/mapping.xml");
	        
	        ServiceFactory factory = ServiceFactory.newInstance();
	        ServiceFactoryImpl fact = (ServiceFactoryImpl) factory;
	        
	        Service service = fact.createService(wsdlURL, serviceQname, mappingURL);
	        
	        endpoint = (XtentisPort) service.getPort(portQname, XtentisPort.class);
	        
	        Stub stub = (Stub) endpoint;
	        stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
	        if (username != null) {
	        	stub._setProperty(Stub.USERNAME_PROPERTY, username);
	        	stub._setProperty(Stub.PASSWORD_PROPERTY, password);
	        }
	        
	        assertNotSame("Webservices active" , "toto",endpoint.ping(new WSPing("toto")).getValue());
	        
	        
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail("Unable to get Port");
    		System.exit(-1);
    	}
		
    	return endpoint;
		
		
	}


}
