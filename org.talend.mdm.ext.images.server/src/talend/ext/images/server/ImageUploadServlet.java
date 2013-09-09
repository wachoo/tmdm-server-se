package talend.ext.images.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
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

public class ImageUploadServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(this.getClass());

    private List okFileTypes = null;

    private String defaultFilefieldName = "";

    private String defaultCatalogfieldName = "";
    private String defaultFileNamefieldName = "";

    private String outputFormat = "xml";

    private String bakInDB = "false";

    private String dbDelegateClass = "";

    private String bakUseTransaction = "false";

    private String uploadPath = null;;

    private String tempPath = null;

    private String sourceFileName = "";

    private String sourceFileType = "";

    private String targetUri = "upload";

    private String targetCatalogName = "";

    private String targetFileName = "";
    private String targetFileShortName = "";

    private boolean changeFileName = true;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String types = config.getInitParameter("image-file-types");
        String[] typeArray = types.split("\\|");
        okFileTypes = Arrays.asList(typeArray);
        defaultFilefieldName = config.getInitParameter("default-file-field-name");
        defaultFileNamefieldName = config.getInitParameter("default-filename-field-name");
        defaultCatalogfieldName = config.getInitParameter("default-catalog-field-name");
        outputFormat = config.getInitParameter("output-format");
        bakInDB = config.getInitParameter("bak-in-db");
        dbDelegateClass = config.getInitParameter("db-delegate-class");
        bakUseTransaction = config.getInitParameter("bak-use-transaction");

        ServletContext sc = config.getServletContext();
        uploadPath = (String) sc.getAttribute(ImageServerInfoServlet.UPLOAD_PATH);
        tempPath = (String) sc.getAttribute(ImageServerInfoServlet.TEMP_PATH);

        if ( uploadPath == null || tempPath == null ) { 
           throw new UnavailableException("Image Upload directory or Upload temp direcotry is not available!"); //$NON-NLS-1$
        } else {
            logger.debug("Images Upload Base Path: " + uploadPath); //$NON-NLS-1$
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        targetUri = "upload";// reset
        String change = request.getParameter("changeFileName");
        if (change != null) {
            changeFileName = Boolean.valueOf(change);
        }
        logger.debug("changeFileName: " + changeFileName);

        String result = onUpload(request, response);
        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        PrintWriter writer = response.getWriter();
        writer.write(result.toString());
        writer.close();
    }

    private String onUpload(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Check that we have a file upload request
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart) {

                DiskFileItemFactory factory = new DiskFileItemFactory();
                // Set cache buffer 4kb
                factory.setSizeThreshold(4096);
                // If file size overflow store in temporary folder
                factory.setRepository(new File(tempPath));

                // Create a new file upload handler
                ServletFileUpload sevletFileUpload = new ServletFileUpload(factory);
                // Set max size, 4m
                sevletFileUpload.setSizeMax(4 * 1024 * 1024);
                sevletFileUpload.setHeaderEncoding("utf8");
                // Create a progress listener
                ProgressListener progressListener = new ProgressListener() {

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

                // Parse the request
                List fileItems = null;

                fileItems = sevletFileUpload.parseRequest(request);

                // Process upload
                if (fileItems != null && fileItems.size() > 0) {
                    Object uploadFileItemObj = getFileItem(fileItems, defaultFilefieldName);
                    if (uploadFileItemObj != null) {
                        FileItem uploadFileItem = (FileItem) uploadFileItemObj;

                        Object catalogNameObj = getFileItem(fileItems, defaultCatalogfieldName);
                        if (catalogNameObj != null)
                            targetCatalogName = (String) catalogNameObj;

                        Object fileNameObj = getFileItem(fileItems, defaultFileNamefieldName);
                        if (fileNameObj != null)
                            targetFileShortName = (String) fileNameObj;
                        else{
                            String name = uploadFileItem.getName();
                            int pos=name.lastIndexOf('.');
                            if(pos!=-1){
                                name=name.substring(0,pos);                                
                            }
                            targetFileShortName=name;
                        }// FIXME this will make the if conditions on line 227 to 233 useless

                        int rtnStatus = processUploadedFile(uploadFileItem, true, Boolean.parseBoolean(bakInDB),
                                Boolean.parseBoolean(bakUseTransaction));

                        if (rtnStatus == 1) {
                            logger.info(sourceFileName + " has been uploaded successfully!");
                            return buildUploadResult(true, targetUri);// Please do not change default output
                        }else if( rtnStatus == -1){
                            String msg = "Unavailable file type! ";
                            logger.error(msg);
                            return buildUploadResult(false, msg);
                        } else if (rtnStatus == -2) {
                            String msg = "Operation rolled back, since backuping to database failed. ";
                            logger.error(msg);
                            return buildUploadResult(false, msg);
                        }
                    }

                }

            }

        } catch (SizeLimitExceededException e) {
            e.printStackTrace();
            return buildUploadResult(false, "File Size Limit Exceeded Exception! ");
        } catch (Exception e) {
            e.printStackTrace();
            return buildUploadResult(false, "Exception occured during uploading! ");
        }

        return buildUploadResult(false,
                "It seems that Upload Task has not been executed, please check your post enctype and post field name! ");
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

        // Process a file upload
        if (!item.isFormField()) {
            String fieldName = item.getFieldName();
            String fileName = item.getName();
            String contentType = item.getContentType();
            boolean isInMemory = item.isInMemory();
            long sizeInBytes = item.getSize();

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
                // generate catalogName
                if (StringUtils.isEmpty(targetCatalogName))
                    targetCatalogName = generateCatalogName();

                upath.append(uploadPath);
                if (!targetCatalogName.equals("/"))
                    upath.append(File.separator).append(targetCatalogName);
                locateCatalog(upath);
                targetFileName = (targetFileShortName + "." + sourceFileType);
                upath.append(File.separator).append(targetFileName);

                if (!targetCatalogName.equals("/"))
                    targetUri += ("/" + targetCatalogName);
                targetUri += ("/" + targetFileShortName + "." + sourceFileType);

                File uploadedFile = new File(upath.toString());
                item.write(uploadedFile);

                if (inBakInDB) {
                    boolean isBakOK = false;

                    DBDelegate dbDelegate = (DBDelegate) ReflectionUtil.newInstance(dbDelegateClass, new Object[0]);
                    isBakOK = dbDelegate.putResource(new ResourcePK(targetCatalogName, targetFileName), upath.toString());

                    // do roll back
                    if(!isBakOK&&inBakUseTransaction){
                        uploadedFile.delete();
                        logger.debug("Rolled back in image server. ");
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

    private Object getFileItem(List fileItems, String fieldName) {

        for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
            FileItem item = (FileItem) iterator.next();
            String name = item.getFieldName();
            if (StringUtils.isNotEmpty(name) && name.equals(fieldName)) {

                if (item.isFormField()) {
                    String value = "";

                    try {
                        value = new String(item.getString("UTF-8"));
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
        String simpleFileName = "";
        if (fileName.indexOf("/") == -1 && fileName.indexOf("\\") == -1) {
            simpleFileName = fileName;
        } else {
            // get file full name through regExp
            String regExp = ".+\\\\(.+)$";
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(fileName);
            m.find();
            simpleFileName = m.group(1);
        }

        int point = simpleFileName.lastIndexOf(".");
        result[0] = simpleFileName.substring(0, point);
        result[1] = simpleFileName.substring(point + 1);

        return result;
    }

    private String generateCatalogName() {
        String catalogName;
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        String showMonth = month < 10 ? ("0" + month) : String.valueOf(month);
        catalogName = "c" + year + showMonth;
        return catalogName;
    }

    private void locateCatalog(StringBuffer basePath) {
        File d = new File(basePath.toString());
        if (d.exists()) {

        } else {
            d.mkdir();
            logger.info("The catalog folder of this file has been created yet. ");
        }
    }

    private String buildUploadResult(boolean success, String message) {
        StringBuffer sb = new StringBuffer();

        if (outputFormat.equals("xml")) {

            sb.append("<UploadResult>");
            sb.append("<success>" + success + "</success>");
            sb.append("<message>").append(message).append("</message>");
            sb.append("</UploadResult>");

        } else if (outputFormat.equals("json")) {

            sb.append("{");
            sb.append("\"success\":" + success + ",");
            sb.append("\"message\":\"").append(message).append("\"");
            sb.append("}");

        }

        return sb.toString();

    }

}
