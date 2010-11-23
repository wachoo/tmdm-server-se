// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================/
package org.talend.mdm.webapp.gxt.framework.shared;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class NavFolder extends BaseTreeModel implements IsSerializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1978752831139866894L;


    // Types
    public static final String _UNKNOWN_ = "_UNKNOWN_"; //$NON-NLS-1$

    public static final String _ROOT_ = "_ROOT_"; //$NON-NLS-1$

    public static final String _CATALOG_ = "_CATALOG_"; //$NON-NLS-1$
    
    public static final String _ENTRY_ = "_ENTRY_"; //$NON-NLS-1$

    public NavFolder() {
    }
    
    public NavFolder(String type) {
        set("type", type); //$NON-NLS-1$
    }
    
    public NavFolder(String name, String type) {
        this(name);
        set("type", type); //$NON-NLS-1$
    }

    public NavFolder(String name, BaseTreeModel[] children) {
        this(name);
        for (int i = 0; i < children.length; i++) {
            add(children[i]);
        }
    }
    
    public NavFolder(String id,String name, String type) {
        set("id", id);
        set("name", name);
        set("type", type); //$NON-NLS-1$
    }
    
    public void setId(String id) {
        set("id", id); 
    }

    public String getId() {
        return (String) get("id"); //$NON-NLS-1$
    }
    
    public void setApplication(String application) {
        set("application", application); 
    }

    public String getApplication() {
        return (String) get("application"); //$NON-NLS-1$
    }
    
    public void setContext(String context) {
        set("context", context); 
    }

    public String getContext() {
        return (String) get("context"); //$NON-NLS-1$
    }
    
    public void setIcon(String icon) {
        set("icon", icon); 
    }

    public String getIcon() {
        return (String) get("icon"); //$NON-NLS-1$
    }
    
    public void setName(String name) {
        set("name", name); 
    }

    public String getName() {
        return (String) get("name"); //$NON-NLS-1$
    }

    public String getType() {
        return (String) get("type"); //$NON-NLS-1$
    }


    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NavFolder other = (NavFolder) obj;

        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;

        return true;
    }

}
