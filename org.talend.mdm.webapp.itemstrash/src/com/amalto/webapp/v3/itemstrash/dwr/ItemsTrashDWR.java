package com.amalto.webapp.v3.itemstrash.dwr;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDroppedItem;
import com.amalto.webapp.util.webservices.WSDroppedItemPK;
import com.amalto.webapp.util.webservices.WSDroppedItemPKArray;
import com.amalto.webapp.util.webservices.WSFindAllDroppedItemsPKs;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSLoadDroppedItem;
import com.amalto.webapp.util.webservices.WSRecoverDroppedItem;
import com.amalto.webapp.util.webservices.WSRemoveDroppedItem;
import com.amalto.webapp.v3.itemstrash.bean.ItemsTrashItem;


public class ItemsTrashDWR {
	
	private Logger logger=org.apache.log4j.Logger.getLogger(this.getClass());
	
	public ItemsTrashDWR() {
		super();
	}

	
//	Date date =new Date(2009,6,4);
//	ItemsTrashInstances iti = new ItemsTrashInstances();
	public ListRange getTrashItems(int start, int limit,String sort,String dir,String regex)throws Exception{
		logger.debug("getTrashItems() regex== "+regex + "start=="+start+"limit=="+limit);
		try{
//			
			if(regex==null || regex.length()==0){
				regex="";
			}
			regex=regex.replaceAll("\\*", "");
			regex=".*"+regex+".*";
			
			List<ItemsTrashItem> li = new ArrayList<ItemsTrashItem>();
			
			WSDroppedItemPKArray  pks= Util.getPort().findAllDroppedItemsPKs(new WSFindAllDroppedItemsPKs(regex));
			WSDroppedItemPK[] items = pks.getWsDroppedItemPK();
			
			for(WSDroppedItemPK pk: items){
				WSDroppedItem wsitem = Util.getPort().loadDroppedItem(new WSLoadDroppedItem(pk));
				ItemsTrashItem item = new ItemsTrashItem();
				item = item.WS2POJO(wsitem);
				li.add(item);
			}
			List<ItemsTrashItem> sublist=li;
			if(li.size()>0){
				start=start<li.size()?start:li.size()-1;
				int end=li.size()<(start+limit)?li.size()-1:(start+limit-1);
				sublist=li.subList(start, end+1);
			}
			ListRange listRange = new ListRange();
			listRange.setData(sublist.toArray(new ItemsTrashItem[sublist.size()]));
			listRange.setTotalSize(li.size());			
			return listRange;
		}catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}

		
	}
	
	public void removeDroppedItem(String itemPk,String partPath,String revisionId,String conceptName,String ids) throws Exception{
		try{
//			WSDroppedItemPK
			String[] ids1 = ids.split("\\.");
			WSDataClusterPK wddcpk = new WSDataClusterPK(itemPk);
			WSItemPK wdipk = new WSItemPK(wddcpk,conceptName,ids1);
			WSDroppedItemPK wddipk = new WSDroppedItemPK(wdipk,partPath,revisionId);
			WSRemoveDroppedItem wsrdi = new WSRemoveDroppedItem(wddipk);
			WSDroppedItemPK wdipkd = Util.getPort().removeDroppedItem(wsrdi);
//			return wdipkd;
		}catch(Exception e) {
			e.printStackTrace();
			
				throw new Exception(e.getClass().getName() + ": "
						+ e.getLocalizedMessage());
		}
		
	}
	 
	public void recoverDroppedItem(String itemPk,String partPath,String revisionId,String conceptName,String ids) throws Exception{
		try{
			
			String[] ids1 = ids.split("\\.");
			WSDataClusterPK wddcpk = new WSDataClusterPK(itemPk);
			WSItemPK wdipk = new WSItemPK(wddcpk,conceptName,ids1);
			WSDroppedItemPK wsdipk = new WSDroppedItemPK(wdipk,partPath,revisionId);
			WSRecoverDroppedItem wsrdi = new WSRecoverDroppedItem(wsdipk);
			WSItemPK wsipk = Util.getPort().recoverDroppedItem(wsrdi);
//			return wsipk;
		}catch(Exception e) {
			e.printStackTrace();
				throw new Exception(e.getClass().getName() + ": "
						+ e.getLocalizedMessage());
		}
		
	}
}
