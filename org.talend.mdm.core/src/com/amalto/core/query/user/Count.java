package com.amalto.core.query.user;

import org.talend.mdm.commmon.metadata.Types;

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
        return Types.LONG;
    }
}
