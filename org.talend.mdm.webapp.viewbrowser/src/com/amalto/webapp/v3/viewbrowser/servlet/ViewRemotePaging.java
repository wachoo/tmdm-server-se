package com.amalto.webapp.v3.viewbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		
		String viewName= request.getParameter("viewName");				
		String criteria = request.getParameter("criteria");
		int max = 50;
		if (limit != null && limit.length() > 0)
			max = Integer.parseInt(limit);
		int skip = 0;
		if (limit != null && limit.length() > 0)
			skip = Integer.parseInt(start);	
		String sortDir= null;
		String sortCol= null;
		if(request.getParameter("sort")!=null&&request.getParameter("sort").length()>0)sortCol=request.getParameter("sort");
		if(sortCol!=null&&request.getParameter("dir")!=null&&request.getParameter("dir").length()>0){
			if(request.getParameter("dir").toUpperCase().equals("ASC"))sortDir="ascending";
			if(request.getParameter("dir").toUpperCase().equals("DESC"))sortDir="descending";
		}
		
		JSONObject json = new JSONObject();
		int totalCount=0;
		String[] results;
		ArrayList<String[]> viewBrowserContent = new ArrayList<String[]>();
		
		try {
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
						Util.getOperator(filterOperators[i]),
						filterValues[i],
						WSStringPredicate.NONE,
						false
						);
				WSWhereItem item=new WSWhereItem(wc,null,null);
				conditions.add(item);
			}				
			if(conditions.size()==0) {
				wi=null;
			} else {
				WSWhereAnd and=new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
				wi=new WSWhereItem(null,and,null);
			}
				results = Util.getPort().viewSearch(
					new WSViewSearch(
						new WSDataClusterPK(config.getCluster()),
						new WSViewPK(viewName),
						wi,
						-1,
						skip,
						max,
						sortCol,
						sortDir
					)
				).getStrings();
				

			String[] results2 = new String[results.length-1];
			System.arraycopy(results, 1, results2, 0, results.length-1);
			request.getSession().setAttribute("resultsXML",results2);
			
			String[] viewables = new View(viewName).getViewablesXpath();
			for (int i = 0; i < results.length; i++) {
			   //yin guo fix bug 0010867. the totalCountOnfirstRow is true.
               if(i == 0) {
                  totalCount = Integer.parseInt(Util.parse(results[i]).
                     getDocumentElement().getTextContent());
                  continue;
               }              
				List<String> list=null;
				if(list==null){
					//aiming modify when there is null value in fields, the viewable fields sequence is the same as the childlist of result
					if(!results[i].startsWith("<result>")){
						results[i]="<result>" + results[i] + "</result>";
					}
					Element root = Util.parse(results[i]).getDocumentElement();
					list=Util.getElementValues("/result",root);
				}
				String[] elements =list.toArray(new String[list.size()]);
				//end
				String[] fields = new String[viewables.length];
				//aiming modify
				int count=Math.min(elements.length, fields.length);
				for (int j = 0; j < count; j++) {
					if(elements[j]!=null)
						fields[j]=StringEscapeUtils.unescapeXml(elements[j]);
					else
						fields[j]="";
				}				

               viewBrowserContent.add(fields);
			}						
			request.getSession().setAttribute("viewBrowserContent",viewBrowserContent);

			//get part we are interested
			if(max>totalCount) max=totalCount;
			if(max>(totalCount-skip)) {max=totalCount-skip;}	
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"doPost() starting to build json object");
			json.put("TotalCount",totalCount);
			ArrayList<JSONObject> rows = new ArrayList<JSONObject>();
			for(int i=skip;i<(max+skip);i++){
				int index= i-skip;
				if(index > viewBrowserContent.size()-1 ) break;
				JSONObject fields = new JSONObject();
				fields.put("id",index);
				for (int j = 0; j < viewBrowserContent.get(index).length; j++) {
					fields.put("/"+viewables[j],viewBrowserContent.get(index)[j]);
				}
				rows.add(fields);
			}	

			json.put("view",rows);
			//aiming add 'success' to let the search result can display after get the results
			json.put("success", true);
			
		}  catch (Exception e) {
			PrintWriter writer = response.getWriter();
	        writer.write(e.getLocalizedMessage());
	        writer.close();				
			throw new ServletException(e.getLocalizedMessage());
		}

		PrintWriter writer = response.getWriter();
        writer.write(json.toString());
        writer.close();
        
	}

}
