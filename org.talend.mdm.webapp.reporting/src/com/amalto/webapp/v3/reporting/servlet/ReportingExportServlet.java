package com.amalto.webapp.v3.reporting.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.*;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.v3.reporting.bean.Reporting;
import com.amalto.webapp.v3.reporting.bean.ReportingContent;
import com.amalto.webapp.v3.reporting.dwr.ReportingDWR;




/**
 * 
 * @author asaintguilhem
 *
 *export as a MS Excel file
 */

public class ReportingExportServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ReportingExportServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		org.apache.log4j.Logger.getLogger(this.getClass()).info("SERVLET Reporting export for excel ");
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		response.reset(); 
		response.setContentType("application/vnd.ms-excel");
		//response.setContentType("application/x-download");
		//response.setContentType ("application/octet-stream" );
		String theReportFile = "Reporting_"+df.format(new Date())+".xls";   
		response.setHeader ("Content-Disposition", "attachment; filename=\""+theReportFile+"\"" );
		   
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("new sheet");
		sheet.setDefaultColumnWidth((short)20);

		String reportingName = request.getParameter("reportingName");
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
		
		
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("doGet() reporting name"+reportingName);
				
		try {
			ReportingDWR reportingDWR = new ReportingDWR();
			Reporting reporting = reportingDWR.getReporting(reportingName);
			ArrayList<ReportingContent> reportingContentList = reportingDWR.getReportingContent(reportingName,parameters);

			//create a cell style
			HSSFCellStyle cs = wb.createCellStyle();
			HSSFFont f = wb.createFont();
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			cs.setFont(f);			
			HSSFRow row = sheet.createRow((short) 0);			
			for (int i = 0; i < reporting.getFields().length; i++) {
				row.createCell((short) i).setCellValue(reporting.getFields()[i].getField());
			}				
			//set a style for these cells
			for(int i=0;i<reporting.getFields().length;i++){
				row.getCell((short)i).setCellStyle(cs);
			}			
			//populate sheet			
			for(int i=0;i<reportingContentList.size();i++){
				row = sheet.createRow((short) i+1);
				//System.out.println(reportingContent[i]);
				for (int j = 0; j < reporting.getFields().length; j++) {
					String tmp=(String)reportingContentList.get(i).getField().get(j);
					if (tmp!=null) {
						tmp = tmp.trim();
						tmp = tmp.replaceAll("__h","");
						tmp = tmp.replaceAll("h__","");
					} else {
						tmp="";
					}
					row.createCell((short) j).setCellValue(tmp);
				}
			}	
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Write the output  
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.close();		
	}
	protected void doPost(	HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		doGet (request, response);
	}
}	
