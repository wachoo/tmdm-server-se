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
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * DOC chliu  class global comment. Detailled comment
 */
public abstract class TypeModel implements Serializable, IsSerializable {
    
    private String typeName;

    private String label;
    
    public TypeModel() {
        super();
    }

    public TypeModel(String typeName, String label) {
        super();
        this.typeName = typeName;
        this.label = label;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public abstract boolean isSimpleType();
}
