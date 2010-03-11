package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSBoolean;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDeleteRoutingOrderV2;
import urn_com_amalto_xtentis_webservice.WSDeleteRoutingRule;
import urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Asynchronously;
import urn_com_amalto_xtentis_webservice.WSExecuteRoutingOrderV2Synchronously;
import urn_com_amalto_xtentis_webservice.WSExistsRoutingOrderV2;
import urn_com_amalto_xtentis_webservice.WSExistsRoutingRule;
import urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2;
import urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2PKsByCriteria;
import urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2SByCriteria;
import urn_com_amalto_xtentis_webservice.WSGetRoutingRule;
import urn_com_amalto_xtentis_webservice.WSGetRoutingRulePKs;
import urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2Details;
import urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2SList;
import urn_com_amalto_xtentis_webservice.WSItemPK;
import urn_com_amalto_xtentis_webservice.WSPutRoutingRule;
import urn_com_amalto_xtentis_webservice.WSRouteItemV2;
import urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Action;
import urn_com_amalto_xtentis_webservice.WSRoutingEngineV2ActionCode;
import urn_com_amalto_xtentis_webservice.WSRoutingEngineV2Status;
import urn_com_amalto_xtentis_webservice.WSRoutingOrderV2;
import urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK;
import urn_com_amalto_xtentis_webservice.WSRoutingOrderV2SearchCriteria;
import urn_com_amalto_xtentis_webservice.WSRoutingOrderV2Status;
import urn_com_amalto_xtentis_webservice.WSRoutingRule;
import urn_com_amalto_xtentis_webservice.WSRoutingRulePK;
import urn_com_amalto_xtentis_webservice.WSString;
import urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem;

public class RoutingWebserviceTestCase extends WebserviceTestCase {

