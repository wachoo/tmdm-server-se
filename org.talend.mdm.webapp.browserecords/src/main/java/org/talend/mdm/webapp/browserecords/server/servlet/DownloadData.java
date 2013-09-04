package org.talend.mdm.webapp.browserecords.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
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
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.ViewHelper;
import org.talend.mdm.webapp.browserecords.shared.Constants;

import com.amalto.core.server.StorageAdmin;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;

public class DownloadData extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(DownloadData.class);
    
    private final Integer maxCount = 1000;

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String viewPK = request.getParameter("viewPk"); //$NON-NLS-1$
        String fileName = new String(request.getParameter("fileName").getBytes("iso-8859-1"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String concept = ViewHelper.getConceptFromDefaultViewName(viewPK);
        List<String> headerList = org.talend.mdm.webapp.base.shared.util.CommonUtil.convertStrigToList(new String(request.getParameter("header").getBytes("iso-8859-1"), "UTF-8"), Constants.FILE_EXPORT_IMPORT_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        boolean includeXmlContent = Boolean.parseBoolean(request.getParameter("includeXmlContent")); //$NON-NLS-1$

        response.reset();
        response.setContentType("application/vnd.ms-excel"); //$NON-NLS-1$
        String theReportFile = fileName + ".xls"; //$NON-NLS-1$
        response.setHeader("Content-Disposition", "attachment; filename=\"" + theReportFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Talend MDM"); //$NON-NLS-1$
        sheet.setDefaultColumnWidth((short) 20);

        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        HSSFRow row = sheet.createRow((short) 0);
        int columnCount = 0;
        try {
            if (includeXmlContent) {
                createHeader(row, columnCount++, Constants.XML_CONTENT_HEADER, style);
                sheet.setColumnHidden(0, true);
            }
            for (String header : headerList) {
                createHeader(row, columnCount++, header, style);
            }
            this.getTableContent(sheet,viewPK,concept,includeXmlContent, request);
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

    private void getTableContent(HSSFSheet sheet,String viewPK,String concept,boolean includeXmlContent,HttpServletRequest request) throws Exception {
        ItemBean itemBean = null;
        String dataCluster = request.getParameter("dataCluster"); //$NON-NLS-1$
        String criteria = request.getParameter("criteria"); //$NON-NLS-1$
        String language = request.getParameter("language"); //$NON-NLS-1$
        String sortField = request.getParameter("sortField"); //$NON-NLS-1$
        String sortDir = request.getParameter("sortDir"); //$NON-NLS-1$
        EntityModel entity = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getEntityModel(concept, language);
        List<String> viewableXpathList = org.talend.mdm.webapp.base.shared.util.CommonUtil.convertStrigToList(request.getParameter("viewableXpath"), Constants.FILE_EXPORT_IMPORT_SEPARATOR);  //$NON-NLS-1$
        Map<String, EntityModel> foreignKeyEntityMap = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.getForeignKeyEntityMap(entity, viewableXpathList, language);

        String[] results = null;
        if (request.getParameter("itemXmlString") != null && !request.getParameter("itemXmlString").isEmpty()) { //$NON-NLS-1$ //$NON-NLS-2$
            List<String> resultlList = org.talend.mdm.webapp.base.shared.util.CommonUtil.convertStrigToList(request.getParameter("itemXmlString"), Constants.FILE_EXPORT_IMPORT_SEPARATOR);  //$NON-NLS-1$
            results = resultlList.toArray(new String[resultlList.size()]);
        } else {
            results = CommonUtil.getPort().viewSearch(new WSViewSearch(new WSDataClusterPK(dataCluster), new WSViewPK(viewPK), (criteria != null ? CommonUtil.buildWhereItem(criteria) : null), -1, new Integer(0),maxCount, sortField, sortDir)).getStrings();
        }
        
        int columnCount;
        for (int i = 1; i < results.length; i++) {
            columnCount = 0;
            HSSFRow row = sheet.createRow((short) i);
            itemBean = new ItemBean(null,null,results[i]);
            org.talend.mdm.webapp.browserecords.server.util.CommonUtil.dynamicAssembleByResultOrder(itemBean, viewableXpathList, entity,foreignKeyEntityMap, language, dataCluster.endsWith(StorageAdmin.STAGING_SUFFIX)); 
            if (includeXmlContent) {
                StringBuilder ids = new StringBuilder();
                for (String key : entity.getKeys()) {
                    org.w3c.dom.Document document = org.talend.mdm.webapp.browserecords.server.util.CommonUtil.parseResultDocument(results[i], "result"); //$NON-NLS-1$
                    ids = ids.length() != 0 ? ids.append(".") : ids; //$NON-NLS-1$
                    ids = ids.append(Util.getFirstTextNode(document.getDocumentElement(), "." + key.substring(key.lastIndexOf('/')))); //$NON-NLS-1$
                }
                WSItem wsItem = CommonUtil.getPort()
                        .getItem(new WSGetItem(new WSItemPK(new WSDataClusterPK(dataCluster), concept, CommonUtil.extractIdWithDots(ids.toString()))));
                createCell(row,columnCount++, wsItem.getContent());
            }
            for (String viewableXpath : viewableXpathList) {
                String content = null;
                if (foreignKeyEntityMap.get(viewableXpath) != null) {
                    String foreignKeyValue = itemBean.get(viewableXpath);
                    ForeignKeyBean foreignKeyBean= itemBean.getForeignkeyDesc(foreignKeyValue);
                    content = foreignKeyBean != null ? foreignKeyBean.toString() : ""; //$NON-NLS-1$
                } else {
                    content = String.valueOf(itemBean.get(viewableXpath));
                }
                createCell(row, columnCount++, content);
            }            
        }        
    }
    
    private void createHeader(HSSFRow row,int columnNumber,String header,HSSFCellStyle style) {
        HSSFCell cell = row.createCell((short) columnNumber);
        cell.setCellValue(header);
        cell.setCellStyle(style);
    }
    
    private void createCell(HSSFRow row,int columnNumber,String content) {
        HSSFCell cell = row.createCell((short) columnNumber);
        cell.setCellValue(content);
    }
}
