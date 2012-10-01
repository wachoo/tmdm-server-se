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

package com.amalto.core.query.user;

/**
 *
 */
public class OrderBy implements Expression {

    public static enum Direction {
        ASC,
        DESC
    }

    private final TypedExpression field;

    private final Direction direction;

    public OrderBy(TypedExpression field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public TypedExpression getField() {
        return field;
    }

    public Expression normalize() {
        return this;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
