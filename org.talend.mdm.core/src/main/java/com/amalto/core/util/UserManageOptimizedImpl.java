// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.amalto.core.ejb.remote.ItemCtrl2;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;

public class UserManageOptimizedImpl extends UserManage {

    private static final Logger LOG = Logger.getLogger(UserManageOptimizedImpl.class);

    private static final String FROM_JNDI_HOST = "from.jndi.host"; //$NON-NLS-1$

    private static final String FROM_JNDI_PORT = "from.jndi.port"; //$NON-NLS-1$

    private boolean isFromRemote = false;

    private ItemCtrl2 itemCtrl = null;

    protected UserManageOptimizedImpl(boolean isFromRemote) {
        this.isFromRemote = isFromRemote;
    }

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
            ArrayList<String> items;
            if (!isFromRemote) {
                items = Util.getItemCtrl2Local().getItems(new DataClusterPOJOPK(PROVISIONING_CLUSTER), USER_CONCEPT, whereItem,
                        -1, 0, 1, false);
            } else {
                if (itemCtrl == null) {
                    String[] jndiProps = getJNDIProperties();
                    itemCtrl = Util.getItemCtrl2Home(jndiProps[0], jndiProps[1]);
                }
                items = itemCtrl.getItems(new DataClusterPOJOPK(PROVISIONING_CLUSTER), USER_CONCEPT, whereItem, -1, 0, 1, false);
            }
            if (items != null && items.size() > 0) {
                String userXML = items.get(0);
                return User.parse(userXML);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String[] getJNDIProperties() throws Exception {
        String host, port;

        InputStream is = UserManageOptimizedImpl.class.getResourceAsStream("ejb.remote.jndi.lookup.properties"); //$NON-NLS-1$
        if (is != null) {
            LOG.info("Retrieving JNDI port from custom file."); //$NON-NLS-1$
            try {
                Properties properties = new Properties();
                properties.load(is);
                host = properties.getProperty(FROM_JNDI_HOST);
                port = properties.getProperty(FROM_JNDI_PORT);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        } else {
            // Retrieve JNDI properties from MBean
            // FIXME Specific to JBoss
            InitialContext ic = new InitialContext();
            MBeanServerConnection server = (MBeanServerConnection) ic.lookup("jmx/invoker/RMIAdaptor"); //$NON-NLS-1$
            ObjectName name = new ObjectName("jboss:service=Naming"); //$NON-NLS-1$
            host = (String) server.getAttribute(name, "BindAddress"); //$NON-NLS-1$
            port = Integer.toString((Integer) server.getAttribute(name, "Port")); //$NON-NLS-1$
        }
        return new String[] { host, port };
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
