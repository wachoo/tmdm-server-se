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
import com.amalto.core.history.accessor.DataRecordAccessor;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordWriter;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import com.amalto.core.storage.record.metadata.DataRecordMetadataImpl;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;

public class StorageDocument implements MutableDocument {

    private final String dataModelName;

    private final MetadataRepository repository;

    private DataRecord dataRecord;

    public StorageDocument(String dataModelName, MetadataRepository repository, DataRecord dataRecord) {
        this.dataModelName = dataModelName;
        this.repository = repository;
        this.dataRecord = dataRecord;
    }

    @Override
    public Accessor createAccessor(String path) {
        return new DataRecordAccessor(getDataRecord(), path);
    }

    @Override
    public Document asDOM() {
        return EmptyDocument.EMPTY_DOCUMENT; // TODO
    }

    @Override
    public Document asValidationDOM() {
        return EmptyDocument.EMPTY_DOCUMENT; // TODO
    }

    @Override
    public MutableDocument create(MutableDocument content) {
        dataRecord = new DataRecord(dataRecord.getType(), new DataRecordMetadataImpl(System.currentTimeMillis(), null));
        return this;
    }

    @Override
    public MutableDocument setContent(MutableDocument content) {
        XmlStringDataRecordReader reader = new XmlStringDataRecordReader();
        dataRecord = reader.read(content.getRevision(), repository, dataRecord.getType(), content.exportToString());
        return this;
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
        return new StorageDocument(dataModelName, repository, dataRecord);
    }

    @Override
    public void clean() {
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
    public String getDataModel() {
        return dataModelName;
    }

    @Override
    public String getRevision() {
        return dataRecord.getRevisionId();
    }

    @Override
    public String getDataCluster() {
        return dataModelName;
    }

    public DataRecord getDataRecord() {
        return dataRecord;
    }
}
