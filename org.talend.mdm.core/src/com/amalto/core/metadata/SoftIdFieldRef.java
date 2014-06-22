/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
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

    private FieldMetadata frozenField;

    public SoftIdFieldRef(MetadataRepository metadataRepository, String typeName) {
        this(metadataRepository, typeName, null);
    }

    public SoftIdFieldRef(MetadataRepository metadataRepository, String typeName, String fieldName) {
        repository = metadataRepository;
        this.typeName = typeName;
        this.fieldName = fieldName;
    }

    private FieldMetadata getField() {
        if (frozenField == null) {
            throw new IllegalStateException("Field reference should be frozen before used.");
        }
        return frozenField;
    }

    @Override
    public synchronized void setData(String key, Object data) {
        getField().setData(key, data);
    }

    @Override
    public <X> X getData(String key) {
        return getField().<X> getData(key);
    }

    @Override
    public String getName() {
        if (fieldName != null) {
            return fieldName;
        } else {
            return getField().getName();
        }
    }

    @Override
    public boolean isKey() {
        return true; // No need to look for the field, this is an id
    }

    @Override
    public TypeMetadata getType() {
        return getField().getType();
    }

    @Override
    public ComplexTypeMetadata getContainingType() {
        return getField().getContainingType();
    }

    @Override
    public void setContainingType(ComplexTypeMetadata typeMetadata) {
        getField().setContainingType(typeMetadata);
    }

    @Override
    public FieldMetadata freeze(ValidationHandler handler) {
        if (frozenField != null) {
            return frozenField;
        }
        ComplexTypeMetadata type = (ComplexTypeMetadata) repository.getType(typeName);
        if (type == null) {
            type = (ComplexTypeMetadata) repository.getNonInstantiableType(repository.getUserNamespace(), typeName);
        }
        if (type == null) {
            handler.error("Type '" + typeName + "' does not exist.");
            return this;
        }
        List<FieldMetadata> keyFields = type.getKeyFields();
        if (keyFields.isEmpty()) {
            handler.error("Type '" + typeName + "' does not own a key and no FK field was defined.");
            return this;
        }
        if (fieldName == null) {
            if (keyFields.size() == 1) {
                frozenField = keyFields.get(0);
            } else {
                frozenField = new CompoundFieldMetadata(keyFields.toArray(new FieldMetadata[keyFields.size()]));
            }
        } else {
            frozenField = type.getField(fieldName);
        }
        return frozenField;
    }

    @Override
    public void promoteToKey() {
        getField().promoteToKey();
    }

    @Override
    public TypeMetadata getDeclaringType() {
        return getField().getDeclaringType();
    }

    @Override
    public void adopt(ComplexTypeMetadata metadata, MetadataRepository repository) {
        getField().adopt(metadata, repository);
    }

    @Override
    public FieldMetadata copy(MetadataRepository repository) {
        return new SoftIdFieldRef(this.repository, typeName);
    }

    @Override
    public List<String> getHideUsers() {
        return getField().getHideUsers();
    }

    @Override
    public List<String> getWriteUsers() {
        return getField().getHideUsers();
    }

    @Override
    public boolean isMany() {
        return getField().isMany();
    }

    @Override
    public boolean isMandatory() {
        return getField().isMandatory();
    }

    @Override
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
