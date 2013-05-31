// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.query;

import java.util.HashSet;
import java.util.Set;

import org.talend.mdm.commmon.util.core.ICoreConstants;

import com.amalto.core.util.User;
import com.amalto.core.util.UserManage;

public class MockUserManageImpl extends UserManage {

    @Override
    public int getWebUsers() {
        return 0;
    }

    @Override
    public int getViewerUsers() {
        return 0;
    }

    @Override
    public int getNormalUsers() {
        return 0;
    }

    @Override
    public int getNBAdminUsers() {
        return 0;
    }

    @Override
    public int getActiveUsers() {
        return 0;
    }

    @Override
    public boolean isExistUser(User user) {
        return false;
    }

    @Override
    public boolean isUpdateDCDM(User user) {
        return false;
    }

    @Override
    public boolean isActiveUser(User user) {
        return false;
    }

    @Override
    public Set<String> getOriginalRole(User user) {
        Set<String> userRoles = new HashSet<String>();
        if ("administrator".equals(user.getUserName())) { //$NON-NLS-1$
            userRoles.add(ICoreConstants.ADMIN_PERMISSION);
            userRoles.add(ICoreConstants.SYSTEM_ADMIN_ROLE);
        }
        return userRoles;
    }

    @Override
    protected int getUserCount(String role) {
        return 0;
    }

}