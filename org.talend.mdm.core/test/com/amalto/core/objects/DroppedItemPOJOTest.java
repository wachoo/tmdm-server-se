/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.objects;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;

import com.amalto.core.server.api.XmlServer;
import com.amalto.core.util.Util;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@SuppressWarnings("nls")
@PrepareForTest({ Util.class, XmlServer.class })
public class DroppedItemPOJOTest extends TestCase {

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + DroppedItemPOJOTest.class.getSimpleName(), DroppedItemPOJOTest.class);
    }

    @Test
    public void testFindAllPKs() throws Exception {
        String[][] result = { { "1", "1" }, { "2" }, { "1" }, { "1", "1" }, { "2", "2" }, { "1" } };
        String[] idsArray = { "TMDM-10011.region.1.1", "Product.Product.2", "TMDM-12975.T1.1", "TMDM-10011.contracts.1.1",
                "TMDM-12975.T3.2.2", "TMDM-12975.T3.1" };
        XmlServer server = PowerMockito.mock(XmlServer.class);
        PowerMockito.mockStatic(Util.class);
        Mockito.when(Util.getXmlServerCtrlLocal()).thenReturn(server);
        Mockito.when(server.getAllDocumentsUniqueID(Mockito.any(String.class))).thenReturn(idsArray);
        List<DroppedItemPOJOPK> allPKs = DroppedItemPOJO.findAllPKs(".*");
        for (int i = 0; i < allPKs.size(); i++) {
            String[] idsValue = allPKs.get(i).getRefItemPOJOPK().getIds();
            String[] exceptedValue = result[i];
            assertEquals(idsValue.length, exceptedValue.length);
            for (int j = 0; j < idsValue.length; j++) {
                assertEquals(exceptedValue[j], idsValue[j]);
            }
        }
    }
}
