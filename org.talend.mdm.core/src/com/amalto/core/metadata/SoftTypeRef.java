/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

/**
*
*/
class SoftTypeRef implements TypeRef {

    private final MetadataRepository repository;

    private final String fieldTypeName;

    public SoftTypeRef(MetadataRepository repository, String fieldTypeName) {
        this.repository = repository;
        this.fieldTypeName = fieldTypeName;
    }

    public String getReferencedTypeName() {
        return fieldTypeName;
    }

    public String getReferencedKey() {
        ComplexTypeMetadata type = (ComplexTypeMetadata) repository.getType(fieldTypeName);
        if (type != null) {
            List<FieldMetadata> keyFields = type.getKeyFields();
            if (!keyFields.isEmpty()) {
                return keyFields.get(0).getName();
            }
        }
        return null;
    }

    public String getReferencedKeyType() {
        ComplexTypeMetadata type = (ComplexTypeMetadata) repository.getType(fieldTypeName);
        if (type != null) {
            List<FieldMetadata> keyFields = type.getKeyFields();
            if (!keyFields.isEmpty()) {
                FieldMetadata fieldMetadata = keyFields.get(0);
                if (fieldMetadata instanceof ReferenceFieldMetadata) {
                    return ((ReferenceFieldMetadata) fieldMetadata).getForeignIdType();
                } else {
                    return fieldMetadata.getType();
                }
            }
        }
        return null;
    }

}
