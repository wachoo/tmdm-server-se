package com.amalto.webapp.v3.itemsbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Util2;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.v3.itemsbrowser.bean.View;

/**
 * 
 * @author asaintguilhem
 *
 *serve data to a json grid
 */

public class ItemsRemotePaging  extends HttpServlet{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Pattern highlightLeft = Pattern.compile("\\s*__h");
	private static Pattern highlightRight = Pattern.compile("h__\\s*");
	private static Pattern emptyTags = Pattern.compile("\\s*<(.*?)\\/>\\s*");
	private static Pattern openingTags = Pattern.compile("\\s*<([^\\/].*?[^\\/])>\\s*");
	private static Pattern closingTags = Pattern.compile("\\s*</(.*?)>\\s*");

	
	public ItemsRemotePaging() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("Remote paging for items");
		
		request.setCharacterEncoding("UTF-8");		
		response.setContentType("text/html; charset=UTF-8");

		Configuration config = null;
		try {
			org.apache.log4j.Logger.getLogger(this.getClass()).trace("doPost() session items "+request.getSession().getId());
			config = (Configuration)request.getSession().getAttribute("configuration");
			if(config==null) {
				org.apache.log4j.Logger.getLogger(this.getClass()).debug(
						"doPost() config items null");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*Enumeration param = request.getParameterNames();
		while(param.hasMoreElements()){
			System.out.println(param.nextElement().toString());
		}*/
		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		String sortCol= (request.getParameter("sort")!=null?request.getParameter("sort"):"0");
		String sortDir= (request.getParameter("dir")!=null?request.getParameter("dir"):"ASC");
		
		String viewName= request.getParameter("viewName");				
		String criteria = request.getParameter("criteria");
		String concept = viewName.replaceAll("Browse_items_","").replaceAll("#.*","");
		
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("doPost() \n" +
				"start : "+start+"\n"+
				"limit : "+limit+"\n"+
				"criteria : "+criteria+"\n"+
				"viewName : "+viewName+"\n"+
				"sortCol : "+sortCol+"\n"+
				"sortDir : "+sortDir);
		
		JSONObject json = new JSONObject();
		String[] results;
		ArrayList<String[]> itemsBrowserContent = new ArrayList<String[]>();
		
		try {
			
			int max = Integer.parseInt(limit);
			int skip = Integer.parseInt(start);		
			View view = new View(viewName); 
			

			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"doPost() case : new remote items call");
			ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>();
			WSWhereItem wi;
			String[] filters = criteria.split(",");
			String[] filterXpaths = new String[filters.length];
			String[] filterOperators = new String[filters.length];
			String[] filterValues = new String[filters.length];

			
			for (int i = 0; i < filters.length; i++) {
				if(filters[i].split("#").length>2){
					filterXpaths[i] = filters[i].split("#")[0];
					filterOperators[i] = filters[i].split("#")[1];
					filterValues[i] = filters[i].split("#")[2];
				}					
			}
			for(int i=0;i<filterValues.length;i++){
				if ((filterValues[i]==null) || ("*".equals(filterValues[i])) || "".equals(filterValues[i])) {
					continue;
				}
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
			if(conditions.size()==0) { 
				wi=null;
			} else {
				WSWhereAnd and=new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
				wi=new WSWhereItem(null,and,null);
			}
			if("Any field".equals(filterXpaths[0])){
				//System.out.println("Any field");
				results = Util.getPort().viewSearch(
						new WSViewSearch(
								new WSDataClusterPK(config.getCluster()),
								new WSViewPK(view.getViewPK()),
								wi,
								-1,
								skip,
								max,
								null,
								null
						)
					).getStrings();
			} else {
				org.apache.log4j.Logger.getLogger(this.getClass()).trace("doPost() starting to search");
				
				results = Util.getPort().viewSearch(
						new WSViewSearch(
							new WSDataClusterPK(config.getCluster()),
							new WSViewPK(view.getViewPK()),
							wi,
							-1,
							skip,
							max,
							null,
							null
					)
				).getStrings();
				org.apache.log4j.Logger.getLogger(this.getClass()).trace("doPost() end of search");
			}



			for (int i = 0; i < results.length; i++) {
				//aiming modify
				results[i] = results[i].replaceAll("<result>","<"+ concept+">");
				results[i] = results[i].replaceAll("</result>","</"+ concept+">");
				//add concept root in case results[i] don't contains concept root
				if(results[i].indexOf("<"+ concept+">") ==-1){
					results[i]="<"+ concept+">"+results[i]+"</"+ concept+">";
				}
				Element root = Util.parse(results[i]).getDocumentElement();
				HashMap<String, String> vMap=com.amalto.core.util.Util.getElementValueMap("/"+concept, root);
				String[] fields = new String[view.getViewables().length];
				for(int j=0; j<view.getViewables().length; j++){
					fields[j]=vMap.get("/"+view.getViewables()[j]);
				}
//				results[i] = results[i].replaceAll("<result>","");
//				results[i] = results[i].replaceAll("</result>","");	
////					results[i] =highlightLeft.matcher(results[i]).replaceAll(" ");
////					results[i] =highlightRight.matcher(results[i]).replaceAll(" ");
//				results[i] =openingTags.matcher(results[i]).replaceAll("");
//				results[i] =closingTags.matcher(results[i]).replaceAll("#");	
//				results[i] =emptyTags.matcher(results[i]).replaceAll(" #");
//				String[] elements = results[i].split("#");
//				String[] fields = new String[view.getViewables().length];
//				//aiming modify
//				int count=Math.min(elements.length, fields.length);
//				for (int j = 0; j < count; j++) {
//					if(elements[j]!=null)
//					fields[j]=StringEscapeUtils.unescapeXml(elements[j]);
//				}				
				itemsBrowserContent.add(fields);
			}				

			int totalCount = (Integer)request.getSession().getAttribute("totalCountItems");
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"doPost() Total result = "+totalCount);

			//sort arraylist
			int tmp = 0;
			for (int i = 0; i < view.getViewables().length; i++) {
				org.apache.log4j.Logger.getLogger(this.getClass())
				.debug("doPost() sortCol "+sortCol+" "+view.getViewables()[i]+" "+i);
				if(sortCol.equals("/"+view.getViewables()[i])){
					tmp = i;
					break;
				}

			}

			final int column = tmp;
			final String direction = sortDir;
			Comparator sort;
			if(direction.equals("ASC")){
				sort = new Comparator() {
					  public int compare(Object o1, Object o2) {
						  try{
							  Double test= ( Double.parseDouble(((String[]) o1)[column])-
									  			Double.parseDouble(((String[]) o2)[column]));
							  return test.intValue();
						  }
						  catch(Exception e){}
						  try{
							  return (((String[]) o1)[column]).compareTo(((String[]) o2)[column]);
						  }						  
						  catch(Exception e){return 0;}
					  }
					};
			}
			else{
				sort = new Comparator() {
					  public int compare(Object o1, Object o2) {
						try{
							Double test= ( Double.parseDouble(((String[]) o2)[column])-
						  			Double.parseDouble(((String[]) o1)[column]));
							return test.intValue();
						}
						catch(Exception e){}
						try{
							return (((String[]) o2)[column]).compareTo(((String[]) o1)[column]);
						}
						
						catch(Exception e){return 0;}
					  }
					};			
			}
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"doPost() sorting the result...");
			//Collections.sort(itemsBrowserContent, sort);
					
			
			//get part we are interested
			if(max>totalCount) max=totalCount;
			if(max>(totalCount-skip)) {max=totalCount-skip;}	
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"doPost() starting to build json object");
			json.put("TotalCount",totalCount);
			ArrayList<JSONObject> rows = new ArrayList<JSONObject>();
			for(int i=skip;i<(max+skip);i++){
				int index= i-skip;
				if(index > itemsBrowserContent.size()-1 ) break;
				JSONObject fields = new JSONObject();
				for (int j = 0; j < itemsBrowserContent.get(index).length; j++) {
					fields.put("/"+view.getViewables()[j],itemsBrowserContent.get(index)[j]);
				}
				rows.add(fields);
			}			
			json.put("items",rows);
			//aiming add 'success' to let the search result can display after get the results
			json.put("success", true);
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(json);
			
			
		} catch (XtentisWebappException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		PrintWriter writer = response.getWriter();
        writer.write(json.toString());
        writer.close();
        
	}
	
	
	
//	private ArrayList<String[]> getItemsBrowserContent(View view, String[] results) throws Exception{
//		ArrayList<String[]> itemsBrowserContent = new ArrayList<String[]>();
//		String[] viewables = view.getViewables();
//		for (int i = 0; i < results.length; i++) {
//			Document document = Util.parse(results[i]);
//			String tmp = "";
//			String[] field = new String[view.getViewables().length];
//			for (int j = 0; j < viewables.length; j++) {		
//				field[i] = Util.getFirstTextNode(document,viewables[j].replaceAll("@",""));
//			}
//			tmp = tmp.replaceAll("__h","");
//			tmp = tmp.replaceAll("h__","");
//			//System.out.println(tmp);
//			itemsBrowserContent.add(field);
//		}
//		return itemsBrowserContent;
//	}
	
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
