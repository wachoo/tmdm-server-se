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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class SimpleTypeMetadata extends AbstractMetadataExtensible implements TypeMetadata {

    private final String nameSpace;

    private final List<TypeMetadata> superTypes = new LinkedList<TypeMetadata>();

    private String name;

    private boolean isFrozen = false;

    public SimpleTypeMetadata(String nameSpace, String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        this.name = name;
        this.nameSpace = nameSpace;
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return superTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isFrozen) {
            throw new IllegalStateException("Cannot change name after type was frozen.");
        }
        this.name = name;
    }

    public String getNamespace() {
        return nameSpace;
    }

    public boolean isAssignableFrom(TypeMetadata type) {
        // Check one level of inheritance
        Collection<TypeMetadata> superTypes = getSuperTypes();
        for (TypeMetadata superType : superTypes) {
            if (type.getName().equals(superType.getName())) {
                return true;
            }
        }
        // Checks in type inheritance hierarchy.
        for (TypeMetadata superType : superTypes) {
            if (superType.isAssignableFrom(type)) {
                return true;
            }
        }
        return getName().equals(type.getName());
    }

    public TypeMetadata copy(MetadataRepository repository) {
        SimpleTypeMetadata copy = new SimpleTypeMetadata(nameSpace, name);
        for (TypeMetadata superType : superTypes) {
            copy.addSuperType(superType.copy(repository), repository);
        }
        return copy;
    }

    public TypeMetadata copyShallow() {
        return new SimpleTypeMetadata(nameSpace, name);
    }

    public TypeMetadata freeze(ValidationHandler handler) {
        if (!superTypes.isEmpty()) {
            List<TypeMetadata> thisSuperTypes = new LinkedList<TypeMetadata>(superTypes);
            superTypes.clear();
            for (TypeMetadata superType : thisSuperTypes) {
                if (isInstantiable() == superType.isInstantiable()) {
                    superType = superType.freeze(handler);
                    superTypes.add(superType);
                } else {
                    handler.error("Non instantiable type cannot inherits from entity type.");
                }
            }
        }
        isFrozen = true;
        return this;
    }

    @Override
    public boolean isInstantiable() {
        return false;
    }

    @Override
    public boolean isFrozen() {
        return true;
    }

    public void addSuperType(TypeMetadata superType, MetadataRepository repository) {
        superTypes.add(superType);
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return '[' + nameSpace + ':' + name + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleTypeMetadata)) return false;

        SimpleTypeMetadata that = (SimpleTypeMetadata) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (nameSpace != null ? !nameSpace.equals(that.nameSpace) : that.nameSpace != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nameSpace != null ? nameSpace.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
