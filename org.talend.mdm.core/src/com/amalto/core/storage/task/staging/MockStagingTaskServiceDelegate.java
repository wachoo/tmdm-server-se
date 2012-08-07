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

package com.amalto.core.storage.task.staging;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;

public class MockStagingTaskServiceDelegate implements StagingTaskServiceDelegate {

    private final Object validationStartMonitor = new Object();

    private final StagingContainerSummary summary = new StagingContainerSummary();

    private final Map<String, ExecutionStatistics> previousExecutions = new HashMap<String, ExecutionStatistics>();

    ExecutionStatistics currentValidation;

    private static final long A_DAY = 1000 * 60 * 60 * 24;

    public MockStagingTaskServiceDelegate() {
        summary.setDataContainer("TestDataContainer");
        summary.setDataModel("TestDataModel");
        summary.setInvalidRecords(1000);
        summary.setTotalRecord(10000);
        summary.setValidRecords(8000);
        summary.setWaitingForValidation(1000);
        for (int i = 0; i < 2; i++) {
            ExecutionStatistics status = new ExecutionStatistics();
            status.setId(UUID.randomUUID().toString());
            status.setStartDate(new Date(System.currentTimeMillis() - (2 * i * A_DAY)));
            status.setEndDate(new Date(System.currentTimeMillis() - (i * A_DAY)));
            status.setTotalRecords(RandomUtils.nextInt(5000));
            status.setProcessedRecords(status.getTotalRecords());
            status.setInvalidRecords(RandomUtils.nextInt(1000));
            previousExecutions.put(status.getId(), status);
        }
    }

    @Override
    public StagingContainerSummary getContainerSummary() {
        return summary;
    }

    @Override
    public String startValidation() {
        synchronized (validationStartMonitor) {
            if (currentValidation == null) {
                currentValidation = new ExecutionStatistics(UUID.randomUUID().toString(), 0, new Date(System.currentTimeMillis()), null);
                currentValidation.setTotalRecords(10000);
            }
            return currentValidation.getId();
        }
    }

    @Override
    public StagingContainerSummary getContainerSummary(String dataContainer, String dataModel) {
        summary.setDataContainer(dataContainer);
        summary.setDataModel(dataModel);
        return summary;
    }

    @Override
    public String startValidation(String dataContainer, String dataModel) {
        return startValidation();
    }

    @Override
    public List<String> listCompletedExecutions(String dataContainer, Date beforeDate, int start, int size) {
        int to;
        if (size < 0) {
            to = previousExecutions.size();
        } else {
            to = start + size > previousExecutions.size() ? size : start + size;
        }
        return (new ArrayList<String>(previousExecutions.keySet())).subList(start - 1, Math.min(to, previousExecutions.size()));
    }

    @Override
    public ExecutionStatistics getExecutionStats(String dataContainer, String dataModel, String executionId) {
        return previousExecutions.get(executionId);
    }

    @Override
    public ExecutionStatistics getCurrentExecutionStats(String dataContainer, String dataModel) {
        synchronized (validationStartMonitor) {
            if (currentValidation == null) {
                return null;
            }
            currentValidation.setProcessedRecords(currentValidation.getProcessedRecords() + 10);
            currentValidation.setInvalidRecords(currentValidation.getInvalidRecords() + 5);
            return currentValidation;
        }
    }

    @Override
    public void cancelCurrentExecution(String dataContainer, String dataModel) {
        synchronized (validationStartMonitor) {
            currentValidation = null;
        }
    }
}
