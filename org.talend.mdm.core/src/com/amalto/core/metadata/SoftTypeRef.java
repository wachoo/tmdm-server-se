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

import org.apache.commons.lang.NotImplementedException;

import java.util.Collection;
import java.util.List;

/**
 * Represents a reference to a {@link ComplexTypeMetadata} type where methods are evaluated using
 * {@link MetadataRepository#getComplexType(String)} calls. This is useful to reference types that might not be already
 * parsed by {@link MetadataRepository#load(java.io.InputStream)}.
 */
public class SoftTypeRef implements ComplexTypeMetadata {

    private final MetadataRepository repository;

    private final String typeName;

    private final FieldMetadata fieldRef;

    private final String namespace;

    private final boolean instantiable;

    private SoftTypeRef(MetadataRepository repository, FieldMetadata fieldRef) {
        if (fieldRef == null) {
            throw new IllegalArgumentException("Field reference cannot be null.");
        }
        this.repository = repository;
        this.typeName = null;
        this.namespace = null;
        this.fieldRef = fieldRef;
        this.instantiable = true;
    }

    public SoftTypeRef(MetadataRepository repository, String namespace, String typeName, boolean isInstantiable) {
        if (typeName == null) {
            throw new IllegalArgumentException("Type name cannot be null.");
        }
        this.repository = repository;
        this.typeName = typeName;
        this.namespace = namespace;
        this.fieldRef = null;
        this.instantiable = isInstantiable;
    }

    private TypeMetadata getType() {
        if (typeName != null) {
            TypeMetadata type;
            if (instantiable) {
                type = repository.getType(namespace, typeName);
            } else {
                type = repository.getNonInstantiableType(namespace, typeName);
            }
            if (type == null) {
                if (instantiable) {
                    throw new IllegalArgumentException("Entity type '" + typeName + "' (namespace: '" + namespace + "') is not present in type repository.");
                } else {
                    throw new IllegalArgumentException("Non entity type '" + typeName + "' (namespace: '" + namespace + "') is not present in type repository.");
                }
            }
            return type;
        } else {
            return fieldRef.getContainingType();
        }
    }

    private ComplexTypeMetadata getTypeAsComplex() {
        TypeMetadata type = getType();
        if (!(type instanceof ComplexTypeMetadata)) {
            throw new IllegalArgumentException("Type '" + typeName + "' was expected to be a complex type (but was "
                    + type.getClass().getName() + ").");
        }
        return (ComplexTypeMetadata) type;
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return getType().getSuperTypes();
    }

    public String getName() {
        return typeName;
    }

    public void setName(String name) {
        getType().setName(name);
    }

    public String getNamespace() {
        return namespace;
    }

    public FieldMetadata getField(String fieldName) {
        return getTypeAsComplex().getField(fieldName);
    }

    public List<FieldMetadata> getFields() {
        return getTypeAsComplex().getFields();
    }

    public boolean isAssignableFrom(TypeMetadata type) {
        return getType().isAssignableFrom(type);
    }

    public TypeMetadata copy(MetadataRepository repository) {
        if (typeName != null) {
            return new SoftTypeRef(repository, namespace, typeName, instantiable);
        } else {
            return new SoftTypeRef(repository, fieldRef.copy(repository));
        }
    }

    public TypeMetadata copyShallow() {
        throw new NotImplementedException(); // Not supported
    }

    public TypeMetadata freeze() {
        return getType().freeze();
    }

    public void addSuperType(TypeMetadata superType, MetadataRepository repository) {
        getType().addSuperType(superType, repository);
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return getType().accept(visitor);
    }

    @Override
    public String toString() {
        if (typeName != null) {
            return typeName;
        } else if (fieldRef != null) {
            return fieldRef.toString();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TypeMetadata && getType().equals(obj);
    }

    public boolean isInstantiable() {
        return instantiable;
    }

    public List<FieldMetadata> getKeyFields() {
        return getTypeAsComplex().getKeyFields();
    }

    public void addField(FieldMetadata fieldMetadata) {
        getTypeAsComplex().addField(fieldMetadata);
    }

    public List<String> getWriteUsers() {
        return getTypeAsComplex().getWriteUsers();
    }

    public List<String> getHideUsers() {
        return getTypeAsComplex().getHideUsers();
    }

    public List<String> getDenyCreate() {
        return getTypeAsComplex().getDenyCreate();
    }

    public List<String> getDenyDelete(DeleteType type) {
        return getTypeAsComplex().getDenyDelete(type);
    }

    public String getSchematron() {
        return getTypeAsComplex().getSchematron();
    }

    public boolean hasField(String fieldName) {
        return getTypeAsComplex().hasField(fieldName);
    }

    public Collection<ComplexTypeMetadata> getSubTypes() {
        return getTypeAsComplex().getSubTypes();
    }

    public void registerSubType(ComplexTypeMetadata type) {
        getTypeAsComplex().registerSubType(type);
    }

    public void registerKey(FieldMetadata keyField) {
        getTypeAsComplex().registerKey(keyField);
    }
}
