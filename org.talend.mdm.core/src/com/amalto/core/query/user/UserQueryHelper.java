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
import com.amalto.core.query.user.metadata.MetadataField;
import com.amalto.core.webservice.WSStringPredicate;
import com.amalto.xmlserver.interfaces.*;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.*;

import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.*;
import static com.amalto.core.query.user.UserQueryBuilder.emptyOrNull;

public class UserQueryHelper {

    public static final NoOpCondition NO_OP_CONDITION = new NoOpCondition();

    private static final Logger LOGGER = Logger.getLogger(UserQueryHelper.class);

    private UserQueryHelper() {
    }

    public static Condition buildCondition(UserQueryBuilder queryBuilder, IWhereItem whereItem, MetadataRepository repository) {
        if (whereItem == null) {
            return NO_OP_CONDITION;
        }
        if (whereItem instanceof WhereAnd || whereItem instanceof WhereOr) { // Handle ANDs and ORs
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
            boolean isNotCondition = WhereCondition.PRE_NOT.equals(whereCondition.getStringPredicate());
            // Special case for full text: left path is actually the keyword for full text search.
            if (WhereCondition.FULLTEXTSEARCH.equals(operator)) {
                return fullText(value);
            }
            String leftPath = whereCondition.getLeftPath();
            if (leftPath.indexOf('/') == -1) {
                throw new IllegalArgumentException("Incorrect XPath '" + leftPath + "'. An XPath like 'Entity/element' was expected."); //$NON-NLS-1$ //$NON-NLS-2$
            }
            String leftTypeName = leftPath.substring(0, leftPath.indexOf('/')); //$NON-NLS-1$
            if (".".equals(leftTypeName)) { //$NON-NLS-1$
                leftTypeName = queryBuilder.getSelect().getTypes().get(0).getName(); // When using ".", uses first type in select
            }
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
                if (isNotCondition) {
                    return not(condition);
                } else {
                    return condition;
                }
            }
            List<TypedExpression> fields = getInnerField(leftPath);
            if (fields == null) {
                fields = getFields(repository, leftTypeName, leftFieldName);
            }
            Condition condition = null;
            for (TypedExpression field : fields) {
                // Field comparisons
                if (!whereCondition.isRightValueXPath()) { // Value based comparison
                    if (isPerformingTypeCheck) {
                        if (!WhereCondition.EMPTY_NULL.equals(whereCondition.getOperator())) {
                            if (!(field instanceof Alias)) {
                                throw new IllegalArgumentException("Expected field '" + leftFieldName + "' to be an alias.");
                            }
                            Alias alias = (Alias) field;
                            if (!(alias.getTypedExpression() instanceof Type)) {
                                throw new IllegalArgumentException("Expected alias '" + leftFieldName + "' to be an alias of type.");
                            }
                            Type fieldExpression = (Type) alias.getTypedExpression();
                            ComplexTypeMetadata typeForCheck = (ComplexTypeMetadata) fieldExpression.getField().getFieldMetadata().getType();
                            if (!typeForCheck.getName().equals(value)) {
                                for (ComplexTypeMetadata subType : typeForCheck.getSubTypes()) {
                                    if (subType.getName().equals(value)) {
                                        typeForCheck = subType;
                                        break;
                                    }
                                }
                            }
                            condition = isa(fieldExpression.getField().getFieldMetadata(), typeForCheck);
                        } else {
                            // TMDM-6831: Consider a "emptyOrNull(type)" as a "isa(field, actual_field_type)".
                            Alias alias = (Alias) field;
                            if (!(alias.getTypedExpression() instanceof Type)) {
                                throw new IllegalArgumentException("Expected alias '" + leftFieldName + "' to be an alias of type.");
                            }
                            Type fieldExpression = (Type) alias.getTypedExpression();
                            condition = emptyOrNull(fieldExpression);
                        }
                    }
                    String fieldTypeName = field.getTypeName();
                    boolean isFk = field instanceof Field && ((Field) field).getFieldMetadata() instanceof ReferenceFieldMetadata;
                    if (!isFk && !MetadataUtils.isValueAssignable(value, fieldTypeName) && !WhereCondition.EMPTY_NULL.equals(operator)) {
                        LOGGER.warn("Skip '" + leftFieldName + "' because it can't accept value '" + value + "'");
                        continue;
                    }
                    if (WhereCondition.CONTAINS.equals(operator) || WhereCondition.STRICTCONTAINS.equals(operator)) {
                        condition = add(condition, contains(field, value));
                    } else if (WhereCondition.EQUALS.equals(operator)) {
                        condition = add(condition, eq(field, value));
                    } else if (WhereCondition.GREATER_THAN.equals(operator)) {
                        condition = add(condition, gt(field, value));
                    } else if (WhereCondition.GREATER_THAN_OR_EQUAL.equals(operator)) {
                        condition = add(condition, gte(field, value));
                    } else if (WhereCondition.LOWER_THAN.equals(operator)) {
                        condition = add(condition, lt(field, value));
                    } else if (WhereCondition.LOWER_THAN_OR_EQUAL.equals(operator)) {
                        condition = add(condition, lte(field, value));
                    } else if (WhereCondition.NOT_EQUALS.equals(operator)) {
                        condition = add(condition, neq(field, value));
                    } else if (WhereCondition.STARTSWITH.equals(operator)) {
                        condition = add(condition, startsWith(field, value));
                    } else if (WhereCondition.EMPTY_NULL.equals(operator)) {
                        condition = add(condition, emptyOrNull(field));
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
                        condition = add(condition, eq(leftField, rightField));
                    } else if (WhereCondition.LOWER_THAN_OR_EQUAL.equals(operator)) {
                        condition = add(condition, lte(leftField, rightField));
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
                    } else {
                        throw new NotImplementedException("'" + operator + "' support not implemented for field to field comparison.");
                    }
                }
            }
            if (condition == null) {
                return NO_OP_CONDITION;
            }
            if (isNotCondition) {
                return not(condition);
            } else {
                return condition;
            }
        } else {
            throw new NotImplementedException("No support for where item of type " + whereItem.getClass().getName());
        }
    }

    private static Condition add(Condition condition, Condition newCondition) {
        if (condition == null) {
            condition = newCondition;
        } else {
            condition = or(newCondition, condition);
        }
        return condition;
    }

    public static List<TypedExpression> getInnerField(String fieldName) {
        MetadataField metadataField = MetadataField.Factory.getMetadataField(fieldName);
        if (metadataField != null) {
            return Collections.singletonList(metadataField.getConditionExpression());
        }
        return null;
    }

    public static List<TypedExpression> getFields(MetadataRepository repository, String typeName, String fieldName) {
        // Considers attributes as elements
        // TODO This is assuming attributes are elements... which is true when these line were written.
        if (fieldName.startsWith("@")) { //$NON-NLS-1$
            fieldName = fieldName.substring(1);
        }
        int position = -1;
        if (fieldName.indexOf('[') > 0) {
            // Check if there's multiple [] in path (unsupported).
            if (fieldName.indexOf('[', fieldName.indexOf('[') + 1) > 0) {
                throw new IllegalArgumentException("Does not support multiple index in path.");
            }
            position = Integer.parseInt(fieldName.substring(fieldName.indexOf('[') + 1, fieldName.indexOf(']'))) - 1;
            fieldName = fieldName.substring(0, fieldName.indexOf('['));
        }
        // Additional trim() (in case XPath is like "Entity/FieldName  ").
        fieldName = fieldName.trim();
        MetadataField metadataField = MetadataField.Factory.getMetadataField(fieldName);
        ComplexTypeMetadata type = repository.getComplexType(typeName);
        if (type == null) {
            throw new IllegalArgumentException("Type '" + typeName + "' does not exist.");
        }
        if (metadataField != null) {
            return Collections.singletonList(metadataField.getConditionExpression());
        }
        if (fieldName.endsWith("xsi:type") || fieldName.endsWith("tmdm:type")) { //$NON-NLS-1$ //$NON-NLS-2$
            FieldMetadata field = repository.getComplexType(typeName).getField(StringUtils.substringBeforeLast(fieldName, "/")); //$NON-NLS-1$
            if (fieldName.endsWith("xsi:type")) { //$NON-NLS-1$
                return Collections.singletonList(alias(type(field), "xsi:type")); //$NON-NLS-1$
            } else {
                return Collections.singletonList(alias(type(field), "tmdm:type")); //$NON-NLS-1$
            }
        } else if (UserQueryBuilder.ID_FIELD.equals(fieldName)) {
            Collection<FieldMetadata> keyFields = type.getKeyFields();
            if (keyFields.isEmpty()) {
                throw new IllegalArgumentException("Can not query id on type '" + typeName + "' because type has no id field.");
            }
            List<TypedExpression> expressions = new LinkedList<TypedExpression>();
            for (FieldMetadata keyField : keyFields) {
                expressions.add(new Field(keyField));
            }
            return expressions;
        } else if ("/*".equals(fieldName)) { //$NON-NLS-1$
            List<TypedExpression> expressions = new LinkedList<TypedExpression>();
            for (FieldMetadata field : type.getFields()) {
                expressions.add(new Field(field));
            }
            return expressions;
        }
        FieldMetadata field = type.getField(fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Field '" + fieldName + "' does not exist in type '" + typeName + "'.");
        }
        if (field instanceof ContainedTypeFieldMetadata) {
            // Field does not contain a value, expected behavior is to return empty string.
            return Collections.<TypedExpression>singletonList(new Alias(new StringConstant(StringUtils.EMPTY), field.getName()));
        } else {
            if (position > -1) {
                return Collections.<TypedExpression>singletonList(new IndexedField(field, position));
            } else {
                return Collections.<TypedExpression>singletonList(new Field(field));
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
