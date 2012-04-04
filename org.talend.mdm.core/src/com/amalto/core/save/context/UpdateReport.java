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
import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.LinkedList;
import java.util.List;

class UpdateReport implements DocumentSaver {

    private static final String UPDATE_REPORT_DATA_MODEL = "UpdateReport"; //$NON-NLS-1$

    private static final String UPDATE_REPORT_TYPE = "Update"; //$NON-NLS-1$

    private final DocumentSaver next;

    UpdateReport(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        if (!(context instanceof ReportDocumentSaverContext)) {
            throw new IllegalArgumentException("Context is expected to allow update report creation.");
        }

        MutableDocument databaseDocument = context.getDatabaseDocument();

        UpdateReportDocument updateReportDocument;
        try {
            Document updateReportAsDOM = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            updateReportAsDOM.appendChild(updateReportAsDOM.createElement("Update")); //$NON-NLS-1$
            updateReportDocument = new UpdateReportDocument(updateReportAsDOM, databaseDocument);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        StringBuilder key = new StringBuilder();
        String[] id = context.getId();
        for (int i = 0; i < id.length; i++) {
            key.append(id[i]);
            if (i < id.length - 1) {
                key.append('.');
            }
        }

        ComplexTypeMetadata type = context.getType();
        List<Action> actions = context.getActions();
        boolean hasHeader = false;
        for (Action action : actions) {
            if (!hasHeader) {
                createHeaderField(updateReportDocument, "UserName", String.valueOf(action.getUserName())); //$NON-NLS-1$
                createHeaderField(updateReportDocument, "Source", String.valueOf(action.getSource())); //$NON-NLS-1$
                createHeaderField(updateReportDocument, "TimeInMillis", String.valueOf(action.getDate().getTime())); //$NON-NLS-1$
                createHeaderField(updateReportDocument, "RevisionID", String.valueOf(context.getRevisionID())); //$NON-NLS-1$
                createHeaderField(updateReportDocument, "DataCluster", String.valueOf(context.getDataCluster())); //$NON-NLS-1$
                createHeaderField(updateReportDocument, "DataModel", String.valueOf(context.getDataModelName())); //$NON-NLS-1$
                createHeaderField(updateReportDocument, "Concept", String.valueOf(type.getName())); //$NON-NLS-1$
                createHeaderField(updateReportDocument, "Key", key.toString()); //$NON-NLS-1$
                hasHeader = true;
                updateReportDocument.enableRecordFieldChange();
            }
            action.perform(updateReportDocument);
            action.undo(updateReportDocument);
        }
        updateReportDocument.disableRecordFieldChange();

        ((ReportDocumentSaverContext) context).setUpdateReportDocument(updateReportDocument);
        // Save update report
        saveUpdateReport(updateReportDocument, session.getSaverSource(), session);

        next.save(session, context);
    }

    private static void saveUpdateReport(UpdateReportDocument updateReportDocument, SaverSource saverSource, SaverSession session) {
        MetadataRepository metadataRepository = saverSource.getMetadataRepository(UPDATE_REPORT_DATA_MODEL);
        ComplexTypeMetadata updateReportType = metadataRepository.getComplexType(UPDATE_REPORT_TYPE);
        if (updateReportType == null) {
            throw new IllegalStateException("Could not find UpdateReport type.");
        }

        List<FieldMetadata> keyFields = updateReportType.getKeyFields();
        LinkedList<String> ids = new LinkedList<String>();
        for (FieldMetadata keyField : keyFields) {
            String keyFieldName = keyField.getName();
            Accessor keyAccessor = updateReportDocument.createAccessor(keyFieldName);
            if (!keyAccessor.exist()) {
                throw new RuntimeException("Unexpected state: update report does not have value for key '" + keyFieldName + "'.");
            }
            ids.add(keyAccessor.get());
        }

        String[] idAsArray = ids.toArray(new String[ids.size()]);
        ItemPOJO updateReport = new ItemPOJO(new DataClusterPOJOPK(UPDATE_REPORT_DATA_MODEL), UPDATE_REPORT_TYPE, idAsArray, System.currentTimeMillis(), updateReportDocument.asDOM().getDocumentElement());

        // Call session's save to save all items in correct order (one transaction per data cluster for the XML db).
        session.save(UPDATE_REPORT_DATA_MODEL, updateReport);
    }

    private void createHeaderField(MutableDocument updateReportDocument, String fieldName, String value) {
        Accessor accessor = updateReportDocument.createAccessor(fieldName);
        accessor.createAndSet(value);
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }

    public String getBeforeSavingMessage() {
        return next.getBeforeSavingMessage();
    }
}

