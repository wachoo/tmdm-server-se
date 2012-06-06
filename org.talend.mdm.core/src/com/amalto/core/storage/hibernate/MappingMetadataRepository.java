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

package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.storage.hibernate.enhancement.TypeMapping;

import java.util.HashMap;
import java.util.Map;

public class MappingMetadataRepository extends MetadataRepository {

    private final Map<TypeMetadata, TypeMapping> typeToMapping = new HashMap<TypeMetadata, TypeMapping>();

    public MappingMetadataRepository() {
    }

    public TypeMapping getMapping(TypeMetadata type) {
        if (type instanceof TypeMapping) {
            return (TypeMapping) type;
        }
        TypeMapping typeMapping = typeToMapping.get(type);
        if (typeMapping == null) {
            for (Map.Entry<TypeMetadata, TypeMapping> entry : typeToMapping.entrySet()) {
                if (entry.getKey().getName().equals(type.getName())) {
                    return entry.getValue();
                }
            }
        }
        return typeMapping;
    }

    public void addMapping(TypeMetadata type, TypeMapping mapping) {
        typeToMapping.put(type, mapping);
    }

}
