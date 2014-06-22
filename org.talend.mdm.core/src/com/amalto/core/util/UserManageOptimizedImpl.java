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
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;

/**
 * DOC hshu class global comment. Detailed comment
 */
public class UserManageOptimizedImpl extends UserManage {

    private static final String PROVISIONING_CLUSTER = "PROVISIONING"; //$NON-NLS-1$

    private static final String PROVISIONING_CONCEPT = "User"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#getWebUsers()
     */
    @Override
    public int getWebUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_WEB.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#getViewerUsers()
     */
    @Override
    public int getViewerUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_VIEWER.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#getNormalUsers()
     */
    @Override
    public int getNormalUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_USER.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#getNBAdminUsers()
     */
    @Override
    public int getNBAdminUsers() {
        return getUserCount(XSystemObjects.ROLE_DEFAULT_ADMIN.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#getActiveUsers()
     */
    @Override
    public int getActiveUsers() {
        int number = 0;
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/enabled", WhereCondition.EQUALS, "yes", "NONE")); //$NON-NLS-1$ //$NON-NLS-2$
            IWhereItem whereItem = new WhereAnd(conditions);

            number = (int) Util.getItemCtrl2Local().count(new DataClusterPOJOPK(PROVISIONING_CLUSTER), PROVISIONING_CONCEPT,
                    whereItem, -1);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return number;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#isExistUser(com.amalto.core.util.User)
     */
    @Override
    public boolean isExistUser(User user) {
        if (user == null || user.getUserName() == null)
            return false;

        boolean has = false;
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/username", WhereCondition.EQUALS, user.getUserName(), "NONE")); //$NON-NLS-1$ //$NON-NLS-2$
            IWhereItem whereItem = new WhereAnd(conditions);

            int number = (int) Util.getItemCtrl2Local().count(new DataClusterPOJOPK(PROVISIONING_CLUSTER), PROVISIONING_CONCEPT,
                    whereItem, -1);

            if (number > 0)
                has = true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return has;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#isUpdateDCDM(com.amalto.core.util.User)
     */
    @Override
    public boolean isUpdateDCDM(User user) {

        User existUser = findUserByName(user);

        if (existUser != null) {
            String cluster = user.getDynamic().get("cluster"); //$NON-NLS-1$
            String model = user.getDynamic().get("model"); //$NON-NLS-1$

            if (cluster == null && model == null) {
                return false;
                // FIXME this logic is a little wired
            } else if (cluster != null && cluster.equals(existUser.getDynamic().get("cluster")) || model != null //$NON-NLS-1$
                    && model.equals(existUser.getDynamic().get("model"))) { //$NON-NLS-1$
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#isActiveUser(com.amalto.core.util.User)
     */
    @Override
    public boolean isActiveUser(User user) {

        if (!user.isEnabled()) {
            return false;
        }

        User existUser = findUserByName(user);
        if (existUser != null)
            return existUser.isEnabled() != user.isEnabled();

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#getOriginalRole(com.amalto.core.util.User)
     */
    @Override
    public Set<String> getOriginalRole(User user) {
        Set<String> result = new HashSet<String>();

        User existUser = findUserByName(user);
        if (existUser != null)
            result = existUser.getRoleNames();

        return result;
    }

    /**
     * DOC hshu Comment method "findUserByName".
     * 
     * @param user
     * @return
     */
    private User findUserByName(User user) {

        if (user == null || user.getUserName() == null)
            return null;

        User gettedUser = null;
        try {
            ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
            conditions.add(new WhereCondition("User/username", WhereCondition.EQUALS, user.getUserName(), "NONE")); //$NON-NLS-1$ //$NON-NLS-2$
            IWhereItem whereItem = new WhereAnd(conditions);

            ArrayList items = Util.getItemCtrl2Local().getItems(new DataClusterPOJOPK(PROVISIONING_CLUSTER),
                    PROVISIONING_CONCEPT, whereItem, -1, 0, 1, false);

            if (items != null && items.size() > 0) {
                String userXML = (String) items.get(0);
                gettedUser = User.parse(userXML);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return gettedUser;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.util.UserManage#getUserCount(java.lang.String)
     */
    @Override
    protected int getUserCount(String role) {

        int count = 0;

        String[] roles = { role };
        // FIXME have not found a way to build this query via where conditions
        // FIXME PROVISIONING always on head revision?
        List result = null;
        try {

            result = Util
                    .getItemCtrl2Local()
                    .runQuery(
                            null,
                            new DataClusterPOJOPK(PROVISIONING_CLUSTER),
                            "let $result:=(for $user in collection(\"/PROVISIONING\")//p/User/roles/role[ string(../../enabled)=\"yes\" and  string(.) = \"%0\"] return $user) return count($result)", //$NON-NLS-1$
                            roles);

        } catch (Exception e) {
            throw new RuntimeException();
        }

        if (result != null && result.size() > 0)
            count = result.get(0) == null ? 0 : Integer.parseInt((String) result.get(0));

        return count;
    }

}
