// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util;

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.Properties;

import junit.framework.TestCase;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSInt;
import com.amalto.webapp.util.webservices.WSPing;
import com.amalto.webapp.util.webservices.WSString;
import com.amalto.webapp.util.webservices.XtentisPort;

@SuppressWarnings("nls")
public class IXtentisRMIPortTest extends TestCase {

    private XtentisPort port;

    @Override
    protected void setUp() throws Exception {
        port = new XtentisRMIMockPort();
        super.setUp();
    }

    public void testPing() throws RemoteException {
        String message = "Hello World! ";
        WSString echoMsg = port.ping(new WSPing(message));
        assertEquals(message, echoMsg.getValue());
    }

    public void testIsPagingAccurate() throws Exception {

        Class<MDMConfiguration> configClass = MDMConfiguration.class;
        Field field = configClass.getDeclaredField("CONFIGURATION"); //$NON-NLS-1$
        field.setAccessible(true);
        Properties props = new Properties();
        field.set(null, props);

        // if no config
        WSBoolean pagingAccurate = port.isPagingAccurate(new WSInt(100));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(1000));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(2000));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(2500));
        assertEquals(true, pagingAccurate.is_true());
        // --------------------------

        props.setProperty("xmldb.type", "qizx"); //$NON-NLS-1$ //$NON-NLS-2$
        props.setProperty("xmldb.qizx.ecountsamplesize", "2000"); //$NON-NLS-1$ //$NON-NLS-2$

        pagingAccurate = port.isPagingAccurate(new WSInt(100));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(1000));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(2000));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(2500));
        assertEquals(false, pagingAccurate.is_true());
        // ---------------------

        props.remove("xmldb.qizx.ecountsamplesize"); //$NON-NLS-1$

        pagingAccurate = port.isPagingAccurate(new WSInt(100));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(1000));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(2000));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(2500));
        assertEquals(true, pagingAccurate.is_true());

        // -----------------
        props.remove("xmldb.type"); //$NON-NLS-1$

        pagingAccurate = port.isPagingAccurate(new WSInt(100));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(1000));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(2000));
        assertEquals(true, pagingAccurate.is_true());

        pagingAccurate = port.isPagingAccurate(new WSInt(2500));
        assertEquals(true, pagingAccurate.is_true());
    }
}
