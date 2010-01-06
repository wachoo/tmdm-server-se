package com.amalto.webapp.v3.synchronization.dwr;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSGetSynchronizationItem;
import com.amalto.webapp.util.webservices.WSGetSynchronizationItemPKs;
import com.amalto.webapp.util.webservices.WSResolveSynchronizationItem;
import com.amalto.webapp.util.webservices.WSSynchronizationItem;
import com.amalto.webapp.util.webservices.WSSynchronizationItemPK;
import com.amalto.webapp.util.webservices.WSSynchronizationItemPKArray;
import com.amalto.webapp.v3.synchronization.bean.SynchronizationItem;
import com.amalto.webapp.v3.synchronization.bean.TreeNode;


public class SynchronizationItemDWR {
	
	private Logger logger=org.apache.log4j.Logger.getLogger(this.getClass());
	
	public SynchronizationItemDWR() {
		super();
	}

	public boolean saveSyncItem(SynchronizationItem item)throws Exception{
		logger.debug("saveSyncItem() ");
		boolean ret=true;
		try {			
			//set item'node resolvedXml 
			String resolvedXml= item.getNode().serialize();
			item.setStatus(SynchronizationItem.STATUS_RESOLVED);
			logger.debug("resolvedXml-- " +resolvedXml + " status-"+ item.getStatus());
			item.setResolvedProjection(resolvedXml);
//			WSSynchronizationItem wsitem=new WSSynchronizationItem();			
//			wsitem=item.POJO2WS(item);
//			logger.debug(" wsitem.status-"+ wsitem.getStatus().getValue());
			//Util.getPort().putSynchronizationItem(new WSPutSynchronizationItem(wsitem));
			Util.getPort().resolveSynchronizationItem(new WSResolveSynchronizationItem(new WSSynchronizationItemPK(new String[]{"",item.getItemPOJOPK().getUniqueID()}), resolvedXml));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			ret=false;
			//Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
			//if (m.matches()) throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());			
		}finally{
			return ret;
		}		
	}
	
	public ListRange getSyncItems(int start, int limit,String sort,String dir,String regex)throws Exception{

		logger.debug("getSyncItems() regex== "+regex);
		try {
			List<SynchronizationItem> list=new ArrayList<SynchronizationItem>();	
			
			if(regex==null || regex.length()==0){
				regex="";
			}
			regex=regex.replaceAll("\\*", "");
			regex=".*"+regex+".*";
			
			WSSynchronizationItemPKArray pks=Util.getPort().getSynchronizationItemPKs(new WSGetSynchronizationItemPKs(regex));
			WSSynchronizationItemPK[] items= pks.getWsSynchronizationItemPK();
			
			logger.debug("pks() " + items.length);
			
			for(WSSynchronizationItemPK pk: items){
				WSSynchronizationItem item=Util.getPort().getSynchronizationItem(new WSGetSynchronizationItem(pk));
				if(item.getResolvedProjection() ==null ){
					continue;
				}
				SynchronizationItem syncItem=new SynchronizationItem();
				syncItem=syncItem.WS2POJO(item);
				if(!syncItem.getItemPK().matches(regex)){
					continue;
				}
				TreeNode node=new TreeNode();
				node=node.deserialize(item.getResolvedProjection());
				logger.debug("item.getResolvedProjection()-- " +item.getResolvedProjection());
				syncItem.setNode(node);				
				list.add(syncItem);
			}	
			List<SynchronizationItem> sublist=list;
			if(list.size()>0){
				start=start<list.size()?start:list.size()-1;
				int end=list.size()<(start+limit)?list.size()-1:(start+limit-1);
				sublist=list.subList(start, end+1);
			}
			ListRange listRange = new ListRange();
			listRange.setData(sublist.toArray(new SynchronizationItem[sublist.size()]));
			listRange.setTotalSize(list.size());			
			return listRange;

		} catch (Exception e) {
			e.printStackTrace();
			//Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
			//if (m.matches()) throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}

	}
	
	
}
