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
import com.amalto.core.query.user.Compare;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.TypedExpression;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.hibernate.enhancement.HibernateClassCreator;
import com.amalto.core.storage.hibernate.enhancement.TypeMapping;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.ObjectDataRecordReader;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

class IdQueryHandler extends AbstractQueryHandler {

    private Object object;

    public IdQueryHandler(Storage storage,
                          MappingMetadataRepository mappingMetadataRepository,
                          StorageClassLoader storageClassLoader,
                          Session session,
                          Select select,
                          List<TypedExpression> selectedFields,
                          Set<EndOfResultsCallback> callbacks) {
        super(storage, mappingMetadataRepository, storageClassLoader, session, select, selectedFields, callbacks);
    }

    @Override
    public StorageResults visit(Select select) {
        if (select.isProjection()) {
            throw new NotImplementedException("No support for projection in select by ID");
        }
        if (select.getCondition() == null) {
            throw new IllegalArgumentException("Select clause is expected a condition.");
        }

        select.getCondition().accept(this);
        
        ComplexTypeMetadata mainType = select.getTypes().get(0);
        String mainTypeName = mainType.getName();
        String className = HibernateClassCreator.PACKAGE_PREFIX + mainTypeName;

        HibernateClassWrapper loadedObject = (HibernateClassWrapper) session.get(className, (Serializable) object);

        if (loadedObject == null) {
            return new HibernateStorageResults(storage, select, new CloseableIterator<DataRecord>() {
                public boolean hasNext() {
                    return false;
                }

                public DataRecord next() {
                    throw new UnsupportedOperationException();
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public void close() throws IOException {
                    // Nothing to do.
                }
            });
        } else {
            TypeMapping mapping = mappingMetadataRepository.getMapping(mainType);
            return new HibernateStorageResults(storage, select, new DataRecordIterator(mapping, select.getRevisionId(), loadedObject));
        }
    }


    @Override
    public StorageResults visit(Compare condition) {
        object = condition.getRight().accept(VALUE_ADAPTER);
        return null;
    }

    private class DataRecordIterator extends CloseableIterator<DataRecord> {

        private final TypeMapping mainType;

        private final String revisionId;

        private final HibernateClassWrapper loadedObject;

        private boolean hasRead;

        public DataRecordIterator(TypeMapping mainType, String revisionId, HibernateClassWrapper loadedObject) {
            this.mainType = mainType;
            this.revisionId = revisionId;
            this.loadedObject = loadedObject;
        }

        public boolean hasNext() {
            return !hasRead;
        }

        public DataRecord next() {
            try {
                mainType.toFlatten();
                ObjectDataRecordReader reader = new ObjectDataRecordReader(mainType.getFields());
                return reader.read(storage.getName(), Long.parseLong(revisionId), mainType, loadedObject);
            } finally {
                hasRead = true;
            }
        }

        public void remove() {
        }

        public void close() throws IOException {
            hasRead = true;
        }
    }
}
