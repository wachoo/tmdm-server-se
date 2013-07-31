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

package com.amalto.core.history.accessor.record;

import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.*;

import java.util.*;

public class DataRecordAccessor implements Accessor {

    private final DataRecord dataRecord;

    private final String path;

    private LinkedList<PathElement> pathElements = null;

    public DataRecordAccessor(DataRecord dataRecord, String path) {
        this.dataRecord = dataRecord;
        this.path = path;
    }

    private void initPath() {
        if (this.pathElements == null) {
            this.pathElements = getPath(dataRecord, path);
        }
    }

    private static LinkedList<PathElement> getPath(DataRecord dataRecord, String path) {
        LinkedList<PathElement> elements = new LinkedList<PathElement>();
        StringTokenizer tokenizer = new StringTokenizer(path, "/"); //$NON-NLS-1$
        DataRecord current = dataRecord;
        String lastFieldName = null;
        while (tokenizer.hasMoreElements()) {
            String element = tokenizer.nextToken();
            PathElement pathElement = new PathElement();
            if (element.indexOf('@') == 0) {
                pathElement.field = elements.getLast().field;
                pathElement.setter = TypeValue.SET;
                pathElement.getter = TypeValue.GET;
            } else if (element.indexOf('[') > 0) {
                String fieldName = StringUtils.substringBefore(element, "["); //$NON-NLS-1$
                if (!current.getType().hasField(fieldName)) {
                    return new LinkedList<PathElement>();
                }
                FieldMetadata currentField = getField(current.getType(), fieldName);
                if (!currentField.isMany()) {
                    throw new IllegalStateException("Expected a repeatable field for '" + fieldName + "' in path '" + path + "'.");
                }
                int indexStart = element.indexOf('[');
                int indexEnd = element.indexOf(']');
                if (indexStart < 0 || indexEnd < 0) {
                    throw new RuntimeException("Field name '" + lastFieldName + "' did not match many field pattern in path '" + path + "'.");
                }
                pathElement.index = Integer.parseInt(element.substring(indexStart + 1, indexEnd)) - 1;
                pathElement.field = currentField;
                pathElement.setter = ManyValue.SET;
                pathElement.getter = ManyValue.GET;
                List list = (List) current.get(pathElement.field);
                if (list == null || pathElement.index > list.size() - 1) {
                    return new LinkedList<PathElement>();
                }
                Object value = list.get(pathElement.index);
                if (value instanceof DataRecord) {
                    current = (DataRecord) value;
                }
            } else {
                if (!current.getType().hasField(element)) {
                    return new LinkedList<PathElement>();
                }
                pathElement.field = getField(current.getType(), element);
                pathElement.setter = SimpleValue.SET;
                pathElement.getter = SimpleValue.GET;
                if (current.get(pathElement.field) instanceof DataRecord) {
                    current = (DataRecord) current.get(pathElement.field);
                }
            }
            elements.add(pathElement);
        }
        return elements;
    }

    private static List<PathElement> getPath(ComplexTypeMetadata dataRecordType, String path) {
        List<PathElement> elements = new LinkedList<PathElement>();
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        ComplexTypeMetadata currentType = dataRecordType;
        String lastFieldName = null;
        while (tokenizer.hasMoreElements()) {
            String element = tokenizer.nextToken();
            PathElement pathElement = new PathElement();
            if (element.indexOf('@') == 0) {
                pathElement.field = getField(currentType, lastFieldName);
                pathElement.setter = TypeValue.SET;
            } else if (element.indexOf('[') > 0) {
                String fieldName = StringUtils.substringBefore(element, "["); //$NON-NLS-1$
                FieldMetadata currentField = getField(currentType, fieldName);
                if (!currentField.isMany()) {
                    throw new IllegalStateException("Expected a repeatable field for '" + fieldName + "' in path '" + path + "'.");
                }
                int indexStart = element.indexOf('[');
                int indexEnd = element.indexOf(']');
                if (indexStart < 0 || indexEnd < 0) {
                    throw new RuntimeException("Field name '" + lastFieldName + "' did not match many field pattern in path '" + path + "'.");
                }
                pathElement.index = Integer.parseInt(element.substring(indexStart + 1, indexEnd)) - 1;
                pathElement.field = currentField;
                pathElement.setter = ManyValue.SET;
                TypeMetadata type = pathElement.field.getType();
                if (type instanceof ComplexTypeMetadata) {
                    currentType = (ComplexTypeMetadata) type;
                }
            } else {
                pathElement.field = getField(currentType, element);
                pathElement.setter = SimpleValue.SET;
                TypeMetadata type = pathElement.field.getType();
                if (type instanceof ComplexTypeMetadata) {
                    currentType = (ComplexTypeMetadata) type;
                }
            }
            elements.add(pathElement);
            lastFieldName = element;
        }
        return elements;
    }

