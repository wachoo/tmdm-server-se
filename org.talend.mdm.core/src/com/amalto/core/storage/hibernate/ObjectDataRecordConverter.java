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

import com.amalto.core.metadata.MetadataUtils;
import org.talend.mdm.commmon.metadata.*;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordConverter;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectDataRecordConverter implements DataRecordConverter<Object> {

    private final StorageClassLoader storageClassLoader;

    private final Session session;

    public ObjectDataRecordConverter(StorageClassLoader storageClassLoader, Session session) {
        this.storageClassLoader = storageClassLoader;
        this.session = session;
    }

    public Object convert(DataRecord dataRecord, TypeMapping mapping) {
        if (dataRecord == null) {
            throw new IllegalArgumentException("Data record can not be null");
        }
        if (mapping == null) {
            throw new IllegalArgumentException("Mapping cannot be null");
        }
        try {
            Class<?> mainInstanceClass = storageClassLoader.findClass(mapping.getDatabase().getName());
            // Try to load existing instance (if any).
            Wrapper mainInstance;
            try {
                Collection<FieldMetadata> keyFields = dataRecord.getType().getKeyFields();
                if (keyFields.size() == 0) {
                    throw new IllegalArgumentException("Type '" + dataRecord.getType().getName() + "' does not define any key field.");
                } else if (keyFields.size() == 1) {
                    String keyFieldName = keyFields.iterator().next().getName();
                    Serializable id = (Serializable) dataRecord.get(keyFieldName);
                    if (id == null) {
                        throw new IllegalArgumentException("Instance of type '" + dataRecord.getType().getName() + "' does not have value for '" + keyFieldName + "'.");
                    }
                    mainInstance = (Wrapper) session.get(mainInstanceClass, id);
                } else {
                    List<Object> compositeIdValues = new ArrayList<Object>(keyFields.size());
                    for (FieldMetadata keyField : keyFields) {
                        compositeIdValues.add(MetadataUtils.convert(String.valueOf(dataRecord.get(keyField)), mapping.getDatabase(keyField)));
                    }
                    mainInstance = (Wrapper) session.get(mainInstanceClass, createCompositeId(storageClassLoader, mainInstanceClass, compositeIdValues));
                }
            } catch (ObjectNotFoundException e) {
                mainInstance = null;
            }
            // Instance does not exist, so create it.
            if(mainInstance == null) {
                mainInstance = (Wrapper) mainInstanceClass.newInstance();
            }
            mapping.setValues(session, dataRecord, mainInstance);
            return mainInstance;
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while creating internal object for type '" + dataRecord.getType().getName() + "'", e);
        }
    }

    public static Serializable createCompositeId(ClassLoader classLoader, Class<?> clazz, List<Object> compositeIdValues) {
        try {
            Class<?> idClass = classLoader.loadClass(clazz.getName() + "_ID"); //$NON-NLS-1$
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
