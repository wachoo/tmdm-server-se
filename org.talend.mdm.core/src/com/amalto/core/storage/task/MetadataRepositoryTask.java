package com.amalto.core.storage.task;

import com.amalto.core.metadata.*;
import com.amalto.core.storage.Storage;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
abstract class MetadataRepositoryTask implements Task {

    protected final Storage storage;

    private final List<Task> tasks = new LinkedList<Task>();

    protected final MetadataRepository repository;

    protected double recordCount;

    protected long startTime;

    protected long endTime = -1;

    protected final AtomicBoolean startLock = new AtomicBoolean();

    protected final AtomicBoolean executionLock = new AtomicBoolean();

    protected final String id = UUID.randomUUID().toString();

    private double maxPerformance = Double.MIN_VALUE;

    private double minPerformance = Double.MAX_VALUE;

    private boolean isCancelled = false;

    private Task currentTypeTask;

    private final Object currentTypeTaskMonitor = new Object();

    MetadataRepositoryTask(Storage storage, MetadataRepository repository) {
        this.storage = storage;
        this.repository = repository;
    }

    protected abstract Task createTypeTask(ComplexTypeMetadata type);

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        synchronized (startLock) {
            startLock.set(true);
            startLock.notifyAll();
        }

        List<ComplexTypeMetadata> types = MetadataUtils.sortTypes(repository);

        for (ComplexTypeMetadata type : types) {
            Task task = createTypeTask(type);
            tasks.add(task);
        }

        startTime = System.currentTimeMillis();
        for (Task task : tasks) {
            synchronized (currentTypeTaskMonitor) {
                currentTypeTask = task;
            }
            if (!isCancelled) {
                System.out.println("--> Executing " + task + "...");
                task.execute(jobExecutionContext);
                recordCount += task.getRecordCount();
                System.out.println("<-- Executed (" + task.getRecordCount() + " record validated @ " + getCurrentPerformance() + " doc/s)");
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println("Staging migration done @" + getCurrentPerformance() + " doc/s.");

        synchronized (executionLock) {
            executionLock.set(true);
            executionLock.notifyAll();
        }
    }

    public String getId() {
        return id;
    }

    public double getRecordCount() {
        return recordCount;
    }

    public double getCurrentPerformance() {
        if (recordCount > 0) {
            float time;
            if (endTime > 0) {
                time = (endTime - startTime) / 1000f;
            } else {
                time = (System.currentTimeMillis() - startTime) / 1000f;
            }
            double currentPerformance = recordCount / time;
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
        synchronized (currentTypeTaskMonitor) {
            currentTypeTask.cancel();
            isCancelled = true;
        }
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
}
