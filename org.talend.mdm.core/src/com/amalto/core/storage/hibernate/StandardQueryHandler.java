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
import com.amalto.core.query.user.Expression;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinFragment;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

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

    private List<ComplexTypeMetadata> selectedTypes;

    private String currentAliasName;

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
        currentAliasName = alias.getAliasName();
        alias.getTypedExpression().accept(this);
        currentAliasName = null;
        return null;
    }

    @Override
    public StorageResults visit(StringConstant constant) {
        Projection p = new ConstantStringProjection(currentAliasName, constant.getValue());
        if (currentAliasName != null) {
            projectionList.add(p, currentAliasName);
        } else {
            throw new IllegalStateException("Expected an alias for a constant expression.");
        }
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
        if (field.getFieldMetadata().isMany()) {
            throw new NotImplementedException("Support for collections in projections is not supported.");
        }
        ComplexTypeMetadata containingType = field.getFieldMetadata().getContainingType();
        if (!selectedTypes.contains(containingType)) {
            TypeMapping mapping = mappingMetadataRepository.getMapping(mainType);
            FieldMetadata database = mapping.getDatabase(field.getFieldMetadata());
            String alias = getAlias(mapping, database);
            if (database instanceof ReferenceFieldMetadata) { // Automatically selects referenced ID in case of FK.
                projectionList.add(Projections.property(alias + '.' + ((ReferenceFieldMetadata) database).getReferencedField().getName()));
            } else {
                projectionList.add(Projections.property(alias + '.' + database.getName()));
            }
        } else {
            if (field.getFieldMetadata() instanceof ReferenceFieldMetadata) {
                ReferenceFieldMetadata fieldMetadata = (ReferenceFieldMetadata) field.getFieldMetadata();
                if (!selectedTypes.contains(fieldMetadata.getReferencedType())) {
                    selectedTypes.add(fieldMetadata.getReferencedType());
                    Field rightField = new Field(fieldMetadata.getReferencedField());
                    Join join = new Join(new Field(fieldMetadata), rightField, JoinType.INNER);
                    join.accept(this);
                    rightField.accept(this);
                } else {
                    projectionList.add(Projections.property(getFieldName(field, mappingMetadataRepository)));
                }
            } else {
                projectionList.add(Projections.property(getFieldName(field, mappingMetadataRepository)));
            }
        }
        return null;
    }

    private String getAlias(TypeMapping mapping, FieldMetadata databaseField) {
        ComplexTypeMetadata mainType = mapping.getDatabase();
        String previousAlias = mainType.getName();
        String alias;
        for (FieldMetadata next : MetadataUtils.path(mainType, databaseField)) {
            if (next instanceof ReferenceFieldMetadata) {
                alias = joinFieldsToAlias.get(next);
                if (alias == null) {
                    alias = "a" + aliasCount++;
                    joinFieldsToAlias.put(next, alias); //$NON-NLS-1$
                    criteria.createAlias(previousAlias + '.' + next.getName(), alias, CriteriaSpecification.INNER_JOIN);
                }
                previousAlias = alias;
            }
        }
        return previousAlias;
    }

    @Override
    public StorageResults visit(Count count) {
        // Do a count on key field (first key field in case of composite key but this should not matter).
        if (mainType.getKeyFields().isEmpty()) {
            throw new IllegalArgumentException("Type '" + mainType.getName() + "' does not own a key (count is based on key).");
        }
        Field keyField = new Field(mainType.getKeyFields().get(0));
        projectionList.add(Projections.count(getFieldName(keyField, mappingMetadataRepository)));
        return null;
    }

    @Override
    public StorageResults visit(Select select) {
        selectedTypes = select.getTypes();
        if (selectedTypes.isEmpty()) {
            throw new IllegalArgumentException("Select clause is expected to select at least one entity type.");
        }
        mainType = selectedTypes.get(0);
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
                public Boolean visit(Isa isa) {
                    return true;
                }

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
        if (paging.getLimit() < Integer.MAX_VALUE) {
            criteria.setFirstResult(paging.getStart());
            criteria.setFetchSize(JDBC_FETCH_SIZE);
            criteria.setMaxResults(paging.getLimit());
        }
        return null;
    }

    @Override
    public StorageResults visit(OrderBy orderBy) {
        Field field = orderBy.getField();

        String fieldName;
        ComplexTypeMetadata containingType = field.getFieldMetadata().getContainingType();
        if (!selectedTypes.contains(containingType)) {
            TypeMapping mapping = mappingMetadataRepository.getMapping(selectedTypes.get(0));
            FieldMetadata database = mapping.getDatabase(field.getFieldMetadata());
            String alias = getAlias(mapping, database);
            fieldName = alias + '.' + database.getName();
        } else {
            fieldName = getFieldName(field, mappingMetadataRepository);
        }

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
    public StorageResults visit(Isa isa) {
        criteria.add(isa.accept(CRITERION_VISITOR));
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
        public Criterion visit(Isa isa) {
            FieldCondition fieldCondition = isa.getExpression().accept(new CriterionFieldCondition());
            String classProperty = fieldCondition.criterionFieldName.isEmpty() ? "class" : StringUtils.substringBeforeLast(fieldCondition.criterionFieldName, ".") + ".class";
            return Restrictions.eq(classProperty, storageClassLoader.getClassFromType(isa.getType()));
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
            FieldCondition leftFieldCondition = condition.getLeft().accept(criterionFieldCondition);
            FieldCondition rightFieldCondition = condition.getRight().accept(criterionFieldCondition);
            if (!leftFieldCondition.isProperty) {
                throw new IllegalArgumentException("Expect left part of condition to be a field.");
            }
            if (leftFieldCondition.isMany || rightFieldCondition.isMany) {
                // This is what could be done with Hibernate 4 for searches that includes conditions on collections:
                // criteria = criteria.createCriteria(fieldName);
                throw new UnsupportedOperationException("Cannot search on field '" + leftFieldCondition.criterionFieldName + "' because it is a collection.");
            }
            if (!rightFieldCondition.isProperty) {  // "Standard" comparison between a field and a constant value.
                Object compareValue = condition.getRight().accept(VALUE_ADAPTER);
                Predicate predicate = condition.getPredicate();
                if (compareValue instanceof Boolean && predicate == Predicate.EQUALS) {
                    if (!(Boolean) compareValue) {
                        // Special case for boolean: when looking for 'false' value, consider null values as 'false' too.
                        return or(eq(leftFieldCondition.criterionFieldName, compareValue), isNull(leftFieldCondition.criterionFieldName));
                    }
                }
                if (predicate == Predicate.EQUALS) {
                    return eq(leftFieldCondition.criterionFieldName, compareValue);
                } else if (predicate == Predicate.CONTAINS) {
                    return like(leftFieldCondition.criterionFieldName, "%" + compareValue + "%"); //$NON-NLS-1$ //$NON-NLS-2$
                } else if (predicate == Predicate.GREATER_THAN) {
                    return gt(leftFieldCondition.criterionFieldName, compareValue);
                } else if (predicate == Predicate.LOWER_THAN) {
                    return lt(leftFieldCondition.criterionFieldName, compareValue);
                } else if (predicate == Predicate.GREATER_THAN_OR_EQUALS) {
                    return ge(leftFieldCondition.criterionFieldName, compareValue);
                } else if (predicate == Predicate.LOWER_THAN_OR_EQUALS) {
                    return le(leftFieldCondition.criterionFieldName, compareValue);
                } else if (predicate == Predicate.STARTS_WITH) {
                    return like(leftFieldCondition.criterionFieldName, compareValue + "%"); //$NON-NLS-1$
                } else {
                    throw new NotImplementedException("No support for predicate '" + predicate.getClass() + "'");
                }
            } else { // Since we expect left part to be a field, this 'else' means we're comparing 2 fields
                Predicate predicate = condition.getPredicate();
                if (predicate == Predicate.EQUALS) {
                    return Restrictions.eqProperty(leftFieldCondition.criterionFieldName, rightFieldCondition.criterionFieldName);
                } else if (predicate == Predicate.GREATER_THAN) {
                    return Restrictions.gtProperty(leftFieldCondition.criterionFieldName, rightFieldCondition.criterionFieldName);
                } else if (predicate == Predicate.LOWER_THAN) {
                    return Restrictions.ltProperty(leftFieldCondition.criterionFieldName, rightFieldCondition.criterionFieldName);
                } else if (predicate == Predicate.GREATER_THAN_OR_EQUALS) {
                    // No GTE for properties, do it "manually"
                    return or(Restrictions.gtProperty(leftFieldCondition.criterionFieldName, rightFieldCondition.criterionFieldName),
                            Restrictions.eqProperty(leftFieldCondition.criterionFieldName, rightFieldCondition.criterionFieldName));
                } else if (predicate == Predicate.LOWER_THAN_OR_EQUALS) {
                    // No LTE for properties, do it "manually"
                    return or(Restrictions.ltProperty(leftFieldCondition.criterionFieldName, rightFieldCondition.criterionFieldName),
                            Restrictions.eqProperty(leftFieldCondition.criterionFieldName, rightFieldCondition.criterionFieldName));
                } else {
                    throw new NotImplementedException("No support for predicate '" + predicate.getClass() + "'");
                }
            }
        }
    }

    private class CriterionFieldCondition extends VisitorAdapter<FieldCondition> {

        private FieldCondition createInternalCondition(String fieldName) {
            FieldCondition condition = new FieldCondition();
            condition.criterionFieldName = fieldName;
            condition.isMany = false;
            condition.isProperty = true;
            return condition;
        }

        private FieldCondition createConstantCondition() {
            FieldCondition condition = new FieldCondition();
            condition.isProperty = false;
            condition.isMany = false;
            condition.criterionFieldName = StringUtils.EMPTY;
            return condition;
        }

        @Override
        public FieldCondition visit(Revision revision) {
            return createInternalCondition(Storage.METADATA_REVISION_ID);
        }

        @Override
        public FieldCondition visit(Timestamp timestamp) {
            return createInternalCondition(Storage.METADATA_TIMESTAMP);
        }

        @Override
        public FieldCondition visit(TaskId taskId) {
            return createInternalCondition(Storage.METADATA_TASK_ID);
        }

        @Override
        public FieldCondition visit(StagingStatus stagingStatus) {
            return createInternalCondition(Storage.METADATA_STAGING_STATUS);
        }

        @Override
        public FieldCondition visit(Expression expression) {
            if (expression instanceof ComplexTypeExpression) {
                return createConstantCondition();
            } else {
                return super.visit(expression);
            }
        }

        @Override
        public FieldCondition visit(Alias alias) {
            return alias.getTypedExpression().accept(this);
        }

        @Override
        public FieldCondition visit(Field field) {
            FieldCondition condition = new FieldCondition();
            condition.isMany = field.getFieldMetadata().isMany();
            // Use line below to allow searches on collection fields (but Hibernate 4 should be used).
            // condition.criterionFieldName = field.getFieldMetadata().isMany() ? "elements" : getFieldName(field, StandardQueryHandler.this.mappingMetadataRepository);
            condition.criterionFieldName = getFieldName(field, StandardQueryHandler.this.mappingMetadataRepository);
            condition.isProperty = true;
            return condition;
        }

        @Override
        public FieldCondition visit(Id id) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(StringConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(IntegerConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(DateConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(DateTimeConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(BooleanConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(BigDecimalConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(TimeConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(ShortConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(ByteConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(LongConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(DoubleConstant constant) {
            return createConstantCondition();
        }

        @Override
        public FieldCondition visit(FloatConstant constant) {
            return createConstantCondition();
        }
    }

    private class ConstantStringProjection extends SimpleProjection {

        private final String aliasName;

        private final String constantValue;

        public ConstantStringProjection(String aliasName, String constantValue) {
            this.aliasName = aliasName;
            this.constantValue = constantValue;
        }

        @Override
        public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
            return "CONCAT('" + constantValue + "', '') as y" + position + "_"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        @Override
        public String[] getAliases() {
            return new String[] {aliasName};
        }

        @Override
        public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
            return new Type[] {new StringType()};
        }
    }
}
