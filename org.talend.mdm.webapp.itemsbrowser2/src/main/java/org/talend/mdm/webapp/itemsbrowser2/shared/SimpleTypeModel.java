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
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataType;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * DOC chliu  class global comment. Detailled comment
 */
public class SimpleTypeModel extends TypeModel implements IsSerializable {

    
    private List<FacetModel> facets;

    public SimpleTypeModel(String name,DataType typeName) {
        super(name,typeName);
    }

    public List<FacetModel> getFacets() {
        return facets;
    }

    public void setFacets(List<FacetModel> facets) {
        this.facets = facets;
    }

    public boolean isSimpleType() {
        return true;
    }

    public boolean hasEnumeration() {
        if (facets == null){
            return false;
        }
        for (FacetModel facet : facets){
            if (facet.getName().equals("enumeration")){
                return true;
            }
        }
        return false;
    }
}
