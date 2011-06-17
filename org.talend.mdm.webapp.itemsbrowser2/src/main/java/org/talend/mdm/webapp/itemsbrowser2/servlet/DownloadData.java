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
			
			String tables= req.getParameter("tableDescription");
			
			req.setCharacterEncoding("UTF-8");//$NON-NLS-1$
			resp.setHeader("Content-Type","application/force-download");//$NON-NLS-1$
			resp.setHeader("Content-Type","application/vnd.ms-excel");//$NON-NLS-1$
			if(tables!=null&&!"".equals(tables)){
				resp.setHeader("Content-Disposition","attachment;filename="+tables+".xls");//$NON-NLS-1$
			}else{
				resp.setHeader("Content-Disposition","attachment;filename=export.xls");//$NON-NLS-1$
			}
			out.print(req.getParameter("exportContent"));//$NON-NLS-1$
		}catch(Exception e){
			org.apache.log4j.Logger.getLogger(this.getClass()).error("doPost() ");
		}
	}
}
