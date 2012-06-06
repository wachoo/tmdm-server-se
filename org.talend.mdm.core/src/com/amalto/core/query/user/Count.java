package com.amalto.core.query.user;

/**
 *
 */
public class Count implements TypedExpression {
    public Expression normalize() {
        return this;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getTypeName() {
        return "long"; // TODO Constants
    }
}
