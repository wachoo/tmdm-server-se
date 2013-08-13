package org.talend.mdm.webapp.browserecords.server.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.shared.FileUtil;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.ViewHelper;
import org.talend.mdm.webapp.browserecords.server.util.CSVReader;
import org.talend.mdm.webapp.browserecords.server.util.UploadUtil;
import org.talend.mdm.webapp.browserecords.shared.Constants;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSPutItemWithReport;
import com.amalto.webapp.util.webservices.WSPutItemWithReportArray;

/**
 * 
 * @author asaintguilhem
 * 
 * read excel and csv file
 */

@SuppressWarnings("serial")
public class UploadData extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(UploadData.class);

    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages", UploadData.class.getClassLoader()); //$NON-NLS-1$

    private boolean cusExceptionFlag = false;

    public UploadData() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        doPost(arg0, arg1);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<WSPutItemWithReport> wSPutItemWithReportList = new LinkedList<WSPutItemWithReport>();
        FileInputStream fileInputStream = null;
        POIFSFileSystem poiFSFile = null;
        CSVReader csvReader = null;
        String concept = "";//$NON-NLS-1$
        String viewPK = ""; //$NON-NLS-1$
        String fileType = "";//$NON-NLS-1$
        String sep = ",";//$NON-NLS-1$
        String textDelimiter = "\"";//$NON-NLS-1$        
        String encoding = "utf-8";//$NON-NLS-1$
        String header = ""; //$NON-NLS-1$
        String mandatoryField = ""; //$NON-NLS-1$
        String viewableXpath = ""; //$NON-NLS-1$
        String language = "en"; //$NON-NLS-1$

        boolean headersOnFirstLine = false;
        int rowNumber = 0;

        request.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        response.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        PrintWriter writer = response.getWriter();

        try {
            if (!FileUploadBase.isMultipartContent(request)) {
                throw new ServletException(MESSAGES.getMessage("error_upload"));//$NON-NLS-1$
            }
            // Create a new file upload handler
            DiskFileUpload upload = new DiskFileUpload();

            // Set upload parameters
            upload.setSizeThreshold(0);
            upload.setSizeMax(-1);

            // Parse the request
            List items = upload.parseRequest(request);

            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMyy-HHmmssSSS"); //$NON-NLS-1$     

            File file = null;
            // Process the uploaded items
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                // FIXME: should handle more than files in parts e.g. text passed as parameter
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    // we are not expecting any field just (one) file(s)
                    String name = item.getFieldName();
                    LOG.debug("doPost() Field: '" + name + "' - value:'" + item.getString() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    if (name.equals("concept")) {
                        viewPK = item.getString();
                    }
                    if (name.equals("sep")) {
                        sep = item.getString();
                    }
                    if (name.equals("delimiter")) {
                        textDelimiter = item.getString();
                    }
                    if (name.equals("language")) {
                        language = item.getString();
                    }
                    if (name.equals("encodings")) {
                        encoding = item.getString();
                    }
                    if (name.equals("header")) {
                        header = item.getString();
                    }
                    if (name.equals("mandatoryField")) {
                        mandatoryField = item.getString();
                    }
                    if (name.equals("viewableXpath")) {
                        viewableXpath = item.getString();
                    }
                    if (name.equals("headersOnFirstLine")) {
                        headersOnFirstLine = "on".equals(item.getString());//$NON-NLS-1$
                    }
                } else {
                    fileType = FileUtil.getFileType(item.getName());
                    file = File.createTempFile("upload", "tmp");//$NON-NLS-1$//$NON-NLS-2$
                    LOG.debug("doPost() data uploaded in " + file.getAbsolutePath()); //$NON-NLS-1$
                    file.deleteOnExit();
                    item.write(file);
                }// if field
            }// while item

            concept = ViewHelper.getConceptFromDefaultViewName(viewPK);
            Locale locale = new Locale(language);
            fileInputStream = new FileInputStream(file);

            Configuration configuration = Configuration.loadConfigurationFromDBDirectly();

            if ("xls".equals(fileType.toLowerCase()) || "xlsx".equals(fileType.toLowerCase())) {//$NON-NLS-1$ //$NON-NLS-2$
                Workbook workBook;
                if ("xls".equals(fileType.toLowerCase())) { //$NON-NLS-1$
                    poiFSFile = new POIFSFileSystem(fileInputStream);
                    workBook = new HSSFWorkbook(poiFSFile);
                } else {
                    workBook = new XSSFWorkbook(new FileInputStream(file));
                }
                Sheet sheet = workBook.getSheetAt(0);
                Iterator<Row> it = sheet.rowIterator();
                String cellValue = ""; //$NON-NLS-1$
                while (it.hasNext()) {
                    rowNumber++;
                    Row row = it.next();

                    Cell cell = row.getCell(0);
                    if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue() != null) {
                        cellValue = cell.getStringCellValue();
                    }

                    if (rowNumber == 1) {
                        if (Constants.XML_CONTENT_HEADER.equals(cellValue)) {
                            continue;
                        } else {
                            cusExceptionFlag = true;
                            throw new ServletException(MESSAGES.getMessage(locale, "error_missing_xmlcontent_column")); //$NON-NLS-1$
                        }
                    }
                    wSPutItemWithReportList.add(getWSPutItemWithReport(cellValue, language, concept, configuration.getCluster(),
                            configuration.getModel()));
                }
            } else if ("csv".equals(fileType.toLowerCase())) { //$NON-NLS-1$

                Map<String, Boolean> visibleMap = UploadUtil.getVisibleMap(header);
                Set<String> mandatorySet = UploadUtil.chechMandatoryField(mandatoryField, visibleMap.keySet());
                String[] modelHeader = UploadUtil.getDefaultHeader(header);
                String[] importHeader = headersOnFirstLine ? null : modelHeader;

                Map recordMap;
                StringBuffer field;

                if (mandatorySet.size() > 0) {
                    cusExceptionFlag = true;
                    throw new ServletException(MESSAGES.getMessage(locale, "error_missing_mandatory_field")); //$NON-NLS-1$
                }
                if (!UploadUtil.isViewableXpathValid(viewableXpath, concept)) {
                    throw new ServletException(MESSAGES.getMessage(locale, "error_invaild_field", concept)); //$NON-NLS-1$
                }
                char separator = ',';
                if ("semicolon".equals(sep)) {
                    separator = ';';
                }
                csvReader = new CSVReader(new InputStreamReader(fileInputStream, encoding), separator, textDelimiter.charAt(0));
                List<String[]> records = csvReader.readAll();
                for (int i = 0; i < records.size(); i++) {
                    rowNumber++;
                    String[] record = records.get(i);
                    if (rowNumber == 1 && headersOnFirstLine) {
                        importHeader = getHeader(record, separator, header, concept, locale);
                        continue;
                    }

                    StringBuffer xml = new StringBuffer();
                    xml.append("<" + concept + ">");//$NON-NLS-1$//$NON-NLS-2$            

                    // build xml
                    recordMap = new HashMap();
                    if (record.length > 0) {
                        for (int j = 0; j < importHeader.length; j++) {
                            field = new StringBuffer();
                            field.append("<" + importHeader[j] + ">");//$NON-NLS-1$//$NON-NLS-2$
                            if (j < record.length && visibleMap.get(importHeader[j])) {
                                field.append(StringEscapeUtils.escapeXml(record[j]));
                            }
                            field.append("</" + importHeader[j] + ">");//$NON-NLS-1$//$NON-NLS-2$
                            if (headersOnFirstLine) {
                                recordMap.put(importHeader[j], field.toString());
                            } else {
                                xml.append(field.toString());
                            }
                        }
                    }
                    if (headersOnFirstLine) {
                        for (String element : modelHeader) {
                            if (recordMap.get(element) != null) {
                                xml.append(recordMap.get(element));
                            }
                        }
                    }
                    xml.append("</" + concept + ">");//$NON-NLS-1$//$NON-NLS-2$                 
                    LOG.debug("Added line " + rowNumber + 1);//$NON-NLS-1$
                    LOG.trace("--val:\n" + xml);//$NON-NLS-1$
                    wSPutItemWithReportList.add(getWSPutItemWithReport(xml.toString(), language, concept,
                            configuration.getCluster(), configuration.getModel()));
                }
            }
            if (wSPutItemWithReportList.size() > 0) {
                putDocument(
                        new WSPutItemWithReportArray(
                                wSPutItemWithReportList.toArray(new WSPutItemWithReport[wSPutItemWithReportList.size()])),
                        concept);
            }
            writer.print("true"); //$NON-NLS-1$
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            if (cusExceptionFlag) {
                writer.print(e.getMessage());
            }
            throw (ServletException) e;
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            writer.close();
        }
    }

    private WSPutItemWithReport getWSPutItemWithReport(String xml, String language, String concept, String model, String cluster) {
        return new WSPutItemWithReport(new WSPutItem(new WSDataClusterPK(cluster), xml, new WSDataModelPK(model), false),
                "genericUI", true); //$NON-NLS-1$
    }

    private void putDocument(WSPutItemWithReportArray wSPutItemWithReportArray, String concept) throws ServletException {
        try {
            CommonUtil.getPort().putItemWithReportArray(wSPutItemWithReportArray);
        } catch (RemoteException exception) {
            cusExceptionFlag = true;
            throw new ServletException(MESSAGES.getMessage("save_fail", concept, UploadUtil.getRootCause(exception))); //$NON-NLS-1$
        } catch (Exception exception) {
            throw new ServletException(exception.getLocalizedMessage());
        }
    }

    private String[] getHeader(String[] headerRecord, char separator, String headerString, String concept, Locale locale)
            throws ServletException {
        for (String element : headerRecord) {
            String fieldName = element;
            if (!headerString.contains(fieldName)) {
                cusExceptionFlag = true;
                throw new ServletException(MESSAGES.getMessage(locale, "error_column_header", fieldName, concept)); //$NON-NLS-1$
            }
        }
        return headerRecord;
    }
}
