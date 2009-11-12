package com.amalto.webapp.v3.reporting.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.v3.reporting.bean.Reporting;
import com.amalto.webapp.v3.reporting.bean.ReportingContent;
import com.amalto.webapp.v3.reporting.bean.ReportingField;
import com.amalto.webapp.v3.reporting.dwr.ReportingDWR;


/**
 * 
 * @author asaintguilhem
 *
 *serve data to a json grid
 */

public class ReportingRemotePaging  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReportingRemotePaging() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		org.apache.log4j.Logger.getLogger(this.getClass()).info(
				"SERVLET Remote paging for reporting");
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		Enumeration param = request.getParameterNames();
		while(param.hasMoreElements()){
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					param.nextElement().toString() );
		}
		
		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		String sortCol= (request.getParameter("sort")!=null?request.getParameter("sort"):"0");
		String sortDir= (request.getParameter("dir")!=null?request.getParameter("dir"):"ASC");
		String reportingName= request.getParameter("reportingName");
		String parametersValues= request.getParameter("params");
		
		
		if (parametersValues == null)
			parametersValues = "";
		

		org.apache.log4j.Logger.getLogger(this.getClass()).debug("params ="+parametersValues);
		
		boolean splitEnd = false;
		String tmpSplit = parametersValues;
		Vector<String> paramVector = new Vector<String>();
		while (!splitEnd) {
			int indexMatch = tmpSplit.indexOf("###");
			if (indexMatch == -1) {
				paramVector.add(tmpSplit);
				splitEnd = true;
			} else {
				if (indexMatch > 0) {
					String tmpParam = tmpSplit.substring(0, indexMatch);
					paramVector.add(tmpParam);
				} else
					paramVector.add("");
				
				if (indexMatch+3 >= tmpSplit.length())
					tmpSplit = "";
				else
					tmpSplit = tmpSplit.substring(indexMatch+3);
			}
		}
		
		//String []parameters = parametersValues.split("###");
		String []parameters = new String [paramVector.size()];
		for (int i=0; i<paramVector.size(); i++) {
			parameters[i] = paramVector.get(i);
		}
		
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("nb params ="+parameters.length);
		
		
		
		ReportingDWR reportingDWR = new ReportingDWR();		
		JSONObject json = new JSONObject();
		
		Configuration config = null;
		try {
			config = (Configuration)request.getSession().getAttribute("configuration");
			System.out.println("session reporting "+request.getSession().getId());
			if(config==null) {
				System.out.println("config null");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			int totalCount=0;
			int max = Integer.parseInt(limit);
			int skip = Integer.parseInt(start);	
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"max "+max+", skip "+skip);	
			Reporting rep = reportingDWR.getReporting(reportingName);
			ReportingField[] fields = rep.getFields();
			ArrayList<ReportingContent> reportingContentList = new ArrayList<ReportingContent>();			
			
//			if(request.getSession().getAttribute("totalCount")==null 
//					|| !reportingName.equals(request.getSession().getAttribute("reportingName"))
//				){
				org.apache.log4j.Logger.getLogger(this.getClass()).debug(
						"case : new reporting");
				reportingContentList = reportingDWR.getReportingContent(reportingName, parameters);
				request.getSession().setAttribute("reportingContentList",reportingContentList);
				totalCount = reportingContentList.size();
				request.getSession().setAttribute("totalCount",totalCount);
				request.getSession().setAttribute("reportingName",reportingName);
				request.getSession().setAttribute("sortCol",sortCol);
				request.getSession().setAttribute("sortDir",sortDir);
//			}
//			else{
//				reportingContentList = (ArrayList<ReportingContent>)request.getSession().getAttribute("reportingContentList");
//				totalCount=(Integer)request.getSession().getAttribute("totalCount");
//			}			

			//System.out.println("sort col de session "+request.getSession().getAttribute("sortCol"));
			if(!sortCol.equals(request.getSession().getAttribute("sortCol")) 
					|| !sortDir.equals(request.getSession().getAttribute("sortDir"))){
				
				//sort arraylist
				final int column =Integer.parseInt(sortCol);
				final String direction = sortDir;
				Comparator sort;
				if(direction.equals("ASC")){
					sort = new Comparator() {
						  public int compare(Object o1, Object o2) {
							  try{
								  Double test= ( Double.parseDouble((String)((ReportingContent) o1).getField().get(column))-
										  			Double.parseDouble((String)((ReportingContent) o2).getField().get(column)));
								  return test.intValue();
							  }
							  catch(Exception e){}
							  try{
								  return ((String)((ReportingContent) o1).getField().get(column)).compareTo(
										  (String)((ReportingContent) o2).getField().get(column));
							  }						  
							  catch(Exception e){
								 // e.printStackTrace();
								  return 0;}
						  }
						};
				}
				else{
					sort = new Comparator() {
						  public int compare(Object o1, Object o2) {
							try{
								Double test= ( Double.parseDouble((String)((ReportingContent) o2).getField().get(column))-
										  		Double.parseDouble((String)((ReportingContent) o1).getField().get(column)));
								return test.intValue();
							}
							catch(Exception e){}
							try{
								return ((String)((ReportingContent) o2).getField().get(column)).compareTo((String)((ReportingContent) o1).getField().get(column));
							}
							
							catch(Exception e){
								//e.printStackTrace();
								return 0;}
						  }
						};			
				}

				request.getSession().setAttribute("sortCol",sortCol);
				request.getSession().setAttribute("sortDir",sortDir);
				Collections.sort(reportingContentList, sort);
			}
			
			//get part we are interested
			if(max>reportingContentList.size()){
				max=reportingContentList.size();
			}				
			if(max>(reportingContentList.size()-skip)) {
				org.apache.log4j.Logger.getLogger(this.getClass()).debug(
						"last page case");
				max=reportingContentList.size()-skip;
			}
			
			json.put("TotalCount",totalCount);
			ArrayList<JSONObject> rows = new ArrayList<JSONObject>();
			for(int i=skip;i<(max+skip);i++){
				JSONObject jsfields = new JSONObject();
				for (int j = 0; j < Math.min(reportingContentList.get(i).getField().size(),fields.length); j++) {
					jsfields.put(""+j,reportingContentList.get(i).getField().get(j));
				}
				rows.add(jsfields);
			}			
			json.put("reporting",rows);
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					json);
			

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
}
