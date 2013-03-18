// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class LogViewerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private File file;

    private int maxLinesRead;

    private String charset;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String filename = config.getInitParameter("logFile"); //$NON-NLS-1$
        file = getLogFile(filename);

        String maxLinesReadString = config.getInitParameter("maxLinesRead"); //$NON-NLS-1$
        maxLinesRead = Integer.valueOf(maxLinesReadString);
        charset = config.getInitParameter("charset"); //$NON-NLS-1$
        if (charset == null) {
            Charset.defaultCharset().name();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long position;
        String positionString = request.getParameter("position"); //$NON-NLS-1$
        if (positionString != null) {
            position = Long.parseLong(positionString);
        } else {
            position = 0;
        }

        if (position < 0) {
            downloadLogFile(response);
        } else {
            writeLogFileChunk(position, response);
        }
    }

    private void writeLogFileChunk(long position, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        FileChunkLoader loader = new FileChunkLoader(file);
        position = loader.loadChunkTo(bufferedOutputStream, position, maxLinesRead);
        bufferedOutputStream.close();

        response.setContentType("text/plain;charset=" + charset); //$NON-NLS-1$
        response.setCharacterEncoding(charset);
        response.setHeader("X-Log-Position", String.valueOf(position)); //$NON-NLS-1$
        response.setHeader("X-Log-Load", Boolean.toString(true)); //$NON-NLS-1$

        OutputStream responseOutputStream = response.getOutputStream();
        outputStream.writeTo(responseOutputStream);
        responseOutputStream.close();
    }

    private void downloadLogFile(HttpServletResponse response) throws IOException {
        String filename = file.getAbsolutePath();
        response.setContentType("application/octet-stream"); //$NON-NLS-1$
        response.setHeader("Content-Disposition ", "attachment; filename=\"" + FilenameUtils.getName(filename) + '"'); //$NON-NLS-1$ //$NON-NLS-2$
        FileInputStream fis = new FileInputStream(file);
        try {
            OutputStream responseOutputStream = response.getOutputStream();
            IOUtils.copyLarge(fis, responseOutputStream);
            responseOutputStream.close();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                // ignore it
            }
        }
    }

    private File getLogFile(String filepath) throws ServletException {
        if (filepath.contains("${jboss.server.log.dir}")) { //$NON-NLS-1$
            String jbossLogDir = System.getProperty("jboss.server.log.dir"); //$NON-NLS-1$
            if (jbossLogDir != null) {
                filepath.replace("${jboss.server.log.dir}", jbossLogDir); //$NON-NLS-1$
            } else {
                throw new ServletException("Wrong logFile parameter " + filepath); //$NON-NLS-1$
            }
        }
        return new File(filepath);
    }
}
