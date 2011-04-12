/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import com.amalto.core.servlet.LoadServlet;
import junit.framework.TestCase;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

/**
 *
 */
public class LoadServletTest extends TestCase {

    public void testOptimizationSelection() {
        LoadServletTestFixture servlet = getFixture();

        // Qizx database
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", "qizx");
        LoadAction loadAction = servlet.getLoadAction(false);
        assertEquals(OptimizedLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportAutoGenPK());
        assertFalse(loadAction.supportValidation());

        loadAction = servlet.getLoadAction(true);
        assertEquals(DefaultLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportAutoGenPK());
        assertTrue(loadAction.supportValidation());

        // Exist database
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", "exist");
        loadAction = servlet.getLoadAction(true);
        assertEquals(DefaultLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportAutoGenPK());
        assertTrue(loadAction.supportValidation());

        loadAction = servlet.getLoadAction(false);
        assertEquals(DefaultLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportAutoGenPK());
        assertTrue(loadAction.supportValidation());
    }

    public void testNullArguments() {
        LoadServletTestFixture servlet = getFixture();

        // Qizx database
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", "qizx");
        LoadAction loadAction = servlet.getLoadAction(null, null, null, false);
        assertEquals(OptimizedLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportAutoGenPK());
        assertFalse(loadAction.supportValidation());

        // Exist database
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", "exist");
        loadAction = servlet.getLoadAction(null, null, null, true);
        assertEquals(DefaultLoadAction.class, loadAction.getClass());
        assertTrue(loadAction.supportAutoGenPK());
        assertTrue(loadAction.supportValidation());
    }

    private LoadServletTestFixture getFixture() {
        return new LoadServletTestFixture();
    }

    private class LoadServletTestFixture extends LoadServlet {

        private LoadServletTestFixture() {
        }

        public LoadAction getLoadAction(boolean needValidate) {
            return getLoadAction("", "", "", needValidate, false);
        }

        public LoadAction getLoadAction(String dataClusterName, String typeName, String dataModelName, boolean needValidate) {
            return LoadServlet.getLoadAction(dataClusterName, typeName, dataModelName, needValidate, false);
        }

    }

}
