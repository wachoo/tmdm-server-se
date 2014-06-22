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

package com.amalto.core.server.jmx;

import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;

import javax.management.*;

public class StorageAdminImpl implements StorageAdmin {

    public StorageAdminImpl() {
    }

    private com.amalto.core.server.StorageAdmin getServerStorageAdmin() {
        return ServerContext.INSTANCE.get().getStorageAdmin();
    }

    public void create(String dataModelName) {
        com.amalto.core.server.StorageAdmin storageAdmin = getServerStorageAdmin();
        String dataSourceName = storageAdmin.getDatasource(dataModelName);
        storageAdmin.create(dataModelName, dataModelName, dataSourceName, null);
    }

    public void delete(String dataModelName) {
        com.amalto.core.server.StorageAdmin storageAdmin = getServerStorageAdmin();
        storageAdmin.delete(null, dataModelName, true);
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
        if ("create".equals(actionName)) {
            create((String) params[0]);
        } else if ("delete".equals(actionName)) {
            delete((String) params[0]);
        }
        return null;
    }

    public MBeanInfo getMBeanInfo() {
        MBeanOperationInfo[] operations;
        try {
            MBeanOperationInfo createOperation = new MBeanOperationInfo("Create", this.getClass().getMethod("create", String.class));
            MBeanOperationInfo deleteOperation = new MBeanOperationInfo("Delete", this.getClass().getMethod("delete", String.class));
            operations = new MBeanOperationInfo[]{createOperation, deleteOperation};
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return new MBeanInfo(this.getClass().getName(), "Storage Admin", new MBeanAttributeInfo[0], new MBeanConstructorInfo[0], operations, new MBeanNotificationInfo[0]);
    }
}
