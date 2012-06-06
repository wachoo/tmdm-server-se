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
package com.amalto.core.delegator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;

public abstract class ILocalUser implements IBeanDelegator {

    private static final Logger logger = Logger.getLogger(ILocalUser.class);

    protected static LinkedHashMap<String,String> onlineUsers = new LinkedHashMap<String,String>();

    public Subject getICurrentSubject() throws XtentisException {
        String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container"; //$NON-NLS-1$     		
        Subject subject;
        try {
            subject = (Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY);
        } catch (PolicyContextException e1) {
            String err = "Unable find the local user: the JACC Policy Context cannot be accessed: " + e1.getMessage(); //$NON-NLS-1$
            logger.error(err, e1);
            throw new XtentisException(err);
        }
        return subject;
    }

    public ILocalUser getILocalUser() throws XtentisException {
        return null;
    }

    public static LinkedHashMap<String, String> getOnlineUsers() {
        return onlineUsers;
    }

    public HashSet<String> getRoles() {
        HashSet<String> set = new HashSet<String>();
        set.add("administration"); //$NON-NLS-1$
        set.add("authenticated"); //$NON-NLS-1$
        return set;
    }

    public UniversePOJO getUniverse() {
        HashMap<String, String> map = new HashMap<String, String>();
        for (String name : UniversePOJO.getXtentisObjectName()) {
            map.put(name, null);
        }
        return new UniversePOJO("[HEAD]", "", map, new LinkedHashMap<String, String>()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String getUserXML() {
        return null;
    }

    public String getUsername() {
        String username = null;
        try {
            username = LocalUser.getPrincipalMember("Username"); //$NON-NLS-1$
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return username == null ? MDMConfiguration.getAdminUser() : username;
    }

    public String getPassword() {
        String passwd = null;
        try {
            passwd = LocalUser.getPrincipalMember("Password"); //$NON-NLS-1$
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return passwd;
    }

    public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException {
        return true;
    }

    public void logout() throws XtentisException {
        String SERVLET_CONTEXT_KEY = "javax.servlet.http.HttpServletRequest"; //$NON-NLS-1$
        HttpServletRequest request;
        try {
            request = (HttpServletRequest) PolicyContext.getContext(SERVLET_CONTEXT_KEY);
        } catch (PolicyContextException e1) {
            String err = "Unable find the local servlet request: the JACC Policy Context cannot be accessed: " + e1.getMessage(); //$NON-NLS-1$
            logger.error(err, e1);
            throw new XtentisException(err);
        }
        if (request != null)
            request.getSession().invalidate();
    }

    public void resetILocalUsers() throws XtentisException {
    }

    public void setRoles(HashSet<String> roles) {
    }

    public void setUniverse(UniversePOJO universe) {
    }

    public void setUserXML(String userXML) {
    }

    public void setUsername(String username) {
    }

    public boolean userCanRead(Class<?> objectTypeClass, String instanceId) throws XtentisException {
        return true;
    }

    public boolean userCanWrite(Class<?> objectTypeClass, String instanceId) throws XtentisException {
        return true;
    }

    public boolean userItemCanRead(ItemPOJO item) throws XtentisException {
        return true;
    }

    public boolean userItemCanRead(ItemPOJOPK item) throws XtentisException {
        return true;
    }

    public boolean userItemCanWrite(ItemPOJO item, String datacluster, String concept) throws XtentisException {
        return true;
    }

    public boolean userItemCanWrite(ItemPOJOPK item, String datacluster, String concept) throws XtentisException {
        return true;
    }
}
