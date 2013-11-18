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
import java.util.LinkedHashMap;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.FileUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.RoleHelper;
import org.talend.mdm.webapp.browserecords.server.util.CSVReader;
import org.talend.mdm.webapp.browserecords.server.util.UploadUtil;
import org.talend.mdm.webapp.browserecords.shared.Constants;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XmlUtil;
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

    private final String FILE_TYPE_CSV_SUFFIX = "csv"; //$NON-NLS-1$
    
    private final String FILE_TYPE_EXCEL_SUFFIX = "xls"; //$NON-NLS-1$
    
    private final String FILE_TYPE_EXCEL2010_SUFFIX = "xlsx"; //$NON-NLS-1$
    
    private final String File_CSV_SEPARATOR_SEMICOLON = "semicolon"; //$NON-NLS-1$
    
    private boolean cusExceptionFlag = false;
    
    private String language = "en"; //$NON-NLS-1$
    
    private String encoding = "utf-8";//$NON-NLS-1$
    
    private String cluster = ""; //$NON-NLS-1$
    
    protected String concept = "";//$NON-NLS-1$
    
    private String fileType = "";//$NON-NLS-1$
    
    private String sep = ",";//$NON-NLS-1$
    
    private String textDelimiter = "\"";//$NON-NLS-1$
    
    private String headerString = ""; //$NON-NLS-1$
    
    private String mandatoryField = ""; //$NON-NLS-1$
    
    private String multipleValueSeparator = null;
    
    private Locale locale = null;
    
    private EntityModel entityModel = null;
    
    private File file = null;
    
    private Map<String, Boolean> headerVisibleMap = null;
    
    private Map<String,Integer> xsiTypeMap = null;
    
    private List<String> inheritanceNodePathList = null;
    
    private boolean headersOnFirstLine;
    
    private void setParameter(HttpServletRequest request) throws Exception {
        headersOnFirstLine = false;
        request.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        if (!FileUploadBase.isMultipartContent(request)) {
            throw new ServletException(MESSAGES.getMessage("error_upload"));//$NON-NLS-1$
        }
        // Create a new file upload handler
        DiskFileUpload upload = new DiskFileUpload();

        // Set upload parameters
        upload.setSizeThreshold(0);
        upload.setSizeMax(-1);

        // Parse the request
        List<FileItem> items = upload.parseRequest(request);

        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMyy-HHmmssSSS"); //$NON-NLS-1$     

        // Process the uploaded items
        Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
            // FIXME: should handle more than files in parts e.g. text passed as parameter
            FileItem item = iter.next();
            if (item.isFormField()) {
                // we are not expecting any field just (one) file(s)
                String name = item.getFieldName();
                LOG.debug("doPost() Field: '" + name + "' - value:'" + item.getString() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                if (name.equals("cluster")) { //$NON-NLS-1$
                    cluster = item.getString();
                } else if (name.equals("concept")) { //$NON-NLS-1$
                    concept = item.getString();
                }
                else if (name.equals("sep")) { //$NON-NLS-1$
                    sep = item.getString();
                }
                else if (name.equals("delimiter")) { //$NON-NLS-1$
                    textDelimiter = item.getString();
                }
                else if (name.equals("language")) { //$NON-NLS-1$
                    locale = new Locale(item.getString());
                }
                else if (name.equals("encodings")) { //$NON-NLS-1$
                    encoding = item.getString();
                }
                else if (name.equals("header")) { //$NON-NLS-1$
                    headerVisibleMap = new LinkedHashMap<String, Boolean>();
                    headerString = item.getString();
                    List<String> headerItemList = org.talend.mdm.webapp.base.shared.util.CommonUtil.convertStrigToList(headerString, Constants.FILE_EXPORT_IMPORT_SEPARATOR);
                    if (headerItemList != null) {
                        for (String headerItem : headerItemList) {
                            String[] headerItemArray = headerItem.split(Constants.HEADER_VISIBILITY_SEPARATOR);                                
                            headerVisibleMap.put(headerItemArray[0], Boolean.valueOf(headerItemArray[1]));                                
                        }
                    }
                }
                else if (name.equals("mandatoryField")) { //$NON-NLS-1$
                    mandatoryField = item.getString();
                }
                else if (name.equals("inheritanceNodePath")) { //$NON-NLS-1$
                    inheritanceNodePathList = org.talend.mdm.webapp.base.shared.util.CommonUtil.convertStrigToList(item.getString(), Constants.FILE_EXPORT_IMPORT_SEPARATOR);
                }
                else if (name.equals("headersOnFirstLine")) { //$NON-NLS-1$
                    headersOnFirstLine = "on".equals(item.getString());//$NON-NLS-1$
                }
                else if (name.equals("multipleValueSeparator")) { //$NON-NLS-1$
                    multipleValueSeparator = item.getString();
                }
            } else {
                fileType = FileUtil.getFileType(item.getName());
                file = File.createTempFile("upload", "tmp");//$NON-NLS-1$//$NON-NLS-2$
                LOG.debug("doPost() data uploaded in " + file.getAbsolutePath()); //$NON-NLS-1$
                file.deleteOnExit();
                item.write(file);
            }// if field
        }// while item
    }
    
    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        doPost(arg0, arg1);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        PrintWriter writer = response.getWriter();
        FileInputStream fileInputStream = null;
        CSVReader csvReader = null;
        entityModel = new EntityModel();
        String[] importHeader = null;
        List<WSPutItemWithReport> wSPutItemWithReportList = new LinkedList<WSPutItemWithReport>();
        try {
            setParameter(request);
            if (!UploadUtil.isViewableXpathValid(headerVisibleMap.keySet(), concept)) {
                throw new ServletException(MESSAGES.getMessage(locale, "error_invaild_field", concept)); //$NON-NLS-1$
            }
            Set<String> mandatorySet = UploadUtil.chechMandatoryField(org.talend.mdm.webapp.base.shared.util.CommonUtil.unescape(mandatoryField), headerVisibleMap.keySet());
            if (mandatorySet.size() > 0) {
                cusExceptionFlag = true;
                throw new ServletException(MESSAGES.getMessage(locale, "error_missing_mandatory_field")); //$NON-NLS-1$
            }
            fileInputStream = new FileInputStream(file);
            DataModelHelper.parseSchema(org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getCurrentDataModel(), concept, entityModel, RoleHelper.getUserRoles());
            TypeModel typeModel = entityModel.getTypeModel(concept);
            Configuration configuration = Configuration.loadConfigurationFromDBDirectly();
            Document document = XmlUtil.parseDocument(org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getSubXML(typeModel, null, null,
                    language));
            xsiTypeMap = new HashMap<String,Integer>();
            Element currentElement = document.getRootElement();
            String fieldValue = ""; //$NON-NLS-1$
            boolean dataLine = false;
            if (FILE_TYPE_EXCEL_SUFFIX.equals(fileType.toLowerCase()) || FILE_TYPE_EXCEL2010_SUFFIX.equals(fileType.toLowerCase())) {
                Workbook workBook = null;
                if (FILE_TYPE_EXCEL_SUFFIX.equals(fileType.toLowerCase())) {
                    POIFSFileSystem poiFSFile = new POIFSFileSystem(fileInputStream);
                    workBook = new HSSFWorkbook(poiFSFile);
                } else {
                    workBook = new XSSFWorkbook(new FileInputStream(file));
                }
                Sheet sheet = workBook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.rowIterator();
                int rowNumber = 0;
                while (rowIterator.hasNext()) {
                    dataLine = false;
                    rowNumber++;
                    Row row = rowIterator.next();
                    if (rowNumber == 1) {
                        importHeader = readHeader(row, null);
                        if (headersOnFirstLine) {
                            continue;
                        }
                    }
                    if (importHeader != null) {
                        for (int i = 0; i < importHeader.length; i++) {
                            if (!importHeader[i].endsWith(Constants.XSI_TYPE_QUALIFIED_NAME)) {
                                Cell tmpCell = row.getCell(i);
                                if (tmpCell != null) {
                                    int cellType = tmpCell.getCellType();
                                    switch (cellType) {
                                        case Cell.CELL_TYPE_NUMERIC: {
                                            double tmp = tmpCell.getNumericCellValue();
                                            fieldValue = getStringRepresentation(tmp);
                                            break;
                                        }
                                        case Cell.CELL_TYPE_STRING: {
                                            fieldValue = tmpCell.getRichStringCellValue().getString();
                                            int result = org.talend.mdm.webapp.browserecords.server.util.CommonUtil
                                                    .getFKFormatType(fieldValue);
                                            if (result > 0) {
                                                fieldValue = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getForeignKeyId(
                                                        fieldValue, result);
                                            }
                                            break;
                                        }
                                        case Cell.CELL_TYPE_BOOLEAN: {
                                            boolean tmp = tmpCell.getBooleanCellValue();
                                            if (tmp) {
                                                fieldValue = "true"; //$NON-NLS-1$
                                            } else {
                                                fieldValue = "false";//$NON-NLS-1$
                                            }
                                            break;
                                        }
                                        case Cell.CELL_TYPE_FORMULA: {
                                            fieldValue = tmpCell.getCellFormula();
                                            break;
                                        }
                                        case Cell.CELL_TYPE_ERROR: {
                                            break;
                                        }
                                        case Cell.CELL_TYPE_BLANK: {
                                            fieldValue = ""; //$NON-NLS-1$
                                        }
                                        default: {
                                        }
                                    }
                                    if (fieldValue != null && !fieldValue.isEmpty()) {
                                        dataLine = true;
                                        fillFieldValue(currentElement, importHeader[i], fieldValue, row, null);
                                    }                                
                                }
                            }
                        }
                        if (dataLine) {
                            wSPutItemWithReportList.add(buildWSPutItemWithReport(document,configuration.getModel()));
                        }
                    }
                }
            } else if (FILE_TYPE_CSV_SUFFIX.equals(fileType.toLowerCase())) {
                char separator = File_CSV_SEPARATOR_SEMICOLON.equals(sep) ? ';' : ',';
                csvReader = new CSVReader(new InputStreamReader(fileInputStream, encoding), separator, textDelimiter.charAt(0));
                List<String[]> records = csvReader.readAll();
                for (int i = 0; i < records.size(); i++) {
                    String[] record = records.get(i);
                    dataLine = false;
                    if (i == 0) {
                        importHeader = readHeader(null, record);
                        if (headersOnFirstLine) {
                            continue;
                        }
                    }
                    if (importHeader != null) {
                        if (record.length > 0) {
                            for (int j = 0; j < importHeader.length; j++) {
                                if (j < record.length && headerVisibleMap.get(importHeader[j]) != null && headerVisibleMap.get(importHeader[j])) {
                                    fieldValue = record[j];
                                    if (fieldValue != null && !fieldValue.isEmpty()) {
                                        dataLine =  true;
                                        fillFieldValue(currentElement, importHeader[j], fieldValue, null, record);
                                    }
                                }
                            }
                        }
                        if (dataLine) {
                            wSPutItemWithReportList.add(buildWSPutItemWithReport(document,configuration.getModel()));
                        }
                    }
                }
            }
            if (wSPutItemWithReportList.size() > 0) {
                putDocument(new WSPutItemWithReportArray(wSPutItemWithReportList.toArray(new WSPutItemWithReport[wSPutItemWithReportList.size()])));                                
            }
            writer.print("true"); //$NON-NLS-1$
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            if (cusExceptionFlag) {
                writer.print(e.getMessage());
            }
            throw e instanceof ServletException ? (ServletException) e : new ServletException(e.getMessage(), e);
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

    protected WSPutItemWithReport buildWSPutItemWithReport(Document document,String model) {
        return new WSPutItemWithReport(new WSPutItem(new WSDataClusterPK(cluster), document.asXML(), new WSDataModelPK(model), false),
                "genericUI", true); //$NON-NLS-1$
    }

    private void putDocument(WSPutItemWithReportArray wSPutItemWithReportArray) throws ServletException {
        try {
            CommonUtil.getPort().putItemWithReportArray(wSPutItemWithReportArray);
        } catch (RemoteException exception) {
            cusExceptionFlag = true;
            throw new ServletException(MESSAGES.getMessage("save_fail", concept, UploadUtil.getRootCause(exception))); //$NON-NLS-1$
        } catch (Exception exception) {
            throw new ServletException(exception.getLocalizedMessage());
        }
    }

    /*
     * Returns a string corresponding to the double value given in parameter Exponent is removed and "0" are added at
     * the end of the string if necessary This method is useful when you import long itemid that you don't want to see
     * modified by importation method.
     */
    private String getStringRepresentation(double value) {
        String result = ""; //$NON-NLS-1$

        result = Double.toString(value);

        int index = result.indexOf("E");//$NON-NLS-1$

        String base = result;

        if (index > 0) {
            try {
                base = result.substring(0, index);
                String puissance = result.substring(index + 1);

                int puissanceValue = Integer.parseInt(puissance);

                int indexPoint = base.indexOf(".");//$NON-NLS-1$

                if (indexPoint > 0) {
                    String beforePoint = base.substring(0, indexPoint);
                    String afterPoint = base.substring(indexPoint + 1);

                    if (puissanceValue >= afterPoint.length()) {
                        base = beforePoint + "" + afterPoint; //$NON-NLS-1$
                        puissanceValue -= afterPoint.length();
                    } else {
                        String newBeforePoint = beforePoint + "" + afterPoint.substring(0, puissanceValue);//$NON-NLS-1$
                        String newAfterPoint = afterPoint.substring(puissanceValue);
                        base = newBeforePoint + "." + newAfterPoint;//$NON-NLS-1$
                        puissanceValue = 0;
                    }
                }

                for (int j = 0; j < puissanceValue; j++) {
                    base += "0"; //$NON-NLS-1$
                }

                result = base;

            } catch (NumberFormatException e) {
            }
        }
        return result;
    }

    protected String[] readHeader(Row headerRow,String[] headerRecord) throws ServletException {
        List<String> headers = new LinkedList<String>();
        String header;
        int index = 0;
        if (headersOnFirstLine) {
            if (FILE_TYPE_EXCEL_SUFFIX.equals(fileType.toLowerCase()) || FILE_TYPE_EXCEL2010_SUFFIX.equals(fileType.toLowerCase())) {
                Iterator<Cell> headerIterator = headerRow.cellIterator();
                while (headerIterator.hasNext()) {
                    Cell cell = headerIterator.next();
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        header = cell.getRichStringCellValue().getString();
                        headers.add(handleHeader(header, index));
                    }
                    index++;
                }
            } else if (FILE_TYPE_CSV_SUFFIX.equals(fileType.toLowerCase())) {
                for (int i=0;i<headerRecord.length;i++) {
                    headers.add(handleHeader(headerRecord[i], index));
                    index++;
                }
            }
        } else {
            Iterator<String> headerIterator = headerVisibleMap.keySet().iterator();
            while (headerIterator.hasNext()) {
                header = headerIterator.next();
                registXsiType(header, index);
                headers.add(header);
                index++;
            }
        }
        return headers.toArray(new String[headers.size()]);
    }
    
    protected String handleHeader(String headerName,int index) throws ServletException {
        String headerPath = ""; //$NON-NLS-1$
        if (!headerName.startsWith(concept + "/")) { //$NON-NLS-1$
            headerPath = concept + "/" + headerName; //$NON-NLS-1$
        } else {
            headerPath = headerName;
        }
        if (!headerPath.endsWith(Constants.XSI_TYPE_QUALIFIED_NAME)) {
            if (entityModel.getTypeModel(headerPath) == null) {
                cusExceptionFlag = true;
                throw new ServletException(MESSAGES.getMessage(locale, "error_column_header", headerName, concept)); //$NON-NLS-1$
            }  
        } else {
            registXsiType(headerPath, index);
        }
        
        return headerPath;
    }
    
    private void registXsiType(String headerName,int index) throws ServletException {
        if (headerName.endsWith(Constants.XSI_TYPE_QUALIFIED_NAME)) {
            if (entityModel.getTypeModel(headerName.substring(0, headerName.indexOf("/@" + Constants.XSI_TYPE_QUALIFIED_NAME))) != null) { //$NON-NLS-1$
                xsiTypeMap.put(headerName, index);
            } else {
                cusExceptionFlag = true;
                throw new ServletException(MESSAGES.getMessage(locale, "error_column_header", headerName, concept)); //$NON-NLS-1$
            }
        }
    }
 
    protected void fillFieldValue(Element currentElement,String fieldPath,String fieldValue,Row row,String[] record) throws Exception {
        QName xsiTypeQName = new QName(Constants.XSI_TYPE_NAME, new Namespace(Constants.XSI_PREFIX,Constants.XSI_URI), Constants.XSI_TYPE_QUALIFIED_NAME);
        String[] xpathPartArray = fieldPath.split("/"); //$NON-NLS-1$
        for (int i=1;i<xpathPartArray.length;i++) {
            if (currentElement != null) {
                currentElement = currentElement.element(xpathPartArray[i]);
                if (i == xpathPartArray.length -1) {
                    if (fieldValue != null && !fieldValue.isEmpty()) {
                        if (multipleValueSeparator != null && !multipleValueSeparator.isEmpty() && fieldValue.contains(multipleValueSeparator)) {
                            List<String> valueList = CommonUtil.splitString(fieldValue, multipleValueSeparator);
                            for (int j = 0; j < valueList.size(); j++) {
                                List<Element> contentList = currentElement.getParent().content();
                                Element copyElement = currentElement.createCopy();
                                contentList.add(contentList.indexOf(currentElement) + j,copyElement);
                                setFieldValue(copyElement, valueList.get(j));
                            }
                        } else {
                            setFieldValue(currentElement,fieldValue);
                        }
                    }
                } else {
                    String currentElemntPath = currentElement.getPath().substring(1);
                    if (inheritanceNodePathList != null && inheritanceNodePathList.contains(currentElemntPath)) {
                        Integer xsiTypeIndex = xsiTypeMap.get(currentElemntPath + "/@" + Constants.XSI_TYPE_QUALIFIED_NAME); //$NON-NLS-1$
                        if (xsiTypeIndex != null) {
                            String xsiTypeValue = ""; //$NON-NLS-1$
                            if (FILE_TYPE_EXCEL_SUFFIX.equals(fileType.toLowerCase()) || FILE_TYPE_EXCEL2010_SUFFIX.equals(fileType.toLowerCase())) {
                                xsiTypeValue = row.getCell(xsiTypeIndex).getRichStringCellValue().getString();
                            } else if (FILE_TYPE_CSV_SUFFIX.equals(fileType.toLowerCase())) {
                                xsiTypeValue = record[i];
                            }                            
                            if (!xsiTypeValue.equals(currentElement.attributeValue(xsiTypeQName))) {
                                currentElement.setAttributeValue(xsiTypeQName, xsiTypeValue);
                            }
                        } else {
                            cusExceptionFlag = true;
                            throw new ServletException(MESSAGES.getMessage(locale, "missing_attribute",currentElemntPath + "/@" + Constants.XSI_TYPE_QUALIFIED_NAME)); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                }
            }
        }
    }
    
    private void setFieldValue(Element currentElement,String value) throws Exception {
        if (currentElement.elements().size() > 0) {                        
            Element complexeElement = XmlUtil.parseDocument(Util.parse(StringEscapeUtils.unescapeXml(value))).getRootElement();
            List<Element> contentList = currentElement.getParent().content();
            int index = contentList.indexOf(currentElement);
            contentList.remove(currentElement);
            contentList.add(index, complexeElement);
        } else {
            currentElement.setText(value);
        }
    }
}
