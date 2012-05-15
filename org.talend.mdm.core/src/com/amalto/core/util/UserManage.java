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

import java.util.Set;


/**
 * DOC hshu  class global comment. Detailed comment
 */
public abstract class UserManage {
    
    public abstract int getWebUsers();

    public abstract int getViewerUsers();

    public abstract int getNormalUsers();
    
    public abstract int getNBAdminUsers();
    
    public abstract int getActiveUsers();
    
    public abstract boolean isExistUser(User user);
    
    public abstract boolean isUpdateDCDM(User user);
    
    public abstract boolean isActiveUser(User user);
    
    public abstract Set<String> getOriginalRole(User user);
    
    protected abstract int getUserCount(String role);

}
