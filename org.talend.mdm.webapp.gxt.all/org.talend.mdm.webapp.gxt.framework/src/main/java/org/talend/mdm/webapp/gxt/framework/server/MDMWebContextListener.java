// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.gxt.framework.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.talend.mdm.webapp.gxt.common.Configuration;
import org.talend.mdm.webapp.gxt.common.PortManager;

public class MDMWebContextListener implements ServletContextListener {

  /** the log used by this class */
  private static Log log = LogFactory.getLog(MDMWebContextListener.class);

  /**
   * Called when the web application is started.
   *
   * @param event a ServletContextEvent instance
   */
  public void contextInitialized(ServletContextEvent event) {
    long startTime = System.currentTimeMillis();
    log.info("Starting MDM-Web");
    
    ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
    Configuration config = (Configuration)applicationContext.getBean("mdmWebConfiguration");
    
    MDMWebContext ctx = MDMWebContext.getInstance();
    ctx.setConfiguration(config);
    ctx.setWebApplicationRoot(event.getServletContext().getRealPath("/"));
    
    PortManager portManager=PortManager.getInstance();
    portManager.setConfiguration(config);
    
    long endTime = System.currentTimeMillis();
    log.info("MDM-Web started in " + (endTime-startTime) + "ms");
   
  }

  /**
   * Called when the web application is shutdown.
   *
   * @param event a ServletContextEvent instance
   */
  public void contextDestroyed(ServletContextEvent event) {
    log.info("Stopping MDM-Web");
    //TODO
    log.info("MDM-Web stopped");
  }

}
