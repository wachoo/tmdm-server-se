package com.amalto.core.query.user;

public class BinaryLogicOperator implements Condition {

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
        left = (Condition) left.normalize();
        right = (Condition) right.normalize();
        // If left and right are same (cond1 OR cond1 = cond1 / cond1 AND cond1 = cond1), return one of the side
        if (left.equals(right)) {
            return left;
        }
        // If right or left is a no op condition, simplify a bit the query.
        if (right == UserQueryHelper.NO_OP_CONDITION && left == UserQueryHelper.NO_OP_CONDITION) {
            return UserQueryHelper.NO_OP_CONDITION;
        } else if (right == UserQueryHelper.NO_OP_CONDITION) {
            return left;
        } else if (left == UserQueryHelper.NO_OP_CONDITION) {
            return right;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BinaryLogicOperator)) {
            return false;
        }
        BinaryLogicOperator that = (BinaryLogicOperator) o;
        if (left != null ? !left.equals(that.left) : that.left != null) {
            return false;
        }
        if (predicate != null ? !predicate.equals(that.predicate) : that.predicate != null) {
            return false;
        }
        if (right != null ? !right.equals(that.right) : that.right != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
