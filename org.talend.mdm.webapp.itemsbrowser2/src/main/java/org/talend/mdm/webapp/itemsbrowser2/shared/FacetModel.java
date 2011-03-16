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
public class FacetModel implements Serializable, IsSerializable {
    
    private static final long serialVersionUID = -8967586064432179060L;

    private String name;
    
    private String value;
    
    
    /**
     * DOC HSHU FacetModel constructor comment.
     */
    public FacetModel() {

    }
    
    public FacetModel(String name, String value){
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
