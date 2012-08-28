/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

    private final Closure closure;

    private final AtomicBoolean isCancelled = new AtomicBoolean(false);

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final String id = UUID.randomUUID().toString();

    private int count;

    private double success;

    private double fail;

    private long taskStartTime;

    public MultiThreadedTask(String name, Storage storage, Expression expression, int threadNumber, Closure closure) {
        this.name = name;
        this.storage = storage;
        this.expression = expression;
        this.closure = new ThreadDispatcher(threadNumber, closure);
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
            closure.begin();
            for (DataRecord record : records) {
                // Exit if cancelled.
                if (isCancelled.get()) {
                    break;
                }
                try {
                    closure.execute(record);
                    success++;
                } catch (Exception e) {
                    fail++;
                }
                count++;
            }
            closure.end();
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
    public int getProcessedRecords() {
        return (int) (success + fail);
    }

    @Override
    public String toString() {
        return getId() + '#' + name;
    }
}
