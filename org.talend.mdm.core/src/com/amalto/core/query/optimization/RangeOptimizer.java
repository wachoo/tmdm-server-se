package com.amalto.core.query.optimization;

import com.amalto.core.query.user.*;

public class RangeOptimizer extends Optimizer {

    private static final RangeOptimization RANGE_OPTIMIZATION = new RangeOptimization();

    public RangeOptimizer() {
        super();
    }

    @Override
    protected void doOptimize(Select select) {
        synchronized (RANGE_OPTIMIZATION) {
            try {
                Condition condition = select.getCondition();
                if (condition != null) {
                    Condition optimizedCondition = condition.accept(RANGE_OPTIMIZATION);
                    select.setCondition(optimizedCondition);
                }
            } finally {
                RANGE_OPTIMIZATION.reset();
            }
        }
    }

    private static class RangeOptimization extends VisitorAdapter<Condition> {

        private Expression rangeStart;

        private Expression rangeEnd;

        @Override
        public Condition visit(Select select) {
            return select.getCondition().accept(this);
        }

        @Override
        public Condition visit(Isa isa) {
            return isa;
        }

        @Override
        public Condition visit(UnaryLogicOperator condition) {
            return condition;
        }

        @Override
        public Condition visit(IsEmpty isEmpty) {
            return isEmpty;
        }

        @Override
        public Condition visit(IsNull isNull) {
            return isNull;
        }

        @Override
        public Condition visit(NotIsEmpty notIsEmpty) {
            return notIsEmpty;
        }

        @Override
        public Condition visit(NotIsNull notIsNull) {
            return notIsNull;
        }

        @Override
        public Condition visit(BinaryLogicOperator condition) {
            // TODO OR is a (not(range())) is start and end does not intersect
            if (condition.getLeft() instanceof Compare && condition.getRight() instanceof Compare) {
                Compare left = (Compare) condition.getLeft();
                Compare right = (Compare) condition.getRight();
                // Only takes into account when left == right
                if (condition.getPredicate() == Predicate.AND && left.getLeft().equals(right.getLeft())) {
                    condition.getLeft().accept(this);
                    condition.getRight().accept(this);
                    if (rangeStart != null && rangeEnd != null) {
                        return new Range(((TypedExpression) left.getLeft()), rangeStart, rangeEnd);
                    } else {
                        return condition;
                    }
                }
            } else {
                condition.setLeft(condition.getLeft().accept(this));
                condition.setRight(condition.getRight().accept(this));
            }
            return condition;
        }

        @Override
        public Condition visit(Compare condition) {
            Predicate predicate = condition.getPredicate();
            if (predicate == Predicate.GREATER_THAN_OR_EQUALS) {
                rangeStart = condition.getRight();
            }
            if (predicate == Predicate.LOWER_THAN_OR_EQUALS) {
                rangeEnd = condition.getRight();
            }
            return condition;
        }

        @Override
        public Condition visit(Range range) {
            return range;
        }

        @Override
        public Condition visit(Condition condition) {
            return condition;
        }

        @Override
        public Condition visit(FullText fullText) {
            return fullText;
        }

        @Override
        public Condition visit(FieldFullText fieldFullText) {
            return fieldFullText;
        }

        public void reset() {
            rangeStart = null;
            rangeEnd = null;
        }
    }
}
