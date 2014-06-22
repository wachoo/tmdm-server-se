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

import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.storage.Storage;

public class TaskFactory {

    public static Task createStagingTask(StagingConfiguration config) {
        Storage stagingStorage = config.getOrigin();
        MetadataRepository stagingRepository = config.getStagingRepository();

        return new StagingTask(TaskSubmitterFactory.getSubmitter(),
                stagingStorage,
                stagingRepository,
                config.getUserRepository(),
                config.getSource(),
                config.getCommitter(),
                config.getDestination());
    }
}
