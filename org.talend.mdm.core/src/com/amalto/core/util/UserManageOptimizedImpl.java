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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;

public class UserManageOptimizedImpl extends UserManage {

    @Override
    public int getActiveUsers() {
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/enabled", //$NON-NLS-1$
                    WhereCondition.EQUALS, "yes", //$NON-NLS-1$
                    "NONE")); //$NON-NLS-1$
            IWhereItem whereItem = new WhereAnd(conditions);
            return (int) Util.getItemCtrl2Local().count(new DataClusterPOJOPK(PROVISIONING_CLUSTER), USER_CONCEPT, whereItem, -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                    WhereCondition.EQUALS, user.getUserName(), "NONE")); //$NON-NLS-1$
            IWhereItem whereItem = new WhereAnd(conditions);
            int number = (int) Util.getItemCtrl2Local().count(new DataClusterPOJOPK(PROVISIONING_CLUSTER), USER_CONCEPT,
                    whereItem, -1);
            return number > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isActivatingUser(User user) {
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
                    WhereCondition.EQUALS, user.getUserName(), "NONE")); //$NON-NLS-1$
            IWhereItem whereItem = new WhereAnd(conditions);
            ArrayList<String> items = Util.getItemCtrl2Local().getItems(new DataClusterPOJOPK(PROVISIONING_CLUSTER),
                    USER_CONCEPT, whereItem, -1, 0, 1, false);
            if (items != null && items.size() > 0) {
                String userXML = items.get(0);
                return User.parse(userXML);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    protected int getUserCount(String matchRole) {
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/enabled", //$NON-NLS-1$
                    WhereCondition.EQUALS, "yes", //$NON-NLS-1$
                    "NONE")); //$NON-NLS-1$
            conditions.add(new WhereCondition("User/roles/role", //$NON-NLS-1$
                    WhereCondition.EQUALS, matchRole, "NONE")); //$NON-NLS-1$
            IWhereItem whereItem = new WhereAnd(conditions);
            return (int) Util.getItemCtrl2Local().count(new DataClusterPOJOPK(PROVISIONING_CLUSTER), USER_CONCEPT, whereItem, -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
