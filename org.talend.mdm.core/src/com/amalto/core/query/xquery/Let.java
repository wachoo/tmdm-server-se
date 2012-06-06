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
public class Let implements Expression {
    private final Variable variable;

    private final Expression expression;

    public Let(Variable variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public Variable getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
