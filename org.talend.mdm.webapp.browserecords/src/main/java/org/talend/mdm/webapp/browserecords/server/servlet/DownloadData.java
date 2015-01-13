package org.talend.mdm.webapp.browserecords.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.server.util.XmlUtil;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.client.util.LabelUtil;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.ViewHelper;
import org.talend.mdm.webapp.browserecords.server.util.DownloadUtil;
import org.talend.mdm.webapp.browserecords.shared.Constants;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;

public class DownloadData extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(DownloadData.class);

    private static final long serialVersionUID = 1L;

    protected final Integer maxCount = 1000;

    private final String SHEET_LABEL = "Talend MDM"; //$NON-NLS-1$

    protected final String DOWNLOADFILE_EXTEND_NAME = ".xls"; //$NON-NLS-1$

    protected String fileName = ""; //$NON-NLS-1$

    private String multipleValueSeparator = null;

    protected String dataCluster = ""; //$NON-NLS-1$

    protected String concept = ""; //$NON-NLS-1$

    private boolean fkResovled = false;

    private String criteria = ""; //$NON-NLS-1$

    private String language = "en"; //$NON-NLS-1$

    private String fkDisplay = ""; //$NON-NLS-1$

    protected EntityModel entity = null;

    private String[] headerArray = null;

    protected String[] xpathArray = null;

    private List<String> idsList = null;

    private Map<String, String> colFkMap = null;

    private Map<String, List<String>> fkMap = null;

    protected int columnIndex = 0;

    protected HSSFCellStyle cs = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();
        cs = workbook.createCellStyle();
        HSSFFont f = workbook.createFont();
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cs.setFont(f);
        HSSFSheet sheet = workbook.createSheet(SHEET_LABEL);
        sheet.setDefaultColumnWidth((short) 20);
        HSSFRow row = sheet.createRow((short) 0);
        try {
            setParameter(request);
            response.reset();
            response.setContentType("application/vnd.ms-excel"); //$NON-NLS-1$
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            fillHeader(row);
            fillSheet(sheet);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    private void setParameter(HttpServletRequest request) throws Exception {
        String tableName = request.getParameter("tableName"); //$NON-NLS-1$
        generateFileName(new String(request.getParameter("fileName").getBytes("iso-8859-1"), "UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String header = new String(request.getParameter("header").getBytes("iso-8859-1"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$        
        String xpath = request.getParameter("xpath"); //$NON-NLS-1$
        fkResovled = Boolean.valueOf(request.getParameter("fkResovled")); //$NON-NLS-1$
        if (fkResovled) {
            colFkMap = new HashMap<String, String>();
            fkMap = new HashMap<String, List<String>>();
            String fkColXPath = request.getParameter("fkColXPath"); //$NON-NLS-1$
            String fkInfo = request.getParameter("fkInfo"); //$NON-NLS-1$
            DownloadUtil.assembleFkMap(colFkMap, fkMap, fkColXPath, fkInfo);
        }
        dataCluster = request.getParameter("dataCluster"); //$NON-NLS-1$
        concept = ViewHelper.getConceptFromDefaultViewName(tableName);
        headerArray = DownloadUtil.convertXml2Array(header, "header"); //$NON-NLS-1$
        xpathArray = DownloadUtil.convertXml2Array(xpath, "xpath"); //$NON-NLS-1$
        criteria = request.getParameter("criteria"); //$NON-NLS-1$
        language = request.getParameter("language"); //$NON-NLS-1$
        fkDisplay = request.getParameter("fkDisplay"); //$NON-NLS-1$
        multipleValueSeparator = request.getParameter("multipleValueSeparator"); //$NON-NLS-1$

        if (request.getParameter("itemIdsListString") != null && !request.getParameter("itemIdsListString").isEmpty()) { //$NON-NLS-1$ //$NON-NLS-2$
            idsList = org.talend.mdm.webapp.base.shared.util.CommonUtil.convertStrigToList(
                    request.getParameter("itemIdsListString"), Constants.FILE_EXPORT_IMPORT_SEPARATOR); //$NON-NLS-1$
        }
    }

    protected void fillHeader(HSSFRow row) {

        for (int i = 0; i < headerArray.length; i++) {
            HSSFCell cell = row.createCell((short) i);
            cell.setCellValue(headerArray[i]);
            cell.setCellStyle(cs);
        }
    }

    protected void fillSheet(HSSFSheet sheet) throws Exception {
        entity = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getEntityModel(concept, language);
        List<String> results = new LinkedList<String>();
        // This blank line is for excel file header
        results.add(""); //$NON-NLS-1$ 
        if (idsList != null) {
            for (String ids : idsList) {
                results.add(CommonUtil
                        .getPort()
                        .getItem(
                                new WSGetItem(new WSItemPK(new WSDataClusterPK(dataCluster), concept, CommonUtil
                                        .extractIdWithDots(entity.getKeys(), ids)))).getContent());
            }
        } else {
            results = Arrays.asList(CommonUtil
                    .getPort()
                    .getItems(
                            new WSGetItems(new WSDataClusterPK(dataCluster), concept, (criteria != null ? CommonUtil
                                    .buildWhereItem(criteria) : null), -1, new Integer(0), maxCount, false)).getStrings());
        }
        for (int i = 1; i < results.size(); i++) {
            Document document = XmlUtil.parseText(results.get(i));
            HSSFRow row = sheet.createRow((short) i);
            fillRow(row, document);
        }
    }

    protected void fillRow(HSSFRow row, Document document) throws Exception {
        columnIndex = 0;
        for (String xpath : xpathArray) {
            String tmp = null;
            if (DownloadUtil.isJoinField(xpath, concept)) {
                tmp = getNodeValue(document, xpath);
                if (fkResovled) {
                    if (colFkMap.containsKey(xpath)) {
                        List<String> fkinfoList = fkMap.get(xpath);
                        if (!fkinfoList.get(0).trim().equalsIgnoreCase("") && !tmp.equalsIgnoreCase("")) { //$NON-NLS-1$ //$NON-NLS-2$
                            List<String> infoList = getFKInfo(colFkMap.get(xpath), fkinfoList, tmp);
                            if (fkDisplay.equalsIgnoreCase("Id-FKInfo")) { //$NON-NLS-1$
                                infoList.add(0, tmp);
                            }
                            tmp = LabelUtil.convertList2String(infoList, "-"); //$NON-NLS-1$
                        }
                    }
                }
            } else {
                tmp = DownloadUtil.getJoinFieldValue(document, xpath, columnIndex);
            }

            if (tmp != null) {
                tmp = tmp.trim();
                tmp = tmp.replaceAll("__h", ""); //$NON-NLS-1$ //$NON-NLS-2$
                tmp = tmp.replaceAll("h__", ""); //$NON-NLS-1$//$NON-NLS-2$
            } else {
                tmp = ""; //$NON-NLS-1$
            }
            row.createCell((short) columnIndex).setCellValue(tmp);
            columnIndex++;
        }
    }

    protected String getNodeValue(Document document, String xpath) {
        List<?> selectNodes = null;
        Map<String, String> namespaceMap = new HashMap<String, String>();
        namespaceMap.put(Constants.XSI_PREFIX, Constants.XSI_URI);
        List<String> valueList = null;
        boolean isAttribute = xpath.endsWith(Constants.XSI_TYPE_QUALIFIED_NAME) ? true : false;
        if (isAttribute) {
            XPath x = document.createXPath(xpath);
            x.setNamespaceURIs(namespaceMap);
            selectNodes = x.selectNodes(document);
        } else {
            selectNodes = document.selectNodes(xpath);
        }
        if (selectNodes != null) {
            valueList = new LinkedList<String>();
            for (Object object : selectNodes) {
                if (isAttribute) {
                    Attribute attribute = (Attribute) object;
                    valueList.add(attribute.getValue());
                } else {
                    Element element = (Element) object;
                    if (element.elements().size() > 0) {
                        valueList.add(element.asXML());
                    } else {
                        valueList.add(element.getText());
                    }
                }
            }
        }
        if (valueList == null || valueList.size() == 0) {
            return ""; //$NON-NLS-1$
        } else {
            if (valueList.size() > 1) {
                return CommonUtil.joinStrings(valueList, multipleValueSeparator);
            } else {
                return valueList.get(0);
            }
        }
    }

    private List<String> getFKInfo(String fk, List<String> fkInfoList, String fkValue) throws Exception {
        List<String> infoList = new ArrayList<String>();
        String conceptName = fk.substring(0, fk.indexOf("/")); //$NON-NLS-1$
        String value = LabelUtil.removeBrackets(fkValue);
        String ids[] = { value };
        WSItem wsItem = Util.getPort().getItem(new WSGetItem(new WSItemPK(new WSDataClusterPK(dataCluster), conceptName, ids)));
        Document doc = XmlUtil.parseText(wsItem.getContent());
        for (String xpath : fkInfoList) {
            infoList.add(XmlUtil.queryNodeText(doc, xpath));
        }
        return infoList;
    }

    protected void generateFileName(String name) {
        fileName = name + DOWNLOADFILE_EXTEND_NAME;
    }

    protected String getCurrentDataCluster() throws Exception {
        return org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getCurrentDataCluster(false);
    }
}
