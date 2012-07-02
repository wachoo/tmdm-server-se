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

import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import org.jboss.logging.Logger;

import javax.management.*;

public class StorageImpl implements Storage {

    private static final Logger LOGGER = Logger.getLogger(StorageImpl.class);

    private final String storageName;

    public StorageImpl(String storageName) {
        this.storageName = storageName;
    }

    public void restart() {
        Server server = ServerContext.INSTANCE.get();
        StorageAdmin storageAdmin = server.getStorageAdmin();
        MetadataRepositoryAdmin repositoryAdmin = server.getMetadataRepositoryAdmin();

        try {
            storageAdmin.get(storageName).prepare(repositoryAdmin.get(storageName), true, true);
        } catch (Exception e) {
            LOGGER.error("Exception occurred during restart.", e);
            throw new RuntimeException(e);
        }
    }

    public void reindex() {
        Server server = ServerContext.INSTANCE.get();
        StorageAdmin storageAdmin = server.getStorageAdmin();
        try {
            storageAdmin.get(storageName).reindex();
        } catch (Exception e) {
            LOGGER.error("Exception occurred during reindex.", e);
            throw new RuntimeException(e);
        }
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
        if ("restart".equals(actionName)) { //$NON-NLS-1$
            try {
                restart();
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
            MBeanOperationInfo restartOperation = new MBeanOperationInfo("Restart", this.getClass().getMethod("restart")); //$NON-NLS-1$ //$NON-NLS-2$
            operations = new MBeanOperationInfo[]{restartOperation};
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return new MBeanInfo(this.getClass().getName(), "Storage " + storageName, new MBeanAttributeInfo[0], new MBeanConstructorInfo[0], operations, new MBeanNotificationInfo[0]);
    }
}
