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
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.helpers.IOUtils;
import org.apache.log4j.Logger;

/**
 * 
 * 
 * export as mime file
 */
public class MimeContentExportServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(this.getClass());

    private List<String> fileTypes = null;

    private List<String> mimeTypes = null;

    public MimeContentExportServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String file_types = config.getInitParameter("file-types");//$NON-NLS-1$
        String[] typeArray = file_types.split("\\s+");//$NON-NLS-1$
        fileTypes = Arrays.asList(typeArray);

        String mime_types = config.getInitParameter("mime-types");//$NON-NLS-1$
        typeArray = mime_types.split("\\s+");//$NON-NLS-1$
        mimeTypes = Arrays.asList(typeArray);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getParameter("uri"); //$NON-NLS-1$

        if (url == null) {
            logger.error("The url is a indispensable parameter !");//$NON-NLS-1$
            return;
        }

        String htpPtn = "^(http|https)\\:\\//(.*)\\/(.*?)(\\.)(.*?)$";//$NON-NLS-1$
        Pattern patternMime = Pattern.compile(htpPtn);
        Matcher matcherMime = patternMime.matcher(url);
        boolean mt = matcherMime.matches();
        if (!mt) {
            logger.error("The url should link to a file with mime type !");//$NON-NLS-1$
            return;
        }

        ServletOutputStream ouputStream = null;
        try {
            String fileType = matcherMime.group(5);
            String mimeType = "application/x-unknown";//$NON-NLS-1$
            int typeIndx = fileTypes.indexOf(fileType.toLowerCase());
            if (typeIndx != -1 && typeIndx < mimeTypes.size()) {
                mimeType = mimeTypes.get(typeIndx);
            }

            byte[] mimecontbytes = null;
            try {
                URL mimeUrl = new URL(url);
                mimecontbytes = IOUtils.readBytesFromStream(mimeUrl.openStream());
            } catch (Exception ex) {
                mimecontbytes = new byte[] {};
            }
            response.setContentType(mimeType);
            response.setContentLength(mimecontbytes.length);
            response.setHeader("Content-Disposition", "attachment"); //$NON-NLS-1$//$NON-NLS-2$
            response.setHeader("Cache-Control", "cache, must-revalidate");//$NON-NLS-1$//$NON-NLS-2$
            response.setHeader("Pragma", "public");//$NON-NLS-1$//$NON-NLS-2$
            ouputStream = response.getOutputStream();
            ouputStream.write(mimecontbytes, 0, mimecontbytes.length);
            ouputStream.flush();
        } finally {
            if (ouputStream != null) {
                ouputStream.close();
            }
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
