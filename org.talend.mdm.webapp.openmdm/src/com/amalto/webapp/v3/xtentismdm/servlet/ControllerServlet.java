package com.amalto.webapp.v3.xtentismdm.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.core.util.Util;



public class ControllerServlet extends com.amalto.webapp.core.servlet.GenericControllerServlet{
	

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
		if(req.getParameter("language")!=null){
			language = req.getParameter("language");
			req.getSession().setAttribute("language",language);
		}
		
		req.getSession().setAttribute("language",language);
		res.setContentType("text/html; charset=UTF-8");
		res.setHeader("Content-Type","text/html; charset=UTF-8");
		
		// Dispacth call
		String jsp = req.getParameter("action");	
		if ("logout".equals(jsp)) {
			req.getSession().invalidate();
			res.sendRedirect("../index.html");
		} 
		/*else if("initialization".equals(jsp)){
			InitializationDWR init = new InitializationDWR();
			init.doInitialize();
			out.write("<h3>Initialization...</H3><a href=\"/denys/secure/?action=logout\">connexion</a>");
			//req.getSession().invalidate();
			//res.sendRedirect("../index.html");
		}
		else if("createroles".equals(jsp)){
			InitializationDWR init = new InitializationDWR();
			init.initializeRole();
			out.write("<h3>Roles initialization...</H3><a href=\"/denys/secure/?action=logout\">connexion</a>");
			//req.getSession().invalidate();
			//res.sendRedirect("../index.html");
		}*/
		else {

			String html = 
					"<html>\n" +
					"<head>\n" +
					"<title>Talend MDM</title>\n" +
					super.getCommonImport();
			html += super.getJavascriptImportsHtml();
			html +="<script type=\"text/javascript\" src=\"/talendmdm/secure/js/conf.js\"></script>\n";
			html +="<script type=\"text/javascript\" src=\"/talendmdm/secure/js/actions.js\"></script>\n";
			html +="<script type=\"text/javascript\" src=\"/talendmdm/secure/dwr/interface/ActionsInterface.js\"></script>\n";
			html +="<script type=\"text/javascript\" src=\"/talendmdm/secure/dwr/interface/WidgetInterface.js\"></script>\n";
			

			//TODO specific YUI import
			//LOGGER
			html +="<link type=\"text/css\" rel=\"stylesheet\" href=\"/core/secure/yui-2.4.0/build/logger/assets/logger.css\" ></link>";
			html +="<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/logger/logger.js\"></script>";
			//CONTAINER
			html +="<link type=\"text/css\" rel=\"stylesheet\" href=\"/core/secure/yui-2.4.0/build/container/assets/container.css\" ></link>";
			html +="<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/container/container.js\"></script>";
			//TREE
			//html +="<link type=\"text/css\" rel=\"stylesheet\" href=\"/xtentismdm/secure/yui-0.12/treeview/assets/tree.css\" ></link>\n";
			//html +="<script type=\"text/javascript\" src=\"/xtentismdm/secure/yui-0.12/treeview/treeview-debug.js\"></script>\n";
			//html +="<script type=\"text/javascript\" src=\"/itemsbrowser/secure/js/ItemNode.js\"></script>\n";
			
			//html+="<link type=\"text/css\" rel=\"stylesheet\" href=\"/talendmdm/secure/css/GenericUI.css\" ></link>";
			html+=
					"</head>\n" +
					
					getBody(language, req)+
					"</html>\n";
			
			out.write(html);
			
		}

		} catch (Exception e) {
			req.getSession().invalidate();
			out.write("<h3>Login Error</h3> <p><font size=\"4\" color=\"red\"> "+ e.getLocalizedMessage()+"</font></p>"+
					"<a href='../index.html'>Back to login screen</a> ");
		
			//e.printStackTrace(out);
		} finally {
			//super.doPost(req, res);			
		}
	}

	
	protected String getBody(String language, HttpServletRequest request){ 
		String timestamp = "2007/07/04";
		String html = 		    
			"		<option value=\"en\" selected>English</option>\n"+
			"		<option value=\"fr\">Francais</option>\n";
		if(language.equals("fr")){
			html = 		    
				"		<option value=\"en\" >English</option>\n"+
				"		<option value=\"fr\" selected>Francais</option>\n";
		}
		String enterprise = Util.isEnterprise()?"color:#EE0000;\"> Enterprise<br/>Edition":"color:#B4DC10;\">Community <br/> Edition";
		return
		
			/*"<body id=\"genericUI\" style=\"font:13px tahoma,verdana,helvetica\">\n"+
				"<div id=\"header\" class=\"generic-header\">\n"+
				"	<img src=\""+ request.getContextPath() +"/secure/img/top-banner-talend.gif\"/>\n" +
				"<table style=\"position: absolute;top: 1px;right:1px;\">"+*/
		
		
				"<body id=\"genericUI\" style=\"font:13px tahoma,verdana,helvetica\">\n"+
				"<div id=\"header\" class=\"generic-header-background\">\n"+
				//"	<img src=\""+ request.getContextPath() +"/secure/img/top-banner-talend.gif\"/>\n" +
				
				"<img src=\""+ request.getContextPath() +"/secure/img/header-back-title.png\"/>\n" +
				"<div id=\"username-div\"  class=\"username\"></div>\n" +
				"<div><table style=\"position: absolute;top: -2px;right:1px;\">"+
			    "<td><div><img src=\""+ request.getContextPath() +"/secure/img/logo-mdm.png\"/></div></td>\n" +
			    "<td><div style=\"font: bold 13px tahoma,verdana,helvetica;"+enterprise+"</div></td>\n"+
			    "<td><div>"+"&nbsp;&nbsp;"+"</div></td>\n"+
			    "<td><div><select style=\" font: normal  11px tahoma,verdana,helvetica; right: 5px\" id=\"languageSelect\" onchange=\"amalto.core.switchLanguage();\">\n"+
			    html+
			    "	</select></div></td>" +
			    "<td><div id=\"logout-btn\" ></div></td>\n"+
			    
			    "<td><div style=\"float position:relative;top: 1px;\" ><a href='"+ request.getContextPath() +"/secure/?action=logout' id='logout-btn' class='logout-btn'></a></div></td>\n"+
			    
			    "</tr>\n" +
			    "</table></div>"+	   
			    
			    "</div>\n"+
			    
			    "<div id=\"menus\" class=\"menus-list\"></div>\n" +
			   "<div id=\"centerdiv\"></div>\n"+		    
			   //"<div id=\"actions\"></div>\n"+		    
			    "<div id=\"statusdiv\"></div>\n"+		
				"<input type=\"hidden\" id=\"contextPath\" value="+ request.getContextPath() +"/>\n"+
				"<input type=\"hidden\" id=\"serverPath\" value="+ request.getScheme()+"://"+request.getLocalAddr() +":"+request.getLocalPort() +"/>\n"+
			"</body>";
	}
	
}

