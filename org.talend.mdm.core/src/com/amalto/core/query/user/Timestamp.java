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

public class Timestamp implements TypedExpression {

    public static final String TIMESTAMP_TYPE_NAME = "long"; //$NON-NLS-1$

    public static final Timestamp INSTANCE = new Timestamp();

    private Timestamp() {
    }

    public String getTypeName() {
        return TIMESTAMP_TYPE_NAME;
    }

    public Expression normalize() {
        return this;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
