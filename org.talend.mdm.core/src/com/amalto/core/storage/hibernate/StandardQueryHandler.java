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
import com.amalto.core.metadata.ContainedComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.query.user.*;
import com.amalto.core.query.user.Expression;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinFragment;

import java.util.*;

import static org.hibernate.criterion.Restrictions.*;

class StandardQueryHandler extends AbstractQueryHandler {

    private static final Logger LOGGER = Logger.getLogger(StandardQueryHandler.class);

    private final CriterionAdapter CRITERION_VISITOR = new CriterionAdapter();

    private Criteria criteria;

    private ProjectionList projectionList;

    private ComplexTypeMetadata mainType;

    private final StandardQueryHandler.CriterionFieldCondition criterionFieldCondition;

    private int aliasCount = 0;

    private final Map<FieldMetadata, String> joinFieldsToAlias = new HashMap<FieldMetadata, String>();

    public StandardQueryHandler(Storage storage,
                                MappingRepository mappingMetadataRepository,
                                StorageClassLoader storageClassLoader,
                                Session session,
                                Select select,
                                List<TypedExpression> selectedFields,
                                Set<EndOfResultsCallback> callbacks) {
        super(storage, mappingMetadataRepository, storageClassLoader, session, select, selectedFields, callbacks);
        criterionFieldCondition = new CriterionFieldCondition();
    }

    private StorageResults createResults(List list, boolean isProjection) {
        CloseableIterator<DataRecord> iterator;
        Iterator listIterator = list.iterator();
        if (isProjection) {
            iterator = new ProjectionIterator(listIterator, selectedFields, callbacks);
        } else {
            iterator = new ListIterator(mappingMetadataRepository,
                    storageClassLoader,
                    listIterator,
                    callbacks);
        }

        return new HibernateStorageResults(storage, select, iterator);
    }

    private StorageResults createResults(ScrollableResults scrollableResults, boolean isProjection) {
        CloseableIterator<DataRecord> iterator;
        if (isProjection) {
            iterator = new ProjectionIterator(scrollableResults, selectedFields, callbacks);
        } else {
            iterator = new ScrollableIterator(mappingMetadataRepository,
                    storageClassLoader,
                    scrollableResults,
                    callbacks);
        }
        return new HibernateStorageResults(storage, select, iterator);
    }

