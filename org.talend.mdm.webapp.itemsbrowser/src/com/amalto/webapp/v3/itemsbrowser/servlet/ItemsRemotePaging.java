package com.amalto.webapp.v3.itemsbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSString;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;
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
		//String sortCol= (request.getParameter("sort")!=null?request.getParameter("sort"):"0");
		//String sortDir= (request.getParameter("dir")!=null?request.getParameter("dir"):"ASC");
		String sortCol= null;
		if(request.getParameter("sort")!=null&&request.getParameter("sort").length()>0&&viewFieldValidate(request.getParameter("viewName"),request.getParameter("sort")))sortCol=request.getParameter("sort");
		String sortDir= null;
		if(sortCol!=null&&request.getParameter("dir")!=null&&request.getParameter("dir").length()>0){
			if(request.getParameter("dir").toUpperCase().equals("ASC"))sortDir="ascending";
			if(request.getParameter("dir").toUpperCase().equals("DESC"))sortDir="descending";
		}
		
		String viewName= request.getParameter("viewName");				
		String criteria = request.getParameter("criteria");
		String concept = viewName.replaceAll("Browse_items_","").replaceAll("#.*","");
		
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("doPost() \n" +
				"start : "+start+"\n"+
				"limit : "+limit+"\n"+
				"criteria : "+criteria+"\n"+
				"viewName : "+viewName+"\n"+
				"sortCol : "+(sortCol==null?"":sortCol)+"\n"+
				"sortDir : "+(sortDir==null?"":sortDir));
		
		JSONObject json = new JSONObject();
		String[] results;
		ArrayList<String[]> itemsBrowserContent = new ArrayList<String[]>();
		
		try {
			int max = 50;
			if (limit != null && limit.length() > 0)
				max = Integer.parseInt(limit);
			int skip = 0;
			if (limit != null && limit.length() > 0)
				skip = Integer.parseInt(start);
			View view = new View(viewName); 
			

			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"doPost() case : new remote items call");
			WSWhereItem wi = buildWhereItems(criteria);

			org.apache.log4j.Logger.getLogger(this.getClass()).trace("doPost() starting to search");
			
			//count each time
			WSString totalString=Util.getPort().count(new WSCount(
					new WSDataClusterPK(config.getCluster()),
					concept,
					wi,
            		-1
            	));
			int totalSize=0;
			if(totalString!=null&&totalString.getValue()!=null&&totalString.getValue().length()>0)totalSize=Integer.parseInt(totalString.getValue());
			
			results = Util.getPort().viewSearch(
					new WSViewSearch(
						new WSDataClusterPK(config.getCluster()),
						new WSViewPK(view.getViewPK()),
						wi,
						-1,
						skip,
						max,
						sortCol,
						sortDir
				)
			).getStrings();
			org.apache.log4j.Logger.getLogger(this.getClass()).trace("doPost() end of search");
			

			for (int i = 0; i < results.length; i++) {
				//aiming modify
				List<String> list=null;
				try{
					Element root1 = Util.parse(results[i]).getDocumentElement(); //exist 1.4 return <concept>...</concept>
					if(root1.getLocalName().equals(concept)){
						list=getElementValues("/"+concept,root1);
					}
				}catch(Exception e){}

				if(list==null){
					//aiming modify when there is null value in fields, the viewable fields sequence is the same as the childlist of result
					if(!results[i].startsWith("<result>")){
						results[i]="<result>" + results[i] + "</result>";
					}
					Element root = Util.parse(results[i]).getDocumentElement();
					list=getElementValues("/result",root);
				}
				String[] elements =list.toArray(new String[list.size()]);
				//end
				String[] fields = new String[view.getViewables().length];
				//aiming modify
				int count=Math.min(elements.length, fields.length);
				for (int j = 0; j < count; j++) {
					if(elements[j]!=null)
						fields[j]=StringEscapeUtils.unescapeXml(elements[j]);
					else
						fields[j]="";
				}				
				itemsBrowserContent.add(fields);
			}				

			//int totalCount = (Integer)request.getSession().getAttribute("totalCountItems");
			int totalCount = totalSize;
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"doPost() Total result = "+totalCount);


			/**
			 * sort the collections		
			 */
			int col = getSortCol(view.getViewables(),sortCol);
			if(checkDigist(itemsBrowserContent,col)){
				sortCollections(itemsBrowserContent,col, sortDir);
			}
					
			
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
	
	public WSWhereItem buildWhereItems(String criteria) throws Exception{
		String[] criterias = criteria.split("[\\s]+OR[\\s]+");
		ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>(); 
		
		for (String cria: criterias)
		{
			ArrayList<WSWhereItem> condition=new ArrayList<WSWhereItem>(); 
			String[] subCriterias = cria.split("[\\s]+AND[\\s]+");
			for (String subCria : subCriterias) {
				if (subCria.startsWith("(")) {
					subCria = subCria.substring(1);
				}
				if (subCria.endsWith(")")) {
					subCria = subCria.substring(0, subCria.length() - 1);
				}

				WSWhereItem whereItem = buildWhereItem(subCria);
				condition.add(whereItem);
			}
			if (condition.size() > 0) {
				WSWhereAnd and = new WSWhereAnd(condition
						.toArray(new WSWhereItem[condition.size()]));
				WSWhereItem whand = new WSWhereItem(null,and,null);
				conditions.add(whand);
			}
		}
		WSWhereOr or = new WSWhereOr(conditions.toArray(new WSWhereItem[conditions.size()]));
		WSWhereItem wi = new WSWhereItem(null,null,or);
		
		return wi;
	}
	
	private WSWhereItem buildWhereItem(String criteria) throws Exception{
		WSWhereItem wi;
		String[] filters = criteria.split(" ");
		String filterXpaths, filterOperators ,filterValues ;

		filterXpaths = filters[0];
		filterOperators = filters[1];
		if (filters.length <= 2)
		    filterValues = " ";
		else
			filterValues = filters[2];

		WSWhereCondition wc=new WSWhereCondition(
				filterXpaths,
				getOperator(filterOperators),
				filterValues,
				WSStringPredicate.NONE,
				false
				);
		//System.out.println("iterator :"+i+"field - getErrors- : " + fields[i] + " " + operator[i]);
		//System.out.println("Xpath field - getErrors- : " + giveXpath(fields[i]) + " - values : "+ regexs[i]);
		ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>();
		WSWhereItem item=new WSWhereItem(wc,null,null);
		conditions.add(item);
						
		if(conditions.size()==0) { 
			wi=null;
		} else {
			WSWhereAnd and=new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
			wi=new WSWhereItem(null,and,null);
		}
		
		
		return wi;
		
	}
	private  List<String> getElementValues(String parentPath,Node n)throws Exception{
		List<String> l=new ArrayList<String>();
		NodeList list=n.getChildNodes();
		for(int i=0; i<list.getLength(); i++){
			Node node=list.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				//String nName=node.getNodeName();
				//String xPath=parentPath+"/"+nName;
				String nValue=com.amalto.core.util.Util.getFirstTextNode(node, ".");
				if( !hasChildren(node)){
					l.add(nValue);
				}
			}
		}		
		return l;
	}
	private static boolean hasChildren(Node node){
		NodeList list=node.getChildNodes();
		for(int i=0; i<list.getLength(); i++){
			if(list.item(i).getNodeType() == Node.ELEMENT_NODE){
				return true;
			}
		}
		return false;
	}	
	private boolean viewFieldValidate(String viewName,String fieldPath) {
		
		if(viewName!=null&&viewName.length()>0&&fieldPath!=null&&fieldPath.length()>0){
			if(viewName.matches("Browse_items_.*")){
				String concept = viewName.replaceAll("Browse_items_","").replaceAll("#.*","");
				if(fieldPath.startsWith(concept)||fieldPath.startsWith("/"+concept)||fieldPath.startsWith("//"+concept))return true;
			}
		}
		
		return false;

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
	 * check the certain column is digit
	 * @author ymli
	 * @param itemsBrowserContent
	 * @param col
	 * @return
	 */
private boolean checkDigist(ArrayList<String[]> itemsBrowserContent,int col){
	if(col==-1)return false;
	for(String[] temp:itemsBrowserContent){
		if(!temp[col].matches("^(-|)[0-9]*(.?)[0-9]*$"))
			return false;
	}
	return true;
}
/**
 * sort the ArrayList by col in direction of dir 
 * @author ymli
 * @param itemsBrowserContent
 * @param col
 * @param dir
 */
 private void sortCollections(ArrayList<String[]> itemsBrowserContent,int col, String dir) {
		System.out.println(dir);
		if (col < 0)
			return;
		if ("descending".equals(dir)) {
			for (int j = 1; j < itemsBrowserContent.size(); j++) {
				String temp[] = itemsBrowserContent.get(j);
				int i = j;
				while (i > 0 
						&& (itemsBrowserContent.get(i-1)[col].length()==0 
								||(itemsBrowserContent.get(i-1)[col].length()>0 &&temp[col].length()>0 
										&& Double.parseDouble(itemsBrowserContent.get(i-1)[col]) < Double.parseDouble(temp[col])))) {
					itemsBrowserContent.set(i, itemsBrowserContent.get(i-1));
					i--;
				}
				itemsBrowserContent.set(i, temp);
			}
		} else {
			for (int j = 1; j < itemsBrowserContent.size(); j++) {
				String temp[] = itemsBrowserContent.get(j);
				int i = j;
					while ((i > 0 && itemsBrowserContent.get(i-1)[col].length()>0&&temp[col].length()>0&&Double.parseDouble(itemsBrowserContent.get(i-1)[col]) > Double.parseDouble(temp[col]))
							||i>0&&temp[col].length()==0) {
						itemsBrowserContent.set(i, itemsBrowserContent.get(i-1));
						i--;
					}
					itemsBrowserContent.set(i, temp);
				
			}
		}
	}
	/**
	 * get the column number of the certain title in Array columns
	 * @author ymli
	 * @param columns
	 * @param title
	 * @return
	 */
 private int getSortCol(String[] columns, String title) {
		int col = -1;
		for (int i = 0; i < columns.length; i++)
			if (("/"+columns[i]).equals(title))
				return i;
		return col;
	}
 
}
