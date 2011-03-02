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

import java.util.List;


/**
 * DOC chliu  class global comment. Detailled comment
 */
public class SimpleTypeModel extends TypeModel {

    private boolean primitive;
    
    private boolean restriction;
    
    private List<FacetModel> facets;

    public SimpleTypeModel() {
        super();
    }

    public SimpleTypeModel(String typeName, String label, boolean primitive,
            boolean restriction, List<FacetModel> facets) {
        super(typeName, label);
        this.primitive = primitive;
        this.restriction = restriction;
        this.facets = facets;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public boolean isRestriction() {
        return restriction;
    }

    public void setRestriction(boolean restriction) {
        this.restriction = restriction;
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
