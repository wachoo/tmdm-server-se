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
import org.apache.commons.lang.StringEscapeUtils;

import java.io.*;

public class ViewSearchResultsWriter implements DataRecordWriter {
    public void write(DataRecord record, OutputStream output) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$
        write(record, out);
    }

    public void write(DataRecord record, Writer writer) throws IOException {
        writer.write("<result>\n"); //$NON-NLS-1$
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
                    valueAsString = "[" + valueAsString + ']'; //$NON-NLS-1$
                }
            }
            if (value != null) {
                writer.append("\t<").append(fieldMetadata.getName()).append(">");
                writer.append(StringEscapeUtils.escapeXml(String.valueOf(valueAsString)));
                writer.append("</").append(fieldMetadata.getName()).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
        }
        writer.append("</result>"); //$NON-NLS-1$
        writer.flush();
    }
}
