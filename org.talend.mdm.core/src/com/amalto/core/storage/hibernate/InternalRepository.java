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

import com.amalto.core.metadata.*;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import java.util.Collection;

abstract class InternalRepository implements MetadataVisitor<MetadataRepository> {

    private static final Logger LOGGER = Logger.getLogger(InternalRepository.class);

    protected final HibernateStorage.TypeMappingStrategy strategy;

    protected MappingRepository mappings;

    protected MetadataRepository internalRepository;

    public InternalRepository(HibernateStorage.TypeMappingStrategy strategy) {
        this.strategy = strategy;
    }

    public MappingRepository getMappings() {
        return mappings;
    }

    public MetadataRepository visit(MetadataRepository repository) {
        mappings = new MappingRepository();
        internalRepository = new MetadataRepository();

        Collection<TypeMetadata> types = repository.getTypes();
        for (TypeMetadata type : types) {
            type.accept(this);
        }
        Collection<TypeMapping> userComplexTypes = mappings.getAllTypeMappings();
        for (TypeMapping typeMapping : userComplexTypes) {
            typeMapping.freeze();
        }
        return internalRepository;
    }

    protected MetadataVisitor<TypeMapping> getTypeMappingCreator(TypeMetadata type, HibernateStorage.TypeMappingStrategy strategy) {
        switch (strategy) {
            case AUTO:
                //throw new NotImplementedException("Not implemented support for automatic mapping selection.");
                LOGGER.info(type.getName() + " -> Automatic selection not yet implemented. Doing default");
                return new TypeMappingCreator(internalRepository, mappings);
            case FLAT:
                LOGGER.info(type.getName() + " -> FLAT");
                return new TypeMappingCreator(internalRepository, mappings);
            case GOOD_FIELD:
                LOGGER.info(type.getName() + " -> GOOD_FIELD");
                return new GoodFieldMappingCreator(internalRepository, mappings);
            default:
                throw new IllegalArgumentException("Strategy '" + this.strategy + "' is not supported.");
        }
    }

    public MetadataRepository visit(ComplexTypeMetadata complexType) {
        MetadataVisitor<TypeMapping> mappingCreator = getTypeMappingCreator(complexType, strategy);
        complexType.accept(mappingCreator);
        return internalRepository;
    }

    public MetadataRepository visit(SimpleTypeMetadata simpleType) {
        internalRepository.addTypeMetadata(simpleType.copy(internalRepository));
        return internalRepository;
    }

    public MetadataRepository visit(ContainedComplexTypeMetadata containedType) {
        return internalRepository;
    }

    public MetadataRepository visit(SimpleTypeFieldMetadata simpleField) {
        return internalRepository;
    }

    public MetadataRepository visit(EnumerationFieldMetadata enumField) {
        return internalRepository;
    }

    public MetadataRepository visit(ReferenceFieldMetadata referenceField) {
        return internalRepository;
    }

    public MetadataRepository visit(ContainedTypeFieldMetadata containedField) {
        if (containedField.isMany()) {
            // TODO Implement this
            throw new NotImplementedException("No support for many property with anonymous type.");
        }
        return internalRepository;
    }

    public MetadataRepository visit(FieldMetadata fieldMetadata) {
        return internalRepository;
    }
}
