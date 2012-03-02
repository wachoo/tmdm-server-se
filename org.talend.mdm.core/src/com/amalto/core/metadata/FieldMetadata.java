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

import java.util.List;

/**
 *
 */
public interface FieldMetadata extends MetadataVisitable {

    public String getName();

    public boolean isKey();

    TypeMetadata getType();

    boolean hasForeignKeyInfo();

    FieldMetadata getForeignKeyInfoField();

    ComplexTypeMetadata getContainingType();

    void setContainingType(ComplexTypeMetadata typeMetadata);

    TypeMetadata getDeclaringType();

    boolean isFKIntegrity();

    boolean allowFKIntegrityOverride();

    void adopt(ComplexTypeMetadata metadata, MetadataRepository repository);

    FieldMetadata copy(MetadataRepository repository);

    List<String> getHideUsers();

    List<String> getWriteUsers();

    boolean isMany();

    boolean isMandatory();

    void setName(String fieldName);
}
