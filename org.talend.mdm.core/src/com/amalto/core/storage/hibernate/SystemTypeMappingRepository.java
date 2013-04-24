/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import org.talend.mdm.commmon.metadata.*;

class SystemTypeMappingRepository extends InternalRepository {

    public SystemTypeMappingRepository(TypeMappingStrategy strategy) {
        super(strategy);
    }

    public MetadataRepository visit(ComplexTypeMetadata complexType) {
        MetadataVisitor<TypeMapping> creator = getTypeMappingCreator(complexType, strategy);
        TypeMapping typeMapping = complexType.accept(creator);
        // Register mapping
        internalRepository.addTypeMetadata(typeMapping.getDatabase());
        mappings.addMapping(complexType, typeMapping);
        return internalRepository;
    }
}
