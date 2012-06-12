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

package com.amalto.core.query.xquery;

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.query.user.Count;
import com.amalto.core.query.user.Field;
import com.amalto.core.query.user.VisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Return implements Expression {

    private Expression expression;

    private boolean returnCount;

    private Return(Expression expression) {
        this.expression = expression;
    }

    private Return() {
    }

    public static Return createWrappedReturn(Map<TypeMetadata, Variable> variable, List<com.amalto.core.query.user.TypedExpression> fields, String wrapperElementName) {
        Return returnFunction = new Return();

        List<Expression> expressions = new ArrayList<Expression>(fields.size());
        FieldVisitor visitor = new FieldVisitor(variable, returnFunction);
        for (com.amalto.core.query.user.Expression field : fields) {
            Expression fieldExpression = field.accept(visitor);
            if (fieldExpression != null) {
                expressions.add(fieldExpression);
            }
        }

        Expression returnExpression = wrapReturn(wrapperElementName, new CompositeExpression(expressions.toArray(new Expression[expressions.size()])));
        returnFunction.setExpression(returnExpression);

        return returnFunction;
    }

    private static Expression wrapReturn(String xmlElementName, Expression expression) {
        return new CompositeExpression(new Constant("<" + xmlElementName + ">"), expression, new Constant("</" + xmlElementName + ">"));
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }

    private void setExpression(Expression expression) {
        this.expression = expression;
    }

    public boolean returnsCount() {
        return returnCount;
    }

    public Expression getExpression() {
        return expression;
    }

    private static class FieldVisitor extends VisitorAdapter<Expression> {

        private final Map<TypeMetadata, Variable> variable;

        private final Return returnFunction;

        public FieldVisitor(Map<TypeMetadata, Variable> variable, Return returnFunction) {
            this.variable = variable;
            this.returnFunction = returnFunction;
        }

        @Override
        public Expression visit(Field field) {
            Variable elementVariable = new Variable();
            FieldMetadata fieldMetadata = field.getFieldMetadata();

            Return returnExpr = new Return(new If(new Not(new Empty(elementVariable)), elementVariable, new Constant("<" + fieldMetadata.getName() + "/>")));
            Let let = new Let(elementVariable, new XPathExpression(variable.get(fieldMetadata.getContainingType()), new Constant("/" + fieldMetadata.getName())));

            return new EvaluatedExpression(new CompositeExpression(let, returnExpr));
        }

        @Override
        public Expression visit(Count count) {
            returnFunction.setReturnCount(true);
            return null;
        }
    }

    private void setReturnCount(boolean returnCount) {
        this.returnCount = returnCount;
    }
}
