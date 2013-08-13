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
package com.amalto.core.util;

import java.util.Set;

/**
 * DOC mhirt class global comment. Detailled comment <br/>
 * 
 */
public final class UserHelper {
    protected static boolean isFromeRemote = false;
    private UserHelper() {
        um = new UserManageOptimizedImpl();
    }

    private static UserHelper instance;

    private UserManage um;

    public static synchronized UserHelper getInstance() {
        if (instance == null) {
            instance = new UserHelper();
        }

        return instance;
    }
    
    public static synchronized UserHelper getInstance(boolean isFromRemotex) {
        isFromeRemote = isFromRemotex;
        if (instance == null) {
            instance = new UserHelper();
        }

        return instance;
    }
    public static void clearInstance() {
        instance = null;
    }

    public void overrideUserManage(UserManage _um) {
        this.um = _um;
    }

    /**
     * get viewer users.
     * 
     * @return
     */
    public int getViewerUsers() {
        return um.getViewerUsers();
    }

    /**
     * get the number of normal users.
     * 
     * @return
     */
    public int getNormalUsers() {
        return um.getNormalUsers();
    }

    /**
     * Get the number of admin users.
     * 
     * @return
     */
    public int getNBAdminUsers() {
        return um.getNBAdminUsers();
    }

    /**
     * Get the number of active users.
     * 
     * @return
     */
    public int getActiveUsers() {
        return um.getActiveUsers();
    }

    /**
     * Check if exist the specify user.
     * 
     * @param user
     * @return
     */
    public boolean isExistUser(User user) {
        return um.isExistUser(user);
    }

    /**
     * Check if update active attribute of specify user.
     * 
     * @param user
     * @return
     */
    public boolean isActivatingUser(User user) {
        return um.isActivatingUser(user);
    }

    /**
     * get the rolenames of specify user.
     * 
     * @param user
     * @return
     */
    public Set<String> getOriginalRole(User user) {
        return um.getOriginalRole(user);
    }

    /**
     * check the users.
     */
    public void checkUsers() {
        if (um instanceof UserManageImpl) {
            ((UserManageImpl) um).listUsers();
        }
    }
}
