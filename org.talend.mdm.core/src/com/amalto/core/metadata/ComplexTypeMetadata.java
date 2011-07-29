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

    public ComplexTypeMetadata(String nameSpace, String name) {
        this.name = name;
        this.nameSpace = nameSpace;
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
        for (FieldMetadata metadata : fieldMetadata.values()) {
            if (metadata.isKey()) {
                keyFields.add(metadata);
            }
        }
        return keyFields;
    }

    public Collection<FieldMetadata> getFields() {
        return fieldMetadata.values();
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
