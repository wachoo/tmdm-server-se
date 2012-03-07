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
package org.talend.mdm.webapp.journal.server.service;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;
import org.w3c.dom.Document;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;

/**
 * The server side implementation of the RPC service.
 */
public class JournalDBService {
    
    public JournalDBService() {

    }
    
    public Object[] getResultListByCriteria(JournalSearchCriteria criteria, int start, int limit, String sort, String field,
            boolean isBrowseRecord) throws Exception {

        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
        String conceptName = "Update"; //$NON-NLS-1$
        ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();

        if (isBrowseRecord) {
            Configuration configuration = Configuration.getInstance(true);
            String dataCluster = configuration.getCluster();
            String dataModel = configuration.getModel();
            WSWhereCondition clusterwc = new WSWhereCondition(
                    "DataCluster", WSWhereOperator.EQUALS, dataCluster.trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereCondition modelwc = new WSWhereCondition(
                    "DataModel", WSWhereOperator.EQUALS, dataModel.trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$

            WSWhereItem wsWhereDataCluster = new WSWhereItem(clusterwc, null, null);
            WSWhereItem wsWhereDataModel = new WSWhereItem(modelwc, null, null);
            conditions.add(wsWhereDataCluster);
            conditions.add(wsWhereDataModel);
        }

        if (criteria.getEntity() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "Concept", isBrowseRecord ? WSWhereOperator.EQUALS : WSWhereOperator.CONTAINS, criteria.getEntity().trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getKey() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "Key", isBrowseRecord ? WSWhereOperator.EQUALS : WSWhereOperator.CONTAINS, criteria.getKey().trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getSource() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "Source", WSWhereOperator.EQUALS, criteria.getSource().trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getOperationType() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "OperationType", WSWhereOperator.EQUALS, criteria.getOperationType().trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getStartDate() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "TimeInMillis", WSWhereOperator.GREATER_THAN_OR_EQUAL, criteria.getStartDate().getTime() + "", WSStringPredicate.NONE, false); //$NON-NLS-1$ //$NON-NLS-2$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getEndDate() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "TimeInMillis", WSWhereOperator.LOWER_THAN_OR_EQUAL, criteria.getEndDate().getTime() + "", WSStringPredicate.NONE, false); //$NON-NLS-1$ //$NON-NLS-2$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        WSWhereItem wi;
        if (conditions.size() == 0) {
            wi = null;
        } else if (conditions.size() == 1) {
            wi = conditions.get(0);
        } else {
            WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
            wi = new WSWhereItem(null, and, null);
        }

        WSGetItems item = new WSGetItems();
        item.setConceptName(conceptName);
        item.setWhereItem(wi);
        item.setTotalCountOnFirstResult(true);
        item.setSkip(start);
        item.setMaxItems(limit);
        item.setWsDataClusterPK(wsDataClusterPK);

        int totalSize = 0;
        List<JournalGridModel> list = new ArrayList<JournalGridModel>();
        WSStringArray resultsArray = Util.getPort().getItems(item);
        String[] results = resultsArray == null ? new String[0] : resultsArray.getStrings();
        Document document = Util.parse(results[0]);
        totalSize = Integer.parseInt(document.getDocumentElement().getTextContent());

        for (int i = 1; i < results.length; i++) {
            String result = results[i];
            list.add(this.parseString2Model(result));
        }

        Object[] resArr = new Object[2];
        resArr[0] = totalSize;
        resArr[1] = list;
        return resArr;
    }

    private JournalGridModel parseString2Model(String xmlStr) throws Exception {
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

    private String checkNull(String str) {
        if (str == null)
            return ""; //$NON-NLS-1$
        if (str.equalsIgnoreCase("null")) //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        return str;
    }
}
