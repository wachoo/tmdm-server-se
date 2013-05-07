package com.amalto.core.query.user;

import org.talend.mdm.commmon.metadata.Types;

public class IntegerConstant implements TypedExpression {

    private final int constant;

    public IntegerConstant(int constant) {
        this.constant = constant;
    }

    public Expression normalize() {
        return this;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public int getValue() {
        return constant;
    }

    public String getTypeName() {
        return Types.INTEGER;
    }
}
