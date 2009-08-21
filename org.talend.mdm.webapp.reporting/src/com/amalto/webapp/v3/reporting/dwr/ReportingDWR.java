package com.amalto.webapp.v3.reporting.dwr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.XMLConfiguration;
import com.amalto.webapp.core.bean.XMLConfiguration_Document;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XSystemObjects;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSExecuteStoredProcedure;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSExistsView;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSPutView;
import com.amalto.webapp.util.webservices.WSStoredProcedurePK;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
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
import com.amalto.webapp.v3.reporting.bean.Translation;
import com.amalto.webapp.v3.reporting.bean.TranslationField;


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
				WSStringPredicate.NONE,false
				);
		WSWhereCondition wc2 = new WSWhereCondition(
				"Reporting/Shared",
				WSWhereOperator.EQUALS,"true",
				WSStringPredicate.NONE,false
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
					null //direction
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
	
	//TODO configuration 
	public ArrayList<ReportingContent> getReportingContent(String reportingName,String []parameters)  throws XtentisWebappException, Exception{
		
		Reporting reporting = getReporting(reportingName);
		
		ArrayList<ReportingContent> reportingContentList = new ArrayList<ReportingContent>();
		Pattern p = Pattern.compile("<(.*?)>(.*?)<\\/\\1>",Pattern.DOTALL);
		Pattern p2 = Pattern.compile("<[^\\/].*?\\/>",Pattern.DOTALL);
		
		
		if (reporting.getStoredProcedure()!=null && !reporting.getStoredProcedure().equals("")) {
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
				
				
				document = document.replaceAll("<result>","");
				document = document.replaceAll("</result>","");
				document = p.matcher(document).replaceAll("$2###");
				document = p2.matcher(document).replaceAll("###");
				document = document.replaceAll("\n","");
				String[] documentArray = document.split("###");
				
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("getReportingContent - result["+i+"] après replace = "+document);
				
				for (int j = 0; j < documentArray.length; j++) {	
					field.add(documentArray[j]);
					//String tmp = Util.getFirstTextNode(document,"//"+xpaths[j].split("/")[xpaths[j].split("/").length-1].replaceAll("@",""));
					//if(tmp==null) tmp ="";
					//field.add(tmp);
					//tmp += Util.getFirstTextNode(document,"result/"+xpaths[j].split("/")[xpaths[j].split("/").length-1])+"###";
					//tmp += Util.getFirstTextNode(document,xpaths[j])+"###";
				}
//				tmp = tmp.replaceAll("__h"," ");
//				tmp = tmp.replaceAll("h__"," ");
//				System.out.println(tmp);
				//results2[i] = tmp;
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
			
			//Configuration config = Configuration.getInstance();
			String[] results = Util.getPort().xPathsSearch(
					new WSXPathsSearch(
						new WSDataClusterPK(reporting.getCluster()),
						reporting.getPivotXpath(),//pivot
						new WSStringArray(xpaths),
						wi,
						0,
						0,
						Integer.MAX_VALUE,
						null, //order by
						null //direction
					)
				).getStrings();

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
				document = document.replaceAll("<result>","");
				document = document.replaceAll("</result>","");
				document = p.matcher(document).replaceAll("$2###");
				document = p2.matcher(document).replaceAll("###");
				document = document.replaceAll("\n","");
				String[] documentArray = document.split("###");
				for (int j = 0; j < documentArray.length; j++) {	
					field.add(documentArray[j]);
					//String tmp = Util.getFirstTextNode(document,"//"+xpaths[j].split("/")[xpaths[j].split("/").length-1].replaceAll("@",""));
					//if(tmp==null) tmp ="";
					//field.add(tmp);
					//tmp += Util.getFirstTextNode(document,"result/"+xpaths[j].split("/")[xpaths[j].split("/").length-1])+"###";
					//tmp += Util.getFirstTextNode(document,xpaths[j])+"###";
				}
//				tmp = tmp.replaceAll("__h"," ");
//				tmp = tmp.replaceAll("h__"," ");
//				System.out.println(tmp);
				//results2[i] = tmp;
				reportingContent.setFields(field);
				reportingContentList.add(reportingContent);
			
			}			
		}
		

	
		return reportingContentList;
	}
	
	public Map<String,String> getBusinessConcepts(String language){
		TreeMap<String,String> map = new TreeMap<String,String>();
		//b2box
		try{
			XMLConfiguration config = XMLConfiguration.getInstance();
			
			Iterator<XMLConfiguration_Document> iter = config.getDocuments().iterator();
			while(iter.hasNext()) {
				XMLConfiguration_Document doc = iter.next();
				map.put(doc.getConceptName(), doc.getName());
			}
		}
		catch(Exception e){
		
		//MDM
		try {
			Configuration config = Configuration.getInstance();
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
		
		}
		return map;
	}
	
	public Map<String, String> getXpathToLabel(String dataObject,String language){
		HashMap<String, String> xpathToLabel = new HashMap<String, String>();
		
		
		//b2box
		try{
			XMLConfiguration config = XMLConfiguration.getInstance();
			String dataModel = config.getDocumentsDataModel();
	    	String view = dataModel+" Translation View";
	    	
	    	if(!(Util.getPort().existsView(new WSExistsView(new WSViewPK(view)))).is_true()){
	    		Util.getPort().putView(
	    				new WSPutView(
	    						new WSView(
	    								view,
	    								"View for translation of "+dataModel,
	    								new String[] {"Translation"},
	    								new WSWhereCondition[] {
	    										new WSWhereCondition("Translation/Format",WSWhereOperator.EQUALS,config.getName(),WSStringPredicate.NONE,false)
	    								},
	    								new String[] {"Translation/Document"}
	    							)
	    						)
	    				);
	    	}
	    	
	    	
			WSWhereItem wi = new WSWhereItem();
	    	//System.out.println("regex : "+documentType);
	    	if ((dataObject==null) || ("*".equals(dataObject)) || "".equals(dataObject)) { 
	    		wi = null;
	    	} else {
	    		WSWhereCondition wc = new WSWhereCondition(
	    				"Translation/Document",
	    				WSWhereOperator.EQUALS,
	    				dataObject,
	    				WSStringPredicate.NONE,
	    				false
	    				);
	    		wi.setWhereCondition(wc);
	    	}
	    	/*if(language.toUpperCase().equals("FR")){
	    		view = "B2BOX Translation FR View";
	    	}*/
			String[] results = Util.getPort().viewSearch(
	    			new WSViewSearch(
	    				new WSDataClusterPK("CONF"),
						new WSViewPK(view),
						wi,  //where
						0, //treshold
						0, //skip
						Integer.MAX_VALUE,
						null, //order by
						null //direction
					)
				).getStrings();
			if(results.length>0){
				Translation translation = Translation.parse(results[0]);
				TranslationField[] fields = translation.getTranslationFields();
				if(language.toUpperCase().equals("FR")){
					for (int i = 0; i < fields.length; i++) {					
						xpathToLabel.put(fields[i].getXpath(), fields[i].getFr());
					}
				}
				else{
					for (int i = 0; i < fields.length; i++) {					
						xpathToLabel.put(fields[i].getXpath(), fields[i].getEn());
					}
				}
			}
		}
		catch(Exception e){
		
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
		if(getReporting(reportingName).isErasable()==true){
			Util.getPort().deleteItem(
				new WSDeleteItem(
					new WSItemPK(
						new WSDataClusterPK(_reportingCluster), 
						"Reporting",
						new String[] {reportingName}
					)
				)					
			);	
		}		
	}
	
	public String saveReporting(Reporting reporting,String concept, String language, boolean edit) throws XtentisWebappException, Exception{
		if(!"".equals(reporting.getReportingName())){
			Configuration config = Configuration.getInstance();
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
				reportingFields[i].setField(xpathToLabel.get(reportingFields[i].getXpath()));
			}
			ReportingFilter[] reportingFilters = reporting.getFilters();
			for (int i = 0; i < reportingFilters.length; i++) {
				reportingFilters[i].setField(xpathToLabel.get(reportingFilters[i].getXpath()));
			}
	        //reporting.setFormat(config.getPivotFormat());
			reporting.setPivotField(xpathToLabel.get(reporting.getPivotXpath()));
			reporting.setOwner(Util.getAjaxSubject().getUsername());
			
			//b2box
			try{
				XMLConfiguration xmlConfig = XMLConfiguration.getInstance();
				reporting.setCluster(xmlConfig.getDocumentsDataCluster());
			}
			//MDM
			catch(Exception e){
				reporting.setCluster(config.getCluster());
			}
			System.out.println("config cluster "+config.getCluster());
	        Util.getPort().putItem(
	                new WSPutItem(
	                                new WSDataClusterPK(_reportingCluster),
	                                reporting.serialize(),
	                                new WSDataModelPK(_reportingModel)
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
}
