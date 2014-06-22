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

    public SoftTypeRef(MetadataRepository repository, String namespace, String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("Type name cannot be null.");
        }

        this.repository = repository;
        this.typeName = typeName;
        this.namespace = namespace;
        this.fieldRef = null;
    }

    private SoftTypeRef(MetadataRepository repository, FieldMetadata fieldRef) {
        if (fieldRef == null) {
            throw new IllegalArgumentException("Field reference cannot be null.");
        }
        this.repository = repository;
        this.typeName = null;
        this.namespace = null;
        this.fieldRef = fieldRef;
    }

    private TypeMetadata getType() {
        if (typeName != null) {
            TypeMetadata type = repository.getType(namespace, typeName);
            if (type == null) {
                type = repository.getNonInstantiableType(typeName);
            }
            if (type == null) {
                throw new IllegalArgumentException("Type '" + typeName + "' (namespace: '" + namespace + "') is not present in type repository.");
            }
            return type;
        } else {
            return fieldRef.getContainingType();
        }
    }

    private ComplexTypeMetadata getTypeAsComplex() {
        TypeMetadata type = getType();
        if (!(type instanceof ComplexTypeMetadata)) {
            throw new IllegalArgumentException("Type '" + typeName + "' was expected to be a complex type (but was " + type.getClass().getName() + ").");
        }
        return (ComplexTypeMetadata) type;
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return getType().getSuperTypes();
    }

    public String getName() {
        return typeName;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isAbstract() {
        return getType().isAbstract();
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
            return new SoftTypeRef(repository, namespace, typeName);
        } else {
            return new SoftTypeRef(repository, fieldRef.copy(repository));
        }
    }

    public TypeMetadata copyShallow() {
        throw new NotImplementedException(); // Not supported
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

    public void registerKey(FieldMetadata keyField) {
        getTypeAsComplex().registerKey(keyField);
    }
}
