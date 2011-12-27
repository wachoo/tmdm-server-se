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
package org.talend.mdm.webapp.browserecords.server.util;

import junit.framework.TestCase;

import org.talend.mdm.webapp.browserecords.server.util.action.BrowseRecordsActionTest;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

public class DefVRuleTest extends TestCase {

    public void test_getDefaultXML() throws Exception {
        String language = "en"; //$NON-NLS-1$
        ViewBean viewBean = new ViewBean();
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
        sb.append("\n"); //$NON-NLS-1$
        sb.append("<Agency><Id/><Name/><City/><State/><Zip/><Region/><MoreInfo/></Agency>"); //$NON-NLS-1$
        String defaultXML = sb.toString();

        EntityModel bindingEntityModel = BrowseRecordsActionTest.getEntityModel();
        viewBean.setBindingEntityModel(bindingEntityModel);

    }

}
