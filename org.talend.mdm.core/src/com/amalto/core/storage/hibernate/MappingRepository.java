/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import org.apache.commons.collections.map.MultiKeyMap;
import org.talend.mdm.commmon.metadata.TypeMetadata;

import java.util.Collection;

public class MappingRepository {

    private final MultiKeyMap userToMapping = new MultiKeyMap();

    private final MultiKeyMap databaseToMapping = new MultiKeyMap();

    public TypeMapping getMappingFromUser(TypeMetadata type) {
        if (type instanceof TypeMapping) {
            return (TypeMapping) type;
        }
        return (TypeMapping) userToMapping.get(type.getName(), type.isInstantiable());
    }

    public TypeMapping getMappingFromDatabase(TypeMetadata type) {
        if (type instanceof TypeMapping) {
            return (TypeMapping) type;
        }
        return (TypeMapping) databaseToMapping.get(type.getName(), type.isInstantiable());
    }

    public void addMapping(TypeMetadata type, TypeMapping mapping) {
        userToMapping.put(type.getName(), type.isInstantiable(), mapping);
        databaseToMapping.put(mapping.getDatabase().getName(), type.isInstantiable(), mapping);
    }

    public Collection<TypeMapping> getAllTypeMappings() {
        return userToMapping.values();
    }
}
