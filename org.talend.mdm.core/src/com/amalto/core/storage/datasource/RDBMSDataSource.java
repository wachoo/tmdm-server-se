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

import java.util.Map;

public class RDBMSDataSource implements DataSource {

    public static enum ContainsOptimization {
        FULL_TEXT, LIKE, DISABLED
    }

    public static enum DataSourceDialect {
        H2, ORACLE_10G, MYSQL, POSTGRES, SQL_SERVER
    }

    public static enum SchemaGeneration {
        CREATE, VALIDATE, UPDATE
    }

    private final ContainsOptimization containsOptimization;

    private final String name;

    private final SchemaGeneration schemaGeneration;

    private final Map<String, String> advancedProperties;

    private final String cacheDirectory;

    private final String initConnectionURL;

    private final String initUserName;

    private final String initPassword;

    private final DataSourceDialect dialect;

    private final String driverClassName;

    private final String password;

    private final String indexDirectory;

    private String userName;

    private String connectionURL;

    private String databaseName;

    private int connectionPoolMinSize;

    private int connectionPoolMaxSize;

    public RDBMSDataSource(String name,
                           String dialectName,
                           String driverClassName,
                           String userName,
                           String password,
                           int connectionPoolMinSize,
                           int connectionPoolMaxSize,
                           String indexDirectory,
                           String cacheDirectory,
                           String schemaGeneration,
                           Map<String, String> advancedProperties,
                           String connectionURL,
                           String databaseName,
                           ContainsOptimization containsOptimization,
                           String initPassword,
                           String initUserName,
                           String initConnectionURL) {
        if ("MySQL".equalsIgnoreCase(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.MYSQL;
        } else if ("H2".equalsIgnoreCase(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.H2;
        } else if ("Oracle11g".equalsIgnoreCase(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.ORACLE_10G;
        } else if ("Oracle10g".equalsIgnoreCase(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.ORACLE_10G;
        } else if ("SQLServer".equalsIgnoreCase(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.SQL_SERVER;
        } else if ("Postgres".equalsIgnoreCase(dialectName)) { //$NON-NLS-1$
            dialect = DataSourceDialect.POSTGRES;
        } else {
            throw new IllegalArgumentException("No support for database '" + dialectName + "'.");
        }
        if ("update".equalsIgnoreCase(schemaGeneration)) { //$NON-NLS-1$
            this.schemaGeneration = SchemaGeneration.UPDATE;
        } else if ("validate".equalsIgnoreCase(schemaGeneration)) { //$NON-NLS-1$
            this.schemaGeneration = SchemaGeneration.VALIDATE;
        } else if ("create".equalsIgnoreCase(schemaGeneration)) { //$NON-NLS-1$
            this.schemaGeneration = SchemaGeneration.CREATE;
        } else {
            throw new IllegalArgumentException("No support for schema generation '" + schemaGeneration + "'.");
        }
        this.initPassword = initPassword;
        this.initUserName = initUserName;
        this.initConnectionURL = initConnectionURL;
        this.name = name;
        this.driverClassName = driverClassName;
        this.userName = userName;
        this.password = password;
        this.connectionPoolMinSize = connectionPoolMinSize;
        this.connectionPoolMaxSize = connectionPoolMaxSize;
        this.indexDirectory = indexDirectory;
        this.cacheDirectory = cacheDirectory;
        this.connectionURL = connectionURL;
        this.databaseName = databaseName;
        this.advancedProperties = advancedProperties;
        this.containsOptimization = containsOptimization;
    }

    public ContainsOptimization getContainsOptimization() {
        return containsOptimization;
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

    public void setUserName(String userName) {
        this.userName = userName;
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

    public int getConnectionPoolMinSize() {
        return this.connectionPoolMinSize;
    }
    
    public int getConnectionPoolMaxSize() {
        return this.connectionPoolMaxSize;
    }

    public boolean hasInit() {
        return !initConnectionURL.isEmpty();
    }

    @Override
    public String getName() {
        return name;
    }

    public SchemaGeneration getSchemaGeneration() {
        return schemaGeneration;
    }

    public Map<String, String> getAdvancedProperties() {
        return advancedProperties;
    }
}
