// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.model;

public class ForeignKeyBean extends ItemBaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String id;

    private String displayInfo;

    private String foreignKeyPath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.getProperties().keySet().size() > 1) {
            for (String key : this.getProperties().keySet()) {
                if (!key.equals("i")) { //$NON-NLS-1$
                    sb.append(this.getProperties().get(key));
                    sb.append("-"); //$NON-NLS-1$
                }
            }
            return sb.toString().substring(0, sb.toString().length() - 1);
        } else {
            if(displayInfo == null && foreignKeyPath == null)
                return id;
            else
                return displayInfo;
        }
    }

    public String getDisplayInfo() {
        return displayInfo;
    }

    public void setDisplayInfo(String displayInfo) {
        this.displayInfo = displayInfo;
    }

    public String getForeignKeyPath() {
        return foreignKeyPath;
    }

    public void setForeignKeyPath(String foreignKeyPath) {
        this.foreignKeyPath = foreignKeyPath;
    }

    public String getFullString() {
        return foreignKeyPath + "-" + id; //$NON-NLS-1$
    }
}