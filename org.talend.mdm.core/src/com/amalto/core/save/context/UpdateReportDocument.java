package com.amalto.core.save.context;

import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.history.DeleteType;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.save.DOMDocument;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class UpdateReportDocument extends DOMDocument {

    private static final NoOpAccessor NO_OP_ACCESSOR = new NoOpAccessor();

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
        item.appendChild(updateReportDocument.createTextNode(UpdateReportPOJO.OPERATION_TYPE_CREATE));
        updateReportDocument.getDocumentElement().appendChild(item);

        return this;
    }

    @Override
    public MutableDocument setContent(MutableDocument content) {
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
            item.appendChild(updateReportDocument.createTextNode(UpdateReportPOJO.OPERATION_TYPE_UPDATE));
            updateReportDocument.getDocumentElement().appendChild(item);
        }
        isRecordingFieldChange = false;
    }

    @Override
    public Accessor createAccessor(String path) {
        if (!isRecordingFieldChange) {
            return super.createAccessor(path);
        } else if (!isCreated) {
            return new FieldChangeRecorder(path, this);
        } else {  // isCreated
            // TODO Don't record changes on created record (but uncomment line below and it will).
            // return new FieldChangeRecorder(path, this);
            return NO_OP_ACCESSOR;
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
            return StringUtils.EMPTY;
        }

        public void touch() {
        }

        public void create() {
            // Nothing to do.
        }

        @Override
        public void insert() {
            // Nothing to do.
        }

        public void createAndSet(String value) {
            set(value);
        }

        public void delete() {
            updateReportDocument.setField(path, "null"); //$NON-NLS-1$
        }

        public boolean exist() {
            return true;
        }

        public void markModified(Marker marker) {
            throw new UnsupportedOperationException();
        }

        public void markUnmodified() {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return 1;
        }

        public String getActualType() {
            return StringUtils.EMPTY;
        }

        @Override
        public int compareTo(Accessor accessor) {
            if (exist() != accessor.exist()) {
                return -1;
            }
            return accessor instanceof FieldChangeRecorder ? 0 : -1;
        }
    }

    private static class NoOpAccessor implements Accessor {
        public void set(String value) {
        }

        public String get() {
            return StringUtils.EMPTY;
        }

        public void touch() {
        }

        public void create() {
        }

        @Override
        public void insert() {
        }

        public void createAndSet(String value) {
        }

        public void delete() {
        }

        public boolean exist() {
            return true;
        }

        public void markModified(Marker marker) {
        }

        public void markUnmodified() {
        }

        public int size() {
            return 0;
        }

        public String getActualType() {
            return StringUtils.EMPTY;
        }

        @Override
        public int compareTo(Accessor accessor) {
            if (exist() != accessor.exist()) {
                return -1;
            }
            if (exist()) {
                return get().equals(accessor.get()) ? 0 : -1;
            }
            return -1;
        }
    }
}
