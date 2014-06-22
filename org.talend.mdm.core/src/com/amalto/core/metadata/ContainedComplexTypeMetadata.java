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

import org.apache.commons.lang.StringUtils;

/**
 * A "contained" type represents a type inside another type (but this type can not be used as MDM entity type). Usually
 * contained types are created for XSD anonymous types.
 */
public class ContainedComplexTypeMetadata extends ComplexTypeMetadataImpl {

    private final ComplexTypeMetadata containerType;

    public ContainedComplexTypeMetadata(ComplexTypeMetadata containerType, String nameSpace, String name) {
        // Inherits permissions from container type.
        super(nameSpace,
                name,
                containerType.getWriteUsers(),
                containerType.getDenyCreate(),
                containerType.getHideUsers(),
                containerType.getDenyDelete(DeleteType.PHYSICAL),
                containerType.getDenyDelete(DeleteType.LOGICAL),
                StringUtils.EMPTY,
                false);
        this.containerType = containerType;
    }

    /**
     * @return The {@link ComplexTypeMetadata} that contains this type. Please note that container type might also be
     *         a contained type.
     */
    public ComplexTypeMetadata getContainerType() {
        return containerType;
    }

    @Override
    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
