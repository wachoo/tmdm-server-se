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
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import com.amalto.core.storage.inmemory.InMemoryJoinStrategy;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.CompoundFieldMetadata;
import org.talend.mdm.commmon.metadata.ContainedComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class SelectAnalyzer extends VisitorAdapter<Visitor<StorageResults>> {

    private static final Logger LOGGER = Logger.getLogger(SelectAnalyzer.class);

    private static final boolean ALLOW_IN_MEMORY_JOINS = Boolean.valueOf(MDMConfiguration.getConfiguration().getProperty("db.allow.memory.join", "false")); //$NON-NLS-1$ //$NON-NLS-2$

    private final List<TypedExpression> selectedFields = new LinkedList<TypedExpression>();

    private final MappingRepository mappings;

    private final StorageClassLoader storageClassLoader;

    private final Session session;

    private final Set<EndOfResultsCallback> callbacks;

    private final Storage storage;

    private final TableResolver resolver;

    private boolean isFullText = false;

    private boolean isCheckingProjection;

    private FullText fullTextExpression;

    SelectAnalyzer(MappingRepository mappings,
                   StorageClassLoader storageClassLoader,
                   Session session,
                   Set<EndOfResultsCallback> callbacks,
                   Storage storage,
                   TableResolver resolver) {
        this.mappings = mappings;
        this.storageClassLoader = storageClassLoader;
        this.session = session;
        this.callbacks = callbacks;
        this.storage = storage;
        this.resolver = resolver;
    }

    @Override
    public Visitor<StorageResults> visit(NativeQuery nativeQuery) {
        return new NativeQueryHandler(storage, storageClassLoader, session, callbacks);
    }

    @Override
    public Visitor<StorageResults> visit(Select select) {
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
            String fullTextValue = fullTextExpression.getValue().trim();
            if (fullTextValue.isEmpty() || StringUtils.containsOnly(fullTextValue, new char[]{'*'})) {
                if (select.getTypes().size() == 1) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Using \"standard query\" strategy (full text query on '*' or ' ')");
                    }
                    return new StandardQueryHandler(storage, mappings, resolver, storageClassLoader, session, select, this.selectedFields, callbacks);
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Using \"multi type\" strategy (full text query on '*' or ' ')");
                    }
                    return new MultiTypeStrategy(storage);
                }
            }
            if (rdbmsDataSource.supportFullText()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"full text query\" strategy");
                }
                return new FullTextQueryHandler(storage, mappings, storageClassLoader, session, select, this.selectedFields, callbacks);
            } else {
                throw new IllegalArgumentException("Storage '" + storage.getName() + "' is not configured to support full text queries.");
            }
        }
        if (condition != null) {
            ConditionChecks conditionChecks = new ConditionChecks(select);
            ConditionChecks.Result result = condition.accept(conditionChecks);
            if (result.id) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"get by id\" strategy");
                }
                return new IdQueryHandler(storage, mappings, storageClassLoader, session, select, this.selectedFields, callbacks);
            }
            if (ALLOW_IN_MEMORY_JOINS && result.limitJoins) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"in memory\" strategy");
                }
                return new InMemoryJoinStrategy(storage);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Using \"standard query\" strategy");
        }
        return new StandardQueryHandler(storage, mappings, resolver, storageClassLoader, session, select, this.selectedFields, callbacks);
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Isa isa) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(StringConstant constant) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(IsEmpty isEmpty) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(IsNull isNull) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(NotIsEmpty notIsEmpty) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(NotIsNull notIsNull) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Alias alias) {
        if (isCheckingProjection) {
            selectedFields.add(alias);
        }
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Timestamp timestamp) {
        if (isCheckingProjection) {
            selectedFields.add(timestamp);
        }
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(TaskId taskId) {
        if (isCheckingProjection) {
            selectedFields.add(taskId);
        }
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Type type) {
        if (isCheckingProjection) {
            selectedFields.add(type);
        }
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(BinaryLogicOperator condition) {
        condition.getLeft().accept(this);
        condition.getRight().accept(this);
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(UnaryLogicOperator condition) {
        condition.getCondition().accept(this);
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Compare condition) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(StagingStatus stagingStatus) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(StagingError stagingError) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(StagingSource stagingSource) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Condition condition) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Count count) {
        if (isCheckingProjection) {
            selectedFields.add(count);
        }
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(FullText fullText) {
        fullTextExpression = fullText;
        isFullText = true;
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Range range) {
        return null;
    }

    @Override
    public VisitorAdapter<StorageResults> visit(Field field) {
        selectedFields.add(field);
        return null;
    }

    private static class ConditionChecks extends VisitorAdapter<ConditionChecks.Result> {

        private final Result result = new Result();

        private final Select select;

        private FieldMetadata keyField;

        public ConditionChecks(Select select) {
            this.select = select;
        }

        class Result {
            boolean id = false;
            public boolean limitJoins = false;
        }

        @Override
        public Result visit(Isa isa) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(IsEmpty isEmpty) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(NotIsEmpty notIsEmpty) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(UnaryLogicOperator condition) {
            // TMDM-5319: Using a 'not' predicate, don't do a get by id.
            if (condition.getPredicate() == Predicate.NOT) {
                result.id = false;
                return result;
            } else {
                return condition.getCondition().accept(this);
            }
        }

        @Override
        public Result visit(Condition condition) {
            result.id = condition != UserQueryHelper.NO_OP_CONDITION;
            return result;
        }

        @Override
        public Result visit(BinaryLogicOperator condition) {
            result.id = condition.getLeft().accept(this).id && condition.getRight().accept(this).id;
            return result;
        }

        @Override
        public Result visit(StagingStatus stagingStatus) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(StagingError stagingError) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(StagingSource stagingSource) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(IndexedField indexedField) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(Compare condition) {
            result.id = condition.getLeft().accept(this).id && condition.getPredicate() == Predicate.EQUALS;
            return result;
        }

        @Override
        public Result visit(Id id) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(Timestamp timestamp) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(IsNull isNull) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(NotIsNull notIsNull) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(FullText fullText) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(Range range) {
            result.id = false;
            return result;
        }

        @Override
        public Result visit(Field field) {
            FieldMetadata fieldMetadata = field.getFieldMetadata();
            // Limit join for contained fields
            if (!result.limitJoins) {
                int level = 0;
                ComplexTypeMetadata containingType = fieldMetadata.getContainingType();
                while (containingType instanceof ContainedComplexTypeMetadata) {
                    containingType = ((ContainedComplexTypeMetadata) containingType).getContainerType();
                    level++;
                }
                if (level > 2 || !select.getTypes().contains(fieldMetadata.getContainingType())) {
                    result.limitJoins = true;
                }
            }
            if (fieldMetadata.getContainingType().getKeyFields().size() == 1) {
                if (fieldMetadata.isKey() && !(fieldMetadata instanceof CompoundFieldMetadata)) {
                    if (keyField != null) {
                        // At least twice an Id field means different Id values
                        // TODO Support for "entity/id = n AND entity/id = n" (could simplified to "entity/id = n").
                        result.id = false;
                    } else {
                        keyField = fieldMetadata;
                        result.id = true;
                    }
                }
            } // TODO Support compound key field.
            return result;
        }
    }
}
