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

package com.amalto.core.storage.hibernate.enhancement;

import com.amalto.core.metadata.*;
import com.amalto.core.storage.hibernate.MappingMetadataRepository;

import java.util.Collections;

public class UserTypeMappingRepository extends TypeMappingRepository {

    public UserTypeMappingRepository() {
        super(TypeMappingStrategy.FLAT);
    }

    public MappingMetadataRepository visit(ComplexTypeMetadata complexType) {
        TypeMapping typeMapping = complexType.accept(creator);

        // Add MDM specific record specific metadata
        typeMapping.addField(new SimpleTypeFieldMetadata(typeMapping, false, false, true, METADATA_TIMESTAMP, repository.getType(XSD_NAMESPACE, "long"), Collections.<String>emptyList(), Collections.<String>emptyList()));
        typeMapping.addField(new SimpleTypeFieldMetadata(typeMapping, false, false, false, METADATA_TASK_ID, repository.getType(XSD_NAMESPACE, "string"), Collections.<String>emptyList(), Collections.<String>emptyList()));
        typeMapping.addField(new SimpleTypeFieldMetadata(typeMapping, false, false, false, METADATA_REVISION_ID, repository.getType(XSD_NAMESPACE, "long"), Collections.<String>emptyList(), Collections.<String>emptyList()));

        // Register mapping
        enhancedRepository.addMapping(complexType, typeMapping);
        enhancedRepository.addTypeMetadata(typeMapping.freeze());
        return enhancedRepository;
    }
}
