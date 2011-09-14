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

    private final TypeMetadata referencedType;

    public ReferenceUnaryFieldMetadata(TypeMetadata containingType,
                                       String name,
                                       TypeMetadata referencedType,
                                       FieldMetadata referencedField,
                                       String foreignKeyInfo,
                                       boolean isKey,
                                       boolean fkIntegrity,
                                       boolean allowFKIntegrityOverride) {
        super(name, containingType, referencedType, referencedField, allowFKIntegrityOverride, fkIntegrity, foreignKeyInfo);
        this.isKey = isKey;
        this.referencedType = referencedType;
    }

    public String getForeignTypeName() {
        return referencedType.getName();
    }

    public String getForeignIdField() {
        return referencedField.getName();
    }

    public String getForeignIdType() {
        return referencedField.getType();
    }

    public boolean isKey() {
        return isKey;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public FieldMetadata copy() {
        return new ReferenceUnaryFieldMetadata(containingType, name, referencedType, referencedField, foreignKeyInfo, isKey, isFKIntegrity, allowFKIntegrityOverride);
    }

}
