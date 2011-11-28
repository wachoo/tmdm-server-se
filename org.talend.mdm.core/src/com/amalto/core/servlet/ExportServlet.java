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

package com.amalto.core.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;

public class ExportServlet extends HttpServlet {

    private static final String PARAMETER_CLUSTER = "cluster";

    private static final String PARAMETER_REVISION = "revision";

    private static final String PARAMETER_START = "start";

    private static final String PARAMETER_END = "end";

    public ExportServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException {
        XmlServerSLWrapperLocal server;
        try {
            server = Util.getXmlServerCtrlLocal();
        } catch (XtentisException e) {
            throw new ServletException(e);
        }

        String revisionId = request.getParameter(PARAMETER_REVISION);
        String clusterName = request.getParameter(PARAMETER_CLUSTER);
        int start = Integer.parseInt(request.getParameter(PARAMETER_START));
        int end = Integer.parseInt(request.getParameter(PARAMETER_END));

        ServletOutputStream outputStream;
        try {
            outputStream = resp.getOutputStream();
        } catch (IOException e) {
            throw new ServletException(e);
        }

        try {
            resp.setContentType("text/xml");
            server.exportDocuments(revisionId, clusterName, start, end, outputStream);
        } catch (XtentisException e) {
            throw new ServletException(e);
        } finally {
            try {
                outputStream.flush();
            } catch (IOException e) {
                Logger.getLogger(ExportServlet.class).error("Error during flush", e);
            }
        }
    }
}

