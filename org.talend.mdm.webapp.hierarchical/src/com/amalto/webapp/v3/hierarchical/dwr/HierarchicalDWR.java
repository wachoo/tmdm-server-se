package com.amalto.webapp.v3.hierarchical.dwr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.ComboItemBean;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSPutItemWithReport;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.amalto.webapp.v3.hierarchical.bean.DataObjectContext;
import com.amalto.webapp.v3.hierarchical.bean.FilterItem;
import com.amalto.webapp.v3.hierarchical.bean.Filters;
import com.amalto.webapp.v3.hierarchical.bean.HierarchicalReport;
import com.amalto.webapp.v3.hierarchical.bean.HierarchicalTreeCriterion;
import com.amalto.webapp.v3.hierarchical.bean.UpdateHistory;
import com.amalto.webapp.v3.hierarchical.bean.UpdateRecordItem;
import com.amalto.webapp.v3.hierarchical.util.HierarchicalUtil;


public class HierarchicalDWR {

	public HierarchicalDWR() {

	}


	public ListRange getDataObjectsList(int start, int limit, String sort,
			String dir, String regex) throws Exception {
		try {
			String language = "";
			if (regex == null || regex.length() == 0 || regex.equals("null")) {
				language = "en";
			} else {
				language = regex;
			}

			ListRange listRange = new ListRange();

			List<ComboItemBean> comboItem = new ArrayList<ComboItemBean>();
			Map<String, String> businessConcepts = HierarchicalUtil
					.getBusinessConcepts(language);
			for (Iterator<String> iterator = businessConcepts.keySet()
					.iterator(); iterator.hasNext();) {
				String xpath = iterator.next();
				String label = businessConcepts.get(xpath);
				comboItem.add(new ComboItemBean(xpath, label));
			}
			listRange.setData(comboItem.toArray());
			listRange.setTotalSize(comboItem.size());
			Map<String, String> map = HierarchicalUtil.getViewsList(language);
			HierarchicalUtil.synchronizeConecptList(listRange, map);
			return listRange;
		} catch (Exception e) {
			String err = "Unable to get Data Objects List! ";
			org.apache.log4j.Logger.getLogger(HierarchicalDWR.class).error(err,
					e);
			throw new Exception(e.getLocalizedMessage());
		}
	}

	

