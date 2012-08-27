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
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.hibernate.Session;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class IdQueryHandler extends AbstractQueryHandler {

    private Object idValue;

    public IdQueryHandler(Storage storage,
                          MappingRepository mappingMetadataRepository,
                          StorageClassLoader storageClassLoader,
                          Session session,
                          Select select,
                          List<TypedExpression> selectedFields,
                          Set<EndOfResultsCallback> callbacks) {
        super(storage, mappingMetadataRepository, storageClassLoader, session, select, selectedFields, callbacks);
    }

    @Override
    public StorageResults visit(Select select) {
        if (select.getCondition() == null) {
            throw new IllegalArgumentException("Select clause is expecting a condition.");
        }

        select.getCondition().accept(this);
        if (idValue == null) {
            throw new IllegalStateException("Expected condition to contain id to use for instance lookup.");
        }

        ComplexTypeMetadata mainType = select.getTypes().get(0);
        String mainTypeName = mainType.getName();
        String className = ClassCreator.PACKAGE_PREFIX + mainTypeName;

        Wrapper loadedObject = (Wrapper) session.get(className, (Serializable) idValue);

        if (loadedObject == null) {
            for (EndOfResultsCallback callback : callbacks) {
                callback.onEndOfResults();
            }
            CloseableIterator<DataRecord> iterator = new CloseableIterator<DataRecord>() {
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
            };
            return new HibernateStorageResults(storage, select, iterator) {
                @Override
                public int getCount() {
                    return 0;
                }
            };
        } else {
            Iterator objectIterator = Collections.singleton(loadedObject).iterator();
            CloseableIterator<DataRecord> iterator;
            if (!select.isProjection()) {
                iterator = new ListIterator(mappingMetadataRepository, storageClassLoader, objectIterator, callbacks);
            } else {
                iterator = new ListIterator(mappingMetadataRepository, storageClassLoader, objectIterator, callbacks) {
                    @Override
                    public DataRecord next() {
                        DataRecord next = super.next();
                        DataRecord nextRecord = new DataRecord(next.getType(), next.getRecordMetadata());
                        for (TypedExpression selectedField : selectedFields) {
                            FieldMetadata field = selectedField.accept(new VisitorAdapter<FieldMetadata>() {
                                @Override
                                public FieldMetadata visit(Field field) {
                                    return field.getFieldMetadata();
                                }

                                @Override
                                public FieldMetadata visit(Alias alias) {
                                    return alias.getTypedExpression().accept(this);
                                }
                            });
                            if (field != null) {
                                nextRecord.set(field, next.get(field));
                            }
                        }
                        for (EndOfResultsCallback callback : callbacks) {
                            callback.onEndOfResults();
                        }
                        return nextRecord;
                    }
                };
            }
            return new HibernateStorageResults(storage, select, iterator) {
                @Override
                public int getCount() {
                    return 1;
                }
            };
        }
    }

    @Override
    public StorageResults visit(Condition condition) {
        return null;
    }

    @Override
    public StorageResults visit(UnaryLogicOperator condition) {
        if (idValue == null) {
            idValue = condition.getCondition().accept(VALUE_ADAPTER);
        }
        return null;
    }

    @Override
    public StorageResults visit(BinaryLogicOperator condition) {
        if (idValue == null) { // try left
            idValue = condition.getLeft().accept(VALUE_ADAPTER);
        }
        if (idValue == null) { // ...then right
            idValue = condition.getRight().accept(VALUE_ADAPTER);
        }
        return null;
    }

    @Override
    public StorageResults visit(Compare condition) {
        if (idValue == null) {
            idValue = condition.getRight().accept(VALUE_ADAPTER);
        }
        return null;
    }

    @Override
    public StorageResults visit(Timestamp timestamp) {
        return null;
    }

    @Override
    public StorageResults visit(Revision revision) {
        return null;
    }

    @Override
    public StorageResults visit(TaskId taskId) {
        return null;
    }

    @Override
    public StorageResults visit(StagingStatus stagingStatus) {
        return super.visit(stagingStatus);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
