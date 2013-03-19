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
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.talend.mdm.commmon.metadata.CompoundFieldMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

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
        if (condition != null) {
            boolean isId = condition.accept(new IdCheck());
            if (isId) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"get by id\" strategy");
                }
                return new IdQueryHandler(storage, storageRepository, storageClassLoader, session, select, this.selectedFields, callbacks);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Using \"standard query\" strategy");
        }
        return new StandardQueryHandler(storage, storageRepository, resolver, storageClassLoader, session, select, this.selectedFields, callbacks);
    }

    @Override
    public AbstractQueryHandler visit(Isa isa) {
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
        if (isCheckingProjection) {
            selectedFields.add(alias);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Timestamp timestamp) {
        if (isCheckingProjection) {
            selectedFields.add(timestamp);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(TaskId taskId) {
        if (isCheckingProjection) {
            selectedFields.add(taskId);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Type type) {
        if (isCheckingProjection) {
            selectedFields.add(type);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(BinaryLogicOperator condition) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(UnaryLogicOperator condition) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Compare condition) {
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
    public AbstractQueryHandler visit(Condition condition) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Count count) {
        if (isCheckingProjection) {
            selectedFields.add(count);
        }
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
        selectedFields.add(field);
        return null;
    }

    private static class IdCheck extends VisitorAdapter<Boolean> {
        private FieldMetadata keyField;

        @Override
        public Boolean visit(Isa isa) {
            return false;
        }

        @Override
        public Boolean visit(IsEmpty isEmpty) {
            return false;
        }

        @Override
        public Boolean visit(NotIsEmpty notIsEmpty) {
            return false;
        }

        @Override
        public Boolean visit(UnaryLogicOperator condition) {
            // TMDM-5319: Using a 'not' predicate, don't do a get by id.
            if (condition.getPredicate() == Predicate.NOT) {
                return false;
            } else {
                return condition.getCondition().accept(this);
            }
        }

        @Override
        public Boolean visit(Condition condition) {
            return condition != UserQueryHelper.NO_OP_CONDITION;
        }

        @Override
        public Boolean visit(BinaryLogicOperator condition) {
            return condition.getLeft().accept(this) && condition.getRight().accept(this);
        }

        @Override
        public Boolean visit(StagingStatus stagingStatus) {
            return false;
        }

        @Override
        public Boolean visit(StagingError stagingError) {
            return false;
        }

        @Override
        public Boolean visit(StagingSource stagingSource) {
            return false;
        }

        @Override
        public Boolean visit(Compare condition) {
            return condition.getLeft().accept(this) && condition.getPredicate() == Predicate.EQUALS;
        }

        @Override
        public Boolean visit(Id id) {
            return false;
        }

        @Override
        public Boolean visit(Timestamp timestamp) {
            return false;
        }

        @Override
        public Boolean visit(IsNull isNull) {
            return false;
        }

        @Override
        public Boolean visit(NotIsNull notIsNull) {
            return false;
        }

        @Override
        public Boolean visit(FullText fullText) {
            return false;
        }

        @Override
        public Boolean visit(Range range) {
            return false;
        }

        @Override
        public Boolean visit(Field field) {
            FieldMetadata fieldMetadata = field.getFieldMetadata();
            if (fieldMetadata.getContainingType().getKeyFields().size() == 1) {
                if (fieldMetadata.isKey() && !(fieldMetadata instanceof CompoundFieldMetadata)) {
                    if (keyField != null) {
                        // At least twice an Id field means different Id values
                        // TODO Support for "entity/id = n AND entity/id = n" (could simplified to "entity/id = n").
                        return false;
                    } else {
                        keyField = fieldMetadata;
                        return true;
                    }
                }
            } // TODO Support compound key field.
            return false;
        }
    }
}
