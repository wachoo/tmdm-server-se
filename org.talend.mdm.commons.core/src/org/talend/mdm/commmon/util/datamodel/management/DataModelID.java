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
package org.talend.mdm.commmon.util.datamodel.management;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class DataModelID {

    private String uniqueID;

    private String revisionID;

    public DataModelID(String uniqueID, String revisionID) {
        super();
        this.uniqueID = uniqueID;
        this.revisionID = revisionID;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public String getRevisionID() {
        return revisionID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uniqueID == null) ? 0 : uniqueID.hashCode());
        result = prime * result + ((revisionID == null) ? 0 : revisionID.hashCode());
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
        DataModelID other = (DataModelID) obj;
        if (uniqueID == null) {
            if (other.uniqueID != null)
                return false;
        } else if (!uniqueID.equals(other.uniqueID))
            return false;
        if (revisionID == null) {
            if (other.revisionID != null)
                return false;
        } else if (!revisionID.equals(other.revisionID))
            return false;
        return true;
    }

}
