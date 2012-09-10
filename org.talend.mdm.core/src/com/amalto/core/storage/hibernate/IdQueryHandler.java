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
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.xml.XMLConstants;
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

        CloseableIterator<DataRecord> emptyIterator = new CloseableIterator<DataRecord>() {
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

        if (idValue == null) {
            System.out.println("No id, no need to query database!");
            for (EndOfResultsCallback callback : callbacks) {
                callback.onEndOfResults();
            }
            return new HibernateStorageResults(storage, select, emptyIterator) {
                @Override
                public int getCount() {
                    return 0;
                }
            };
        }

        Wrapper loadedObject = (Wrapper) session.get(className, (Serializable) idValue);

        if (loadedObject == null) {
            for (EndOfResultsCallback callback : callbacks) {
                callback.onEndOfResults();
            }
            return new HibernateStorageResults(storage, select, emptyIterator) {
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
                        final DataRecord next = super.next();
                        final ComplexTypeMetadata explicitProjectionType = new ComplexTypeMetadataImpl(StringUtils.EMPTY, Storage.PROJECTION_TYPE, false);
                        final DataRecord nextRecord = new DataRecord(explicitProjectionType, next.getRecordMetadata());
                        ExplicitProjectionAdapter projectionAdapter = new ExplicitProjectionAdapter(next, explicitProjectionType, nextRecord);
                        for (TypedExpression selectedField : selectedFields) {
                            selectedField.accept(projectionAdapter);
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
        return null;
    }

    private static class ExplicitProjectionAdapter extends VisitorAdapter<Void> {

        private String currentAliasName;
        private final DataRecord next;
        private final ComplexTypeMetadata explicitProjectionType;
        private final DataRecord nextRecord;

        public ExplicitProjectionAdapter(DataRecord next, ComplexTypeMetadata explicitProjectionType, DataRecord nextRecord) {
            this.next = next;
            this.explicitProjectionType = explicitProjectionType;
            this.nextRecord = nextRecord;
        }

        @Override
        public Void visit(Field field) {
            FieldMetadata fieldMetadata = field.getFieldMetadata();
            Object o = next.get(fieldMetadata);
            if (currentAliasName == null) {
                explicitProjectionType.addField(fieldMetadata);
                nextRecord.set(fieldMetadata, o);
            } else {
                SimpleTypeFieldMetadata fieldType = new SimpleTypeFieldMetadata(explicitProjectionType,
                        false,
                        false,
                        false,
                        currentAliasName,
                        fieldMetadata.getType(),
                        Collections.<String>emptyList(),
                        Collections.<String>emptyList());
                explicitProjectionType.addField(fieldType);
                nextRecord.set(fieldMetadata, o);
            }
            return null;
        }

        @Override
        public Void visit(Alias alias) {
            currentAliasName = alias.getAliasName();
            alias.getTypedExpression().accept(this);
            currentAliasName = null;
            return null;
        }

        @Override
        public Void visit(StringConstant constant) {
            SimpleTypeFieldMetadata fieldType = new SimpleTypeFieldMetadata(explicitProjectionType,
                    false,
                    false,
                    false,
                    currentAliasName,
                    new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, constant.getTypeName()),
                    Collections.<String>emptyList(),
                    Collections.<String>emptyList());
            explicitProjectionType.addField(fieldType);
            nextRecord.set(fieldType, constant.getValue());
            return null;
        }

        @Override
        public Void visit(Timestamp timestamp) {
            nextRecord.getRecordMetadata().setLastModificationTime(next.getRecordMetadata().getLastModificationTime());
            return null;
        }

        @Override
        public Void visit(Revision revision) {
            nextRecord.setRevisionId(next.getRevisionId());
            return null;
        }

        @Override
        public Void visit(TaskId taskId) {
            nextRecord.getRecordMetadata().setTaskId(next.getRecordMetadata().getTaskId());
            return null;
        }
    }
}
