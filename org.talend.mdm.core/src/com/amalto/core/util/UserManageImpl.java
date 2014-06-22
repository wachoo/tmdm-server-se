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
package com.amalto.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;

/**
 * DOC hshu class global comment. Detailed comment
 */
public class UserManageImpl extends UserManage {

    private static List<User> users;

    /**
     * list all users.
     * 
     * @return
     */
    protected List<User> listUsers() {
        String dataclusterPK = XSystemObjects.DC_PROVISIONING.getName();
        List<String> results = new ArrayList<String>();
        users = new ArrayList<User>();

        try {
            results = Util.getItemCtrl2Local().getItems(new DataClusterPOJOPK(dataclusterPK), "User", null, 0, 0,
                    Integer.MAX_VALUE, false);

            for (String userXML : results) {
                User user = User.parse(userXML);
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * get web users
     * 
     * @return
     */
    public int getWebUsers() {
        List<User> viewers = new ArrayList<User>();

        for (User user : getUsers()) {
            if (user.enabled && user.getRoleNames().contains(XSystemObjects.ROLE_DEFAULT_WEB.getName())) {
                viewers.add(user);
            }
        }

        return viewers.size();
    }

    /**
     * DOC hshu Comment method "getUsers".
     * @return
     */
    private List<User> getUsers() {
        if (users == null)
            listUsers();
        return users;
    }

    /**
     * get viewer users.
     * 
     * @return
     */
    public int getViewerUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_WEB.getName());
    }

    /**
     * get the number of normal users.
     * 
     * @return
     */
    public int getNormalUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_USER.getName());
    }

    /**
     * Get the number of admin users.
     * 
     * @return
     */
    public int getNBAdminUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_ADMIN.getName());
    }

    /**
     * Get the number of active users.
     * 
     * @return
     */
    public int getActiveUsers() {
        List<User> activeUsers = new ArrayList<User>();

        for (User user : getUsers()) {
            if (user.enabled) {
                activeUsers.add(user);
            }
        }

        return activeUsers.size();
    }

    /**
     * Check if exist the specify user.
     * 
     * @param user
     * @return
     */
    public boolean isExistUser(User user) {
        boolean result = false;

        for (User existUser : getUsers()) {
            if (user.getUserName().equals(existUser.getUserName())) {
                return true;
            }
        }

        return result;
    }

    /**
     * Check if update cluster or model of specify user.
     * 
     * @param user
     * @return
     */
    public boolean isUpdateDCDM(User user) {
        boolean result = false;

        for (User existUser : getUsers()) {
            if (user.getUserName().equals(existUser.getUserName())) {
                String cluster = user.getDynamic().get("cluster");
                String model = user.getDynamic().get("model");

                if (cluster == null && model == null) {
                    return false;
                } else if (cluster != null && cluster.equals(existUser.getDynamic().get("cluster")) || model != null
                        && model.equals(existUser.getDynamic().get("model"))) {
                    return true;
                }
            }
        }

        return result;
    }


    /**
     * Check if update active attribute of specify user.
     * @param user
     * @return
     */
    public boolean isActiveUser(User user) {
        boolean result = false;

        if (!user.isEnabled()) {
            return false;
        }

        for (User existUser : getUsers()) {
            if (existUser.getUserName().equals(user.getUserName())) {
                return existUser.isEnabled() != user.isEnabled();
            }
        }
        return result;
    }

    /**
     * get the rolenames of specify user.
     * 
     * @param user
     * @return
     */
    public Set<String> getOriginalRole(User user) {
        Set<String> result = new HashSet<String>();

        for (User existUser : getUsers()) {
            if (existUser.getUserName().equals(user.getUserName())) {
                result = existUser.getRoleNames();
                break;
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#getUserCount(java.lang.String)
     */
    @Override
    protected int getUserCount(String role) {
        List<User> userList = new ArrayList<User>();

        for (User user : getUsers()) {
            if (user.enabled && user.getRoleNames().contains(role)) {
                userList.add(user);
            }
        }
        return userList.size();
    }

}
