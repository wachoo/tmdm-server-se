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

import org.talend.mdm.commmon.metadata.Types;

public class ByteConstant implements TypedExpression {

    private final Byte constant;

    public ByteConstant(String constant) {
        this.constant = Byte.parseByte(constant);
    }

    public Expression normalize() {
        return this;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public Byte getValue() {
        return constant;
    }

    public String getTypeName() {
        return Types.BYTE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ByteConstant)) {
            return false;
        }
        ByteConstant that = (ByteConstant) o;
        return !(constant != null ? !constant.equals(that.constant) : that.constant != null);
    }

    @Override
    public int hashCode() {
        return constant != null ? constant.hashCode() : 0;
    }
}
