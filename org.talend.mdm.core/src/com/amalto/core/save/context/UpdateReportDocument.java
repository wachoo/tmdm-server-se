package com.amalto.core.save.context;

import com.amalto.core.history.DeleteType;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.save.DOMDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class UpdateReportDocument extends DOMDocument {

    private final Document updateReportDocument;

    private final MutableDocument savedDocument;

    private boolean isRecordingFieldChange;

    private int index = 0;

    private String currentNewValue = null;

    private boolean isCreated = false;

    public UpdateReportDocument(Document updateReportDocument, MutableDocument savedDocument) {
        super(updateReportDocument);
        this.updateReportDocument = updateReportDocument;
        this.savedDocument = savedDocument;
    }

    @Override
    public MutableDocument setField(String field, String value) {
        if (index++ % 2 == 0) {
            currentNewValue = value;
        } else {
            Element item = updateReportDocument.createElement("Item"); //$NON-NLS-1$
            {
                // Path
                Node pathNode = updateReportDocument.createElement("path"); //$NON-NLS-1$
                pathNode.appendChild(updateReportDocument.createTextNode(field));
                item.appendChild(pathNode);

                // Old value
                Node oldValueNode = updateReportDocument.createElement("oldValue"); //$NON-NLS-1$
                oldValueNode.appendChild(updateReportDocument.createTextNode(value));
                item.appendChild(oldValueNode);

                // New value
                Node newValueNode = updateReportDocument.createElement("newValue"); //$NON-NLS-1$
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
        Element item = updateReportDocument.createElement("Item"); //$NON-NLS-1$
        {
            // Path
            Node pathNode = updateReportDocument.createElement("path"); //$NON-NLS-1$
            pathNode.appendChild(updateReportDocument.createTextNode(field));
            item.appendChild(pathNode);

            // Old value
            Node oldValueNode = updateReportDocument.createElement("oldValue"); //$NON-NLS-1$
            oldValueNode.appendChild(updateReportDocument.createTextNode(savedDocument.createAccessor(field).get()));
            item.appendChild(oldValueNode);

            // New value
            Node newValueNode = updateReportDocument.createElement("newValue"); //$NON-NLS-1$
            item.appendChild(newValueNode);
        }
        updateReportDocument.getDocumentElement().appendChild(item);

        return this;
    }

    @Override
    public MutableDocument addField(String field, String value) {
        Element item = updateReportDocument.createElement("Item"); //$NON-NLS-1$
        {
            // Path node
            Node pathNode = updateReportDocument.createElement("path"); //$NON-NLS-1$
            pathNode.appendChild(updateReportDocument.createTextNode(field));
            item.appendChild(pathNode);

            // Old value
            Node oldValueNode = updateReportDocument.createElement("oldValue"); //$NON-NLS-1$
            oldValueNode.appendChild(updateReportDocument.createTextNode(savedDocument.createAccessor(field).get()));
            item.appendChild(oldValueNode);

            // New value
            Node newValueNode = updateReportDocument.createElement("newValue"); //$NON-NLS-1$
            item.appendChild(newValueNode);
        }
        updateReportDocument.getDocumentElement().appendChild(item);

        return this;
    }

    @Override
    public MutableDocument create(MutableDocument content) {
        isCreated = true;
        Element item = updateReportDocument.createElement("OperationType"); //$NON-NLS-1$
        item.appendChild(updateReportDocument.createTextNode("CREATE")); //$NON-NLS-1$
        updateReportDocument.getDocumentElement().appendChild(item);

        return this;
    }

    @Override
    public MutableDocument delete(DeleteType deleteType) {
        // Nothing to do
        // TODO Could extend saver to handle deletes?
        return this;
    }

    public void enableRecordFieldChange() {
        isRecordingFieldChange = true;
    }

    public void disableRecordFieldChange() {
        if (!isCreated) {
            // TODO Doing so add the OperationType element to the end of the document... not very human readable but works for an XML parser.
            Element item = updateReportDocument.createElement("OperationType"); //$NON-NLS-1$
            item.appendChild(updateReportDocument.createTextNode("UPDATE")); //$NON-NLS-1$
            updateReportDocument.getDocumentElement().appendChild(item);
        }
        isRecordingFieldChange = false;
    }

    @Override
    public Accessor createAccessor(String path) {
        if (!isRecordingFieldChange) {
            return super.createAccessor(path);
        } else {
            return new FieldChangeRecorder(path, this);
        }
    }

    private static class FieldChangeRecorder implements Accessor {

        private final String path;

        private final UpdateReportDocument updateReportDocument;

        public FieldChangeRecorder(String path, UpdateReportDocument updateReportDocument) {
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

        public int size() {
            return 1;
        }
    }
}
