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

package com.amalto.core.integrity;

import com.amalto.core.metadata.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Returns the field that references the concept <code>typeName</code>. References are returned as a {@link Set} of
 * {@link com.amalto.core.metadata.ReferenceFieldMetadata}.
 */
class ForeignKeyIntegrity extends DefaultMetadataVisitor<Set<ReferenceFieldMetadata>> {

    // Internal: for optimization purpose prevents checking a type more than once.
    private final Set<TypeMetadata> checkedTypes = new HashSet<TypeMetadata>();

    // Foreign key fields list to be returned at end of visit.
    private final Set<ReferenceFieldMetadata> fieldToCheck = new HashSet<ReferenceFieldMetadata>();

    private final TypeMetadata type;

    /**
     * This {@link MetadataVisitor} returns foreign key fields that points to <code>type</code>.
     * @param type A type.
     */
    public ForeignKeyIntegrity(TypeMetadata type) {
        this.type = type;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(ComplexTypeMetadata metadata) {
        if (!checkedTypes.contains(metadata)) {
            checkedTypes.add(metadata);
            return super.visit(metadata);
        }
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(ReferenceFieldMetadata metadata) {
        if (type.isAssignableFrom(metadata.getReferencedType())) {
            fieldToCheck.add(metadata);
        }
        super.visit(metadata);
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(SimpleTypeMetadata typeMetadata) {
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(MetadataRepository repository) {
        super.visit(repository);
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(FieldMetadata metadata) {
        super.visit(metadata);
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(SimpleTypeFieldMetadata metadata) {
        super.visit(metadata);
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(EnumerationFieldMetadata metadata) {
        super.visit(metadata);
        return fieldToCheck;
    }
}
