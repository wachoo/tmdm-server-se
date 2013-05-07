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

import com.amalto.core.metadata.MetadataUtils;
import org.talend.mdm.commmon.metadata.*;
import com.amalto.core.webservice.WSStringPredicate;
import com.amalto.xmlserver.interfaces.*;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.*;

import java.util.Collection;
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
            String leftTypeName = leftPath.substring(0, leftPath.indexOf('/')); //$NON-NLS-1$
            String leftFieldName = StringUtils.substringAfter(leftPath, "/"); //$NON-NLS-1$
            boolean isPerformingTypeCheck = false;
            ComplexTypeMetadata leftType = repository.getComplexType(leftTypeName);
            if (leftFieldName.endsWith("xsi:type") || leftFieldName.endsWith("tmdm:type")) { //$NON-NLS-1$ //$NON-NLS-2$
                isPerformingTypeCheck = true;
            }
            if (UserQueryBuilder.ALL_FIELD.equals(leftFieldName)) {
                Collection<FieldMetadata> list = leftType.getFields();
                Condition condition = NO_OP_CONDITION;
                for (FieldMetadata fieldMetadata : list) {
                    if (fieldMetadata instanceof SimpleTypeFieldMetadata){
                        condition = or(condition, buildCondition(queryBuilder, 
                                new WhereCondition(leftTypeName + '/' + fieldMetadata.getName(), operator, value,
                                        WSStringPredicate.NONE.getValue()), repository));
                    }
                }
                return condition;
            }
            TypedExpression field = getInnerField(leftPath);
            if (field == null) {
                field = getField(repository, leftTypeName, leftFieldName);
            }
            // Field comparisons
            if (!whereCondition.isRightValueXPath()) { // Value based comparison
                if (isPerformingTypeCheck) {
                    TypeMetadata typeForCheck = repository.getNonInstantiableType(repository.getUserNamespace(), value);
                    if (typeForCheck == null) {
                        throw new IllegalArgumentException("Type '" + value + "' was not found.");
                    }
                    if (!(typeForCheck instanceof ComplexTypeMetadata)) {
                        throw new IllegalArgumentException("Expected type '" + value + "' to be a complex type.");
                    }
                    if (!(field instanceof Alias)) {
                        throw new IllegalArgumentException("Expected field '" + leftFieldName + "' to be an alias.");
                    }
                    Alias alias = (Alias) field;
                    if(!(alias.getTypedExpression() instanceof Type)) {
                        throw new IllegalArgumentException("Expected alias '" + leftFieldName + "' to be an alias of type.");
                    }
                    Type fieldExpression = (Type) alias.getTypedExpression();
                    return isa(fieldExpression.getField().getFieldMetadata(), ((ComplexTypeMetadata) typeForCheck));
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
                String rightTypeName = StringUtils.substringBefore(whereCondition.getRightValueOrPath(), "/"); //$NON-NLS-1$
                String rightFieldName = StringUtils.substringAfter(whereCondition.getRightValueOrPath(), "/"); //$NON-NLS-1$
                FieldMetadata leftField = leftType.getField(leftFieldName);
                ComplexTypeMetadata rightType = repository.getComplexType(rightTypeName);
                if (rightType == null) {
                    throw new IllegalArgumentException("Path '" + whereCondition.getRightValueOrPath()
                            + "' seems invalid (entity '" + rightTypeName + "' does not exist).");
                }
                FieldMetadata rightField = rightType.getField(rightFieldName);
                if (WhereCondition.EQUALS.equals(operator)) {
                    return eq(leftField, rightField);
                } else if (WhereCondition.LOWER_THAN_OR_EQUAL.equals(operator)) {
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

    public static TypedExpression getInnerField(String fieldName) {
        if (UserQueryBuilder.TIMESTAMP_FIELD.equals(fieldName)) {
            return timestamp();
        } else if (UserQueryBuilder.TASK_ID_FIELD.equals(fieldName)) {
            return taskId();
        }
        return null;
    }

    public static TypedExpression getField(MetadataRepository repository, String typeName, String fieldName) {
        // Considers attributes as elements
        // TODO This is assuming attributes are elements... which is true when these line were written.
        if(fieldName.startsWith("@")) { //$NON-NLS-1$
            fieldName = fieldName.substring(1);
        }
        int position = -1;
        if (fieldName.indexOf('[') > 0) {
            // TODO Check if there's multiple [] in path (unsupported).
            position = Integer.parseInt(fieldName.substring(fieldName.indexOf('[') + 1, fieldName.indexOf(']'))) - 1;
            fieldName = fieldName.substring(0, fieldName.indexOf('['));
        }
        // Additional trim() (in case XPath is like "Entity/FieldName  ").
        fieldName = fieldName.trim();
        ComplexTypeMetadata type = repository.getComplexType(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Type '" + typeName + "' does not exist.");
        }
        if (fieldName.endsWith("xsi:type") || fieldName.endsWith("tmdm:type")) { //$NON-NLS-1$ //$NON-NLS-2$
            FieldMetadata field = repository.getComplexType(typeName).getField(StringUtils.substringBeforeLast(fieldName, "/")); //$NON-NLS-1$
            if (fieldName.endsWith("xsi:type")) { //$NON-NLS-1$
                return alias(type(field), "xsi:type"); //$NON-NLS-1$
            } else {
                return alias(type(field), "tmdm:type"); //$NON-NLS-1$
            }
        } else if (UserQueryBuilder.TIMESTAMP_FIELD.equals(fieldName)) {
            return timestamp();
        } else if (UserQueryBuilder.TASK_ID_FIELD.equals(fieldName)) {
            return taskId();
        } else if (UserQueryBuilder.ID_FIELD.equals(fieldName)) {
            Collection<FieldMetadata> keyFields = type.getKeyFields();
            if (keyFields.isEmpty()) {
                throw new IllegalArgumentException("Can not query id on type '" + typeName + "' because type has no id field.");
            }
            if (keyFields.size() > 1) {
                throw new NotImplementedException("No support for query on composite key.");
            }
            return new Field(keyFields.iterator().next());
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
        if (field instanceof ContainedTypeFieldMetadata) {
            // Field does not contain a value, expected behavior is to return empty string.
            return new Alias(new StringConstant(StringUtils.EMPTY), field.getName());
        } else {
            if (position > -1) {
                return new IndexedField(field, position);
            } else {
                return new Field(field);
            }
        }
    }

    private static class NoOpCondition implements Condition {
        public Expression normalize() {
            return this;
        }

        public <T> T accept(Visitor<T> visitor) {
            return visitor.visit(this);
        }
    }
}
