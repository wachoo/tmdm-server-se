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

import com.amalto.core.objects.ItemPOJO;
import com.amalto.core.metadata.ClassRepository;
import com.amalto.core.objects.universe.UniversePOJO;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordWriter;
import com.amalto.core.storage.record.SystemDataRecordXmlWriter;
import com.amalto.core.util.XtentisException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

public abstract class ILocalUser implements IBeanDelegator {

    public ILocalUser getILocalUser() throws XtentisException {
        return null;
    }

    public HashSet<String> getRoles() {
        HashSet<String> set = new HashSet<String>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                set.add(authority.getAuthority());
            }
        } else {
            set.add("administration"); //$NON-NLS-1$
            set.add("authenticated"); //$NON-NLS-1$
        }
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
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage systemStorage = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM, null);
        ComplexTypeMetadata userType = systemStorage.getMetadataRepository().getComplexType("User"); //$NON-NLS-1$
        UserQueryBuilder qb = from(userType).where(eq(userType.getField("username"), getUsername())); //$NON-NLS-1$
        DataRecordWriter writer = new SystemDataRecordXmlWriter((ClassRepository) systemStorage.getMetadataRepository(), userType);
        StringWriter userXml = new StringWriter();
        try {
            systemStorage.begin();
            StorageResults results = systemStorage.fetch(qb.getSelect());
            for (DataRecord result : results) {
                writer.write(result, userXml);
            }
            systemStorage.commit();
        } catch (IOException e) {
            systemStorage.rollback();
            throw new RuntimeException("Could not access user record.", e);
        }
        return userXml.toString();
    }

    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getUsername(); 
    }

    public String getPassword() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getPassword();
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

    public boolean userItemCanWrite(ItemPOJO item, String datacluster, String concept) throws XtentisException {
        return true;
    }

}
