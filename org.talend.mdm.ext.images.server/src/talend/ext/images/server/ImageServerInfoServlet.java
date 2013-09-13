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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
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

    public static final String UPLOAD_PATH = "upload"; //$NON-NLS-1$

    public static final String TEMP_PATH = "upload_tmp"; //$NON-NLS-1$
    
    private final Logger logger = Logger.getLogger(ImageServerInfoServlet.class);

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action"); //$NON-NLS-1$
        ServletContext sc = this.getServletConfig().getServletContext();
        if (action != null) {
            if (action.equals(ACTION_GET_UPLOAD_HOME)) {
                String realUploadPath = (String) sc.getAttribute(UPLOAD_PATH);
                
                File uploadFolder = new File(realUploadPath);
                
                if(!uploadFolder.isDirectory() || !uploadFolder.canWrite()){
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                    
                response.setContentType("text/plain"); //$NON-NLS-1$
                PrintWriter writer = response.getWriter();
                writer.write(realUploadPath);
                writer.close();
            }
        }
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext sc  = config.getServletContext();
        String realUploadPath = (String) sc.getAttribute(ImageServerInfoServlet.UPLOAD_PATH);
        String realTempUploadPath = (String) sc.getAttribute(ImageServerInfoServlet.TEMP_PATH);

        if ( realUploadPath == null || realTempUploadPath == null ) {
            String jbossServerDir = System.getProperty("jboss.server.home.dir"); //$NON-NLS-1$
            if (jbossServerDir != null) {
                realUploadPath = jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                        + "mdm_resources" + File.separator + ImageServerInfoServlet.UPLOAD_PATH; //$NON-NLS-1$
                realTempUploadPath = jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                        + "mdm_resources" + File.separator + ImageServerInfoServlet.TEMP_PATH; //$NON-NLS-1$
            }
        }
        
        File uploadFolder = new File(realUploadPath);
        File tempUploadFolder = new File(realTempUploadPath);

        if (!uploadFolder.exists())
            uploadFolder.mkdirs();

        if (!tempUploadFolder.exists())
            tempUploadFolder.mkdirs();            

        if ( !uploadFolder.exists() || !uploadFolder.canWrite() || !tempUploadFolder.exists() || !tempUploadFolder.canWrite()) {
            throw new UnavailableException("Image Upload directory or Upload temp direcotry is not available for writting!"); //$NON-NLS-1$
        } else {
            sc.setAttribute(UPLOAD_PATH, realUploadPath);
            sc.setAttribute(TEMP_PATH, realTempUploadPath);
            logger.debug("Images Upload Base Path: " + realUploadPath); //$NON-NLS-1$
        }

    }

}
