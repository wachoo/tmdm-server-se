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

import com.amalto.core.metadata.TypeMetadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class MappingRepository {

    private final Map<String, TypeMapping> userToMapping = new HashMap<String, TypeMapping>();

    public TypeMapping getMapping(TypeMetadata type) {
        if (type instanceof TypeMapping) {
            return (TypeMapping) type;
        }
        return userToMapping.get(type.getName());
    }

    public void addMapping(TypeMetadata type, TypeMapping mapping) {
        userToMapping.put(type.getName(), mapping);
    }

    public Collection<TypeMapping> getAllTypeMappings() {
        return userToMapping.values();
    }
}
