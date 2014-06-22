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

package com.amalto.core.query;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.query.user.*;
import com.amalto.core.query.user.OrderBy;
import com.amalto.core.query.xquery.*;
import com.amalto.core.query.xquery.Condition;
import com.amalto.core.query.xquery.Expression;
import com.amalto.core.query.xquery.Number;
import org.apache.commons.lang.NotImplementedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class QizxQueryModelConverter implements Visitor<Expression> {

    private final Map<TypeMetadata, Variable> typeToVariable = new HashMap<TypeMetadata, Variable>();

    private For forStatement;

    private boolean returnCount;

    private String dataContainerName;

    public QizxQueryModelConverter(String dataContainerName) {
        this.dataContainerName = dataContainerName;
    }

    public Expression visit(Select select) {
        List<ComplexTypeMetadata> types = select.getTypes();
        for (ComplexTypeMetadata type : types) {
            typeToVariable.put(type, new Variable());
        }

        // Create return statement
        Return returnExpression = Return.createWrappedReturn(typeToVariable, select.getSelectedFields(), "result"); //$NON-NLS
        returnCount = returnExpression.returnsCount();

        Expression collectionFilter = select.getCondition().accept(this);
        Expression orderBy = select.getOrderBy().accept(this);

        List<Join> joins = select.getJoins();
        Expression[] allConditionsExpressions = new Expression[joins.size() + 1];
        allConditionsExpressions[0] = new Condition(new XQueryCollection(dataContainerName), collectionFilter);
        int j = 1;
        for (Join currentJoin : joins) {
            allConditionsExpressions[j++] = currentJoin.accept(this);
        }
        CompositeExpression forExpression = new CompositeExpression(allConditionsExpressions);

        forStatement = new For(typeToVariable.get(select.getTypes().get(0)), forExpression, orderBy, returnExpression);

        return select.getPaging().accept(this);
    }

    @Override
    public Expression visit(NativeQuery nativeQuery) {
        throw new NotImplementedException();
    }

    public Expression visit(com.amalto.core.query.user.Condition condition) {
        return null;
    }

    public Expression visit(Compare condition) {
        Expression conditionExpression = getState(condition).getExpression(condition, this);
        return new CompositeExpression(Constant.PARENTHESIS_OPEN, conditionExpression, Constant.PARENTHESIS_CLOSE);
    }

    public Expression visit(BinaryLogicOperator condition) {
        Expression left = condition.getLeft().accept(this);
        Expression predicate = condition.getPredicate().accept(this);
        Expression right = condition.getRight().accept(this);
        CompositeExpression conditionExpression = new CompositeExpression(left, predicate, right);

        return new CompositeExpression(Constant.PARENTHESIS_OPEN, conditionExpression, Constant.PARENTHESIS_CLOSE);
    }

    public Expression visit(UnaryLogicOperator condition) {
        Expression predicate = condition.getPredicate().accept(this);
        Expression conditionExpression = condition.getCondition().accept(this);
        CompositeExpression compositeExpression = new CompositeExpression(predicate, conditionExpression);

        return new CompositeExpression(Constant.PARENTHESIS_OPEN, compositeExpression, Constant.PARENTHESIS_CLOSE);
    }

    public Expression visit(Range range) {
        return new InRange(range.getExpression().accept(this), range.getStart().accept(this), range.getEnd().accept(this));
    }

    public Expression visit(Timestamp timestamp) {
        throw new NotImplementedException();
    }

    public Expression visit(TaskId taskId) {
        throw new NotImplementedException();
    }

    @Override
    public Expression visit(Type type) {
        throw new NotImplementedException();
    }

    public Expression visit(StagingStatus stagingStatus) {
        throw new NotImplementedException();
    }

    @Override
    public Expression visit(StagingError stagingError) {
        throw new NotImplementedException();
    }

    @Override
    public Expression visit(StagingSource stagingSource) {
        throw new NotImplementedException();
    }

    public Expression visit(Join join) {
        Variable joinVariable = new Variable();
        typeToVariable.put(join.getRightField().getFieldMetadata().getContainingType(), joinVariable);

        Compare condition = new Compare(join.getLeftField(), Predicate.EQUALS, join.getRightField());
        return new Let(joinVariable, new Condition(new XQueryCollection(dataContainerName), condition.accept(this)));
    }

    public Expression visit(OrderBy orderBy) {
        return new com.amalto.core.query.xquery.OrderBy(orderBy.getField().accept(this));
    }

    public Expression visit(Paging paging) {
        boolean usePagingFunction = returnCount || paging.getLimit() != Integer.MAX_VALUE;
        if (usePagingFunction) {
            return new PagedQuery(paging.getStart(), paging.getLimit(), forStatement);
        } else {
            return forStatement;
        }
    }

    public Expression visit(Count count) {
        return null;
    }

    public Expression visit(Predicate.And and) {
        return new Constant(" and "); //NON-NLS
    }

    public Expression visit(Predicate.Or or) {
        return new Constant(" or "); //NON-NLS
    }

    public Expression visit(Predicate.Equals equals) {
        return new Constant(" = ");
    }

    public Expression visit(Predicate.GreaterThan greaterThan) {
        return new Constant(" > ");
    }

    public Expression visit(Predicate.LowerThan lowerThan) {
        return new Constant(" < ");
    }

    public Expression visit(FullText fullText) {
        throw new NotImplementedException("Support for full text query to be implemented");
    }

    public Expression visit(Isa isa) {
        throw new NotImplementedException("Support for 'is a' to be implemented");
    }

    @Override
    public Expression visit(FieldFullText fieldFullText) {
        throw new NotImplementedException("Support for full text query to be implemented");
    }

    @Override
    public Expression visit(ComplexTypeExpression expression) {
        throw new NotImplementedException("Support for typed expressions to be implemented");
    }

    public Expression visit(Predicate.Contains contains) {
        throw new NotImplementedException("No support for '" + contains + "'");
    }

    public Expression visit(IsEmpty isEmpty) {
        throw new NotImplementedException("No support for isEmpty.");
    }

    public Expression visit(NotIsEmpty notIsEmpty) {
        throw new NotImplementedException("No support for notIsEmpty.");
    }

    public Expression visit(IsNull isNull) {
        throw new NotImplementedException("No support for isNull.");
    }

    public Expression visit(NotIsNull notIsNull) {
        throw new NotImplementedException("No support for notIsNull.");
    }

    public Expression visit(com.amalto.core.query.user.Expression expression) {
        throw new NotImplementedException("No support for '" + expression + "'");
    }

    public Expression visit(Predicate predicate) {
        throw new NotImplementedException("No support for '" + predicate + "'");
    }

    public Expression visit(Field field) {
        Variable variable = typeToVariable.get(field.getFieldMetadata().getContainingType());
        return new XPathExpression(variable, new Constant("/" + field.getFieldMetadata().getName()));
    }

    public Expression visit(Alias alias) {
        throw new NotImplementedException("No support for alias.");
    }

    public Expression visit(Id id) {
        throw new NotImplementedException("No support for id reference.");
    }

    public Expression visit(StringConstant constant) {
        return new Text(constant.getValue());
    }

    public Expression visit(IntegerConstant constant) {
        return new Number(constant.getValue());
    }

    public Expression visit(DateConstant constant) {
        throw new NotImplementedException("No support for date");
    }

    public Expression visit(DateTimeConstant constant) {
        throw new NotImplementedException("No support for date");
    }

    public Expression visit(BooleanConstant constant) {
        throw new NotImplementedException("No support for boolean");
    }

    public Expression visit(BigDecimalConstant constant) {
        throw new NotImplementedException("No support for big decimal");
    }

    public Expression visit(TimeConstant constant) {
        throw new NotImplementedException("No support for time");
    }

    public Expression visit(ShortConstant constant) {
        throw new NotImplementedException("No support for short");
    }

    public Expression visit(ByteConstant constant) {
        throw new NotImplementedException("No support for byte");
    }

    public Expression visit(LongConstant constant) {
        throw new NotImplementedException("No support for long");
    }

    public Expression visit(DoubleConstant constant) {
        throw new NotImplementedException("No support for double");
    }

    public Expression visit(FloatConstant constant) {
        throw new NotImplementedException("No support for float");
    }

    private State getState(Compare condition) {
        if (condition.getPredicate().isPrimitive()) {
            return new BuiltinOperatorState();
        } else {
            return new BuiltinFunctionState();
        }

    }

    interface State {
        Expression getExpression(Compare condition, Visitor<Expression> visitor);
    }

    class BuiltinOperatorState implements State {
        public Expression getExpression(Compare condition, Visitor<Expression> visitor) {
            Expression left = condition.getLeft().accept(visitor);
            Expression predicate = condition.getPredicate().accept(visitor);
            Expression right = condition.getRight().accept(visitor);
            return new CompositeExpression(left, predicate, right);
        }
    }

    class BuiltinFunctionState implements State {
        public Expression getExpression(Compare condition, Visitor<Expression> visitor) {
            Expression left = condition.getLeft().accept(visitor);
            Expression right = condition.getRight().accept(visitor);
            List<Expression> expressions = Arrays.asList(left, right);

            // TODO Might not be always a Contains after all
            return new Contains(expressions);
        }

    }

}
