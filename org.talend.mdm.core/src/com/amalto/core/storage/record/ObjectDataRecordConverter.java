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

import com.amalto.core.metadata.*;
import com.amalto.core.storage.hibernate.HibernateClassWrapper;
import com.amalto.core.storage.hibernate.StorageClassLoader;
import com.amalto.core.storage.hibernate.enhancement.TypeMapping;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ObjectDataRecordConverter implements DataRecordConverter<Object> {

    private final StorageClassLoader storageClassLoader;

    private final Session session;

    public ObjectDataRecordConverter(StorageClassLoader storageClassLoader, Session session) {
        this.storageClassLoader = storageClassLoader;
        this.session = session;
    }

    public Object convert(DataRecord dataRecord, TypeMapping mapping) {
        try {
            mapping.toUser();
            Class<?> newInstanceClass = storageClassLoader.findClass(mapping.getName());
            HibernateClassWrapper mainInstance = (HibernateClassWrapper) newInstanceClass.newInstance();

            Set<FieldMetadata> fields = dataRecord.getSetFields();
            for (FieldMetadata field : fields) {
                Object value = dataRecord.get(field);
                FieldMetadata flattenField = mapping.getFlatten(field);
                if (flattenField != null) {
                    field = flattenField;
                }

                // "instance of" could be replaced by visitor on field... but is a bit too much for this simple step.
                if (field instanceof SimpleTypeFieldMetadata || field instanceof EnumerationFieldMetadata) {
                    if (!field.isMany()) {
                        if (value != null) {
                            mainInstance.set(field.getName(), value);
                        }
                    } else {
                        List list = (List) mainInstance.get(field.getName());
                        if (list == null) {
                            mainInstance.set(field.getName(), value);
                        } else {
                            List valueList = (List) value;
                            list.addAll(valueList);
                        }
                    }
                } else if (field instanceof ReferenceFieldMetadata) {
                    ReferenceFieldMetadata referenceFieldMetadata = (ReferenceFieldMetadata) field;
                    TypeMetadata referencedType = referenceFieldMetadata.getReferencedType();

                    Class<?> referencedClass = storageClassLoader.findClass(referencedType.getName());

                    if (!field.isMany()) {
                        Object referencedObject = createReferencedObject((ComplexTypeMetadata) referencedType, referencedClass, value);
                        mainInstance.set(field.getName(), referencedObject);
                    } else {
                        List list = (List) mainInstance.get(field.getName());
                        if (list == null) {
                            list = new LinkedList();
                            mainInstance.set(field.getName(), list);
                        }

                        List valueList = (List) value;
                        for (Object current : valueList) {
                            list.add(createReferencedObject((ComplexTypeMetadata) referencedType, referencedClass, current));
                        }
                    }
                }
            }

            mainInstance.taskId(dataRecord.getRecordMetadata().getTaskId());
            mainInstance.timestamp(dataRecord.getRecordMetadata().getLastModificationTime());

            return mainInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object createReferencedObject(ComplexTypeMetadata referencedType, Class<?> referencedClass, Object referencedIdValue) throws InstantiationException, IllegalAccessException {
        if (referencedIdValue == null) {
            return null; // Means no reference (reference is null).
        }
        if (referencedIdValue instanceof HibernateClassWrapper) {
            return referencedIdValue; // It's already the referenced object.
        }

        // Try to load object from current session
        if (referencedIdValue instanceof List) {
            // Handle composite id values
            Serializable referencedValueId = createCompositeId(storageClassLoader, referencedClass, (List) referencedIdValue);

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
            HibernateClassWrapper referencedObject = (HibernateClassWrapper) referencedClass.newInstance();
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
