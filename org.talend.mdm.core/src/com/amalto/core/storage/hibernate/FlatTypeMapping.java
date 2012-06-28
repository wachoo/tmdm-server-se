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

package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.*;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class FlatTypeMapping extends TypeMapping {
    public FlatTypeMapping(ComplexTypeMetadata complexType, MappingRepository mappings) {
        super(complexType, mappings);
    }

    @Override
    public void setValues(Session session, DataRecord from, Wrapper to) {
        try {
            Set<FieldMetadata> fields = from.getSetFields();
            for (FieldMetadata field : fields) {
                Object value = from.get(field);
                if (value == null) {
                    continue;
                }

                FieldMetadata databaseField = getDatabase(field);

                // "instance of" could be replaced by visitor on field... but is a bit too much for this simple step.
                if (field instanceof SimpleTypeFieldMetadata || field instanceof EnumerationFieldMetadata) {
                    if (!field.isMany()) {
                        to.set(databaseField.getName(), value);
                    } else {
                        List list = (List) to.get(field.getName());
                        if (list == null) {
                            to.set(databaseField.getName(), value);
                        } else {
                            List valueList = (List) value;
                            list.addAll(valueList);
                        }
                    }
                } else if (field instanceof ReferenceFieldMetadata) {
                    StorageClassLoader storageClassLoader = (StorageClassLoader) Thread.currentThread().getContextClassLoader();

                    if (!field.isMany()) {
                        DataRecord dataRecordValue = (DataRecord) value;
                        TypeMetadata referencedType = dataRecordValue.getType();
                        Class<?> referencedClass = storageClassLoader.findClass(referencedType.getName());

                        Object referencedObject = createReferencedObject(session, (ComplexTypeMetadata) referencedType, referencedClass, dataRecordValue);
                        to.set(databaseField.getName(), referencedObject);
                    } else {
                        List list = (List) to.get(field.getName());
                        if (list == null) {
                            list = new LinkedList();
                            to.set(databaseField.getName(), list);
                        }
                        List<DataRecord> valueList = (List<DataRecord>) value;
                        for (DataRecord current : valueList) {
                            TypeMetadata referencedType = current.getType();
                            Class<?> referencedClass = storageClassLoader.findClass(referencedType.getName());
                            list.add(createReferencedObject(session, (ComplexTypeMetadata) referencedType, referencedClass, current));
                        }
                    }
                } else if (field instanceof ContainedTypeFieldMetadata) {
                    List<FieldMetadata> path = MetadataUtils.path(from.getType(), field);
                    for (FieldMetadata containedField : path) {
                        DataRecord containedDataRecord;
                        if (field.isMany()) {
                            List<DataRecord> dataRecords = (List<DataRecord>) from.get(containedField);
                            if (dataRecords.size() > 1) {
                                throw new IllegalArgumentException("No support for many valued contained fields for this mapping.");
                            }
                            containedDataRecord = dataRecords.get(0);
                        } else {
                            containedDataRecord = (DataRecord) from.get(containedField);
                        }
                        setValues(session, containedDataRecord, to);
                    }
                }
            }

            to.taskId(from.getRecordMetadata().getTaskId());
            to.timestamp(from.getRecordMetadata().getLastModificationTime());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataRecord setValues(Wrapper from, DataRecord to) {
        StorageClassLoader contextClassLoader = (StorageClassLoader) Thread.currentThread().getContextClassLoader();
        ComplexTypeMetadata typeFromClass = contextClassLoader.getTypeFromClass(from.getClass());

        for (FieldMetadata field : typeFromClass.getFields()) {
            FieldMetadata userField = getUser(field);
            if (userField != null) {
                DataRecord previous = to;
                if (userField.getContainingType() != getUser()) {
                    Iterator<FieldMetadata> path = MetadataUtils.path(getUser(), userField).iterator();
                    if (!path.hasNext()) {
                        throw new IllegalStateException("No path found from '" + getUser().getName() + "' to field '" + userField.getName() + "'.");
                    }
                    while (path.hasNext()) {
                        FieldMetadata nextField = path.next();
                        if (path.hasNext()) {
                            DataRecord containedRecord = (DataRecord) to.get(nextField);
                            if (containedRecord == null) {
                                containedRecord = new DataRecord((ComplexTypeMetadata) nextField.getType(), UnsupportedDataRecordMetadata.INSTANCE);
                                to.set(nextField, containedRecord);
                            }
                            to = containedRecord;
                        }
                    }
                }
                if (userField instanceof ContainedTypeFieldMetadata) {
                    // TODO inheritance
                    if (!userField.isMany()) {
                        DataRecord containedDataRecord = new DataRecord((ComplexTypeMetadata) userField.getType(), UnsupportedDataRecordMetadata.INSTANCE);
                        to.set(userField, setValues(((Wrapper) from.get(field.getName())), containedDataRecord));
                    } else {
                        List<DataRecord> containedDataRecords = new LinkedList<DataRecord>();
                        List<Wrapper> wrapperList = (List<Wrapper>) from.get(field.getName());
                        for (Wrapper wrapper : wrapperList) {
                            containedDataRecords.add(setValues(wrapper, new DataRecord((ComplexTypeMetadata) userField.getType(), UnsupportedDataRecordMetadata.INSTANCE)));
                        }
                        to.set(userField, containedDataRecords);
                    }
                } else if (userField instanceof ReferenceFieldMetadata) {
                    if (!userField.isMany()) {
                        Wrapper wrapper = (Wrapper) from.get(field.getName());
                        if (wrapper != null) {
                            TypeMapping mapping = mappings.getMapping(contextClassLoader.getTypeFromClass(wrapper.getClass()));
                            DataRecord referencedRecord = new DataRecord(mapping.getUser(), UnsupportedDataRecordMetadata.INSTANCE);
                            for (FieldMetadata keyField : mapping.getDatabase().getKeyFields()) {
                                referencedRecord.set(mapping.getUser(keyField), wrapper.get(keyField.getName()));
                            }
                            to.set(userField, referencedRecord);
                        }
                    } else {
                        List<Wrapper> wrapperList = (List<Wrapper>) from.get(field.getName());
                        if (wrapperList != null) {
                            for (Wrapper wrapper : wrapperList) {
                                TypeMapping mapping = mappings.getMapping(contextClassLoader.getTypeFromClass(wrapper.getClass()));
                                DataRecord referencedRecord = new DataRecord(mapping.getUser(), UnsupportedDataRecordMetadata.INSTANCE);
                                for (FieldMetadata keyField : mapping.getDatabase().getKeyFields()) {
                                    referencedRecord.set(mapping.getUser(keyField), wrapper.get(keyField.getName()));
                                }
                                to.set(userField, referencedRecord);
                            }
                        }
                    }
                } else {
                    to.set(userField, from.get(field.getName()));
                }

                to = previous;
            }
        }
        return to;
    }

    private Object createReferencedObject(Session session, ComplexTypeMetadata referencedType, Class<?> referencedClass, Object referencedIdValue) throws InstantiationException, IllegalAccessException {
        if (referencedIdValue == null) {
            return null; // Means no reference (reference is null).
        }
        if (referencedIdValue instanceof Wrapper) {
            return referencedIdValue; // It's already the referenced object.
        }

        // Try to load object from current session
        if (referencedIdValue instanceof DataRecord) {
            Serializable referencedValueId;
            DataRecord idAsDataRecord = (DataRecord) referencedIdValue;
            List<FieldMetadata> keyFields = idAsDataRecord.getType().getKeyFields();
            if (keyFields.size() == 1) {
                referencedValueId = (Serializable) idAsDataRecord.get(keyFields.get(0));
            } else {
                ClassLoader storageClassLoader = Thread.currentThread().getContextClassLoader();
                List<Object> ids = new LinkedList<Object>();
                for (FieldMetadata keyField : keyFields) {
                    ids.add(idAsDataRecord.get(keyField));
                }
                referencedValueId = createCompositeId(storageClassLoader, referencedClass, ids);
            }
            Object sessionObject = session.load(referencedClass, referencedValueId);
            if (sessionObject != null) {
                return sessionObject;
            }
        } else {
            throw new NotImplementedException("Unexpected state.");
        }


        Class<?> fieldJavaType = referencedIdValue.getClass();
        // Null package might happen with proxy classes generated by Hibernate
        if (fieldJavaType.getPackage() != null && fieldJavaType.getPackage().getName().startsWith("java.")) {
            Wrapper referencedObject = (Wrapper) referencedClass.newInstance();
            for (FieldMetadata fieldMetadata : referencedType.getFields()) {
                if (fieldMetadata.isKey()) {
                    referencedObject.set(fieldMetadata.getName(), referencedIdValue);
                }
            }
            return referencedObject;
        } else {
            return referencedIdValue;
        }
    }

    public static Serializable createCompositeId(ClassLoader classLoader, Class<?> clazz, List<Object> compositeIdValues) {
        try {
            Class<?> idClass = classLoader.loadClass(clazz.getName() + "_ID");
            Class[] parameterClasses = new Class[compositeIdValues.size()];
            int i = 0;
            for (Object o : compositeIdValues) {
                parameterClasses[i++] = o.getClass();
            }
            Constructor<?> constructor = idClass.getConstructor(parameterClasses);
            return (Serializable) constructor.newInstance(compositeIdValues.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
