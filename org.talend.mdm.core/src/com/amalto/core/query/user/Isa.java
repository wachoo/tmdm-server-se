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


import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;

public class Isa implements Condition {

    private final TypedExpression expression;

    private final ComplexTypeMetadata type;

    public Isa(TypedExpression expression, ComplexTypeMetadata type) {
        this.expression = expression;
        this.type = type;
    }

    public TypedExpression getExpression() {
        return expression;
    }

    public ComplexTypeMetadata getType() {
        return type;
    }

    public Expression normalize() {
        return this;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
