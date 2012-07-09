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
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

class OracleStorageInitializer implements StorageInitializer {

    private static final Logger LOGGER = Logger.getLogger(OracleStorageInitializer.class);

    public boolean isInitialized(Storage storage) {
        try {
            RDBMSDataSource dataSource = getDataSource(storage);
            Driver driver = (Driver) Class.forName(dataSource.getDriverClassName()).newInstance();
            Connection connection = driver.connect(dataSource.getConnectionURL(), new Properties());
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize(Storage storage) {
        try {
            RDBMSDataSource dataSource = getDataSource(storage);
            Connection connection = DriverManager.getConnection(dataSource.getInitConnectionURL(), dataSource.getInitUserName(), dataSource.getInitPassword());
            try {
                Statement statement = connection.createStatement();
                try {
                    statement.execute("create user " + dataSource.getUserName() + " identified by " + dataSource.getPassword());  //$NON-NLS-1$ //$NON-NLS-2$
                    statement.execute("grant connect, dba to " + dataSource.getUserName());  //$NON-NLS-1$
                    statement.execute("alter user " + dataSource.getUserName() + " account unlock");  //$NON-NLS-1$ //$NON-NLS-2$
                } catch (SQLException e) {
                    // Assumes database is already created.
                    LOGGER.debug("Exception occurred during CREATE USER statement.", e);
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
            LOGGER.info("Oracle database " + dataSource.getUserName() + " has been prepared.");
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during initialization of Oracle database", e);
        }
    }

    private RDBMSDataSource getDataSource(Storage storage) {
        DataSource storageDataSource = storage.getDataSource();
        if (!(storageDataSource instanceof RDBMSDataSource)) {
            throw new IllegalArgumentException("Storage to initialize does not seem to be a RDBMS storage.");
        }

        RDBMSDataSource dataSource = (RDBMSDataSource) storageDataSource;
        if (!dataSource.hasInit()) {
            throw new IllegalArgumentException("Data source '" + dataSource.getName() + "' does not define initialization information.");
        }
        return dataSource;
    }
}
