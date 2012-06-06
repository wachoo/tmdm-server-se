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

import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class QizxQueryTextConverter implements ExpressionVisitor {

    private final StringBuilder builder = new StringBuilder();

    public StringBuilder getBuilder() {
        return builder;
    }

    public void visit(Expression expression, ExpressionVisitorContext context) {
        System.out.println("WARNING: not visited type: " + expression); //NON-NLS
    }

    public void visit(Function function, ExpressionVisitorContext context) {
        context.enter();
        {
            builder.append(function.getName()).append('(');

            List<Expression> parameters = function.getParameters();
            Iterator<Expression> expressionIterator = parameters.iterator();
            while (expressionIterator.hasNext()) {
                expressionIterator.next().accept(this, context);
                if (expressionIterator.hasNext()) {
                    builder.append(',');
                }
            }

            builder.append(')');
        }
        context.leave();
    }

    public void visit(XQueryCollection collection, ExpressionVisitorContext context) {
        builder.append("collection(\"").append(collection.getName()).append("\")"); //NON-NLS
    }

    public void visit(Condition condition, ExpressionVisitorContext context) {
        condition.getRoot().accept(this, context);
        builder.append("[");
        condition.getCondition().accept(this, context);
        builder.append("] ");
    }

    public void visit(Constant constant, ExpressionVisitorContext context) {
        builder.append(constant.getValue());
    }

    public void visit(Text text, ExpressionVisitorContext context) {
        builder.append("'").append(text.getValue()).append("'");
    }

    public void visit(Number number, ExpressionVisitorContext context) {
        builder.append(number.getValue());
    }

    public void visit(For forExpression, ExpressionVisitorContext context) {
        context.enter();
        {
            builder.append("for "); //NON-NLS
            forExpression.getVariable().accept(this, context);
            builder.append(" in "); //NON-NLS
            forExpression.getCollection().accept(this, context);
            forExpression.getOrderBy().accept(this, context);
            forExpression.getReturnExpression().accept(this, context);
        }
        context.leave();
    }

    public void visit(Let letDeclaration, ExpressionVisitorContext context) {
        builder.append("let "); //NON-NLS
        letDeclaration.getVariable().accept(this, context);
        builder.append(" := ");
        letDeclaration.getExpression().accept(this, context);
    }

    public void visit(OrderBy orderBy, ExpressionVisitorContext context) {
        builder.append(" order by "); //NON-NLS
        orderBy.getExpression().accept(this, context);
    }

    public void visit(Variable variable, ExpressionVisitorContext context) {
        builder.append('$').append(context.getVariable(variable));
    }

    public void visit(Return returnExpression, ExpressionVisitorContext context) {
        builder.append(" return "); //NON-NLS
        returnExpression.getExpression().accept(this, context);
    }

    public void visit(EvaluatedExpression expression, ExpressionVisitorContext context) {
        builder.append('{');
        expression.getInnerExpression().accept(this, context);
        builder.append("}\n");
    }

    public void visit(XPathExpression expression, ExpressionVisitorContext context) {
        expression.getRoot().accept(this, context);
        expression.getXPathAsString().accept(this, context);
    }

    public void visit(If ifExpression, ExpressionVisitorContext context) {
        visit(((Function) ifExpression), context);
        builder.append(" then "); //NON-NLS
        ifExpression.getOnSuccess().accept(this, context);
        builder.append(" else "); //NON-NLS
        ifExpression.getOnFail().accept(this, context);
    }
}
