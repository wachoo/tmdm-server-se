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
                if (whereItem instanceof WhereAnd) {
                    current = and(current, buildCondition(queryBuilder, item, repository));
                } else {
                    current = or(current, buildCondition(queryBuilder, item, repository));
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
            String leftPath = whereCondition.getLeftPath();
            String typeName = leftPath.substring(0, leftPath.indexOf('/')); //$NON-NLS-1$
            String leftFieldName = StringUtils.substringAfter(leftPath, "/"); //$NON-NLS-1$
            boolean isPerformingTypeCheck = false;
            ComplexTypeMetadata type = repository.getComplexType(typeName);
            if (leftFieldName.endsWith("xsi:type") || leftFieldName.endsWith("tmdm:type")) { //$NON-NLS-1$ //$NON-NLS-2$
                isPerformingTypeCheck = true;
            }
            TypedExpression field = getField(repository, typeName, leftFieldName);
            // Field comparisons
            if (!whereCondition.isRightValueXPath()) { // Value based comparison
                if (isPerformingTypeCheck) {
                    TypeMetadata typeForCheck = repository.getNonInstantiableType("", value); //$NON-NLS-1$
                    if (!(typeForCheck instanceof ComplexTypeMetadata)) {
                        throw new IllegalArgumentException("Expected type '" + value + "' to be a complex type.");
                    }
                    if (!(field instanceof Field)) {
                        throw new IllegalArgumentException("Expected field '" + leftFieldName + "' to be a element path.");
                    }
                    return isa(((Field) field).getFieldMetadata(), ((ComplexTypeMetadata) typeForCheck));
                }
                String fieldTypeName = field.getTypeName();
                boolean isFk = field instanceof Field && ((Field) field).getFieldMetadata() instanceof ReferenceFieldMetadata;
                if (!isFk && !MetadataUtils.isValueAssignable(value, fieldTypeName)) {
                    LOGGER.warn("Skip '" + leftFieldName + "' because it can't accept value '" + value + "'");
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

    public static TypedExpression getField(MetadataRepository repository, String typeName, String fieldName) {
        ComplexTypeMetadata type = repository.getComplexType(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Type '" + typeName + "' does not exist.");
        }
        if (fieldName.endsWith("xsi:type") || fieldName.endsWith("tmdm:type")) { //$NON-NLS-1$ //$NON-NLS-2$
            return getField(repository, typeName, StringUtils.substringBeforeLast(fieldName, "/")); //$NON-NLS-1$
        } else if (UserQueryBuilder.TIMESTAMP_FIELD.equals(fieldName)) {
            return timestamp();
        } else if (UserQueryBuilder.TASK_ID_FIELD.equals(fieldName)) {
            return taskId();
        } else if (UserQueryBuilder.ID_FIELD.equals(fieldName)) {
            List<FieldMetadata> keyFields = type.getKeyFields();
            if (keyFields.isEmpty()) {
                throw new IllegalArgumentException("Can not query id on type '" + typeName + "' because type has no id field.");
            }
            if (keyFields.size() > 1) {
                throw new NotImplementedException("No support for query on composite key.");
            }
            return new Field(keyFields.get(0));
        } else if (UserQueryBuilder.STAGING_STATUS_FIELD.equals(fieldName)) {
            return UserStagingQueryBuilder.status();
        } else if (UserQueryBuilder.STAGING_SOURCE_FIELD.equals(fieldName)) {
            return UserStagingQueryBuilder.source();
        } else if (UserQueryBuilder.STAGING_ERROR_FIELD.equals(fieldName)) {
            return UserStagingQueryBuilder.error();
        }
        FieldMetadata field = type.getField(fieldName);
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
