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

import java.util.Collections;
import java.util.Stack;

class GoodFieldMappingCreator extends DefaultMetadataVisitor<TypeMapping> {

    private static final String GENERATED_ID = "x_talend_id";

    private final MetadataRepository internalRepository;

    private final MappingRepository mappings;

    private Stack<ComplexTypeMetadata> currentType = new Stack<ComplexTypeMetadata>();

    private TypeMapping mapping;

    public GoodFieldMappingCreator(MetadataRepository repository, MappingRepository mappings) {
        internalRepository = repository;
        this.mappings = mappings;
    }

    private TypeMapping handleField(FieldMetadata field) {
        SimpleTypeFieldMetadata newFlattenField;
        ComplexTypeMetadata database = mapping.getDatabase();
        newFlattenField = new SimpleTypeFieldMetadata(database, field.isKey(), field.isMany(), field.isMandatory(), "x_" + field.getName().toLowerCase(), field.getType(), field.getWriteUsers(), field.getHideUsers());
        database.addField(newFlattenField);
        mapping.map(field, newFlattenField);
        return null;
    }

    @Override
    public TypeMapping visit(ReferenceFieldMetadata referenceField) {
        String name = referenceField.getName();
        ComplexTypeMetadata referencedType = new SoftTypeRef(internalRepository, "", referenceField.getReferencedType().getName());

        FieldMetadata referencedField = referenceField.getReferencedField();
        FieldMetadata referencedFieldCopy = referencedField.copy(internalRepository);
        FieldMetadata foreignKeyInfoFieldCopy = referenceField.hasForeignKeyInfo() ? referenceField.getForeignKeyInfoField().copy(internalRepository) : null;

        ComplexTypeMetadata database = currentType.peek();
        ReferenceFieldMetadata newFlattenField = new ReferenceFieldMetadata(database, referenceField.isKey(), referenceField.isMany(), referenceField.isMandatory(), name, referencedType, referencedFieldCopy, foreignKeyInfoFieldCopy, referenceField.isFKIntegrity(), referenceField.allowFKIntegrityOverride(), referenceField.getWriteUsers(), referenceField.getHideUsers());
        database.addField(newFlattenField);
        mapping.map(referenceField, newFlattenField);
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
                GENERATED_ID,
                new SoftTypeRef(internalRepository, "", "UUID"),
                containedType.getWriteUsers(),
                containedType.getHideUsers()));

        internalRepository.addTypeMetadata(newInternalType);
        currentType.push(newInternalType);
        {
            super.visit(containedType);
        }
        currentType.pop();
        return null;
    }

    @Override
    public TypeMapping visit(ContainedTypeFieldMetadata containedField) {
        String typeName = containedField.getContainingType().getName() + '_' + containedField.getContainedType().getName();
        SoftTypeRef typeRef = new SoftTypeRef(internalRepository,
                containedField.getContainingType().getNamespace(),
                typeName);
        ComplexTypeMetadata database = currentType.peek();
        ReferenceFieldMetadata newFlattenField = new ReferenceFieldMetadata(database,
                false,
                containedField.isMany(),
                containedField.isMandatory(),
                containedField.getName(),
                typeRef,
                new SoftIdFieldRef(internalRepository, typeName),
                null,
                true,
                false,
                containedField.getWriteUsers(),
                containedField.getHideUsers());

        database.addField(newFlattenField);
        mapping.map(containedField, newFlattenField);
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
        mapping = new GoodFieldTypeMapping(complexType, mappings);
        ComplexTypeMetadata database = mapping.getDatabase();

        currentType.push(database);
        {
            internalRepository.addTypeMetadata(database);
            if (complexType.getKeyFields().isEmpty()) {
                database.addField(new SimpleTypeFieldMetadata(database, true, false, true, GENERATED_ID, new SoftTypeRef(internalRepository, "", "UUID"), Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            super.visit(complexType);
        }
        currentType.pop();
        if (!currentType.isEmpty()) { // This is unexpected
            throw new IllegalStateException("Type remained in process stack.");
        }
        return mapping;
    }
}
