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
 * Represents a contained type (in a sense of nested contained type in a MDM entity type) but in this case actual type
 * definition is located in a shared type of the {@link MetadataRepository}. This happens when a type declares a element
 * of a type that is not a MDM entity type.
 */
public class ContainedComplexTypeRef extends ContainedComplexTypeMetadata {

    private ComplexTypeMetadata reference;

    private boolean isFrozen = false;

    public ContainedComplexTypeRef(ComplexTypeMetadata containerType, String nameSpace, String name, ComplexTypeMetadata reference) {
        super(containerType, nameSpace, name);
        this.reference = reference;
    }

    @Override
    public FieldMetadata getField(String fieldName) {
        return reference.getField(fieldName);
    }

    @Override
    public boolean hasField(String fieldName) {
        return reference.hasField(fieldName);
    }

    @Override
    public void addField(FieldMetadata fieldMetadata) {
    }

    @Override
    public Collection<TypeMetadata> getSuperTypes() {
        return reference.getSuperTypes();
    }

    @Override
    public List<FieldMetadata> getFields() {
        return reference.getFields();
    }

    @Override
    public TypeMetadata freeze(ValidationHandler handler) {
        if (isFrozen) {
            return this;
        }
        isFrozen = true;
        super.freeze(handler);
        reference = (ComplexTypeMetadata) reference.freeze(handler);
        return this;
    }

    @Override
    public String getName() {
        return reference.getName();
    }

    @Override
    public String getNamespace() {
        return reference.getNamespace();
    }

    @Override
    public boolean isAssignableFrom(TypeMetadata type) {
        return reference.isAssignableFrom(type);
    }

    @Override
    public Collection<ComplexTypeMetadata> getSubTypes() {
        List<ComplexTypeMetadata> subTypes = new LinkedList<ComplexTypeMetadata>();
        for (ComplexTypeMetadata subType : reference.getSubTypes()) {
            subTypes.add(subType);
            subTypes.addAll(subType.getSubTypes());
        }
        return subTypes;
    }

    @Override
    public boolean equals(Object o) {
        return reference.equals(o);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}
