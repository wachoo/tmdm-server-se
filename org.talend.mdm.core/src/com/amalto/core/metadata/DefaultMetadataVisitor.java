/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import java.util.List;

/**
 *
 */
public class DefaultMetadataVisitor<T> implements MetadataVisitor<T> {

    public T visit(MetadataRepository repository) {
        Collection<TypeMetadata> types = repository.getTypes();
        for (TypeMetadata type : types) {
            type.accept(this);
        }

        return null;
    }

    public T visit(SimpleTypeMetadata typeMetadata) {
        return null;
    }

    public T visit(ComplexTypeMetadata metadata) {
        Collection<FieldMetadata> fields = metadata.getFields();
        for (FieldMetadata field : fields) {
            field.accept(this);
        }

        return null;
    }

    public T visit(FieldMetadata metadata) {
        throw new IllegalArgumentException("Field metadata '" + metadata.getName() + "' does not have its visit method (class: " + metadata.getClass().getName() + ")");
    }

    public T visit(ReferenceFieldMetadata metadata) {
        return null;
    }

    public T visit(ReferenceCollectionFieldMetadata metadata) {
        return null;
    }

    public T visit(SimpleTypeFieldMetadata metadata) {
        return null;
    }

    public T visit(SimpleTypeCollectionFieldMetadata metadata) {
        return null;
    }

    public T visit(EnumerationFieldMetadata metadata) {
        return null;
    }

}