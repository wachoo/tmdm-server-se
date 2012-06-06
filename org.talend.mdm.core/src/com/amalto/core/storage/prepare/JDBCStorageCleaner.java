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

package com.amalto.core.storage.prepare;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.apache.commons.lang.NotImplementedException;

public class JDBCStorageCleaner implements StorageCleaner {

    private StorageCleaner next;

    public JDBCStorageCleaner(StorageCleaner next) {
        this.next = next;
    }

    public void clean(Storage storage) {
        try {
            DataSource storageDataSource = storage.getDataSource();
            if (!(storageDataSource instanceof RDBMSDataSource)) {
                throw new IllegalArgumentException("Storage to clean does not seem to be a RDBMS storage.");
            }

            RDBMSDataSource dataSource = (RDBMSDataSource) storageDataSource;
            RDBMSDataSource.DataSourceDialect dialect = dataSource.getDialectName();
            switch (dialect) {
                case MYSQL:
                    next = new MySQLStorageCleaner(next);
                    break;
                case H2:
                    next = new H2StorageCleaner(next);
                    break;
                case ORACLE_10G:
                    next = new OracleStorageCleaner(next);
                    break;
                case SQL_SERVER:
                    next = new SQLServerStorageCleaner(next);
                    break;
                default:
                    throw new NotImplementedException("Can not clean storages based on dialect '" + dialect + "'");
            }

            next.clean(storage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
