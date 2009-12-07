package org.talend.mdm.workflow.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginContext;

import junit.framework.TestCase;

import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.SimpleCallbackHandler;
import org.talend.mdm.workflow.client.TalendMDMItemUpdater;
import org.talend.mdm.workflow.client.TalendMDMUserFinder;

public class ClientTestCase extends TestCase{
	
	private LoginContext loginContext;
	private String login = "user";
	private String password = "user";
	
    protected void setUp() throws Exception {
	    System.setProperty(BonitaConstants.API_TYPE_PROPERTY, "EJB2");
	    System.setProperty(BonitaConstants.INITIAL_CONTEXT_FACTORY_PROPERTY, "org.jnp.interfaces.NamingContextFactory");
	    System.setProperty(BonitaConstants.PROVIDER_URL_PROPERTY, "jnp://localhost:1099");
	    System.setProperty(BonitaConstants.JAAS_PROPERTY, "resources/jaas-jboss.cfg");
	    this.loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(login, password));
	    this.loginContext.login();
    }
		
	public  void testUpdateItem() {
		  //mock
		  String url="http://localhost:8080/talend/TalendPort";//get from variable named 'MDM_url', passed by MDM Trigger
		  String universe="";//get from variable named 'MDM_universe', passed by MDM Trigger
		  
		  String dataCluster="Order";//get from variable named 'MDM_dataCluster', passed by MDM Trigger
		  String dataModel="Order";//get from variable named 'MDM_dataModel', passed by MDM Trigger
		  
		  Map<String,Object> itemUpdateMap= new HashMap<String, Object>(); 
		  //value get from variable, provided by MDM Trigger
		  //xpath get from variable, provided by MDM Trigger, which variable name pattern is {variableName}_xpath
		  itemUpdateMap.put("User/id", "1"); //pk is mandatory                                 
		  itemUpdateMap.put("User/name", "Newton");
		  itemUpdateMap.put("User/country", "UK");
		  itemUpdateMap.put("User/state", "approved");//field which has been changed
		  
		  //call
		  TalendMDMItemUpdater itemUpdater=new TalendMDMItemUpdater(url,universe);
		  itemUpdater.setDataCluster(dataCluster);
		  itemUpdater.setDataModel(dataModel);
		  itemUpdater.setItemUpdateMap(itemUpdateMap);
		  try {
			itemUpdater.update();
		  } catch (Exception e) {
			e.printStackTrace();
		  }

	}
	
	public  void testfindUsers() {
		// mock
		String roleId="Order_User";
		
		String url = "http://localhost:8080/talend/TalendPort";//get from variable named 'MDM_url', passed by MDM Trigger
		String universe = "";//get from variable named 'MDM_universe', passed by MDM Trigger

		// call
		TalendMDMUserFinder userFinder = new TalendMDMUserFinder(url, universe);
		List<String> users = null;
		try {
			users = userFinder.find(roleId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals("user", users.iterator().next());

	}
	
	

}
