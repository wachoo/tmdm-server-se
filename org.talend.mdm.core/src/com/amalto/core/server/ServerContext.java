// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageLogger;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.hibernate.HibernateStorage;

public class ServerContext {

    public static ServerContext INSTANCE = new ServerContext();

    private Server server;

    private ServerLifecycle serverLifecycle;

    private ServerContext() {
    }

    public synchronized void close() {
        serverLifecycle.destroyServer(server);
        server = null;
    }

    public synchronized Server get() {
        // Uncomment line below to enable JMX administration.
        // return get(new JMXLifecycle(new DefaultServerLifecycle()));
        return get(new DefaultServerLifecycle());
    }

    public synchronized Server get(ServerLifecycle lifecycle) {
        if (server == null) {
            init(lifecycle);
        }
        return server;
    }

    public ServerLifecycle getLifecycle() {
        return serverLifecycle;
    }

    private void init(ServerLifecycle lifecycle) {
        // Wrap the lifecycle with a implementation that creates/removes JMX MBeans. 
        serverLifecycle = lifecycle;
        server = serverLifecycle.createServer();
        server.init();
    }

    private static class DefaultServerLifecycle implements ServerLifecycle {
        public Server createServer() {
            return new ServerImpl();
        }

        public void destroyServer(Server server) {
            server.close();
        }

        public StorageAdmin createStorageAdmin() {
            return new StorageAdminImpl();
        }

        public void destroyStorageAdmin(StorageAdmin storageAdmin) {
            storageAdmin.close();
        }

        public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
            return new MetadataRepositoryAdminImpl();
        }

        public void destroyMetadataRepositoryAdmin(MetadataRepositoryAdmin metadataRepositoryAdmin) {
            metadataRepositoryAdmin.close();
        }

        public Storage createStorage(String storageName, String dataSourceName, StorageType storageType) {
            Storage storage = new HibernateStorage(storageName, storageType);
            storage = new SecuredStorage(storage, new SecuredStorage.UserDelegator() {
                public boolean hide(FieldMetadata field) {
                    return false;
                }

                public boolean hide(ComplexTypeMetadata type) {
                    return false;
                }
            });
            storage = new StorageLogger(storage);
            return storage;
        }

        public void destroyStorage(Storage storage, boolean dropExistingData) {
            storage.close(dropExistingData);
        }
    }
}