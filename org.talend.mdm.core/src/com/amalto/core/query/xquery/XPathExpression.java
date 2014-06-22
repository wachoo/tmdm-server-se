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
public class XPathExpression implements Expression {
    private final Expression root;
    private final Expression xpathAsString;

    public XPathExpression(Expression root, Expression xpathAsString) {
        this.root = root;
        this.xpathAsString = xpathAsString;
    }

    public Expression getRoot() {
        return root;
    }

    public Expression getXPathAsString() {
        return xpathAsString;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
