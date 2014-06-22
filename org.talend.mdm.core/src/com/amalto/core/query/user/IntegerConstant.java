package com.amalto.core.query.user;

import org.talend.mdm.commmon.metadata.Types;

public class IntegerConstant implements ConstantExpression<Integer> {

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

    public Integer getValue() {
        return constant;
    }

    public String getTypeName() {
        return Types.INTEGER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerConstant)) {
            return false;
        }
        IntegerConstant that = (IntegerConstant) o;
        return constant == that.constant;
    }

    @Override
    public int hashCode() {
        return constant;
    }
}
