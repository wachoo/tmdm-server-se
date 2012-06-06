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

import javax.xml.XMLConstants;
import java.util.Collections;
import java.util.Stack;

class GoodFieldMappingCreator extends DefaultMetadataVisitor<TypeMapping> {

    private final Stack<TypeMapping> internalTypes = new Stack<TypeMapping>();

    private final MappingMetadataRepository enhancedRepository;

    private TypeMapping flattenType;

    public GoodFieldMappingCreator(MappingMetadataRepository enhancedRepository) {
        this.enhancedRepository = enhancedRepository;
    }

    private TypeMapping handleField(FieldMetadata field) {
        SimpleTypeFieldMetadata newFlattenField;
        newFlattenField = new SimpleTypeFieldMetadata(internalTypes.peek(), field.isKey(), field.isMany(), field.isMandatory(), field.getName(), field.getType(), field.getWriteUsers(), field.getHideUsers());
        internalTypes.peek().addField(newFlattenField);
        internalTypes.peek().map(newFlattenField, newFlattenField);
        flattenType.map(field, newFlattenField);
        return null;
    }

    @Override
    public TypeMapping visit(ReferenceFieldMetadata referenceField) {
        String name = referenceField.getName();
        ComplexTypeMetadata referencedType = (ComplexTypeMetadata) referenceField.getReferencedType().copy(enhancedRepository);

        FieldMetadata referencedField = referenceField.getReferencedField();
        FieldMetadata referencedFieldCopy = referencedField.copy(enhancedRepository);
        FieldMetadata foreignKeyInfoFieldCopy = referenceField.hasForeignKeyInfo() ? referenceField.getForeignKeyInfoField().copy(enhancedRepository) : null;

        ReferenceFieldMetadata newFlattenField = new ReferenceFieldMetadata(internalTypes.peek(), referenceField.isKey(), referenceField.isMany(), referenceField.isMandatory(), name, referencedType, referencedFieldCopy, foreignKeyInfoFieldCopy, referenceField.isFKIntegrity(), referenceField.allowFKIntegrityOverride(), referenceField.getWriteUsers(), referenceField.getHideUsers());
        internalTypes.peek().addField(newFlattenField);
        internalTypes.peek().map(newFlattenField, newFlattenField);
        flattenType.map(referenceField, newFlattenField);
        return null;
    }

    @Override
    public TypeMapping visit(ContainedComplexTypeMetadata containedType) {
        String newTypeName = containedType.getContainerType().getName() + '_' + containedType.getName();
        ComplexTypeMetadata newInternalType = new ComplexTypeMetadataImpl(containedType.getNamespace(),
                newTypeName,
                containedType.getWriteUsers(),
                containedType.getDenyCreate(),
                containedType.getHideUsers(),
                containedType.getDenyDelete(ComplexTypeMetadata.DeleteType.PHYSICAL),
                containedType.getDenyDelete(ComplexTypeMetadata.DeleteType.LOGICAL),
                containedType.getSchematron());
        newInternalType.addField(new SimpleTypeFieldMetadata(newInternalType,
                true,
                false,
                true,
                "x_talend_id",
                new SoftTypeRef(enhancedRepository, "", "UUID"),
                containedType.getWriteUsers(),
                containedType.getHideUsers()));

        // TODO There's no need to add MDM specific record specific metadata: it's there for POJO class generation.
        newInternalType.addField(new SimpleTypeFieldMetadata(newInternalType, false, false, true, TypeMappingRepository.METADATA_TIMESTAMP, enhancedRepository.getType(TypeMappingRepository.XSD_NAMESPACE, "long"), Collections.<String>emptyList(), Collections.<String>emptyList()));
        newInternalType.addField(new SimpleTypeFieldMetadata(newInternalType, false, false, false, TypeMappingRepository.METADATA_TASK_ID, enhancedRepository.getType(TypeMappingRepository.XSD_NAMESPACE, "string"), Collections.<String>emptyList(), Collections.<String>emptyList()));
        newInternalType.addField(new SimpleTypeFieldMetadata(newInternalType, false, false, false, TypeMappingRepository.METADATA_REVISION_ID, enhancedRepository.getType(TypeMappingRepository.XSD_NAMESPACE, "long"), Collections.<String>emptyList(), Collections.<String>emptyList()));

        enhancedRepository.addTypeMetadata(newInternalType);
        TypeMapping mapping = new TypeMapping(newInternalType, newInternalType, enhancedRepository);
        enhancedRepository.addMapping(newInternalType, mapping);
        internalTypes.push(mapping);
        {
            super.visit(containedType);
        }
        internalTypes.pop();
        return null;
    }

    @Override
    public TypeMapping visit(ContainedTypeFieldMetadata containedField) {
        String typeName = containedField.getContainingType().getName() + '_' + containedField.getContainedType().getName();
        SoftTypeRef typeRef = new SoftTypeRef(enhancedRepository,
                containedField.getContainingType().getNamespace(),
                typeName);
        ReferenceFieldMetadata newFlattenField = new ReferenceFieldMetadata(internalTypes.peek(),
                false,
                containedField.isMany(),
                containedField.isMandatory(),
                containedField.getName(),
                typeRef,
                new SoftIdFieldRef(enhancedRepository, typeName),
                null,
                true,
                false,
                containedField.getWriteUsers(),
                containedField.getHideUsers());

        internalTypes.peek().addField(newFlattenField);
        internalTypes.peek().map(newFlattenField, newFlattenField);
        flattenType.map(containedField, newFlattenField);
        containedField.getContainedType().accept(this);
        return null;
    }

    @Override
    public TypeMapping visit(SimpleTypeFieldMetadata simpleField) {
        return handleField(simpleField);
    }

    @Override
    public TypeMapping visit(EnumerationFieldMetadata enumField) {
        return handleField(enumField);
    }

    @Override
    public TypeMapping visit(ComplexTypeMetadata complexType) {
        flattenType = new TypeMapping(complexType, enhancedRepository);
        internalTypes.push(flattenType);
        {
            super.visit(complexType);
        }
        internalTypes.pop();
        if (!internalTypes.isEmpty()) {
            throw new IllegalStateException("One type remained in stack.");
        }
        return flattenType;
    }
}