    @Override
    public StorageResults visit(Join join) {
        FieldMetadata fieldMetadata = join.getRightField().getFieldMetadata();

        // Choose the right join type
        String rightTableName = fieldMetadata.getContainingType().getName();
        int joinType;
        switch (join.getJoinType()) {
            case INNER:
                joinType = JoinFragment.INNER_JOIN;
                break;
            case LEFT_OUTER:
                joinType = JoinFragment.LEFT_OUTER_JOIN;
                break;
            case FULL:
                joinType = JoinFragment.FULL_JOIN;
                break;
            default:
                throw new NotImplementedException("No support for join type " + join.getJoinType());
        }

        // Select a path from mainType to the selected field (properties are '.' separated).
        TypeMapping mainTypeMapping = mappingMetadataRepository.getMapping(mainType);
        ComplexTypeMetadata containingType = join.getLeftField().getFieldMetadata().getContainingType();
        while (containingType instanceof ContainedComplexTypeMetadata) {
            containingType = ((ContainedComplexTypeMetadata) containingType).getContainerType();
        }
        TypeMapping leftTypeMapping = mappingMetadataRepository.getMapping(containingType);
        List<FieldMetadata> path = MetadataUtils.path(mainTypeMapping.getDatabase(), leftTypeMapping.getDatabase(join.getLeftField().getFieldMetadata()));
        if (path.isEmpty()) {
            // Empty path means no path then this is an error (all joined entities should be reachable from main type).
            String destinationFieldName;
            try {
                destinationFieldName = fieldMetadata.getName();
            } catch (Exception e) {
                // Ignored
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Exception occurred during exception creation", e);
                }
                destinationFieldName = String.valueOf(fieldMetadata);
            }
            throw new IllegalArgumentException("Join to '" + destinationFieldName + "' (in type '"
                    + fieldMetadata.getContainingType().getName() + "') is invalid since there is no path from '"
                    + mainType.getName() + "' to this field.");
        }
        Iterator<FieldMetadata> pathIterator = path.iterator();
        String previousAlias = mainType.getName();
        while (pathIterator.hasNext()) {
            FieldMetadata nextField = pathIterator.next();
            String newAlias = "a" + aliasCount++; //$NON-NLS-1$
            // TODO One interesting improvement here: can add conditions on rightTable when defining join.
            if (pathIterator.hasNext()) {
                if (!joinFieldsToAlias.containsKey(nextField)) {
                    criteria.createAlias(previousAlias + '.' + nextField.getName(), newAlias, joinType);
                    joinFieldsToAlias.put(nextField, newAlias);
                    previousAlias = newAlias;
                } else {
                    previousAlias = joinFieldsToAlias.get(nextField);
                }
            } else {
                if (!joinFieldsToAlias.containsKey(nextField)) {
                    criteria.createAlias(previousAlias + '.' + nextField.getName(), rightTableName, joinType);
                    joinFieldsToAlias.put(nextField, rightTableName);
                    previousAlias = rightTableName;
                } else {
                    previousAlias = joinFieldsToAlias.get(nextField);
                }
            }
        }
        return null;
    }

    @Override
    public StorageResults visit(Alias alias) {
        alias.getTypedExpression().accept(this);
        return null;
    }

    @Override
    public StorageResults visit(Revision revision) {
        projectionList.add(Projections.property(Storage.METADATA_REVISION_ID));
        return null;
    }

    @Override
    public StorageResults visit(Timestamp timestamp) {
        projectionList.add(Projections.property(Storage.METADATA_TIMESTAMP));
        return null;
    }

    @Override
    public StorageResults visit(TaskId taskId) {
        projectionList.add(Projections.property(Storage.METADATA_TASK_ID));
        return null;
    }

    @Override
    public StorageResults visit(Field field) {
        projectionList.add(Projections.property(getFieldName(field, mappingMetadataRepository)));
        return null;
    }

    @Override
    public StorageResults visit(Count count) {
        // Do a count on key field (first key field in case of composite key but this should not matter).
        Field keyField = new Field(mainType.getKeyFields().get(0));
        projectionList.add(Projections.count(getFieldName(keyField, mappingMetadataRepository)));
        return null;
    }

    @Override
    public StorageResults visit(Select select) {
        mainType = select.getTypes().get(0);
        String mainTypeName = mainType.getName();
        String className = ClassCreator.PACKAGE_PREFIX + mainTypeName;
        criteria = session.createCriteria(className, mainTypeName);
        criteria.setReadOnly(true); // We are reading data, turns on ready only mode.

        List<Join> joins = select.getJoins();
        for (Join join : joins) {
            join.accept(this);
        }

        // If select is not a projection, selecting root type is enough, otherwise add projection for selected fields.
        if (select.isProjection()) {
            projectionList = Projections.projectionList();
            {
                List<TypedExpression> selectedFields = select.getSelectedFields();
                for (Expression selectedField : selectedFields) {
                    selectedField.accept(this);
                }
            }
            criteria.setProjection(projectionList);
        }

        Condition condition = select.getCondition();
        if (condition != null) {
            boolean hasActualCondition = condition.accept(new VisitorAdapter<Boolean>() {
                @Override
                public Boolean visit(Condition condition) {
                    return condition != UserQueryHelper.NO_OP_CONDITION;
                }

                @Override
                public Boolean visit(BinaryLogicOperator condition) {
                    return condition.getLeft().accept(this) || condition.getRight().accept(this);
                }

                @Override
                public Boolean visit(UnaryLogicOperator condition) {
                    return condition.getCondition().accept(this);
                }

                @Override
                public Boolean visit(NotIsEmpty notIsEmpty) {
                    return true;
                }

                @Override
                public Boolean visit(NotIsNull notIsNull) {
                    return true;
                }

                @Override
                public Boolean visit(IsEmpty isEmpty) {
                    return true;
                }

                @Override
                public Boolean visit(IsNull isNull) {
                    return true;
                }

                @Override
                public Boolean visit(Compare condition) {
                    return true; // Consider all "compare" as hibernate-worthy conditions.
                }

                @Override
                public Boolean visit(FullText fullText) {
                    return true; // Consider all "full text" as hibernate-worthy conditions.
                }

                @Override
                public Boolean visit(Range range) {
                    return true; // Consider all "range" as hibernate-worthy conditions.
                }
            });
            if (hasActualCondition) {
                condition.accept(this);
            }
        }

        OrderBy orderBy = select.getOrderBy();
        if (orderBy != null) {
            orderBy.accept(this);
        }

        Paging paging = select.getPaging();
        paging.accept(this);

        int pageSize = paging.getLimit();
        boolean hasPaging = pageSize < Integer.MAX_VALUE;
        if (!hasPaging) {
            return createResults(criteria.scroll(ScrollMode.FORWARD_ONLY), select.isProjection());
        } else {
            return createResults(criteria.list(), select.isProjection());
        }
    }

    @Override
    public StorageResults visit(Paging paging) {
        criteria.setFirstResult(paging.getStart());
        criteria.setFetchSize(JDBC_FETCH_SIZE);
        criteria.setMaxResults(paging.getLimit());
        return null;
    }

    @Override
    public StorageResults visit(OrderBy orderBy) {
        String fieldName = getFieldName(orderBy.getField(), mappingMetadataRepository);
        OrderBy.Direction direction = orderBy.getDirection();
        switch (direction) {
            case ASC:
                criteria.addOrder(Order.asc(fieldName));
                break;
            case DESC:
                criteria.addOrder(Order.desc(fieldName));
                break;
        }

        return null;
    }

    @Override
    public StorageResults visit(BinaryLogicOperator condition) {
        criteria.add(condition.accept(CRITERION_VISITOR));
        return null;
    }

    @Override
    public StorageResults visit(UnaryLogicOperator condition) {
        criteria.add(condition.accept(CRITERION_VISITOR));
        return null;
    }

    @Override
    public StorageResults visit(Compare condition) {
        Criterion criterion = condition.accept(CRITERION_VISITOR);
        criteria.add(criterion);
        return null;
    }

    @Override
    public StorageResults visit(IsNull isNull) {
        Criterion criterion = isNull.accept(CRITERION_VISITOR);
        criteria.add(criterion);
        return null;
    }

    @Override
    public StorageResults visit(IsEmpty isEmpty) {
        Criterion criterion = isEmpty.accept(CRITERION_VISITOR);
        criteria.add(criterion);
        return null;
    }

    @Override
    public StorageResults visit(NotIsEmpty notIsEmpty) {
        Criterion criterion = notIsEmpty.accept(CRITERION_VISITOR);
        criteria.add(criterion);
        return null;
    }

    @Override
    public StorageResults visit(NotIsNull notIsNull) {
        Criterion criterion = notIsNull.accept(CRITERION_VISITOR);
        criteria.add(criterion);
        return null;
    }

    @Override
    public StorageResults visit(Range range) {
        Object start = range.getStart().accept(VALUE_ADAPTER);
        Object end = range.getEnd().accept(VALUE_ADAPTER);
        criteria.add(Restrictions.between(range.accept(FIELD_VISITOR), start, end));
        return null;
    }

    private class CriterionAdapter extends VisitorAdapter<Criterion> {

        @Override
        public Criterion visit(Condition condition) {
            if (condition == UserQueryHelper.NO_OP_CONDITION) {
                return NO_OP_CRITERION;
            }
            return super.visit(condition);
        }

        @Override
        public Criterion visit(UnaryLogicOperator condition) {
            Predicate predicate = condition.getPredicate();
            Criterion conditionCriterion = condition.getCondition().accept(this);

            if (predicate == Predicate.NOT) {
                return not(conditionCriterion);
            } else {
                throw new NotImplementedException("No support for predicate '" + predicate + "'");
            }
        }

        @Override
        public Criterion visit(IsNull isNull) {
            FieldCondition fieldCondition = isNull.getField().accept(new CriterionFieldCondition());
            if (fieldCondition.isMany) {
                throw new UnsupportedOperationException("Does not support isNull operation on collections.");
            }
            return Restrictions.isNull(fieldCondition.criterionFieldName);
        }

        @Override
        public Criterion visit(IsEmpty isEmpty) {
            FieldCondition fieldCondition = isEmpty.getField().accept(new CriterionFieldCondition());
            if (fieldCondition.isMany) {
                return Restrictions.isEmpty(fieldCondition.criterionFieldName);
            } else {
                return Restrictions.eq(fieldCondition.criterionFieldName, StringUtils.EMPTY);
            }
        }

        @Override
        public Criterion visit(NotIsEmpty notIsEmpty) {
            FieldCondition fieldCondition = notIsEmpty.getField().accept(new CriterionFieldCondition());
            if (fieldCondition.isMany) {
                return Restrictions.isNotEmpty(fieldCondition.criterionFieldName);
            } else {
                return Restrictions.not(Restrictions.eq(fieldCondition.criterionFieldName, StringUtils.EMPTY));
            }
        }

        @Override
        public Criterion visit(NotIsNull notIsNull) {
            FieldCondition fieldCondition = notIsNull.getField().accept(new CriterionFieldCondition());
            if (fieldCondition.isMany) {
                throw new UnsupportedOperationException("Does not support notIsNull operation on collections.");
            }
            return Restrictions.isNotNull(fieldCondition.criterionFieldName);
        }

        @Override
        public Criterion visit(BinaryLogicOperator condition) {
            Predicate predicate = condition.getPredicate();
            Criterion left = condition.getLeft().accept(this);
            Criterion right = condition.getRight().accept(this);

            if (predicate == Predicate.AND) {
                return and(left, right);
            } else if (predicate == Predicate.OR) {
                return or(left, right);
            } else {
                throw new NotImplementedException("No support for predicate '" + predicate + "'");
            }
        }

        @Override
        public Criterion visit(Compare condition) {
            String fieldName = condition.getLeft().accept(FIELD_VISITOR);
            Object compareValue = condition.getRight().accept(VALUE_ADAPTER);

            Predicate predicate = condition.getPredicate();
            FieldCondition fieldCondition = condition.getLeft().accept(criterionFieldCondition);
            if (fieldCondition.isMany) {
                // This is what could be done with Hibernate 4 for searches that includes conditions on collections:
                // criteria = criteria.createCriteria(fieldName);
                throw new UnsupportedOperationException("Cannot search on field '" + fieldName + "' because it is a collection.");
            }
            if (compareValue instanceof Boolean && predicate == Predicate.EQUALS) {
                if (!(Boolean) compareValue) {
                    // Special case for boolean: when looking for 'false' value, consider null values as 'false' too.
                    return or(eq(fieldCondition.criterionFieldName, compareValue), isNull(fieldCondition.criterionFieldName));
                }
            }

            if (predicate == Predicate.EQUALS) {
                return eq(fieldCondition.criterionFieldName, compareValue);
            } else if (predicate == Predicate.CONTAINS) {
                return like(fieldCondition.criterionFieldName, "%" + compareValue + "%"); //$NON-NLS-1$ //$NON-NLS-2$
            } else if (predicate == Predicate.GREATER_THAN) {
                return gt(fieldCondition.criterionFieldName, compareValue);
            } else if (predicate == Predicate.LOWER_THAN) {
                return lt(fieldCondition.criterionFieldName, compareValue);
            } else if (predicate == Predicate.GREATER_THAN_OR_EQUALS) {
                return ge(fieldCondition.criterionFieldName, compareValue);
            } else if (predicate == Predicate.LOWER_THAN_OR_EQUALS) {
                return le(fieldCondition.criterionFieldName, compareValue);
            } else if (predicate == Predicate.STARTS_WITH) {
                return like(fieldCondition.criterionFieldName, compareValue + "%"); //$NON-NLS-1$
            } else {
                throw new NotImplementedException("No support for predicate '" + predicate.getClass() + "'");
            }
        }
    }

    private class CriterionFieldCondition extends VisitorAdapter<FieldCondition> {

        private FieldCondition createInternalCriterion(String fieldName) {
            FieldCondition condition = new FieldCondition();
            condition.criterionFieldName = fieldName;
            condition.isMany = false;
            return condition;
        }

        @Override
        public FieldCondition visit(Revision revision) {
            return createInternalCriterion(Storage.METADATA_REVISION_ID);
        }

        @Override
        public FieldCondition visit(Timestamp timestamp) {
            return createInternalCriterion(Storage.METADATA_TIMESTAMP);
        }

        @Override
        public FieldCondition visit(TaskId taskId) {
            return createInternalCriterion(Storage.METADATA_TASK_ID);
        }

        @Override
        public FieldCondition visit(StagingStatus stagingStatus) {
            return createInternalCriterion(Storage.METADATA_STAGING_STATUS);
        }

        @Override
        public FieldCondition visit(Field field) {
            FieldCondition condition = new FieldCondition();
            condition.isMany = field.getFieldMetadata().isMany();
            // Use line below to allow searches on collection fields (but Hibernate 4 should be used).
            // condition.criterionFieldName = field.getFieldMetadata().isMany() ? "elements" : getFieldName(field, StandardQueryHandler.this.mappingMetadataRepository);
            condition.criterionFieldName = getFieldName(field, StandardQueryHandler.this.mappingMetadataRepository);
            return condition;
        }
    }

}
