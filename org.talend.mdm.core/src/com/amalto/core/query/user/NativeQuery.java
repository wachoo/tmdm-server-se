package com.amalto.core.query.user;

/**
 *
 */
public class NativeQuery implements Expression {

    private final String nativeQuery;

    public NativeQuery(String nativeQuery) {
        this.nativeQuery = nativeQuery;
    }

    public String getQueryText() {
        return nativeQuery;
    }

    @Override
    public Expression normalize() {
        return this;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
