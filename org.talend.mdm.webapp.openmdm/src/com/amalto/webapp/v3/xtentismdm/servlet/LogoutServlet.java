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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.amalto.webapp.core.util.SessionListener;

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
        int errorCode = -1;
        if (user != null) {
            try {
                String username = com.amalto.webapp.core.util.Util.getAjaxSubject().getUsername();
                if (user.equals(username))
                    SessionListener.unregisterUser(user);
                else
                    errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            } catch (Exception e) {
                if (logger.isDebugEnabled())
                    logger.debug("Error occured while updating online users!"); //$NON-NLS-1$
                errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        }
        if (errorCode != -1)
            resp.sendError(errorCode);
        else
            resp.sendRedirect(req.getContextPath());
        req.getSession().invalidate();
    }
}
