package org.talend.mdm.webapp.browserecords.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.server.util.XmlUtil;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.ViewHelper;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereItem;

public class DownloadData extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(DownloadData.class);

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName"); //$NON-NLS-1$
        String header = request.getParameter("header"); //$NON-NLS-1$
        String xpath = request.getParameter("xpath"); //$NON-NLS-1$

        String[] fieldNames = header.split("@"); //$NON-NLS-1$
        String[] xpathArr = xpath.split("@"); //$NON-NLS-1$

        String concept = ViewHelper.getConceptFromDefaultViewName(tableName);

        response.reset();
        response.setContentType("application/vnd.ms-excel"); //$NON-NLS-1$
        String theReportFile = concept + ".xls"; //$NON-NLS-1$
        response.setHeader("Content-Disposition", "attachment; filename=\"" + theReportFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Talend MDM"); //$NON-NLS-1$
        sheet.setDefaultColumnWidth((short) 20);

        HSSFCellStyle cs = wb.createCellStyle();
        HSSFFont f = wb.createFont();
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cs.setFont(f);
        HSSFRow row = sheet.createRow((short) 0);
        for (int i = 0; i < fieldNames.length; i++) {
            row.createCell((short) i).setCellValue(fieldNames[i]);
        }

        for (int i = 0; i < fieldNames.length; i++) {
            row.getCell((short) i).setCellStyle(cs);
        }

        try {
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

    private String[] getItemBeans(String dataClusterPK, String viewPk, String criteria, int skip,
            int max, String sortDir, String sortCol, String language) throws Exception {

        WSWhereItem wi = null;
        if (criteria != null)
            wi = CommonUtil.buildWhereItems(criteria);
        String[] results = CommonUtil
                .getPort()
                .viewSearch(
                new WSViewSearch(new WSDataClusterPK(dataClusterPK), new WSViewPK(viewPk), wi, -1, skip, max, sortCol, sortDir))
                .getStrings();

        return results;
    }

    private void getTableContent(String[] xpathArr, String concept, HSSFSheet sheet, HttpServletRequest request) throws Exception {

        String dataCluster = request.getParameter("dataCluster"); //$NON-NLS-1$
        String viewPk = request.getParameter("viewPk"); //$NON-NLS-1$
        String criteria = request.getParameter("criteria"); //$NON-NLS-1$
        String language = request.getParameter("language"); //$NON-NLS-1$

        String sortField = request.getParameter("sortField"); //$NON-NLS-1$
        String sortDir = request.getParameter("sortDir"); //$NON-NLS-1$

        Properties mdmConfig = MDMConfiguration.getConfiguration();
        
        Object value =  mdmConfig.get("max.export.browserecord"); //$NON-NLS-1$

        int maxCount = 1000;
        if (value != null) {
            maxCount = Integer.parseInt(value.toString());
        }

        String[] results = getItemBeans(dataCluster, viewPk, criteria, 0, maxCount, sortDir, sortField, language);

        for (int i = 1; i < results.length; i++) {
            Document doc = parseResultDocument(results[i], "result"); //$NON-NLS-1$
            HSSFRow row = sheet.createRow((short) i);
            int colCount = 0;
            for (String xpath : xpathArr) {
                String tmp = XmlUtil.queryNodeText(doc, xpath.replaceFirst(concept + "/", "result/")); //$NON-NLS-1$ //$NON-NLS-2$
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

    private Document parseResultDocument(String result, String expectedRootElement) throws DocumentException {
        Document doc = XmlUtil.parseText(result);
        Element rootElement = doc.getRootElement();
        if (!rootElement.getName().equals(expectedRootElement)) {
            // When there is a null value in fields, the viewable fields sequence is not enclosed by expected element
            // FIXME Better to find out a solution at the underlying stage
            rootElement.detach();
            Element resultElement = doc.addElement(expectedRootElement);
            resultElement.add(rootElement);
        }
        return doc;
    }
}
