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
package com.amalto.core.delegator;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.XtentisException;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public abstract class ILocalUser implements IBeanDelegator {

    public Subject getICurrentSubject() throws XtentisException {
        return null; // TODO
    }

    public ILocalUser getILocalUser() throws XtentisException {
        return null;
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
        return MDMConfiguration.getAdminUser(); // TODO
    }

    public String getPassword() {
        return MDMConfiguration.getAdminUser(); // TODO
    }

    public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException {
        return true;
    }

    public void logout() throws XtentisException {
        // TODO
    }

    public void resetILocalUsers() throws XtentisException {
    }

    public void setRoles(HashSet<String> roles) {
    }

    public void setUniverse(UniversePOJO universe) {
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

    public boolean userItemCanWrite(ItemPOJO item, String datacluster, String concept) throws XtentisException {
        return true;
    }

}
