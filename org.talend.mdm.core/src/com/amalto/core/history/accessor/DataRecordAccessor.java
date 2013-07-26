/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history.accessor;

import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class DataRecordAccessor implements Accessor {

    private final DataRecord dataRecord;

    private final String path;

    private static class PathElement {
        FieldMetadata field = null;
        int index = -1; // 0-based index
    }

    public DataRecordAccessor(DataRecord dataRecord, String path) {
        this.dataRecord = dataRecord;
        this.path = path;
    }

    private static List<PathElement> getPath(DataRecord dataRecord, String path) {
        List<PathElement> elements = new LinkedList<PathElement>();
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        ComplexTypeMetadata current = dataRecord.getType();
        String lastFieldName = null;
        while (tokenizer.hasMoreElements()) {
            String element = tokenizer.nextToken();
            PathElement pathElement = new PathElement();
            if (element.indexOf('@') == 0) {
                throw new NotImplementedException(); // TODO
            } else if (element.indexOf('[') > 0) {
                String fieldName = StringUtils.substringBefore(element, "[");
                FieldMetadata currentField = current.getField(fieldName);
                if (!currentField.isMany()) {
                    throw new IllegalStateException("Expected a repeatable field for '" + fieldName + "' in path '" + path + "'.");
                }
                int indexStart = element.indexOf('[');
                int indexEnd = element.indexOf(']');
                if (indexStart < 0 || indexEnd < 0) {
                    throw new RuntimeException("Field name '" + lastFieldName + "' did not match many field pattern in path '" + path + "'.");
                }
                pathElement.index = Integer.parseInt(element.substring(indexStart + 1, indexEnd)) - 1;
                pathElement.field = current.getField(StringUtils.substringBefore(fieldName, "["));
                TypeMetadata type = pathElement.field.getType();
                if (type instanceof ComplexTypeMetadata) {
                    current = (ComplexTypeMetadata) type;
                }
            } else {
                pathElement.field = current.getField(element);
                TypeMetadata type = pathElement.field.getType();
                if (type instanceof ComplexTypeMetadata) {
                    current = (ComplexTypeMetadata) type;
                }
            }
            elements.add(pathElement);
        }
        return elements;
    }

    @Override
    public void set(String value) {
        List<PathElement> elements = getPath(dataRecord, path);
        DataRecord current = dataRecord;
        for (PathElement pathElement : elements) {
            if (!pathElement.field.isMany()) {
                if (pathElement.field instanceof ReferenceFieldMetadata) {
                    ComplexTypeMetadata referencedType = ((ReferenceFieldMetadata) pathElement.field).getReferencedType();
                    DataRecord record = (DataRecord) MetadataUtils.convert(value, pathElement.field, referencedType);
                    current.set(pathElement.field, record);
                } else if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                    current = (DataRecord) current.get(pathElement.field);
                } else {
                    current.set(pathElement.field, value);
                }
            } else {
                List<Object> list = (List) dataRecord.get(pathElement.field);
                if (list == null) {
                    list = new ArrayList<Object>();
                    dataRecord.set(pathElement.field, list);
                }
                while (pathElement.index > list.size() - 1) { // Expand list size
                    list.add(null);
                }
                if (pathElement.field instanceof ReferenceFieldMetadata) {
                    ComplexTypeMetadata referencedType = ((ReferenceFieldMetadata) pathElement.field).getReferencedType();
                    DataRecord record = (DataRecord) MetadataUtils.convert(value, pathElement.field, referencedType);
                    list.set(pathElement.index, record);
                } else if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                    current = (DataRecord) ((List) current.get(pathElement.field)).get(pathElement.index);
                } else {
                    list.set(pathElement.index, value);
                }
            }
        }
    }

    @Override
    public String get() {
        DataRecord current = dataRecord;
        List<PathElement> elements = getPath(dataRecord, path);
        for (PathElement pathElement : elements) {
            Object o = current.get(pathElement.field);
            if (o == null && !(pathElement.field instanceof ContainedTypeFieldMetadata)) {
                throw new IllegalArgumentException("Path '" + path + "' has no value.");
            }
            if (pathElement.field instanceof ReferenceFieldMetadata) {
                StringBuilder builder = new StringBuilder();
                DataRecord record;
                if (o != null && pathElement.field.isMany()) {
                    record = (DataRecord) ((List) o).get(pathElement.index);
                } else {
                    record = (DataRecord) o;
                }
                if (record == null) {
                    return StringUtils.EMPTY;
                }
                for (FieldMetadata keyField : ((ReferenceFieldMetadata) pathElement.field).getReferencedType().getKeyFields()) {
                    builder.append('[').append(record.get(keyField)).append(']');
                }
                return builder.toString();
            } else if(pathElement.field instanceof ContainedTypeFieldMetadata) {
                if (pathElement.field.isMany()) {
                    current = (DataRecord) ((List) current.get(pathElement.field)).get(pathElement.index);
                } else {
                    current = (DataRecord) current.get(pathElement.field);
                }
            } else if(o != null) {
                if (pathElement.field.isMany()) {
                    return String.valueOf(((List) o).get(pathElement.index));
                } else {
                    return o.toString();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void touch() {
        // No need to implement anything for this kind of accessor.
    }

    @Override
    public void create() {
        dataRecord.set(dataRecord.getType().getField(path), StringUtils.EMPTY);
    }

    @Override
    public void insert() {
        // No need to implement anything for this kind of accessor.
    }

    @Override
    public void createAndSet(String value) {
        set(value);
    }

    @Override
    public void delete() {
        List<PathElement> elements = getPath(dataRecord, path);
        DataRecord current = dataRecord;
        for (PathElement pathElement : elements) {
            if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                if (pathElement.field.isMany()) {
                    current = (DataRecord) ((List) current.get(pathElement.field)).get(pathElement.index);
                } else {
                    current = (DataRecord) current.get(pathElement.field);
                }
            } else if (pathElement.field.isMany()) {
                if (pathElement.index < 0) {
                    ((List) dataRecord.get(pathElement.field)).clear();
                } else {
                    ((List) dataRecord.get(pathElement.field)).remove(pathElement.index);
                }
            } else {
                dataRecord.set(pathElement.field, null);
            }
        }
    }

    @Override
    public boolean exist() {
        List<PathElement> elements = getPath(dataRecord, path);
        DataRecord current = dataRecord;
        for (PathElement pathElement : elements) {
            if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                if (pathElement.field.isMany()) {
                    List list = (List) current.get(pathElement.field);
                    if (list == null) {
                        return false;
                    }
                    current = (DataRecord) list.get(pathElement.index);
                } else {
                    Object o = current.get(pathElement.field);
                    if (o == null) {
                        return false;
                    }
                    current = (DataRecord) o;
                }
            } else {
                Object o = current.get(pathElement.field);
                if (pathElement.field.isMany()) {
                    List list = (List) o;
                    if (pathElement.index > 0) {
                        return list != null && pathElement.index < list.size() && list.get(pathElement.index) != null;
                    } else {
                        return list != null;
                    }
                } else {
                    return o != null;
                }
            }
        }
        return false;
    }

    @Override
    public void markModified(Marker marker) {
        // No need to implement anything for this kind of accessor.
    }

    @Override
    public void markUnmodified() {
        // No need to implement anything for this kind of accessor.
    }

    @Override
    public int size() {
        List<PathElement> elements = getPath(dataRecord, path);
        DataRecord current = dataRecord;
        for (PathElement pathElement : elements) {
            if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                if (!pathElement.field.isMany()) {
                    Object o = current.get(pathElement.field);
                    if (o == null) {
                        return 0;
                    }
                    current = (DataRecord) o;
                } else {
                    List list = (List) current.get(pathElement.field);
                    if (list == null) {
                        return 0;
                    } else if (pathElement.index < 0) {
                        return list.size();
                    }
                    current = (DataRecord) list.get(pathElement.index);
                }
            } else {
                if (!pathElement.field.isMany()) {
                    return current.get(pathElement.field) == null ? 0 : 1;
                } else {
                    List list = (List) dataRecord.get(pathElement.field);
                    return list == null ? 0 : list.size();
                }
            }
        }
        return 0;
    }

    @Override
    public String getActualType() {
        List<PathElement> elements = getPath(dataRecord, path);
        DataRecord current = dataRecord;
        for (PathElement pathElement : elements) {
            if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                if (!pathElement.field.isMany()) {
                    Object o = current.get(pathElement.field);
                    if (o == null) {
                        return StringUtils.EMPTY;
                    }
                    current = (DataRecord) o;
                } else {
                    List list = (List) current.get(pathElement.field);
                    if (list == null) {
                        return StringUtils.EMPTY;
                    }
                    current = (DataRecord) list.get(pathElement.index);
                }
            }
        }
        return current.getType().getName();
    }

    @Override
    public int compareTo(Accessor accessor) {
        if (exist() != accessor.exist()) {
            return -1;
        }
        if (exist()) {
            return get().equals(accessor.get()) ? 0 : -1;
        }
        return -1;
    }
}
