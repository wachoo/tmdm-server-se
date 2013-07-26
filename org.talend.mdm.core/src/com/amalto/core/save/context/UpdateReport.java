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

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.w3c.dom.Document;

import java.util.List;

class UpdateReport implements DocumentSaver {

    public static final String UPDATE_REPORT_DATA_MODEL = "UpdateReport"; //$NON-NLS-1$

    public static final String UPDATE_REPORT_TYPE = "Update"; //$NON-NLS-1$

    private final DocumentSaver next;

    UpdateReport(DocumentSaver next) {
        this.next = next;
    }

    @Override
    public void save(SaverSession session, DocumentSaverContext context) {
        UpdateReportDocument updateReportDocument;
        Document updateReportAsDOM = SaverContextFactory.DOCUMENT_BUILDER.newDocument();
        updateReportAsDOM.appendChild(updateReportAsDOM.createElement(UPDATE_REPORT_TYPE));
        updateReportDocument = new UpdateReportDocument(updateReportAsDOM);

        StringBuilder key = new StringBuilder();
        String[] id = context.getId();
        for (int i = 0; i < id.length; i++) {
            key.append(id[i]);
            if (i < id.length - 1) {
                key.append('.');
            }
        }

        ComplexTypeMetadata type = context.getUserDocument().getType();
        List<Action> actions = context.getActions();
        boolean hasHeader = false;
        for (Action action : actions) {
            if (!hasHeader) {
                createHeaderField(updateReportDocument, "UserName", session.getSaverSource().getLegitimateUser()); //$NON-NLS-1$
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

        context.setUpdateReportDocument(updateReportDocument);
        next.save(session, context);
    }

    private void createHeaderField(MutableDocument updateReportDocument, String fieldName, String value) {
        Accessor accessor = updateReportDocument.createAccessor(fieldName);
        accessor.createAndSet(value);
    }

    @Override
    public String[] getSavedId() {
        return next.getSavedId();
    }

    @Override
    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }

    @Override
    public String getBeforeSavingMessage() {
        return next.getBeforeSavingMessage();
    }
}
