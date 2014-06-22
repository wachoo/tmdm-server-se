// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.v3.xtentismdm.servlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class SessionListener implements HttpSessionListener {

    private static final Logger logger = Logger.getLogger(SessionListener.class);

    public void sessionCreated(HttpSessionEvent event) {
        if (logger.isDebugEnabled())
            logger.debug("Session created"); //$NON-NLS-1$
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        if (logger.isDebugEnabled())
            logger.debug("Session destroyed"); //$NON-NLS-1$
    }
}
