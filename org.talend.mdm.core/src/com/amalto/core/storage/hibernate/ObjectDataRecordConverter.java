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
import com.amalto.core.storage.record.DataRecordConverter;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
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
            throw new IllegalArgumentException("Mapping can not be null");
        }

        try {
            Class<?> mainInstanceClass = storageClassLoader.findClass(dataRecord.getType().getName());
            Wrapper mainInstance = (Wrapper) mainInstanceClass.newInstance();
            mapping.setValues(session, dataRecord, mainInstance);
            return mainInstance;
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while creating internal object for type '" + dataRecord.getType().getName() + "'", e);
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
