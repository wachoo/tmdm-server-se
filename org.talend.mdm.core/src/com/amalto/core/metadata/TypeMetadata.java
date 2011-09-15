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
public interface TypeMetadata extends MetadataVisitable {

    /**
     * Returns a <b>READ ONLY</b> collection of super types. For adding super type see {@link #addSuperType(TypeMetadata)}
     * @return A collection of super types.
     */
    Collection<TypeMetadata> getSuperTypes();

    /**
     * <p>
     * Adds a super type for this type. This causes all fields in super type to be added to this type.
     * </p>
     * @param superType A type.
     */
    void addSuperType(TypeMetadata superType);

    String getName();

    String getNamespace();

    boolean isAbstract();

    FieldMetadata getField(String fieldName);

    /**
     * Returns a <b>READ ONLY</b> collection of fields. For adding super type see {@link ComplexTypeMetadata#addField(FieldMetadata)}}
     *
     * @return A collection of super types.
     */
    List<FieldMetadata> getFields();

    /**
     * @param type A type.
     * @return Returns <code>true</code> if <u>this</u> type can be safely casted to <code>type</code>. This returns <code>true</code>
     * if <u>this</u> type is a sub type of <code>type</code>.
     */
    boolean isAssignableFrom(TypeMetadata type);


}
