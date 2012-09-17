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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.ContainedTypeFieldMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents type mapping between data model as specified by the user and data model as used by hibernate storage.
 */
public class ScatteredTypeMapping extends TypeMapping {

    public ScatteredTypeMapping(ComplexTypeMetadata user, MappingRepository mappings) {
        super(user, mappings);
    }

    public void setValues(Session session, DataRecord from, Wrapper to) {
        _setValues(session, from, to);
    }

    Object _setValues(Session session, DataRecord from, Wrapper to) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        List<FieldMetadata> fields = from.getType().getFields();
        for (FieldMetadata field : fields) {
            FieldMetadata mappedDatabaseField = getDatabase(field);
            if (mappedDatabaseField == null) {
                throw new IllegalStateException("Field '" + field.getName() + "' was expected to have a database mapping");
            }

            if (field instanceof ContainedTypeFieldMetadata) {
                if (!(mappedDatabaseField instanceof ReferenceFieldMetadata)) {
                    throw new IllegalStateException("Contained elements are expected to be mapped to reference.");
                }
                ReferenceFieldMetadata referenceFieldMetadata = (ReferenceFieldMetadata) mappedDatabaseField;
                if (!field.isMany()) {
                    DataRecord containedRecord = (DataRecord) from.get(field);
                    if (containedRecord != null) {
                        ComplexTypeMetadata referencedType = containedRecord.getType();
                        Wrapper existingValue = (Wrapper) to.get(referenceFieldMetadata.getName());
                        boolean needCreate = existingValue == null;
                        if (!needCreate) {
                            ComplexTypeMetadata existingType = ((StorageClassLoader) contextClassLoader).getTypeFromClass(existingValue.getClass());
                            needCreate = !existingType.equals(referencedType);
                        }
                        Wrapper object = needCreate ? createObject(contextClassLoader, referencedType) : existingValue;
                        to.set(referenceFieldMetadata.getName(), _setValues(session, containedRecord, object));
                        if (needCreate) {
                            session.persist(object);
                        }
                    } else {
                        to.set(referenceFieldMetadata.getName(), null);
                    }
                } else {
                    List<DataRecord> dataRecords = (List<DataRecord>) from.get(field);
                    Object value = to.get(getDatabase(field).getName());
                    if (dataRecords != null) {
                        List<Wrapper> existingValue = (List<Wrapper>) value;
                        List<Wrapper> objects = existingValue == null ? new ArrayList<Wrapper>(dataRecords.size()) : existingValue;
                        int i = 0;
                        for (DataRecord dataRecord : dataRecords) {
                            if (i < objects.size() && objects.get(i) != null) {
                                objects.set(i, (Wrapper) _setValues(session, dataRecord, objects.get(i)));
                            } else {
                                Wrapper object = createObject(contextClassLoader, dataRecord.getType());
                                objects.add((Wrapper) _setValues(session, dataRecord, object));
                                session.persist(object);
                            }
                            i++;
                        }
                        to.set(referenceFieldMetadata.getName(), objects);
                    } else {
                        if (value != null && value instanceof List) {
                            ((List) value).clear();
                        }
                    }
                }
            } else if (field instanceof ReferenceFieldMetadata) {
                if (!field.isMany()) {
                    DataRecord referencedObject = (DataRecord) from.get(field);
                    if (referencedObject != null) {
                        List<FieldMetadata> keyFields = referencedObject.getType().getKeyFields();
                        Object referenceId;
                        if (keyFields.size() > 1) {
                            List<Object> referenceIdList = new LinkedList<Object>();
                            for (FieldMetadata keyField : keyFields) {
                                referenceIdList.add(referencedObject.get(keyField));
                            }
                            referenceId = referenceIdList;
                        } else {
                            referenceId = referencedObject.get(keyFields.get(0));
                        }
                        to.set(mappedDatabaseField.getName(), getReferencedObject(contextClassLoader, session, mappings.getMappingFromUser(referencedObject.getType()).getDatabase(), referenceId));
                    } else {
                        to.set(mappedDatabaseField.getName(), null);
                    }
                } else {
                    List<DataRecord> referencedObjectList = (List<DataRecord>) from.get(field);
                    if (referencedObjectList != null) {
                        List<Object> wrappers = new LinkedList<Object>();
                        for (DataRecord dataRecord : referencedObjectList) {
                            List<FieldMetadata> keyFields = dataRecord.getType().getKeyFields();
                            Object referenceId;
                            if (keyFields.size() > 1) {
                                List<Object> referenceIdList = new LinkedList<Object>();
                                for (FieldMetadata keyField : keyFields) {
                                    referenceIdList.add(dataRecord.get(keyField));
                                }
                                referenceId = referenceIdList;
                            } else {
                                referenceId = dataRecord.get(keyFields.get(0));
                            }
                            wrappers.add(getReferencedObject(contextClassLoader, session, dataRecord.getType(), referenceId));
                        }
                        to.set(mappedDatabaseField.getName(), wrappers);
                    } else {
                        Object value = to.get(mappedDatabaseField.getName());
                        if (value != null && value instanceof List) {
                            ((List) value).clear();
                        }
                    }
                }
            } else {
                to.set(mappedDatabaseField.getName(), from.get(field));
            }
        }
        return to;
    }

    public DataRecord setValues(Wrapper from, DataRecord to) {
        StorageClassLoader contextClassLoader = (StorageClassLoader) Thread.currentThread().getContextClassLoader();
        ComplexTypeMetadata typeFromClass = contextClassLoader.getTypeFromClass(from.getClass());
        for (FieldMetadata field : typeFromClass.getFields()) {
            FieldMetadata userField = getUser(field);
            if (userField != null) {
                if (userField instanceof ContainedTypeFieldMetadata) {
                    if (!userField.isMany()) {
                        Wrapper value = (Wrapper) from.get(field.getName());
                        if (value != null) {
                            DataRecord containedDataRecord = new DataRecord(getActualContainedType(userField, value), UnsupportedDataRecordMetadata.INSTANCE);
                            to.set(userField, setValues(value, containedDataRecord));
                        }
                    } else {
                        List<Wrapper> wrapperList = (List<Wrapper>) from.get(field.getName());
                        if (wrapperList != null) {
                            for (Wrapper wrapper : wrapperList) {
                                to.set(userField, setValues(wrapper, new DataRecord(getActualContainedType(userField, wrapper), UnsupportedDataRecordMetadata.INSTANCE)));
                            }
                        }
                    }
                } else if (userField instanceof ReferenceFieldMetadata) {
                    if (!userField.isMany()) {
                        Wrapper wrapper = (Wrapper) from.get(field.getName());
                        if (wrapper != null) {
                            TypeMapping mapping = mappings.getMappingFromUser(contextClassLoader.getTypeFromClass(wrapper.getClass()));
                            DataRecord referencedRecord = new DataRecord(mapping.getUser(), UnsupportedDataRecordMetadata.INSTANCE);
                            for (FieldMetadata keyField : ((ReferenceFieldMetadata) field).getReferencedType().getKeyFields()) {
                                referencedRecord.set(mapping.getUser(keyField), wrapper.get(keyField.getName()));
                            }
                            to.set(userField, referencedRecord);
                        }
                    } else {
                        List<Wrapper> wrapperList = (List<Wrapper>) from.get(field.getName());
                        if (wrapperList != null) {
                            for (Wrapper wrapper : wrapperList) {
                                TypeMapping mapping = mappings.getMappingFromUser(contextClassLoader.getTypeFromClass(wrapper.getClass()));
                                DataRecord referencedRecord = new DataRecord(mapping.getUser(), UnsupportedDataRecordMetadata.INSTANCE);
                                for (FieldMetadata keyField : ((ReferenceFieldMetadata) field).getReferencedType().getKeyFields()) {
                                    referencedRecord.set(mapping.getUser(keyField), wrapper.get(keyField.getName()));
                                }
                                to.set(userField, referencedRecord);
                            }
                        }
                    }
                } else {
                    to.set(userField, from.get(field.getName()));
                }
            }
        }
        return to;
    }

    // Returns actual contained type (in case in reference to hold contained record can have sub types).
    // Not expected to be use for foreign keys, and also very specific to this mapping implementation.
    private static ComplexTypeMetadata getActualContainedType(FieldMetadata userField, Wrapper value) {
        String actualValueType = value.getClass().getSimpleName();
        if (actualValueType.equalsIgnoreCase(userField.getType().getName())) {
            return (ComplexTypeMetadata) userField.getType();
        } else {
            Collection<ComplexTypeMetadata> subTypes = ((ComplexTypeMetadata) userField.getType()).getSubTypes();
            for (ComplexTypeMetadata subType : subTypes) {
                if (subType.getName().equalsIgnoreCase(actualValueType)) {
                    return subType;
                }
            }
        }
        throw new IllegalStateException("Could value type from class '" + String.valueOf(value) + "' and for field '" + userField.getName() + "'.");
    }

    private Wrapper createObject(ClassLoader storageClassLoader, ComplexTypeMetadata referencedType) {
        try {
            Class<? extends Wrapper> referencedClass = ((StorageClassLoader) storageClassLoader).getClassFromType(referencedType);
            return referencedClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create wrapper object for type '" + referencedType.getName() + "'", e);
        }
    }

    private Object getReferencedObject(ClassLoader storageClassLoader, Session session, ComplexTypeMetadata referencedType, Object referencedIdValue) {
        Class<?> referencedClass;
        try {
            referencedClass = ((StorageClassLoader) storageClassLoader).getClassFromType(referencedType);
        } catch (Exception e) {
            throw new RuntimeException("Could not get class for type '" + referencedType.getName() + "'", e);
        }
        try {
            if (referencedIdValue == null) {
                return null; // Means no reference (reference is null).
            }
            if (referencedIdValue instanceof Wrapper) {
                return referencedIdValue; // It's already the referenced object.
            }
            // Try to load object from current session
            if (referencedIdValue instanceof List) {
                // Handle composite id values
                Serializable result;
                try {
                    Class<?> idClass = storageClassLoader.loadClass(referencedClass.getName() + "_ID"); //$NON-NLS-1$
                    Class[] parameterClasses = new Class[((List) referencedIdValue).size()];
                    int i = 0;
                    for (Object o : (List) referencedIdValue) {
                        parameterClasses[i++] = o.getClass();
                    }
                    Constructor<?> constructor = idClass.getConstructor(parameterClasses);
                    result = (Serializable) constructor.newInstance(((List) referencedIdValue).toArray());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Serializable referencedValueId = result;

                Object sessionObject = session.load(referencedClass, referencedValueId);
                if (sessionObject != null) {
                    return sessionObject;
                }
            } else if (referencedIdValue instanceof Serializable) {
                Object sessionObject = session.load(referencedClass, (Serializable) referencedIdValue);
                if (sessionObject != null) {
                    return sessionObject;
                }
            } else {
                throw new NotImplementedException("Unexpected state.");
            }
            Class<?> fieldJavaType = referencedIdValue.getClass();
            // Null package might happen with proxy classes generated by Hibernate
            if (fieldJavaType.getPackage() != null && fieldJavaType.getPackage().getName().startsWith("java.")) {  //$NON-NLS-1$
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
        } catch (Exception e) {
            throw new RuntimeException("Could not create referenced object of type '" + referencedClass + "' with id '" + String.valueOf(referencedIdValue) + "'", e);
        }
    }

    @Override
    public String toString() {
        return "SCATTERED (" + user.getName() + ")";
    }
}