	public void testGetRoutingRulePKs() {
		WSGetRoutingRulePKs regex = new WSGetRoutingRulePKs("*");
		try {
			WSRoutingRulePK[] wsRoutingRulePKArray = defaultPort
					.getRoutingRulePKs(regex);
			for (int i = 0; i < wsRoutingRulePKArray.length; i++) {
				System.out.println(wsRoutingRulePKArray[i].getPk());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetRoutingRule() {
		try {
			WSGetRoutingRule wsRoutingRuleGet = new WSGetRoutingRule();
			WSGetRoutingRulePKs regex = new WSGetRoutingRulePKs("*");
			WSRoutingRulePK[] wsRoutingRulePKArray = defaultPort
					.getRoutingRulePKs(regex);
			wsRoutingRuleGet.setWsRoutingRulePK(wsRoutingRulePKArray[0]);
			WSRoutingRule wsRoutingRule = defaultPort
					.getRoutingRule(wsRoutingRuleGet);
			System.out.println(wsRoutingRule.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExistsRoutingRule() {
		try {
			WSExistsRoutingRule wsExistsRoutingRule = new WSExistsRoutingRule();
			WSGetRoutingRulePKs regex = new WSGetRoutingRulePKs("*");
			WSRoutingRulePK[] wsRoutingRulePKArray = defaultPort
					.getRoutingRulePKs(regex);
			wsExistsRoutingRule.setWsRoutingRulePK(wsRoutingRulePKArray[0]);
			WSBoolean wsBoolean = defaultPort
					.existsRoutingRule(wsExistsRoutingRule);
			System.out.println(wsBoolean.is_true());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutRoutingRule() {
		try {
			WSGetRoutingRule wsRoutingRuleGet = new WSGetRoutingRule();
			WSGetRoutingRulePKs regex = new WSGetRoutingRulePKs("*");
			WSRoutingRulePK[] wsRoutingRulePKArray = defaultPort
					.getRoutingRulePKs(regex);
			wsRoutingRuleGet.setWsRoutingRulePK(wsRoutingRulePKArray[0]);
			WSRoutingRule wsRoutingRule = defaultPort
					.getRoutingRule(wsRoutingRuleGet);
			wsRoutingRule.setName(wsRoutingRule.getName() + "2");
			WSPutRoutingRule wsRoutingPutRule = new WSPutRoutingRule();
			wsRoutingPutRule.setWsRoutingRule(wsRoutingRule);
			WSRoutingRulePK wsRoutingRulePK = defaultPort
					.putRoutingRule(wsRoutingPutRule);
			System.out.println(wsRoutingRulePK);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testDeleteRoutingRule() {
		try {
			WSDeleteRoutingRule wsDeleteRoutingRule = new WSDeleteRoutingRule();
			WSGetRoutingRulePKs regex = new WSGetRoutingRulePKs("*");
			WSRoutingRulePK[] wsRoutingRulePKArray = defaultPort
					.getRoutingRulePKs(regex);
			wsRoutingRulePKArray[0]
					.setPk(wsRoutingRulePKArray[0].getPk() + "2");
			wsDeleteRoutingRule.setWsRoutingRulePK(wsRoutingRulePKArray[0]);
			WSRoutingRulePK wsRoutingRulePK = defaultPort
					.deleteRoutingRule(wsDeleteRoutingRule);
			System.out.println(wsRoutingRulePK.getPk());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetRoutingOrderV2() {
		WSGetRoutingOrderV2 wsGetRoutingOrder = new WSGetRoutingOrderV2();
		WSRoutingOrderV2PK wsRoutingOrderV2PK = new WSRoutingOrderV2PK();
		wsRoutingOrderV2PK.setName("Create_Customer");
		wsRoutingOrderV2PK.setStatus(WSRoutingOrderV2Status.ACTIVE);
		wsGetRoutingOrder.setWsRoutingOrderPK(wsRoutingOrderV2PK);
		try {
			WSRoutingOrderV2 wsRoutingOrderV2 = defaultPort
					.getRoutingOrderV2(wsGetRoutingOrder);
			System.out.println(wsRoutingOrderV2.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void tesGetRoutingOrderV2PKsByCriteria() {
		try {
			WSGetTransformerPluginV2Details wsGetTransformerPluginDetails = new WSGetTransformerPluginV2Details();
			WSGetTransformerPluginV2SList wsGetTransformerPluginsList = new WSGetTransformerPluginV2SList();
			wsGetTransformerPluginsList.setLanguage("en");
			WSTransformerPluginV2SListItem[] wsTransformerPluginV2SArray = defaultPort
					.getTransformerPluginV2SList(wsGetTransformerPluginsList);

			WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria = new WSGetRoutingOrderV2PKsByCriteria();
			WSRoutingOrderV2SearchCriteria wsSearchCriteria = new WSRoutingOrderV2SearchCriteria();
			wsSearchCriteria.setAnyFieldContains("id");
			wsSearchCriteria.setItemPKConceptContains("id");
			wsSearchCriteria
					.setServiceJNDIContains(wsTransformerPluginV2SArray[0]
							.getJndiName());
			wsGetRoutingOrderV2PKsByCriteria
					.setWsSearchCriteria(wsSearchCriteria);
			WSRoutingOrderV2PK[] wsRoutingOrderV2PKArray = defaultPort
					.getRoutingOrderV2PKsByCriteria(wsGetRoutingOrderV2PKsByCriteria);
			for (int i = 0; i < wsRoutingOrderV2PKArray.length; i++) {
				System.out.println(wsRoutingOrderV2PKArray[i].getName());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetRoutingOrderV2SByCriteria() {
		WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria = new WSGetRoutingOrderV2SByCriteria();
		WSRoutingOrderV2SearchCriteria wsSearchCriteria = new WSRoutingOrderV2SearchCriteria();
		wsSearchCriteria.setAnyFieldContains("id");
		wsSearchCriteria.setItemPKConceptContains("id");
		wsGetRoutingOrderV2SByCriteria.setWsSearchCriteria(wsSearchCriteria);
		try {
			WSRoutingOrderV2[] wsRoutingOrderV2Array = defaultPort
					.getRoutingOrderV2SByCriteria(wsGetRoutingOrderV2SByCriteria);
			for (int i = 0; i < wsRoutingOrderV2Array.length; i++) {
				System.out.println(wsRoutingOrderV2Array[i].getName());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExistsRoutingOrderV2() {
		try {
			WSGetRoutingOrderV2 wsGetRoutingOrder = new WSGetRoutingOrderV2();
			WSRoutingOrderV2PK wsRoutingOrderV2PK = new WSRoutingOrderV2PK();
			wsRoutingOrderV2PK.setName("Create_Customer");
			wsRoutingOrderV2PK.setStatus(WSRoutingOrderV2Status.ACTIVE);
			wsGetRoutingOrder.setWsRoutingOrderPK(wsRoutingOrderV2PK);
			WSRoutingOrderV2 wsRoutingOrderV2 = defaultPort
					.getRoutingOrderV2(wsGetRoutingOrder);

			WSExistsRoutingOrderV2 wsExistsRoutingOrder = new WSExistsRoutingOrderV2();

			wsExistsRoutingOrder.setWsRoutingOrderPK(wsRoutingOrderV2PK);

			WSRoutingOrderV2 wsRoutingOrderV2Return = defaultPort
					.existsRoutingOrderV2(wsExistsRoutingOrder);
			System.out.println(wsRoutingOrderV2Return.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testExecuteRoutingOrderV2Asynchronously() {
		WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously = new WSExecuteRoutingOrderV2Asynchronously();
		WSRoutingOrderV2PK wsRoutingOrderV2PK = new WSRoutingOrderV2PK();
		wsRoutingOrderV2PK.setName("Create_Customer");
		wsRoutingOrderV2PK.setStatus(WSRoutingOrderV2Status.ACTIVE);
		wsExecuteRoutingOrderAsynchronously
				.setRoutingOrderV2PK(wsRoutingOrderV2PK);
		try {
			WSRoutingOrderV2PK wsRoutingOrderV2PKReturn = defaultPort
					.executeRoutingOrderV2Asynchronously(wsExecuteRoutingOrderAsynchronously);
			System.out.println(wsRoutingOrderV2PKReturn);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExecuteRoutingOrderV2Synchronously() {
		WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously = new WSExecuteRoutingOrderV2Synchronously();
		WSRoutingOrderV2PK wsRoutingOrderV2PK = new WSRoutingOrderV2PK();
		wsRoutingOrderV2PK.setName("Create_Customer");
		wsRoutingOrderV2PK.setStatus(WSRoutingOrderV2Status.ACTIVE);
		wsExecuteRoutingOrderSynchronously
				.setRoutingOrderV2PK(wsRoutingOrderV2PK);
		try {
			WSString wsString = defaultPort
					.executeRoutingOrderV2Synchronously(wsExecuteRoutingOrderSynchronously);
			System.out.print(wsString.getValue());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testDeleteRoutingOrderV2() {
		WSDeleteRoutingOrderV2 wsDeleteRoutingOrder = new WSDeleteRoutingOrderV2();
		WSRoutingOrderV2PK wsRoutingOrderV2PK = new WSRoutingOrderV2PK();
		wsRoutingOrderV2PK.setName("Create_Customer");
		wsRoutingOrderV2PK.setStatus(WSRoutingOrderV2Status.ACTIVE);

		wsDeleteRoutingOrder.setWsRoutingOrderPK(wsRoutingOrderV2PK);
		try {
			WSRoutingOrderV2PK wsRoutingOrderV2PKReturn = defaultPort
					.deleteRoutingOrderV2(wsDeleteRoutingOrder);
			System.out.println(wsRoutingOrderV2PKReturn.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	/***************************************************************************
	 * Routing Engine V2
	 * **************************************************************************/
	public void testRouteItemV2() {
		WSRouteItemV2 wsRouteItem = new WSRouteItemV2();
		WSItemPK wsItemPK = new WSItemPK();
		wsItemPK.setConceptName("Student");
		wsItemPK.setWsDataClusterPK(new WSDataClusterPK("Order"));
		wsRouteItem.setWsItemPK(wsItemPK);
		try {
			WSRoutingRulePK[] wsRoutingRulePKArray = defaultPort
					.routeItemV2(wsRouteItem);
			for (int i = 0; i < wsRoutingRulePKArray.length; i++) {
				System.out.println(wsRoutingRulePKArray[i].getPk());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testRoutingEngineV2Action() {
		WSRoutingEngineV2Action wsRoutingEngineAction = new WSRoutingEngineV2Action();
		wsRoutingEngineAction.setWsAction(WSRoutingEngineV2ActionCode.START);
		try {
			WSRoutingEngineV2Status wsRoutingEngineV2Status = defaultPort
					.routingEngineV2Action(wsRoutingEngineAction);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
}
