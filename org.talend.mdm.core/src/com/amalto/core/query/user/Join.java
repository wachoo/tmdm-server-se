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
public class Join implements Expression {

    private final Field leftField;

    private final Field rightField;

    private final JoinType joinType;

    public Join(Field leftField, Field rightField, JoinType joinType) {
        this.leftField = leftField;
        this.rightField = rightField;
        this.joinType = joinType;
    }

    public Expression normalize() {
        return this;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Field getLeftField() {
        return leftField;
    }

    public Field getRightField() {
        return rightField;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
