package talend.ext.images.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import talend.ext.images.server.backup.DBDelegate;
import talend.ext.images.server.backup.DBDelegateException;
import talend.ext.images.server.backup.ResourcePK;
import talend.ext.images.server.util.IOUtil;
import talend.ext.images.server.util.ReflectionUtil;

public class ImageDeleteServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ImageDeleteServlet.class);

    private String outputFormat = ""; //$NON-NLS-1$

    private String deleteInDB = "false"; //$NON-NLS-1$

    private String dbDelegateClass = ""; //$NON-NLS-1$

    private String deleteUseTransaction = "false"; //$NON-NLS-1$

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        outputFormat = config.getInitParameter("output-format"); //$NON-NLS-1$
        deleteInDB = config.getInitParameter("delete-in-db"); //$NON-NLS-1$
        dbDelegateClass = config.getInitParameter("db-delegate-class"); //$NON-NLS-1$
        deleteUseTransaction = config.getInitParameter("delete-use-transaction"); //$NON-NLS-1$
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result = onDelete(request);
        response.setContentType("text/html"); //$NON-NLS-1$
        PrintWriter writer = response.getWriter();
        writer.write(result);
        writer.close();
    }

    private String onDelete(HttpServletRequest request) {
        try {
            String uri = request.getParameter("uri"); //$NON-NLS-1$
            if (logger.isDebugEnabled()) {
                logger.debug("Input URI: " + uri);
            }
            if (uri == null || uri.length() == 0) {
                return buildDeleteResult(false, "Request parameter 'uri' can not be empty!");
            }
            String contextPath = request.getSession().getServletContext().getContextPath();
            // normalize uri
            uri = normalizeURI(uri, contextPath);
            String toDeleteFilePath = buildDeleteFilePath(uri);
            if (logger.isDebugEnabled()) {
                logger.debug("To Delete File Path: " + toDeleteFilePath);
            }
            // delete on file system
            // TODO care about synchronized when delete file
            File toDeleteFile = new File(toDeleteFilePath);
            if (toDeleteFile.exists()) {
                if (toDeleteFile.delete()) {
                    // also delete in backup db
                    if (Boolean.parseBoolean(deleteInDB)) {
                        DBDelegate dbDelegate = (DBDelegate) ReflectionUtil.newInstance(dbDelegateClass, new Object[0]);
                        ResourcePK resourcePK = new ResourcePK(uri);
                        if (dbDelegate.deleteResource(resourcePK)) {
                            return buildDeleteResult(true,
                                    "The target file has been deleted from MDM Image Server successfully! ");
                        } else if (Boolean.parseBoolean(deleteUseTransaction)) {
                            // roll back
                            byte[] fileBytes = dbDelegate.getResource(resourcePK);
                            if (fileBytes != null) {
                                if (IOUtil.byteToImage(fileBytes, toDeleteFilePath)) {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("Roll back while deleting!");
                                    }
                                    return buildDeleteResult(false,
                                            "Failed in deleting in backup database, Roll back while deleting! ");
                                } else {
                                    throw new DBDelegateException("Roll back failed!");
                                }
                            } else {
                                throw new DBDelegateException("Roll back failed: Can not get resource!");
                            }

                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Deleting from file system is OK, but from backup db failed. It's still OK, since no Transaction!");
                            }
                            return buildDeleteResult(true, "The target file has been deleted from MDM Image Server successfully!");
                        }
                    } else {
                        return buildDeleteResult(true, "The target file has been deleted from MDM Image Server successfully!");
                    }
                } else {
                    return buildDeleteResult(false, "To delete the target file from file system failed!");
                }
            } else {
                return buildDeleteResult(false, "The target file does not exist on file system!");
            }
        } catch (Exception e) {
            String err = "Exception occurred during deleting!";
            logger.error(err, e);
            return buildDeleteResult(false, err);
        }
    }

    private String normalizeURI(String uri, String contextPath) {
        if (uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        if (uri.startsWith("/")) { //$NON-NLS-1$
            uri = uri.substring(1);
        }
        return uri;
    }

    public static String buildDeleteFilePath(String uri) throws Exception {
        try {
            StringBuffer fullFilename = new StringBuffer();
            String filename = URLDecoder.decode(uri, "UTF-8").substring(uri.indexOf("/") + 1); //$NON-NLS-1$ //$NON-NLS-2$
            filename = StringUtils.replace(filename, "/", File.separator); //$NON-NLS-1$
            fullFilename.append(ImageUploadServlet.getUploadPath()).append(File.separator).append(filename);
            return fullFilename.toString();
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception occured during decoding URI:" + uri, e); //$NON-NLS-1$
            throw new Exception(e);
        }

    }

    private String buildDeleteResult(boolean success, String message) {
        StringBuilder sb = new StringBuilder();
        if ("xml".equals(outputFormat)) { //$NON-NLS-1$
            sb.append("<DeleteResult>"); //$NON-NLS-1$
            sb.append("<success>").append(success).append("</success>"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("<message>").append(message).append("</message>"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("</DeleteResult>"); //$NON-NLS-1$
        } else if ("json".equals(outputFormat)) { //$NON-NLS-1$
            sb.append("{"); //$NON-NLS-1$
            sb.append("\"success\":").append(success).append(","); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("\"message\":\"").append(message).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("}"); //$NON-NLS-1$
        }
        return sb.toString();
    }
}
