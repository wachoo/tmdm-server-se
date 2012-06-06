/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query.user;

public class Alias implements TypedExpression {

    private final TypedExpression typedExpression;

    private final String alias;

    public Alias(TypedExpression typedExpression, String alias) {
        this.typedExpression = typedExpression;
        this.alias = alias;
    }

    public String getAliasName() {
        return alias;
    }

    public TypedExpression getTypedExpression() {
        return typedExpression;
    }

    public String getTypeName() {
        return typedExpression.getTypeName();
    }

    public Expression normalize() {
        return this;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
