package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSComponent;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSDroppedItem;
import urn_com_amalto_xtentis_webservice.WSDroppedItemPK;
import urn_com_amalto_xtentis_webservice.WSFindAllDroppedItemsPKs;
import urn_com_amalto_xtentis_webservice.WSGetComponentVersion;
import urn_com_amalto_xtentis_webservice.WSInitData;
import urn_com_amalto_xtentis_webservice.WSInt;
import urn_com_amalto_xtentis_webservice.WSItemPK;
import urn_com_amalto_xtentis_webservice.WSLoadDroppedItem;
import urn_com_amalto_xtentis_webservice.WSLogout;
import urn_com_amalto_xtentis_webservice.WSMDMConfig;
import urn_com_amalto_xtentis_webservice.WSPing;
import urn_com_amalto_xtentis_webservice.WSPutItem;
import urn_com_amalto_xtentis_webservice.WSRecoverDroppedItem;
import urn_com_amalto_xtentis_webservice.WSRemoveDroppedItem;
import urn_com_amalto_xtentis_webservice.WSRunQuery;
import urn_com_amalto_xtentis_webservice.WSString;
import urn_com_amalto_xtentis_webservice.WSVersion;
import urn_com_amalto_xtentis_webservice.WSWorkflowGetProcessDefinitions;
import urn_com_amalto_xtentis_webservice.WSWorkflowProcessDefinitionUUID;

public class CommonWebserviceTestCase extends WebserviceTestCase {

	public void testPing() {

		String msg = "Hello MDM";
		try {
			WSString echo = defaultPort.ping(new WSPing(msg));
			assertEquals(msg, echo.getValue());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testLogout() {
		WSString flag = null;
		WSLogout logout = new WSLogout();
		try {
			flag = defaultPort.logout(logout);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println(Boolean.valueOf(flag.getValue()));

	}

	public void testInitMDM() {
		WSInitData initData = new WSInitData();
		initData.setZap(true);
		String xmlSchema = "";
		initData.setXmlSchema(xmlSchema);
		WSInt wsInt = null;
		try {
			wsInt = defaultPort.initMDM(initData);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println(wsInt.getValue());

	}

	public void testGetMDMConfiguration() {
		try {
			WSMDMConfig config = defaultPort.getMDMConfiguration();
			assertNotNull(config);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testRunQuery() {
		WSRunQuery wsRunQuery = new WSRunQuery();
		wsRunQuery.setWsDataClusterPK(new WSDataClusterPK("Order"));
		wsRunQuery
				.setQuery("for $pivot0 in collection('Order')/ii/p/Student/id order by $pivot0/../age ascending return $pivot0");
		try {
			String[] returnValues = defaultPort.runQuery(wsRunQuery);
			for (int i = 0; i < returnValues.length; i++) {
				System.out.println(returnValues[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testWorkflowGetProcessDefinitions() {

		try {
			WSWorkflowProcessDefinitionUUID[] processes = defaultPort
					.workflowGetProcessDefinitions(new WSWorkflowGetProcessDefinitions(
							".*"));
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

	public void testGetComponentVersion() {
		WSGetComponentVersion wsGetComponentVersion = new WSGetComponentVersion();
		WSComponent component = WSComponent.DataManager;
		wsGetComponentVersion.setComponent(component);
		try {
			WSVersion version = defaultPort
					.getComponentVersion(wsGetComponentVersion);
			assertNotNull(version);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testFindAllDroppedItemsPKs() {
		WSFindAllDroppedItemsPKs regex = new WSFindAllDroppedItemsPKs();
		regex.setRegex("*");
		try {
			WSDroppedItemPK[] wsDroppedItemPKArray = defaultPort
					.findAllDroppedItemsPKs(regex);
			for (int i = 0; i < wsDroppedItemPKArray.length; i++) {
				System.out.println(wsDroppedItemPKArray[i].getPartPath());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testLoadDroppedItem() {
		try {
			WSFindAllDroppedItemsPKs regex = new WSFindAllDroppedItemsPKs();
			regex.setRegex("*");

			WSDroppedItemPK[] wsDroppedItemPKArray = defaultPort
					.findAllDroppedItemsPKs(regex);

			WSLoadDroppedItem wsLoadDroppedItem = new WSLoadDroppedItem();
			wsLoadDroppedItem.setWsDroppedItemPK(wsDroppedItemPKArray[0]);
			WSDroppedItem wsDroppedItem = defaultPort
					.loadDroppedItem(wsLoadDroppedItem);
			assertNotNull(wsDroppedItem);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testRecoverDroppedItem() {
		try {
			WSRecoverDroppedItem wsRecoverDroppedItem = new WSRecoverDroppedItem();
			WSFindAllDroppedItemsPKs regex = new WSFindAllDroppedItemsPKs();
			regex.setRegex("*");

			WSDroppedItemPK[] wsDroppedItemPKArray = defaultPort
					.findAllDroppedItemsPKs(regex);

			wsRecoverDroppedItem.setWsDroppedItemPK(wsDroppedItemPKArray[0]);
			WSItemPK wsItemPK = defaultPort
					.recoverDroppedItem(wsRecoverDroppedItem);
			System.out.println(wsItemPK.getConceptName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testRemoveDroppedItem() {
		try {
			WSRemoveDroppedItem wsRemoveDroppedItem = new WSRemoveDroppedItem();
			WSFindAllDroppedItemsPKs regex = new WSFindAllDroppedItemsPKs();
			regex.setRegex(".*");
			WSDroppedItemPK[] wsDroppedItemPKArray = defaultPort
					.findAllDroppedItemsPKs(regex);
			wsRemoveDroppedItem.setWsDroppedItemPK(wsDroppedItemPKArray[0]);
			WSDroppedItemPK wsDroppedItemPK = defaultPort
					.removeDroppedItem(wsRemoveDroppedItem);
			System.out.println(wsDroppedItemPK.getPartPath());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testBatchInsert() {

		for (int i = 0; i < 10000; i++) {

			String xmlString = "<User><id>" + i + "</id><name>Newton" + i
					+ "</name><country>UK</country><state>new</state></User>";
			WSPutItem wsPutItem = new WSPutItem(new WSDataClusterPK("User"),
					xmlString, new WSDataModelPK("User"), new Boolean(false));

			try {
				defaultPort.putItem(wsPutItem);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			System.out.println("Inserted item " + i);
		}

	}
}
