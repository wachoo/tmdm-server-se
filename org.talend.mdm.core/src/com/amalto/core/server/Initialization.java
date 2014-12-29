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

package com.amalto.core.server;

import static com.amalto.core.query.user.UserQueryBuilder.from;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.metadata.ClassRepository;
import com.amalto.core.objects.configurationinfo.assemble.AssembleConcreteBuilder;
import com.amalto.core.objects.configurationinfo.assemble.AssembleDirector;
import com.amalto.core.objects.configurationinfo.assemble.AssembleProc;
import com.amalto.core.objects.datacluster.DataClusterPOJO;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.security.SecurityConfig;
import com.amalto.core.storage.DispatchWrapper;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.util.Version;

public class Initialization implements InitializingBean {

    @Autowired(required = true)
    private ServerLifecycle serverLifecycle;

    protected static final Logger LOGGER = Logger.getLogger(Initialization.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        String version = Version.getSimpleVersionAsString(Initialization.class);
        LOGGER.info("=======================================================");
        LOGGER.info("Talend MDM " + version);
        LOGGER.info("=======================================================");
        // Set directory (to find configuration files).
        String homeDirectory = this.getClass().getResource("/").getPath(); //$NON-NLS-1$
        LOGGER.info("Setting home directory to: " + homeDirectory);
        System.setProperty("jboss.server.home.dir", homeDirectory); //$NON-NLS-1$
        // Initializes server now
        if (serverLifecycle == null) {
            throw new IllegalStateException("Server lifecycle is not set (is server running on a supported platform?)");
        }
        Server server = ServerContext.INSTANCE.get(serverLifecycle);
        server.init();
        // Initialize system storage
        LOGGER.info("Starting system storage...");
        StorageAdmin storageAdmin = server.getStorageAdmin();
        String systemDataSourceName = storageAdmin.getDatasource(StorageAdmin.SYSTEM_STORAGE);
        storageAdmin.create(StorageAdmin.SYSTEM_STORAGE, StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM, systemDataSourceName);
        Storage systemStorage = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM);
        if (systemStorage == null) {
            LOGGER.error("System storage could not start.");
            throw new IllegalStateException("Could not start server (unable to initialize system storage).");
        } else {
            LOGGER.info("System storage started.");
        }
        // Migration
        LOGGER.info("Initialization and migration of system database...");
        AssembleConcreteBuilder concreteBuilder = new AssembleConcreteBuilder();
        AssembleDirector director = new AssembleDirector(concreteBuilder);
        director.constructAll();
        final AssembleProc assembleProc = concreteBuilder.getAssembleProc();
        SecurityConfig.invokeSynchronousPrivateInternal(new Runnable() {
			
			@Override
			public void run() {
				assembleProc.run();
			}
		});
        
        LOGGER.info("Initialization and migration done.");
        // Find configured containers
        MetadataRepository repository = systemStorage.getMetadataRepository();
        String className = StringUtils.substringAfterLast(DataClusterPOJO.class.getName(), "."); //$NON-NLS-1$
        ComplexTypeMetadata containerType = repository.getComplexType(ClassRepository.format(className));
        UserQueryBuilder qb = from(containerType);
        Set<String> containerNames = new HashSet<String>();
        systemStorage.begin();
        try {
            StorageResults containers = systemStorage.fetch(qb.getSelect());
            for (DataRecord container : containers) {
                String name = String.valueOf(container.get("name"));
                if (!DispatchWrapper.isMDMInternal(name)) {
                    containerNames.add(name);
                }
            }
            systemStorage.commit();
        } catch (Exception e) {
            systemStorage.rollback();
            throw new RuntimeException("Could not list configured containers", e);
        }
        // Log configured containers
        StringBuilder containerNamesLog = new StringBuilder();
        containerNamesLog.append('[').append(' ');
        for (String containerName : containerNames) {
            containerNamesLog.append(containerName).append(' ');
        }
        containerNamesLog.append(']');
        LOGGER.info("Container to initialize (" + containerNames.size() + " found) : " + containerNamesLog);
        // Initialize configured containers
        int i = 1;
        for (String containerName : containerNames) {
            LOGGER.info("Starting storage " + containerName + "(" + i + " of " + containerNames.size() + ") ...");
            try {
                String datasource = storageAdmin.getDatasource(containerName);
                DataSourceDefinition dataSourceDefinition = server.getDefinition(datasource, containerName);
                storageAdmin.create(containerName, containerName, StorageType.MASTER, datasource);
                if(dataSourceDefinition.hasStaging()) {
                    storageAdmin.create(containerName, containerName, StorageType.STAGING, datasource);
                }
                LOGGER.info("Storage " + containerName + " started.");
            } catch (Exception e) {
                LOGGER.warn("Skipping container '" + containerName + "'.");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Skipping container '" + containerName + "' due to exception.", e);
                }
            }
            i++;
        }
        LOGGER.info("Talend MDM " + version + " started.");
    }
}
