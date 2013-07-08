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

public class FloatConstant implements TypedExpression {

    private final Float constant;

    public FloatConstant(String constant) {
        this.constant = Float.parseFloat(constant);
    }

    public Expression normalize() {
        return this;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public Float getValue() {
        return constant;
    }

    public String getTypeName() {
        return "float"; // TODO Constants
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FloatConstant)) {
            return false;
        }
        FloatConstant that = (FloatConstant) o;
        return !(constant != null ? !constant.equals(that.constant) : that.constant != null);
    }

    @Override
    public int hashCode() {
        return constant != null ? constant.hashCode() : 0;
    }
}
