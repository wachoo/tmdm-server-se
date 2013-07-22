/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import com.amalto.core.history.DeleteType;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.EmptyDocument;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordWriter;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import com.amalto.core.storage.record.metadata.DataRecordMetadataImpl;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class StorageDocument implements MutableDocument {

    private final String dataModelName;

    private DataRecord dataRecord;

    public StorageDocument(String dataModelName, DataRecord dataRecord) {
        this.dataModelName = dataModelName;
        this.dataRecord = dataRecord;
    }

    @Override
    public Accessor createAccessor(String path) {
        return new DataRecordAccessor(dataRecord, StringUtils.substringBefore(path, "["));
    }

    @Override
    public Document asDOM() {
        return EmptyDocument.EMPTY_DOCUMENT;
    }

    @Override
    public MutableDocument setField(String field, String newValue) {
        createAccessor(field).set(newValue);
        return this;
    }

    @Override
    public MutableDocument deleteField(String field) {
        createAccessor(field).delete();
        return this;
    }

    @Override
    public MutableDocument addField(String field, String value) {
        Accessor accessor = createAccessor(field);
        accessor.insert();
        accessor.set(value);
        return this;
    }

    @Override
    public MutableDocument create(MutableDocument content) {
        dataRecord = new DataRecord(dataRecord.getType(), new DataRecordMetadataImpl(System.currentTimeMillis(), null));
        return this;
    }

    @Override
    public MutableDocument setContent(MutableDocument content) {
        return null;
    }

    @Override
    public MutableDocument delete(DeleteType deleteType) {
        return this;
    }

    @Override
    public MutableDocument recover(DeleteType deleteType) {
        return this;
    }

    @Override
    public com.amalto.core.history.Document applyChanges() {
        return this;
    }

    @Override
    public MutableDocument copy() {
        return new StorageDocument(dataModelName, dataRecord);
    }

    @Override
    public String exportToString() {
        StringWriter output = new StringWriter();
        DataRecordWriter writer = new DataRecordXmlWriter();
        try {
            writer.write(dataRecord, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return output.toString();
    }

    @Override
    public com.amalto.core.history.Document transform(DocumentTransformer transformer) {
        return transformer.transform(this);
    }

    @Override
    public void restore() {
    }

    @Override
    public ComplexTypeMetadata getType() {
        return dataRecord.getType();
    }

    @Override
    public String getDataModelName() {
        return dataModelName;
    }

    @Override
    public String getRevision() {
        return dataRecord.getRevisionId();
    }

    private static class DataRecordAccessor implements Accessor {

        private final DataRecord dataRecord;

        private final String path;

        public DataRecordAccessor(DataRecord dataRecord, String path) {
            this.dataRecord = dataRecord;
            this.path = path;
        }

        @Override
        public void set(String value) {
            FieldMetadata field = dataRecord.getType().getField(path);
            if (field instanceof ReferenceFieldMetadata) {
                ComplexTypeMetadata referencedType = ((ReferenceFieldMetadata) field).getReferencedType();
                DataRecord record = (DataRecord) MetadataUtils.convert(value, field, referencedType);
                dataRecord.set(field, record);
            } else {
                dataRecord.set(field, value);
            }
        }

        @Override
        public String get() {
            Object o = dataRecord.get(path);
            if (o == null) {
                throw new IllegalArgumentException("Path '" + path + "' has no value.");
            }
            return o.toString();
        }

        @Override
        public void touch() {
        }

        @Override
        public void create() {
            dataRecord.set(dataRecord.getType().getField(path), StringUtils.EMPTY);
        }

        @Override
        public void insert() {
        }

        @Override
        public void createAndSet(String value) {
            set(value);
        }

        @Override
        public void delete() {
            dataRecord.set(dataRecord.getType().getField(path), null);
        }

        @Override
        public boolean exist() {
            return dataRecord.get(path) != null;
        }

        @Override
        public void markModified(Marker marker) {
        }

        @Override
        public void markUnmodified() {
        }

        @Override
        public int size() {
            FieldMetadata field = dataRecord.getType().getField(path);
            if (!field.isMany()) {
                return !exist() ? 0 : 1;
            } else {
                return !exist() ? 0 : ((List) dataRecord.get(field)).size();
            }
        }

        @Override
        public String getActualType() {
            return null;
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
