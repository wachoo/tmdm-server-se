package com.amalto.core.servlet;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocalHome;


/**
 * This servlet starts the auto-upgrade which in turn starts the Routing Engine
 * @author bgrieder
 * 
 */

public class StartupServlet extends HttpServlet {


    
    /**
     */
    public StartupServlet() {
        super();
    }

    
    public void init(ServletConfig config) throws ServletException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("init() ");
        super.init(config);
        try {
        	//AutoUpgrade
        	ConfigurationInfoCtrlLocal ctrl =  ((ConfigurationInfoCtrlLocalHome)new InitialContext().lookup(ConfigurationInfoCtrlLocalHome.JNDI_NAME)).create();
        	ctrl.autoUpgradeInBackground();
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to initialize the Auto Upgrades Startup servlet : "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new ServletException(err);
	    }
        
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
            doGet(request,response);
        }
    

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
        IOException {
                
    }
    
     
    
}