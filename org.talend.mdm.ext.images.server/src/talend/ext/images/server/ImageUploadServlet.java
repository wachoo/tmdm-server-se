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
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import talend.ext.images.server.backup.DBDelegate;
import talend.ext.images.server.backup.ResourcePK;
import talend.ext.images.server.util.ReflectionUtil;
import talend.ext.images.server.util.Uuid;

public class ImageUploadServlet extends ImageServerInfoServlet {

    private static final long serialVersionUID = 5281522568086790496L;

    private Logger logger = Logger.getLogger(this.getClass());

    private List<String> okFileTypes = null;

    private String defaultFilefieldName = ""; //$NON-NLS-1$

    private String defaultCatalogfieldName = ""; //$NON-NLS-1$

    private String defaultFileNamefieldName = ""; //$NON-NLS-1$

    private String outputFormat = "xml"; //$NON-NLS-1$

    private String bakInDB = "false"; //$NON-NLS-1$

    private String dbDelegateClass = ""; //$NON-NLS-1$

    private String bakUseTransaction = "false"; //$NON-NLS-1$

    private String uploadPath = null;;

    private String tempPath = null;

    private String sourceFileName = ""; //$NON-NLS-1$

    private String sourceFileType = ""; //$NON-NLS-1$

    private String targetUri = "upload"; //$NON-NLS-1$

    private String targetCatalogName = ""; //$NON-NLS-1$

    private String targetFileName = ""; //$NON-NLS-1$

    private String targetFileShortName = ""; //$NON-NLS-1$

    private boolean changeFileName = true;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String types = config.getInitParameter("image-file-types"); //$NON-NLS-1$
        String[] typeArray = types.split("\\|"); //$NON-NLS-1$
        okFileTypes = Arrays.asList(typeArray);
        defaultFilefieldName = config.getInitParameter("default-file-field-name"); //$NON-NLS-1$
        defaultFileNamefieldName = config.getInitParameter("default-filename-field-name"); //$NON-NLS-1$
        defaultCatalogfieldName = config.getInitParameter("default-catalog-field-name"); //$NON-NLS-1$
        outputFormat = config.getInitParameter("output-format"); //$NON-NLS-1$
        bakInDB = config.getInitParameter("bak-in-db"); //$NON-NLS-1$
        dbDelegateClass = config.getInitParameter("db-delegate-class"); //$NON-NLS-1$
        bakUseTransaction = config.getInitParameter("bak-use-transaction"); //$NON-NLS-1$

        ServletContext sc = config.getServletContext();
        uploadPath = (String) sc.getAttribute(ImageServerInfoServlet.UPLOAD_PATH);
        tempPath = (String) sc.getAttribute(ImageServerInfoServlet.TEMP_PATH);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        targetUri = "upload"; //$NON-NLS-1$
        String change = request.getParameter("changeFileName"); //$NON-NLS-1$
        if (change != null) {
            changeFileName = Boolean.valueOf(change);
        }
        logger.debug("changeFileName: " + changeFileName); //$NON-NLS-1$

        String result = onUpload(request, response);