    private static FieldMetadata getField(ComplexTypeMetadata type, String fieldName) {
        if (!type.hasField(fieldName)) {
            FieldMetadata subField = null;
            for (ComplexTypeMetadata subType : type.getSubTypes()) {
                subField = getField(subType, fieldName);
                if (subField != null) {
                    break;
                }
            }
            return subField;
        } else {
            return type.getField(fieldName);
        }
    }

    @Override
    public void set(String value) {
        initPath();
        DataRecord current = dataRecord;
        ListIterator<PathElement> listIterator = pathElements.listIterator();
        PathElement pathElement = null;
        while (listIterator.hasNext()) {
            pathElement = listIterator.next();
            if (!listIterator.hasNext()) {
                break;
            }
            if (!pathElement.field.isMany()) {
                if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                    current = (DataRecord) current.get(pathElement.field);
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
                if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                    current = (DataRecord) ((List) current.get(pathElement.field)).get(pathElement.index);
                }
            }
        }
        if (pathElement != null) {
            pathElement.setter.set(current, pathElement, value);
        }
    }

    @Override
    public String get() {
        initPath();
        DataRecord current = dataRecord;
        ListIterator<PathElement> listIterator = pathElements.listIterator();
        PathElement pathElement = null;
        while (listIterator.hasNext()) {
            pathElement = listIterator.next();
            if (!listIterator.hasNext()) {
                break;
            }
            if (!pathElement.field.isMany()) {
                if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                    current = (DataRecord) current.get(pathElement.field);
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
                if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                    current = (DataRecord) ((List) current.get(pathElement.field)).get(pathElement.index);
                }
            }
        }
        if (pathElement != null) {
            return pathElement.getter.get(current, pathElement);
        }
        throw new IllegalArgumentException("Path '" + path + "' does not exist in document.");
    }

    @Override
    public void touch() {
        // No need to implement anything for this kind of accessor.
    }

    @Override
    public void create() {
        DataRecord current = dataRecord;
        ListIterator<PathElement> elements = getPath(dataRecord.getType(), path).listIterator();
        PathElement pathElement = null;
        while (elements.hasNext()) {
            pathElement = elements.next();
            if (!elements.hasNext()) {
                break;
            }
            if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                Object o = current.get(pathElement.field);
                if (o == null) {
                    DataRecord record = new DataRecord((ComplexTypeMetadata) pathElement.field.getType(), UnsupportedDataRecordMetadata.INSTANCE);
                    current.set(pathElement.field, record);
                    current = record;
                } else {
                    if (pathElement.field.isMany()) {
                        List list = (List) o;
                        while(pathElement.index >= list.size()) {
                            DataRecord record = new DataRecord((ComplexTypeMetadata) pathElement.field.getType(), UnsupportedDataRecordMetadata.INSTANCE);
                            list.add(record);
                        }
                        current = (DataRecord) list.get(pathElement.index);
                    } else {
                        current = (DataRecord) o;
                    }
                }
            }
        }
        if (pathElement != null) {
            pathElement.setter.set(current, pathElement, StringUtils.EMPTY);
        }
    }

    @Override
    public void insert() {
        DataRecord current = dataRecord;
        ListIterator<PathElement> elements = getPath(dataRecord.getType(), path).listIterator();
        PathElement pathElement = null;
        while (elements.hasNext()) {
            pathElement = elements.next();
            if (!elements.hasNext()) {
                break;
            }
            if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                Object o = current.get(pathElement.field);
                if (pathElement.field.isMany()) {
                    List list = (List) o;
                    current = (DataRecord) list.get(pathElement.index);
                } else {
                    current = (DataRecord) o;
                }
            }
        }
        if (pathElement != null && pathElement.field.isMany()) {
            List list = (List) current.get(pathElement.field);
            list.add(pathElement.index, null);
        }
    }

    @Override
    public void createAndSet(String value) {
        create();
        pathElements = null;
        initPath();
        set(value);
    }

    @Override
    public void delete() {
        initPath();
        DataRecord current = dataRecord;
        ListIterator<PathElement> listIterator = pathElements.listIterator();
        PathElement pathElement = null;
        while (listIterator.hasNext()) {
            pathElement = listIterator.next();
            if (!listIterator.hasNext()) {
                break;
            }
            if (!pathElement.field.isMany()) {
                if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                    current = (DataRecord) current.get(pathElement.field);
                }
            } else {
                List<Object> list = (List) dataRecord.get(pathElement.field);
                if (list == null) {
                    list = new ArrayList<Object>();
                    dataRecord.set(pathElement.field, list);
                }
                if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                    current = (DataRecord) ((List) current.get(pathElement.field)).get(pathElement.index);
                }
            }
        }
        if (pathElement != null) {
            if (pathElement.field.isMany()) {
                if (pathElement.index < 0) {
                    ((List) current.get(pathElement.field)).clear();
                } else {
                    ((List) current.get(pathElement.field)).remove(pathElement.index);
                }
            } else {
                current.set(pathElement.field, null);
            }
        }
    }

    @Override
    public boolean exist() {
        initPath();
        if (pathElements.isEmpty()) {
            return false;
        }
        DataRecord current = dataRecord;
        for (PathElement pathElement : pathElements) {
            if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                if (pathElement.field.isMany()) {
                    if (!current.getType().hasField(pathElement.field.getName())) {
                        return false;
                    }
                    List list = (List) current.get(pathElement.field);
                    if (list == null || pathElement.index >= list.size()) {
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
                if (!current.getType().hasField(pathElement.field.getName())) {
                    return false;
                }
                Object o = current.get(pathElement.field);
                if (pathElement.field.isMany()) {
                    List list = (List) o;
                    if (pathElement.index >= 0) {
                        return list != null && pathElement.index < list.size() && list.get(pathElement.index) != null;
                    } else {
                        return list != null;
                    }
                } else {
                    return o != null;
                }
            }
        }
        return current != null;
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
        initPath();
        DataRecord current = dataRecord;
        for (PathElement pathElement : pathElements) {
            if (pathElement.field instanceof ContainedTypeFieldMetadata) {
                if (!pathElement.field.isMany()) {
                    if (!current.getType().hasField(pathElement.field.getName())) {
                        return 0;
                    }
                    Object o = current.get(pathElement.field);
                    if (o == null) {
                        return 0;
                    }
                    current = (DataRecord) o;
                } else {
                    if (!current.getType().hasField(pathElement.field.getName())) {
                        return 0;
                    }
                    List list = (List) current.get(pathElement.field);
                    if (list == null) {
                        return 0;
                    } else if (pathElement.index < 0) {
                        return list.size();
                    }
                    current = (DataRecord) list.get(pathElement.index);
                }
            } else {
                if (!current.getType().hasField(pathElement.field.getName())) {
                    return 0;
                } else if (!pathElement.field.isMany()) {
                    return current.get(pathElement.field) == null ? 0 : 1;
                } else {
                    List list = (List) current.get(pathElement.field);
                    return list == null ? 0 : list.size();
                }
            }
        }
        return 0;
    }

    @Override
    public String getActualType() {
        initPath();
        DataRecord current = dataRecord;
        for (PathElement pathElement : pathElements) {
            if (pathElement.field instanceof ContainedTypeFieldMetadata || pathElement.field instanceof ReferenceFieldMetadata) {
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
