package com.amalto.core.query.optimization;

import com.amalto.core.query.user.*;

public class RangeOptimizer extends Optimizer {

    public RangeOptimizer() {
        super();
    }

    @Override
    protected void doOptimize(Select select) {
        Condition optimizedCondition = select.getCondition().accept(new RangeOptimization());
        select.setCondition(optimizedCondition);
    }

    private static class RangeOptimization extends VisitorAdapter<Condition> {

        private boolean isRange;

        private Expression rangeStart;

        private Expression rangeEnd;

        private Expression rangeExpression;

        @Override
        public Condition visit(Select select) {
            return select.getCondition().accept(this);
        }

        @Override
        public Condition visit(BinaryLogicOperator condition) {
            if (condition.getPredicate() == Predicate.AND) {
                return processCondition(condition);
            } else if (condition.getPredicate() == Predicate.OR) {
                // TODO OR is a (not(range())) is start and end does not intersect
                condition.setLeft(condition.getLeft().accept(this));
                condition.setRight(condition.getRight().accept(this));
            } else {
                condition.setLeft(condition.getLeft().accept(this));
                condition.setRight(condition.getRight().accept(this));
            }

            return condition;
        }

        private Condition processCondition(BinaryLogicOperator condition) {
            condition.getLeft().accept(this);
            condition.getRight().accept(this);

            if (isRange) {
                isRange = false;
                return new Range(rangeExpression, rangeStart, rangeEnd);
            } else {
                // TODO This is a bit ugly but works
                // Try the other way in case lower than is declared before greater than
                condition.getRight().accept(this);
                condition.getLeft().accept(this);
                if (isRange) {
                    isRange = false;
                    return new Range(rangeExpression, rangeStart, rangeEnd);
                }

                return condition;
            }
        }

        @Override
        public Condition visit(Compare condition) {
            if (condition.getPredicate() == Predicate.GREATER_THAN) {
                if (condition.getLeft() instanceof Field) {
                    rangeExpression = condition.getLeft();
                    rangeStart = condition.getRight();
                }
            }
            if (condition.getPredicate() == Predicate.LOWER_THAN) {
                if (condition.getLeft() instanceof Field) {
                    isRange = rangeExpression == condition.getLeft();
                    rangeEnd = condition.getRight();
                }
            }

            return condition;
        }
    }
}
