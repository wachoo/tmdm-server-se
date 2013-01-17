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
package org.talend.mdm.webapp.journal.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.journal.client.JournalService;
import org.talend.mdm.webapp.journal.server.dwr.JournalDWR;
import org.talend.mdm.webapp.journal.server.service.JournalDBService;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Webapp;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class JournalAction extends RemoteServiceServlet implements JournalService {

    private static final Logger LOG = Logger.getLogger(JournalAction.class);

    private JournalDBService service = new JournalDBService();

    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.journal.client.i18n.JournalMessages", JournalDWR.class.getClassLoader()); //$NON-NLS-1$

    public PagingLoadResult<JournalGridModel> getJournalList(JournalSearchCriteria criteria, PagingLoadConfig load)
            throws ServiceException {

        int start = load.getOffset();
        int limit = load.getLimit();
        String sort = load.getSortDir().toString();
        String field = load.getSortField();
        boolean isBrowseRecord = criteria.isBrowseRecord();

        Object[] result = null;
        try {
            result = service.getResultListByCriteria(criteria, start, limit, sort, field, isBrowseRecord);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        int totalSize = Integer.parseInt(result[0].toString());
        List<JournalGridModel> resultList = (List<JournalGridModel>) result[1];

        return new BasePagingLoadResult<JournalGridModel>(resultList, load.getOffset(), totalSize);
    }

    public JournalTreeModel getDetailTreeModel(String ids) throws ServiceException {
        String[] idsArr = ids.split("\\."); //$NON-NLS-1$
        JournalTreeModel root = null;
        try {
            root = service.getDetailTreeModel(idsArr);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return root;
    }

    public JournalTreeModel getComparisionTree(JournalParameters parameter) throws ServiceException {
        JournalTreeModel root = null;
        try {
            if (parameter.isAuth()) {
                String xmlStr = service.getComparisionTreeString(parameter);
                root = service.getComparisionTreeModel(xmlStr);
            } else {
                root = new JournalTreeModel("root", "Document"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return root;
    }

    public boolean isEnterpriseVersion() {
        return Webapp.INSTANCE.isEnterpriseVersion();
    }

    public boolean checkDCAndDM(String dataContainer, String dataModel) {
        return Util.checkDCAndDM(dataContainer, dataModel);
    }

    public boolean restoreRecord(JournalParameters parameter) throws ServiceException {
        boolean result = false;
        try {
            result = service.restoreRecord(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getReportString(String config, String entity, String key, String source, String operationType,
            String startDate, String endDate, boolean isBrowseRecord) {
        String[] cfgArr = config.split(","); //$NON-NLS-1$
        int start = Integer.parseInt(cfgArr[0]);
        int limit = Integer.parseInt(cfgArr[1]);
        String sort = cfgArr[2];
        String field = cfgArr[3].equalsIgnoreCase("null") ? "" : cfgArr[3]; //$NON-NLS-1$ //$NON-NLS-2$
        String language = cfgArr[4];

        JournalSearchCriteria criteria = this.buildCriteria(entity, key, source, operationType, startDate, endDate);
        String reportString = null;
        try {
            Object[] result = service.getResultListByCriteria(criteria, start, limit, sort, field, isBrowseRecord);
            List<JournalGridModel> resultList = (List<JournalGridModel>) result[1];
            reportString = this.generateEventString(resultList, language);

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return reportString;
    }

    public String getReportString(int start, int limit, String sort, String field, String language, String entity, String key,
            String source, String operationType, String startDate, String endDate, boolean isBrowseRecord) {

        JournalSearchCriteria criteria = this.buildCriteria(entity, key, source, operationType, startDate, endDate);
        String reportString = null;
        try {
            Object[] result = service.getResultListByCriteria(criteria, start, limit, sort, field, isBrowseRecord);
            List<JournalGridModel> resultList = (List<JournalGridModel>) result[1];
            reportString = this.generateEventString(resultList, language);

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return reportString;
    }

    private JournalSearchCriteria buildCriteria(String entity, String key, String source, String operationType, String startDate,
            String endDate) {
        JournalSearchCriteria criteria = new JournalSearchCriteria();
        if (!entity.equalsIgnoreCase("")) { //$NON-NLS-1$
            criteria.setEntity(entity);
        }
        if (!key.equalsIgnoreCase("")) { //$NON-NLS-1$
            criteria.setKey(key);
        }
        if (!source.equalsIgnoreCase("")) { //$NON-NLS-1$
            criteria.setSource(source);
        }
        if (!operationType.equalsIgnoreCase("")) { //$NON-NLS-1$
            criteria.setOperationType(operationType);
        }
        if (!startDate.equalsIgnoreCase("")) { //$NON-NLS-1$
            criteria.setStartDate(new Date(Long.parseLong(startDate)));
        }
        if (!endDate.equalsIgnoreCase("")) { //$NON-NLS-1$
            criteria.setEndDate(new Date(Long.parseLong(endDate)));
        }
        return criteria;
    }

    private String generateEventString(List<JournalGridModel> resultList, String language) throws ParseException {
        StringBuilder sb = new StringBuilder("{'dateTimeFormat': 'iso8601',"); //$NON-NLS-1$
        sb.append("'events' : ["); //$NON-NLS-1$
        int i = 0;
        for (JournalGridModel model : resultList) {
            ++i;
            sb.append("{'start':'").append(this.computeTime(model.getOperationDate())).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sb.append("'title':'").append(model.getOperationDate()).append(" - ").append(model.getOperationType()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            //            sb.append("'link':'").append("javascript:showDialog(\"").append(model.toString()).append("\")',"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            Locale locale = new Locale(language);
            sb.append("'description':'").append(MESSAGES.getMessage(locale, "updatereport.timeline.label.userName")).append(":").append(model.getUserName()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$ //$NON-NLS-4$
                    .append(MESSAGES.getMessage(locale, "updatereport.timeline.label.source")).append(":").append(model.getSource()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                    .append(MESSAGES.getMessage(locale, "updatereport.timeline.label.entity")).append(":").append(model.getEntity()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                    .append(MESSAGES.getMessage(locale, "updatereport.timeline.label.revision")).append(":").append(model.getRevisionId()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                    .append(MESSAGES.getMessage(locale, "updatereport.timeline.label.dataContainer")).append(":").append(model.getDataContainer()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                    .append(MESSAGES.getMessage(locale, "updatereport.timeline.label.dataModel")).append(":").append(model.getDataModel()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                    .append(MESSAGES.getMessage(locale, "updatereport.timeline.label.key")).append(":").append(model.getKey()).append("<br>"); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$

            sb.append("'}"); //$NON-NLS-1$

            if (i != resultList.size()) {
                sb.append(","); //$NON-NLS-1$
            }
        }

        sb.append("]}"); //$NON-NLS-1$
        sb.append("@||@"); //$NON-NLS-1$

        if (resultList.size() >= 1) {
            JournalGridModel obj = resultList.get(0);
            sb.append(this.changeDataFormat(obj.getOperationDate()));
        } else {
            sb.append(this.getDateStringPlusGMT(new Date()));
        }

        sb.append("@||@"); //$NON-NLS-1$
        if (resultList.size() > 0) {
            sb.append("true"); //$NON-NLS-1$
        } else {
            sb.append("false"); //$NON-NLS-1$
        }
        return sb.toString();
    }

    private String computeTime(String src) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); //$NON-NLS-1$
        Date d = sdf.parse(src);
        Calendar c = Calendar.getInstance();
        int offset = c.getTimeZone().getRawOffset() / 3600000;
        c.setTime(d);
        c.add(Calendar.HOUR, offset);
        return sdf.format(c.getTime());
    }

    private String changeDataFormat(String src) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); //$NON-NLS-1$
        Date d = sdf.parse(src);
        return this.getDateStringPlusGMT(d);
    }

    private String getDateStringPlusGMT(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH); //$NON-NLS-1$
        String timeStr = sdf.format(d);
        return timeStr + " GMT"; //$NON-NLS-1$
    }
}