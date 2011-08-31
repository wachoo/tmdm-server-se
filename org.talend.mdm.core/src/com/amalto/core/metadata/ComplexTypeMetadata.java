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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ComplexTypeMetadata implements com.amalto.core.metadata.TypeMetadata {

    private final String name;

    private final String nameSpace;

    private final Map<String, FieldMetadata> fieldMetadata = new HashMap<String, FieldMetadata>();

    private final Collection<TypeMetadata> superTypes;

    public ComplexTypeMetadata(String nameSpace, String name, Collection<TypeMetadata> superTypes) {
        this.name = name;
        this.nameSpace = nameSpace;
        this.superTypes = superTypes;
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return superTypes;
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

    public List<FieldMetadata> getKeyFields() {
        List<FieldMetadata> keyFields = new ArrayList<FieldMetadata>();
        for (FieldMetadata metadata : getFields()) {
            if (metadata.isKey()) {
                keyFields.add(metadata);
            }
        }
        return keyFields;
    }

    public Collection<FieldMetadata> getFields() {
        Collection<FieldMetadata> declaredFields = fieldMetadata.values();
        for (TypeMetadata type : superTypes) {
            declaredFields.addAll(type.getFields());
        }
        return declaredFields;
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
