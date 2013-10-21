/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.record;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.XMLConstants;

import org.apache.commons.lang.StringEscapeUtils;

import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;
import com.amalto.core.query.user.DateConstant;
import com.amalto.core.query.user.DateTimeConstant;
import com.amalto.core.query.user.TimeConstant;

public class ViewSearchResultsWriter implements DataRecordWriter {

    @Override
    public void write(DataRecord record, OutputStream output) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$
        write(record, out);
    }

    @Override
    public void write(DataRecord record, Writer writer) throws IOException {
        writer.write("<result xmlns:xsi=\"" + XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI + "\">\n"); //$NON-NLS-1$ //$NON-NLS-2$
        for (FieldMetadata fieldMetadata : record.getSetFields()) {
            Object value = record.get(fieldMetadata);
            String name = fieldMetadata.getName();
            if (value != null) {
                writer.append("\t<").append(name).append(">"); //$NON-NLS-1$ //$NON-NLS-2$
                processValue(writer, fieldMetadata, value);
                writer.append("</").append(name).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                writer.append("\t<").append(name).append("/>\n"); //$NON-NLS-1$//$NON-NLS-2$
            }
        }
        writer.append("</result>"); //$NON-NLS-1$
        writer.flush();
    }

    private void processValue(Writer out, FieldMetadata fieldMetadata, Object value) throws IOException {
        if (value == null) {
            throw new IllegalArgumentException("Not supposed to write null values to XML."); //$NON-NLS-1$
        }
        String stringValue;
        if ("date".equals(fieldMetadata.getType().getName())) { //$NON-NLS-1$
            synchronized (DateConstant.DATE_FORMAT) {
                stringValue = (DateConstant.DATE_FORMAT).format(value);
            }
        } else if ("dateTime".equals(fieldMetadata.getType().getName())) { //$NON-NLS-1$
            synchronized (DateTimeConstant.DATE_FORMAT) {
                stringValue = (DateTimeConstant.DATE_FORMAT).format(value);
            }
        } else if ("time".equals(fieldMetadata.getType().getName())) { //$NON-NLS-1$
            synchronized (TimeConstant.TIME_FORMAT) {
                stringValue = (TimeConstant.TIME_FORMAT).format(value);
            }
        } else if (value instanceof Object[]) {
            StringBuilder valueAsString = new StringBuilder();
            for (Object current : ((Object[]) value)) {
                valueAsString.append('[').append(String.valueOf(current)).append(']');
            }
            stringValue = valueAsString.toString();
        } else {
            stringValue = String.valueOf(value);
        }
        if (fieldMetadata instanceof ReferenceFieldMetadata) {
            if (value instanceof DataRecord) {
                // TODO Replace with MetadataUtils.toString()
                DataRecord referencedRecord = (DataRecord) value;
                StringBuilder fkValueAsString = new StringBuilder();
                for (FieldMetadata keyField : referencedRecord.getType().getKeyFields()) {
                    fkValueAsString.append('[').append(referencedRecord.get(keyField)).append(']');
                }
                stringValue = fkValueAsString.toString();
            } else {
                if (!stringValue.startsWith("[")) { //$NON-NLS-1$
                    stringValue = "[" + stringValue + ']'; //$NON-NLS-1$
                }
            }
        }
        out.append(StringEscapeUtils.escapeXml(stringValue));
    }

}