        response.setContentType("text/html"); //$NON-NLS-1$
        response.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        PrintWriter writer = response.getWriter();
        writer.write(result.toString());
        writer.close();
    }

    private String onUpload(HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart) {

                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(4096);
                factory.setRepository(new File(tempPath));

                ServletFileUpload sevletFileUpload = new ServletFileUpload(factory);
                sevletFileUpload.setSizeMax(4 * 1024 * 1024);
                sevletFileUpload.setHeaderEncoding("utf8"); //$NON-NLS-1$
                ProgressListener progressListener = new ProgressListener() {

                    @Override
                    public void update(long pBytesRead, long pContentLength, int pItems) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("We are currently reading item " + pItems); //$NON-NLS-1$
                            if (pContentLength == -1) {
                                logger.debug("So far, " + pBytesRead + " bytes have been read."); //$NON-NLS-1$//$NON-NLS-2$
                            } else {
                                logger.debug("So far, " + pBytesRead + " of " + pContentLength + " bytes have been read."); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                            }
                        }
                    }
                };
                sevletFileUpload.setProgressListener(progressListener);

                List<FileItem> fileItems = null;

                fileItems = sevletFileUpload.parseRequest(request);

                if (fileItems != null && fileItems.size() > 0) {
                    Object uploadFileItemObj = getFileItem(fileItems, defaultFilefieldName);
                    if (uploadFileItemObj != null) {
                        FileItem uploadFileItem = (FileItem) uploadFileItemObj;

                        Object catalogNameObj = getFileItem(fileItems, defaultCatalogfieldName);
                        if (catalogNameObj != null) {
                            targetCatalogName = (String) catalogNameObj;
                        }

                        Object fileNameObj = getFileItem(fileItems, defaultFileNamefieldName);
                        if (fileNameObj != null) {
                            targetFileShortName = (String) fileNameObj;
                        } else {
                            String name = uploadFileItem.getName();
                            int pos = name.lastIndexOf('.');
                            if (pos != -1) {
                                name = name.substring(0, pos);
                            }
                            targetFileShortName = name;
                        }

                        int rtnStatus = processUploadedFile(uploadFileItem, true, Boolean.parseBoolean(bakInDB),
                                Boolean.parseBoolean(bakUseTransaction));

                        if (rtnStatus == 1) {
                            logger.info(sourceFileName + " has been uploaded successfully!"); //$NON-NLS-1$
                            return buildUploadResult(true, targetUri);
                        } else if (rtnStatus == -1) {
                            String msg = "Unavailable file type! "; //$NON-NLS-1$
                            logger.error(msg);
                            return buildUploadResult(false, msg);
                        } else if (rtnStatus == -2) {
                            String msg = "Operation rolled back, since backuping to database failed."; //$NON-NLS-1$
                            logger.error(msg);
                            return buildUploadResult(false, msg);
                        }
                    }

                }

            }

        } catch (SizeLimitExceededException e) {
            logger.error("File Size Limit Exceeded Exception!", e); //$NON-NLS-1$
            return buildUploadResult(false, "File Size Limit Exceeded Exception!"); //$NON-NLS-1$
        } catch (Exception e) {
            logger.error("Exception occured during uploading!", e); //$NON-NLS-1$
            return buildUploadResult(false, "Exception occured during uploading!"); //$NON-NLS-1$
        }

        return buildUploadResult(false,
                "It seems that Upload Task has not been executed, please check your post enctype and post field name!"); //$NON-NLS-1$
    }

    /**
     * @param item
     * @param writeToFile
     * @param bakInDB
     * @param bakUseTransaction
     * @return 1 :success 0 :unknown failure -1:nonsupport type -2:roll back, failure in DB Backup
     * @throws Exception
     */
    private int processUploadedFile(FileItem item, boolean writeToFile, boolean inBakInDB, boolean inBakUseTransaction)
            throws Exception {

        if (!item.isFormField()) {
            String fileName = item.getName();
            String uid = Uuid.get32Code().toString();

            String[] fileParsedResult = parseFileFullName(fileName);
            sourceFileName = fileParsedResult[0];
            sourceFileType = fileParsedResult[1];
            if (targetFileShortName != null && targetFileShortName.trim().length() > 0) {
                // do nothing
            } else if (!changeFileName) {
                targetFileShortName = sourceFileName;
            } else {
                targetFileShortName = uid;
            }
            if (!okFileTypes.contains(this.sourceFileType.toLowerCase())) {
                return -1;
            }

            if (writeToFile) {
                StringBuffer upath = new StringBuffer();
                if (StringUtils.isEmpty(targetCatalogName)) {
                    targetCatalogName = generateCatalogName();
                }

                upath.append(uploadPath);
                if (!targetCatalogName.equals("/")) {
                    upath.append(File.separator).append(targetCatalogName);
                }
                locateCatalog(upath);
                targetFileName = (targetFileShortName + "." + sourceFileType); //$NON-NLS-1$
                upath.append(File.separator).append(targetFileName);

                if (!targetCatalogName.equals("/")) {
                    targetUri += ("/" + targetCatalogName); //$NON-NLS-1$
                }
                targetUri += ("/" + targetFileShortName + "." + sourceFileType); //$NON-NLS-1$ //$NON-NLS-2$

                File uploadedFile = new File(upath.toString());
                item.write(uploadedFile);

                if (inBakInDB) {
                    boolean isBakOK = false;

                    DBDelegate dbDelegate = (DBDelegate) ReflectionUtil.newInstance(dbDelegateClass, new Object[0]);
                    isBakOK = dbDelegate.putResource(new ResourcePK(targetCatalogName, targetFileName), upath.toString());

                    if (!isBakOK && inBakUseTransaction) {
                        uploadedFile.delete();
                        logger.debug("Rolled back in image server. "); //$NON-NLS-1$
                        return -2;
                    }

                }

                return 1;

            } else {
                InputStream uploadedStream = item.getInputStream();
                uploadedStream.close();
            }
        }
        return 0;

    }

    private Object getFileItem(List<FileItem> fileItems, String fieldName) {

        for (FileItem item : fileItems) {
            String name = item.getFieldName();
            if (StringUtils.isNotEmpty(name) && name.equals(fieldName)) {

                if (item.isFormField()) {
                    String value = ""; //$NON-NLS-1$

                    try {
                        value = new String(item.getString("UTF-8")); //$NON-NLS-1$
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    return value;
                } else {

                    return item;

                }

            }

        }
        return null;
    }

    private String[] parseFileFullName(String fileName) {
        String[] result = new String[2];
        String simpleFileName = ""; //$NON-NLS-1$
        if (fileName.indexOf("/") == -1 && fileName.indexOf("\\") == -1) { //$NON-NLS-1$ //$NON-NLS-2$
            simpleFileName = fileName;
        } else {
            String regExp = ".+\\\\(.+)$"; //$NON-NLS-1$
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(fileName);
            m.find();
            simpleFileName = m.group(1);
        }

        int point = simpleFileName.lastIndexOf("."); //$NON-NLS-1$
        result[0] = simpleFileName.substring(0, point);
        result[1] = simpleFileName.substring(point + 1);

        return result;
    }

    private String generateCatalogName() {
        String catalogName;
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        String showMonth = month < 10 ? ("0" + month) : String.valueOf(month); //$NON-NLS-1$
        catalogName = "c" + year + showMonth; //$NON-NLS-1$
        return catalogName;
    }

    private void locateCatalog(StringBuffer basePath) {
        File d = new File(basePath.toString());
        if (!d.exists()) {
            d.mkdir();
            logger.info("The catalog folder of this file has been created yet."); //$NON-NLS-1$
        }
    }

    private String buildUploadResult(boolean success, String message) {
        StringBuffer sb = new StringBuffer();

        if (outputFormat.equals("xml")) { //$NON-NLS-1$

            sb.append("<UploadResult>"); //$NON-NLS-1$
            sb.append("<success>" + success + "</success>"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("<message>").append(message).append("</message>"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("</UploadResult>"); //$NON-NLS-1$

        } else if (outputFormat.equals("json")) { //$NON-NLS-1$

            sb.append("{"); //$NON-NLS-1$
            sb.append("\"success\":" + success + ","); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("\"message\":\"").append(message).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("}"); //$NON-NLS-1$

        }

        return sb.toString();

    }

}
