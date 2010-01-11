package com.amalto.core.delegator;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;

public interface IItemCtrlDelegator {
	//methods from ItemCtrl2Bean
	public ArrayList<String> viewSearch(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		ViewPOJOPK viewPOJOPK,
			IWhereItem whereItem, 
			int spellThreshold,
			String orderBy,
			String direction,
			int start,
			int limit
		) throws XtentisException;
	
	public ArrayList<String> getItemsPivotIndex(
    		String clusterName,     		
			String mainPivotName,
			LinkedHashMap<String, String[]> pivotWithKeys, 
			String[] indexPaths,
			IWhereItem whereItem, 
			String[] pivotDirections,
			String[] indexDirections, 
			int start, 
			int limit
		) throws XtentisException;
	
	public void resendFailtSvnMessage()throws Exception;
	
	
	ItemPOJOPK putItem(ItemPOJO item, String schema,String dataModelName) throws XtentisException;
	
}
