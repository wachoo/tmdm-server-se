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
public class Constant implements Expression {
    public static final Constant PARENTHESIS_OPEN = new Constant("(");

    public static final Constant PARENTHESIS_CLOSE = new Constant(")");

    private final String value;

    public Constant(String value) {
        this.value = value;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Constant {value='" + value + '\'' + '}'; //NON-NLS NON-NLS
    }
}
