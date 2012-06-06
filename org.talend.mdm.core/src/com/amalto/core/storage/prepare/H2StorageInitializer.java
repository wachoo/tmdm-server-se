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

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

class H2StorageInitializer implements StorageInitializer {
    public boolean isInitialized(Storage storage) {
        return false; // Returns always false to ensure initialize() is called.
    }

    public void initialize(Storage storage) {
        try {
            DataSource storageDataSource = storage.getDataSource();
            if (!(storageDataSource instanceof RDBMSDataSource)) {
                throw new IllegalArgumentException("Storage to clean does not seem to be a RDBMS storage.");
            }

            RDBMSDataSource dataSource = (RDBMSDataSource) storageDataSource;
            Driver driver = (Driver) Class.forName(dataSource.getDriverClassName()).newInstance();
            // On H2, opening a connection implicitly creates the database files.
            Connection connection = driver.connect(dataSource.getConnectionURL(), new Properties());
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during initialization of H2 database", e);
        }

    }
}
