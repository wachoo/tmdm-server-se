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
public class SimpleTypeFieldMetadata implements FieldMetadata {

    private final String name;

    private final String fieldTypeName;

    private final boolean isKey;

    private final TypeMetadata containingType;

    public SimpleTypeFieldMetadata(TypeMetadata containingType, boolean key, String name, String fieldTypeName) {
        this.containingType = containingType;
        isKey = key;
        this.name = name;
        this.fieldTypeName = fieldTypeName;
    }

    public String getName() {
        return name;
    }

    public boolean isKey() {
        return isKey;
    }

    public String getType() {
        return fieldTypeName;
    }

    public boolean hasForeignKeyInfo() {
        return false; // This type of field can't be a foreign key
    }

    public String getForeignKeyInfoField() {
        throw new IllegalStateException("This type of field can't be a foreign key");
    }

    public TypeMetadata getContainingType() {
        return containingType;
    }

    public boolean isFKIntegrity() {
        return false;
    }

    public boolean allowFKIntegrityOverride() {
        return false;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
