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

        private boolean isRange;

        private Expression rangeStart;

        private Expression rangeEnd;

        private TypedExpression rangeExpression;

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
            }

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

        @Override
        public Condition visit(Compare condition) {
            Predicate predicate = condition.getPredicate();
            if (predicate == Predicate.GREATER_THAN_OR_EQUALS) {
                rangeExpression = (TypedExpression) condition.getLeft();  //TODO What if left isn't a TypedExpression?
                rangeStart = condition.getRight();
            }
            if (predicate == Predicate.LOWER_THAN_OR_EQUALS) {
                isRange = rangeExpression != null && rangeExpression.equals(condition.getLeft());
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
            rangeExpression = null;
            rangeStart = null;
            rangeEnd = null;
        }
    }
}
