package org.talend.mdm.test;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import urn_com_amalto_xtentis_webservice.WSCount;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSGetItems;
import urn_com_amalto_xtentis_webservice.WSGetRoutingOrderV2PKsByCriteria;
import urn_com_amalto_xtentis_webservice.WSGetViewPKs;
import urn_com_amalto_xtentis_webservice.WSRoutingOrderV2PK;
import urn_com_amalto_xtentis_webservice.WSRoutingOrderV2SearchCriteria;
import urn_com_amalto_xtentis_webservice.WSRoutingOrderV2Status;
import urn_com_amalto_xtentis_webservice.WSStringPredicate;
import urn_com_amalto_xtentis_webservice.WSViewPK;
import urn_com_amalto_xtentis_webservice.WSViewSearch;
import urn_com_amalto_xtentis_webservice.WSWhereCondition;
import urn_com_amalto_xtentis_webservice.WSWhereItem;
import urn_com_amalto_xtentis_webservice.WSWhereOperator;
import urn_com_amalto_xtentis_webservice.WSXPathsSearch;

public class CoreSearchTestCase extends WebserviceTestCase {


	public void testViewSearch() {
		
		WSViewSearch wsViewSearch = new WSViewSearch();
		wsViewSearch.setWsDataClusterPK(new WSDataClusterPK("Order"));
		wsViewSearch.setWsViewPK(new WSViewPK("Browse_items_City"));
		wsViewSearch.setWhereItem(null);
		wsViewSearch.setSpellTreshold(-1);
		wsViewSearch.setSkip(0);
		wsViewSearch.setMaxItems(10);
		wsViewSearch.setOrderBy("/City/name");
		wsViewSearch.setDirection("descending");
		
		String[] wsStringArray;
		try {
			wsStringArray = defaultPort.viewSearch(wsViewSearch);
			for (int i = 0; i < wsStringArray.length; i++) {
				System.out.println(wsStringArray[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
    public void testViewSearchWithWhereItems() {
    	
    	ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>();
    	
    	WSWhereCondition wc1=new WSWhereCondition(
				"City/name",
				WSWhereOperator.CONTAINS,
				"Beijing",
				WSStringPredicate.NONE,
				false
				);
    	WSWhereItem item1=new WSWhereItem(wc1,null,null);
    	
    	WSWhereCondition wc2=new WSWhereCondition(
				"City/country",
				WSWhereOperator.EQUALS,
				"[0101]",
				WSStringPredicate.NONE,
				false
				);
    	WSWhereItem item2=new WSWhereItem(wc2,null,null);
    	
    	conditions.add(item1);
    	conditions.add(item2);
		WSWhereItem wi = new WSWhereItem(null,conditions.toArray(new WSWhereItem[conditions.size()]),null);
		
		WSViewSearch wsViewSearch = new WSViewSearch();
		wsViewSearch.setWsDataClusterPK(new WSDataClusterPK("Order"));
		wsViewSearch.setWsViewPK(new WSViewPK("Browse_items_City"));
		wsViewSearch.setWhereItem(wi);
		wsViewSearch.setSpellTreshold(-1);
		wsViewSearch.setSkip(0);
		wsViewSearch.setMaxItems(10);
		wsViewSearch.setOrderBy("/City/name");
		wsViewSearch.setDirection("descending");
		
		String[] wsStringArray;
		try {
			wsStringArray = defaultPort.viewSearch(wsViewSearch);
			for (int i = 0; i < wsStringArray.length; i++) {
				System.out.println(wsStringArray[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
    
    public void testViewSearchWithMultiPivot() {
    	
        ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>();
    	
    	WSWhereCondition wc1=new WSWhereCondition(
				"City/name",
				WSWhereOperator.CONTAINS,
				"Beijing",
				WSStringPredicate.NONE,
				false
				);
    	WSWhereItem item1=new WSWhereItem(wc1,null,null);
    	
    	conditions.add(item1);
		WSWhereItem wi = new WSWhereItem(null,conditions.toArray(new WSWhereItem[conditions.size()]),null);
		
		WSViewSearch wsViewSearch = new WSViewSearch();
		wsViewSearch.setWsDataClusterPK(new WSDataClusterPK("Order"));
		wsViewSearch.setWsViewPK(new WSViewPK("Browse_view_CityAndCountry"));
		wsViewSearch.setWhereItem(wi);
		wsViewSearch.setSpellTreshold(-1);
		wsViewSearch.setSkip(0);
		wsViewSearch.setMaxItems(10);
		
		
		String[] wsStringArray;
		try {
			wsStringArray = defaultPort.viewSearch(wsViewSearch);
			for (int i = 0; i < wsStringArray.length; i++) {
				System.out.println(wsStringArray[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
    
    public void testCount() {
    	//count items 
    	try {
			String countNum=defaultPort.count(new WSCount(
					new WSDataClusterPK("Test"),
					"Customer",
					null,
					-1
			)).getValue();
			System.out.println("Total count number is: "+countNum);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
    
    public void testGetItems() {
    	
    	WSDataClusterPK wsDataClusterPK=new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
 		String conceptName="Update";//Hard Code
 		
    	try {
			String[] results =
				defaultPort.getItems(new WSGetItems(
						wsDataClusterPK,
						conceptName,
						null,
			    		-1,
			    		0,
			    		20
			    	)
			    );
			for (int i = 0; i < results.length; i++) {
				System.out.println(results[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
    
    public void testXpathSearch() {
        String filterConcept="Company[matches(., '', 'i')]";
    	ArrayList<String> xPaths = new ArrayList<String>();
		//add the key paths last, since there may be multiple keys
    	xPaths.add(filterConcept);
		xPaths.add(filterConcept+"/../../i");
		
    	//Run the query
		try {
			String [] results = defaultPort.xPathsSearch(new WSXPathsSearch(
				new WSDataClusterPK("Test"),
				null,
				xPaths.toArray(new String[xPaths.size()]),
				null,
				-1,
				0,
				20,
				null,
				null
			));
			
			for (int i = 0; i < results.length; i++) {
				System.out.println(results[i]);
			}
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
    
    public void testGetViewPKs() {
    	try {
			WSViewPK[] viewPKs=defaultPort.getViewPKs(new WSGetViewPKs(".*"));
			for (int i = 0; i < viewPKs.length; i++) {
				System.out.println(viewPKs[i].getPk());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
    
    public void testGetRoutingOrderPKs() {
    	try {
    		WSRoutingOrderV2PK[] wsRoutingOrderV2PKs=defaultPort.getRoutingOrderV2PKsByCriteria(new WSGetRoutingOrderV2PKsByCriteria(
			    new WSRoutingOrderV2SearchCriteria(WSRoutingOrderV2Status.FAILED,null,null,-1,-1,-1,-1,-1,-1,-1,-1,null,null,null,null,null)		
			
			));
			for (int i = 0; i < wsRoutingOrderV2PKs.length; i++) {
				System.out.println(wsRoutingOrderV2PKs[i].getName());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
    
    
}
