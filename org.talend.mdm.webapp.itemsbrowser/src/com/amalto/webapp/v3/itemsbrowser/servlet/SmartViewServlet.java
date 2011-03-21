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
		
		response.setCharacterEncoding("UTF-8");//$NON-NLS-1$ 
		response.setHeader("Cache-Control", "no-cache, must-revalidate");//$NON-NLS-1$
		response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");//$NON-NLS-1$
		
		String idsString= request.getParameter("ids");//$NON-NLS-1$
		String concept = request.getParameter("concept");//$NON-NLS-1$
		if(concept==null || idsString==null) return;
		String[] ids = idsString.split("@");//$NON-NLS-1$
		String language = (request.getParameter("language")!=null?request.getParameter("language").toUpperCase():"EN");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String optname = request.getParameter("optname");//$NON-NLS-1$
		
		boolean transfo_lang = ItemsBrowserDWR.checkIfTransformerExists(concept,language,optname);
		
		boolean transfo_no_lang = ItemsBrowserDWR.checkIfTransformerExists(concept,null,optname);
		String content="";//$NON-NLS-1$
		String contentType = "text/html";//$NON-NLS-1$
		String transformer=null;
		if(transfo_lang) {
			transformer = "Smart_view_"+concept+"_"+language.toUpperCase();//$NON-NLS-1$ //$NON-NLS-2$
			if(optname!=null&&optname.length()>0)transformer+="#"+optname;//$NON-NLS-1$
		}else if(transfo_no_lang){
			transformer = "Smart_view_"+concept;//$NON-NLS-1$
			if(optname!=null&&optname.length()>0)transformer+="#"+optname;//$NON-NLS-1$
		}

		if(transformer!=null) {		
    		String dataClusterPK = "";//$NON-NLS-1$
    		try {
    			Configuration conf = (Configuration)(request.getSession().getAttribute("configuration"));//$NON-NLS-1$
    			dataClusterPK = conf.getCluster();
    		} catch (Exception e) {
    			String err = "Unable to read the configuration";//$NON-NLS-1$
    			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    			throw new ServletException(err, e);
    		}
    		
    		//String transformer = "Smart_view_"+concept+"_"+language.toUpperCase();
    		
    		try {
    			//run the Transformer
    			WSTransformerContextPipelinePipelineItem[] entries = Util.getPort().extractThroughTransformerV2(new WSExtractThroughTransformerV2(
    				new WSItemPK(new WSDataClusterPK(dataClusterPK),concept,ids),
    				new WSTransformerV2PK(transformer)
    			)).getPipeline().getPipelineItem();
    			
    			//Scan the entries - in priority, taka the content of the 'html' entry, 
    			//else take the content of the _DEFAULT_ entry
    			for (int i = 0; i < entries.length; i++) {
    				if ("_DEFAULT_".equals(entries[i].getVariable())) {//$NON-NLS-1$
    					content = new String(entries[i].getWsTypedContent().getWsBytes().getBytes(), "UTF-8");//$NON-NLS-1$
    					contentType = entries[i].getWsTypedContent().getContentType();
    				}
    				if ("html".equals(entries[i].getVariable())) {//$NON-NLS-1$
    					content = new String(entries[i].getWsTypedContent().getWsBytes().getBytes(), "UTF-8");//$NON-NLS-1$
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
    			String err = "Unable to run the transformer '"+transformer+"'";//$NON-NLS-1$ //$NON-NLS-2$
    			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    			throw new ServletException(err, e);
    		} 
		}
		
		if (contentType.startsWith("application/xhtml+xml"))//$NON-NLS-1$
			response.setContentType("text/html");//$NON-NLS-1$
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
