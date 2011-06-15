package org.talend.mdm.webapp.itemsbrowser2.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadData extends HttpServlet{

	
	public void doGet(HttpServletRequest req,HttpServletResponse resp){
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("doGet() ");
		this.doPost(req, resp);
	}
	
	public void doPost(HttpServletRequest req,HttpServletResponse resp){
		try{
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("doPost() ");
			
			PrintWriter out = resp.getWriter();
			
			req.setCharacterEncoding("UTF-8");
			resp.setHeader("Content-Type","application/force-download");
			resp.setHeader("Content-Type","application/vnd.ms-excel");
			resp.setHeader("Content-Disposition","attachment;filename=export.xls");
			out.print(req.getParameter("exportContent"));
		}catch(Exception e){
			org.apache.log4j.Logger.getLogger(this.getClass()).error("doPost() ");
		}
	}
}
