// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.test.server;

import junit.framework.TestCase;

import org.talend.mdm.webapp.itemsbrowser2.server.ItemsBrowserConfiguration;

public class ConfigurationTest extends TestCase {

    public void testGetProperty() throws Exception {

        String modeValue=ItemsBrowserConfiguration.getPropertyValue("mode");//$NON-NLS-1$
        assertTrue("Property value is illegal! ", modeValue.equals("standalone")||modeValue.equals("jboss"));//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
    }
}
