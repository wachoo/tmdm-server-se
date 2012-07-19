// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.query.user;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereOr;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class UserQueryHelper {

    public static final NoOpCondition NO_OP_CONDITION = new NoOpCondition();

    private UserQueryHelper() {
    }

    public static Condition buildCondition(UserQueryBuilder queryBuilder, IWhereItem whereItem, MetadataRepository repository) {
        if (whereItem == null) {
            return NO_OP_CONDITION;
        }

        if (whereItem instanceof WhereAnd) {
            List<IWhereItem> whereItems = ((WhereAnd) whereItem).getItems();
            Condition currentAnd = null;
            for (IWhereItem item : whereItems) {
                if (currentAnd != null) {
                    currentAnd = UserQueryBuilder.and(currentAnd, buildCondition(queryBuilder, item, repository));
                } else {
                    currentAnd = buildCondition(queryBuilder, item, repository);
                }
            }
            return currentAnd;
        } else if (whereItem instanceof WhereOr) {
            List<IWhereItem> whereItems = ((WhereOr) whereItem).getItems();
            Condition currentOr = null;
            for (IWhereItem item : whereItems) {
                if (currentOr != null) {
                    UserQueryBuilder.or(currentOr, buildCondition(queryBuilder, item, repository));
                } else {
                    currentOr = buildCondition(queryBuilder, item, repository);
                }
            }
            return currentOr;
        } else if (whereItem instanceof WhereCondition) {
            // TODO Generate where items for inter-field conditions.
            // TODO Support request on Id ("../../i")
            WhereCondition whereCondition = (WhereCondition) whereItem;
            String operator = whereCondition.getOperator();
            String value = whereCondition.getRightValueOrPath();
            // Special case for full text: left path is actually the keyword for full text search.
            if (WhereCondition.FULLTEXTSEARCH.equals(operator)) {
                return UserQueryBuilder.fullText(value);
            }
            TypedExpression field;
            String leftPath = whereCondition.getLeftPath();
            if ("../../t".equals(leftPath)) {
                field = UserQueryBuilder.timestamp();
            } else {
                String typeName = leftPath.substring(0, leftPath.indexOf('/')); //$NON-NLS-1$
                String fieldName = StringUtils.substringAfter(leftPath, "/"); //$NON-NLS-1$
                field = getField(repository, typeName, fieldName);
            }
            if (WhereCondition.CONTAINS.equals(operator)
                    || WhereCondition.STRICTCONTAINS.equals(operator)) {
                return UserQueryBuilder.contains(field, value);
            } else if (WhereCondition.EQUALS.equals(operator)) {
                return UserQueryBuilder.eq(field, value);
            } else if (WhereCondition.GREATER_THAN.equals(operator)) {
                return UserQueryBuilder.gt(field, value);
            } else if (WhereCondition.GREATER_THAN_OR_EQUAL.equals(operator)) {
                return UserQueryBuilder.gte(field, value);
            } else if (WhereCondition.LOWER_THAN.equals(operator)) {
                return UserQueryBuilder.lt(field, value);
            } else if (WhereCondition.LOWER_THAN_OR_EQUAL.equals(operator)) {
                return UserQueryBuilder.lte(field, value);
            } else if (WhereCondition.NOT_EQUALS.equals(operator)) {
                return UserQueryBuilder.neq(field, value);
            } else if (WhereCondition.STARTSWITH.equals(operator)) {
                return UserQueryBuilder.startsWith(field, value);
            } else if (WhereCondition.JOINS.equals(operator)) {
                queryBuilder.join(field, ((ReferenceFieldMetadata) field).getReferencedField());
                return NO_OP_CONDITION;
            } else if (WhereCondition.EMPTY_NULL.equals(operator)) {
                return UserQueryBuilder.emptyOrNull(field);
            } else {
                throw new NotImplementedException("'" + operator + "' support not implemented.");
            }
        } else {
            throw new NotImplementedException("No support for where item of type " + whereItem.getClass().getName());
        }
    }

    private static TypedExpression getField(MetadataRepository repository, String typeName, String fieldName) {
        ComplexTypeMetadata complexType = repository.getComplexType(typeName);
        if (complexType == null) {
            throw new IllegalArgumentException("Type '" + typeName + "' does not exist.");
        }
        FieldMetadata field = complexType.getField(fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Field '" + fieldName + "' does not exist in type '" + typeName + "'.");
        }
        return new Field(field);
    }

    private static class NoOpCondition extends Condition {
        public Expression normalize() {
            return this;
        }

        public <T> T accept(Visitor<T> visitor) {
            return visitor.visit(this);
        }
    }
}
