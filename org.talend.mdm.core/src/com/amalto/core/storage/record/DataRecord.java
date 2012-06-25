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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.storage.hibernate.TypeMapping;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;

import java.util.*;

public class DataRecord {

    private final ComplexTypeMetadata type;

    private final Map<FieldMetadata, Object> fieldToValue = new HashMap<FieldMetadata, Object>();

    private final DataRecordMetadata recordMetadata;

    private long revisionId;

    /**
     * @param type           Type as {@link ComplexTypeMetadata} of the data record
     * @param recordMetadata Record specific metadata (e.g. last modification timestamp...)
     */
    public DataRecord(ComplexTypeMetadata type, DataRecordMetadata recordMetadata) {
        this.type = type;
        this.recordMetadata = recordMetadata;
    }

    public long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(long revisionId) {
        this.revisionId = revisionId;
    }

    public ComplexTypeMetadata getType() {
        return type;
    }

    public DataRecordMetadata getRecordMetadata() {
        return recordMetadata;
    }

    public Object get(FieldMetadata field) {
        if (field.getContainingType() != this.getType()) {
            Iterator<FieldMetadata> path = MetadataUtils.path(type, field).iterator();
            if (!path.hasNext()) {
                throw new IllegalArgumentException("Field '" + field.getName() + "' isn't reachable from type '" + type.getName() + "'");
            }
            DataRecord current = this;
            while (path.hasNext()) {
                FieldMetadata nextField = path.next();
                if (path.hasNext()) {
                    current = (DataRecord) current.fieldToValue.get(nextField);
                } else {
                    return current.fieldToValue.get(nextField);
                }
                if(current == null) {
                    break;
                }
            }
            return null; // Not found.
        } else {
            return fieldToValue.get(field);
        }
    }

    public Object get(String fieldName) {
        StringTokenizer tokenizer = new StringTokenizer(fieldName, "/");
        DataRecord current = this;
        Object currentValue = null;
        while (tokenizer.hasMoreTokens()) {
            currentValue = current.get(current.getType().getField(tokenizer.nextToken()));
            if (tokenizer.hasMoreTokens()) {
                if (currentValue == null) {
                    // Means record does not own field last call to "tokenizer.nextToken()" returned.
                    return null;
                } else {
                    current = (DataRecord) currentValue;
                }
            }
        }
        return currentValue;
    }

    public void set(FieldMetadata field, Object o) {
        if (field == null) {
            throw new IllegalArgumentException("Field can not be null.");
        }

        if (!field.isMany()) {
            fieldToValue.put(field, o);
        } else {
            List list = (List) fieldToValue.get(field);
            if (!(field instanceof ReferenceFieldMetadata)) {
                if (list == null && o instanceof List) {
                    fieldToValue.put(field, o);
                } else {
                    if (list == null) {
                        list = new LinkedList();
                        fieldToValue.put(field, list);
                    }
                    list.add(o);
                }
            } else {  // reference field
                if (list == null) {
                    list = new LinkedList();
                    fieldToValue.put(field, list);
                }
                list.add(o);
            }
        }
    }

    public void set(String fieldName, Object o) {
        for (Map.Entry<FieldMetadata, Object> entry : fieldToValue.entrySet()) {
            if (entry.getKey().getName().equals(fieldName)) {
                entry.setValue(o);
            }
        }
    }

    public <T> T convert(DataRecordConverter<T> converter, TypeMapping mapping) {
        return converter.convert(this, mapping);
    }

    public Set<FieldMetadata> getSetFields() {
        return fieldToValue.keySet();
    }

}
