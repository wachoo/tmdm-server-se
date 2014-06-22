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

/**
 *
 */
public class For implements Expression {

    private final Expression variable;

    private final Expression collection;

    private final Expression orderBy;

    private final Expression returnExpression;

    public For(Expression variable, Expression collection, Expression orderBy, Expression returnExpression) {
        this.variable = variable;
        this.collection = collection;
        this.orderBy = orderBy;
        this.returnExpression = returnExpression;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }

    public Expression getOrderBy() {
        return orderBy;
    }

    public Expression getVariable() {
        return variable;
    }

    public Expression getCollection() {
        return collection;
    }

    public Expression getReturnExpression() {
        return returnExpression;
    }


}
