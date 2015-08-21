// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.storage.task.staging;

import java.util.List;

import org.apache.log4j.Logger;

import com.amalto.core.storage.task.ClosureExecutionStats;
import com.amalto.core.storage.task.StagingTask;

public abstract class AbstractStagingTaskManager implements StagingTaskManager {
    
    private static final Logger LOGGER = Logger.getLogger(AbstractStagingTaskManager.class);
    
    protected StagingTaskRepository repository;
    
    @Override
    public void taskStarted(StagingTask task) {
        final String dataContainer = task.getDataContainer();
        final String id = task.getId();
        final long startTime = task.getStartDate();
        this.getRepository().saveNewTask(dataContainer, id, startTime);
    }
    
    @Override
    public void taskCompleted(StagingTask task, ClosureExecutionStats stats){
        final String dataContainer = task.getDataContainer();
        final String taskId = task.getId();
        final long endMatchTime = stats.getEndMatchTime();
        final int errorCount = task.getErrorCount();
        final int recordCount = task.getRecordCount();
        this.getRepository().saveTaskAsCompleted(dataContainer, taskId, endMatchTime, errorCount, recordCount);
    }
    
    @Override
    public List<String> listCompletedExecutions(String dataContainer, String beforeDate, int start, int size){
        return this.getRepository().listCompletedExecutions(dataContainer, beforeDate, start, size);
    }
    
    public StagingTaskRepository getRepository() {
        return repository;
    }

    
    public void setRepository(StagingTaskRepository repository) {
        this.repository = repository;
    }
}
