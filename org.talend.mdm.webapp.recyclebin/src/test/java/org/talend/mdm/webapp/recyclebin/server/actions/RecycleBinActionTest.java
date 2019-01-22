/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package org.talend.mdm.webapp.recyclebin.server.actions;

import java.rmi.RemoteException;
import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;
import org.talend.mdm.webapp.base.client.exception.ServiceException;

import com.amalto.core.util.LocaleUtil;
import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.core.webservice.WSConceptKey;
import com.amalto.core.webservice.WSGetBusinessConceptKey;
import com.amalto.core.webservice.WSRecoverDroppedItem;
import com.amalto.core.webservice.XtentisPort;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@PrepareForTest({ com.amalto.core.webservice.XtentisPort.class, org.talend.mdm.webapp.base.server.util.CommonUtil.class,
        com.amalto.webapp.core.util.Webapp.class })
@SuppressWarnings("nls")
public class RecycleBinActionTest extends TestCase {

    private final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.recyclebin.client.i18n.RecycleBinMessages", this.getClass().getClassLoader()); //$NON-NLS-1$

    private RecycleBinAction action = new RecycleBinAction() {
        
        @Override
        protected boolean checkNoPermission(String modelName, String conceptName) {
            return false;
        }
    };

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + RecycleBinActionTest.class.getSimpleName(),
                RecycleBinActionTest.class);
    }


    @Test(expected = ServiceException.class)
    public void testRecoverDroppedItem() throws Exception {
        String language = "en";
        String clusterName = "Product";
        String modelName = "Product";
        String conceptName = "Product";
        String ids = "1";
        Locale locale = LocaleUtil.getLocale(language);
        String[] idArray = { "03" };
        WSConceptKey key = new WSConceptKey();
        PowerMockito.mockStatic(org.talend.mdm.webapp.base.server.util.CommonUtil.class);
        XtentisPort port = PowerMockito.mock(XtentisPort.class);
        Mockito.when(org.talend.mdm.webapp.base.server.util.CommonUtil.getPort()).thenReturn(port);
        Mockito.when(port.getBusinessConceptKey(Mockito.any(WSGetBusinessConceptKey.class))).thenReturn(key);
        Mockito.when(org.talend.mdm.webapp.base.server.util.CommonUtil.extractIdWithDots(Mockito.any(String[].class),
                Mockito.any(String.class))).thenReturn(idArray);
        Mockito.when(port.recoverDroppedItem(Mockito.any(WSRecoverDroppedItem.class))).thenThrow(new RemoteException());
        try {
            action.recoverDroppedItem(clusterName, modelName, conceptName, ids, language);
        } catch (Exception e) {
            assertEquals(e.getMessage(), MESSAGES.getMessage(locale, "restoreErrorMessage"));
        }
    }

}
