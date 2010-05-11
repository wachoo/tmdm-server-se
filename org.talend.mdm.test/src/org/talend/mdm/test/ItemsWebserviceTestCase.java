package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDeleteItem;
import urn_com_amalto_xtentis_webservice.WSDeleteItems;
import urn_com_amalto_xtentis_webservice.WSDropItem;
import urn_com_amalto_xtentis_webservice.WSDroppedItemPK;
import urn_com_amalto_xtentis_webservice.WSExtractUsingTransformer;
import urn_com_amalto_xtentis_webservice.WSExtractUsingTransformerThruView;
import urn_com_amalto_xtentis_webservice.WSInt;
import urn_com_amalto_xtentis_webservice.WSItemPK;
import urn_com_amalto_xtentis_webservice.WSPipelineTypedContentEntry;
import urn_com_amalto_xtentis_webservice.WSTransformerPK;
import urn_com_amalto_xtentis_webservice.WSViewPK;

public class ItemsWebserviceTestCase extends WebserviceTestCase {
	/***************************************************************************
	 *Extract Items
	 * **************************************************************************/

	public void testExtractUsingTransformer() {
		WSExtractUsingTransformer wsExtractUsingTransformer = new WSExtractUsingTransformer();
		WSItemPK wsItemPK = new WSItemPK();
		wsItemPK.setConceptName("Student");
		wsItemPK.setWsDataClusterPK(new WSDataClusterPK("Order"));
		String[] ids = { "001"};
		wsItemPK.setIds(ids);
		wsExtractUsingTransformer.setWsItemPK(wsItemPK);
		WSTransformerPK wsTransformerPK = new WSTransformerPK();
		wsTransformerPK.setPk("default_TISJob_tiscall_no_return_transformer");
		wsExtractUsingTransformer.setWsTransformerPK(wsTransformerPK);
		try {
			WSPipelineTypedContentEntry[] wsPipelineTypedContentEntryArray = defaultPort
					.extractUsingTransformer(wsExtractUsingTransformer);
			
			assertNotNull(wsPipelineTypedContentEntryArray);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testExtractUsingTransformerThruView() {
		WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView = new WSExtractUsingTransformerThruView();
		wsExtractUsingTransformerThruView.setDirection("");
		wsExtractUsingTransformerThruView
				.setWsDataClusterPK(new WSDataClusterPK("Order"));
		wsExtractUsingTransformerThruView.setWsViewPK(new WSViewPK(
				"Browse_items_Student"));
		WSTransformerPK wsTransformerPK = new WSTransformerPK();
		wsTransformerPK.setPk("default_TISJob_tiscall_no_return_transformer");
		wsExtractUsingTransformerThruView.setWsTransformerPK(wsTransformerPK);
		try {
			WSPipelineTypedContentEntry[] wsPipelineTypedContentEntryArray = defaultPort
					.extractUsingTransformerThruView(wsExtractUsingTransformerThruView);
			
			assertNotNull(wsPipelineTypedContentEntryArray);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	/***************************************************************************
	 * Delete Items
	 * **************************************************************************/

	public void testDeleteItem() {
		WSDeleteItem wsDeleteItem = new WSDeleteItem();
		WSItemPK wsItemPK = new WSItemPK();
		wsItemPK.setConceptName("Student");
		wsItemPK.setWsDataClusterPK(new WSDataClusterPK("Order"));
		String[] ids = { "001"};
		wsItemPK.setIds(ids);
		wsDeleteItem.setWsItemPK(wsItemPK);
		try {
			WSItemPK wsItemPKReturn = defaultPort.deleteItem(wsDeleteItem);
			assertNotNull(wsItemPKReturn);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testDeleteItems() {
		WSDeleteItems wsDeleteItems = new WSDeleteItems();
		wsDeleteItems.setConceptName("Student");
		wsDeleteItems.setWsDataClusterPK(new WSDataClusterPK("Order"));
		try {
			WSInt wsInt = defaultPort.deleteItems(wsDeleteItems);
			assertNotNull(wsInt);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testDropItem() {
		WSDropItem wsDropItem = new WSDropItem();
		wsDropItem.setPartPath("/");
		WSItemPK wsItemPK = new WSItemPK();
		wsItemPK.setConceptName("Student");
		wsItemPK.setWsDataClusterPK(new WSDataClusterPK("Order"));
		String[] ids = { "001" };
		wsItemPK.setIds(ids);
		wsDropItem.setWsItemPK(wsItemPK);
		try {
			WSDroppedItemPK wsDroppedItemPK = defaultPort.dropItem(wsDropItem);
			assertNotNull(wsDroppedItemPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
