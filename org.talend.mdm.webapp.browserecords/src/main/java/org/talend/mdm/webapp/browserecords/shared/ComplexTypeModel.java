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
package org.talend.mdm.webapp.browserecords.shared;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.DataType;

/**
 * DOC chliu class global comment. Detailled comment
 */
public class ComplexTypeModel extends TypeModel {

    private List<TypeModel> subTypes = new ArrayList<TypeModel>();

    private List<ComplexTypeModel> reusableTypes = new ArrayList<ComplexTypeModel>();

    /**
     * DOC HSHU ComplexTypeModel constructor comment.
     */
    public ComplexTypeModel() {
        super();
    }

    public ComplexTypeModel(String name, DataType dataType) {
        super(name, dataType);
    }

    public List<TypeModel> getSubTypes() {
        return getRealTypeModel().subTypes;
    }

    /**
     * DOC HSHU Comment method "addSubType".
     */
    public void addSubType(TypeModel subType) {
        if (getRealTypeModel().subTypes != null) {
            getRealTypeModel().subTypes.add(subType);
        }
    }

    private ComplexTypeModel getRealTypeModel() {
        for (TypeModel tm : reusableTypes) {
            if (tm.getName().equals(this.getRealType())) {
                return (ComplexTypeModel) tm;
            }
        }
        return this;
    }
    
    public void addComplexReusableTypes(ComplexTypeModel reusableType) {
        reusableTypes.add(reusableType);
    }

    public List<ComplexTypeModel> getReusableComplexTypes() {
        return reusableTypes;
    }

    public boolean isSimpleType() {
        return false;
    }

    public boolean hasEnumeration() {
        return false;
    }
}
