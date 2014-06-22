/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;

class Save implements DocumentSaver {

    private String[] savedId = new String[0];

    public void save(SaverSession session, DocumentSaverContext context) {
        DataClusterPOJOPK dataCluster = new DataClusterPOJOPK(context.getDataCluster());
        String typeName = context.getType().getName();
        savedId = context.getId();
        if (savedId.length == 0) {
            throw new IllegalStateException("No ID information to save instance of '" + typeName + "'");
        }
        Element documentElement = context.getDatabaseDocument().asDOM().getDocumentElement();
        ItemPOJO item = new ItemPOJO(dataCluster, typeName, savedId, System.currentTimeMillis(), documentElement);
        item.setTaskId(context.getTaskId());
        // Data model name is rather important! (used by FK integrity checks for instance).
        item.setDataModelName(context.getDataModelName());
        item.setDataModelRevision(context.getRevisionID()); // TODO Is data model revision ok?
        session.save(context.getDataCluster(), item, context.hasMetAutoIncrement());

        if (context instanceof ReportDocumentSaverContext) {
            MutableDocument updateReportDocument = context.getUpdateReportDocument();
            if (updateReportDocument != null) {
                saveUpdateReport(updateReportDocument, session.getSaverSource(), session);
            }
        }
    }

    private static void saveUpdateReport(MutableDocument updateReportDocument, SaverSource saverSource, SaverSession session) {
        MetadataRepository metadataRepository = saverSource.getMetadataRepository(UpdateReport.UPDATE_REPORT_DATA_MODEL);
        ComplexTypeMetadata updateReportType = metadataRepository.getComplexType(UpdateReport.UPDATE_REPORT_TYPE);
        if (updateReportType == null) {
            throw new IllegalStateException("Could not find UpdateReport type.");
        }

        List<FieldMetadata> keyFields = updateReportType.getKeyFields();
        List<String> ids = new LinkedList<String>();
        long updateReportTime = 0;
        for (FieldMetadata keyField : keyFields) {
            String keyFieldName = keyField.getName();
            Accessor keyAccessor = updateReportDocument.createAccessor(keyFieldName);
            if (!keyAccessor.exist()) {
                throw new RuntimeException("Unexpected state: update report does not have value for key '" + keyFieldName + "'.");
            }
            ids.add(keyAccessor.get());
            if ("TimeInMillis".equals(keyFieldName)) { //$NON-NLS-1$
                updateReportTime = Long.parseLong(keyAccessor.get());
            }
        }
        if (updateReportTime < 1) { // This is unexpected (would mean update report "TimeInMillis" is not present).
            throw new IllegalStateException("Missing update report time value.");
        }
        String[] idAsArray = ids.toArray(new String[ids.size()]);
        ItemPOJO updateReport = new ItemPOJO(new DataClusterPOJOPK(UpdateReport.UPDATE_REPORT_DATA_MODEL),
                UpdateReport.UPDATE_REPORT_TYPE,
                idAsArray,
                updateReportTime,
                updateReportDocument.asDOM().getDocumentElement());
        updateReport.setDataModelName(UpdateReport.UPDATE_REPORT_DATA_MODEL);
        updateReport.setDataModelRevision(saverSource.getConceptRevisionID(updateReport.getConceptName()));
        // Call session's save to save all items in correct order (one transaction per data cluster for the XML db).
        session.save(UpdateReport.UPDATE_REPORT_DATA_MODEL, updateReport, false);
    }


    public String[] getSavedId() {
        return savedId;
    }

    public String getSavedConceptName() {
        throw new UnsupportedOperationException();
    }

    public String getBeforeSavingMessage() {
        return StringUtils.EMPTY;
    }
}