// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.server.bizhelpers;

import java.util.ArrayList;
import java.util.List;

import javax.security.jacc.PolicyContextException;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.server.BaseConfiguration;
import org.talend.mdm.webapp.base.server.mockup.FakeData;

import com.amalto.webapp.core.util.Util;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class RoleHelper {

    private static final Logger logger = Logger.getLogger(RoleHelper.class);

    /**
     * DOC HSHU Comment method "getRoles".
     */
    public static List<String> getUserRoles() {
        List<String> roles = new ArrayList<String>();
        if (!BaseConfiguration.isStandalone()) {
            try {
                roles = Util.getAjaxSubject().getRoles();
            } catch (PolicyContextException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            roles.add(FakeData.DEFAULT_ROLE);
        }
        return roles;

    }

    /**
     * DOC HSHU Comment method "getCurrentUserName".
     */
    public static String getCurrentUserName() {
        String userName = "";//$NON-NLS-1$
        if (!BaseConfiguration.isStandalone()) {
            try {
                userName = Util.getLoginUserName();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            userName = FakeData.DEFAULT_USER;
        }
        return userName;
    }

}
