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
import java.util.List;

public class ContainedComplexTypeRef extends ContainedComplexTypeMetadata {

    private final SoftTypeRef reference;

    public ContainedComplexTypeRef(ComplexTypeMetadata containerType, String nameSpace, String name, SoftTypeRef reference) {
        super(containerType, nameSpace, name);
        this.reference = reference;
    }

    @Override
    public FieldMetadata getField(String fieldName) {
        return reference.getField(fieldName);
    }

    @Override
    public void addField(FieldMetadata fieldMetadata) {
    }

    @Override
    public Collection<TypeMetadata> getSuperTypes() {
        return reference.getSuperTypes();
    }

    @Override
    public List<FieldMetadata> getFields() {
        return reference.getFields();
    }

    @Override
    public boolean isAssignableFrom(TypeMetadata type) {
        return reference.isAssignableFrom(type);
    }
}
