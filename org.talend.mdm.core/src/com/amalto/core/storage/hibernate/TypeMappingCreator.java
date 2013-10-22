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
import org.apache.log4j.Logger;

import javax.xml.XMLConstants;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

class TypeMappingCreator extends DefaultMetadataVisitor<TypeMapping> {

    private static final Logger LOGGER = Logger.getLogger(TypeMappingCreator.class);

    private final LinkedList<String> prefix = new LinkedList<String>();

    private final MetadataRepository internalRepository;

    private final MappingRepository mappings;

    private TypeMapping typeMapping;

    private boolean forceKey = false;

    public TypeMappingCreator(MetadataRepository repository, MappingRepository mappings) {
        this.mappings = mappings;
        this.internalRepository = repository;
    }

    String getColumnName(FieldMetadata field) {
        StringBuilder buffer = new StringBuilder();
        for (String currentPrefix : prefix) {
            buffer.append(currentPrefix).append('_');
        }
        String name = field.getName();
        // Note #1: Hibernate (starting from 4.0) internally sets a lower case letter as first letter if field starts with a
        // upper case character. To prevent any error due to missing field, lower case the field name.
        // Note #2: Prefix everything with "x_" so there won't be any conflict with database internal type names.
        // Note #3: Having '-' character is bad for Java code generation, so replace it with '_'.
        return "x_" + (buffer.toString().replace('-', '_') + name.replace('-', '_')).toLowerCase(); //$NON-NLS-1$
    }

    private static boolean isDatabaseMandatory(FieldMetadata field, TypeMetadata declaringType) {
        boolean isDatabaseMandatory = field.isMandatory() && declaringType.isInstantiable();
        if (field.isMandatory() && !isDatabaseMandatory) {
            LOGGER.warn("Field '" + field.getName() + "' is mandatory but constraint cannot be enforced in database schema.");
        }
        return isDatabaseMandatory;
    }

