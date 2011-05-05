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
package com.amalto.webapp.v3.xtentismdm.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.amalto.core.util.LocalUser;

public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(LogoutServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        doLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doLogout(req, resp);
    }

    private void doLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String user = req.getParameter("user"); //$NON-NLS-1$
        if (user != null) {
            try {
                String sessionId = LocalUser.getLocalUser().getOnlineUsers().remove(user);
                if (sessionId != null) {
                    ServletContext context = req.getSession().getServletContext();
                    @SuppressWarnings("unchecked")
                    Map<String, HttpSession> activeSessions = (Map<String, HttpSession>) context
                            .getAttribute(SessionListener.ACTIVE_SESSIONS);
                    HttpSession session = activeSessions.get(sessionId);
                    session.invalidate();
                }
            } catch (Exception e) {
                logger.debug("Error happened while updating online users!"); //$NON-NLS-1$
            }
        }

        req.getSession().invalidate();
        resp.sendRedirect(req.getContextPath() + "/index.html"); //$NON-NLS-1$

    }

}
