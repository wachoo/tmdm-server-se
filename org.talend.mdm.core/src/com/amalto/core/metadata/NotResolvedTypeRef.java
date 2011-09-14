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

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
*
*/
class NotResolvedTypeRef implements TypeMetadata {

    public static TypeMetadata INSTANCE = new NotResolvedTypeRef();

    NotResolvedTypeRef() {
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return Collections.emptyList();
    }

    public void addSuperType(TypeMetadata superType) {
        // Nothing to do
    }

    public String getName() {
        return StringUtils.EMPTY;
    }

    public String getNamespace() {
        return StringUtils.EMPTY;
    }

    public boolean isAbstract() {
        return false;
    }

    public FieldMetadata getField(String fieldName) {
        return null;
    }

    public List<FieldMetadata> getFields() {
        return Collections.emptyList();
    }

    public boolean isAssignableFrom(TypeMetadata type) {
        return false;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
