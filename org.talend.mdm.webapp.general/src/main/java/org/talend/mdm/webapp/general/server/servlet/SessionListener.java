// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.general.server.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

    public static final String ACTIVE_SESSIONS = "activeSessions"; //$NON-NLS-1$

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();
        @SuppressWarnings("unchecked")
        Map<String, HttpSession> activeSessions = (Map<String, HttpSession>) context.getAttribute(ACTIVE_SESSIONS);
        if (activeSessions == null) {
            activeSessions = new HashMap<String, HttpSession>();
            context.setAttribute(ACTIVE_SESSIONS, activeSessions);
        }
        activeSessions.put(session.getId(), session);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();
        @SuppressWarnings("unchecked")
        Map<String, HttpSession> activeSessions = (Map<String, HttpSession>) context.getAttribute(ACTIVE_SESSIONS);
        activeSessions.remove(session.getId());
    }
}
