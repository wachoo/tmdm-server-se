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

import org.dom4j.Document;
import org.dom4j.DocumentException;
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

        String[] mockResult = { "totalCount", "DataInDB1", "DataInDB2", "DataInDB3" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        PowerMockito.mockStatic(org.talend.mdm.webapp.base.server.util.CommonUtil.class);
        XtentisPort port = PowerMockito.mock(XtentisPort.class);
        Mockito.when(org.talend.mdm.webapp.base.server.util.CommonUtil.getPort()).thenReturn(port);
        Mockito.when(
                org.talend.mdm.webapp.base.server.util.CommonUtil.getItemBeans(Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString())).thenReturn(mockResult);
        assertEquals(
                "1", new DataProvider("dataCluster", "Product", "viewPk", "criteria", 0, "sortDir", "sortField", "language", "1@2@3").getDataResult()[1]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

        assertEquals(
                "DataInDB1", new DataProvider("dataCluster", "Product", "viewPk", "criteria", 0, "sortDir", "sortField", "language", "").getDataResult()[1]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    }

    public void testGetRootElementName() throws DocumentException {
        DataProvider dataProvider = new DataProvider("dataCluster", "Product", "viewPk", "criteria", 0, "sortDir", "sortField",
                "language", "");
        String xmlString = "<Product><Id>1</Id></Product>";
        dataProvider.setRootElementName("result");
        dataProvider.parseResultDocument(xmlString);
        assertEquals("Product", dataProvider.getRootElementName());
        xmlString = "<result><Id>1</Id></result>";
        dataProvider.setRootElementName("result");
        dataProvider.parseResultDocument(xmlString);
        assertEquals("result", dataProvider.getRootElementName());
        xmlString = "<Id>1</Id>";
        dataProvider.setRootElementName("result");
        Document doc = dataProvider.parseResultDocument(xmlString);
        assertEquals("result", doc.getRootElement().getName());
    }
}
