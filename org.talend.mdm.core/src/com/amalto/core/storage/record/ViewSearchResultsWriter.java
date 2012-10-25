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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.query.user.DateConstant;
import com.amalto.core.query.user.DateTimeConstant;
import com.amalto.core.query.user.TimeConstant;

public class ViewSearchResultsWriter implements DataRecordWriter {
    public void write(DataRecord record, OutputStream output) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$
        write(record, out);
    }

    public void write(DataRecord record, Writer writer) throws IOException {
        writer.write("<result>\n"); //$NON-NLS-1$
        for (FieldMetadata fieldMetadata : record.getSetFields()) {
            Object value = record.get(fieldMetadata);
            String valueAsString = getValueAsString(value);
            if (fieldMetadata instanceof ReferenceFieldMetadata) {
                if (value instanceof DataRecord) {
                    DataRecord referencedRecord = (DataRecord) value;
                    StringBuilder fkValueAsString = new StringBuilder();
                    for (FieldMetadata keyField : referencedRecord.getType().getKeyFields()) {
                        fkValueAsString.append('[').append(referencedRecord.get(keyField)).append(']');
                    }
                    valueAsString = fkValueAsString.toString();
                } else {
                    if (!valueAsString.startsWith("[")) { //$NON-NLS-1$
                        valueAsString = "[" + valueAsString + ']'; //$NON-NLS-1$
                    }
                }
            }
            if (value != null) {
                writer.append("\t<").append(fieldMetadata.getName()).append(">"); //$NON-NLS-1$ //$NON-NLS-2$
                handleSimpleValue(writer, fieldMetadata, getValueAsString(valueAsString));
                writer.append("</").append(fieldMetadata.getName()).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        writer.append("</result>"); //$NON-NLS-1$
        writer.flush();
    }


    private void handleSimpleValue(Writer out, FieldMetadata simpleField, Object value) throws IOException {
        if (value == null) {
            throw new IllegalArgumentException("Not supposed to write null values to XML."); //$NON-NLS-1$
        }
        if ("date".equals(simpleField.getType().getName())) { //$NON-NLS-1$
            synchronized (DateConstant.DATE_FORMAT) {
                out.write((DateConstant.DATE_FORMAT).format(value));
            }
        } else if ("dateTime".equals(simpleField.getType().getName())) { //$NON-NLS-1$
            synchronized (DateTimeConstant.DATE_FORMAT) {
                out.write((DateTimeConstant.DATE_FORMAT).format(value));
            }
        } else if ("time".equals(simpleField.getType().getName())) { //$NON-NLS-1$
            synchronized (TimeConstant.TIME_FORMAT) {
                out.write((TimeConstant.TIME_FORMAT).format(value));
            }
        } else {
            out.write(StringEscapeUtils.escapeXml(String.valueOf(value)));
        }
    }

    private String getValueAsString(Object value) {
        if (value instanceof Object[]) {
            StringBuilder valueAsString = new StringBuilder();
            for (Object current : ((Object[]) value)) {
                valueAsString.append('[').append(String.valueOf(current)).append(']');
            }
            return valueAsString.toString();
        } else {
            return String.valueOf(value);
        }
    }
}
