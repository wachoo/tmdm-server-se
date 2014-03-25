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

package com.amalto.core.storage.record;

import java.util.*;

import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.storage.hibernate.TypeMapping;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;
import org.talend.mdm.commmon.metadata.*;

public class DataRecord {

    private ComplexTypeMetadata type;

    private final Map<FieldMetadata, Object> fieldToValue = new LinkedHashMap<FieldMetadata, Object>();

    private final DataRecordMetadata recordMetadata;

    private String revisionId;

    /**
     * @param type           Type as {@link ComplexTypeMetadata} of the data record
     * @param recordMetadata Record specific metadata (e.g. last modification timestamp...)
     */
    public DataRecord(ComplexTypeMetadata type, DataRecordMetadata recordMetadata) {
        this.type = type;
        this.recordMetadata = recordMetadata;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public ComplexTypeMetadata getType() {
        return type;
    }

    public DataRecordMetadata getRecordMetadata() {
        return recordMetadata;
    }

    public Object get(FieldMetadata field) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null.");
        }
        ComplexTypeMetadata containingType = field.getContainingType();
        if (containingType != this.getType() && !this.getType().isAssignableFrom(containingType)) {
            if (fieldToValue.containsKey(field)) {
                return fieldToValue.get(field);
            } else if (recordMetadata.getRecordProperties().containsKey(field.getName())) { // Try to read from metadata
                return recordMetadata.getRecordProperties().get(field.getName());
            }
            Iterator<FieldMetadata> path = MetadataUtils.path(type, field, false).iterator();
            if (!path.hasNext()) {
                Object value = get(field.getName());
                if (value != null) { // Support explicit projection type fields
                    return value;
                }
                throw new IllegalArgumentException("Field '" + field.getName() + "' isn't reachable from type '" + type.getName() + "'");
            }
            DataRecord current = this;
            while (path.hasNext()) {
                FieldMetadata nextField = path.next();
                Object nextObject = current.fieldToValue.get(nextField);
                if (nextObject == null) {
                    return null;
                }
                if (path.hasNext()) {
                    if (!(nextObject instanceof DataRecord)) {
                        if (!path.hasNext()) {
                            return nextObject;
                        } else if (nextObject instanceof List) {
                            // TODO This is maybe (surely?) not what user expect, but there's no way to select the nth instance of a collection in query API.
                            nextObject = ((List) nextObject).get(0);
                        } else {
                            return nextObject;
                        }
                    }
                    current = (DataRecord) nextObject;
                } else {
                    return nextObject;
                }
            }
            return null; // Not found.
        } else {
            if (fieldToValue.containsKey(field)) {
                return fieldToValue.get(field);
            } else if (recordMetadata.getRecordProperties().containsKey(field.getName())) { // Try to read from metadata
                return recordMetadata.getRecordProperties().get(field.getName());
            }
            // Last chance for finding value: reused field might not be hashCode-equals so does a by-name lookup.
            if (field instanceof ContainedTypeFieldMetadata) {
                for (Map.Entry<FieldMetadata, Object> entry : fieldToValue.entrySet()) {
                    if (field.getName().equals(entry.getKey().getName())) {
                        return entry.getValue();
                    }
                }
            }
            return null;
        }
    }

    public Object get(String fieldName) {
        StringTokenizer tokenizer = new StringTokenizer(fieldName, "/"); //$NON-NLS-1$
        DataRecord current = this;
        Object currentValue = null;
        while (tokenizer.hasMoreTokens()) {
            String currentPathElement = tokenizer.nextToken();
            FieldMetadata field = current.getType().getField(currentPathElement);
            currentValue = current.get(field);
            if (tokenizer.hasMoreTokens()) {
                if (currentValue == null) {
                    // Means record does not own field last call to "tokenizer.nextToken()" returned.
                    return null;
                } else if(!field.isMany()) {
                    current = (DataRecord) currentValue;
                } else { // Repeatable element
                    current = (DataRecord) ((List) currentValue).get(0); // TODO No way to specify index in API.
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
            if (field instanceof ReferenceFieldMetadata || field instanceof ContainedTypeFieldMetadata) {  // fields that may contain data records.
                if (list == null) {
                    list = new LinkedList();
                    fieldToValue.put(field, list);
                }
                if (o instanceof Collection) {
                    list.addAll((Collection) o);
                } else {
                    list.add(o);
                }
            } else {
                if (list == null && o instanceof List) {
                    fieldToValue.put(field, o);
                } else {
                    if (list == null) {
                        list = new LinkedList();
                        fieldToValue.put(field, list);
                    }
                    list.add(o);
                }
            }
        }
    }

    public <T> T convert(DataRecordConverter<T> converter, TypeMapping mapping) {
        return converter.convert(this, mapping);
    }

    public Set<FieldMetadata> getSetFields() {
        return fieldToValue.keySet();
    }

    public boolean isEmpty() {
        for (Map.Entry<FieldMetadata, Object> entry : fieldToValue.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getKey() instanceof ContainedTypeFieldMetadata) {
                    if (entry.getKey().isMany()) {
                        List<DataRecord> list = (List<DataRecord>) entry.getValue();
                        for (DataRecord o : list) {
                            if (!o.isEmpty()) {
                                return false;
                            }
                        }
                    } else {
                        if (!((DataRecord) entry.getValue()).isEmpty()) {
                            return false;
                        }
                    }
                } else {
                    if (entry.getKey().isMany()) {
                        List list = (List) entry.getValue();
                        boolean containsOnlyNulls = true;
                        for (Object o : list) {
                            containsOnlyNulls &= (o == null);
                        }
                        if (!list.isEmpty() && !containsOnlyNulls) {
                            return false;
                        }
                    } else {
                        if (entry.getValue() != null) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataRecord)) return false;

        DataRecord that = (DataRecord) o;
        for (FieldMetadata key : fieldToValue.keySet()) {
            if (key.isKey()) {
                if (!fieldToValue.get(key).equals(that.fieldToValue.get(key))) {
                    return false;
                }
            }
        }
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        for (FieldMetadata key : fieldToValue.keySet()) {
            if (key.isKey()) {
                result += fieldToValue.get(key).hashCode();
            }
        }
        return result;
    }

    /**
     * @param type The new type of this data record. New type <b>MUST</b> be a sub type of current type.
     */
    public void setType(ComplexTypeMetadata type) {
        // TODO Check if type is eligible
        this.type = type;
    }

    public static DataRecord copy(DataRecord record) {
        DataRecord copy = new DataRecord(record.getType(), record.getRecordMetadata().copy());
        for (Map.Entry<FieldMetadata, Object> entry : record.fieldToValue.entrySet()) {
            Object value = record.get(entry.getKey());
            if (value instanceof DataRecord) {
                value = DataRecord.copy((DataRecord) value);
            } else if (value instanceof List) {
                value = new ArrayList<Object>((Collection<?>) value);
            }
            copy.set(entry.getKey(), value);
        }
        return copy;
    }

    public void remove(FieldMetadata field) {
        fieldToValue.remove(field);
    }

    @Override
    public String toString() {
        StringBuilder keyValue = new StringBuilder();
        for (FieldMetadata keyField : type.getKeyFields()) {
            keyValue.append('[').append(fieldToValue.get(keyField)).append(']');
        }
        return "Record {" +
                "type=" + type.getName() +
                ",key=" + keyValue.toString() +
                '}';
    }
}
