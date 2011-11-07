package com.amalto.webapp.v3.reporting.dwr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultText;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSExecuteStoredProcedure;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetStoredProcedure;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSRegexStoredProcedure;
import com.amalto.webapp.util.webservices.WSStoredProcedurePK;
import com.amalto.webapp.util.webservices.WSString;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.amalto.webapp.v3.reporting.bean.Reporting;
import com.amalto.webapp.v3.reporting.bean.ReportingContent;
import com.amalto.webapp.v3.reporting.bean.ReportingField;
import com.amalto.webapp.v3.reporting.bean.ReportingFilter;


/**
 * 
 * @author asaintguilhem
 *
 */
public class ReportingDWR {


	public ReportingDWR() {
		super();
	}
	
	private static final String _reportingCluster = XSystemObjects.DC_XTENTIS_COMMON_REPORTING.getName();
	private static final String _reportingModel = XSystemObjects.DM_XTENTIS_COMMON_REPORTING.getName();
	
		
	public Map<String,String> getReportingsName(String value) throws XtentisWebappException, Exception {
		WSWhereItem wi = new WSWhereItem();
		
		WSWhereCondition wc = new WSWhereCondition(
				"Reporting/Owner",
				WSWhereOperator.EQUALS,Util.getAjaxSubject().getUsername(),
				WSStringPredicate.OR,false
				);
		WSWhereCondition wc2 = new WSWhereCondition(
				"Reporting/Shared",
				WSWhereOperator.EQUALS,"true",
				WSStringPredicate.OR,false
				);
		/*ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>();
		conditions.add(new WSWhereItem(wc,null,null));
		conditions.add(new WSWhereItem(wc2,null,null));		
		WSWhereOr or=new WSWhereOr((WSWhereItem[])conditions.toArray(new WSWhereItem[conditions.size()]));*/
		WSWhereOr or=new WSWhereOr(new WSWhereItem[] {new WSWhereItem(wc,null,null),new WSWhereItem(wc2,null,null)});
		
		wi=new WSWhereItem(null,null,or);
		
		String[] results = Util.getPort().xPathsSearch(
				new WSXPathsSearch(
					new WSDataClusterPK(_reportingCluster),
					null,//pivot
					new WSStringArray(new String[] {"Reporting/ReportingName"}),
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
			results[i] = results[i].replaceAll("<ReportingName>(.*)</ReportingName>", "$1");
			map.put(results[i],results[i]);
		}
		return map;
	}
	
	public Reporting getReporting(String reportingName) throws XtentisWebappException, Exception{
    	
    	String result = Util.getPort().getItem(new WSGetItem(new WSItemPK(new WSDataClusterPK(_reportingCluster),"Reporting",new String[] {reportingName}))).getContent();
    	
		if(result!=null){
			Reporting reporting = Reporting.parse(result);
			if(!Util.getAjaxSubject().getUsername().equals(reporting.getOwner()))
				reporting.setErasable(false);
			return reporting;
		}
		else{
			return null;
		}		
	}
	
	public ArrayList<ReportingContent> getReportingContent(String reportingName,String []parameters)  throws XtentisWebappException, Exception{
		
		return (ArrayList<ReportingContent>) getReportingContent(reportingName,parameters,0,Integer.MAX_VALUE,"0","ASC").get(0);
		
	}
	
	//TODO configuration 
	public ArrayList getReportingContent(String reportingName,String []parameters,int skip,int max,String sortCol,String sortDir)  throws XtentisWebappException, Exception{
		
		Reporting reporting = getReporting(reportingName);
		
        ArrayList<ReportingContent> reportingContentList = new ArrayList<ReportingContent>();
		
		String totalCount="-1";
		Boolean usingSP=new Boolean(false);
		
		if (reporting.getStoredProcedure()!=null && !reporting.getStoredProcedure().equals("")) {
			
			usingSP=new Boolean(true);
			//TODO
			//String []parameters = {"/BuyerOrderNumber","8110000202"};
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getReportingContent - execute storedProcedure "+reporting.getStoredProcedure());
			//execute query, replacing $parameters with values coming from input fields
			String[] results = Util.getPort().executeStoredProcedure(
					new WSExecuteStoredProcedure(
							new WSStoredProcedurePK(reporting.getStoredProcedure()),
							null, 
							new WSDataClusterPK(reporting.getCluster()),
							parameters
						)
					).getStrings();
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getReportingContent - execute storedProcedure "+reporting.getStoredProcedure()+" nb result = "+results.length);
			
			for (int i = 0; i < results.length; i++) {
				ReportingContent reportingContent = new ReportingContent();
				//Document document = Util.parse(results[i]);
				//String tmp = "";
				ArrayList<String> field = new ArrayList<String>();
				String document = results[i];
				
				org.apache.log4j.Logger.getLogger(this.getClass()).trace("getReportingContent - result["+i+"] = "+results[i]);

                Document doc = DocumentHelper.parseText(document);
                if (doc != null && doc.getRootElement() != null) {
                    Element root = doc.getRootElement();
                    for (int j = 0; j < root.nodeCount(); j++)
                        if ((root.nodeCount() == 1 && root.node(j) instanceof DefaultText) || root.node(j) instanceof Element) {
                            field.add(root.node(j).getText());
                        }
                }
				
				reportingContent.setFields(field);
				reportingContentList.add(reportingContent);
			
			}		
			
			//TODO
			
		} else {
			ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>();
			String[] filterXpaths = new String[reporting.getFilters().length];
			String[] filterValues = new String[reporting.getFilters().length];
			String[] filterOperators = new String[reporting.getFilters().length];
			for (int i = 0; i < filterXpaths.length; i++) {
				filterXpaths[i]=reporting.getFilters()[i].getXpath();
				filterOperators[i]=reporting.getFilters()[i].getOperator();
				filterValues[i]=reporting.getFilters()[i].getValue();
			}
	    	WSWhereItem wi;
			if(filterXpaths!=null && filterValues!=null)
				for(int i=0;i<filterValues.length;i++){
					if ((filterValues[i]==null) || (filterValues[i]=="*") || "".equals(filterValues[i])) continue;
					//CONTAINS hack to automatically add "*" before and after the value
					//if("CONTAINS".equals(filterOperators[i])) filterValues[i] = "*"+filterValues[i]+"*";
					WSWhereCondition wc=new WSWhereCondition(
							filterXpaths[i],
							getOperator(filterOperators[i]),
							filterValues[i],
							WSStringPredicate.NONE,
							false
							);
					//System.out.println("iterator :"+i+"field - getErrors- : " + fields[i] + " " + operator[i]);
					//System.out.println("Xpath field - getErrors- : " + giveXpath(fields[i]) + " - values : "+ regexs[i]);
					WSWhereItem item=new WSWhereItem(wc,null,null);
					conditions.add(item);
			}		

			if(conditions.size()==0) wi=null;
			else{
				WSWhereAnd and=new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
				wi=new WSWhereItem(null,and,null);
			}
			
			String[] xpaths = new String[reporting.getFields().length];
			String[] fields = new String[reporting.getFields().length];
			for (int i = 0; i < xpaths.length; i++) {
				xpaths[i]=reporting.getFields()[i].getXpath();
				fields[i]=reporting.getFields()[i].getField();
			}
			
			String sortColumn=null;
			if(sortCol!=null)sortColumn=reporting.getFields()[Integer.parseInt(sortCol)].getXpath();
			String sortDirection=null;
			if(sortDir!=null) {
				if(sortDir.equals("ASC")) {
					sortDirection="ascending";
				}else if(sortDir.equals("DESC")) {
					sortDirection="descending";
				}
			}
			
			//Configuration config = Configuration.getInstance();
			String[] results = Util.getPort().xPathsSearch(
					new WSXPathsSearch(
						new WSDataClusterPK(reporting.getCluster()),
						reporting.getPivotXpath(),//pivot
						new WSStringArray(xpaths),
						wi,
						0,
						skip,
						max,
						sortColumn, //order by
						sortDirection, //direction
                        false
					)
				).getStrings();
			
			WSString wsString=Util.getPort().count(new WSCount(new WSDataClusterPK(reporting.getCluster()),reporting.getPivotXpath(),wi,-1));
			totalCount=wsString.getValue();

//			<result>
//		    <Name>IMERYS CANADA - Nanaimo</Name>
//		    <Headcount/>
//		    <LegalEntityName>IMERYS CANADA 2004 INC.</LegalEntityName>
//		    <LegalCode>8215</LegalCode>
//		</result>
			
			

			for (int i = 0; i < results.length; i++) {
				ReportingContent reportingContent = new ReportingContent();
				//Document document = Util.parse(results[i]);
				//String tmp = "";
				ArrayList<String> field = new ArrayList<String>();
				String document = results[i];
                Document doc = DocumentHelper.parseText(document);
                if (doc != null && doc.getRootElement() != null) {
                    Element root = doc.getRootElement();
                    if (root.nodeCount() == 1) {
                        field.add(root.node(0).getText());
                    } else {
                    for (int j = 0; j < root.nodeCount(); j++)
                        if (root.node(j) instanceof Element)
                                field.add(root.node(j).getText());
                    }
				}

				reportingContent.setFields(field);
				reportingContentList.add(reportingContent);
			
			}			
		}
		
		ArrayList resultList=new ArrayList();
        resultList.add(reportingContentList);
        resultList.add((totalCount!=null&&totalCount.equals("-1"))?reportingContentList.size()+"":totalCount);
        resultList.add(usingSP);
	     
		return resultList;
	}
	
	public Map<String,String> getBusinessConcepts(String language){
		TreeMap<String,String> map = new TreeMap<String,String>();
		
		//MDM
		try {
			Configuration config = Configuration.getInstance(true);
			String dataModelPK = config.getModel();
			String[] bc = Util.getPort().getBusinessConcepts(
					new WSGetBusinessConcepts(new WSDataModelPK(dataModelPK)))
					.getStrings();
			String[] bcLabel = new String[bc.length];
			for (int i = 0; i < bc.length; i++) {
				map.put(bc[i],bcLabel[i]=CommonDWR.getConceptLabel(dataModelPK,bc[i],language));
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
		
		return map;
	}
	
	public Map<String, String> getStoredProcedurePKs(){
		
		Map<String, String> storedProcedurePKs = new HashMap<String, String>();
		
		
		try {
			WSStoredProcedurePK[] pkArray=Util.getPort().getStoredProcedurePKs(new WSRegexStoredProcedure(".*")).getWsStoredProcedurePK();
			for (int i = 0; i < pkArray.length; i++) {
				storedProcedurePKs.put(pkArray[i].getPk(), pkArray[i].getPk());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (XtentisWebappException e) {
			e.printStackTrace();
		}
		
		return storedProcedurePKs;
		
	}
	
	public Map<String, String> getXpathToLabel(String dataObject,String language){
		HashMap<String, String> xpathToLabel = new HashMap<String, String>();
		
		//MDM
		try {
			Configuration config = Configuration.getInstance();
			String dataModelPK = config.getModel();
			xpathToLabel = CommonDWR
					.getFieldsByDataModel(dataModelPK, dataObject, language,false);
			xpathToLabel.remove(dataObject);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		
		// sort
		TreeSet<Map.Entry<String,String>> set = new TreeSet<Map.Entry<String,String>>(new Comparator<Map.Entry<String,String>>() {
			public int compare(Map.Entry<String,String> obj, Map.Entry<String,String> obj1) {
				return (obj.getValue()).compareTo(obj1.getValue());
			}
		});
		set.addAll(xpathToLabel.entrySet());
		LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Iterator<Map.Entry<String,String>> i = set.iterator(); i.hasNext();) {
			Map.Entry<String,String> entry = i.next();
			//System.out.println(entry.getKey()+" "+entry.getValue());
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		
        WebContext ctx = WebContextFactory.get();	
        ctx.getSession().setAttribute("xpathToLabel",xpathToLabel);
		return sortedMap;
		
	}
	
	

	/*public String[] getElements(String dataObject,String language){
		try {
			LinkedHashMap<String, String> sortedMap = getXpathToLabel(dataObject, language);	
*/			
			/*TreeSet<Map.Entry> set = new TreeSet<Map.Entry>(new Comparator() {
				public int compare(Object obj, Object obj1) {
					return ((Comparable) ((Map.Entry) obj).getValue())
							.compareTo(((Map.Entry) obj1).getValue());
				}
			});
			set.addAll(xpathToLabel.entrySet());
			LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
			for (Iterator i = set.iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				sortedMap.put((String) entry.getKey(), (String) entry.getValue());
			}
			*/
/*			Collection<String> list = sortedMap.keySet();
			String[] elements = (String[])(list.toArray(new String[list.size()]));
	
			return elements;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
*/
	public void deleteReporting(String reportingName) throws XtentisWebappException, Exception{
		if(getReporting(reportingName).isErasable()==true||(getReporting(reportingName).isErasable()==false&&getReporting(reportingName).isUnErasableButDeleteable()==true)){
			Util.getPort().deleteItem(
				new WSDeleteItem(
					new WSItemPK(
						new WSDataClusterPK(_reportingCluster), 
						"Reporting",
						new String[] {reportingName}
					), false
				)					
			);	
		}
	}
	
	public String saveReporting(Reporting reporting,String concept, String language, boolean edit) throws XtentisWebappException, Exception{
		if(!"".equals(reporting.getReportingName())){
			Configuration config = Configuration.getInstance(true);
			//HashMap<String,String> fields = CommonDWR.getFieldsByDataModel(config.getModel(),concept, language, false);
	        WebContext ctx = WebContextFactory.get();	
	        HashMap<String,String> xpathToLabel = (HashMap<String,String>) ctx.getSession().getAttribute("xpathToLabel");
			//System.out.println(xpathToLabel);
			
	        if(edit==false || getReporting(reporting.getReportingName()).isErasable()==false){
				boolean itemExist = Util.getPort().existsItem(
						new WSExistsItem(
								new WSItemPK(
										new WSDataClusterPK(_reportingCluster),
										"Reporting",
										new String[] {reporting.getReportingName()}
										)
									)
								).is_true();
				System.out.println(itemExist+" "+reporting.getReportingName());
				if(itemExist) {
					return "ERROR1";
				}	        	
	        }

			ReportingField[] reportingFields = reporting.getFields();
			for (int i = 0; i < reportingFields.length; i++) {
				if(xpathToLabel.get(reportingFields[i].getXpath()) != null) {
					reportingFields[i].setField(
					   xpathToLabel.get(reportingFields[i].getXpath()));
				}
			}
			ReportingFilter[] reportingFilters = reporting.getFilters();
			for (int i = 0; i < reportingFilters.length; i++) {
				reportingFilters[i].setField(xpathToLabel.get(reportingFilters[i].getXpath()));
			}
	        //reporting.setFormat(config.getPivotFormat());
			reporting.setPivotField(xpathToLabel.get(reporting.getPivotXpath()));
			reporting.setOwner(Util.getAjaxSubject().getUsername());
			
			
			//b2box
//			try{
//				XMLConfiguration xmlConfig = XMLConfiguration.getInstance();
//				reporting.setCluster(xmlConfig.getDocumentsDataCluster());
//			}
			
			reporting.setCluster(config.getCluster());
			
			System.out.println("config cluster "+config.getCluster());
	        Util.getPort().putItem(
	                new WSPutItem(
	                                new WSDataClusterPK(_reportingCluster),
	                                reporting.serialize(),
	                                new WSDataModelPK(_reportingModel),false
	                )
	        );
	        ctx.getSession().setAttribute("reportingName","");
	        return "OK";
		}	
		return "ERROR2";
	}
	
	/**
	 * gives the operator associated to the string 'option'
	 * @param option
	 * @return
	 */
	private WSWhereOperator getOperator(String option){
		WSWhereOperator res = null;
		if (option.equalsIgnoreCase("CONTAINS"))
			res = WSWhereOperator.CONTAINS;
		else if (option.equalsIgnoreCase("EQUALS"))
			res = WSWhereOperator.EQUALS;
		else if (option.equalsIgnoreCase("GREATER_THAN"))
			res = WSWhereOperator.GREATER_THAN;
		else if (option.equalsIgnoreCase("GREATER_THAN_OR_EQUAL"))
			res = WSWhereOperator.GREATER_THAN_OR_EQUAL;
		else if (option.equalsIgnoreCase("JOIN"))
			res = WSWhereOperator.JOIN;
		else if (option.equalsIgnoreCase("LOWER_THAN"))
			res = WSWhereOperator.LOWER_THAN;
		else if (option.equalsIgnoreCase("LOWER_THAN_OR_EQUAL"))
			res = WSWhereOperator.LOWER_THAN_OR_EQUAL;
		else if (option.equalsIgnoreCase("NOT_EQUALS"))
			res = WSWhereOperator.NOT_EQUALS;
		else if (option.equalsIgnoreCase("STARTSWITH"))
			res = WSWhereOperator.STARTSWITH;
		else if (option.equalsIgnoreCase("STRICTCONTAINS"))
			res = WSWhereOperator.STRICTCONTAINS;
		return res;											
	}
	
	/**
	 * Get the number of paramters in specified storedProduce. 
	 * @param name is the specified storedProduce.
	 * @return 
	 */
	public int getNumberOfParamters(String name) throws 
		XtentisWebappException, Exception 
	{
		String proceduce = "";
		
		try {
			WSGetStoredProcedure storedProcedure = 
				new WSGetStoredProcedure(new WSStoredProcedurePK(name));
			proceduce = Util.getPort().getStoredProcedure(storedProcedure).getProcedure();
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
		
		Pattern pattern = Pattern.compile("\\%\\d+");
		Matcher matcher = pattern.matcher(proceduce);
		Set<String> parameters = new HashSet<String>();
		
		while(matcher.find()) {
			parameters.add(matcher.group(0));
		}
		
		return parameters.size();
	}
	
	/**
	 * syn a field to xpathtolabel with web.
	 * @param xpath
	 * @param label
	 */
	public void addFieldsToXpath(String xpath, String label) {
		WebContext ctx = WebContextFactory.get();
		HashMap<String,String> xpathToLabel = (HashMap<String,String>) ctx.getSession().getAttribute("xpathToLabel");
		xpathToLabel.put(xpath, label);
	}
}
