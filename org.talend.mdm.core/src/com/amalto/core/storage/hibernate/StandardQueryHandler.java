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
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.sql.JoinFragment;
import org.hibernate.transform.DistinctRootEntityResultTransformer;

import java.util.*;

import static org.hibernate.criterion.Restrictions.*;

class StandardQueryHandler extends AbstractQueryHandler {

    private static final Logger LOGGER = Logger.getLogger(StandardQueryHandler.class);

    private static final StringConstant EMPTY_STRING_CONSTANT = (new StringConstant(StringUtils.EMPTY));

    private final CriterionAdapter CRITERION_VISITOR = new CriterionAdapter();

    private final StandardQueryHandler.CriterionFieldCondition criterionFieldCondition;

    private final Map<FieldMetadata, String> joinFieldsToAlias = new HashMap<FieldMetadata, String>();

    private final TableResolver resolver;

    private Criteria criteria;

    private ProjectionList projectionList;

    private ComplexTypeMetadata mainType;

    private int aliasCount = 0;

    private List<ComplexTypeMetadata> selectedTypes;

    private String currentAliasName;

    private String mainClassName;

    public StandardQueryHandler(Storage storage,
                                MappingRepository mappingMetadataRepository,
                                TableResolver resolver,
                                StorageClassLoader storageClassLoader,
                                Session session,
                                Select select,
                                List<TypedExpression> selectedFields,
                                Set<EndOfResultsCallback> callbacks) {
        super(storage, mappingMetadataRepository, storageClassLoader, session, select, selectedFields, callbacks);
        this.resolver = resolver;
        criterionFieldCondition = new CriterionFieldCondition();
    }

