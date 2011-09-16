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
public class SoftIdFieldRef implements FieldMetadata {
    private final MetadataRepository repository;
    private final String typeName;

    public SoftIdFieldRef(MetadataRepository metadataRepository, String typeName) {
        repository = metadataRepository;
        this.typeName = typeName;
    }

    private FieldMetadata getField() {
        return repository.getComplexType(typeName).getKeyFields().get(0);
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
        return getField().hasForeignKeyInfo();
    }

    public void adopt(ComplexTypeMetadata metadata) {
        getField().adopt(metadata);
    }

    public FieldMetadata copy() {
        return new SoftIdFieldRef(repository, typeName);
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return getField().accept(visitor);
    }
}
