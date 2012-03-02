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

import java.util.*;

/**
 *
 */
public class ComplexTypeMetadataImpl implements ComplexTypeMetadata {

    private final String name;

    private final String nameSpace;

    private final Map<String, FieldMetadata> fieldMetadata = new LinkedHashMap<String, FieldMetadata>();

    private final Map<String, FieldMetadata> keyFields = new LinkedHashMap<String, FieldMetadata>();

    private final Collection<TypeMetadata> superTypes = new LinkedList<TypeMetadata>();

    private MetadataRepository repository;

    public ComplexTypeMetadataImpl(String nameSpace, String name) {
        this.name = name;
        this.nameSpace = nameSpace;
    }

    public void addSuperType(TypeMetadata superType, MetadataRepository repository) {
        this.repository = repository;
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
        StringTokenizer tokenizer = new StringTokenizer(fieldName, "/");
        String firstFieldName = tokenizer.nextToken();
        FieldMetadata currentField = fieldMetadata.get(firstFieldName);
        if (currentField == null) { // Look in super types if it wasn't found in current type.
            for (TypeMetadata superType : superTypes) {
                currentField = superType.getField(firstFieldName);
                if (currentField != null) {
                    break;
                }
            }
        }
        if (currentField == null) {
            throw new RuntimeException("Type '" + getName() + "' does not own field '" + firstFieldName + "'");
        }
        if (tokenizer.hasMoreTokens()) {
            TypeMetadata currentType = currentField.getType();
            while (tokenizer.hasMoreTokens()) {
                String currentFieldName = tokenizer.nextToken();
                currentField = currentType.getField(currentFieldName);
                currentType = currentField.getType();
            }
        }
        return currentField;
    }

    public List<FieldMetadata> getKeyFields() {
        return new ArrayList<FieldMetadata>(keyFields.values());
    }

    public List<FieldMetadata> getFields() {
        for (TypeMetadata superType : superTypes) {
            List<FieldMetadata> superTypeFields = superType.getFields();
            for (FieldMetadata superTypeField : superTypeFields) {
                superTypeField.adopt(this, repository);
            }
        }

        return new ArrayList<FieldMetadata>(fieldMetadata.values());
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
        this.fieldMetadata.put(fieldMetadata.getName(), fieldMetadata);
        if (fieldMetadata.isKey()) {
            registerKey(fieldMetadata);
        }
    }

    public void registerKey(FieldMetadata keyField) {
        keyFields.put(keyField.getName(), keyField);
    }

    public ComplexTypeMetadata copy(MetadataRepository repository) {
        ComplexTypeMetadata registeredCopy = repository.getComplexType(getName());
        if (registeredCopy != null) {
            return registeredCopy;
        }

        ComplexTypeMetadataImpl copy = new ComplexTypeMetadataImpl(getNamespace(), getName());
        repository.addTypeMetadata(copy);

        List<FieldMetadata> fields = getFields();
        for (FieldMetadata field : fields) {
            copy.addField(field.copy(repository));
        }
        for (TypeMetadata superType : superTypes) {
            copy.addSuperType(superType.copy(repository), repository);
        }

        List<FieldMetadata> typeKeyFields = getKeyFields();
        for (FieldMetadata typeKeyField : typeKeyFields) {
            copy.registerKey(typeKeyField.copy(repository));
        }

        return copy;
    }

    public TypeMetadata copyShallow() {
        return new ComplexTypeMetadataImpl(getNamespace(), getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexTypeMetadata)) return false;
        ComplexTypeMetadata that = (ComplexTypeMetadata) o;
        return that.getName().equals(name) && that.getNamespace().equals(nameSpace);
    }
}
