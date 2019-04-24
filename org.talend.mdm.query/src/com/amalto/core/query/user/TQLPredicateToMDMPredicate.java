/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */
package com.amalto.core.query.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;
import org.talend.tql.model.AllFields;
import org.talend.tql.model.AndExpression;
import org.talend.tql.model.ComparisonExpression;
import org.talend.tql.model.ComparisonOperator;
import org.talend.tql.model.Expression;
import org.talend.tql.model.FieldBetweenExpression;
import org.talend.tql.model.FieldCompliesPattern;
import org.talend.tql.model.FieldContainsExpression;
import org.talend.tql.model.FieldInExpression;
import org.talend.tql.model.FieldIsEmptyExpression;
import org.talend.tql.model.FieldIsInvalidExpression;
import org.talend.tql.model.FieldIsValidExpression;
import org.talend.tql.model.FieldMatchesRegex;
import org.talend.tql.model.FieldReference;
import org.talend.tql.model.FieldWordCompliesPattern;
import org.talend.tql.model.LiteralValue;
import org.talend.tql.model.NotExpression;
import org.talend.tql.model.OrExpression;
import org.talend.tql.model.TqlElement;
import org.talend.tql.visitor.IASTVisitor;

public class TQLPredicateToMDMPredicate implements IASTVisitor<Condition> {

    private final Stack<TypedExpression> typedValues = new Stack<>();

    private final Map<String, ComplexTypeMetadata> types = new HashMap<>();

    public TQLPredicateToMDMPredicate(ComplexTypeMetadata... types) {
        for (ComplexTypeMetadata type : types) {
            this.types.put(type.getName(), type);
        }
    }

    private Condition mergeAndOr(Supplier<Expression[]> source, Predicate predicate) {
        final List<Condition> conditions = Stream
                .of(source.get())
                .map(e -> e.accept(this))
                .collect(Collectors.toList());
        Condition current = conditions.get(0);
        for (int i = 1; i < conditions.size(); i++) {
            current = new BinaryLogicOperator(current, predicate, conditions.get(i));
        }
        return current;
    }

    private Condition mergeContains(Predicate predicate, String fieldValue) {
        Condition current = new Compare(popCurrentExpression(), Predicate.CONTAINS, new StringConstant(fieldValue));
        for (int i = 0; i < typedValues.size(); i++) {
            current = new BinaryLogicOperator(current, predicate,
                    new Compare(popCurrentExpression(), Predicate.CONTAINS, new StringConstant(fieldValue)));
        }
        return current;
    }

    private Condition mergeEquals(Predicate predicate, TypedExpression fieldLeftValue, TypedExpression fieldRightValue) {
        Condition current = new Compare(fieldLeftValue, Predicate.EQUALS, fieldRightValue);
        for (int i = 0; i < typedValues.size(); i++) {
            current = new BinaryLogicOperator(current, predicate,
                    new Compare(popCurrentExpression(), Predicate.EQUALS, fieldRightValue));
        }
        return current;
    }


    private TypedExpression peekCurrentExpression() {
        return typedValues.peek();
    }

    private TypedExpression popCurrentExpression() {
        return typedValues.pop();
    }

    @Override
    public Condition visit(TqlElement tqlElement) {
        throw new NotImplementedException("TqlElement not implemented.");
    }

    @Override
    public Condition visit(ComparisonOperator comparisonOperator) {
        // Be careful here: order in pop() is very important!
        final TypedExpression right = popCurrentExpression();
        final TypedExpression left = popCurrentExpression();

        switch (comparisonOperator.getOperator()) {
            case EQ:
                return mergeEquals(Predicate.OR, left, right);
            case LT:
                return new Compare(left, Predicate.LOWER_THAN, right);
            case GT:
                return new Compare(left, Predicate.GREATER_THAN, right);
            case NEQ:
                return new UnaryLogicOperator(new Compare(left, Predicate.EQUALS, right), Predicate.NOT);
            case LET:
                return new Compare(left, Predicate.LOWER_THAN_OR_EQUALS, right);
            case GET:
                return new Compare(left, Predicate.GREATER_THAN_OR_EQUALS, right);
            default:
                throw new NotImplementedException(
                        "'" + comparisonOperator.getOperator().name() + "' support not implemented.");
        }
    }

    @Override
    public Condition visit(LiteralValue literalValue) {
        typedValues.push(UserQueryBuilder.createConstant(peekCurrentExpression(), literalValue.getValue()));
        return null;
    }

