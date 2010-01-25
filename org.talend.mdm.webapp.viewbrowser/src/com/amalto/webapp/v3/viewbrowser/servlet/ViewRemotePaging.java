package com.amalto.webapp.v3.viewbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.v3.viewbrowser.bean.View;

/**
 * 
 * @author asaintguilhem
 *
 *serve data to a json grid
 */

public class ViewRemotePaging  extends HttpServlet{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Pattern highlightLeft = Pattern.compile("\\s*__h");
	private static Pattern highlightRight = Pattern.compile("h__\\s*");
	private static Pattern emptyTags = Pattern.compile("\\s*<(.*?)\\/>\\s*");
	private static Pattern openingTags = Pattern.compile("\\s*<([^\\/].*?[^\\/])>\\s*");
	private static Pattern closingTags = Pattern.compile("\\s*</(.*?)>\\s*");
	private static Pattern firstTag = Pattern.compile("<result>");
	private static Pattern lastTag = Pattern.compile("</result>");
	
	public ViewRemotePaging() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		org.apache.log4j.Logger.getLogger(this.getClass()).info("SERVLET VIEW REMOTE PAGING");
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		Configuration config = null;
		try {
			config = (Configuration)request.getSession().getAttribute("configuration");
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
		
		
		JSONObject json = new JSONObject();
		int totalCount=0;
		String[] results;
		ArrayList<String[]> viewBrowserContent = new ArrayList<String[]>();
		
		try {
			View view = new View(viewName);
			int max = Integer.parseInt(limit);
			int skip = Integer.parseInt(start);		
		
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
			"doPost() case : new remote items call");
			ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>();
			WSWhereItem wi;
			String[] filters = criteria.split(",");
			String[] filterXpaths = new String[filters.length];
			String[] filterOperators = new String[filters.length];
			String[] filterValues = new String[filters.length];

			
			for (int i = 0; i < filters.length; i++) {
				System.out.println(filters[i]);
				filterXpaths[i] = filters[i].split("#")[0];
				filterOperators[i] = filters[i].split("#")[1];
				filterValues[i] = filters[i].split("#")[2];
			}
			for(int i=0;i<filterValues.length;i++){
				if (filterValues[i]==null || "*".equals(filterValues[i]) || "".equals(filterValues[i])) continue;
				//if("CONTAINS".equals(filterOperators[i])) filterValues[i] = filterValues[i];
				if("Any field".equals(filterXpaths[i])) filterXpaths[i] = "";
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
//					results = Util.getPort().singleSearch(
//			    			new WSSingleSearch(
//		    					new WSDataClusterPK(config.getCluster()),
//								new WSViewPK(viewName),
//								wi,  //where
//								0, //treshold
//								0, //skip
//								Integer.MAX_VALUE //max items
//								)
//							).getStrings();
				results = Util.getPort().viewSearch(
					new WSViewSearch(
						new WSDataClusterPK(config.getCluster()),
						new WSViewPK(viewName),
						wi,
						-1,
						0,
						-1,
						null,
						null
					)
				).getStrings();
				

			String[] results2 = new String[results.length];
			System.arraycopy(results, 0, results2, 0, results.length);
			request.getSession().setAttribute("resultsXML",results2);
			
			String[] viewables = new View(viewName).getViewablesXpath();
			for (int i = 0; i < results.length; i++) {
			   //yin guo fix bug 0010867. the totalCountOnfirstRow is true.
               if(i == 0) {
                  totalCount = Integer.parseInt(Util.parse(results[i]).
                     getDocumentElement().getTextContent());
                  continue;
               }
               
               results[i] = results[i].replaceAll("<result>","");
               results[i] = results[i].replaceAll("</result>","");	
               results[i] =highlightLeft.matcher(results[i]).replaceAll(" ");
               results[i] =highlightRight.matcher(results[i]).replaceAll(" ");
               results[i] =openingTags.matcher(results[i]).replaceAll("");
               results[i] =closingTags.matcher(results[i]).replaceAll("#");	
               results[i] =emptyTags.matcher(results[i]).replaceAll(" #");
               String[] elements = results[i].split("#");
               String[] fields = new String[viewables.length+1];
               fields[0]=""+i;
               
               for (int j = 0; j < elements.length; j++) {
                  fields[j+1]=elements[j];
               }
               
               viewBrowserContent.add(fields);
			}				

			
			request.getSession().setAttribute("viewBrowserContent",viewBrowserContent);
			
			/*
			for(int i=0;i<results.length;i++){
				results[i] = firstTag.matcher(results[i]).replaceAll("");
				results[i] = lastTag.matcher(results[i]).replaceAll("");
				results[i] =highlightLeft.matcher(results[i]).replaceAll(" ");
				results[i] =highlightRight.matcher(results[i]).replaceAll(" ");
				results[i] =emptyTags.matcher(results[i]).replaceAll("[$1]");
				results[i] =openingTags.matcher(results[i]).replaceAll("$1: ");
				results[i] =closingTags.matcher(results[i]).replaceAll(" - ");
				//results[i] = StringEscapeUtils.unescapeXml(results[i]);
			}	
			request.getSession().setAttribute("results",results);*/
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"doPost() Total result = "+totalCount);
			
			//sort arraylist
			int tmp = 0;
			for (int i = 0; i < view.getViewablesXpath().length; i++) {
				org.apache.log4j.Logger.getLogger(this.getClass())
				.debug("doPost() sortCol "+sortCol+" "+view.getViewablesXpath()[i]+" "+i);
				if(sortCol.equals("/"+view.getViewablesXpath()[i])){
					tmp = i+1;
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
			request.getSession().setAttribute("sortColItems",sortCol);
			request.getSession().setAttribute("sortDirItems",sortDir);
			Collections.sort(viewBrowserContent, sort);
		
			
			
			//get part we are interested
			if(max>totalCount) max=totalCount;
			if(max>(totalCount-skip)) {max=totalCount-skip;}
	
			
			json.put("TotalCount",totalCount);
			
			ArrayList<JSONObject> rows = new ArrayList<JSONObject>();
			for(int i=skip;i<(max+skip);i++){
				JSONObject fields = new JSONObject();
				fields.put("id",viewBrowserContent.get(i)[0]);
				for (int j = 0; j < view.getViewablesXpath().length; j++) {
					fields.put("/"+view.getViewablesXpath()[j],viewBrowserContent.get(i)[j+1]);
				}
				rows.add(fields);
			}	
			/*
			ArrayList<JSONObject> rows = new ArrayList<JSONObject>();
			for(int i=skip;i<(max+skip);i++){
				JSONObject fields = new JSONObject();
				fields.put(""+0,results[i]);
				rows.add(fields);
			}			*/
			json.put("view",rows);
			

			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					json);
			
		} catch (XtentisWebappException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		PrintWriter writer = response.getWriter();
        writer.write(json.toString());
        writer.close();
        
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
