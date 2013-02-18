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

import java.util.*;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;

public class UserManageOptimizedImpl extends UserManage {

    private static final String PROVISIONING_CLUSTER = "PROVISIONING"; //$NON-NLS-1$

    private static final String PROVISIONING_CONCEPT = "User"; //$NON-NLS-1$

    @Override
    public int getWebUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_WEB.getName());
    }

    @Override
    public int getViewerUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_VIEWER.getName());
    }

    @Override
    public int getNormalUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_USER.getName());
    }

    @Override
    public int getNBAdminUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_ADMIN.getName());
    }

    @Override
    public int getActiveUsers() {
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/enabled", //$NON-NLS-1$
                    WhereCondition.EQUALS,
                    "yes", //$NON-NLS-1$
                    "NONE")); //$NON-NLS-1$
            IWhereItem whereItem = new WhereAnd(conditions);
            return (int) Util.getItemCtrl2Local().count(new DataClusterPOJOPK(PROVISIONING_CLUSTER),
                    PROVISIONING_CONCEPT,
                    whereItem,
                    -1);
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve active user number", e);
        }
    }

    @Override
    public boolean isExistUser(User user) {
        if (user == null || user.getUserName() == null) {
            return false;
        }
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/username", //$NON-NLS-1$
                    WhereCondition.EQUALS,
                    user.getUserName(),
                    "NONE")); //$NON-NLS-1$
            IWhereItem whereItem = new WhereAnd(conditions);
            int number = (int) Util.getItemCtrl2Local().count(new DataClusterPOJOPK(PROVISIONING_CLUSTER),
                    PROVISIONING_CONCEPT,
                    whereItem,
                    -1);
            return number > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isUpdateDCDM(User user) {
        User existUser = findUserByName(user);
        if (existUser != null) {
            String cluster = user.getDynamic().get("cluster"); //$NON-NLS-1$
            String model = user.getDynamic().get("model"); //$NON-NLS-1$
            if (cluster == null && model == null) {
                return false;
            } else if (cluster != null && cluster.equals(existUser.getDynamic().get("cluster")) || model != null //$NON-NLS-1$
                    && model.equals(existUser.getDynamic().get("model"))) { //$NON-NLS-1$
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isActiveUser(User user) {
        if (!user.isEnabled()) {
            return false;
        }
        User existUser = findUserByName(user);
        return existUser != null && existUser.isEnabled() != user.isEnabled();
    }

    @Override
    public Set<String> getOriginalRole(User user) {
        User existUser = findUserByName(user);
        if (existUser != null) {
            return existUser.getRoleNames();
        }
        return Collections.emptySet();
    }

    private User findUserByName(User user) {
        if (user == null || user.getUserName() == null) {
            return null;
        }
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/username", //$NON-NLS-1$
                    WhereCondition.EQUALS,
                    user.getUserName(),
                    "NONE")); //$NON-NLS-1$
            IWhereItem whereItem = new WhereAnd(conditions);
            ArrayList items = Util.getItemCtrl2Local().getItems(new DataClusterPOJOPK(PROVISIONING_CLUSTER),
                    PROVISIONING_CONCEPT,
                    whereItem,
                    -1,
                    0,
                    1,
                    false);
            if (items != null && items.size() > 0) {
                String userXML = (String) items.get(0);
                return User.parse(userXML);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    protected int getUserCount(String role) {
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/enabled", //$NON-NLS-1$
                    WhereCondition.EQUALS,
                    "yes", //$NON-NLS-1$
                    "NONE")); //$NON-NLS-1$
            conditions.add(new WhereCondition("User/roles/role", //$NON-NLS-1$
                    WhereCondition.EQUALS,
                    role,
                    "NONE")); //$NON-NLS-1$
            IWhereItem whereItem = new WhereAnd(conditions);
            return (int) Util.getItemCtrl2Local().count(new DataClusterPOJOPK(PROVISIONING_CLUSTER),
                    PROVISIONING_CONCEPT,
                    whereItem,
                    -1);
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve active user number", e);
        }
    }
}
