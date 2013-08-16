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

import org.talend.mdm.commmon.metadata.*;
import com.amalto.core.storage.Storage;

import javax.xml.XMLConstants;
import java.util.Collections;

class StagingTypeMappingRepository extends InternalRepository {
    public StagingTypeMappingRepository(TypeMappingStrategy mappingStrategy) {
        super(mappingStrategy);
    }

    public MetadataRepository visit(ComplexTypeMetadata complexType) {
        TypeMapping typeMapping = complexType.accept(getTypeMappingCreator(complexType, strategy));

        // Add MDM specific record specific metadata
        ComplexTypeMetadata database = typeMapping.getDatabase();
        if (database.isInstantiable() && !database.isFrozen()) {
            TypeMetadata intType = new SoftTypeRef(internalRepository, XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.INT, false);
            TypeMetadata longType = new SoftTypeRef(internalRepository, XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.LONG, false);
            TypeMetadata stringType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.STRING);
            database.addField(new SimpleTypeFieldMetadata(database, false, false, true, Storage.METADATA_TIMESTAMP, longType, Collections.<String>emptyList(), Collections.<String>emptyList()));
            database.addField(new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_TASK_ID, stringType, Collections.<String>emptyList(), Collections.<String>emptyList()));
            database.addField(new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_STAGING_STATUS, intType, Collections.<String>emptyList(), Collections.<String>emptyList()));
            database.addField(new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_STAGING_SOURCE, stringType, Collections.<String>emptyList(), Collections.<String>emptyList()));
            database.addField(new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_STAGING_BLOCK_KEY, stringType, Collections.<String>emptyList(), Collections.<String>emptyList()));
            SimpleTypeFieldMetadata errorField = new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_STAGING_ERROR, stringType, Collections.<String>emptyList(), Collections.<String>emptyList());
            errorField.getType().setData(TypeMapping.SQL_TYPE, "text"); //$NON-NLS-1$
            database.addField(errorField);
        }

        // Register mapping
        internalRepository.addTypeMetadata(typeMapping.getDatabase());
        mappings.addMapping(complexType, typeMapping);
        return internalRepository;
    }
}
