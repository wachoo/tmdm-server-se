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

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;

import java.util.Collection;

/**
 *
 */
public interface StorageAdmin {
    String STAGING_SUFFIX = "#STAGING";

    String[] getAll(String revisionID);

    void delete(String revisionID, String storageName, boolean dropExistingData);

    void deleteAll(String revisionID, boolean dropExistingData);

    Storage create(String dataModelName, String storageName, String dataSourceName, String revisionId);

    boolean exist(String revision, String storageName, StorageType storageType);

    void close();

    /**
     * @param storageName A {@link com.amalto.core.storage.Storage} name.
     * @param revisionId A revision id.
     * @return A previously created {@link Storage} or <code>null</code> if no storage was previously created.
     * @see #create(String, String, String, String)
     */
    Storage get(String storageName, String revisionId);

    /**
     * @param storageName A {@link com.amalto.core.storage.Storage} name.
     * @return A list of previously created {@link Storage} for all revisions or empty list if no storage was previously
     * created.
     * @see #create(String, String, String, String)
     */
    Collection<Storage> get(String storageName);
}
