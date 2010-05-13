package org.talend.mdm.test;

import java.rmi.RemoteException;
import java.util.ArrayList;

import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSStringPredicate;
import urn_com_amalto_xtentis_webservice.WSViewPK;
import urn_com_amalto_xtentis_webservice.WSViewSearch;
import urn_com_amalto_xtentis_webservice.WSWhereCondition;
import urn_com_amalto_xtentis_webservice.WSWhereItem;
import urn_com_amalto_xtentis_webservice.WSWhereOperator;

public class ViewSearchTestCase extends WebserviceTestCase {


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
    
    
}
