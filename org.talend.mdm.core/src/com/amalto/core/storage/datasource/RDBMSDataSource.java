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

public class RDBMSDataSource implements DataSource {

    private final String name;

    public static enum DataSourceDialect {
        H2, ORACLE_10G, MYSQL, SQL_SERVER
    }

    private final String cacheDirectory;

    private final String initConnectionURL;

    private final String initUserName;

    private final String initPassword;

    private final DataSourceDialect dialect;

    private final String driverClassName;

    private final String userName;

    private final String password;

    private final String indexDirectory;

    private String connectionURL;

    private String databaseName;

    public RDBMSDataSource(String name,
                           String dialectName,
                           String driverClassName,
                           String userName,
                           String password,
                           String indexDirectory,
                           String cacheDirectory,
                           String connectionURL,
                           String databaseName,
                           String initPassword,
                           String initUserName,
                           String initConnectionURL) {
        if ("MySQL".equals(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.MYSQL;
        } else if ("H2".equals(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.H2;
        } else if ("Oracle11g".equals(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.ORACLE_10G;
        } else if ("Oracle10g".equals(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.ORACLE_10G;
        } else if ("SQLServer".equals(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.SQL_SERVER;
        } else {
            throw new IllegalArgumentException("No support for type '" + dialectName + "'.");
        }
        this.initPassword = initPassword;
        this.initUserName = initUserName;
        this.initConnectionURL = initConnectionURL;
        this.name = name;
        this.driverClassName = driverClassName;
        this.userName = userName;
        this.password = password;
        this.indexDirectory = indexDirectory;
        this.cacheDirectory = cacheDirectory;
        this.connectionURL = connectionURL;
        this.databaseName = databaseName;
    }

    public DataSourceDialect getDialectName() {
        return dialect;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getCacheDirectory() {
        return cacheDirectory;
    }

    public String getIndexDirectory() {
        return indexDirectory;
    }

    public boolean supportFullText() {
        return !indexDirectory.isEmpty();
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    // Intentionally left with 'friendly' visibility.
    void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    // Intentionally left with 'friendly' visibility.
    void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getInitConnectionURL() {
        return initConnectionURL;
    }

    public String getInitUserName() {
        return initUserName;
    }

    public String getInitPassword() {
        return initPassword;
    }

    public boolean hasInit() {
        return !initConnectionURL.isEmpty();
    }

    @Override
    public String getName() {
        return name;
    }
}
