package com.amalto.webapp.v3.hierarchical.dwr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.ComboItemBean;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSPutItemWithReport;
import com.amalto.webapp.v3.hierarchical.bean.DataObjectContext;
import com.amalto.webapp.v3.hierarchical.bean.FilterItem;
import com.amalto.webapp.v3.hierarchical.bean.HierarchicalTreeCriterion;
import com.amalto.webapp.v3.hierarchical.bean.UpdateHistory;
import com.amalto.webapp.v3.hierarchical.bean.UpdateRecordItem;
import com.amalto.webapp.v3.hierarchical.util.HierarchicalUtil;

public class HierarchicalDWR {

	public HierarchicalDWR() {

	}

    public ListRange getDataObjectsList(int start, int limit,String sort,String dir,String regex) {
    	
    	String language="";
    	if(regex==null||regex.length()==0||regex.equals("null"))language="en";
    	
		ListRange listRange = new ListRange();
		
		List<ComboItemBean> comboItem=new ArrayList<ComboItemBean>();
		Map<String, String> businessConcepts=HierarchicalUtil.getBusinessConcepts(language);
		for (Iterator<String> iterator = businessConcepts.keySet().iterator(); iterator.hasNext();) {
			String xpath = iterator.next();
			String label = businessConcepts.get(xpath);
			comboItem.add(new ComboItemBean(xpath,label));
		}
		
		listRange.setData(comboItem.toArray());
		listRange.setTotalSize(comboItem.size());

		return listRange;
	}
    
    public ListRange getPossiblePivotsList(int start, int limit,String sort,String dir,String regex){

		ListRange listRange = new ListRange();
		
		if(regex==null||regex.length()==0)return listRange;
		String[] inputParams=regex.split("&");
		String businessConcept=inputParams[0];
		String language=inputParams[1];
		
		List<ComboItemBean> comboItem=new ArrayList<ComboItemBean>();
		Map<String, String> possiblePivots=HierarchicalUtil.getPossiblePivots(businessConcept, language);
		for (Iterator<String> iterator = possiblePivots.keySet().iterator(); iterator.hasNext();) {
			String xpath = iterator.next();
			String label = possiblePivots.get(xpath);
			comboItem.add(new ComboItemBean(xpath,label));
		}
		
		listRange.setData(comboItem.toArray());
		listRange.setTotalSize(comboItem.size());

		return listRange;
    	
    }
	
