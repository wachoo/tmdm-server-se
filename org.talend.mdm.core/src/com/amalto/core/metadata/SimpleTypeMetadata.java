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

import org.apache.commons.lang.NotImplementedException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SimpleTypeMetadata implements TypeMetadata {

    private final String name;

    private final String nameSpace;

    public SimpleTypeMetadata(String nameSpace, String name) {
        this.name = name;
        this.nameSpace = nameSpace;
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return Collections.emptySet();
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return nameSpace;
    }

    public boolean isAbstract() {
        return true;
    }

    public FieldMetadata getField(String fieldName) {
        throw new IllegalStateException("Should not be called on a simple type.");
    }

    public List<FieldMetadata> getFields() {
        return Collections.emptyList();
    }

    public boolean isAssignableFrom(TypeMetadata type) {
        // Don't support inheritance with simple types.
        return false;
    }

    public void addSuperType(TypeMetadata superType) {
        throw new IllegalStateException("Cannot add a super type to a simple type");
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return '[' + nameSpace + ':' + name + ']';
    }

}
