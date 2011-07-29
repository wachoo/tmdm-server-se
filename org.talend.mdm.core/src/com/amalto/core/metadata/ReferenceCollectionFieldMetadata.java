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
public class ReferenceCollectionFieldMetadata implements FieldMetadata {

    private final String name;

    private final ReferenceFieldMetadata fieldMetadata;

    private final boolean isKey;

    private final String foreignKeyInfo;

    public ReferenceCollectionFieldMetadata(String name, boolean key, ReferenceFieldMetadata fieldMetadata, String foreignKeyInfo) {
        this.name = name;
        this.fieldMetadata = fieldMetadata;
        isKey = key;
        this.foreignKeyInfo = foreignKeyInfo;
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
        return foreignKeyInfo != null;
    }

    public String getForeignKeyInfoField() {
        return foreignKeyInfo;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getForeignIdType() {
        return fieldMetadata.getForeignIdType();
    }

    public String getForeignTypeName() {
        return fieldMetadata.getForeignTypeName();
    }

    public String getForeignIdField() {
        return fieldMetadata.getForeignIdField();
    }

}
