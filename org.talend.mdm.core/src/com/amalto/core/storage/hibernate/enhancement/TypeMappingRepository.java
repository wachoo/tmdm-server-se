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
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import javax.xml.XMLConstants;
import java.util.Collection;

public abstract class TypeMappingRepository extends MetadataRepository implements MetadataVisitor<MappingMetadataRepository> {

    public static enum TypeMappingStrategy {
        FLAT,
        GOOD_FIELD
    }

    public static final String METADATA_TIMESTAMP = "x_talend_timestamp"; //$NON-NLS-1$

    public static final String METADATA_TASK_ID = "x_talend_task_id"; //$NON-NLS-1$

    public static final String METADATA_REVISION_ID = "x_talend_revision_id"; //$NON-NLS-1$

    public static final String METADATA_STAGING_STATUS = "x_talend_staging_status"; //$NON-NLS-1$

    public static final String METADATA_STAGING_ERROR = "x_talend_staging_error"; //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(TypeMappingRepository.class);

    protected MappingMetadataRepository enhancedRepository;

    protected MetadataVisitor<TypeMapping> creator;

    protected MetadataRepository repository;

    private final TypeMappingStrategy strategy;

    public TypeMappingRepository(TypeMappingStrategy strategy) {
        this.strategy = strategy;
    }

    public MappingMetadataRepository visit(MetadataRepository repository) {
        this.repository = repository;
        this.enhancedRepository = new MappingMetadataRepository();
        Collection<TypeMetadata> simpleTypes = repository.getTypes(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        for (TypeMetadata simpleType : simpleTypes) {
            enhancedRepository.addTypeMetadata(simpleType);
        }
        creator = getTypeMappingCreator();
        Collection<ComplexTypeMetadata> types = repository.getUserComplexTypes();
        for (ComplexTypeMetadata type : types) {
            type.accept(this);
        }
        return enhancedRepository;
    }

    protected MetadataVisitor<TypeMapping> getTypeMappingCreator() {
        switch (strategy) {
            case FLAT:
                LOGGER.info("Using FLAT strategy");
                return new TypeMappingCreator(enhancedRepository);
            case GOOD_FIELD:
                LOGGER.info("Using GOOD_FIELD strategy");
                return new GoodFieldMappingCreator(enhancedRepository);
            default:
                throw new IllegalArgumentException("Strategy '" + strategy + "' is not supported.");
        }
    }

    public MappingMetadataRepository visit(SimpleTypeMetadata simpleType) {
        enhancedRepository.addTypeMetadata(simpleType);
        return enhancedRepository;
    }

    public MappingMetadataRepository visit(ContainedComplexTypeMetadata containedType) {
        return enhancedRepository;
    }

    public MappingMetadataRepository visit(SimpleTypeFieldMetadata simpleField) {
        return enhancedRepository;
    }

    public MappingMetadataRepository visit(EnumerationFieldMetadata enumField) {
        return enhancedRepository;
    }

    public MappingMetadataRepository visit(ReferenceFieldMetadata referenceField) {
        return enhancedRepository;
    }

    public MappingMetadataRepository visit(ContainedTypeFieldMetadata containedField) {
        if (containedField.isMany()) {
            // TODO Implement this
            throw new NotImplementedException("No support for many property with anonymous type.");
        }
        return enhancedRepository;
    }

    public MappingMetadataRepository visit(FieldMetadata fieldMetadata) {
        return enhancedRepository;
    }
}
