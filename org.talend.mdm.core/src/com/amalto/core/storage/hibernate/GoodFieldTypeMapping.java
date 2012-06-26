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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents type mapping between data model as specified by the user and data model as used by hibernate storage.
 */
public class GoodFieldTypeMapping extends TypeMapping {

    public GoodFieldTypeMapping(ComplexTypeMetadata user, MappingRepository mappings) {
        this(user, (ComplexTypeMetadata) user.copyShallow(), mappings);
    }

    public GoodFieldTypeMapping(ComplexTypeMetadata user, ComplexTypeMetadata database, MappingRepository mappings) {
        super(user, database, mappings);
    }

    public void setValues(Session session, DataRecord from, Wrapper to) {
        _setValues(session, from, to);
    }

    public Object _setValues(Session session, DataRecord record, Wrapper wrapper) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Set<FieldMetadata> fields = record.getSetFields();
        for (FieldMetadata field : fields) {
            FieldMetadata mappedDatabaseField = getDatabase(field);

            if (field instanceof ContainedTypeFieldMetadata) {
                if (!(mappedDatabaseField instanceof ReferenceFieldMetadata)) {
                    throw new IllegalStateException("Contained elements are expected to be mapped to reference.");
                }
                ReferenceFieldMetadata referenceFieldMetadata = (ReferenceFieldMetadata) mappedDatabaseField;
                if (!field.isMany()) {
                    Wrapper object = createObject(contextClassLoader, referenceFieldMetadata.getReferencedType());
                    DataRecord containedRecord = (DataRecord) record.get(field);
                    if (containedRecord != null) {
                        wrapper.set(referenceFieldMetadata.getName(), _setValues(session, containedRecord, object));
                        session.persist(object);
                    }
                } else {
                    List<DataRecord> dataRecords = (List<DataRecord>) record.get(field);
                    if (dataRecords != null) {
                        List<Object> objects = new LinkedList<Object>();
                        for (DataRecord dataRecord : dataRecords) {
                            Wrapper object = createObject(contextClassLoader, referenceFieldMetadata.getReferencedType());
                            objects.add(_setValues(session, dataRecord, object));
                            session.persist(object);
                        }
                        wrapper.set(referenceFieldMetadata.getName(), objects);
                    }
                }
            } else if (field instanceof ReferenceFieldMetadata) {
                ReferenceFieldMetadata referenceFieldMetadata = (ReferenceFieldMetadata) mappedDatabaseField;

                if (!field.isMany()) {
                    DataRecord referencedObject = (DataRecord) record.get(field);
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
                        wrapper.set(mappedDatabaseField.getName(), getReferencedObject(contextClassLoader, session, referenceFieldMetadata.getReferencedType(), referenceId));
                    }
                } else {
                    List<DataRecord> referencedObjectList = (List<DataRecord>) record.get(field);
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
                            wrappers.add(getReferencedObject(contextClassLoader, session, referenceFieldMetadata.getReferencedType(), referenceId));
                        }
                        wrapper.set(mappedDatabaseField.getName(), wrappers);
                    }
                }
            } else {
                wrapper.set(mappedDatabaseField.getName(), record.get(field));
            }
        }
        return wrapper;
    }

    public DataRecord setValues(Wrapper from, DataRecord to) {
        StorageClassLoader contextClassLoader = (StorageClassLoader) Thread.currentThread().getContextClassLoader();
        ComplexTypeMetadata typeFromClass = contextClassLoader.getTypeFromClass(from.getClass());

        for (FieldMetadata field : typeFromClass.getFields()) {
            FieldMetadata userField = getUser(field);
            if (userField != null) {
                if (userField instanceof ContainedTypeFieldMetadata) {
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
                    ReferenceFieldMetadata referenceFieldMetadata = (ReferenceFieldMetadata) userField;
                    TypeMapping mapping = mappings.getMapping(referenceFieldMetadata.getReferencedType());
                    if (!userField.isMany()) {
                        DataRecord referencedRecord = new DataRecord(mapping.getUser(), UnsupportedDataRecordMetadata.INSTANCE);
                        Wrapper wrapper = (Wrapper) from.get(field.getName());
                        if (wrapper != null) {
                            for (FieldMetadata keyField : mapping.getDatabase().getKeyFields()) {
                                referencedRecord.set(mapping.getUser(keyField), wrapper.get(keyField.getName()));
                            }
                            to.set(userField, referencedRecord);
                        }
                    } else {
                        List<Wrapper> wrapperList = (List<Wrapper>) from.get(field.getName());
                        if (wrapperList != null) {
                            for (Wrapper wrapper : wrapperList) {
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
            }
        }
        return to;
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
                    Class<?> idClass = storageClassLoader.loadClass(referencedClass.getName() + "_ID");
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
        } catch (Exception e) {
            throw new RuntimeException("Could not create referenced object of type '" + referencedClass + "' with id '" + String.valueOf(referencedIdValue) + "'", e);
        }
    }
}
