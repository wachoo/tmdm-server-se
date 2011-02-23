// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

public class UploadServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(UploadServlet.class);

    public UploadServlet() {
        super();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter;
        try {
            iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();

                if ("uploadedfile".equals(item.getFieldName())) {
                    BufferedInputStream bin = new BufferedInputStream(item.openStream());
                    String filePath = getServletContext().getRealPath("/imageserver/upload/temp.gif");

                    File file = new File(filePath);

                    BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] b = new byte[8 * 1024];

                    int i = bin.read(b);
                    while (i > 0) {
                        bout.write(b, 0, i);
                        i = bin.read(b);
                    }
                    bout.flush();
                    bout.close();
                }
            }
        } catch (FileUploadException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
