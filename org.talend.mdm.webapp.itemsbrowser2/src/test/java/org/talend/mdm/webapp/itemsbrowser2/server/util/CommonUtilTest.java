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
package org.talend.mdm.webapp.itemsbrowser2.server.util;

import junit.framework.TestCase;

import org.talend.mdm.webapp.itemsbrowser2.client.exception.ParserException;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.server.i18n.ItemsbrowserMessagesImpl;

@SuppressWarnings("nls")
public class CommonUtilTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        MessagesFactory.setMessages(new ItemsbrowserMessagesImpl());
    }

    public void testBuildWhereItems() {

        String criteria = "MyEntity/id CONTAINS *";
        try {
            CommonUtil.buildWhereItems(criteria);
        } catch (Exception e) {
            fail();
        }

        criteria = "blahblah";
        try {
            CommonUtil.buildWhereItems(criteria);
        } catch (ParserException e) {
            // OK
        } catch (Exception e) {
            fail();
        }
    }
}
