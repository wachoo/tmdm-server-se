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

import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.hibernate.Session;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class SelectAnalyzer extends VisitorAdapter<AbstractQueryHandler> {

    private final List<TypedExpression> selectedFields = new LinkedList<TypedExpression>();

    private final MappingMetadataRepository storageRepository;

    private final StorageClassLoader storageClassLoader;

    private final Session session;

    private final Set<EndOfResultsCallback> callbacks;

    private final Storage storage;

    private boolean isFullText = false;

    private boolean isOnlyId = false;

    SelectAnalyzer(MappingMetadataRepository storageRepository, StorageClassLoader storageClassLoader, Session session, Set<EndOfResultsCallback> callbacks, Storage storage) {
        this.storageRepository = storageRepository;
        this.storageClassLoader = storageClassLoader;
        this.session = session;
        this.callbacks = callbacks;
        this.storage = storage;
    }

    @Override
    public AbstractQueryHandler visit(Select select) {
        List<Expression> selectedFields = select.getSelectedFields();
        for (Expression selectedField : selectedFields) {
            selectedField.accept(this);
        }

        Condition condition = select.getCondition();
        if (condition != null) {
            condition.accept(this);
            if (isOnlyId) {
                return new IdQueryHandler(storage, storageRepository, storageClassLoader, session, select, this.selectedFields, callbacks);
            }
        }

        if (isFullText) {
            DataSource dataSource = storage.getDataSource();
            RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) dataSource;
            if (rdbmsDataSource.supportFullText()) {
                return new FullTextQueryHandler(storage, storageRepository, storageClassLoader, session, select, this.selectedFields, callbacks);
            } else {
                throw new IllegalArgumentException("Storage '" + storage.getName() + "' is not configured to support full text queries.");
            }
        }

        return new StandardQueryHandler(storage, storageRepository, storageClassLoader, session, select, this.selectedFields, callbacks);
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
        if (isOnlyId) {
            isOnlyId = condition.getPredicate() == Predicate.EQUALS;
        }
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
        isFullText = true;
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Range range) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Field field) {
        if (!field.getFieldMetadata().isKey()) {
            isOnlyId = false;
        }
        selectedFields.add(field);
        return null;
    }
}
