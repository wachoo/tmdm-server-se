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

package com.amalto.core.load;

import com.amalto.core.load.action.DefaultLoadAction;
import com.amalto.core.load.action.LoadAction;
import com.amalto.core.load.action.OptimizedLoadAction;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.servlet.LoadServlet;
import junit.framework.TestCase;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

/**
 *
 */
public class LoadServletTest extends TestCase {

    public static final String TEST_DATA_CLUSTER = "TestDataCluster";

    public void testOptimizationSelection() {
        LoadServletTestFixture servlet = getFixture();

        // Qizx database
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", "qizx");
        LoadAction loadAction = servlet.getLoadAction(false);
        assertEquals(OptimizedLoadAction.class, loadAction.getClass());
        assertFalse(loadAction.supportValidation());

        loadAction = servlet.getLoadAction(true);
        assertEquals(DefaultLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportValidation());

        // Exist database
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", "exist");
        loadAction = servlet.getLoadAction(true);
        assertEquals(DefaultLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportValidation());

        loadAction = servlet.getLoadAction(false);
        assertEquals(DefaultLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportValidation());
    }

    public void testNullArguments() {
        LoadServletTestFixture servlet = getFixture();

        // Qizx database
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", "qizx");
        LoadAction loadAction = servlet.getLoadAction(TEST_DATA_CLUSTER, null, null, false);
        assertEquals(OptimizedLoadAction.class, loadAction.getClass());
        assertFalse(loadAction.supportValidation());

        // Exist database
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", "exist");
        loadAction = servlet.getLoadAction(TEST_DATA_CLUSTER, null, null, true);
        assertEquals(DefaultLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportValidation());
    }

    public void testNonExistingDataCluster() {
        LoadServletTestFixture servlet = getFixture();

        // Qizx database
        try {
            servlet.getLoadAction("NonExistingDataCluster", null, null, false);
            fail("Expected an error (data cluster does not exist)");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    private LoadServletTestFixture getFixture() {
        return new LoadServletTestFixture(TEST_DATA_CLUSTER);
    }

    private class LoadServletTestFixture extends LoadServlet {

        private final String[] dataClusterNames;

        private LoadServletTestFixture(String... dataClusterNames) {
            this.dataClusterNames = dataClusterNames;
        }

        public LoadAction getLoadAction(boolean needValidate) {
            return getLoadAction(TEST_DATA_CLUSTER, "", "", needValidate, false);
        }

        public LoadAction getLoadAction(String dataClusterName, String typeName, String dataModelName, boolean needValidate) {
            return getLoadAction(dataClusterName, typeName, dataModelName, needValidate, false);
        }

        @Override
        protected DataClusterPOJO getDataCluster(String dataClusterName) {
            for (String clusterName : dataClusterNames) {
                if (clusterName.equals(dataClusterName)) {
                    return new DataClusterPOJO(null, clusterName);
                }
            }

            return null;
        }
    }

}
