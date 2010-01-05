package com.amalto.webapp.v3.itemsbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSExtractThroughTransformerV2;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSTransformerContextPipelinePipelineItem;
import com.amalto.webapp.util.webservices.WSTransformerV2PK;
import com.amalto.webapp.v3.itemsbrowser.dwr.ItemsBrowserDWR;



/**
 * 
 * @author asaintguilhem
 *
 */

public class SmartViewServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SmartViewServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding("UTF-8");		
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
		
		String idsString= request.getParameter("ids");
		String concept = request.getParameter("concept");
		if(concept==null || idsString==null) return;
		String[] ids = idsString.split("@");
		String language = (request.getParameter("language")!=null?request.getParameter("language").toUpperCase():"EN");
		
		boolean transfo = ItemsBrowserDWR.checkIfTransformerExists(concept,language);

		
		String content="";
		String contentType = "text/html";
		
		
		if(transfo) {		
    		String dataClusterPK = "";
    		try {
    			Configuration conf = (Configuration)(request.getSession().getAttribute("configuration"));
    			dataClusterPK = conf.getCluster();
    		} catch (Exception e) {
    			String err = "Unable to read the configuration";
    			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    			throw new ServletException(err, e);
    		}
    		
    		String transformer = "Smart_view_"+concept+"_"+language.toUpperCase();
    		
    		try {
    			//run the Transformer
    			WSTransformerContextPipelinePipelineItem[] entries = Util.getPort().extractThroughTransformerV2(new WSExtractThroughTransformerV2(
    				new WSItemPK(new WSDataClusterPK(dataClusterPK),concept,ids),
    				new WSTransformerV2PK(transformer)
    			)).getPipeline().getPipelineItem();
    			
    			//Scan the entries - in priority, taka the content of the 'html' entry, 
    			//else take the content of the _DEFAULT_ entry
    			for (int i = 0; i < entries.length; i++) {
    				if ("_DEFAULT_".equals(entries[i].getVariable())) {
    					content = new String(entries[i].getWsTypedContent().getWsBytes().getBytes(), "UTF-8");
    					contentType = entries[i].getWsTypedContent().getContentType();
    				}
    				if ("html".equals(entries[i].getVariable())) {
    					content = new String(entries[i].getWsTypedContent().getWsBytes().getBytes(), "UTF-8");
    					contentType = entries[i].getWsTypedContent().getContentType();
    					break;
    				}
    			}
    			
    			
    //				entries = Util.getPort().extractUsingTransformer(
    //						new WSExtractUsingTransformer(
    //								new WSItemPK(new WSDataClusterPK(dataClusterPK),concept,ids),
    //								new WSTransformerPK("Smart_view_"+concept+"_"+language.toUpperCase())
    //						)
    //				).getTypedContentEntry();
    			
    //			for (int i = 0; i < entries.length; i++) {
    //				if("html".equals(entries[i].getOutput())){
    //					byte[] b = entries[i].getWsExtractedContent().getWsByteArray().getBytes();
    //					html = new String(
    //							b,
    //							"utf-8"
    //					);
    //					org.apache.log4j.Logger.getLogger(this.getClass()).debug(
    //							"doGet() "+html);
    //				}
    //			}
    		} catch (Exception e) {
    			String err = "Unable to run the transformer '"+transformer+"'";
    			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    			throw new ServletException(err, e);
    		} 
		}
		
		if (contentType.startsWith("application/xhtml+xml"))
			response.setContentType("text/html");
		else
			response.setContentType(contentType);
		PrintWriter out = response.getWriter();
		out.write(content);
		out.close();		
	}
	protected void doPost(	HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		doGet (request, response);
	}
}	
