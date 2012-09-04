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

package com.amalto.core.storage.record;

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.ReferenceFieldMetadata;

import java.io.*;

public class ViewSearchResultsWriter implements DataRecordWriter {
    public void write(DataRecord record, OutputStream output) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(output));
        write(record, out);
    }

    public void write(DataRecord record, Writer writer) throws IOException {
        writer.write("<result>\n");
        for (FieldMetadata fieldMetadata : record.getSetFields()) {
            Object value = record.get(fieldMetadata);
            Object valueAsString = String.valueOf(value);
            if (fieldMetadata instanceof ReferenceFieldMetadata) {
                if (value instanceof DataRecord) {
                    DataRecord referencedRecord = (DataRecord) value;
                    StringBuilder fkValueAsString = new StringBuilder();
                    for (FieldMetadata keyField : referencedRecord.getType().getKeyFields()) {
                        fkValueAsString.append('[').append(referencedRecord.get(keyField)).append(']');
                    }
                    valueAsString = fkValueAsString.toString();
                } else {
                    valueAsString = "[" + valueAsString + ']';
                }
            }
            if (value != null) {
                writer.append("\t<").append(fieldMetadata.getName()).append(">").append(String.valueOf(valueAsString)).append("</").append(fieldMetadata.getName()).append(">\n");
            }
        }
        writer.append("</result>");
        writer.flush();
    }
}
