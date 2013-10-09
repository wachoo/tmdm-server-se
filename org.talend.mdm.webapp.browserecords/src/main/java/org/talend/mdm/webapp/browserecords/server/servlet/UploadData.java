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

    private boolean cusExceptionFlag = false;
    
    private Map<String,Integer> xsiTypeMap = new HashMap<String,Integer>();
    
    private EntityModel entityModel = null;
    
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
        String fileType = "";//$NON-NLS-1$
        String sep = ",";//$NON-NLS-1$
        String textDelimiter = "\"";//$NON-NLS-1$        
        String encoding = "utf-8";//$NON-NLS-1$
        String headerString = ""; //$NON-NLS-1$
        Map<String, Boolean> headerVisibleMap = new LinkedHashMap<String, Boolean>();
        String mandatoryField = ""; //$NON-NLS-1$
        List<String> inheritanceNodePathList = null;
        String language = "en"; //$NON-NLS-1$
        boolean headersOnFirstLine = false;
        int rowNumber = 0;

        request.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        response.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        PrintWriter writer = response.getWriter();
        
        QName xsiTypeQName = new QName(Constants.XSI_TYPE_NAME, new Namespace(Constants.XSI_PREFIX,Constants.XSI_URI), Constants.XSI_TYPE_QUALIFIED_NAME);

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
                    if (name.equals("concept")) { //$NON-NLS-1$
                        concept = item.getString();
                    }
                    else if (name.equals("sep")) { //$NON-NLS-1$
                        sep = item.getString();
                    }
                    else if (name.equals("delimiter")) { //$NON-NLS-1$
                        textDelimiter = item.getString();
                    }
                    else if (name.equals("language")) { //$NON-NLS-1$
                        language = item.getString();
                    }
                    else if (name.equals("encodings")) { //$NON-NLS-1$
                        encoding = item.getString();
                    }
                    else if (name.equals("header")) { //$NON-NLS-1$
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
                } else {
                    fileType = FileUtil.getFileType(item.getName());
                    file = File.createTempFile("upload", "tmp");//$NON-NLS-1$//$NON-NLS-2$
                    LOG.debug("doPost() data uploaded in " + file.getAbsolutePath()); //$NON-NLS-1$
                    file.deleteOnExit();
                    item.write(file);
                }// if field
            }// while item

            entityModel = new EntityModel();
            DataModelHelper.parseSchema(org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getCurrentDataModel(), concept, entityModel, RoleHelper.getUserRoles());
            TypeModel typeModel = entityModel.getTypeModel(concept);

            Locale locale = new Locale(language);
            if (!UploadUtil.isViewableXpathValid(headerVisibleMap.keySet(), concept)) {
                throw new ServletException(MESSAGES.getMessage(locale, "error_invaild_field", concept)); //$NON-NLS-1$
            }

            Set<String> mandatorySet = UploadUtil.chechMandatoryField(org.talend.mdm.webapp.base.shared.util.CommonUtil.unescape(mandatoryField), headerVisibleMap.keySet());

            if (mandatorySet.size() > 0) {
                cusExceptionFlag = true;
                throw new ServletException(MESSAGES.getMessage(locale, "error_missing_mandatory_field")); //$NON-NLS-1$
            }

            fileInputStream = new FileInputStream(file);

            Set<String> modelHeader = headerVisibleMap.keySet();
            String[] importHeader = headersOnFirstLine ? null : getHeader(modelHeader);
            Map<String,String> recordMap;
            StringBuffer field;
            String fieldValue = ""; //$NON-NLS-1$

            Configuration configuration = Configuration.loadConfigurationFromDBDirectly();

            if ("xls".equals(fileType.toLowerCase()) || "xlsx".equals(fileType.toLowerCase())) {//$NON-NLS-1$ //$NON-NLS-2$
                Document document = null;
                Workbook workBook = null;
                if ("xls".equals(fileType.toLowerCase())) { //$NON-NLS-1$
                    poiFSFile = new POIFSFileSystem(fileInputStream);
                    workBook = new HSSFWorkbook(poiFSFile);
                } else {
                    workBook = new XSSFWorkbook(new FileInputStream(file));
                }
                Sheet sheet = workBook.getSheetAt(0);
                Iterator<Row> it = sheet.rowIterator();

                while (it.hasNext()) {
                    rowNumber++;
                    Row row = it.next();
                    if (rowNumber == 1 && headersOnFirstLine) {
                        importHeader = getHeader(row, headerVisibleMap.keySet(), concept, locale);
                        continue;
                    }
                    document = XmlUtil.parseDocument(org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getSubXML(typeModel, null, null,
                            language));
                    boolean allCellsEmpty = true;
                    recordMap = new HashMap<String,String>();
                    Element root = document.getRootElement();
                    Element currentElement = null;
                    for (int i = 0; i < importHeader.length; i++) {
                        Cell tmpCell = row.getCell(i);
                        if (tmpCell != null) {
                            if (importHeader[i].endsWith(Constants.XSI_TYPE_QUALIFIED_NAME)) {
                                continue;
                            }
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
                                        fieldValue = "true";//$NON-NLS-1$
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
                                }
                                default: {
                                }
                            }

                            if (fieldValue != null && !"".equals(fieldValue)) { //$NON-NLS-1$
                                allCellsEmpty = false;
                                currentElement = root;
                                String[] xpathPartArray = importHeader[i].split("/"); //$NON-NLS-1$
                                for (int j=1;j<xpathPartArray.length;j++) {
                                    if (currentElement != null) {
                                        currentElement = currentElement.element(xpathPartArray[j]);
                                        String currentElemntPath = currentElement.getPath().substring(1);
                                        if (inheritanceNodePathList != null && inheritanceNodePathList.contains(currentElemntPath)) {
                                            Integer xsiTypeIndex = xsiTypeMap.get(currentElemntPath + "/@" + Constants.XSI_TYPE_QUALIFIED_NAME); //$NON-NLS-1$
                                            if (xsiTypeIndex != null) {
                                                String xsiTypeValue = row.getCell(xsiTypeIndex).getRichStringCellValue().getString();
                                                if (!xsiTypeValue.equals(currentElement.attributeValue(xsiTypeQName))) {
                                                    currentElement.setAttributeValue(xsiTypeQName, xsiTypeValue);
                                                }
                                            } else {
                                                cusExceptionFlag = true;
                                                throw new ServletException(MESSAGES.getMessage(locale, "missing_attribute",currentElemntPath + "/@" + Constants.XSI_TYPE_QUALIFIED_NAME)); //$NON-NLS-1$ //$NON-NLS-2$
                                            }
                                        }
                                        
                                        if (j == xpathPartArray.length -1) {
                                            currentElement.setText(fieldValue);  
                                        } 
                                    }
                                }                                
                            }
                        }
                    }
                    if (!allCellsEmpty) {
                        wSPutItemWithReportList.add(getWSPutItemWithReport(document.asXML(), language, concept,
                                configuration.getCluster(), configuration.getModel()));
                    }
                }
            } else if ("csv".equals(fileType.toLowerCase())) { //$NON-NLS-1$                
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
                        importHeader = getHeader(record, separator, headerVisibleMap.keySet(), concept, locale);
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
                            if (j < record.length && headerVisibleMap.get(importHeader[j])) {
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
                        base = beforePoint + "" + afterPoint;//$NON-NLS-1$
                        puissanceValue -= afterPoint.length();
                    } else {
                        String newBeforePoint = beforePoint + "" + afterPoint.substring(0, puissanceValue);//$NON-NLS-1$
                        String newAfterPoint = afterPoint.substring(puissanceValue);
                        base = newBeforePoint + "." + newAfterPoint;//$NON-NLS-1$
                        puissanceValue = 0;
                    }
                }

                for (int j = 0; j < puissanceValue; j++) {
                    base += "0";//$NON-NLS-1$
                }

                result = base;

            } catch (NumberFormatException e) {
            }
        }
        return result;
    }

    private String[] getHeader(Row headerRow, Set<String> headerSet, String concept, Locale locale) throws ServletException {
        List<String> headers = new LinkedList<String>();
        Iterator<Cell> iter = headerRow.cellIterator();
        while (iter.hasNext()) {
            Cell cell = iter.next();
            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                String fieldName = cell.getRichStringCellValue().getString();
                if (fieldName.endsWith(Constants.XSI_TYPE_QUALIFIED_NAME)) {
                    xsiTypeMap.put(concept + "/" + fieldName, cell.getColumnIndex()); //$NON-NLS-1$
                }
                String elementPath = concept + "/" + fieldName; //$NON-NLS-1$
                if (headerSet.contains(elementPath)) {
                    headers.add(elementPath);
                } else {
                    cusExceptionFlag = true;
                    throw new ServletException(MESSAGES.getMessage(locale, "error_column_header", fieldName, concept)); //$NON-NLS-1$
                }
            }
        }
        return headers.toArray(new String[headers.size()]);
    }
    
    private String[] getHeader(Set<String> headerSet) throws ServletException {
        Iterator<String> iter = headerSet.iterator();
        int index = 0;
        while (iter.hasNext()) {
            String fieldName = iter.next();
            if (fieldName.endsWith(Constants.XSI_TYPE_QUALIFIED_NAME)) {
                xsiTypeMap.put(fieldName, index);
            }
            index++;
        }
        return headerSet.toArray(new String[headerSet.size()]);
    }

    private String[] getHeader(String[] headerRecord, char separator, Set<String> headerSet, String concept, Locale locale)
            throws ServletException {
        for (String element : headerRecord) {
            String fieldName = element;
            if (!headerSet.contains(fieldName)) {
                cusExceptionFlag = true;
                throw new ServletException(MESSAGES.getMessage(locale, "error_column_header", fieldName, concept)); //$NON-NLS-1$
            }
        }
        return headerRecord;
    }
}
