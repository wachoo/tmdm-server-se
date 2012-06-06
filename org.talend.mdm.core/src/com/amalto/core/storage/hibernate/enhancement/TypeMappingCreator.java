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
import java.util.LinkedList;
import java.util.List;

class TypeMappingCreator extends DefaultMetadataVisitor<TypeMapping> {

    protected TypeMapping flattenType;

    private final LinkedList<String> prefix = new LinkedList<String>();

    private final MappingMetadataRepository enhancedRepository;

    private boolean forceKey = false;

    public TypeMappingCreator(MappingMetadataRepository enhancedRepository) {
        this.enhancedRepository = enhancedRepository;
    }

    protected String getColumnName(FieldMetadata field, boolean addPrefix) {
        StringBuilder buffer = new StringBuilder();
        if (addPrefix) {
            for (String currentPrefix : prefix) {
                buffer.append(currentPrefix).append('_');
            }
        }
        String name = field.getName();
        // Note #1: Hibernate (starting from 4.0) internally sets a lower case letter as first letter if field starts with a
        // upper case character. To prevent any error due to missing field, lower case the field name.
        // Note #2: Prefix everything with "x_" so there won't be any conflict with database internal type names.
        // Note #3: Having '-' character is bad for Java code generation, so replace it with '_'.
        return "x_" + (buffer.toString().replace('-', '_') + name).toLowerCase();
    }

    @Override
    public TypeMapping visit(ReferenceFieldMetadata referenceField) {
        String name = getColumnName(referenceField, true);
        ComplexTypeMetadata referencedType = (ComplexTypeMetadata) referenceField.getReferencedType().copy(enhancedRepository);

        FieldMetadata referencedField = referenceField.getReferencedField();
        FieldMetadata referencedFieldCopy = referencedField.copy(enhancedRepository);
        FieldMetadata foreignKeyInfoFieldCopy = referenceField.hasForeignKeyInfo() ? referenceField.getForeignKeyInfoField().copy(enhancedRepository) : null;

        ReferenceFieldMetadata newFlattenField = new ReferenceFieldMetadata(flattenType, referenceField.isKey(), referenceField.isMany(), referenceField.isMandatory(), name, referencedType, referencedFieldCopy, foreignKeyInfoFieldCopy, referenceField.isFKIntegrity(), referenceField.allowFKIntegrityOverride(), referenceField.getWriteUsers(), referenceField.getHideUsers());
        flattenType.map(referenceField, newFlattenField);
        flattenType.addField(newFlattenField);
        return flattenType;
    }

    @Override
    public TypeMapping visit(ContainedComplexTypeMetadata containedType) {
        List<FieldMetadata> fields = containedType.getFields();
        for (FieldMetadata field : fields) {
            field.accept(this);
        }
        return flattenType;
    }

    @Override
    public TypeMapping visit(ContainedTypeFieldMetadata containedField) {
        prefix.add(containedField.getName());
        {
            containedField.getContainedType().accept(this);
        }
        prefix.removeLast();
        return flattenType;
    }

    @Override
    public TypeMapping visit(SimpleTypeFieldMetadata simpleField) {
        boolean isKey = simpleField.isKey() || forceKey;
        SimpleTypeFieldMetadata newFlattenField = new SimpleTypeFieldMetadata(flattenType, isKey, simpleField.isMany(), simpleField.isMandatory(), getColumnName(simpleField, true), simpleField.getType(), simpleField.getWriteUsers(), simpleField.getHideUsers());
        flattenType.map(simpleField, newFlattenField);
        flattenType.addField(newFlattenField);
        return flattenType;
    }

    @Override
    public TypeMapping visit(EnumerationFieldMetadata enumField) {
        // Seems pretty strange to use an enum field as key but nothing prevents user to do this.
        boolean isKey = enumField.isKey() || forceKey;
        EnumerationFieldMetadata newFlattenField = new EnumerationFieldMetadata(flattenType, isKey, enumField.isMany(), enumField.isMandatory(), getColumnName(enumField, true), enumField.getType(), enumField.getWriteUsers(), enumField.getHideUsers());
        flattenType.map(enumField, newFlattenField);
        flattenType.addField(newFlattenField);
        return flattenType;
    }

    @Override
    public TypeMapping visit(ComplexTypeMetadata complexType) {
        flattenType = new TypeMapping(complexType, enhancedRepository);
        List<FieldMetadata> fields = complexType.getFields();
        for (FieldMetadata field : fields) {
            field.accept(this);
        }
        List<FieldMetadata> keyFields = complexType.getKeyFields();
        forceKey = true;
        for (FieldMetadata keyField : keyFields) {
            keyField.accept(this);
        }
        forceKey = false;

        if (flattenType.getKeyFields().isEmpty()) {
            flattenType.addField(new SimpleTypeFieldMetadata(flattenType, true, false, true, "X_TALEND_ID", new SoftTypeRef(enhancedRepository, "", "string"), Collections.<String>emptyList(), Collections.<String>emptyList())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        return flattenType;
    }
}
