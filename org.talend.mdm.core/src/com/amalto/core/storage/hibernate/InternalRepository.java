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
import org.apache.log4j.Logger;

import java.util.Collection;

abstract class InternalRepository implements MetadataVisitor<MetadataRepository> {

    private static final Logger LOGGER = Logger.getLogger(InternalRepository.class);

    private MetadataRepository userRepository;

    final HibernateStorage.TypeMappingStrategy strategy;

    MappingRepository mappings;

    MetadataRepository internalRepository;

    InternalRepository(HibernateStorage.TypeMappingStrategy strategy) {
        this.strategy = strategy;
    }

    public MappingRepository getMappings() {
        return mappings;
    }

    public MetadataRepository getInternalRepository() {
        return internalRepository;
    }

    public MetadataRepository visit(MetadataRepository repository) {
        userRepository = repository;
        mappings = new MappingRepository();
        internalRepository = new MetadataRepository();
        for (TypeMetadata type : repository.getTypes()) {
            type.accept(this);
        }
        for (TypeMapping typeMapping : mappings.getAllTypeMappings()) {
            typeMapping.freeze();
        }
        return internalRepository;
    }

    MetadataVisitor<TypeMapping> getTypeMappingCreator(TypeMetadata type, HibernateStorage.TypeMappingStrategy strategy) {
        if ("Update".equals(type.getName())) { //$NON-NLS-1$
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Mapping strategy: " + type.getName() + " -> fixed update report mapping.");
            }
            return new UpdateReportMappingCreator(type, userRepository, mappings);
        }
        switch (strategy) {
            case AUTO:
                HibernateStorage.TypeMappingStrategy actualStrategy = type.accept(new MappingStrategySelector(20));
                return getTypeMappingCreator(type, actualStrategy);
            case FLAT:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Mapping strategy: " + type.getName() + " -> FLAT");
                }
                return new TypeMappingCreator(internalRepository, mappings);
            case SCATTERED:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Mapping strategy: " + type.getName() + " -> SCATTERED");
                }
                return new ScatteredMappingCreator(internalRepository, mappings);
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
        return internalRepository;
    }

    public MetadataRepository visit(FieldMetadata fieldMetadata) {
        return internalRepository;
    }

    private static class MappingStrategySelector extends DefaultMetadataVisitor<HibernateStorage.TypeMappingStrategy> {

        private final int fieldThreshold;

        private int fieldCount = 0;

        private MappingStrategySelector(int fieldThreshold) {
            this.fieldThreshold = fieldThreshold;
        }

        @Override
        public HibernateStorage.TypeMappingStrategy visit(ComplexTypeMetadata complexType) {
            if (!complexType.getSubTypes().isEmpty()) {
                return HibernateStorage.TypeMappingStrategy.SCATTERED;
            }
            if (!complexType.isInstantiable()) {
                return HibernateStorage.TypeMappingStrategy.SCATTERED;
            }
            Collection<FieldMetadata> fields = complexType.getFields();
            for (FieldMetadata field : fields) {
                HibernateStorage.TypeMappingStrategy result = field.accept(this);
                if (fieldCount > fieldThreshold || result == HibernateStorage.TypeMappingStrategy.SCATTERED) {
                    return HibernateStorage.TypeMappingStrategy.SCATTERED;
                }
            }
            return HibernateStorage.TypeMappingStrategy.FLAT;
        }

        @Override
        public HibernateStorage.TypeMappingStrategy visit(ContainedComplexTypeMetadata containedType) {
            fieldCount += containedType.getFields().size();
            Collection<FieldMetadata> fields = containedType.getFields();
            for (FieldMetadata field : fields) {
                HibernateStorage.TypeMappingStrategy result = field.accept(this);
                if (result == HibernateStorage.TypeMappingStrategy.SCATTERED) {
                    return result;
                }
            }
            return HibernateStorage.TypeMappingStrategy.FLAT;
        }

        @Override
        public HibernateStorage.TypeMappingStrategy visit(ContainedTypeFieldMetadata containedField) {
            if (containedField.isMany() || !containedField.getContainedType().getSubTypes().isEmpty()) {
                return HibernateStorage.TypeMappingStrategy.SCATTERED;
            }
            return super.visit(containedField);
        }

        @Override
        public HibernateStorage.TypeMappingStrategy visit(SimpleTypeFieldMetadata simpleField) {
            fieldCount++;
            return HibernateStorage.TypeMappingStrategy.AUTO;
        }
    }
}
