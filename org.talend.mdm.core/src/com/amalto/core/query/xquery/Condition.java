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
public class Condition implements Expression {
    private Expression root;

    private Expression condition;

    public Condition() {
    }

    public Condition(Expression root, Expression condition) {
        this.root = root;
        this.condition = condition;
    }

    public Expression getRoot() {
        return root;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setRoot(Expression root) {
        this.root = root;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
