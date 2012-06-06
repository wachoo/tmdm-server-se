package com.amalto.core.query.xquery;

public class Number extends Constant {
    public Number(int value) {
        super(String.valueOf(value));
    }

    @Override
    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
