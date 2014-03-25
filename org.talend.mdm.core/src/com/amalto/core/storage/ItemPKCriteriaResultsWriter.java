/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage;

import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

class ItemPKCriteriaResultsWriter implements DataRecordWriter {

    private final String typeName;

    private final ComplexTypeMetadata itemType;

    public ItemPKCriteriaResultsWriter(String typeName, ComplexTypeMetadata itemType) {
        this.typeName = typeName;
        this.itemType = itemType;
    }

    public void write(DataRecord record, OutputStream output) throws IOException {
        doWrite(record, new OutputStreamWriter(output));
    }

    public void write(DataRecord record, Writer writer) throws IOException {
        doWrite(record, writer);
    }

    private void doWrite(DataRecord record, Writer writer) throws IOException {
        writer.write("<r>"); //$NON-NLS-1$
        {
            Object timestamp;
            if (record.getType().hasField("timestamp")) { //$NON-NLS-1$
                timestamp = record.get("timestamp"); //$NON-NLS-1$
            } else {
                timestamp = record.getRecordMetadata().getLastModificationTime();
            }
            Object taskId;
            if (record.getType().hasField("taskid")) { //$NON-NLS-1$
                taskId = record.get("taskid"); //$NON-NLS-1$
            } else {
                taskId = record.getRecordMetadata().getLastModificationTime();
            }
            writer.write("<t>" + timestamp + "</t>"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.write("<taskId>" + taskId + "</taskId>"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.write("<n>" + typeName + "</n>"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.write("<ids>"); //$NON-NLS-1$
            Collection<FieldMetadata> keyFields = itemType.getKeyFields();
            for (FieldMetadata keyField : keyFields) {
                writer.write("<i>" + StringEscapeUtils.escapeXml(String.valueOf(record.get(keyField))) + "</i>"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            writer.write("</ids>"); //$NON-NLS-1$
        }
        writer.write("</r>"); //$NON-NLS-1$
        writer.flush();
    }
}
