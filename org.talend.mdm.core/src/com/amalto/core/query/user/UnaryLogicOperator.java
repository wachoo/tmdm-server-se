// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.query.user;

public class UnaryLogicOperator implements Condition {

    private Condition condition;

    private final Predicate predicate;

    public UnaryLogicOperator(Condition condition, Predicate predicate) {
        this.condition = condition;
        this.predicate = predicate;
    }

    public Condition getCondition() {
        return condition;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public Expression normalize() {
        if (predicate == Predicate.NOT) {
            return condition.accept(new VisitorAdapter<Condition>() {
                @Override
                public Condition visit(IsEmpty isEmpty) {
                    return new NotIsEmpty(isEmpty.getField());
                }

                @Override
                public Condition visit(IsNull isNull) {
                    return new NotIsNull(isNull.getField());
                }

                @Override
                public Condition visit(NotIsEmpty notIsEmpty) {
                    return new IsEmpty(notIsEmpty.getField());
                }

                @Override
                public Condition visit(NotIsNull notIsNull) {
                    return new IsNull(notIsNull.getField());
                }

                @Override
                public Condition visit(Compare condition) {
                    return new UnaryLogicOperator(condition, Predicate.NOT);
                }

                @Override
                public Condition visit(BinaryLogicOperator condition) {
                    return new BinaryLogicOperator(condition.getLeft().accept(this), condition.getPredicate(), condition.getRight().accept(this));
                }
            });
        } else {
            condition = (Condition) condition.normalize();
            return this;
        }
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
