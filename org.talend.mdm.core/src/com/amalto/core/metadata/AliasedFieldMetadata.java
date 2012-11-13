/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */
package com.amalto.core.metadata;

import java.util.List;

public class AliasedFieldMetadata extends SimpleTypeFieldMetadata {

    private String realFieldName;

    public AliasedFieldMetadata(ComplexTypeMetadata containingType, boolean isKey, boolean isMany, boolean isMandatory,
                                String name, TypeMetadata fieldType, List<String> allowWriteUsers, List<String> hideUsers, String realFieldName) {
        super(containingType, isKey, isMany, isMandatory, name, fieldType, allowWriteUsers, hideUsers);
        this.realFieldName = realFieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AliasedFieldMetadata)) return false;
        if (!super.equals(o)) return false;

        AliasedFieldMetadata that = (AliasedFieldMetadata) o;

        return !(realFieldName != null ? !realFieldName.equals(that.realFieldName) : that.realFieldName != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (realFieldName != null ? realFieldName.hashCode() : 0);
        return result;
    }
}