    @Override
    public TypeMapping visit(ReferenceFieldMetadata referenceField) {
        String name = getColumnName(referenceField);
        String typeName = referenceField.getReferencedType().getName().replace('-', '_');
        ComplexTypeMetadata referencedType = new SoftTypeRef(internalRepository, referenceField.getReferencedType().getNamespace(), typeName, true);
        FieldMetadata referencedField = new SoftIdFieldRef(internalRepository, typeName);

        FieldMetadata newFlattenField;
        if (referenceField.getContainingType() == referenceField.getDeclaringType()) {
            ComplexTypeMetadata database = typeMapping.getDatabase();
            newFlattenField = new ReferenceFieldMetadata(database,
                    referenceField.isKey(),
                    referenceField.isMany(),
                    isDatabaseMandatory(referenceField, referenceField.getDeclaringType()),
                    name,
                    referencedType,
                    referencedField,
                    Collections.<FieldMetadata>emptyList(),
                    referenceField.isFKIntegrity(),
                    referenceField.allowFKIntegrityOverride(),
                    new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.STRING),
                    referenceField.getWriteUsers(),
                    referenceField.getHideUsers(),
                    referenceField.getWorkflowAccessRights(),
                    name);
            database.addField(newFlattenField);
        } else {
            newFlattenField = new SoftFieldRef(internalRepository, getColumnName(referenceField), referenceField.getContainingType());
        }
        typeMapping.map(referenceField, newFlattenField);
        return typeMapping;
    }

    @Override
    public TypeMapping visit(ContainedComplexTypeMetadata containedType) {
        mappings.addMapping(containedType, typeMapping);
        Collection<FieldMetadata> fields = containedType.getFields();
        for (FieldMetadata field : fields) {
            field.accept(this);
        }
        return typeMapping;
    }

    @Override
    public TypeMapping visit(ContainedTypeFieldMetadata containedField) {
        prefix.add(containedField.getName());
        {
            ContainedComplexTypeMetadata containedType = containedField.getContainedType();
            containedType.accept(this);
            for (ComplexTypeMetadata subType : containedType.getSubTypes()) {
                for (FieldMetadata subTypeField : subType.getFields()) {
                    subTypeField.accept(this);
                }
            }
        }
        prefix.removeLast();
        return typeMapping;
    }

    @Override
    public TypeMapping visit(SimpleTypeFieldMetadata simpleField) {
        SimpleTypeFieldMetadata newFlattenField;
        ComplexTypeMetadata database = typeMapping.getDatabase();
        TypeMetadata declaringType = simpleField.getDeclaringType();
        if (simpleField.getContainingType() == declaringType) {
            newFlattenField = new SimpleTypeFieldMetadata(database,
                    false,
                    simpleField.isMany(),
                    isDatabaseMandatory(simpleField, declaringType),
                    getColumnName(simpleField),
                    simpleField.getType(),
                    simpleField.getWriteUsers(),
                    simpleField.getHideUsers(),
                    simpleField.getWorkflowAccessRights(),
                    getColumnName(simpleField));
            database.addField(newFlattenField);
        } else {
            SoftTypeRef internalDeclaringType;
            if (!declaringType.isInstantiable()) {
                internalDeclaringType = new SoftTypeRef(internalRepository, declaringType.getNamespace(), "X_" + declaringType.getName(), declaringType.isInstantiable()); //$NON-NLS-1$
            } else {
                internalDeclaringType = new SoftTypeRef(internalRepository, declaringType.getNamespace(), declaringType.getName(), declaringType.isInstantiable());
            }
            newFlattenField = new SimpleTypeFieldMetadata(database,
                    false,
                    simpleField.isMany(),
                    isDatabaseMandatory(simpleField, declaringType),
                    getColumnName(simpleField),
                    simpleField.getType(),
                    simpleField.getWriteUsers(),
                    simpleField.getHideUsers(),
                    simpleField.getWorkflowAccessRights(),
                    getColumnName(simpleField));
            newFlattenField.setDeclaringType(internalDeclaringType);
            database.addField(newFlattenField);
        }
        typeMapping.map(simpleField, newFlattenField);
        return typeMapping;
    }

    @Override
    public TypeMapping visit(EnumerationFieldMetadata enumField) {
        // Seems pretty strange to use an enum field as key but nothing prevents user to do this.
        boolean isKey = enumField.isKey() || forceKey;
        FieldMetadata newFlattenField;
        if (enumField.getContainingType() == enumField.getDeclaringType()) {
            ComplexTypeMetadata database = typeMapping.getDatabase();
            newFlattenField = new EnumerationFieldMetadata(database,
                    isKey,
                    enumField.isMany(),
                    isDatabaseMandatory(enumField, enumField.getDeclaringType()),
                    getColumnName(enumField),
                    enumField.getType(),
                    enumField.getWriteUsers(),
                    enumField.getHideUsers(),
                    enumField.getWorkflowAccessRights(),
                    getColumnName(enumField));
            database.addField(newFlattenField);
        } else {
            newFlattenField = new SoftFieldRef(internalRepository, getColumnName(enumField), enumField.getContainingType());
        }
        typeMapping.map(enumField, newFlattenField);
        return typeMapping;
    }

    @Override
    public TypeMapping visit(ComplexTypeMetadata complexType) {
        typeMapping = new FlatTypeMapping(complexType, TypeMappingCreator.this.mappings);
        Collection<FieldMetadata> fields = complexType.getFields();
        for (FieldMetadata field : fields) {
            field.accept(this);
        }
        Collection<FieldMetadata> keyFields = complexType.getKeyFields();
        ComplexTypeMetadata database = typeMapping.getDatabase();
        Collection<TypeMetadata> superTypes = complexType.getSuperTypes();
        for (TypeMetadata superType : superTypes) {
            database.addSuperType(new SoftTypeRef(internalRepository, superType.getNamespace(), superType.getName(), true), internalRepository);
        }
        forceKey = true;
        for (FieldMetadata keyField : keyFields) {
            database.registerKey(typeMapping.getDatabase(keyField));
        }
        forceKey = false;
        if (typeMapping.getUser().getKeyFields().isEmpty() && typeMapping.getUser().getSuperTypes().isEmpty()) { // Assumes super type defines key field.
            SoftTypeRef type = new SoftTypeRef(internalRepository, XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.STRING, false);
            database.addField(new SimpleTypeFieldMetadata(database, true, false, true, "X_TALEND_ID", type, Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList(), "X_TALEND_ID")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return typeMapping;
    }
}
