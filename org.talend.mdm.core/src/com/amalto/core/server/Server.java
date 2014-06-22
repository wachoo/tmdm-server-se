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

import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.transaction.TransactionManager;

public interface Server {

    boolean hasDataSource(String dataSourceName, String container, StorageType type);

    DataSource getDataSource(String dataSourceName, String container, StorageType type);

    DataSource getDataSource(String dataSourceName, String container, String revisionId, StorageType type);
    
    StorageAdmin getStorageAdmin();

    MetadataRepositoryAdmin getMetadataRepositoryAdmin();

    void close();

    void init();

    TransactionManager getTransactionManager();
}
