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

package com.amalto.core.storage;

import com.amalto.core.load.io.ResettableStringWriter;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

// TODO Implementation is somewhat bad: does not use parameters to write results
class ItemPKCriteriaResultsWriter implements DataRecordWriter {

    private final ResettableStringWriter writer;

    private final String typeName;

    private final List<String> itemPKResults;

    private final ComplexTypeMetadata itemType;

    public ItemPKCriteriaResultsWriter(String typeName, List<String> itemPKResults, ComplexTypeMetadata itemType) {
        this.typeName = typeName;
        this.itemPKResults = itemPKResults;
        this.itemType = itemType;
        writer = new ResettableStringWriter();
    }

    public void write(DataRecord record, OutputStream output) throws IOException {
        doWrite(record);
    }

    public void write(DataRecord record, Writer writer) throws IOException {
        doWrite(record);
    }

    private void doWrite(DataRecord record) {
        writer.write("<r>"); //$NON-NLS-1$
        {
            Object timestamp;
            if (record.getType().hasField("timestamp")) {
                timestamp = record.get("timestamp");
            } else {
                timestamp = record.getRecordMetadata().getLastModificationTime();
            }
            Object taskId;
            if (record.getType().hasField("taskid")) {
                taskId = record.get("taskid");
            } else {
                taskId = record.getRecordMetadata().getLastModificationTime();
            }
            writer.write("<t>" + timestamp + "</t>"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.write("<taskId>" + taskId + "</taskId>"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.write("<n>" + typeName + "</n>"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.write("<ids>"); //$NON-NLS-1$
            List<FieldMetadata> keyFields = itemType.getKeyFields();
            for (FieldMetadata keyField : keyFields) {
                writer.write("<i>" + record.get(keyField) + "</i>"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            writer.write("</ids>"); //$NON-NLS-1$
        }
        writer.write("</r>"); //$NON-NLS-1$
        itemPKResults.add(writer.toString());

        writer.reset();
    }
}
