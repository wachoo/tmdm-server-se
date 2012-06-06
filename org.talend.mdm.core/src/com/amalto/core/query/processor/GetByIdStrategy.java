/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query.processor;

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.query.QueryExecutor;
import com.amalto.core.query.user.*;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
class GetByIdStrategy implements QueryProcessor {
    public QueryExecutor getExecution(Select select) {
        Set<String> idList = select.accept(new DocumentIdVisitor());
        return new GetByIdQueryExecutor(idList);
    }

    private static class DocumentIdVisitor implements Visitor<Set<String>> {
        final Set<String> idList = new HashSet<String>();

        public Set<String> visit(Select select) {
            select.getCondition().accept(this);
            return idList;
        }

        public Set<String> visit(Condition condition) {
            return idList;
        }

        public Set<String> visit(Compare condition) {
            condition.getLeft().accept(this);
            condition.getRight().accept(this);
            return idList;
        }

        public Set<String> visit(BinaryLogicOperator condition) {
            condition.getLeft().accept(this);
            condition.getRight().accept(this);
            return idList;
        }

        public Set<String> visit(UnaryLogicOperator condition) {
            return idList;
        }

        public Set<String> visit(Range range) {
            return idList;
        }

        public Set<String> visit(Timestamp timestamp) {
            return idList;
        }

        public Set<String> visit(Revision revision) {
            return idList;
        }

        public Set<String> visit(TaskId taskId) {
            return idList;
        }

        public Set<String> visit(StagingStatus stagingStatus) {
            return idList;
        }

        public Set<String> visit(Join join) {
            return idList;
        }

        public Set<String> visit(Field field) {
            FieldMetadata key = field.getFieldMetadata();
            if (!key.isKey()) {
                throw new IllegalArgumentException("Field '" + key.getName() + "' is not a key");
            }

            return idList;
        }

        public Set<String> visit(Alias alias) {
            return idList;
        }

        public Set<String> visit(Id id) {
            return idList;
        }

        public Set<String> visit(StringConstant constant) {
            idList.add(constant.getValue());
            return idList;
        }

        public Set<String> visit(IntegerConstant constant) {
            return idList;
        }

        public Set<String> visit(DateConstant constant) {
            return idList;
        }

        public Set<String> visit(DateTimeConstant constant) {
            return idList;
        }

        public Set<String> visit(BooleanConstant constant) {
            return idList;
        }

        public Set<String> visit(BigDecimalConstant constant) {
            return idList;
        }

        public Set<String> visit(TimeConstant constant) {
            return idList;
        }

        public Set<String> visit(ShortConstant constant) {
            return idList;
        }

        public Set<String> visit(ByteConstant constant) {
            return idList;
        }

        public Set<String> visit(LongConstant constant) {
            return idList;
        }

        public Set<String> visit(DoubleConstant constant) {
            return idList;
        }

        public Set<String> visit(FloatConstant constant) {
            return idList;
        }

        public Set<String> visit(Predicate.And and) {
            return idList;
        }

        public Set<String> visit(Predicate.Or or) {
            return idList;
        }

        public Set<String> visit(Predicate.Equals equals) {
            return idList;
        }

        public Set<String> visit(Predicate.Contains contains) {
            return idList;
        }

        public Set<String> visit(IsEmpty isEmpty) {
            return idList;
        }

        public Set<String> visit(NotIsEmpty notIsEmpty) {
            return idList;
        }

        public Set<String> visit(IsNull isNull) {
            return idList;
        }

        public Set<String> visit(NotIsNull notIsNull) {
            return idList;
        }

        public Set<String> visit(OrderBy orderBy) {
            return idList;
        }

        public Set<String> visit(Paging paging) {
            return idList;
        }

        public Set<String> visit(Count count) {
            return idList;
        }

        public Set<String> visit(Predicate.GreaterThan greaterThan) {
            return idList;
        }

        public Set<String> visit(Predicate.LowerThan lowerThan) {
            return idList;
        }

        public Set<String> visit(FullText fullText) {
            return idList;
        }

        public Set<String> visit(Expression expression) {
            return idList;
        }

        public Set<String> visit(Predicate predicate) {
            return idList;
        }
    }

    private static class GetByIdQueryExecutor implements QueryExecutor {
        private final Set<String> idList;

        public GetByIdQueryExecutor(Set<String> idList) {
            this.idList = idList;
        }

        public int getResultsCount() {
            return idList.size();
        }

        public int getCount() {
            return idList.size();
        }

        public Iterable<String> getResults() {
            return idList;
        }
    }
}
