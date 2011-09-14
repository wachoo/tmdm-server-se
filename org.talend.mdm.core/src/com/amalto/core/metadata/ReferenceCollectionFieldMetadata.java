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
public class ReferenceCollectionFieldMetadata extends ReferenceFieldMetadata {

    private final String name;

    private final ReferenceUnaryFieldMetadata referencedField;

    private final boolean isKey;

    public ReferenceCollectionFieldMetadata(TypeMetadata containingType,
                                            String name,
                                            boolean key,
                                            ReferenceUnaryFieldMetadata referencedField,
                                            String foreignKeyInfo, boolean fkIntegrity, boolean allowFKIntegrityOverride) {
        super(name, containingType, referencedField.getContainingType(), referencedField, allowFKIntegrityOverride, fkIntegrity, foreignKeyInfo);
        this.name = name;
        this.referencedField = referencedField;
        isKey = key;
    }

    public boolean isKey() {
        return isKey;
    }

    public FieldMetadata copy() {
        return new ReferenceCollectionFieldMetadata(containingType, name, isKey, referencedField, foreignKeyInfo, isFKIntegrity, allowFKIntegrityOverride);
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getForeignIdType() {
        return referencedField.getForeignIdType();
    }

    public String getForeignTypeName() {
        return referencedField.getForeignTypeName();
    }

    public String getForeignIdField() {
        return referencedField.getForeignIdField();
    }

}
