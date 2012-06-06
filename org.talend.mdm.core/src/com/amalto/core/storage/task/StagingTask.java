package com.amalto.core.storage.task;

import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.storage.Storage;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class StagingTask implements Task {

    private final TaskSubmitter taskSubmitter;

    private final String id;

    private boolean isCancelled = false;

    private final List<MetadataRepositoryTask> tasks;

    private final AtomicBoolean startLock = new AtomicBoolean();

    private final AtomicBoolean executionLock = new AtomicBoolean();

    private final Object currentTaskMonitor = new Object();

    private MetadataRepositoryTask currentTask;

    public StagingTask(TaskSubmitter taskSubmitter, Storage stagingStorage, MetadataRepository repository, SaverSource source, SaverSession.Committer committer, Storage destinationStorage) {
        this.taskSubmitter = taskSubmitter;
        id = UUID.randomUUID().toString();
        tasks = Arrays.asList(new ClusterTask(stagingStorage, repository),
                new MergeTask(stagingStorage, repository),
                new MDMValidationTask(stagingStorage, destinationStorage, repository, source, committer));
                // new DSCUpdaterTask(stagingStorage, destinationStorage, repository));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getRecordCount() {
        return 0;
    }

    @Override
    public double getCurrentPerformance() {
        return 0;
    }

    @Override
    public double getMinPerformance() {
        return 0;
    }

    @Override
    public double getMaxPerformance() {
        return 0;
    }

    @Override
    public void cancel() {
        synchronized (currentTaskMonitor) {
            isCancelled = true;
            if (currentTask != null) {
                currentTask.cancel();
            }
        }
    }

    @Override
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
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        synchronized (startLock) {
            startLock.set(true);
            startLock.notifyAll();
        }

        for (MetadataRepositoryTask task : tasks) {
            synchronized (currentTaskMonitor) {
                if(isCancelled) {
                    break;
                }
                currentTask = task;
            }
            System.out.println("--> " + task.toString());
            taskSubmitter.submitAndWait(currentTask);
            System.out.println("<-- DONE " + task.toString());
        }

        synchronized (executionLock) {
            executionLock.set(true);
            executionLock.notifyAll();
        }
    }
}
