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

    private String name;
    private final Storage storage;

    private final Expression expression;

    private final Closure closure;

    private final AtomicBoolean isCancelled = new AtomicBoolean(false);

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final String id = UUID.randomUUID().toString();

    private double minPerformance = Double.MAX_VALUE;

    private double maxPerformance = Double.MIN_VALUE;

    private double count;

    private double success;

    private double fail;

    private long taskStartTime;

    public MultiThreadedTask(String name, Storage storage, Expression expression, Closure closure) {
        this(name, storage, expression, 3, closure);
    }

    public MultiThreadedTask(String name, Storage storage, Expression expression, int threadNumber, Closure closure) {
        this.name = name;
        this.storage = storage;
        this.expression = expression;
        this.closure = new ThreadDispatcher(threadNumber, closure);
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        synchronized (startLock) {
            startLock.set(true);
            startLock.notifyAll();
        }

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

        synchronized (executionLock) {
            executionLock.set(true);
            executionLock.notifyAll();
        }
    }

    public String getId() {
        return id;
    }

    public double getRecordCount() {
        return count;
    }

    public double getCurrentPerformance() {
        if (count > 0) {
            long time = Math.abs(System.currentTimeMillis() - taskStartTime) / 1000;
            double currentPerformance = count / time;
            minPerformance = Math.min(minPerformance, currentPerformance);
            maxPerformance = Math.max(maxPerformance, currentPerformance);
            return currentPerformance;
        } else {
            return 0;
        }
    }

    public double getMinPerformance() {
        return minPerformance;
    }

    public double getMaxPerformance() {
        return maxPerformance;
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
    public String toString() {
        return getId() + '#' + name;
    }
}
