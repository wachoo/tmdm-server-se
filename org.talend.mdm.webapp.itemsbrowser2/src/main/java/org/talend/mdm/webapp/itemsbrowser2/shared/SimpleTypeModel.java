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

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataType;

/**
 * DOC chliu class global comment. Detailled comment
 */
public class SimpleTypeModel extends TypeModel {

    private List<String> enumeration;

    private List<FacetModel> facets;

    /**
     * DOC HSHU SimpleTypeModel constructor comment.
     */
    public SimpleTypeModel() {
        super();
    }

    public SimpleTypeModel(String name, DataType typeName) {
        super(name, typeName);
    }

    public List<String> getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(List<String> enumeration) {
        this.enumeration = enumeration;
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

        if (enumeration != null && enumeration.size() > 0)
            return true;

        return false;
    }
}
