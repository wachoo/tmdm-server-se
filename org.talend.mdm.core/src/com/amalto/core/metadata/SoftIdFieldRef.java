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

/**
 *
 */
public class SoftIdFieldRef implements FieldMetadata {

    private final MetadataRepository repository;

    private final String typeName;

    private final String fieldName;

    public SoftIdFieldRef(MetadataRepository metadataRepository, String typeName) {
        this(metadataRepository, typeName, null);
    }

    public SoftIdFieldRef(MetadataRepository metadataRepository, String typeName, String fieldName) {
        repository = metadataRepository;
        this.typeName = typeName;
        this.fieldName = fieldName;
    }

    private FieldMetadata getField() {
        ComplexTypeMetadata type = (ComplexTypeMetadata) repository.getType(typeName);
        if (type == null) {
            type = (ComplexTypeMetadata) repository.getNonInstantiableType(typeName);
        }
        if (type == null) {
            throw new IllegalArgumentException("Type '" + typeName + "' does not exist.");
        }
        List<FieldMetadata> keyFields = type.getKeyFields();
        if (keyFields.isEmpty()) {
            throw new IllegalArgumentException("Type '" + typeName + "' does not own a key and no FK field was defined.");
        }

        if (fieldName == null) {
            if (keyFields.size() == 1) {
                return keyFields.get(0);
            } else {
                return new CompoundFieldMetadata(keyFields.toArray(new FieldMetadata[keyFields.size()]));
            }
        } else {
            return type.getField(fieldName);
        }
    }

    public String getName() {
        if (fieldName != null) {
            return fieldName;
        } else {
            return getField().getName();
        }
    }

    public boolean isKey() {
        return true; // No need to look for the field, this is an id
    }

    public TypeMetadata getType() {
        return getField().getType();
    }

    public ComplexTypeMetadata getContainingType() {
        return getField().getContainingType();
    }

    public void setContainingType(ComplexTypeMetadata typeMetadata) {
        getField().setContainingType(typeMetadata);
    }

    public FieldMetadata freeze() {
        return getField().freeze();
    }

    public void promoteToKey() {
        getField().promoteToKey();
    }

    public TypeMetadata getDeclaringType() {
        return getField().getDeclaringType();
    }

    public void adopt(ComplexTypeMetadata metadata, MetadataRepository repository) {
        getField().adopt(metadata, repository);
    }

    public FieldMetadata copy(MetadataRepository repository) {
        return new SoftIdFieldRef(this.repository, typeName);
    }

    public List<String> getHideUsers() {
        return getField().getHideUsers();
    }

    public List<String> getWriteUsers() {
        return getField().getHideUsers();
    }

    public boolean isMany() {
        return getField().isMany();
    }

    public boolean isMandatory() {
        return getField().isMandatory();
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return getField().accept(visitor);
    }

    @Override
    public String toString() {
        return getField().toString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof FieldMetadata && getField().equals(o);
    }
}
