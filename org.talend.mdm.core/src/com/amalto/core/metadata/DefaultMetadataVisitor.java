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

package com.amalto.core.metadata;

import java.util.Collection;

/**
 * Default visitor for data model classes in package com.amalto.core.metadata.
 */
public class DefaultMetadataVisitor<T> implements MetadataVisitor<T> {

    /**
     * Visit all types located in <code>repository</code>.
     * @param repository A {@link MetadataRepository}.
     * @return Result typed as T.
     */
    public T visit(MetadataRepository repository) {
        Collection<TypeMetadata> types = repository.getTypes();
        for (TypeMetadata type : types) {
            type.accept(this);
        }

        return null;
    }

    /**
     * @param simpleType A simple typed field.
     * @return Result typed as T.
     */
    public T visit(SimpleTypeMetadata simpleType) {
        return null;
    }

    /**
     * Visit all fields declared in <code>complexType</code>.
     * @param complexType A complex type.
     * @return Result typed as T.
     */
    public T visit(ComplexTypeMetadata complexType) {
        Collection<FieldMetadata> fields = complexType.getFields();
        for (FieldMetadata field : fields) {
            field.accept(this);
        }

        return null;
    }

    /**
     * Visit all field declared in contained type.
     * @param containedType A contained type.
     * @return Result typed as T.
     */
    public T visit(ContainedComplexTypeMetadata containedType) {
        Collection<FieldMetadata> fields = containedType.getFields();
        for (FieldMetadata field : fields) {
            field.accept(this);
        }

        return null;
    }

    public T visit(ReferenceFieldMetadata referenceField) {
        return null;
    }

    public T visit(ContainedTypeFieldMetadata containedField) {
        return containedField.getContainedType().accept(this);
    }

    public T visit(FieldMetadata fieldMetadata) {
        return null;
    }

    public T visit(SimpleTypeFieldMetadata simpleField) {
        return null;
    }

    public T visit(EnumerationFieldMetadata enumField) {
        return null;
    }

}