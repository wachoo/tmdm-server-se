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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 */
public class SoftFieldRef implements FieldMetadata {
    private final MetadataRepository repository;
    private final String fieldTypeName;
    private final String fieldName;

    public SoftFieldRef(MetadataRepository metadataRepository, String fieldTypeName, String fieldName) {
        this.repository = metadataRepository;
        this.fieldTypeName = fieldTypeName;
        this.fieldName = fieldName;
    }

    private FieldMetadata getField() {
        return repository.getType(fieldTypeName).getField(fieldName);
    }

    public String getName() {
        return getField().getName();
    }

    public boolean isKey() {
        return getField().isKey();
    }

    public String getType() {
        return getField().getType();
    }

    public boolean hasForeignKeyInfo() {
        return getField().hasForeignKeyInfo();
    }

    public String getForeignKeyInfoField() {
        return getField().getForeignKeyInfoField();
    }

    public TypeMetadata getContainingType() {
        return getField().getContainingType();
    }

    public void setContainingType(TypeMetadata typeMetadata) {
        getField().setContainingType(typeMetadata);
    }

    public TypeMetadata getDeclaringType() {
        return getField().getDeclaringType();
    }

    public boolean isFKIntegrity() {
        return getField().isFKIntegrity();
    }

    public boolean allowFKIntegrityOverride() {
        return getField().allowFKIntegrityOverride();
    }

    public void adopt(ComplexTypeMetadata metadata) {
        FieldMetadata copy = getField().copy();
        copy.setContainingType(metadata);
        metadata.addField(copy);
    }

    public FieldMetadata copy() {
        throw new NotImplementedException();
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return getField().accept(visitor);
    }
}
