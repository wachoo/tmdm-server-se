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
import org.apache.commons.lang.StringUtils;

import javax.xml.XMLConstants;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

class ScatteredMappingCreator extends DefaultMetadataVisitor<TypeMapping> {

    public static final String GENERATED_ID = "x_talend_id";  //$NON-NLS-1$

    private final MetadataRepository internalRepository;

    private final MappingRepository mappings;

    private final Stack<ComplexTypeMetadata> currentType = new Stack<ComplexTypeMetadata>();

    private final Set<TypeMetadata> processedTypes = new HashSet<TypeMetadata>();

    private TypeMapping mapping;

    public ScatteredMappingCreator(MetadataRepository repository, MappingRepository mappings) {
        internalRepository = repository;
        this.mappings = mappings;
    }

    private TypeMapping handleField(FieldMetadata field) {
        SimpleTypeFieldMetadata newFlattenField;
        String name = getFieldName(field);
        newFlattenField = new SimpleTypeFieldMetadata(currentType.peek(),
                false,
                field.isMany(),
                field.isMandatory(),
                name,
                field.getType(),
                field.getWriteUsers(),
                field.getHideUsers());
        TypeMetadata declaringType = field.getDeclaringType();
        if (declaringType != field.getContainingType() && declaringType.isInstantiable()) {
            SoftTypeRef type = new SoftTypeRef(internalRepository,
                    declaringType.getNamespace(),
                    declaringType.getName(),
                    true);
            newFlattenField.setDeclaringType(type);
        }
        currentType.peek().addField(newFlattenField);
        mapping.map(field, newFlattenField);
        return null;
    }

    private String getFieldName(FieldMetadata field) {
        return "x_" + field.getName().replace('-', '_').toLowerCase(); //$NON-NLS-1$
    }

    @Override
    public TypeMapping visit(ReferenceFieldMetadata referenceField) {
        String name = getFieldName(referenceField);
        ComplexTypeMetadata fieldReferencedType = referenceField.getReferencedType();
        ComplexTypeMetadata referencedType;
        if (fieldReferencedType.isInstantiable()) {
            String typeName = fieldReferencedType.getName().replace('-', '_');
            referencedType = new SoftTypeRef(internalRepository,
                    fieldReferencedType.getNamespace(),
                    typeName,
                    true);
        } else {
            referencedType = new SoftTypeRef(internalRepository,
                    fieldReferencedType.getNamespace(),
                    newNonInstantiableTypeName(fieldReferencedType),
                    true);
        }

        String referencedTypeName = referencedType.getName().replace('-', '_');
        FieldMetadata referencedFieldCopy = new SoftIdFieldRef(internalRepository, referencedTypeName);

        ComplexTypeMetadata database = currentType.peek();

        boolean fkIntegrity = referenceField.isFKIntegrity() && (fieldReferencedType != mapping.getUser()); // Don't enforce FK integrity for references to itself.
        ReferenceFieldMetadata newFlattenField = new ReferenceFieldMetadata(currentType.peek(),
                referenceField.isKey(),
                referenceField.isMany(),
                referenceField.isMandatory(),
                name,
                referencedType,
                referencedFieldCopy,
                null,
                fkIntegrity,
                referenceField.allowFKIntegrityOverride(),
                new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string"),
                referenceField.getWriteUsers(),
                referenceField.getHideUsers());
        database.addField(newFlattenField);
        mapping.map(referenceField, newFlattenField);
        return null;
    }

    private String newNonInstantiableTypeName(ComplexTypeMetadata fieldReferencedType) {
        return getNonInstantiableTypeName(fieldReferencedType.getName());
    }

    private String getNonInstantiableTypeName(String typeName) {
        if (!typeName.startsWith("X_")) { //$NON-NLS-1$
            return "X_" + typeName.replace('-', '_'); //$NON-NLS-1$
        } else {
            return typeName;
        }
    }

    @Override
    public TypeMapping visit(ContainedComplexTypeMetadata containedType) {
        String typeName = containedType.getName().replace('-', '_');
        String databaseSuperType = createContainedType(getNonInstantiableTypeName(typeName), null, containedType);
        for (ComplexTypeMetadata subType : containedType.getSubTypes()) {
            String subTypeName = subType.getName().replace('-', '_');
            createContainedType(getNonInstantiableTypeName(subTypeName), databaseSuperType, subType);
        }
        return null;
    }

