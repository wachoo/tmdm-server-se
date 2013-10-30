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

package com.amalto.core.storage.datasource;

/**
 * Represents a "datasource": a representation of how to connect to the underlying database.
 * @see com.amalto.core.storage.Storage#init(DataSource)
 */
public interface DataSource {
    /**
     * @return A unique name as {@link String} that identifies the datasource in MDM.
     */
    String getName();

    /**
     * @return <code>true</code> if this datasource can potentially be shared by multiple instances of
     * {@link com.amalto.core.storage.Storage}, <code>false</code> otherwise. When this method returns <code>true</code>,
     * storage should enforce all necessary actions (e.g. naming conventions) to ensure no collision between
     * storages.
     */
    boolean isShared();

    void setShared(boolean isShared);
}
