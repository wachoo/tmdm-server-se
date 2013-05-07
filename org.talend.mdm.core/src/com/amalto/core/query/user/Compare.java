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
public class Compare implements Condition {

    private Expression left;

    private Expression right;

    private final Predicate predicate;

    public Compare(Expression left, Predicate predicate, Expression right) {
        this.left = left;
        this.right = right;
        this.predicate = predicate;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expression normalize() {
        left = left.normalize();
        right = right.normalize();
        if (predicate == Predicate.OR) {
            if (left == UserQueryHelper.NO_OP_CONDITION) {
                return right;
            } else if (right == UserQueryHelper.NO_OP_CONDITION) {
                return left;
            }
        }
        return this;
    }
}
