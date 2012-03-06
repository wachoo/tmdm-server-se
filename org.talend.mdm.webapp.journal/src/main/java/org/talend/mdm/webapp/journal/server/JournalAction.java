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

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.journal.client.JournalService;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;
import org.w3c.dom.Document;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class JournalAction extends RemoteServiceServlet implements JournalService {

    public PagingLoadResult<JournalGridModel> getJournalList(JournalSearchCriteria criteria, PagingLoadConfig load)
            throws ServiceException {
        int start = load.getOffset();
        int limit = load.getLimit();
        String sort = load.getSortDir().toString();
        String field = load.getSortField();

        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
        String conceptName = "Update"; //$NON-NLS-1$
        WSGetItems item = new WSGetItems();
        item.setConceptName(conceptName);
        item.setWhereItem(null);
        item.setTotalCountOnFirstResult(true);
        item.setSkip(start);
        item.setMaxItems(limit);
        item.setWsDataClusterPK(wsDataClusterPK);

        int totalSize = 0;
        List<JournalGridModel> list = new ArrayList<JournalGridModel>();
        try {
            WSStringArray resultsArray = Util.getPort().getItems(item);
            String[] results = resultsArray == null ? new String[0] : resultsArray.getStrings();
            Document document = Util.parse(results[0]);
            totalSize = Integer.parseInt(document.getDocumentElement().getTextContent());
            
            for (int i = 1; i < results.length; i++) {
                String result = results[i];
                list.add(this.parseString2Model(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new BasePagingLoadResult<JournalGridModel>(list, load.getOffset(), totalSize);
    }
    
    private JournalGridModel parseString2Model (String xmlStr) throws Exception {
        JournalGridModel model = new JournalGridModel();
        Document doc = Util.parse(xmlStr);
        String source = checkNull(Util.getFirstTextNode(doc, "result/Update/Source")); //$NON-NLS-1$
        String timeInMillis = checkNull(Util.getFirstTextNode(doc, "result/Update/TimeInMillis")); //$NON-NLS-1$

        model.setDataContainer(checkNull(Util.getFirstTextNode(doc, "result/Update/DataCluster"))); //$NON-NLS-1$
        model.setDataModel(checkNull(Util.getFirstTextNode(doc, "result/Update/DataModel"))); //$NON-NLS-1$
        model.setEntity(checkNull(Util.getFirstTextNode(doc, "result/Update/Concept"))); //$NON-NLS-1$
        model.setKey(checkNull(Util.getFirstTextNode(doc, "result/Update/Key"))); //$NON-NLS-1$
        model.setRevisionId(checkNull(Util.getFirstTextNode(doc, "result/Update/RevisionID"))); //$NON-NLS-1$
        model.setOperationType(checkNull(Util.getFirstTextNode(doc, "result/Update/OperationType"))); //$NON-NLS-1$
        model.setOperationTime(timeInMillis);
        model.setSource(source);
        model.setUserName(checkNull(Util.getFirstTextNode(doc, "result/Update/UserName"))); //$NON-NLS-1$
        model.setIds(Util.joinStrings(new String[] { source, timeInMillis }, ".")); //$NON-NLS-1$

        return model;
    }
    
    private String checkNull(String str){
        if(str == null)
            return ""; //$NON-NLS-1$
        if(str.equalsIgnoreCase("null")) //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        return str;
    }
}