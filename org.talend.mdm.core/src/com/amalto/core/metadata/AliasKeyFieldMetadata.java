// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.metadata;

import java.util.List;

public class AliasKeyFieldMetadata extends SimpleTypeFieldMetadata {

    private String realFieldName;

    public AliasKeyFieldMetadata(ComplexTypeMetadata containingType, boolean isKey, boolean isMany, boolean isMandatory,
            String name, TypeMetadata fieldType, List<String> allowWriteUsers, List<String> hideUsers, String realFieldName) {
        super(containingType, isKey, isMany, isMandatory, name, fieldType, allowWriteUsers, hideUsers);
        this.realFieldName = realFieldName;
    }

    public boolean equals(Object o) {
        boolean result = super.equals(o);
        if (result) {
            AliasKeyFieldMetadata another = (AliasKeyFieldMetadata) o;
            result = this.realFieldName.equals(another.realFieldName);
        }
        return result;
    }

}
