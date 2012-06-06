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
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageErrorDump;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.jmx.StagingImpl;
import com.amalto.core.storage.jmx.StorageImpl;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class ServerContext {

    public static ServerContext INSTANCE = new ServerContext();

    private Server server;

    private ServerLifecycle serverLifecycle;

    private ServerContext() {
    }

    public synchronized void close() {
        server.close();
        server = null;
    }

    public synchronized Server get() {
        return get(new JMXLifecycle(new DefaultServerLifecycle()));
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

        public Storage createStorage(String storageName, String dataSourceName, HibernateStorage.StorageType storageType) {
            Storage storage = new HibernateStorage(storageName, storageType);
            storage = new SecuredStorage(storage, new SecuredStorage.UserDelegator() {
                public boolean hide(FieldMetadata field) {
                    return false;
                }

                public boolean hide(ComplexTypeMetadata type) {
                    return false;
                }
            });
            // TODO This is a prototype, but we should be able configure this dump on error behavior
            storage = new StorageErrorDump(storage);
            return storage;
        }

        public void destroyStorage(Storage storage) {
            storage.close();
        }
    }

    class JMXLifecycle implements ServerLifecycle {
        ServerLifecycle delegate;

        JMXLifecycle(ServerLifecycle delegate) {
            if (delegate == null) {
                throw new IllegalArgumentException("Delegate can not be null.");
            }
            this.delegate = delegate;
        }

        public Server createServer() {
            return delegate.createServer();
        }

        public void destroyServer(Server server) {
            delegate.destroyServer(server);
        }

        public StorageAdmin createStorageAdmin() {
            StorageAdmin storageAdmin = delegate.createStorageAdmin();
            try {
                MBeanServer mBeanServer = server.getMBeanServer();
                ObjectName objectName = getStorageAdmin();

                // Register Storage admin MBean
                if (mBeanServer.queryMBeans(objectName, null).isEmpty()) {
                    com.amalto.core.server.jmx.StorageAdmin jmxStorageAdmin = new com.amalto.core.server.jmx.StorageAdminImpl();
                    mBeanServer.registerMBean(jmxStorageAdmin, objectName);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return storageAdmin;
        }

        public void destroyStorageAdmin(StorageAdmin storageAdmin) {
            delegate.destroyStorageAdmin(storageAdmin);
            try {
                MBeanServer mBeanServer = server.getMBeanServer();
                ObjectName stagingObjectName = getStorageAdmin();

                if (!mBeanServer.queryMBeans(stagingObjectName, null).isEmpty()) {
                    // Unregister Storage admin MBean
                    mBeanServer.unregisterMBean(stagingObjectName);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
            return delegate.createMetadataRepositoryAdmin();
        }

        public void destroyMetadataRepositoryAdmin(MetadataRepositoryAdmin metadataRepositoryAdmin) {
            delegate.destroyMetadataRepositoryAdmin(metadataRepositoryAdmin);
        }

        public Storage createStorage(String storageName, String dataSourceName, HibernateStorage.StorageType storageType) {
            Storage storage = delegate.createStorage(storageName, dataSourceName, storageType);

            try {
                MBeanServer mBeanServer = server.getMBeanServer();
                ObjectName storageObjectName = getStorageMBeanName(storageName);
                ObjectName stagingObjectName = getStagingObjectName(storageName);

                if (mBeanServer.queryMBeans(storageObjectName, null).isEmpty()) {
                    // Register Storage MBean
                    com.amalto.core.storage.jmx.Storage jmxStorageAdmin = new StorageImpl(storageName);
                    mBeanServer.registerMBean(jmxStorageAdmin, storageObjectName);

                    // Register Staging MBean
                    com.amalto.core.storage.jmx.Staging staging = new StagingImpl(storageName);
                    mBeanServer.registerMBean(staging, stagingObjectName);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return storage;
        }

        public void destroyStorage(Storage storage) {
            delegate.destroyStorage(storage);

            try {
                String storageName = storage.getName();
                MBeanServer mBeanServer = server.getMBeanServer();
                ObjectName storageObjectName = getStorageMBeanName(storageName);
                ObjectName stagingObjectName = getStagingObjectName(storageName);

                if (!mBeanServer.queryMBeans(storageObjectName, null).isEmpty()) {
                    // Unregister Storage MBean
                    mBeanServer.unregisterMBean(storageObjectName);
                    // Unregister Staging MBean
                    mBeanServer.unregisterMBean(stagingObjectName);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private ObjectName getStorageAdmin() throws MalformedObjectNameException {
            return new ObjectName("MDM:type=Storages,name=Storage Admin"); //$NON-NLS-1$
        }

        private ObjectName getStagingObjectName(String storageName) throws MalformedObjectNameException {
            return new ObjectName("MDM:type=Storages,name=Staging " + storageName); //$NON-NLS-1$
        }

        private ObjectName getStorageMBeanName(String storageName) throws MalformedObjectNameException {
            return new ObjectName("MDM:type=Storages,name=Storage " + storageName); //$NON-NLS-1$
        }
    }

}