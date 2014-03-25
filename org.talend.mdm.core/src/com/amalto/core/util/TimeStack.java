// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage a stack of time events. These events could be start, step or pause.
 */
class TimeStack {

    private List<TimeElement> steps;

    public TimeStack() {
        steps = new ArrayList<TimeElement>();
        steps.add(new TimeElement());
    }

    public void addStep(boolean pause) {
        steps.add(new TimeElement(pause));
    }

    public boolean hasManySteps() {
        return steps.size() > 1;
    }

    public long getTotalElapsedTime() {
        long currentTime = System.currentTimeMillis();
        long toReturn = 0;
        for (int i = steps.size() - 1; i >= 0; i--) {
            TimeElement current = steps.get(i);
            if (!current.pause) {
                toReturn += currentTime - current.start;
            }
            currentTime = current.start;
        }
        return toReturn;
    }

    public long getElapsedTimeSinceLastRequest() {
        long currentTime = System.currentTimeMillis();
        for (int i = steps.size() - 1; i >= 0; i--) {
            TimeElement current = steps.get(i);
            if (!current.pause) {
                return currentTime - current.start;
            } else {
                currentTime = current.start;
            }
        }
        return -1;
    }

    /**
     * Represents an element on the stack.
     */
    private static class TimeElement {

        public long start; // date when this event occurs

        public boolean pause; // indicates if this element is a pause (true) or a start or step (false)

        public TimeElement(long start, boolean pause) {
            this.start = start;
            this.pause = pause;
        }

        public TimeElement() {
            this(System.currentTimeMillis(), false);
        }

        public TimeElement(boolean pause) {
            this(System.currentTimeMillis(), pause);
        }

    }
}