    private String createContainedType(String typeName, String superTypeName, ComplexTypeMetadata originalContainedType) {
        ComplexTypeMetadata internalContainedType = (ComplexTypeMetadata) internalRepository.getType(typeName);
        if (internalContainedType == null) {
            internalContainedType = new ComplexTypeMetadataImpl(originalContainedType.getNamespace(),
                    typeName,
                    originalContainedType.getWriteUsers(),
                    originalContainedType.getDenyCreate(),
                    originalContainedType.getHideUsers(),
                    originalContainedType.getDenyDelete(ComplexTypeMetadata.DeleteType.PHYSICAL),
                    originalContainedType.getDenyDelete(ComplexTypeMetadata.DeleteType.LOGICAL),
                    originalContainedType.getSchematron(),
                    false);
            internalRepository.addTypeMetadata(internalContainedType);
            if (superTypeName == null) {  // Generate a technical ID only if contained type does not have super type (subclasses will inherit it).
                internalContainedType.addField(new SimpleTypeFieldMetadata(internalContainedType,
                        true,
                        false,
                        true,
                        GENERATED_ID,
                        new SoftTypeRef(internalRepository, internalRepository.getUserNamespace(), "UUID", false), //$NON-NLS-1$
                        originalContainedType.getWriteUsers(),
                        originalContainedType.getHideUsers()));
            } else {
                internalContainedType.addSuperType(new SoftTypeRef(internalRepository, internalContainedType.getNamespace(), superTypeName, false), internalRepository);
            }
            internalRepository.addTypeMetadata(internalContainedType);
        }
        currentType.push(internalContainedType);
        {
            super.visit(originalContainedType);
        }
        currentType.pop();
        return typeName;
    }

    @Override
    public TypeMapping visit(ContainedTypeFieldMetadata containedField) {
        if (processedTypes.contains(containedField.getContainedType())) {
            return null;
        } else {
            processedTypes.add(containedField.getContainedType());
        }
        String fieldName = getFieldName(containedField);
        String containedTypeName = newNonInstantiableTypeName(containedField.getContainedType());
        SoftTypeRef typeRef = new SoftTypeRef(internalRepository,
                containedField.getDeclaringType().getNamespace(),
                containedTypeName,
                false);
        ReferenceFieldMetadata newFlattenField = new ReferenceFieldMetadata(currentType.peek(),
                false,
                containedField.isMany(),
                containedField.isMandatory(),
                fieldName,
                typeRef,
                new SoftIdFieldRef(internalRepository, containedTypeName),
                null,
                false,  // No need to enforce FK in references to these technical objects.
                false,
                new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string"),
                containedField.getWriteUsers(),
                containedField.getHideUsers());
        newFlattenField.setData("SQL_DELETE_CASCADE", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        currentType.peek().addField(newFlattenField);
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
        mapping = new ScatteredTypeMapping(complexType, mappings);
        ComplexTypeMetadata database = mapping.getDatabase();
        if (!complexType.isInstantiable()) {
            // In this mapping prefix non instantiable types with "x_" so table name is not mixed up with an entity
            // table with same name.
            database.setName(newNonInstantiableTypeName(database)); //$NON-NLS-1$
        }
        currentType.push(database);
        {
            internalRepository.addTypeMetadata(database);
            if (complexType.getKeyFields().isEmpty() && complexType.getSuperTypes().isEmpty()) { // Assumes super type will define an id.
                SoftTypeRef type = new SoftTypeRef(internalRepository, StringUtils.EMPTY, "UUID", false); //$NON-NLS-1$
                database.addField(new SimpleTypeFieldMetadata(database,
                        true,
                        false,
                        true,
                        GENERATED_ID,
                        type,
                        Collections.<String>emptyList(),
                        Collections.<String>emptyList()));
            }
            for (TypeMetadata superType : complexType.getSuperTypes()) {
                if (superType.isInstantiable()) {
                    SoftTypeRef type = new SoftTypeRef(internalRepository,
                            superType.getNamespace(),
                            superType.getName(),
                            superType.isInstantiable());
                    database.addSuperType(type, internalRepository);
                } else {
                    SoftTypeRef type = new SoftTypeRef(internalRepository,
                            superType.getNamespace(),
                            getNonInstantiableTypeName(superType.getName()),
                            superType.isInstantiable());
                    database.addSuperType(type, internalRepository);
                }
            }
            super.visit(complexType);
            for (FieldMetadata keyField : complexType.getKeyFields()) {
                database.registerKey(database.getField("x_" + keyField.getName().replace('-', '_').toLowerCase()));
            }
        }
        currentType.pop();
        if (!currentType.isEmpty()) { // This is unexpected
            throw new IllegalStateException("Type remained in process stack.");
        }
        return mapping;
    }
}
