/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.servlet;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocalHome;
import com.amalto.core.util.Util;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

/**
 *
 */
public class ContextListener implements ServletContextListener {

    private static final Logger log = Logger.getLogger(ContextListener.class);

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (log.isDebugEnabled()) {
            log.debug("contextInitialized()"); //$NON-NLS-1$
        }

        try {
            // AutoUpgrade
            ConfigurationInfoCtrlLocal ctrl = ((ConfigurationInfoCtrlLocalHome) new InitialContext().lookup(ConfigurationInfoCtrlLocalHome.JNDI_NAME)).create();
            ctrl.autoUpgradeInBackground();
        } catch (Throwable e) {
            log.error("Unable to perform Auto Upgrade", e);
        }
    }

    /**
     * <p>
     * Called by J2EE container when web application is being un-deployed. This is usually a good place to
     * clean up the resources such as database.
     * </p>
     *
     * @param servletContextEvent Parameter to access servlet specific info (provided by J2EE container)
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // Close database
        log.info("Closing database");
        try {
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            server.close();
        } catch (Throwable e) {
            // Don't re-throw exception in case this breaks normal J2EE container operation.
            log.error("Error during database shutdown", e);
        }
    }
}
