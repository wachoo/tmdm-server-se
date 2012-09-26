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

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.SimpleTypeFieldMetadata;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordWriter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class FullTextResultsWriter implements DataRecordWriter {
    private final String keyword;

    public FullTextResultsWriter(String keyword) {
        this.keyword = keyword;
    }

    public void write(DataRecord record, OutputStream output) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8"); //$NON-NLS-1$
        write(record, writer);
    }

    public void write(DataRecord record, Writer writer) throws IOException {
        List<FieldMetadata> keyFields = record.getType().getKeyFields();

        writer.write("<item>"); //$NON-NLS-1$
        {
            {
                writer.write("<ids>"); //$NON-NLS-1$
                for (FieldMetadata keyField : keyFields) {
                    writer.write("<id>" + record.get(keyField) + "</id>"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                writer.write("</ids>"); //$NON-NLS-1$
            }
            {
                writer.write("<title>"); //$NON-NLS-1$
                writer.write(record.getType().getName());
                for (FieldMetadata keyField : keyFields) {
                    writer.write(" " + record.get(keyField));
                }
                writer.write("</title>"); //$NON-NLS-1$
            }
            {
                writer.write("<text>"); //$NON-NLS-1$
                String[] snippetWords = new String[] {StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY}; // Prevent "null" values in results
                boolean hasMetKeyword = false;
                for (FieldMetadata field : record.getSetFields()) {
                    if (field instanceof SimpleTypeFieldMetadata) {
                        Object recordFieldValue = record.get(field);
                        if (recordFieldValue != null) {
                            String value = StringEscapeUtils.escapeXml(String.valueOf(recordFieldValue));
                            if (value.contains(keyword)) {
                                snippetWords[1] = "<b>" + value + "</b>"; //$NON-NLS-1$ //$NON-NLS-2$
                                hasMetKeyword = true;
                            } else {
                                snippetWords[hasMetKeyword ? 0 : 2] = value;
                                if (hasMetKeyword) {
                                    break;
                                }
                            }
                        }
                    }
                }
                StringBuilder builder = new StringBuilder();
                for (String snippetWord : snippetWords) {
                    builder.append(snippetWord).append(" ... "); //$NON-NLS-1$
                }
                writer.write(builder.toString());
                writer.write("</text>"); //$NON-NLS-1$
            }
            {
                writer.write("<typeName>"); //$NON-NLS-1$
                writer.write(record.getType().getName());
                writer.write("</typeName>"); //$NON-NLS-1$
            }
        }
        writer.write("</item>"); //$NON-NLS-1$
        writer.flush();
    }
}
