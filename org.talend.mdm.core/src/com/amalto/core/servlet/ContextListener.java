/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.servlet;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.jobox.properties.ThreadIsolatedSystemProperties;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocalHome;
import com.amalto.core.util.Util;
import org.apache.log4j.Logger;

/**
 *
 */
public class ContextListener implements ServletContextListener {

    private static final Logger log = Logger.getLogger(ContextListener.class);

    private Properties previousSystemProperties;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (log.isDebugEnabled()) {
            log.debug("contextInitialized()"); //$NON-NLS-1$
        }

        // TMDM-2933: ThreadIsolatedSystemProperties allows threads to get different system properties when needed.
        previousSystemProperties = System.getProperties();
        System.setProperties(ThreadIsolatedSystemProperties.getInstance());
        if (log.isDebugEnabled()) {
            log.debug("Enabled system properties isolation for threads."); //$NON-NLS-1$
        }

        try {
            // AutoUpgrade
            ConfigurationInfoCtrlLocal ctrl = ((ConfigurationInfoCtrlLocalHome) new InitialContext().lookup(ConfigurationInfoCtrlLocalHome.JNDI_NAME)).create();
            ctrl.autoUpgradeInBackground();
        } catch (Throwable e) {
            log.error("Unable to perform Auto Upgrade", e);  //$NON-NLS-1$
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
        log.info("Closing database");  //$NON-NLS-1$
        try {
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            server.close();
        } catch (Throwable e) {
            // Don't re-throw exception in case this breaks normal J2EE container operation.
            log.error("Error during database shutdown", e);   //$NON-NLS-1$
        }

        // Server is shutting down, replace the system properties with the ones backed up at start up.
        System.setProperties(previousSystemProperties);
    }
}