    public ListRange getPossiblePivotsList(int start, int limit,String sort,String dir,String regex) throws Exception{

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
	
	public ListRange getTitleList(int start, int limit,String sort,String dir,String regex) throws Exception {
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
			String dataModelName = config.getModel();
			
			HierarchicalTreeCriterion criterion=new HierarchicalTreeCriterion(dataClusterName,dataModelName,dataObjectName, pivotPath,titleFieldPath, filters);
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
    
    public boolean updateHierarchicalTreeCriterionAndUpdateHistory(String dataObjectName,String pivotPath,String titleFieldPath,FilterItem[] filters,String orderExpr,int limit) {
    	
    	if(updateHierarchicalTreeCriterion(dataObjectName,pivotPath,titleFieldPath,filters,orderExpr,limit)){
    		try {
				resetHierarchicalTreeUpdateHistory();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
    		return true;
    	}
    	
    	return false;
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
    
   public String getDataObjectNameFromHierarchicalTreeCriterion() {
	   
    	String dataObjectName="";
	   
    	WebContext ctx = WebContextFactory.get();
    	Object hierarchicalTreeCriterionObj=ctx.getSession().getAttribute(HierarchicalUtil.HIERARCHICAL_TREE_CRITERION);
    	if(hierarchicalTreeCriterionObj==null){

    	}else{
    		HierarchicalTreeCriterion hierarchicalTreeCriterion=(HierarchicalTreeCriterion)hierarchicalTreeCriterionObj;
    		dataObjectName=hierarchicalTreeCriterion.getDataObjectName();
    	}
    	
        return dataObjectName;
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
    
    public boolean recordSingleChanges(String keys,String xpath,String newValue) {
    	
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
			org.apache.log4j.Logger.getLogger(HierarchicalDWR.class).debug("The size of Update History is: "+updateHistory.size());
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
    
    public boolean saveHierarchicalReport(String reportName,boolean isSharedReport,String dataObjectName,String pivotPath,String titleFieldPath,FilterItem[] filterItems,String orderExpr,int limit) throws Exception {
    	try {
    		
    		if(reportName==null||reportName.length()==0)return false;
    		String owner=Util.getLoginUserName();
    		
	    	Configuration config = Configuration.getInstance();
			String dataClusterName = config.getCluster();
	    	String dataModelName = config.getModel();
	    	
			HierarchicalTreeCriterion criterion=new HierarchicalTreeCriterion(dataClusterName,dataModelName,dataObjectName, pivotPath,titleFieldPath, filterItems);
			HierarchicalUtil.equipDirection2HierarchicalTreeCriterion(criterion, orderExpr);
			HierarchicalUtil.equipLimit2HierarchicalTreeCriterion(criterion, limit);
			
			//build report
			HierarchicalReport hierarchicalReport = new HierarchicalReport();
			hierarchicalReport.setReportName(reportName);//mandatory 
			hierarchicalReport.setOwner(owner);
			hierarchicalReport.setShared(new Boolean(isSharedReport));
			hierarchicalReport.setDataCluster(dataClusterName==null?"":dataClusterName);//mandatory
			hierarchicalReport.setDataModel(dataModelName==null?"":dataModelName);//mandatory
			hierarchicalReport.setDataObjectName(criterion.getDataObjectName()==null?"":criterion.getDataObjectName());//mandatory
			hierarchicalReport.setPivotPath(criterion.getPivotPath()==null?"":criterion.getPivotPath());//mandatory
			hierarchicalReport.setTitleFieldPath(criterion.getTitleFieldPath()==null?"":criterion.getTitleFieldPath());//mandatory
	
			if(criterion.getFilters()!=null&&criterion.getFilters().length>0){
				Filters filters = new Filters(criterion.getFilters());
				hierarchicalReport.setFilters(filters);
			}

			hierarchicalReport.setPivotDirectionsExpr(criterion.obtainPivotDirectionsExpr());
			hierarchicalReport.setIndexDirectionsExpr(criterion.obtainIndexDirectionsExpr());
			hierarchicalReport.setLimit(criterion.getLimit()==Integer.MAX_VALUE?-1:criterion.getLimit());
			
			Util.getPort().putItem(
	                new WSPutItem(
	                                new WSDataClusterPK(XSystemObjects.DC_XTENTIS_COMMON_REPORTING.getName()),
	                                hierarchicalReport.marshal2String(),
	                                new WSDataModelPK(XSystemObjects.DM_XTENTIS_COMMON_REPORTING.getName()),false
	                )
	        );
			
			return true;
		
    	}catch(Exception e){
			String err= "Unable to save a report! ";
			org.apache.log4j.Logger.getLogger(HierarchicalDWR.class).error(err,e);
			throw new Exception(e.getLocalizedMessage());
		}
	}
    
    public Map<String,String> getReportsName(String value) throws XtentisWebappException, Exception {
		WSWhereItem wi = new WSWhereItem();
		
		Configuration config = Configuration.getInstance();
		WSWhereCondition wc1 = new WSWhereCondition(
				"hierarchical-report/data-cluster",
				WSWhereOperator.EQUALS,config.getCluster(),
				WSStringPredicate.NONE,false
				);
		WSWhereCondition wc2 = new WSWhereCondition(
				"hierarchical-report/data-model",
				WSWhereOperator.EQUALS,config.getModel(),
				WSStringPredicate.NONE,false
				);
		
		WSWhereCondition wc3 = new WSWhereCondition(
				"hierarchical-report/owner",
				WSWhereOperator.EQUALS,Util.getAjaxSubject().getUsername(),
				WSStringPredicate.NONE,false
				);
		WSWhereCondition wc4 = new WSWhereCondition(
				"hierarchical-report/shared",
				WSWhereOperator.EQUALS,"true",
				WSStringPredicate.OR,false
				);
		
		WSWhereOr or=new WSWhereOr(new WSWhereItem[] {new WSWhereItem(wc3,null,null),new WSWhereItem(wc4,null,null)});
		
		WSWhereAnd and =new WSWhereAnd(new WSWhereItem[] {new WSWhereItem(wc1,null,null),new WSWhereItem(wc2,null,null),new WSWhereItem(null,null,or)});
		
		wi=new WSWhereItem(null,and,null);
		
		String[] results = Util.getPort().xPathsSearch(
				new WSXPathsSearch(
					new WSDataClusterPK(XSystemObjects.DC_XTENTIS_COMMON_REPORTING.getName()),
					null,//pivot
					new WSStringArray(new String[] {"hierarchical-report/report-name"}),
					wi,
					-1,
					0,
					Integer.MAX_VALUE,
					null, //order by
					null, //direction
                    false
				)
			).getStrings();
		
		Map<String,String> map = new HashMap<String,String>();
		for (int i = 0; i < results.length; i++) {
			results[i] = results[i].replaceAll("<report-name>(.*)</report-name>", "$1");
			map.put(results[i],results[i]);
		}
		return map;
	}
    
    public HierarchicalReport getReport(String reportName) throws XtentisWebappException, Exception{
    	
    	String result = Util.getPort().getItem(new WSGetItem(new WSItemPK(new WSDataClusterPK(XSystemObjects.DC_XTENTIS_COMMON_REPORTING.getName()),"hierarchical-report",new String[] {reportName}))).getContent();
    	
		if(result!=null){
			HierarchicalReport report = HierarchicalReport.unmarshal2POJO(result);
			return report;
		}
		else{
			return null;
		}		
	}

}
