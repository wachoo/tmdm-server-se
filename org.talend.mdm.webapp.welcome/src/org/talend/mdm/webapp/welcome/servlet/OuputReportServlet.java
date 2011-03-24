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
package org.talend.mdm.webapp.welcome.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DOC fliu class global comment. Detailled comment
 */
public class OuputReportServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletOutputStream ouputStream = null;
        try {
            String process = req.getParameter("process").replace("*", "#");
            byte[] mimecontbytes = (byte[]) req.getSession().getAttribute(process);
            byte[] mimetypebytes = (byte[]) req.getSession().getAttribute(process + "mimetype");
            if (mimecontbytes == null || mimetypebytes == null) {
                return;
            }
            String mimetype = new String(mimetypebytes);
            mimetype = mimetype.split(";")[0];
            resp.setContentType(mimetype);
            resp.setContentLength(mimecontbytes.length);
            resp.setHeader("Content-Disposition", "inline");
            resp.setHeader("Cache-Control", "cache, must-revalidate");
            resp.setHeader("Pragma", "public");
            ouputStream = resp.getOutputStream();
            ouputStream.write(mimecontbytes, 0, mimecontbytes.length);
            ouputStream.flush();
        } finally {
            if (ouputStream != null) {
                ouputStream.close();
            }
        }

    }
}
