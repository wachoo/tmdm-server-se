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

import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class StorageTableResolver implements TableResolver {

    private final Set<FieldMetadata> indexedFields;

    private final int maxLength;

    private final AtomicInteger fkIncrement = new AtomicInteger();

    private final Set<String> referenceFieldNames = new HashSet<String>();

    public StorageTableResolver(Set<FieldMetadata> indexedFields) {
        this(indexedFields, Integer.MAX_VALUE);
    }

    public StorageTableResolver(Set<FieldMetadata> indexedFields, int maxLength) {
        this.indexedFields = indexedFields;
        this.maxLength = maxLength;
    }

    public String get(ComplexTypeMetadata type) {
        return type.getName().toUpperCase();
    }

    public String get(FieldMetadata field) {
        return field.getName();
    }

    public boolean isIndexed(FieldMetadata field) {
        return indexedFields.contains(field);
    }

    @Override
    public int getNameMaxLength() {
        return maxLength;
    }

    @Override
    public String getFkConstraintName(ReferenceFieldMetadata referenceField) {
        // TMDM-6896 Uses containing type length since FK collision issues happens when same FK is contained in a type
        // with same
        // length but different name.
        if (!referenceFieldNames.add(referenceField.getContainingType().getName().length() + '_' + referenceField.getName())) {
            return MappingGenerator.formatSQLName("FK_" + Math.abs(referenceField.getName().hashCode()) + fkIncrement.incrementAndGet(), maxLength); //$NON-NLS-1$
        } else {
            return StringUtils.EMPTY;
        }
    }
}
