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

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;

public class DownloadData extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(DownloadData.class);

    private static final long serialVersionUID = 1L;
    
    private final Integer maxCount = 1000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName"); //$NON-NLS-1$
        String fileName = new String(request.getParameter("fileName").getBytes("iso-8859-1"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$                
        String header = new String(request.getParameter("header").getBytes("iso-8859-1"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$        
        String xpath = request.getParameter("xpath"); //$NON-NLS-1$
        String concept = ViewHelper.getConceptFromDefaultViewName(tableName);

        response.reset();
        response.setContentType("application/vnd.ms-excel"); //$NON-NLS-1$
        String theReportFile = fileName + ".xls"; //$NON-NLS-1$
        response.setHeader("Content-Disposition", "attachment; filename=\"" + theReportFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Talend MDM"); //$NON-NLS-1$
        sheet.setDefaultColumnWidth((short) 20);

        HSSFCellStyle cs = wb.createCellStyle();
        HSSFFont f = wb.createFont();
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cs.setFont(f);
        HSSFRow row = sheet.createRow((short) 0);
        try {
            String[] fieldNames = DownloadUtil.convertXml2Array(header, "header"); //$NON-NLS-1$
            String[] xpathArr = DownloadUtil.convertXml2Array(xpath, "xpath");  //$NON-NLS-1$
            for (int i = 0; i < fieldNames.length; i++) {
                row.createCell((short) i).setCellValue(fieldNames[i]);
            }
            for (int i = 0; i < fieldNames.length; i++) {
                row.getCell((short) i).setCellStyle(cs);
            }
            this.getTableContent(xpathArr, concept, sheet, request);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    public String getCurrentDataModel() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getModel();
    }

    public String getCurrentDataCluster() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getCluster();
    }

    private void getTableContent(String[] xpathArr, String concept, HSSFSheet sheet, HttpServletRequest request) throws Exception {

        String dataCluster = request.getParameter("dataCluster"); //$NON-NLS-1$
        String criteria = request.getParameter("criteria"); //$NON-NLS-1$
        String language = request.getParameter("language"); //$NON-NLS-1$
        EntityModel entity = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getEntityModel(concept, language);
        String sortField = request.getParameter("sortField"); //$NON-NLS-1$
        String sortDir = request.getParameter("sortDir"); //$NON-NLS-1$
        boolean fkResovled = Boolean.valueOf(request.getParameter("fkResovled")); //$NON-NLS-1$
        String fkDisplay = request.getParameter("fkDisplay"); //$NON-NLS-1$
        Map<String, String> colFkMap = new HashMap<String, String>();
        Map<String, List<String>> fkMap = new HashMap<String, List<String>>();
        if (fkResovled) {
            String fkColXPath = request.getParameter("fkColXPath"); //$NON-NLS-1$
            String fkInfo = request.getParameter("fkInfo"); //$NON-NLS-1$
            DownloadUtil.assembleFkMap(colFkMap, fkMap, fkColXPath, fkInfo);
        }

        List<String> results = new LinkedList<String>();
        // This blank line is for excel file header
        results.add(""); //$NON-NLS-1$ 
        String[] idsArray = null;
        if (request.getParameter("itemIdsListString") != null && !request.getParameter("itemIdsListString").isEmpty()) { //$NON-NLS-1$ //$NON-NLS-2$
            List<String> idsList = org.talend.mdm.webapp.base.shared.util.CommonUtil.convertStrigToList(request.getParameter("itemIdsListString"), Constants.FILE_EXPORT_IMPORT_SEPARATOR);  //$NON-NLS-1$
            for (String ids : idsList) {
                results.add(CommonUtil.getPort().getItem(new WSGetItem(new WSItemPK(new WSDataClusterPK(dataCluster),concept,CommonUtil.extractIdWithDots(entity.getKeys(),ids)))).getContent());
            }
        } else {
            results = Arrays.asList(CommonUtil.getPort().getItems(new WSGetItems(new WSDataClusterPK(dataCluster),concept,(criteria != null ? CommonUtil.buildWhereItem(criteria) : null), -1, new Integer(0),maxCount,false)).getStrings());
        }

        for (int i = 1; i < results.size(); i++) {
            Document document = XmlUtil.parseText(results.get(i));
            HSSFRow row = sheet.createRow((short) i);
            int colCount = 0;
            for (String xpath : xpathArr) {
                String tmp = null;
                if (DownloadUtil.isJoinField(xpath, concept)) {
                    tmp = getNodeValue(document, xpath);
                    if (fkResovled) {
                        if (colFkMap.containsKey(xpath)) {
                            List<String> fkinfoList = fkMap.get(xpath);
                            if (!fkinfoList.get(0).trim().equalsIgnoreCase("") && !tmp.equalsIgnoreCase("")) { //$NON-NLS-1$ //$NON-NLS-2$
                                List<String> infoList = getFKInfo(dataCluster, colFkMap.get(xpath), fkinfoList, tmp);
                                if (fkDisplay.equalsIgnoreCase("Id-FKInfo")) { //$NON-NLS-1$
                                    infoList.add(0, tmp);
                                }
                                tmp = LabelUtil.convertList2String(infoList, "-"); //$NON-NLS-1$                            
                            }
                        }
                    }
                } else {
                    tmp = DownloadUtil.getJoinFieldValue(document, xpath, colCount);
                }

                if (tmp != null) {
                    tmp = tmp.trim();
                    tmp = tmp.replaceAll("__h", ""); //$NON-NLS-1$ //$NON-NLS-2$
                    tmp = tmp.replaceAll("h__", ""); //$NON-NLS-1$//$NON-NLS-2$
                } else {
                    tmp = ""; //$NON-NLS-1$
                }
                row.createCell((short) colCount).setCellValue(tmp);
                colCount++;
            }
        }
    }

    private List<String> getFKInfo(String dataCluster, String fk, List<String> fkInfoList, String fkValue) throws Exception {
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
    
    private String getNodeValue(Document document,String xpath) {
        List<?> selectNodes = null;
        Map<String,String> namespaceMap = new HashMap<String,String>();
        namespaceMap.put(Constants.XSI_PREFIX,Constants.XSI_URI);
        String value = ""; //$NON-NLS-1$
        boolean isAttribute = xpath.endsWith(Constants.XSI_TYPE_QUALIFIED_NAME) ? true : false;
        if (isAttribute) {
            XPath x = document.createXPath(xpath);
            x.setNamespaceURIs(namespaceMap);
            selectNodes = x.selectNodes(document);
        } else {
            selectNodes = document.selectNodes(xpath);
        }
        if (selectNodes != null) {
            for (Object object : selectNodes) {         
                if (isAttribute) {
                    Attribute attribute = (Attribute)object;
                    value = attribute.getValue();
                } else {
                    Element element = (Element)object;
                    value = element.getText();
                }
            }
        }        
        return value;
    }
}
