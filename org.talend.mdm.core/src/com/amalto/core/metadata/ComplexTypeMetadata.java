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

import java.util.*;

/**
 *
 */
public class ComplexTypeMetadata implements TypeMetadata {

    private final String name;

    private final String nameSpace;

    private final Map<String, FieldMetadata> fieldMetadata = new HashMap<String, FieldMetadata>();

    private final Collection<TypeMetadata> superTypes;

    private boolean hasResolvedSuperTypes = false;

    public ComplexTypeMetadata(String nameSpace, String name, Collection<TypeMetadata> superTypes) {
        this.name = name;
        this.nameSpace = nameSpace;
        this.superTypes = superTypes;
    }

    public void addSuperType(TypeMetadata superType) {
        superTypes.add(superType);
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return Collections.unmodifiableCollection(superTypes);
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return nameSpace;
    }

    public boolean isAbstract() {
        return true;
    }

    public FieldMetadata getField(String fieldName) {
        return fieldMetadata.get(fieldName);
    }

    public List<FieldMetadata> getKeyFields() {
        List<FieldMetadata> keyFields = new ArrayList<FieldMetadata>();
        for (FieldMetadata metadata : getFields()) {
            if (metadata.isKey()) {
                keyFields.add(metadata);
            }
        }
        return keyFields;
    }

    public List<FieldMetadata> getFields() {
        if (!hasResolvedSuperTypes) {
            // Resolve fields from super types lazily in case type does not exist in repository when
            // addSuperType is called.
            for (TypeMetadata superType : superTypes) {
                List<FieldMetadata> superTypeFields = superType.getFields();
                for (FieldMetadata superTypeField : superTypeFields) {
                    superTypeField.adopt(this);
                }
            }
            hasResolvedSuperTypes = true;
        }

        return Collections.unmodifiableList(new ArrayList<FieldMetadata>(fieldMetadata.values()));
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

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return '[' + nameSpace + ':' + name + ']';
    }

    public void addField(FieldMetadata fieldMetadata) {
        FieldMetadata previousFieldValue = this.fieldMetadata.get(fieldMetadata.getName());
        if (previousFieldValue != null && previousFieldValue.isKey() && !fieldMetadata.isKey()) {
            return;
        }

        this.fieldMetadata.put(fieldMetadata.getName(), fieldMetadata);
    }

}