    @Override
    public Condition visit(FieldReference fieldReference) {
        final String path = fieldReference.getPath();
        final String typeName = StringUtils.substringBefore(path, ".");
        final String fieldName = StringUtils.substringAfter(path, ".");
        final ComplexTypeMetadata complexTypeMetadata = types.get(typeName);
        if (complexTypeMetadata == null) {
            throw new IllegalArgumentException("Type '" + typeName + "' is not selected in query.");
        }
        if ((complexTypeMetadata.getField(fieldName) instanceof ReferenceFieldMetadata)
                && (!((ReferenceFieldMetadata) complexTypeMetadata.getField(fieldName))
                .getForeignKeyInfoFields()
                .isEmpty())) {
            ((ReferenceFieldMetadata) complexTypeMetadata.getField(fieldName))
                    .getForeignKeyInfoFields()
                    .forEach(key -> typedValues.push(new Field(key)));
        } else {
            typedValues.push(new Field(complexTypeMetadata.getField(fieldName)));
        }
        return null;
    }

    @Override
    public Condition visit(org.talend.tql.model.Expression expression) {
        throw new NotImplementedException("Expression not implemented.");
    }

    @Override
    public Condition visit(AndExpression andExpression) {
        return mergeAndOr(andExpression::getExpressions, Predicate.AND);
    }

    @Override
    public Condition visit(OrExpression orExpression) {
        return mergeAndOr(orExpression::getExpressions, Predicate.OR);
    }

    @Override
    public Condition visit(ComparisonExpression comparisonExpression) {
        comparisonExpression.getField().accept(this);
        comparisonExpression.getValueOrField().accept(this);
        return comparisonExpression.getOperator().accept(this);
    }

    @Override
    public Condition visit(FieldIsEmptyExpression fieldIsEmptyExpression) {
        fieldIsEmptyExpression.getField().accept(this);
        // TMDM-13367 : Workaround : A value not valorised in MDM is null
        return new IsNull(popCurrentExpression());
    }

    @Override
    public Condition visit(FieldBetweenExpression fieldBetweenExpression) {
        fieldBetweenExpression.getField().accept(this);
        final int start = Integer.parseInt(fieldBetweenExpression.getLeft().getValue());
        final int end = Integer.parseInt(fieldBetweenExpression.getRight().getValue());
        return new Range(popCurrentExpression(), start, end);
    }

    @Override
    public Condition visit(FieldContainsExpression fieldContainsExpression) {
        fieldContainsExpression.getField().accept(this);
        return mergeContains(Predicate.OR, fieldContainsExpression.getValue());
    }

    @Override
    public Condition visit(NotExpression notExpression) {
        return new UnaryLogicOperator(notExpression.getExpression().accept(this), Predicate.NOT);
    }

    @Override
    public Condition visit(FieldInExpression fieldInExpression) {
        fieldInExpression.getField().accept(this);
        List<String> constantValues = new ArrayList<>();
        Stream.of(fieldInExpression.getValues()).forEach(val -> constantValues.add(val.getValue()));
        final TypedExpression typedExpression = popCurrentExpression();
        return new Compare(typedExpression, Predicate.IN,
                UserQueryBuilder.createConstant(typedExpression, constantValues));
    }

    @Override
    public Condition visit(FieldIsValidExpression fieldIsValidExpression) {
        throw new UnsupportedOperationException("FieldIsValidExpression not supported by MDM.");
    }

    @Override
    public Condition visit(FieldIsInvalidExpression fieldIsInvalidExpression) {
        throw new UnsupportedOperationException("FieldIsInvalidExpression not supported by MDM.");
    }

    @Override
    public Condition visit(FieldMatchesRegex fieldMatchesRegex) {
        throw new UnsupportedOperationException("FieldMatchesRegex not supported by MDM.");
    }

    @Override
    public Condition visit(FieldCompliesPattern fieldCompliesPattern) {
        throw new UnsupportedOperationException("FieldCompliesPattern not supported by MDM.");
    }

    @Override
    public Condition visit(FieldWordCompliesPattern fieldWordCompliesPattern) {
        throw new UnsupportedOperationException("FieldWordCompliesPattern not supported by MDM.");
    }

    @Override
    public Condition visit(AllFields allFields) {
        throw new UnsupportedOperationException("AllFields not supported by MDM.");
    }
}
