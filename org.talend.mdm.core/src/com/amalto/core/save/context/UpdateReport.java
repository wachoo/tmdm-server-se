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
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

class UpdateReport implements DocumentSaver {

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
                updateReportDocument.recordFieldChange();
            }
            action.perform(updateReportDocument);
            action.undo(updateReportDocument);
        }

        ((ReportDocumentSaverContext) context).setUpdateReportDocument(updateReportDocument);

        next.save(session, context);
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
}

