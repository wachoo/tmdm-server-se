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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderBy)) {
            return false;
        }
        OrderBy orderBy = (OrderBy) o;
        if (direction != orderBy.direction) {
            return false;
        }
        if (field != null ? !field.equals(orderBy.field) : orderBy.field != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        return result;
    }
}