	public ListRange getTitleList(int start, int limit,String sort,String dir,String regex) {
        ListRange listRange = new ListRange();
		
		if(regex==null||regex.length()==0)return listRange;
		String[] inputParams=regex.split("&");
		String businessConcept=inputParams[0];
		String language=inputParams[1];
		
		List<ComboItemBean> comboItem=new ArrayList<ComboItemBean>();
		Map<String, String> titleList=HierarchicalUtil.getTitleList(businessConcept, language);
		for (Iterator<String> iterator = titleList.keySet().iterator(); iterator.hasNext();) {
			String xpath = iterator.next();
			String label = titleList.get(xpath);
			comboItem.add(new ComboItemBean(xpath,label));
		}
		
		listRange.setData(comboItem.toArray());
		listRange.setTotalSize(comboItem.size());

		return listRange;
	}
	
	
    public boolean updateHierarchicalTreeCriterion(String dataObjectName,String pivotPath,String titleFieldPath,FilterItem[] filters,String orderExpr,int limit) {
    	try {
			WebContext ctx = WebContextFactory.get();
			Configuration config = Configuration.getInstance();
			String dataClusterName = config.getCluster();
			
			HierarchicalTreeCriterion criterion=new HierarchicalTreeCriterion(dataClusterName,dataObjectName, pivotPath,titleFieldPath, filters);
			HierarchicalUtil.equipDirection2HierarchicalTreeCriterion(criterion, orderExpr);
			HierarchicalUtil.equipLimit2HierarchicalTreeCriterion(criterion, limit);
			ctx.getSession().setAttribute(
					HierarchicalUtil.HIERARCHICAL_TREE_CRITERION,criterion
					);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
    
    public void resetHierarchicalTreeCriterion() {
    	WebContext ctx = WebContextFactory.get();
    	ctx.getSession().setAttribute(
				HierarchicalUtil.HIERARCHICAL_TREE_CRITERION,
				null);
	}
    
    public boolean checkHierarchicalTreeCriterion() {
    	
    	WebContext ctx = WebContextFactory.get();
    	Object hierarchicalTreeCriterionObj=ctx.getSession().getAttribute(HierarchicalUtil.HIERARCHICAL_TREE_CRITERION);
    	if(hierarchicalTreeCriterionObj==null){
    		return false;
    	}else{
    		HierarchicalTreeCriterion hierarchicalTreeCriterion=(HierarchicalTreeCriterion)hierarchicalTreeCriterionObj;
    		if(hierarchicalTreeCriterion.getDataObjectName()!=null&&hierarchicalTreeCriterion.getDataObjectName().length()>0)return true;
    	}
	
        return false;
	}
    
    public void resetHierarchicalTreeUpdateHistory() throws Exception{
    	
    	WebContext ctx = WebContextFactory.get();
    	Configuration config = Configuration.getInstance();
    	HierarchicalTreeCriterion hierarchicalTreeCriterion=(HierarchicalTreeCriterion)ctx.getSession().getAttribute(HierarchicalUtil.HIERARCHICAL_TREE_CRITERION);
    	
    	DataObjectContext dataObjectContext=new DataObjectContext(config.getCluster(),config.getModel(),hierarchicalTreeCriterion.getDataObjectName());
    	UpdateHistory updateHistory=new UpdateHistory(dataObjectContext);
    	
    	ctx.getSession().setAttribute(
				HierarchicalUtil.HIERARCHICAL_TREE_UPDATEHISTORY,
				updateHistory);
    }
    
    public boolean recordChanges(String keys,String xpath,String newValue) {
    	
    	try {
			WebContext ctx = WebContextFactory.get();
			UpdateHistory updateHistory = (UpdateHistory) ctx.getSession().getAttribute(HierarchicalUtil.HIERARCHICAL_TREE_UPDATEHISTORY);
			updateHistory.logChange(keys, xpath, newValue);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	return true;

	}
    
    public boolean recordChanges(String[] keysArray,String[] xpathArray,String newValue) {
    	
    	try {
			WebContext ctx = WebContextFactory.get();
			UpdateHistory updateHistory = (UpdateHistory) ctx.getSession().getAttribute(HierarchicalUtil.HIERARCHICAL_TREE_UPDATEHISTORY);
			for (int i = 0; i < keysArray.length; i++) {
				updateHistory.logChange(keysArray[i], xpathArray[i], newValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	return true;

	}
    
    public String saveChanges() throws Exception {
    	
    	try {
			WebContext ctx = WebContextFactory.get();
			UpdateHistory updateHistory = (UpdateHistory) ctx.getSession().getAttribute(HierarchicalUtil.HIERARCHICAL_TREE_UPDATEHISTORY);
			if(updateHistory.isEmpty())return "No Changes! ";
			
			DataObjectContext dataObjectContext=updateHistory.getDataObjectContext();
			for (Iterator<UpdateRecordItem> iterator = updateHistory.iterator(); iterator.hasNext();) {
				UpdateRecordItem updateRecordItem =  iterator.next();
				
				//combine to update data
				String[] keys={updateRecordItem.getKeys()};
				WSItem wsItem=Util.getPort().getItem(new WSGetItem(new WSItemPK(new WSDataClusterPK(dataObjectContext.getDataCluster()),dataObjectContext.getConcept(),keys)));
				Document document = Util.parse(wsItem.getContent());
				NodeList nlist=Util.getNodeList(document, "/"+updateRecordItem.getXpath());
				if(nlist!=null&&nlist.getLength()>0){
					Node targetNode=nlist.item(0);
					targetNode.setTextContent(updateRecordItem.getNewValue());
				}
				String updatedContent=Util.nodeToString(document);
				//do put
				WSPutItem wsPutItem=new WSPutItem(new WSDataClusterPK(dataObjectContext.getDataCluster()),updatedContent,new WSDataModelPK(dataObjectContext.getDataModel()),false);
				WSPutItemWithReport wsPutItemWithReport=new WSPutItemWithReport(wsPutItem,"genericUI",new Boolean(true));
				Util.getPort().putItemWithReport(wsPutItemWithReport);
			}
			
			resetHierarchicalTreeUpdateHistory();
			return "OK";
		}catch(Exception e){
			String err= "Unable to update items completely! ";
			org.apache.log4j.Logger.getLogger(HierarchicalDWR.class).error(err,e);
			throw new Exception(e.getLocalizedMessage());
		}
	}

}
