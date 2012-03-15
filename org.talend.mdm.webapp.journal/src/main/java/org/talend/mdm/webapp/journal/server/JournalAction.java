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

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.journal.client.JournalService;
import org.talend.mdm.webapp.journal.server.service.JournalDBService;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

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
        List<JournalGridModel> resultList = (List<JournalGridModel>)result[1];
        
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
}