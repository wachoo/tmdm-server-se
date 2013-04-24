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
package org.talend.mdm.webapp.browserecords.server.provider;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;

import com.amalto.webapp.util.webservices.XtentisPort;

@PrepareForTest({ org.talend.mdm.webapp.base.server.util.CommonUtil.class, DataProvider.class })
@SuppressWarnings("nls")
public class DataProviderTest extends TestCase {

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + DataProviderTest.class.getSimpleName(), DataProviderTest.class);
    }

    public void testGetDataResult() throws Exception {

        String result = "<result><item/><item><result xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "    <Id>1</Id>" + "    <Name>z@@zz@Z</Name>" + "    <Family>[1]</Family>" + "    <Price>111.00</Price>"
                + "    <Availability/>" + "</result>"
                + "</item><item><result xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "    <Id>q</Id>"
                + "    <Name>x@@xx</Name>" + "    <Family>[2]</Family>" + "    <Price>1.00</Price>"
                + "    <Availability>true</Availability>" + "</result>" + "</item></result>";

        String[] mockResult = { "totalCount", "DataInDB1", "DataInDB2", "DataInDB3" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        PowerMockito.mockStatic(org.talend.mdm.webapp.base.server.util.CommonUtil.class);
        XtentisPort port = PowerMockito.mock(XtentisPort.class);
        Mockito.when(org.talend.mdm.webapp.base.server.util.CommonUtil.getPort()).thenReturn(port);
        Mockito.when(
                org.talend.mdm.webapp.base.server.util.CommonUtil.getItemBeans(Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString())).thenReturn(mockResult);
        assertEquals(
                "<result xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "    <Id>1</Id>"
                        + "    <Name>z@@zz@Z</Name>" + "    <Family>[1]</Family>" + "    <Price>111.00</Price>"
                        + "    <Availability/>" + "</result>", new DataProvider("dataCluster", "viewPk", "criteria", 0, "sortDir", "sortField", "language", result).getDataResult()[1].replace("\r", "").replace("\n", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

        assertEquals(
                "DataInDB1", new DataProvider("dataCluster", "viewPk", "criteria", 0, "sortDir", "sortField", "language", "").getDataResult()[1]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    }
}
