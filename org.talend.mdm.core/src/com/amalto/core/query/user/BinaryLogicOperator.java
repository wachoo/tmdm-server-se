package com.amalto.core.query.user;

public class BinaryLogicOperator extends Condition {

    private Condition left;

    private final Predicate predicate;

    private Condition right;

    public BinaryLogicOperator(Condition left, Predicate predicate, Condition right) {
        this.left = left;
        this.predicate = predicate;
        this.right = right;
    }

    public Condition getLeft() {
        return left;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public Condition getRight() {
        return right;
    }

    public Expression normalize() {
        // If right or left is a no op condition, simplify a bit the query.
        if (right == UserQueryHelper.NO_OP_CONDITION) {
            return left.normalize();
        } else if (left == UserQueryHelper.NO_OP_CONDITION) {
            return right.normalize();
        } else {
            return this;
        }
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public void setLeft(Condition condition) {
        this.left = condition;
    }

    public void setRight(Condition condition) {
        this.right = condition;
    }
}
