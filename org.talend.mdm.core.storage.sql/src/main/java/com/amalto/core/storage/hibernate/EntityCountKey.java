/*
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.Select;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;

public class EntityCountKey {

    private String storageName;

    private StorageType storageType;

    private String entityName;

    private Condition condition;

    public EntityCountKey(Storage storage, Select select) {
        this(storage.getName(), storage.getType(), select.getTypes().get(0).getName(), select.getCondition());
    }

    public EntityCountKey(String storageName, StorageType storageType, String entityName, Condition condition) {
        this.storageName = storageName;
        this.storageType = storageType;
        this.entityName = entityName;
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getStorageName() {
        return storageName;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    @Override
    public String toString() {
        String entityKey = EntityCountUtil.getEntityKey(storageName, storageType, entityName);
        int conditionHash = condition != null ? condition.hashCode() : 0;
        return entityKey + conditionHash;
    }

}