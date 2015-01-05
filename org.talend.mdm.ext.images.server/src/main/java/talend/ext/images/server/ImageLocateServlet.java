// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class ImageLocateServlet
 */
public class ImageLocateServlet extends HttpServlet {

    private static final long serialVersionUID = -3012919798771313147L;

    private static final Logger LOGGER = Logger.getLogger(ImageLocateServlet.class);

    private String scalePath;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        scalePath = config.getInitParameter("scalePath"); //$NON-NLS-1$
    }

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
        try {
            String resourceFilePath = getResourceFilePath(request);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Resource file path: '" + resourceFilePath + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            File resourceFile = new File(resourceFilePath);
            if (resourceFile.exists()) {
                String imageUrl = resourceFile.toURI().toURL().toExternalForm();
                String width = request.getParameter("width"); //$NON-NLS-1$
                String height = request.getParameter("height"); //$NON-NLS-1$
                String preserveAspectRatio = request.getParameter("preserveAspectRatio"); //$NON-NLS-1$
                StringBuilder url = new StringBuilder(scalePath);
                url.append("?imageUrl=").append(imageUrl); //$NON-NLS-1$
                if (width != null) {
                    url.append("&width=").append(width); //$NON-NLS-1$
                }
                if (height != null) {
                    url.append("&height=").append(height); //$NON-NLS-1$
                }
                if (preserveAspectRatio != null) {
                    url.append("&preserveAspectRatio=").append(preserveAspectRatio); //$NON-NLS-1$
                }
                RequestDispatcher rd = request.getRequestDispatcher(url.toString());
                rd.forward(request, response);

            } else {
                LOGGER.error("Resource file '" + resourceFilePath + "' not found!"); //$NON-NLS-1 //$NON-NLS-2$
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found!"); //$NON-NLS-1$
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found!"); //$NON-NLS-1$
            return;
        }

    }

    private String getResourceFilePath(HttpServletRequest req) {
        String path = req.getPathInfo();
        try {
            path = URLDecoder.decode(path, "UTF-8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        path = ImageServerInfoServlet.getUploadPath() + File.separator + path;
        path = path.replaceAll("\\\\", "/"); //$NON-NLS-1$//$NON-NLS-2$
        return path;
    }
}
