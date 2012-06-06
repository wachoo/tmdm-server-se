package com.amalto.core.query.optimization;

import com.amalto.core.query.user.Select;

/**
 *
 */
public abstract class Optimizer {

    private static final NoOpOptimizer NO_OP_OPTIMIZER = new NoOpOptimizer();

    private final Optimizer next;

    public Optimizer(Optimizer next) {
        this.next = next;
    }

    Optimizer() {
        this.next = NO_OP_OPTIMIZER;
    }

    public void optimize(Select select) {
        doOptimize(select);
        next.optimize(select);
    }

    protected abstract void doOptimize(Select select);

    private static class NoOpOptimizer extends Optimizer {
        @Override
        public void doOptimize(Select select) {
            // Do nothing
        }

        @Override
        public void optimize(Select select) {
            // Do nothing
        }
    }
}
