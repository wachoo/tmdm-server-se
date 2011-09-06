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

/**
 *
 */
public class ReferenceUnaryFieldMetadata extends ReferenceFieldMetadata {

    private final boolean isKey;

    private final String name;

    private final String type;

    private final TypeRef referencedType;


    public ReferenceUnaryFieldMetadata(TypeMetadata containingType, boolean isKey, String name, String type, TypeRef referencedType, String foreignKeyInfo, boolean fkIntegrity, boolean allowFKIntegrityOverride) {
        super(foreignKeyInfo, containingType, allowFKIntegrityOverride, fkIntegrity);
        this.isKey = isKey;
        this.name = name;
        this.type = type;
        this.referencedType = referencedType;
    }

    public String getForeignTypeName() {
        return referencedType.getReferencedTypeName();
    }

    public String getForeignIdField() {
        return referencedType.getReferencedKey();
    }

    public String getForeignIdType() {
        return referencedType.getReferencedKeyType();
    }

    public String getName() {
        return name;
    }

    public boolean isKey() {
        return isKey;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getType() {
        return type;
    }
}
