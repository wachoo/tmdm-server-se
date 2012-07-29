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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.quartz.Trigger;

import java.util.UUID;

public class TaskFactory {

    public interface ExecutionConfiguration {
        Trigger createTrigger();
    }

    public static TaskExecutor createStagingTask(StagingConfiguration config, ExecutionConfiguration executionConfig) {
        Storage stagingStorage = config.getOrigin();
        MetadataRepository stagingRepository = config.getStagingRepository();
        ComplexTypeMetadata taskDefinitionType = stagingRepository.getComplexType("TALEND_TASK_DEFINITION");

        String taskDefinitionId = UUID.randomUUID().toString();
        StagingTask task = new StagingTask(TaskSubmitter.getInstance(),
                stagingStorage,
                stagingRepository,
                config.getUserRepository(),
                config.getSource(),
                config.getCommitter(),
                config.getDestination(),
                taskDefinitionId);
        Trigger trigger = executionConfig.createTrigger();

        try {
            DataRecord taskDefinition = new DataRecord(taskDefinitionType, UnsupportedDataRecordMetadata.INSTANCE);
            taskDefinition.set(taskDefinitionType.getField("id"), taskDefinitionId);
            // TODO Base64
            /*
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(trigger);
                taskDefinition.set(taskDefinitionType.getField("trigger"), Base64.encode(bos.toByteArray()));
            } catch (IOException e) {
                throw new RuntimeException("Could not save trigger", e);
            }
            */
            taskDefinition.set(taskDefinitionType.getField("completed"), false);
            taskDefinition.set(taskDefinitionType.getField("type"), TaskType.STAGING.name());

            stagingStorage.begin();
            stagingStorage.update(taskDefinition);
            stagingStorage.commit();
        } catch (Exception e) {
            stagingStorage.rollback();
            throw new RuntimeException("Could not save task definition.", e);
        }

        return new TaskExecutor(task, trigger);
    }
}