    protected StorageResults createResults(List list, boolean isProjection) {
        CloseableIterator<DataRecord> iterator;
        Iterator listIterator = list.iterator();
        if (isProjection) {
            iterator = new ProjectionIterator(listIterator, selectedFields, callbacks, mappingMetadataRepository);
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
            iterator = new ProjectionIterator(scrollableResults, selectedFields, callbacks, mappingMetadataRepository);
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
        TypeMapping mainTypeMapping = mappingMetadataRepository.getMappingFromUser(mainType);
        ComplexTypeMetadata containingType = join.getLeftField().getFieldMetadata().getContainingType();
        while (containingType instanceof ContainedComplexTypeMetadata) {
            containingType = ((ContainedComplexTypeMetadata) containingType).getContainerType();
        }
        TypeMapping leftTypeMapping = mappingMetadataRepository.getMappingFromUser(containingType);
        List<FieldMetadata> path = MetadataUtils.path(mainTypeMapping.getDatabase(), leftTypeMapping.getDatabase(join.getLeftField().getFieldMetadata()));
        // Empty path means no path then this is an error (all joined entities should be reachable from main type).
        if (path.isEmpty()) {
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
        // Generate all necessary joins to go from main type to join right table.
        generateJoinPath(rightTableName, joinType, path);
        return null;
    }

    private void generateJoinPath(String rightTableName, int joinType, List<FieldMetadata> path) {
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
    }

    @Override
    public StorageResults visit(Alias alias) {
        currentAliasName = alias.getAliasName();
        alias.getTypedExpression().accept(this);
        currentAliasName = null;
        return null;
    }

    @Override
    public StorageResults visit(Type type) {
        if (currentAliasName != null) {
            type.getField().accept(this);
            projectionList.add(new ClassNameProjection(currentAliasName), currentAliasName);
        } else {
            throw new IllegalStateException("Expected an alias for a type expression.");
        }
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
    public StorageResults visit(Timestamp timestamp) {
        String timeStamp = mappingMetadataRepository.getMappingFromUser(mainType).getDatabaseTimestamp();
        if (timeStamp != null) {
            projectionList.add(Projections.property(timeStamp));
        } else {
            EMPTY_STRING_CONSTANT.accept(this);
        }
        return null;
    }

    @Override
    public StorageResults visit(TaskId taskId) {
        String taskIdField = mappingMetadataRepository.getMappingFromUser(mainType).getDatabaseTaskId();
        if (taskIdField != null) {
            projectionList.add(Projections.property(taskIdField));
        } else {
            EMPTY_STRING_CONSTANT.accept(this);
        }
        return null;
    }

    @Override
    public StorageResults visit(StagingStatus stagingStatus) {
        projectionList.add(Projections.property(Storage.METADATA_STAGING_STATUS));
        return null;
    }

    @Override
    public StorageResults visit(StagingError stagingError) {
        projectionList.add(Projections.property(Storage.METADATA_STAGING_ERROR));
        return null;
    }

    @Override
    public StorageResults visit(StagingSource stagingSource) {
        projectionList.add(Projections.property(Storage.METADATA_STAGING_SOURCE));
        return null;
    }

    @Override
    public StorageResults visit(final Field field) {
        final FieldMetadata userFieldMetadata = field.getFieldMetadata();
        ComplexTypeMetadata containingType = field.getFieldMetadata().getContainingType();
        if (!containingType.isInstantiable()) {
            containingType = mainType;
        }
        TypeMapping mapping = mappingMetadataRepository.getMappingFromUser(containingType);
        final FieldMetadata database = mapping.getDatabase(userFieldMetadata);
        final String alias = getAlias(mapping, database);
        database.accept(new DefaultMetadataVisitor<Void>() {
            @Override
            public Void visit(ReferenceFieldMetadata referenceField) {
                // Automatically selects referenced ID in case of FK.
                if (userFieldMetadata instanceof ReferenceFieldMetadata) {
                    referenceField.getReferencedField().accept(this);
                }
                return null;
            }

            @Override
            public Void visit(SimpleTypeFieldMetadata simpleField) {
                if (!simpleField.isMany()) {
                    projectionList.add(Projections.property(alias + '.' + simpleField.getName()));
                } else {
                    projectionList.add(new ManyFieldProjection(simpleField, resolver));
                }
                return null;
            }

            @Override
            public Void visit(EnumerationFieldMetadata enumField) {
                if (!enumField.isMany()) {
                    projectionList.add(Projections.property(alias + '.' + enumField.getName()));
                } else {
                    projectionList.add(new ManyFieldProjection(enumField, resolver));
                }
                return null;
            }
        });
        return null;
    }

    private String getAlias(TypeMapping mapping, FieldMetadata databaseField) {
        ComplexTypeMetadata mainType = mapping.getDatabase();
        String previousAlias = mapping.getUser().getName();
        String alias;
        for (FieldMetadata next : MetadataUtils.path(mainType, databaseField)) {
            if (next instanceof ReferenceFieldMetadata) {
                alias = joinFieldsToAlias.get(next);
                if (alias == null) {
                    alias = "a" + aliasCount++; //$NON-NLS-1$
                    joinFieldsToAlias.put(next, alias);
                    int joinType;
                    // TMDM-4866: Do a left join in case FK is not mandatory.
                    if (next.isMandatory()) {
                        joinType = CriteriaSpecification.INNER_JOIN;
                    } else {
                        joinType = CriteriaSpecification.LEFT_JOIN;
                    }
                    criteria.createAlias(previousAlias + '.' + next.getName(), alias, joinType);
                }
                previousAlias = alias;
            }
        }
        return previousAlias;
    }

    @Override
    public StorageResults visit(Count count) {
        projectionList.add(Projections.rowCount());
        return null;
    }

    @Override
    public StorageResults visit(Select select) {
        selectedTypes = select.getTypes();
        createCriteria(select);
        // Paging
        Paging paging = select.getPaging();
        paging.accept(this);
        return createResults(select);
    }

    protected StorageResults createResults(Select select) {
        Paging paging = select.getPaging();
        int pageSize = paging.getLimit();
        boolean hasPaging = pageSize < Integer.MAX_VALUE;
        // Results
        if (!hasPaging) {
            return createResults(criteria.scroll(ScrollMode.FORWARD_ONLY), select.isProjection());
        } else {
            List list = criteria.list();
            return createResults(list, select.isProjection());
        }
    }

    protected Criteria createCriteria(Select select) {
        List<ComplexTypeMetadata> selectedTypes = select.getTypes();
        if (selectedTypes.isEmpty()) {
            throw new IllegalArgumentException("Select clause is expected to select at least one entity type.");
        }
        mainType = selectedTypes.get(0);
        String mainTypeName = mainType.getName();
        mainClassName = ClassCreator.PACKAGE_PREFIX + mappingMetadataRepository.getMappingFromUser(mainType).getDatabase().getName();
        criteria = session.createCriteria(mainClassName, mainTypeName);
        criteria.setReadOnly(true); // We are reading data, turns on ready only mode.
        // Handle JOIN (if any)
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
        } else {
            // TMDM-5388: Hibernate sometimes returns duplicate results (like for User stored in System storage), this
            // line avoids this situation.
            criteria.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
        }
        // Make projection read only in case code tries to modify it later (see code that handles condition).
        projectionList = ReadOnlyProjectionList.makeReadOnly(projectionList);
        // Handle condition (if there's any condition to handle).
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
        // Order by
        OrderBy orderBy = select.getOrderBy();
        if (orderBy != null) {
            orderBy.accept(this);
        }
        return criteria;
    }

    @Override
    public StorageResults visit(FullText fullText) {
        // Ignore full text queries (if any).
        return null;
    }

    @Override
    public StorageResults visit(FieldFullText fullText) {
        // Ignore full text queries (if any).
        return null;
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
        TypedExpression orderByExpression = orderBy.getField();
        FieldCondition condition = orderByExpression.accept(new CriterionFieldCondition());
        if (orderByExpression instanceof Field) {
            Field field = (Field) orderByExpression;
            FieldMetadata userFieldMetadata = field.getFieldMetadata();
            TypeMapping mapping = mappingMetadataRepository.getMappingFromUser(field.getFieldMetadata().getContainingType());
            FieldMetadata database = mapping.getDatabase(userFieldMetadata);
            String alias = getAlias(mapping, database);
            condition.criterionFieldName = alias + '.' + database.getName();
        }
        if (condition != null) {
            String fieldName = condition.criterionFieldName;
            OrderBy.Direction direction = orderBy.getDirection();
            switch (direction) {
                case ASC:
                    criteria.addOrder(Order.asc(fieldName));
                    break;
                case DESC:
                    criteria.addOrder(Order.desc(fieldName));
                    break;
            }
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
        Criterion criterion = range.accept(CRITERION_VISITOR);
        if (criterion != null) {
            criteria.add(criterion);
        }
        return null;
    }

    private class CriterionAdapter extends VisitorAdapter<Criterion> {

        private final CriterionFieldCondition visitor = new CriterionFieldCondition();

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
            FieldCondition fieldCondition = isa.getExpression().accept(visitor);
            if (fieldCondition == null) {
                return NO_OP_CRITERION;
            }
            if (fieldCondition.criterionFieldName.isEmpty()) {
                // Case #1: doing a simple instance type check on main selected type.
                return Restrictions.eq("class", storageClassLoader.getClassFromType(isa.getType())); //$NON-NLS-1$
            } else {
                // Case #2: doing a instance type check on a field reachable from main selected type.
                // First, need to join with all tables to get to the table that stores the type
                TypeMapping typeMapping = mappingMetadataRepository.getMappingFromUser(mainType);
                ComplexTypeMetadata database = typeMapping.getDatabase();
                FieldMetadata field = database.getField(StringUtils.substringAfter(fieldCondition.criterionFieldName.replace('.', '/'), "/")); //$NON-NLS-1$
                List<FieldMetadata> path = MetadataUtils.path(database, field);
                if (path.isEmpty()) {
                    throw new IllegalStateException("Expected field '" + field.getName() + "' to be reachable from '" + database.getName() + "'.");
                }
                // Generate the joins
                String alias = getAlias(typeMapping, field);
                generateJoinPath(alias, JoinFragment.INNER_JOIN, path);
                // Find the criteria that does the join to the table to check (only way to get the SQL alias for table).
                if (criteria instanceof CriteriaImpl) {
                    Iterator iterator = ((CriteriaImpl) criteria).iterateSubcriteria();
                    Criteria typeCheckCriteria = null;
                    while (iterator.hasNext()) {
                        Criteria subCriteria = (Criteria) iterator.next();
                        if (alias.equals(subCriteria.getAlias())) {
                            typeCheckCriteria = subCriteria;
                            break;
                        }
                    }
                    if (typeCheckCriteria == null) {
                        throw new IllegalStateException("Could not find criteria for type check.");
                    }
                    TypeMapping isaType = mappingMetadataRepository.getMappingFromUser(isa.getType());
                    String name = storageClassLoader.getClassFromType(isaType.getDatabase()).getName();
                    return new FieldTypeCriterion(typeCheckCriteria, name);
                } else {
                    throw new IllegalStateException("Expected a criteria instance of " + CriteriaImpl.class.getName() + ".");
                }
            }
        }

        @Override
        public Criterion visit(IsNull isNull) {
            FieldCondition fieldCondition = isNull.getField().accept(visitor);
            if (fieldCondition == null) {
                return NO_OP_CRITERION;
            }
            if (fieldCondition.isMany) {
                throw new UnsupportedOperationException("Does not support isNull operation on collections.");
            }
            return Restrictions.isNull(fieldCondition.criterionFieldName);
        }

        @Override
        public Criterion visit(IsEmpty isEmpty) {
            FieldCondition fieldCondition = isEmpty.getField().accept(visitor);
            if (fieldCondition == null) {
                return NO_OP_CRITERION;
            }
            if (fieldCondition.isMany) {
                return Restrictions.isEmpty(fieldCondition.criterionFieldName);
            } else {
                return Restrictions.eq(fieldCondition.criterionFieldName, StringUtils.EMPTY);
            }
        }

        @Override
        public Criterion visit(NotIsEmpty notIsEmpty) {
            FieldCondition fieldCondition = notIsEmpty.getField().accept(visitor);
            if (fieldCondition == null) {
                return NO_OP_CRITERION;
            }
            if (fieldCondition.isMany) {
                return Restrictions.isNotEmpty(fieldCondition.criterionFieldName);
            } else {
                return Restrictions.not(Restrictions.eq(fieldCondition.criterionFieldName, StringUtils.EMPTY));
            }
        }

        @Override
        public Criterion visit(NotIsNull notIsNull) {
            FieldCondition fieldCondition = notIsNull.getField().accept(visitor);
            if (fieldCondition == null) {
                return NO_OP_CRITERION;
            }
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
        public Criterion visit(Range range) {
            FieldCondition condition = range.getExpression().accept(new CriterionFieldCondition());
            TypedExpression expression = range.getExpression();
            Object start;
            Object end;
            String startValue = String.valueOf(range.getStart().accept(VALUE_ADAPTER));
            String endValue = String.valueOf(range.getEnd().accept(VALUE_ADAPTER));
            if (expression instanceof Field) {
                TypeMapping mapping = mappingMetadataRepository.getMappingFromUser(mainType);
                Field field = (Field) expression;
                FieldMetadata fieldMetadata = field.getFieldMetadata();
                FieldMetadata databaseField = mapping.getDatabase(fieldMetadata);
                start = MetadataUtils.convert(startValue, databaseField);
                end = MetadataUtils.convert(endValue, databaseField);
            } else {
                start = MetadataUtils.convert(startValue, expression.getTypeName());
                end = MetadataUtils.convert(endValue, expression.getTypeName());
            }
            if (condition != null) {
                return Restrictions.between(condition.criterionFieldName, start, end);
            } else {
                return null;
            }
        }

        @Override
        public Criterion visit(Compare condition) {
            FieldCondition leftFieldCondition = condition.getLeft().accept(criterionFieldCondition);
            FieldCondition rightFieldCondition = condition.getRight().accept(criterionFieldCondition);
            if (!leftFieldCondition.isProperty) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Query on '" + leftFieldCondition + "' is not a user set property. Ignore this condition.");
                }
                return NO_OP_CRITERION;
            }
            TypeMapping mapping = mappingMetadataRepository.getMappingFromUser(mainType);
            if (condition.getLeft() instanceof Field) {
                Field leftField = (Field) condition.getLeft();
                FieldMetadata fieldMetadata = leftField.getFieldMetadata();
                String alias = mainType.getName();
                FieldMetadata left = mapping.getDatabase(fieldMetadata);
                // TODO Ugly code path to fix once test coverage is ok.
                if (!mainType.equals(fieldMetadata.getContainingType()) || fieldMetadata instanceof ReferenceFieldMetadata) {
                    (new Field(fieldMetadata)).accept(StandardQueryHandler.this);
                    if (left == null) {
                        mapping = mappingMetadataRepository.getMappingFromUser(fieldMetadata.getContainingType());
                        left = mapping.getDatabase(fieldMetadata);
                    }
                    alias = getAlias(mapping, left);
                    if (!fieldMetadata.isMany()) {
                        if (fieldMetadata instanceof ReferenceFieldMetadata) {
                            // ignored CompoundFieldMetadata
                            if (!(((ReferenceFieldMetadata) left).getReferencedField() instanceof CompoundFieldMetadata)) {
                                leftFieldCondition.criterionFieldName = alias + '.'
                                        + ((ReferenceFieldMetadata) left).getReferencedField().getName();
                            }
                        } else {
                            leftFieldCondition.criterionFieldName = alias + '.' + left.getName();
                        }
                    }
                }
                if (leftFieldCondition.isMany || rightFieldCondition.isMany) {
                    // This is what could be done with Hibernate 4 for searches that includes conditions on collections:
                    // criteria = criteria.createCriteria(leftFieldCondition.criterionFieldName);
                    // This is what is done on Hibernate 3.5.6
                    if (criteria instanceof CriteriaImpl) {
                        Iterator iterator = ((CriteriaImpl) criteria).iterateSubcriteria();
                        Criteria typeCheckCriteria = criteria;
                        while (iterator.hasNext()) {
                            Criteria subCriteria = (Criteria) iterator.next();
                            if (alias.equals(subCriteria.getAlias())) {
                                typeCheckCriteria = subCriteria;
                                break;
                            }
                        }
                        return new ManyFieldCriterion(typeCheckCriteria, resolver, left, condition.getRight().accept(VALUE_ADAPTER));
                    } else {
                        throw new IllegalStateException("Expected a criteria instance of " + CriteriaImpl.class.getName() + ".");
                    }
                }
            }
            if (!rightFieldCondition.isProperty) {  // "Standard" comparison between a field and a constant value.
                Object compareValue = condition.getRight().accept(VALUE_ADAPTER);
                if (condition.getLeft() instanceof Field) {
                    Field leftField = (Field) condition.getLeft();
                    FieldMetadata fieldMetadata = leftField.getFieldMetadata();
                    FieldMetadata left = mapping.getDatabase(fieldMetadata);
                    if (!left.getType().equals(fieldMetadata.getType())) {
                        compareValue = MetadataUtils.convert(String.valueOf(compareValue), left);
                    }
                }
                Predicate predicate = condition.getPredicate();
                if (compareValue instanceof Boolean && predicate == Predicate.EQUALS) {
                    if (!(Boolean) compareValue) {
                        // Special case for boolean: when looking for 'false' value, consider null values as 'false' too.
                        return or(eq(leftFieldCondition.criterionFieldName, compareValue), isNull(leftFieldCondition.criterionFieldName));
                    }
                }
                if (predicate == Predicate.EQUALS) {
                    if (compareValue instanceof Object[]) {
                        Field leftField = (Field) condition.getLeft();
                        FieldMetadata fieldMetadata = leftField.getFieldMetadata();
                        FieldMetadata left = mapping.getDatabase(fieldMetadata);
                        FieldMetadata referencedField = ((ReferenceFieldMetadata) left).getReferencedField();
                        if (!(referencedField instanceof CompoundFieldMetadata)) {
                            throw new IllegalArgumentException("Expected field '" + referencedField + "' to be a composite key.");
                        }
                        String alias = getAlias(mapping, left);
                        FieldMetadata[] fields = ((CompoundFieldMetadata) referencedField).getFields();
                        Object[] keyValues = (Object[]) compareValue;
                        Criterion[] keyValueCriteria = new Criterion[keyValues.length];
                        int i = 0;
                        for (FieldMetadata keyField : fields) {
                            Object keyValue = MetadataUtils.convert(String.valueOf(keyValues[i]), keyField);
                            keyValueCriteria[i] = eq(alias + "." + keyField.getName(), keyValue); //$NON-NLS-1$
                            i++;
                        }
                        return makeAnd(keyValueCriteria);
                    } else {
                        return eq(leftFieldCondition.criterionFieldName, compareValue);
                    }
                } else if (predicate == Predicate.CONTAINS) {
                    String value = String.valueOf(compareValue);
                    if (!value.isEmpty()) {
                        if (value.charAt(0) != '%') {
                            value = '%' + value;
                        }
                        if (value.charAt(value.length() - 1) != '%') {
                            value += '%';
                        }
                    } else {
                        value = "%"; //$NON-NLS-1$
                    }
                    return like(leftFieldCondition.criterionFieldName, value);
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

    public static Criteria findCriteria(Criteria mainCriteria, String alias) {
        if (alias.equals(mainCriteria.getAlias())) {
            return mainCriteria;
        }
        if (mainCriteria instanceof CriteriaImpl) {
            Criteria foundSubCriteria = null;
            Iterator iterator = ((CriteriaImpl) mainCriteria).iterateSubcriteria();
            while (iterator.hasNext()) {
                Criteria subCriteria = (Criteria) iterator.next();
                if (alias.equals(subCriteria.getAlias())) {
                    foundSubCriteria = subCriteria;
                    break;
                }
            }
            if (foundSubCriteria == null) {
                throw new IllegalStateException("Could not find criteria for type check.");
            }
            return foundSubCriteria;
        } else {
            throw new IllegalStateException("Expected a criteria instance of " + CriteriaImpl.class.getName() + ".");
        }
    }

    private static Criterion makeAnd(Criterion... criterions) {
        if (criterions.length == 1) {
            return criterions[0];
        }
        Criterion current = NO_OP_CRITERION;
        for (Criterion cri : criterions) {
            current = Restrictions.and(current, cri);
        }
        return current;
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
        public FieldCondition visit(Timestamp timestamp) {
            String databaseTimestamp = mappingMetadataRepository.getMappingFromUser(mainType).getDatabaseTimestamp();
            if (databaseTimestamp != null) {
                return createInternalCondition(databaseTimestamp);
            } else {
                return null;
            }
        }

        @Override
        public FieldCondition visit(TaskId taskId) {
            String taskIdField = mappingMetadataRepository.getMappingFromUser(mainType).getDatabaseTaskId();
            if (taskIdField != null) {
                return createInternalCondition(Storage.METADATA_TASK_ID);
            } else {
                return null;
            }
        }

        @Override
        public FieldCondition visit(StagingStatus stagingStatus) {
            return createInternalCondition(Storage.METADATA_STAGING_STATUS);
        }

        @Override
        public FieldCondition visit(StagingError stagingError) {
            return createInternalCondition(Storage.METADATA_STAGING_ERROR);
        }

        @Override
        public FieldCondition visit(StagingSource stagingSource) {
            return createInternalCondition(Storage.METADATA_STAGING_SOURCE);
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

        @Override
        public FieldCondition visit(ComplexTypeExpression expression) {
            FieldCondition fieldCondition = new FieldCondition();
            fieldCondition.criterionFieldName = StringUtils.EMPTY;
            fieldCondition.isMany = false;
            fieldCondition.isProperty = true;
            return fieldCondition;
        }

        @Override
        public FieldCondition visit(Type type) {
            FieldCondition fieldCondition = new FieldCondition();
            Field field = type.getField();
            FieldMetadata fieldMetadata = field.getFieldMetadata();
            TypeMapping mapping = mappingMetadataRepository.getMappingFromUser(fieldMetadata.getContainingType());
            fieldCondition.criterionFieldName = getAlias(mapping, mapping.getDatabase(fieldMetadata)) + ".class"; //$NON-NLS-1$
            fieldCondition.isMany = false;
            fieldCondition.isProperty = true;
            return fieldCondition;
        }
    }
}
