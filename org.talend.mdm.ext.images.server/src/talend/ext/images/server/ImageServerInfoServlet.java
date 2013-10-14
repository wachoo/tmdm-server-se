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
package talend.ext.images.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class ImageLocateServlet
 */
public class ImageServerInfoServlet extends HttpServlet {

    private static final long serialVersionUID = 1211531979103254445L;

    private static final String ACTION_GET_UPLOAD_HOME = "getUploadHome"; //$NON-NLS-1$

    private static final String UPLOAD_FOLDER = "upload"; //$NON-NLS-1$

    private static final String TEMP_FOLDER = "upload_tmp"; //$NON-NLS-1$

    private static final Logger logger = Logger.getLogger(ImageServerInfoServlet.class);

    private static String uploadPath = null;

    private static String tempPath = null;

    private static void initPaths() {
        String jbossServerDir = System.getProperty("jboss.server.home.dir"); //$NON-NLS-1$
        if (jbossServerDir != null) {
            uploadPath = jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                    + "mdm_resources" + File.separator + ImageServerInfoServlet.UPLOAD_FOLDER; //$NON-NLS-1$
            tempPath = jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                    + "mdm_resources" + File.separator + ImageServerInfoServlet.TEMP_FOLDER; //$NON-NLS-1$

            File uploadFolder = new File(uploadPath);
            File tempUploadFolder = new File(tempPath);

            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            if (!tempUploadFolder.exists()) {
                tempUploadFolder.mkdirs();
            }

            if (!uploadFolder.exists() || !uploadFolder.canWrite() || !tempUploadFolder.exists() || !tempUploadFolder.canWrite()) {
                throw new IllegalStateException("Image Upload directory or Upload temp directory is not available for writing!"); //$NON-NLS-1$
            } else {
                logger.info("Images Upload Base Path: " + uploadPath); //$NON-NLS-1$
                logger.info("Images Temporary Base Path: " + tempPath); //$NON-NLS-1$
            }
        } else {
            throw new RuntimeException("Wrong server environment"); //$NON-NLS-1$
        }
    }

    public static String getUploadPath() {
        if (uploadPath == null) {
            initPaths();
        }
        return uploadPath;
    }

    public static String getTempPath() {
        if (tempPath == null) {
            initPaths();
        }
        return tempPath;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action"); //$NON-NLS-1$
        if (action != null) {
            if (action.equals(ACTION_GET_UPLOAD_HOME)) {

                File uploadFolder = new File(getUploadPath());

                if (!uploadFolder.isDirectory() || !uploadFolder.canWrite()) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }

                response.setContentType("text/plain"); //$NON-NLS-1$
                PrintWriter writer = response.getWriter();
                writer.write(getUploadPath());
                writer.close();
            }
        }
    }
}
