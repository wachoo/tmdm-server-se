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

import junit.framework.TestCase;

import java.io.InputStream;

public class DataSourceParsingTest extends TestCase {

    public void testInvalidDocument() throws Exception {

    }

    public void testInvalidParameters() {
        try {
            DataSourceFactory.getInstance().getDataSource(null, null);
            fail();
        } catch (Exception e) {
            // Expected
        }

        try {
            DataSourceFactory.getInstance().getDataSource("Test-1", null);
            fail();
        } catch (Exception e) {
            // Expected
        }

        try {
            DataSourceFactory.getInstance().getDataSource(null, "MDM");
            fail();
        } catch (Exception e) {
            // Expected
        }
    }

    public void testParsing() throws Exception {
        InputStream stream = DataSourceParsingTest.class.getResourceAsStream("datasources1.xml");
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource(stream, "Test-0", "MDM");
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof RDBMSDataSource);

        RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) dataSource;
        assertTrue(rdbmsDataSource.hasInit());
        assertEquals("jdbc:mysql://10.42.150.15:3306/Test3", rdbmsDataSource.getConnectionURL());
        assertEquals("mdm_dev2", rdbmsDataSource.getDatabaseName());
        assertEquals(RDBMSDataSource.DataSourceDialect.MYSQL, rdbmsDataSource.getDialectName());
        assertEquals("com.mysql.jdbc.Driver", rdbmsDataSource.getDriverClassName());
        assertEquals("/var/lucene/indexes/DS2", rdbmsDataSource.getIndexDirectory());
        assertEquals("/var/cache/DS2", rdbmsDataSource.getCacheDirectory());
        assertEquals("Test-0", rdbmsDataSource.getName());
        assertEquals("toor", rdbmsDataSource.getPassword());
        assertEquals("root", rdbmsDataSource.getUserName());
        assertEquals("jdbc:mysql://10.42.150.15:3306/", rdbmsDataSource.getInitConnectionURL());
        assertEquals("root", rdbmsDataSource.getInitUserName());
        assertEquals("toor", rdbmsDataSource.getInitPassword());
    }

    public void testContainerChange1() throws Exception {
        InputStream stream = DataSourceParsingTest.class.getResourceAsStream("datasources1.xml");
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource(stream, "Test-1", "MDM");
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof RDBMSDataSource);

        RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) dataSource;
        assertTrue(rdbmsDataSource.hasInit());
        assertEquals("jdbc:mysql://10.42.150.15:3306/MDM", rdbmsDataSource.getConnectionURL());
        assertEquals("MDM", rdbmsDataSource.getDatabaseName());
    }

    public void testContainerChange2() throws Exception {
        InputStream stream = DataSourceParsingTest.class.getResourceAsStream("datasources1.xml");
        DataSource dataSource = DataSourceFactory.getInstance().getDataSource(stream, "Test-2", "MDM");
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof RDBMSDataSource);

        RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) dataSource;
        assertTrue(rdbmsDataSource.hasInit());
        assertEquals("jdbc:mysql://10.42.150.15:3306/mdm_dev2", rdbmsDataSource.getConnectionURL());
        assertEquals("mdm_dev2", rdbmsDataSource.getDatabaseName());
    }

}
