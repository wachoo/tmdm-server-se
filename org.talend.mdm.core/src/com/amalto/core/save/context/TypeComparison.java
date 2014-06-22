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

package com.amalto.core.save.context;

import org.talend.mdm.commmon.metadata.*;

import java.util.Set;
import java.util.Stack;

class TypeComparison extends DefaultMetadataVisitor<Set<String>> {

    private final Stack<String> pathPrefix = new Stack<String>();

    private final Set<String> paths;

    private ComplexTypeMetadata type;

    TypeComparison(ComplexTypeMetadata type, Set<String> paths) {
        this.type = type;
        this.paths = paths;
    }

    private String getCurrentPath(FieldMetadata referenceField) {
        StringBuilder builder = new StringBuilder();
        for (String currentPathElement : pathPrefix) {
            builder.append(currentPathElement);
            builder.append('/');
        }
        return builder.append(referenceField.getName()).toString();
    }

    @Override
    public Set<String> visit(ComplexTypeMetadata complexType) {
        super.visit(complexType);
        return paths;
    }

    @Override
    public Set<String> visit(ReferenceFieldMetadata referenceField) {
        try {
            type.getField(getCurrentPath(referenceField));
        } catch (Exception e) {
            paths.add(getCurrentPath(referenceField));
        }
        return paths;
    }

    @Override
    public Set<String> visit(ContainedTypeFieldMetadata containedField) {
        pathPrefix.push(containedField.getName());
        {
            if (type.hasField(containedField.getName())) {
                type = containedField.getContainedType();
                super.visit(containedField);
                type = containedField.getContainingType();
            } else {
                paths.add(containedField.getName());
            }
        }
        pathPrefix.pop();
        return paths;
    }

    @Override
    public Set<String> visit(FieldMetadata fieldMetadata) {
        try {
            type.getField(fieldMetadata.getName());
        } catch (Exception e) {
            paths.add(getCurrentPath(fieldMetadata));
        }
        return paths;
    }

    @Override
    public Set<String> visit(SimpleTypeFieldMetadata simpleField) {
        try {
            type.getField(simpleField.getName());
        } catch (Exception e) {
            paths.add(getCurrentPath(simpleField));
        }
        return paths;
    }

    @Override
    public Set<String> visit(EnumerationFieldMetadata enumField) {
        try {
            type.getField(enumField.getName());
        } catch (Exception e) {
            paths.add(getCurrentPath(enumField));
        }
        return paths;
    }
}
