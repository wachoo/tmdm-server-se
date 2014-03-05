/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.query.user;

import static com.amalto.core.query.user.UserQueryBuilder.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;

/**
 * A utility to slice a query into time slots (based on timestamp).
 */
public class TimeSlicer {

    private TimeSlicer() {
    }

    /**
     * <p>
     * "Slices" the <code>expression</code> into smaller queries depending on last modification timestamp. Each slice
     * contains all records for which last modification time was within <code>step</code>. For example:
     * </p>
     * <code>
     *     TimeSlicer.slice(expression, storage, 1, TimeUnit.HOURS)
     * </code>
     * <p>
     * returns a {@link java.util.Iterator iterator} of {@link com.amalto.core.query.user.Expression expressions} that
     * groups modifications done within the same hour.
     * </p>
     * 
     * @param expression The original expression to slice into queries on timestamp ranges.
     * @param storage Use to get the last modified record time (upper bound for timestamp) and the less modified record
     * (lower bound for timestamp).
     * @param step Indicates how long a slice can be (this is a maximum, last slice might be smaller). Parameter is
     * evaluated with <code>unit</code>.
     * @param unit The {@link java.util.concurrent.TimeUnit unit} for the <code>step</code>.
     * @return A {@link java.util.Iterator iterator} to all {@link com.amalto.core.query.user.TimeSlicer.Slice slices}
     * created based on <code>expression</code>.
     */
    public static Iterator<Slice> slice(Expression expression, Storage storage, long step, TimeUnit unit) {
        if (expression == null) {
            return null;
        }
        if (!(expression instanceof Select)) {
            Slice slice = new Slice(Long.MAX_VALUE, 0, expression);
            return Collections.singletonList(slice).iterator();
        }
        // Get the main type
        Select select = (Select) expression;
        ComplexTypeMetadata mainType = select.getTypes().get(0);
        // Get lower and upper timestamp bounds
        UserQueryBuilder upperBoundQuery = from(mainType).select(alias(max(timestamp()), "upper")); //$NON-NLS-1$
        UserQueryBuilder lowerBoundQuery = from(mainType).select(alias(min(timestamp()), "lower")); //$NON-NLS-1$
        StorageResults upperBoundResult = storage.fetch(upperBoundQuery.getSelect());
        long upperBound;
        try {
            upperBound = (Long) upperBoundResult.iterator().next().get("upper"); //$NON-NLS-1$
        } finally {
            upperBoundResult.close();
        }
        StorageResults lowerBoundResult = storage.fetch(lowerBoundQuery.getSelect());
        long lowerBound;
        try {
            lowerBound = (Long) upperBoundResult.iterator().next().get("lower"); //$NON-NLS-1$
        } finally {
            lowerBoundResult.close();
        }
        // Create the slice iterator
        return new SliceIterator(lowerBound, upperBound, unit.toMillis(step), select);
    }

    /**
     * Represents a slice of the original query: the <code>expression</code> to retrieve the records, the lower and upper bound
     * for the slice.
     */
    public static class Slice {

        private final Expression expression;

        private final long sliceLowerBound;

        private final long sliceUpperBound;

        public Slice(long sliceLowerBound, long sliceUpperBound, Expression expression) {
            this.sliceUpperBound = sliceUpperBound;
            this.sliceLowerBound = sliceLowerBound;
            this.expression = expression;
        }

        public Expression getExpression() {
            return expression;
        }

        public long getLowerBound() {
            return sliceLowerBound;
        }

        public long getUpperBound() {
            return sliceUpperBound;
        }
    }

    private static class SliceIterator implements Iterator<Slice> {

        private final long upperBound;

        private final Select select;

        private final long step;

        private long currentTimeStamp = 0;

        public SliceIterator(long lowerBound, long upperBound, long step, Select select) {
            this.currentTimeStamp = lowerBound;
            this.upperBound = upperBound;
            this.step = step;
            this.select = select;
        }

        @Override
        public boolean hasNext() {
            return currentTimeStamp < upperBound;
        }

        @Override
        public Slice next() {
            if (currentTimeStamp > upperBound) {
                throw new NoSuchElementException("No more step for timestamp.");
            }
            Slice slice;
            try {
                Select copy = select.copy();
                Condition previousCondition = copy.getCondition();
                Condition rangeCondition = and(gte(timestamp(), String.valueOf(currentTimeStamp)),
                        gte(timestamp(), String.valueOf(currentTimeStamp + step)));
                if (previousCondition == null) {
                    copy.setCondition(rangeCondition);
                } else {
                    copy.setCondition(and(previousCondition, rangeCondition));
                }
                slice = new Slice(currentTimeStamp, currentTimeStamp + step, copy);
            } finally {
                currentTimeStamp += step;
            }
            return slice;
        }

        @Override
        public void remove() {
            currentTimeStamp += step;
        }
    }
}
