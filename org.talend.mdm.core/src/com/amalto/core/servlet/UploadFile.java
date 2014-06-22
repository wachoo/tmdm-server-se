package com.amalto.core.servlet;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.util.Util;

/**
 * @author bgrieder
 * 
 * 
 */

public class UploadFile extends HttpServlet {

    private static final long serialVersionUID = 1254957429740848823L;

    private static final Logger LOG = Logger.getLogger(UploadFile.class);

    /**
     * UploadFile.java Constructor
     * 
     */
    public UploadFile() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        resp.setContentType("text/plain; charset=\"UTF-8\""); //$NON-NLS-1$
        resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$

        PrintWriter writer = resp.getWriter();
        // delete file
        String deleteFilename = req.getParameter("deletefile"); //$NON-NLS-1$
        String jobdeployPath = getServletConfig().getServletContext().getRealPath("/"); //$NON-NLS-1$
        LOG.info("context path-->" + jobdeployPath); //$NON-NLS-1$

        int pos = jobdeployPath.indexOf("tmp"); //$NON-NLS-1$
        jobdeployPath = jobdeployPath.substring(0, pos - 1);
        String path = new File(jobdeployPath).getAbsolutePath();
        path = path + File.separator + "deploy" + File.separator; //$NON-NLS-1$
        LOG.info("deploy path-->" + path); //$NON-NLS-1$

        // delete file
        if (deleteFilename != null) {
            if (deleteFilename.endsWith(".zip")) { //$NON-NLS-1$
                File f = new File(JobContainer.getUniqueInstance().getDeployDir() + File.separator + deleteFilename);
                f.delete();
            } else if (deleteFilename.endsWith(".war")) { //$NON-NLS-1$
                File f = new File(path + File.separator + deleteFilename);
                f.delete();
            } else if (deleteFilename.endsWith(".bar")) { //$NON-NLS-1$
                String barpath = Util.getBarHomeDir();
                File f = new File(barpath + File.separator + deleteFilename);
                f.delete();
            }
            writer.write("Delete sucessfully"); //$NON-NLS-1$
            writer.close();
            return;
        }
        // upload file
        if (!ServletFileUpload.isMultipartContent(req)) {
            throw new ServletException("Upload File Error: the request is not multipart!"); //$NON-NLS-1$
        }
        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();

        // Set upload parameters
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(0);
        upload.setFileItemFactory(factory);
        upload.setSizeMax(-1);

        // Parse the request
        List<FileItem> items; // FileItem
        try {
            items = upload.parseRequest(req);
        } catch (Exception e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        }

        // Process the uploaded items
        if (items != null && items.size() > 0) {
            // Only one file
            Iterator<FileItem> iter = items.iterator();
            FileItem item = iter.next();
            File tempFile = new File(Util.getJbossHomeDir() + "/server/default/tmp/" + item.getName()); //$NON-NLS-1$
            LOG.info(item.getFieldName());
            if (!item.isFormField()) {
                try {
                    if (req.getParameter("deployjob") != null) {// deploy job //$NON-NLS-1$
                        String deploydir = item.getName().endsWith(".zip") ? JobContainer.getUniqueInstance().getDeployDir() : path; //$NON-NLS-1$
                        tempFile = new File(deploydir + File.separator + item.getName());
                    }
                    // bar files
                    if (item.getName().endsWith(".bar")) { //$NON-NLS-1$
                        String barpath = Util.getBarHomeDir();
                        if (!new File(barpath).exists()) {
                            new File(barpath).mkdir();
                        }
                        tempFile = new File(barpath + File.separator + item.getName());
                    } else if (item.getName().endsWith(".war")) { //$NON-NLS-1$
                        String deplayPath = Util.getAppServerDeployDir();
                        tempFile = new File(deplayPath + File.separator + "server" + File.separator + "default" + File.separator //$NON-NLS-1$//$NON-NLS-2$
                                + "deploy" + File.separator + item.getName()); //$NON-NLS-1$
                    }
                    item.write(tempFile);
                    if (req.getParameter("deployjob") != null && item.getName().endsWith(".zip")) {// deploy job  //$NON-NLS-1$//$NON-NLS-2$
                        String contextStr = req.getParameter("contextStr"); //$NON-NLS-1$
                        JobContainer jobContainer = JobContainer.getUniqueInstance();
                        jobContainer.setContextStrToBeSaved(tempFile.getAbsolutePath(), contextStr);
                    }
                } catch (Exception e) {
                    // better handle concurrent file system modifications
                    // see com.amalto.core.jobox.watch.JoboxListener.fileChanged(List<String>, List<String>, List<String>)
                    if (e.getCause() instanceof EOFException || e.getCause() instanceof ZipException) {
                        LOG.warn("Attempted to to update job '" + item.getName() + "' but is being modified by concurrent process."); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage());//$NON-NLS-1$
                    }
                }
            }
            String urlRedirect = req.getParameter("urlRedirect"); //$NON-NLS-1$
            if (urlRedirect != null && "true".equals(urlRedirect)) { //$NON-NLS-1$
                String redirectUrl = req.getContextPath() + "?" + "mimeFile=" + tempFile.getName(); //$NON-NLS-1$//$NON-NLS-2$
                writer.write(redirectUrl);
            } else {
                writer.write(tempFile.getAbsolutePath());
            }
        }
        writer.close();
    }
}