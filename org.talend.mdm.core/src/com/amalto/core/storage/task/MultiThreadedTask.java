/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.task;

import com.amalto.core.query.user.Expression;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class MultiThreadedTask implements Task {

    private final String name;

    private final Storage storage;

    private final Expression expression;

    private final ClosureExecutionStats stats;

    private final Closure closure;

    private final AtomicBoolean isCancelled = new AtomicBoolean(false);

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final String id = UUID.randomUUID().toString();

    private int count;

    private long taskStartTime;

    private boolean isFinished;

    public MultiThreadedTask(String name,
                             Storage storage,
                             Expression expression,
                             int threadNumber,
                             Closure closure,
                             ClosureExecutionStats stats) {
        this.name = name;
        this.storage = storage;
        this.expression = expression;
        this.stats = stats;
        this.closure = new ThreadDispatcher(threadNumber, closure, stats);
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            run();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    @Override
    public void run() {
        synchronized (startLock) {
            startLock.set(true);
            startLock.notifyAll();
        }

        try {
            taskStartTime = System.currentTimeMillis();
            StorageResults records = storage.fetch(expression);
            if (records.getCount() > 0) {
                closure.begin();
                for (DataRecord record : records) {
                    // Exit if cancelled.
                    if (isCancelled.get()) {
                        break;
                    }
                    closure.execute(record, stats);
                    count++;
                }
                closure.end(stats);
            }
            isFinished = true;
        } finally {
            synchronized (executionLock) {
                executionLock.set(true);
                executionLock.notifyAll();
            }
        }
    }

    public String getId() {
        return id;
    }

    public int getRecordCount() {
        return count;
    }

    @Override
    public int getErrorCount() {
        return stats.getErrorCount();
    }

    public double getPerformance() {
        if (count > 0) {
            long time = Math.abs(System.currentTimeMillis() - taskStartTime) / 1000;
            return count / time;
        } else {
            return 0;
        }
    }

    public void cancel() {
        isCancelled.set(true);
        closure.cancel();
    }

    public void waitForCompletion() throws InterruptedException {
        while (!startLock.get()) {
            synchronized (startLock) {
                startLock.wait();
            }
        }
        while (!executionLock.get()) {
            synchronized (executionLock) {
                executionLock.wait();
            }
        }
    }

    @Override
    public long getStartDate() {
        return taskStartTime;
    }

    @Override
    public boolean hasFinished() {
        return isCancelled.get() || isFinished;
    }

    @Override
    public int getProcessedRecords() {
        return stats.getSuccessCount() + stats.getErrorCount();
    }

    @Override
    public String toString() {
        return getId() + '#' + name;
    }
}
