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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;

/**
 *
 */
public class ContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(ContextListener.class);

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Nothing to do here.
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
