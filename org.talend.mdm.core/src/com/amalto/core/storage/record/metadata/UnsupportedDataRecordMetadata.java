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

import java.util.Collections;
import java.util.Map;

/**
 * A special implementation for {@link com.amalto.core.storage.record.DataRecord} that do not want to expose {@link DataRecordMetadata}.
 * This singleton might be used for projections of fields for instance.
 */
public class UnsupportedDataRecordMetadata implements DataRecordMetadata {

    public static final DataRecordMetadata INSTANCE = new UnsupportedDataRecordMetadata();

    private UnsupportedDataRecordMetadata() {
    }

    public long getLastModificationTime() {
        return 0;
    }

    public void setLastModificationTime(long lastModificationTime) {
    }

    public String getTaskId() {
        return null;
    }

    public void setTaskId(String taskId) {
    }

    public Map<String, String> getRecordProperties() {
        return Collections.emptyMap();
    }
}
