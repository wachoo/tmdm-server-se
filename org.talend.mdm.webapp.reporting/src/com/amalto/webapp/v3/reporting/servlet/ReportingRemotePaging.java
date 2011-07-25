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
import com.amalto.webapp.v3.reporting.bean.ForeignKeyDesc;
import com.amalto.webapp.v3.reporting.bean.ForeignKeyDescHelper;
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
        request.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
		
		org.apache.log4j.Logger.getLogger(this.getClass()).info(
				"SERVLET Remote paging for reporting");
		
        response.setContentType("text/html; charset=UTF-8"); //$NON-NLS-1$
		
		Enumeration param = request.getParameterNames();
		while(param.hasMoreElements()){
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					param.nextElement().toString() );
		}
		
        String start = request.getParameter("start"); //$NON-NLS-1$
        String limit = request.getParameter("limit"); //$NON-NLS-1$
        String sortCol = (request.getParameter("sort") != null ? request.getParameter("sort") : "0"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        String sortDir = (request.getParameter("dir") != null ? request.getParameter("dir") : "ASC"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        String reportingName = request.getParameter("reportingName"); //$NON-NLS-1$
        String parametersValues = request.getParameter("params"); //$NON-NLS-1$
		
		
		if (parametersValues == null)
            parametersValues = ""; //$NON-NLS-1$
		

		org.apache.log4j.Logger.getLogger(this.getClass()).debug("params ="+parametersValues);
		
		boolean splitEnd = false;
		String tmpSplit = parametersValues;
		Vector<String> paramVector = new Vector<String>();
		while (!splitEnd) {
            int indexMatch = tmpSplit.indexOf("###"); //$NON-NLS-1$
			if (indexMatch == -1) {
				paramVector.add(tmpSplit);
				splitEnd = true;
			} else {
				if (indexMatch > 0) {
					String tmpParam = tmpSplit.substring(0, indexMatch);
					paramVector.add(tmpParam);
				} else
                    paramVector.add(""); //$NON-NLS-1$
				
				if (indexMatch+3 >= tmpSplit.length())
                    tmpSplit = ""; //$NON-NLS-1$
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
				ArrayList rsList=reportingDWR.getReportingContent(reportingName, parameters,skip,max,sortCol,sortDir);
				reportingContentList = (ArrayList<ReportingContent>) rsList.get(0);
            request.getSession().setAttribute("reportingContentList", reportingContentList); //$NON-NLS-1$
				totalCount = Integer.parseInt((String) rsList.get(1));
            request.getSession().setAttribute("totalCount", totalCount); //$NON-NLS-1$
            request.getSession().setAttribute("reportingName", reportingName); //$NON-NLS-1$
            request.getSession().setAttribute("sortCol", sortCol); //$NON-NLS-1$
            request.getSession().setAttribute("sortDir", sortDir); //$NON-NLS-1$
//			}
//			else{
//				reportingContentList = (ArrayList<ReportingContent>)request.getSession().getAttribute("reportingContentList");
//				totalCount=(Integer)request.getSession().getAttribute("totalCount");
//			}			

			//System.out.println("sort col de session "+request.getSession().getAttribute("sortCol"));
			if(((Boolean)rsList.get(2)).booleanValue()&&
 (!sortCol.equals(request.getSession().getAttribute("sortCol")) //$NON-NLS-1$
                    || !sortDir.equals(request.getSession().getAttribute("sortDir"))) //$NON-NLS-1$
				){
				
				//sort arraylist
				final int column =Integer.parseInt(sortCol);
				final String direction = sortDir;
				Comparator sort;
                if (direction.equals("ASC")) { //$NON-NLS-1$
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

                request.getSession().setAttribute("sortCol", sortCol); //$NON-NLS-1$
                request.getSession().setAttribute("sortDir", sortDir); //$NON-NLS-1$
				Collections.sort(reportingContentList, sort);
			}
			
			if(!((Boolean)rsList.get(2)).booleanValue()) {
				skip=0;
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
			
            json.put("TotalCount", totalCount); //$NON-NLS-1$
			ArrayList<JSONObject> rows = new ArrayList<JSONObject>();
            // fetch fkinfo
            Configuration config = Configuration.getInstance(true);
            String dataModelPK = config.getModel();
            ForeignKeyDesc fkdesc = new ForeignKeyDesc(rep.getConcept(), dataModelPK, config.getCluster());
            fkdesc.fetchAnnotations();
			for(int i=skip;i<(max+skip);i++){
				JSONObject jsfields = new JSONObject();
				for (int j = 0; j < Math.min(reportingContentList.get(i).getField().size(),fields.length); j++) {
                    String xpath = fields[j].getXpath();
                    String fkids = reportingContentList.get(i).getField().get(j).toString();
                    jsfields.put("" + j, ForeignKeyDescHelper.getFkInfoValue(xpath, fkdesc, fkids)); //$NON-NLS-1$
				}
				rows.add(jsfields);
			}			
            json.put("reporting", rows); //$NON-NLS-1$
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
