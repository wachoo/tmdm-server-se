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

import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;
import org.talend.mdm.webapp.recyclebin.shared.NoPermissionException;

import com.amalto.core.util.LocaleUtil;
import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

@PrepareForTest({ com.amalto.core.webservice.XtentisPort.class, org.talend.mdm.webapp.base.server.util.CommonUtil.class,
        com.amalto.webapp.core.util.Webapp.class, LocaleUtil.class })
@SuppressWarnings("nls")
public class RecycleBinActionTest extends TestCase {

    private final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.recyclebin.client.i18n.RecycleBinMessages", this.getClass().getClassLoader()); //$NON-NLS-1$

    private RecycleBinAction action = new RecycleBinAction() {
        
        @Override
        protected void checkRestoreAccess(String modelName, String conceptName) {
            throw new NoPermissionException();
        }
    };

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + RecycleBinActionTest.class.getSimpleName(),
                RecycleBinActionTest.class);
    }


    @Test
    public void testRecoverDroppedItem() throws Exception {
        String language = "en";
        String clusterName = "Product";
        String modelName = "Product";
        String conceptName = "Product";
        String ids = "1";
        Locale locale = LocaleUtil.getLocale(language);
        PowerMockito.mockStatic(LocaleUtil.class);
        PowerMockito.mockStatic(org.talend.mdm.webapp.base.server.util.CommonUtil.class);
        Mockito.when(LocaleUtil.getLocale()).thenReturn(new Locale("en"));
        try {
            action.recoverDroppedItem(clusterName, modelName, conceptName, ids);
        } catch (Exception e) {
            assertEquals("org.talend.mdm.webapp.base.client.exception.ServiceException", e.getClass().getName());
            assertEquals(MESSAGES.getMessage(locale, "restoreErrorMessage"), e.getMessage());
        }
    }

}
