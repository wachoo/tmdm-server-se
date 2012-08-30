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

import com.amalto.core.metadata.*;
import com.amalto.xmlserver.interfaces.*;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

import static com.amalto.core.query.user.UserQueryBuilder.*;

public class UserQueryHelper {

    public static final NoOpCondition NO_OP_CONDITION = new NoOpCondition();

    public static final String STAGING_STATUS_FIELD = "$staging_status$";

    private static final Logger LOGGER = Logger.getLogger(UserQueryHelper.class);

    private UserQueryHelper() {
    }

    public static Condition buildCondition(UserQueryBuilder queryBuilder, IWhereItem whereItem, MetadataRepository repository) {
        if (whereItem == null) {
            return NO_OP_CONDITION;
        }

        if (whereItem instanceof WhereAnd || whereItem instanceof WhereOr) {
            List<IWhereItem> whereItems = ((WhereLogicOperator) whereItem).getItems();
            Condition current = NO_OP_CONDITION;
            for (IWhereItem item : whereItems) {
                if (item instanceof WhereAnd) {
                    current = and(current, buildCondition(queryBuilder, item, repository));
                } else if (item instanceof WhereOr) {
                    current = or(current, buildCondition(queryBuilder, item, repository));
                } else {
                    return buildCondition(queryBuilder, item, repository);
                }
            }
            return current;

        } else if (whereItem instanceof WhereCondition) {
            WhereCondition whereCondition = (WhereCondition) whereItem;
            String operator = whereCondition.getOperator();
            String value = whereCondition.getRightValueOrPath();
            // Special case for full text: left path is actually the keyword for full text search.
            if (WhereCondition.FULLTEXTSEARCH.equals(operator)) {
                return fullText(value);
            }
            TypedExpression field;
            String leftPath = whereCondition.getLeftPath();
            String typeName = leftPath.substring(0, leftPath.indexOf('/')); //$NON-NLS-1$
            ComplexTypeMetadata type = repository.getComplexType(typeName);
            String leftFieldName = StringUtils.substringAfter(leftPath, "/"); //$NON-NLS-1$
            if ("../../t".equals(leftPath)) {
                field = timestamp();
            } else if ("../../i".equals(leftPath)) {
                List<FieldMetadata> keyFields = type.getKeyFields();
                if (keyFields.isEmpty()) {
                    throw new IllegalArgumentException("Can not query id on type '" + typeName + "' because type has no id field.");
                }
                if (keyFields.size() > 1) {
                    throw new NotImplementedException("No support for query on composite key.");
                }
                field = new Field(keyFields.get(0));
            } else if (STAGING_STATUS_FIELD.equals(leftFieldName)) {
                field = UserStagingQueryBuilder.status();
            } else {
                field = getField(repository, typeName, leftFieldName);
            }

            if (!whereCondition.isRightValueXPath()) {
                String fieldTypeName = field.getTypeName();
                if (!MetadataUtils.isValueAssignable(value, fieldTypeName)) {
                    Logger.getLogger(UserQueryHelper.class).warn("Skip '" + leftFieldName + "' because it can't accept value '" + value + "'");
                    return NO_OP_CONDITION;
                }
                if (WhereCondition.CONTAINS.equals(operator) || WhereCondition.STRICTCONTAINS.equals(operator)) {
                    return contains(field, value);
                } else if (WhereCondition.EQUALS.equals(operator)) {
                    return eq(field, value);
                } else if (WhereCondition.GREATER_THAN.equals(operator)) {
                    return gt(field, value);
                } else if (WhereCondition.GREATER_THAN_OR_EQUAL.equals(operator)) {
                    return gte(field, value);
                } else if (WhereCondition.LOWER_THAN.equals(operator)) {
                    return lt(field, value);
                } else if (WhereCondition.LOWER_THAN_OR_EQUAL.equals(operator)) {
                    return lte(field, value);
                } else if (WhereCondition.NOT_EQUALS.equals(operator)) {
                    return neq(field, value);
                } else if (WhereCondition.STARTSWITH.equals(operator)) {
                    return startsWith(field, value);
                } else if (WhereCondition.EMPTY_NULL.equals(operator)) {
                    return emptyOrNull(field);
                } else {
                    throw new NotImplementedException("'" + operator + "' support not implemented.");
                }
            } else {
                // Right value is another field name
                String rightPath = whereCondition.getRightValueOrPath();
                rightPath = StringUtils.substringAfter(rightPath, "/"); //$NON-NLS-1$;
                FieldMetadata leftField = type.getField(leftFieldName);
                FieldMetadata rightField = type.getField(rightPath);
                if (WhereCondition.LOWER_THAN_OR_EQUAL.equals(operator)) {
                    return lte(leftField, rightField);
                } else if (WhereCondition.JOINS.equals(operator)) {
                    if (field instanceof Field) {
                        FieldMetadata fieldMetadata = ((Field) field).getFieldMetadata();
                        if (!(fieldMetadata instanceof ReferenceFieldMetadata)) {
                            throw new IllegalArgumentException("Field '" + leftFieldName + "' is not a FK field.");
                        }
                        queryBuilder.join(field, ((ReferenceFieldMetadata) fieldMetadata).getReferencedField());
                    } else {
                        throw new IllegalArgumentException("Can not perform not on '" + leftFieldName + "' because it is not a field.");
                    }
                    return NO_OP_CONDITION;
                } else {
                    throw new NotImplementedException("'" + operator + "' support not implemented for field to field comparison.");
                }
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
