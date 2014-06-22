// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.journal.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import org.talend.mdm.webapp.journal.server.service.JournalDBService;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;

/**
 * The server side implementation of the RPC service.
 */
public class JournalExportServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(JournalExportServlet.class);

    private static final long serialVersionUID = 1L;

    private JournalDBService service = new JournalDBService();
    
    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.journal.client.i18n.JournalMessages", JournalExportServlet.class.getClassLoader()); //$NON-NLS-1$

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.info("Exporting excel file"); //$NON-NLS-1$
        String language = request.getParameter("language"); //$NON-NLS-1$
        Locale locale = new Locale(language);
        JournalSearchCriteria criteria = this.getCriteriaFromRequest(request);
        
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$
        response.reset();
        response.setContentType("application/vnd.ms-excel"); //$NON-NLS-1$
        String theReportFile = "Journal_" + df.format(new Date()) + ".xls"; //$NON-NLS-1$ //$NON-NLS-2$
        response.setHeader("Content-Disposition", "attachment; filename=\"" + theReportFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Talend MDM"); //$NON-NLS-1$
        sheet.setDefaultColumnWidth((short) 20);
        HSSFCellStyle cs = wb.createCellStyle();
        HSSFFont f = wb.createFont();
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cs.setFont(f);
        HSSFRow row = sheet.createRow((short) 0);

        row.createCell((short) 0).setCellValue(MESSAGES.getMessage(locale, "data_container_label")); //$NON-NLS-1$
        row.getCell((short) 0).setCellStyle(cs);
        
        row.createCell((short) 1).setCellValue(MESSAGES.getMessage(locale, "data_model_label")); //$NON-NLS-1$
        row.getCell((short) 1).setCellStyle(cs);
        
        row.createCell((short) 2).setCellValue(MESSAGES.getMessage(locale, "entity_label")); //$NON-NLS-1$
        row.getCell((short) 2).setCellStyle(cs);
        
        row.createCell((short) 3).setCellValue(MESSAGES.getMessage(locale, "key_label")); //$NON-NLS-1$
        row.getCell((short) 3).setCellStyle(cs);
        
        row.createCell((short) 4).setCellValue(MESSAGES.getMessage(locale, "revision_id_label")); //$NON-NLS-1$
        row.getCell((short) 4).setCellStyle(cs);
        
        row.createCell((short) 5).setCellValue(MESSAGES.getMessage(locale, "operation_type_label")); //$NON-NLS-1$
        row.getCell((short) 5).setCellStyle(cs);
        
        row.createCell((short) 6).setCellValue(MESSAGES.getMessage(locale, "operation_time_label")); //$NON-NLS-1$
        row.getCell((short) 6).setCellStyle(cs);
        
        row.createCell((short) 7).setCellValue(MESSAGES.getMessage(locale, "source_label")); //$NON-NLS-1$
        row.getCell((short) 7).setCellStyle(cs);
        
        row.createCell((short) 8).setCellValue(MESSAGES.getMessage(locale, "user_name_label")); //$NON-NLS-1$
        row.getCell((short) 8).setCellStyle(cs);
        
        Object[] resultArr = null;
        try {
            resultArr = service.getResultListByCriteria(criteria, 0, -1, null, null, false);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        
        List<JournalGridModel> resultList = (List<JournalGridModel>)resultArr[1];
        int i = 1;
        for(JournalGridModel model : resultList){
            row = sheet.createRow((short) i++);
            row.createCell((short) 0).setCellValue(model.getDataContainer());
            row.createCell((short) 1).setCellValue(model.getDataModel());
            row.createCell((short) 2).setCellValue(model.getEntity());
            row.createCell((short) 3).setCellValue(model.getKey());
            row.createCell((short) 4).setCellValue(model.getRevisionId());
            row.createCell((short) 5).setCellValue(model.getOperationType());
            row.createCell((short) 6).setCellValue(model.getOperationTime());
            row.createCell((short) 7).setCellValue(model.getSource());
            row.createCell((short) 8).setCellValue(model.getUserName());
        }
        
        response.setCharacterEncoding("utf-8"); //$NON-NLS-1$
        OutputStream out = response.getOutputStream();

        wb.write(out);
        out.close();
    }
    
    private JournalSearchCriteria getCriteriaFromRequest(HttpServletRequest request){        
        String entity = request.getParameter("entity"); //$NON-NLS-1$
        String key = request.getParameter("key"); //$NON-NLS-1$
        String source = request.getParameter("source"); //$NON-NLS-1$
        String operationType = request.getParameter("operationType"); //$NON-NLS-1$
        String startDate = request.getParameter("startDate"); //$NON-NLS-1$
        String endDate = request.getParameter("endDate"); //$NON-NLS-1$
        
        JournalSearchCriteria criteria = new JournalSearchCriteria();
        criteria.setEntity(entity);
        criteria.setKey(key);
        criteria.setSource(source);
        criteria.setOperationType(operationType);
        
        if(startDate != null){
            Date date = new Date();
            date.setTime(Long.valueOf(startDate));
            criteria.setStartDate(date);
        }      
        
        if(endDate != null){
            Date date = new Date();
            date.setTime(Long.valueOf(endDate));
            criteria.setEndDate(date);
        }
        
        return criteria;
    }
}