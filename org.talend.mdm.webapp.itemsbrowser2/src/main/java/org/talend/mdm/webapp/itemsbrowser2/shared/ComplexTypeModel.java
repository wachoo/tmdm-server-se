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

import java.util.ArrayList;
import java.util.List;


/**
 * DOC chliu  class global comment. Detailled comment
 */
public class ComplexTypeModel extends TypeModel {

    List<ComplexTypeModel> subComplexTypes = new ArrayList<ComplexTypeModel>();
    
    List<SimpleTypeModel> subSimpleTypes = new ArrayList<SimpleTypeModel>();
    
    public ComplexTypeModel() {
        super();
    }
    
    public ComplexTypeModel(String typeName, String label) {
        super(typeName, label);
    }

    public List<ComplexTypeModel> getSubComplexTypes() {
        return subComplexTypes;
    }

    public void setSubComplexTypes(List<ComplexTypeModel> subComplexTypes) {
        this.subComplexTypes = subComplexTypes;
    }

    public List<SimpleTypeModel> getSubSimpleTypes() {
        return subSimpleTypes;
    }

    public void setSubSimpleTypes(List<SimpleTypeModel> subSimpleTypes) {
        this.subSimpleTypes = subSimpleTypes;
    }

    public boolean isSimpleType() {
        return false;
    }

    public boolean hasEnumeration() {
        return false;
    }
}
