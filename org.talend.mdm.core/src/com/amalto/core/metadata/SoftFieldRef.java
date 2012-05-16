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
public class SoftFieldRef implements FieldMetadata {

    private final MetadataRepository repository;

    private final SoftFieldRef containingField;

    private final TypeMetadata containingType;

    private final String fieldName;

    public SoftFieldRef(MetadataRepository metadataRepository, String fieldName, TypeMetadata containingType) {
        this.repository = metadataRepository;
        this.containingType = containingType;
        this.fieldName = fieldName;
        this.containingField = null;
    }

    public SoftFieldRef(MetadataRepository metadataRepository, String fieldName, SoftFieldRef containingField) {
        this.repository = metadataRepository;
        this.containingField = containingField;
        this.containingType = null;
        this.fieldName = fieldName;
    }

    private FieldMetadata getField() {
        if (containingType != null) {
            ComplexTypeMetadata type = repository.getComplexType(containingType.getName());
            if (type == null) {
                throw new IllegalArgumentException("Type '" + containingType + "' does not exist.");
            }
            FieldMetadata field = type.getField(fieldName);
            if (field == null) {
                throw new IllegalArgumentException("Type '" + containingType + "' does not own field '" + fieldName + "'.");
            }
            return field;
        } else {
            return containingField;
        }
    }

    public String getName() {
        return fieldName;
    }

    public boolean isKey() {
        return getField().isKey();
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

    public TypeMetadata getDeclaringType() {
        return getField().getDeclaringType();
    }

    public void adopt(ComplexTypeMetadata metadata, MetadataRepository repository) {
        FieldMetadata copy = getField().copy(this.repository);
        copy.setContainingType(metadata);
        metadata.addField(copy);
    }

    public FieldMetadata copy(MetadataRepository repository) {
        return new SoftFieldRef(repository, fieldName, containingType.copy(repository));
    }

    public List<String> getHideUsers() {
        return getField().getHideUsers();
    }

    public List<String> getWriteUsers() {
        return getField().getWriteUsers();
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
        if (containingType != null) {
            return containingType.toString() + "/" + fieldName; //$NON-NLS-1$
        } else {
            return containingField.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof FieldMetadata && getField().equals(o);
    }
}
