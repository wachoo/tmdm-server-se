/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.record;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONWriter;
import org.talend.mdm.commmon.metadata.ContainedTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import com.amalto.core.storage.StorageMetadataUtils;

/**
 * A {@link com.amalto.core.storage.record.DataRecordWriter} implementation to serialize a {@link DataRecord record} to JSON.
 */
public class DataRecordJSONWriter implements DataRecordWriter {

    private static void writeRecord(DataRecord record, JSONWriter writer) throws JSONException {
        writer.array();
        {
            for (FieldMetadata field : record.getType().getFields()) {
                writer.object().key(field.getName().toLowerCase());
                {
                    if (field instanceof ContainedTypeFieldMetadata) {
                        if (field.isMany()) {
                            List values = (List) record.get(field);
                            writer.array();
                            {
                                for (Object value : values) {
                                    writeRecord((DataRecord) value, writer);
                                }
                            }
                            writer.endArray();
                        } else {
                            writeRecord((DataRecord) record.get(field), writer);
                        }
                    } else {
                        if (field.isMany()) {
                            List values = (List) record.get(field);
                            writer.array();
                            {
                                for (Object value : values) {
                                    writer.value(StorageMetadataUtils.toString(value));
                                }
                            }
                            writer.endArray();
                        } else {
                            writer.value(StorageMetadataUtils.toString(record.get(field)));
                        }
                    }
                }
                writer.endObject();
            }
        }
        writer.endArray();
    }

    @Override
    public void write(DataRecord record, OutputStream output) throws IOException {
        write(record, new OutputStreamWriter(output));
    }

    @Override
    public void write(DataRecord record, Writer writer) throws IOException {
        JSONWriter jsonWriter = new JSONWriter(writer);
        try {
            jsonWriter.object().key(record.getType().getName().toLowerCase());
            {
                writeRecord(record, jsonWriter);
            }
            jsonWriter.endObject();
            writer.flush();
        } catch (JSONException e) {
            throw new IOException("Could not serialize to JSON.", e);
        }
    }
}
