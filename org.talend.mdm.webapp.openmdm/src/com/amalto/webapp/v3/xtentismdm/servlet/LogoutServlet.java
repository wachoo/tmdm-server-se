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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class LogoutServlet extends HttpServlet{
    
    private String userName = null;
    
    private Logger logger = Logger.getLogger(this.getClass());
    
    /* (non-Jsdoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        userName=req.getParameter("user");
        doPost(req, resp);
    }
    
    /* (non-Jsdoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            LocalUser.getLocalUser().getOnlineUsers().remove(userName);
        } catch (XtentisException e) {
            logger.error("Error happened while updating online users! ");
        }
        req.getSession().invalidate();
        resp.sendRedirect(req.getContextPath()+"/index.html");

    }

}
