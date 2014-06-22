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
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.ObjectDataRecordReader;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class ScrollableIterator extends CloseableIterator<DataRecord> {

    private final static Logger LOGGER = Logger.getLogger(ScrollableIterator.class);

    private final static Map<ComplexTypeMetadata, ObjectDataRecordReader> typeToReader = new HashMap<ComplexTypeMetadata, ObjectDataRecordReader>();

    private final ScrollableResults results;

    private final Set<EndOfResultsCallback> callbacks;

    private final StorageClassLoader storageClassLoader;

    private final MappingRepository storageRepository;

    public ScrollableIterator(MappingRepository storageRepository, StorageClassLoader storageClassLoader, ScrollableResults results, Set<EndOfResultsCallback> callbacks) {
        this.storageRepository = storageRepository;
        this.storageClassLoader = storageClassLoader;
        this.results = results;
        this.callbacks = callbacks;
    }

    public boolean hasNext() {
        boolean hasNext;
        try {
            hasNext = results.next();
            if (!hasNext) {
                notifyCallbacks();
            }
        } catch (HibernateException e) {
            LOGGER.error("Could not get next result due to exception.", e);
            notifyCallbacks(); // In case of exception, notify the callbacks so statelessSession can be closed.
            return false;
        }

        return hasNext;
    }

    public DataRecord next() {
        Object next;
        try {
            next = results.get()[0];
        } catch (Exception e) {
            notifyCallbacks(); // In case of exception, notify the callbacks so statelessSession can be closed.
            throw new RuntimeException(e);
        }

        ComplexTypeMetadata type = storageClassLoader.getTypeFromClass(next.getClass());
        ObjectDataRecordReader reader = getReader(type);
        if (!(next instanceof Wrapper)) {
            throw new IllegalArgumentException("Result object is not an instance of " + Wrapper.class.getName());
        }
        return reader.read(storageRepository.getMappingFromDatabase(type), (Wrapper) next);
    }

    // Cache type readers
    private static ObjectDataRecordReader getReader(ComplexTypeMetadata type) {
        synchronized (typeToReader) {
            ObjectDataRecordReader reader = typeToReader.get(type);
            if (reader == null) {
                reader = new ObjectDataRecordReader();
                typeToReader.put(type, reader);
            }
            return reader;
        }
    }

    public static void resetTypeReaders() {
        synchronized (typeToReader) {
            typeToReader.clear();
        }
    }

    public void remove() {
        throw new NotImplementedException();
    }

    private void notifyCallbacks() {
        for (EndOfResultsCallback callback : callbacks) {
            try {
                callback.onEndOfResults();
            } catch (Throwable t) {
                // Catch Throwable and log it (to ensure all callbacks get called).
                LOGGER.error("End of result callback exception", t);
            }
        }
    }

    public void close() throws IOException {
        notifyCallbacks();
    }
}
