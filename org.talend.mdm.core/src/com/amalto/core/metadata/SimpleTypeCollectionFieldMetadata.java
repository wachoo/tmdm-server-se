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
public class SimpleTypeCollectionFieldMetadata implements FieldMetadata {

    private final String name;

    private final SimpleTypeFieldMetadata fieldMetadata;

    private final boolean isKey;

    public SimpleTypeCollectionFieldMetadata(String name, boolean key, SimpleTypeFieldMetadata fieldMetadata) {
        this.name = name;
        this.fieldMetadata = fieldMetadata;
        isKey = key;
    }

    public String getName() {
        return name;
    }

    public boolean isKey() {
        return isKey;
    }

    public String getType() {
        return fieldMetadata.getType();
    }

    public boolean hasForeignKeyInfo() {
        return false;
    }

    public String getForeignKeyInfoField() {
        throw new IllegalStateException("This type of field can't be a foreign key");
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
