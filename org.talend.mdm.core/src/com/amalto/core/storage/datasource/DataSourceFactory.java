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

import org.apache.commons.lang.NotImplementedException;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class DataSourceFactory {

    public static final String DB_DATASOURCES = "db.datasources"; //$NON-NLS-1$

    private static final DataSourceFactory INSTANCE = new DataSourceFactory();

    private static DocumentBuilderFactory factory;

    private DataSourceFactory() {
        factory = DocumentBuilderFactory.newInstance();
    }

    public static DataSourceFactory getInstance() {
        return INSTANCE;
    }

    public DataSource getDataSource(String dataSourceName, String container) {
        return getDataSource(readDataSourcesConfiguration(), dataSourceName, container);
    }

    public DataSource getDataSource(InputStream configurationStream, String dataSourceName, String container) {
        if (dataSourceName == null) {
            throw new IllegalArgumentException("Data source name can not be null.");
        }
        if (container == null) {
            throw new IllegalArgumentException("Container name can not be null.");
        }

        Map<String, DataSource> dataSourceMap = readDocument(configurationStream);
        DataSource dataSource = dataSourceMap.get(dataSourceName);

        if (dataSource == null) {
            throw new IllegalArgumentException("Data source '" + dataSourceName + "' can not be found in configuration.");
        }

        // Additional post parsing (replace potential ${container} with container parameter value).
        if (dataSource instanceof RDBMSDataSource) {
            RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) dataSource;
            String connectionURL = rdbmsDataSource.getConnectionURL();
            String processedConnectionURL = connectionURL.replace("${container}", container);
            rdbmsDataSource.setConnectionURL(processedConnectionURL);

            if ("${container}".equals(rdbmsDataSource.getDatabaseName())) {
                rdbmsDataSource.setDatabaseName(container);
            }
        }

        return dataSource;
    }

    private static synchronized InputStream readDataSourcesConfiguration() {
        Properties configuration = MDMConfiguration.getConfiguration();
        String dataSourcesFileName = (String) configuration.get(DB_DATASOURCES);
        // DB_DATASOURCES property is mandatory to continue.
        if (dataSourcesFileName == null) {
            throw new IllegalStateException(DB_DATASOURCES + " is not defined in MDM configuration.");
        }

        InputStream configurationAsStream = null;

        // 1- Try from file (direct lookup)
        File file = new File(dataSourcesFileName);
        if (file.exists()) {
            try {
                configurationAsStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Unexpected state (file exists but can't create a stream from it).", e);
            }
        }

        // 1- Try from file (lookup from user.dir)
        if (configurationAsStream == null) {
            file = new File(System.getProperty("user.dir") + "/bin/" + dataSourcesFileName); //$NON-NLS-1$ //$NON-NLS-2$
            if (file.exists()) {
                try {
                    configurationAsStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("Unexpected state (file exists but can't create a stream from it).", e);
                }
            }
        }

        // 2- From class path
        if (configurationAsStream == null) {
            List<String> filePaths = Arrays.asList(dataSourcesFileName);
            Iterator<String> iterator = filePaths.iterator();

            while (configurationAsStream == null && iterator.hasNext()) {
                String currentFilePath = iterator.next();
                configurationAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(currentFilePath);
            }
        }

        // 3- error: configuration was not found
        if (configurationAsStream == null) {
            throw new IllegalStateException("Could not find datasources configuration file '" + dataSourcesFileName + "'.");
        }

        return configurationAsStream;

    }

    private static Map<String, DataSource> readDocument(InputStream configurationAsStream) {
        Document document;
        try {
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            document = documentBuilder.parse(configurationAsStream);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during data sources XML configuration parsing", e);
        }

        try {
            Map<String, DataSource> nameToDataSources = new HashMap<String, DataSource>();
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList dataSourceList = (NodeList) evaluate(document, xPath, "/datasources/datasource", XPathConstants.NODESET); //$NON-NLS-1$
            DataSource currentDataSource;

            for (int i = 0; i < dataSourceList.getLength(); i++) {
                Node dataSource = dataSourceList.item(i);
                String name = (String) evaluate(dataSource, xPath, "@name", XPathConstants.STRING); //$NON-NLS-1$
                String type = (String) evaluate(dataSource, xPath, "type", XPathConstants.STRING); //$NON-NLS-1$
                if ("RDBMS".equals(type)) { //$NON-NLS-1$
                    String dialectName = (String) evaluate(dataSource, xPath, "rdbms-configuration/dialect", XPathConstants.STRING); //$NON-NLS-1$
                    String driverClassName = (String) evaluate(dataSource, xPath, "rdbms-configuration/connection-driver-class", XPathConstants.STRING); //$NON-NLS-1$
                    String connectionURL = (String) evaluate(dataSource, xPath, "rdbms-configuration/connection-url", XPathConstants.STRING); //$NON-NLS-1$
                    String databaseName = (String) evaluate(dataSource, xPath, "rdbms-configuration/database-name", XPathConstants.STRING); //$NON-NLS-1$
                    String userName = (String) evaluate(dataSource, xPath, "rdbms-configuration/connection-username", XPathConstants.STRING); //$NON-NLS-1$
                    String password = (String) evaluate(dataSource, xPath, "rdbms-configuration/connection-password", XPathConstants.STRING); //$NON-NLS-1$
                    String indexDirectory = (String) evaluate(dataSource, xPath, "rdbms-configuration/fulltext-index-directory", XPathConstants.STRING); //$NON-NLS-1$
                    String initConnectionURL = (String) evaluate(dataSource, xPath, "rdbms-configuration/init/connection-url", XPathConstants.STRING); //$NON-NLS-1$
                    String initUserName = (String) evaluate(dataSource, xPath, "rdbms-configuration/init/connection-username", XPathConstants.STRING); //$NON-NLS-1$
                    String initPassword = (String) evaluate(dataSource, xPath, "rdbms-configuration/init/connection-password", XPathConstants.STRING); //$NON-NLS-1$

                    currentDataSource = new RDBMSDataSource(name,
                            dialectName,
                            driverClassName,
                            userName,
                            password,
                            indexDirectory,
                            connectionURL,
                            databaseName,
                            initPassword,
                            initUserName,
                            initConnectionURL);
                } else {
                    throw new NotImplementedException("No support for type '" + type + "'.");
                }
                nameToDataSources.put(name, currentDataSource);
            }
            return nameToDataSources;
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Invalid data sources configuration.", e);
        }
    }

    private static Object evaluate(Node node, XPath xPathParser, String expression, QName returnType) throws XPathExpressionException {
        XPathExpression result = xPathParser.compile(expression);
        return result.evaluate(node, returnType);
    }

}
