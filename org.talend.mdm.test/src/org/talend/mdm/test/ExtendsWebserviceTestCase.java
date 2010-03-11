package org.talend.mdm.test;

import java.rmi.RemoteException;
import java.util.Date;

import urn_com_amalto_xtentis_webservice.WSCategoryData;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSGetChildrenItems;
import urn_com_amalto_xtentis_webservice.WSGetItemPKsByCriteria;
import urn_com_amalto_xtentis_webservice.WSGetItemPKsByFullCriteria;
import urn_com_amalto_xtentis_webservice.WSItemPK;
import urn_com_amalto_xtentis_webservice.WSItemPKsByCriteriaResponseResults;
import urn_com_amalto_xtentis_webservice.WSPutItem;
import urn_com_amalto_xtentis_webservice.WSPutItemWithCustomReport;
import urn_com_amalto_xtentis_webservice.WSPutItemWithReport;

public class ExtendsWebserviceTestCase extends WebserviceTestCase {
	//pass
	public void testGetItemPKsByFullCriteria() {
		WSGetItemPKsByFullCriteria wsGetItemPKsByFullCriteria = new WSGetItemPKsByFullCriteria();
		
		wsGetItemPKsByFullCriteria.setUseFTSearch(false);
		
		WSGetItemPKsByCriteria wsGetItemPKsByCriteria = new WSGetItemPKsByCriteria();
		wsGetItemPKsByCriteria.setConceptName("Student");
		
		wsGetItemPKsByCriteria.setContentKeywords("ContentKeywords");
		wsGetItemPKsByCriteria.setKeysKeywords("KeysKeywords");
		wsGetItemPKsByCriteria.setMaxItems(2);
		wsGetItemPKsByCriteria.setSkip(1);
		wsGetItemPKsByCriteria.setFromDate(new Date().getTime());
		wsGetItemPKsByCriteria.setToDate(new Date().getTime());
		wsGetItemPKsByCriteria.setWsDataClusterPK(new WSDataClusterPK("Order"));
		
		wsGetItemPKsByFullCriteria
				.setWsGetItemPKsByCriteria(wsGetItemPKsByCriteria);
		try {
			WSItemPKsByCriteriaResponseResults[] wsItemPKsByCriteriaResponseResults = defaultPort
					.getItemPKsByFullCriteria(wsGetItemPKsByFullCriteria);
			for (int i = 0; i < wsItemPKsByCriteriaResponseResults.length; i++) {
				System.out.println(wsItemPKsByCriteriaResponseResults[i]
						.getWsItemPK().getConceptName());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	//pass
	public void testGetMDMCategory() {
		WSCategoryData wsCategoryDataRequest = new WSCategoryData();
		wsCategoryDataRequest.setCategorySchema(this.BUSINESSCONCEPTSCHEMA);
		
		try {
			WSCategoryData wsCategoryData = defaultPort
					.getMDMCategory(wsCategoryDataRequest);
			System.out.println(wsCategoryData.getCategorySchema());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	//pass
	public void testGetChildrenItems() {
		WSGetChildrenItems wsGetChildrenItems = new WSGetChildrenItems();
		wsGetChildrenItems.setClusterName("Order");
		wsGetChildrenItems.setConceptName("Student");
		wsGetChildrenItems.setFatherPK("002");
		wsGetChildrenItems.setFKXpath(wsGetChildrenItems.getConceptName()+"/classId");
		wsGetChildrenItems.setLabelXpath(wsGetChildrenItems.getConceptName()+"/name");
		String[] PKXpaths={wsGetChildrenItems.getConceptName()+"/id"};
		wsGetChildrenItems.setPKXpaths(PKXpaths);
		try {
			String[] items = defaultPort.getChildrenItems(wsGetChildrenItems);
			for (int i = 0; i < items.length; i++) {
				System.out.println(items[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	//pass
	public void testPutItemWithCustomReport() {
		String updateReport=" <Update><UserName>administrator</UserName><Source>genericUI</Source><TimeInMillis>1266980303890</TimeInMillis><OperationType>CREATE</OperationType><RevisionID/><DataCluster>Order</DataCluster><DataModel>Order</DataModel><Concept>Picture</Concept><Key>003</Key></Update>";
		String xmlString="<Student>"+
            "<id>001</id>"+
            "<name>Jack</name>"+
            "<age>2010-03-11T00:00:00</age>"+
            "<classId>[002]</classId>"+
        "</Student>";
		WSPutItemWithCustomReport wsPutItemWithCustomReport = new WSPutItemWithCustomReport();
		wsPutItemWithCustomReport.setUser(null);
		WSPutItemWithReport wsPutItemWithReport = new WSPutItemWithReport();
		wsPutItemWithReport.setInvokeBeforeSaving(true);
		wsPutItemWithReport.setSource(updateReport);
		
		WSPutItem wsPutItem = new WSPutItem();
		wsPutItem.setXmlString(xmlString);
		wsPutItem.setIsUpdate(false);
		wsPutItem.setWsDataClusterPK(new WSDataClusterPK("Order"));
		wsPutItem.setWsDataModelPK(new WSDataModelPK("Order"));
		
		wsPutItemWithReport.setWsPutItem(wsPutItem);
		wsPutItemWithCustomReport.setWsPutItemWithReport(wsPutItemWithReport);
		try {
			WSItemPK wsItemPK = defaultPort
					.putItemWithCustomReport(wsPutItemWithCustomReport);
			System.out.println(wsItemPK.getConceptName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
}
