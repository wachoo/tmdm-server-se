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

import com.amalto.core.metadata.CompoundFieldMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class SelectAnalyzer extends VisitorAdapter<AbstractQueryHandler> {

    private static final Logger LOGGER = Logger.getLogger(SelectAnalyzer.class);

    private final List<TypedExpression> selectedFields = new LinkedList<TypedExpression>();

    private final MappingRepository storageRepository;

    private final StorageClassLoader storageClassLoader;

    private final Session session;

    private final Set<EndOfResultsCallback> callbacks;

    private final Storage storage;

    private final TableResolver resolver;

    private boolean isFullText = false;

    private boolean isOnlyId = false;

    private FieldMetadata keyField = null;

    private boolean isCheckingProjection;

    SelectAnalyzer(MappingRepository storageRepository, StorageClassLoader storageClassLoader, Session session, Set<EndOfResultsCallback> callbacks, Storage storage, TableResolver resolver) {
        this.storageRepository = storageRepository;
        this.storageClassLoader = storageClassLoader;
        this.session = session;
        this.callbacks = callbacks;
        this.storage = storage;
        this.resolver = resolver;
    }

    @Override
    public AbstractQueryHandler visit(NativeQuery nativeQuery) {
        return new NativeQueryHandler(storage, storageRepository, storageClassLoader, session, callbacks);
    }

    @Override
    public AbstractQueryHandler visit(Select select) {
        List<TypedExpression> selectedFields = select.getSelectedFields();
        isCheckingProjection = true;
        {
            for (Expression selectedField : selectedFields) {
                selectedField.accept(this);
            }
        }
        isCheckingProjection = false;
        Condition condition = select.getCondition();
        if (condition != null) {
            condition.accept(this);
        }
        if (condition != null) {
            if (isOnlyId) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"get by id\" strategy");
                }
                return new IdQueryHandler(storage, storageRepository, storageClassLoader, session, select, this.selectedFields, callbacks);
            }
        }
        if (isFullText) {
            DataSource dataSource = storage.getDataSource();
            RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) dataSource;
            if (rdbmsDataSource.supportFullText()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"full text query\" strategy");
                }
                return new FullTextQueryHandler(storage, storageRepository, storageClassLoader, session, select, this.selectedFields, callbacks);
            } else {
                throw new IllegalArgumentException("Storage '" + storage.getName() + "' is not configured to support full text queries.");
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Using \"standard query\" strategy");
        }
        return new StandardQueryHandler(storage, storageRepository, resolver, storageClassLoader, session, select, this.selectedFields, callbacks);
    }

    @Override
    public AbstractQueryHandler visit(Isa isa) {
        isOnlyId = false;
        return null;
    }

    @Override
    public AbstractQueryHandler visit(StringConstant constant) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(IsEmpty isEmpty) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(IsNull isNull) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(NotIsEmpty notIsEmpty) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(NotIsNull notIsNull) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Alias alias) {
        selectedFields.add(alias);
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Timestamp timestamp) {
        selectedFields.add(timestamp);
        return null;
    }

    @Override
    public AbstractQueryHandler visit(TaskId taskId) {
        selectedFields.add(taskId);
        return null;
    }

    @Override
    public AbstractQueryHandler visit(BinaryLogicOperator condition) {
        condition.getLeft().accept(this);
        condition.getRight().accept(this);
        return null;
    }

    @Override
    public AbstractQueryHandler visit(UnaryLogicOperator condition) {
        condition.getCondition().accept(this);
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Compare condition) {
        condition.getLeft().accept(this);
        if (isOnlyId) {
            isOnlyId = condition.getPredicate() == Predicate.EQUALS;
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(StagingStatus stagingStatus) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(StagingError stagingError) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(StagingSource stagingSource) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Revision revision) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Condition condition) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Count count) {
        selectedFields.add(count);
        return null;
    }

    @Override
    public AbstractQueryHandler visit(FullText fullText) {
        isOnlyId = false;
        isFullText = true;
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Range range) {
        isOnlyId = false;
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Field field) {
        if (!isCheckingProjection) {
            FieldMetadata fieldMetadata = field.getFieldMetadata();
            if(fieldMetadata.getContainingType().getKeyFields().size() == 1) {
                if (fieldMetadata.isKey() && !(fieldMetadata instanceof CompoundFieldMetadata)) {
                    if (keyField != null) {
                        // At least twice an Id field means different Id values
                        // TODO Support for "entity/id = n AND entity/id = n" (could simplified to "entity/id = n").
                        isOnlyId = false;
                    } else {
                        keyField = fieldMetadata;
                        isOnlyId = true;
                    }
                }
            } // TODO Support compound key field.
        }
        selectedFields.add(field);
        return null;
    }
}
