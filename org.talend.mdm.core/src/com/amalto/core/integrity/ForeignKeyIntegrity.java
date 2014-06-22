/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.integrity;

import org.talend.mdm.commmon.metadata.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Returns the field that references the concept <code>typeName</code>. References are returned as a {@link Set} of
 * {@link ReferenceFieldMetadata}.
 */
public class ForeignKeyIntegrity extends DefaultMetadataVisitor<Set<ReferenceFieldMetadata>> {

    public static final String ATTRIBUTE_ROOTTYPE = "RootType"; //$NON-NLS-1$

    public static final String ATTRIBUTE_XPATH = "XPath"; //$NON-NLS-1$

    // Internal: for optimization purpose prevents checking a type more than once.
    private final Set<TypeMetadata> checkedTypes = new HashSet<TypeMetadata>();

    // Foreign key fields list to be returned at end of visit.
    private final Set<ReferenceFieldMetadata> fieldToCheck = new HashSet<ReferenceFieldMetadata>();

    private final TypeMetadata type;

    private Stack<String> currentPosition = new Stack<String>();

    private String rootTypeName;

    private String getCurrentPath() {
        StringBuilder path = new StringBuilder();
        for (String pathElement : currentPosition) {
            if (path.length() == 0) {
                rootTypeName = pathElement;
            }
            if (path.length() > 0) {
                path.append('/');
            }
            path.append(pathElement);
        }
        return path.toString();
    }

    /**
     * This {@link MetadataVisitor} returns foreign key fields that points to <code>type</code>.
     * @param type A type.
     */
    public ForeignKeyIntegrity(TypeMetadata type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null"); //$NON-NLS-1$
        }
        this.type = type;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(ComplexTypeMetadata metadata) {
        if (!checkedTypes.contains(metadata)) {
            checkedTypes.add(metadata);
            currentPosition.push(metadata.getName());
            Set<ReferenceFieldMetadata> result = super.visit(metadata);
            currentPosition.pop();
            return result;
        }
        return fieldToCheck;

    }

    @Override
    public Set<ReferenceFieldMetadata> visit(ReferenceFieldMetadata metadata) {
        currentPosition.push(metadata.getName());
        {
            if (type.isAssignableFrom(metadata.getReferencedType())) {
                metadata.setData(ATTRIBUTE_XPATH, getCurrentPath());
                metadata.setData(ATTRIBUTE_ROOTTYPE, rootTypeName);
                fieldToCheck.add(metadata);
            }
            for (ComplexTypeMetadata subType : metadata.getReferencedType().getSubTypes()) {
                subType.accept(this);
            }
            super.visit(metadata);
        }
        currentPosition.pop();
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(SimpleTypeMetadata typeMetadata) {
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(MetadataRepository repository) {
        Collection<TypeMetadata> types = repository.getTypes();
        for (TypeMetadata type : types) {
            type.accept(this);
        }
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(FieldMetadata metadata) {
        super.visit(metadata);
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(SimpleTypeFieldMetadata metadata) {
        currentPosition.push(metadata.getName());
        {
            super.visit(metadata);
        }
        currentPosition.pop();
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(EnumerationFieldMetadata metadata) {
        super.visit(metadata);
        return fieldToCheck;
    }

    @Override
    public Set<ReferenceFieldMetadata> visit(ContainedTypeFieldMetadata metadata) {
        currentPosition.push(metadata.getName());
        {
            super.visit(metadata);
            for (ComplexTypeMetadata subType : metadata.getContainedType().getSubTypes()) {
                subType.accept(this);
            }
        }
        currentPosition.pop();
        return fieldToCheck;
    }
}
