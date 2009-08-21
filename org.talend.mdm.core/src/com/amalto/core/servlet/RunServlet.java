package com.amalto.core.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.core.util.Util;


/**
 * @author bgrieder
 * 
 *
 */

public class RunServlet extends HttpServlet {


    
    /**
     * UploadFile.java
     * Constructor
     * 
     */
    public RunServlet() {
        super();
    }

    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
            doGet(request,response);
        }
    

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
        IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
//        String parameters = req.getParameter("parameters");
        

        resp.setContentType("text/html; charset=\"UTF-8\"");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write("<html><body>");
        
    	try {
    		if ("upgrade".equals(action)) {
    			writer.write(
   					"<p><b>Running the core upgrade</b><br/>" +
   					"Check jboss/server/default/log/server.log or the jboss console output to determine when upgrade is completed</b></p>"
    			);
    			Util.getConfigurationInfoCtrlLocal().autoUpgradeInBackground();
    		} else {
    			writer.write(
   					"<p><b>Unknown action: </b>"+action+"<br/>"
    			);
    			
    		}
    	
    	} catch (Exception e) {
    		writer.write("<h1>An error occured: "+e.getLocalizedMessage()+"</h1>");
    	}
        writer.write("</body></html>");
                
    }
    
      
 
 
    
}