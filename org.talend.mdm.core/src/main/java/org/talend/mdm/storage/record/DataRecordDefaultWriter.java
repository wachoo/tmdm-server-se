// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.storage.record;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;

import org.talend.mdm.storage.StorageMetadataUtils;


public class DataRecordDefaultWriter implements DataRecordWriter {

    public void write(DataRecord record, OutputStream output) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$
        write(record, out);
    }

    public void write(DataRecord record, Writer writer) throws IOException {
        boolean isReferenceField;
        writer.write("<result>\n"); //$NON-NLS-1$
        for (FieldMetadata fieldMetadata : record.getSetFields()) {
            Object value = record.get(fieldMetadata);
            if (value != null) {
                String name = fieldMetadata.getName();
                writer.append("\t<").append(name).append(">"); //$NON-NLS-1$ //$NON-NLS-2$
                isReferenceField = fieldMetadata instanceof ReferenceFieldMetadata;
                if (value instanceof Object[]) {
                    Object[] values = (Object[]) value;
                    for (Object currentValue : values) {
                        if (isReferenceField) {
                            writer.append("[").append(StringEscapeUtils.escapeXml(String.valueOf(currentValue))).append("]");  //$NON-NLS-1$ //$NON-NLS-2$
                        } else {
                            writer.append(StringEscapeUtils.escapeXml(String.valueOf(currentValue)));
                        }
                    }
                } else {
                    if (isReferenceField) {
                        writer.append("[").append(StringEscapeUtils.escapeXml(String.valueOf(value))).append("]");  //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        writer.append(StringEscapeUtils.escapeXml(StorageMetadataUtils.toString(value, fieldMetadata)));
                    }
                }
                writer.append("</").append(name).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        writer.append("</result>"); //$NON-NLS-1$
        writer.flush();
    }

}
