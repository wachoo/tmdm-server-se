package com.amalto.webapp.core.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSLogout;


public abstract class GenericControllerServlet extends HttpServlet {
	
	/**
	 * 
	 */

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		PrintWriter out = res.getWriter();
		try {
		
		Locale locale = req.getLocale();
		String language= "fr";
		if(locale.getLanguage()!=null){
			language = locale.getLanguage();
		}
		if(req.getSession().getAttribute("language")!=null){
			language = (String) req.getSession().getAttribute("language");
		}
		//req.getSession().setAttribute("language",language);

		res.setContentType("text/html; charset=UTF-8");
		res.setHeader("Content-Type","text/html; charset=UTF-8");

		// Dispatch call
		String action = req.getParameter("action");	
		
		if ("logout".equals(action)) {
			//logout the LocalUser cache
			try {
	            Util.getPort().logout(new WSLogout());
            } catch (Exception e) {
            	String err = "Unable to call logout() on the server side";
	            org.apache.log4j.Logger.getLogger(this.getClass()).warn(err, e);
            }
            //invalidate the session
			req.getSession().invalidate();
			res.sendRedirect("../index.html");
		} 
		else {
			String html = 
					"<html>" +
					"<head>" +
					"<title>Webapp core</title>" +
					getCommonImport();
			// getJavascriptImports
			html += getJavascriptImportsHtml();

			html+=
					"</head>" +
					getBody(language,req)+
					"</html>";
			
			out.write(html);
			
			//dispatch
			/*action = "js/core.jsp";
		    RequestDispatcher disp;
		    disp = req.getRequestDispatcher(action);
		   // forward the request to the dispatcher
		    disp.forward(req, res);*/
		    //disp.include(req,res);
		}

		} catch (Exception e) {
			out.write("<h1>ERROR</H1>");
			e.printStackTrace(out);
		} finally {
			//super.doPost(req, res);			
		}
	}
	
	protected String getJavascriptImportsHtml() throws Exception{
		String html = "";
		ArrayList<String> imports = getJavascriptImport();
		for (Iterator<String> iter = imports.iterator(); iter.hasNext();) {
			String jsImport = iter.next();
			//html+="<script type=\"text/javascript\" src=\""+jsImport+"\"></script>\n";
			html += jsImport;
		}
		return html;
	}
	
	private ArrayList<String> getJavascriptImport() throws Exception{
		ArrayList<String> imports = new ArrayList<String>();
		getJavascriptImportDetail(Menu.getRootMenu(), imports, 1,1);
		return imports;
	}

	
	private int getJavascriptImportDetail(Menu menu, ArrayList< String> imports, int level, int i) throws Exception{
		for (Iterator<String> iter = menu.getSubMenus().keySet().iterator(); iter.hasNext(); ) {
			String key = iter.next();
			Menu subMenu= menu.getSubMenus().get(key);
			
			if(subMenu.getContext()!=null) {
				String tmp ="<script type=\"text/javascript\" src=\"/"+subMenu.getContext()+"/secure/dwr/interface/"+subMenu.getApplication()+"Interface.js\"></script>\n";
				imports.add(tmp);
				tmp ="<script type=\"text/javascript\" src=\"/"+subMenu.getContext()+"/secure/js/"+subMenu.getApplication()+".js\"></script>\n";
				imports.add(tmp);
				//tmp ="<link rel=\"stylesheet\" type=\"text/css\" href=\"/"+subMenu.getContext()+"/secure/css/"+subMenu.getApplication()+".css\"></link>\n";
				//imports.add(tmp);
				i++;
			}

			if (subMenu.getSubMenus().size()>0) 
				i=getJavascriptImportDetail(subMenu, imports, level+1,i);			
		}
		return i;	
	}
	
	protected String getCommonImport(){
		return 
		//EXT & YUI
		"<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/utilities/utilities.js\"></script>\n"+
		"<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/yuiloader/yuiloader-beta.js\"></script>\n"+
		//"<script src=\"/core/secure/ext-2.0/adapter/yui/yui-utilities.js\" type=\"text/javascript\"></script>\n"+
		"<script type=\"text/javascript\" src=\"/core/secure/ext-2.0/adapter/yui/ext-yui-adapter.js\"></script>\n"+
		"<script type=\"text/javascript\" src=\"/core/secure/ext-2.0/ext-all-debug.js\"></script>\n"+ 
		"<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/ext-2.0/resources/css/ext-all.css\" />\n" +
		//Firefox3 Fixes
		"<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/firefox3-fix.css\" />\n" +
		//CORE
		"<script type=\"text/javascript\" src=\"/core/secure/js/core.js\"></script>\n" +
		"<script type=\"text/javascript\" src=\"/core/secure/dwr/interface/LayoutInterface.js\"></script>\n"+
		"<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/webapp-core.css\" />\n" +
		"<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/amalto-menus.css\" />\n"+
		//Proxy DWR <-> Ext
		"<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRAction.js\"></script>\n"+
		"<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRProxy.js\"></script>\n"+
		//"<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRProxy.js\"></script>\n"+
		//utility class
		"<script type=\"text/javascript\" src=\"/core/secure/js/bgutil.js\"></script>\n"+
		//DWR
		"<script language=\"javascript1.2\" type='text/javascript' src='/core/secure/dwr/engine.js'></script>\n"+
		"<script language=\"javascript1.2\" type='text/javascript' src='/core/secure/dwr/util.js'></script>\n" ;
		
	}
	

	
	protected String getBody(String language, HttpServletRequest request){ 
//		String timestamp = "2007/07/04";
		String html = 		    
			"		<option value=\"en\" selected>English</option>\n"+
			"		<option value=\"fr\">Francais</option>\n";
		if(language.equals("fr")){
			html = 		    
				"		<option value=\"en\" >English</option>\n"+
				"		<option value=\"fr\" selected>Francais</option>\n";
		}
		
		return
			"<body id=\"genericUI\">\n"+
				"<div id=\"header\" class=\"generic-header\">\n"+
				//TODO image 				
				"	<img src=\""+ request.getContextPath() +"/secure/img/top-banner-talend.gif\"/>\n"+
				//"	<div id=\"logout-btn\" class=\"logout-btn\"></div>\n"+
			    "	<div class=\"language-select\"><select style=\"align: right; font: normal  11px tahoma,verdana,helvetica; right: 5px\" id=\"languageSelect\" onchange=\"amalto.core.switchLanguage();\">\n"+
			    html+
			    "	</select></div>\n"+	   
			    
			    "	<div id=\"username-div\" class=\"username\"></div>\n" +
			    "<a href='"+ request.getContextPath() +"/secure/?action=logout' id='logout-btn' class='logout-btn'>"+(language.equals("fr")?"d&eacute;connexion":"logout")+"</a>\n"+
			    "</div>\n"+
			    "<div id=\"menus\" class=\"menus-list\"></div>\n" +
			   "<div id=\"centerdiv\"></div>\n"+		    
			  // "<div id=\"actions\"></div>\n"+		    
			    "<div id=\"statusdiv\"></div>\n"+		
				"<input type=\"hidden\" id=\"contextPath\" value="+ request.getContextPath() +"/>\n"+
				"<input type=\"hidden\" id=\"serverPath\" value="+ request.getScheme()+"://"+request.getLocalAddr() +":"+request.getLocalPort() +"/>\n"+
			"</body>";
	}
	
	protected String getDefaultHtml(){
		return "";
	}
}

