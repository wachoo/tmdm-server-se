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
import com.amalto.core.history.DeleteType;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.DOMDocument;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
            updateReportAsDOM.appendChild(updateReportAsDOM.createElement("Update"));
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
                createHeaderField(updateReportDocument, "UserName", String.valueOf(action.getUserName()));
                createHeaderField(updateReportDocument, "Source", String.valueOf(action.getSource()));
                createHeaderField(updateReportDocument, "TimeInMillis", String.valueOf(action.getDate().getTime()));
                createHeaderField(updateReportDocument, "RevisionID", String.valueOf(context.getRevisionID()));
                createHeaderField(updateReportDocument, "DataCluster", String.valueOf(context.getDataCluster()));
                createHeaderField(updateReportDocument, "DataModel", String.valueOf(context.getDataModelName()));
                createHeaderField(updateReportDocument, "Concept", String.valueOf(type.getName()));
                createHeaderField(updateReportDocument, "Key", key.toString());
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
}

class UpdateReportDocument extends DOMDocument {

    private final Document updateReportDocument;

    private final MutableDocument savedDocument;

    private boolean isRecordingFieldChange;

    private int index = 0;

    private String currentNewValue = null;

    public UpdateReportDocument(Document updateReportDocument, MutableDocument savedDocument) {
        super(updateReportDocument);
        this.updateReportDocument = updateReportDocument;
        this.savedDocument = savedDocument;
    }

    @Override
    public MutableDocument setField(String field, String value) {
        if (index++ % 2 == 0) {
            Accessor oldValueAccessor = savedDocument.createAccessor(field);
            if (oldValueAccessor.exist()) {
                currentNewValue = oldValueAccessor.get();
            }
        } else {
            Element item = updateReportDocument.createElement("Item");
            {
                // Path
                Node pathNode = updateReportDocument.createElement("path");
                pathNode.appendChild(updateReportDocument.createTextNode(field));
                item.appendChild(pathNode);

                // Old value
                Node oldValueNode = updateReportDocument.createElement("oldValue");
                oldValueNode.appendChild(updateReportDocument.createTextNode(value));
                item.appendChild(oldValueNode);

                // New value
                Node newValueNode = updateReportDocument.createElement("newValue");
                if (currentNewValue != null) {
                    newValueNode.appendChild(updateReportDocument.createTextNode(currentNewValue));
                }
                item.appendChild(newValueNode);
            }
            updateReportDocument.getDocumentElement().appendChild(item);
            currentNewValue = null;
        }

        return this;
    }

    @Override
    public MutableDocument deleteField(String field) {
        Element item = updateReportDocument.createElement("Item");
        {
            // Path
            Node pathNode = updateReportDocument.createElement("path");
            pathNode.appendChild(updateReportDocument.createTextNode(field));
            item.appendChild(pathNode);

            // Old value
            Node oldValueNode = updateReportDocument.createElement("oldValue");
            oldValueNode.appendChild(updateReportDocument.createTextNode(savedDocument.createAccessor(field).get()));
            item.appendChild(oldValueNode);

            // New value
            Node newValueNode = updateReportDocument.createElement("newValue");
            item.appendChild(newValueNode);
        }
        updateReportDocument.getDocumentElement().appendChild(item);

        return this;
    }

    @Override
    public MutableDocument addField(String field, String value) {
        Element item = updateReportDocument.createElement("Item");
        {
            // Path node
            Node pathNode = updateReportDocument.createElement("path");
            pathNode.appendChild(updateReportDocument.createTextNode(field));
            item.appendChild(pathNode);

            // Old value
            Node oldValueNode = updateReportDocument.createElement("oldValue");
            oldValueNode.appendChild(updateReportDocument.createTextNode(savedDocument.createAccessor(field).get()));
            item.appendChild(oldValueNode);

            // New value
            Node newValueNode = updateReportDocument.createElement("newValue");
            item.appendChild(newValueNode);
        }
        updateReportDocument.getDocumentElement().appendChild(item);

        return this;
    }

    @Override
    public MutableDocument create(MutableDocument content) {
        Element item = updateReportDocument.createElement("OperationType");
        item.appendChild(updateReportDocument.createTextNode("CREATE"));
        updateReportDocument.getDocumentElement().appendChild(item);

        return this;
    }

    @Override
    public MutableDocument delete(DeleteType deleteType) {
        // Nothing to do
        return this;
    }

    public void recordFieldChange() {
        isRecordingFieldChange = true;
    }

    @Override
    public Accessor createAccessor(String path) {
        if (!isRecordingFieldChange) {
            return super.createAccessor(path);
        } else {
            return new MyAccessor(path, this);
        }

    }

    private static class MyAccessor implements Accessor {

        private final String path;

        private final UpdateReportDocument updateReportDocument;

        public MyAccessor(String path, UpdateReportDocument updateReportDocument) {
            this.path = path;
            this.updateReportDocument = updateReportDocument;
        }

        public void set(String value) {
            updateReportDocument.setField(path, value);
        }

        public String get() {
            throw new UnsupportedOperationException();
        }

        public void create() {
            // Nothing to do.
        }

        public void createAndSet(String value) {
            throw new UnsupportedOperationException();
        }

        public void delete() {
            throw new UnsupportedOperationException();
        }

        public boolean exist() {
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void markModified() {
            throw new UnsupportedOperationException();
        }

        public void markUnmodified() {
            throw new UnsupportedOperationException();
        }
    }
}

