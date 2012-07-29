// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.server;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.DefaultCommitter;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DefaultSaverSource;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceFactory;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.task.StagingTask;
import com.amalto.core.storage.task.Task;
import com.amalto.core.storage.task.TaskSubmitter;
import com.amalto.core.storage.task.TaskType;
import com.amalto.core.util.Util;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Map;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

class ServerImpl implements Server {

    private static final Logger LOGGER = Logger.getLogger(ServerImpl.class);

    private final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

    private StorageAdmin storageAdmin;

    private MetadataRepositoryAdmin metadataRepositoryAdmin;

    ServerImpl() {
    }

    public DataSource getDataSource(String dataSourceName, String container) {
        return DataSourceFactory.getInstance().getDataSource(dataSourceName, container);
    }

    public StorageAdmin getStorageAdmin() {
        if (storageAdmin == null) {
            ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
            storageAdmin = lifecycle.createStorageAdmin();
        }
        return storageAdmin;
    }

    public MetadataRepositoryAdmin getMetadataRepositoryAdmin() {
        if (metadataRepositoryAdmin == null) {
            ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
            metadataRepositoryAdmin = lifecycle.createMetadataRepositoryAdmin();
        }
        return metadataRepositoryAdmin;
    }

    public MBeanServer getMBeanServer() {
        return platformMBeanServer;
    }

    public void close() {
        ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
        lifecycle.destroyStorageAdmin(storageAdmin);
        lifecycle.destroyMetadataRepositoryAdmin(metadataRepositoryAdmin);
    }

    public void init() {
        if (MDMConfiguration.getConfiguration().get(DataSourceFactory.DB_DATASOURCES) == null) {
            LOGGER.warn("Server is not configured for SQL storage.");
            return;
        }

        try {
            LOGGER.info("Creating SQL storage for containers...");
            DataClusterCtrlLocal dataClusterControl = Util.getDataClusterCtrlLocal();
            Collection<DataClusterPOJOPK> allContainers = dataClusterControl.getDataClusterPKs(".*"); //$NON-NLS-1$
            StorageAdmin serverStorageAdmin = getStorageAdmin();

            Map<String, XSystemObjects> xDataClustersMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
            for (DataClusterPOJOPK container : allContainers) {
                if (!xDataClustersMap.containsKey(container.getUniqueId())) {
                    try {
                        LOGGER.info("Created SQL storage for container '" + container.getUniqueId() + "'.");
                        Storage user = serverStorageAdmin.create(container.getUniqueId(), container.getUniqueId(), Storage.DEFAULT_DATA_SOURCE_NAME);
                        Storage staging = serverStorageAdmin.get(container.getUniqueId() + StorageAdminImpl.STAGING_SUFFIX);
                        MetadataRepository stagingRepository = metadataRepositoryAdmin.get(container.getUniqueId() + StorageAdminImpl.STAGING_SUFFIX);
                        MetadataRepository userRepository = metadataRepositoryAdmin.get(container.getUniqueId());
                        ComplexTypeMetadata definedTaskType = stagingRepository.getComplexType("TALEND_TASK_DEFINITION"); //$NON-NLS-1$
                        UserQueryBuilder qb = from(definedTaskType).where(eq(definedTaskType.getField("completed"), "false")); //$NON-NLS-1$ //$NON-NLS-2$
                        for (DataRecord definedTask : staging.fetch(qb.getSelect())) {
                            String encodedTrigger = (String) definedTask.get("trigger"); //$NON-NLS-1$
                            // TODO Base64!
                            if (encodedTrigger != null && !encodedTrigger.trim().isEmpty()) {
                                /*
                                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decode(encodedTrigger));
                                ObjectInputStream ois = new ObjectInputStream(inputStream);
                                Trigger trigger = (Trigger) ois.readObject();
                                */
                                Trigger trigger = new SimpleTrigger("myStagingTask", "group");
                                Task task = createTask(definedTask, staging, user, stagingRepository, userRepository);
                                try {
                                    TaskSubmitter.getInstance().submit(task, trigger);
                                } catch (Exception e) {
                                    LOGGER.error("Could not resume task '" + task.getId() + "' due to exception.", e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Could not create SQL storage for container '" + container.getUniqueId() + "'.", e);
                    }
                }
            }
            LOGGER.info("Done.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Task createTask(DataRecord definedTask, Storage staging, Storage destination, MetadataRepository stagingRepository, MetadataRepository userRepository) {
        TaskType taskType = TaskType.valueOf(String.valueOf(definedTask.getType().getField("type"))); //$NON-NLS-1$
        switch (taskType) {
            case STAGING:
                TaskSubmitter taskSubmitter = TaskSubmitter.getInstance();
                SaverSource source = new DefaultSaverSource();
                SaverSession.Committer committer = new DefaultCommitter();
                return new StagingTask(taskSubmitter, staging, stagingRepository, userRepository, source, committer, destination, "TODO");
            default:
                throw new NotImplementedException("No support for task type '" + taskType + "'.");
        }
    }

}
