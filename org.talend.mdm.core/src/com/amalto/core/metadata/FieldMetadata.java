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

import java.util.List;

/**
 * Represents a "field" in a {@link ComplexTypeMetadata}. A field is an element in a MDM entity where user can specify
 * values.
 */
public interface FieldMetadata extends MetadataVisitable, MetadataExtensible {

    /**
     * @return The field's name that should be unique within the type
     */
    public String getName();

    /**
     * @return <code>true</code> if field is defined as key <b>in its declaring type</b>. Field may not be defined as
     *         a key in its declaring type but might be used as a key in sub types.
     */
    public boolean isKey();

    /**
     * @return The {@link TypeMetadata} of the field's value.
     */
    TypeMetadata getType();

    /**
     * @return Returns the {@link TypeMetadata} that <b>contains</b> the field. Field might be contained by a type but
     *         declared in another (in case of inheritance).
     * @see #getDeclaringType()
     */
    ComplexTypeMetadata getContainingType();

    /**
     * @return Returns the {@link TypeMetadata} that <b>declares</b> the field. Field might be contained by a type but
     *         declared in another (in case of inheritance).
     * @see #getContainingType()
     */
    TypeMetadata getDeclaringType();

    /**
     * @return The {@link List} of users this field should be hidden to.
     */
    List<String> getHideUsers();

    /**
     * @return The {@link List} of users allowed to write to this field.
     */
    List<String> getWriteUsers();

    /**
     * @return <code>true</code> if field contains multiple values (i.e. a sequence of values).
     */
    boolean isMany();

    /**
     * @return <code>true</code> if field <b>must</b> have a value (usually, this means a minOccurs='1' in the data model).
     */
    boolean isMandatory();

    /**
     * "Adopt" the field in <code>metadata</code> type. This method performs all necessary operations so this field behaves
     * as is
     * @param metadata The new type.
     * @param repository The {@link MetadataRepository} of the type to adopt.
     */
    void adopt(ComplexTypeMetadata metadata, MetadataRepository repository);

    /**
     * Copy the field and all depending information in <code>repository</code>.
     * @param repository The {@link MetadataRepository} to copy to.
     * @return A copy of this field.
     */
    FieldMetadata copy(MetadataRepository repository);

    /**
     * Changes containing type for this field. Use this method with extra caution.
     *
     * @param typeMetadata The new containing type for this field.
     * @see #getContainingType()
     */
    void setContainingType(ComplexTypeMetadata typeMetadata);

    /**
     * Freezes all modifications that can be done to a field. Similar to {@link TypeMetadata#freeze(ValidationHandler)}.
     * @return A frozen field metadata.
     * @see TypeMetadata#freeze(ValidationHandler)
     * @param handler A {@link ValidationHandler} to receive all data model errors and warnings (if any).
     */
    FieldMetadata freeze(ValidationHandler handler);

    /**
     * Promotes this field to "key". After this method has been called, {@link #isKey()} must return <code>true</code>.
     */
    void promoteToKey();
}
