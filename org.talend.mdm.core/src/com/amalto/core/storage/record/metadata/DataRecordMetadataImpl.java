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

package com.amalto.core.storage.record.metadata;

import java.util.HashMap;
import java.util.Map;

public class DataRecordMetadataImpl implements DataRecordMetadata {

    private long lastModificationTime;

    private String taskId;
    private HashMap<String,String> recordProperties;

    public DataRecordMetadataImpl(long lastModificationTime, String taskId) {
        this.lastModificationTime = lastModificationTime;
        this.taskId = taskId;
    }

    public long getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(long lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<String, String> getRecordProperties() {
        if (recordProperties == null) {
            recordProperties = new HashMap<String, String>();
        }
        return recordProperties;
    }
}
