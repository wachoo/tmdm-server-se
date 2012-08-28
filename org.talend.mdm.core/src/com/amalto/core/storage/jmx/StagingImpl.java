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

package com.amalto.core.storage.jmx;

import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.DefaultCommitter;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DefaultSaverSource;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.task.StagingTask;
import com.amalto.core.storage.task.Task;
import com.amalto.core.storage.task.TaskSubmitter;
import com.amalto.core.storage.task.TaskSubmitterFactory;
import org.jboss.logging.Logger;

import javax.management.*;

public class StagingImpl implements Staging {

    private static final Logger LOGGER = Logger.getLogger(StorageImpl.class);

    private final String storageName;

    private final SaverSource source;

    private final SaverSession.Committer committer;

    private final ClassLoader classLoader;

    public StagingImpl(String storageName) {
        this.storageName = storageName;
        source = new DefaultSaverSource();
        committer = new DefaultCommitter();
        classLoader = TaskSubmitter.class.getClassLoader();
    }

    public int getAvailableProcessorSlots() {
        return 0;
    }

    public int getInvalidRecords() {
        return 0;
    }

    public int getValidRecords() {
        return 0;
    }

    public int getNotProcessedRecords() {
        return 0;
    }

    public void createSubmitter() {
        try {
            Server server = ServerContext.INSTANCE.get();
            MetadataRepository userRepository = server.getMetadataRepositoryAdmin().get(storageName);
            MetadataRepository stagingRepository = server.getMetadataRepositoryAdmin().get(storageName + StorageAdmin.STAGING_SUFFIX);
            Storage origin = server.getStorageAdmin().get(storageName + StorageAdmin.STAGING_SUFFIX);
            Storage destination = server.getStorageAdmin().get(storageName);

            Task task = new StagingTask(TaskSubmitterFactory.getSubmitter(), origin, stagingRepository, userRepository, source, committer, destination);

            ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(classLoader);
                TaskSubmitterFactory.getSubmitter().submit(task);
            } finally {
                Thread.currentThread().setContextClassLoader(previousClassLoader);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred during create staging submitter.", e);
            throw new RuntimeException(e);
        }
    }

    public void createCleaner() {
    }

    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return null;
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    }

    public AttributeList getAttributes(String[] attributes) {
        return new AttributeList(0);
    }

    public AttributeList setAttributes(AttributeList attributes) {
        return attributes;
    }

    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        if ("createSubmitter".equals(actionName)) {  //$NON-NLS-1$
            try {
                createSubmitter();
            } catch (Exception e) {
                e.printStackTrace();
                throw new MBeanException(e);
            }
        }
        return null;
    }

    public MBeanInfo getMBeanInfo() {
        MBeanOperationInfo[] operations;
        try {
            MBeanOperationInfo restartOperation = new MBeanOperationInfo("Create submitter", this.getClass().getMethod("createSubmitter")); //$NON-NLS-1$ //$NON-NLS-2$
            operations = new MBeanOperationInfo[]{restartOperation};
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new MBeanInfo(this.getClass().getName(), "Staging " + storageName, new MBeanAttributeInfo[0], new MBeanConstructorInfo[0], operations, new MBeanNotificationInfo[0]);
    }
}
