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
 * Represents a "complex" type (i.e. a MDM entity type).
 */
public interface ComplexTypeMetadata extends TypeMetadata {
    /**
     * @return A {@link List} of {@link FieldMetadata} that represents key information for the complex type. This method
     *         might return an empty list if no key field is defined for this type.
     * @see #registerKey(FieldMetadata)
     */
    List<FieldMetadata> getKeyFields();

    /**
     * @param keyField Register <code>keyField</code> as a key for this type.
     * @throws IllegalArgumentException If <code>keyField</code> is <code>null</code>.
     */
    void registerKey(FieldMetadata keyField);

    /**
     * @param fieldName A field name.
     * @return The {@link FieldMetadata} for the given <code>fieldName</code>.
     * @throws IllegalArgumentException If the field is not declared in type or type's super types.
     */
    FieldMetadata getField(String fieldName);

    /**
     * Returns a <b>READ ONLY</b> collection of fields. For adding super type see {@link ComplexTypeMetadata#addField(FieldMetadata)}}
     *
     * @return A collection of super types.
     */
    List<FieldMetadata> getFields();

    /**
     * Adds a new field to this type. Please note that if {@link com.amalto.core.metadata.FieldMetadata#isKey()} returns
     * <code>true</code>, there's no need to call {@link #registerKey(FieldMetadata)}.
     *
     * @param fieldMetadata A new field to add to this type.
     * @throws IllegalArgumentException If <code>fieldMetadata</code> is <code>null</code>.
     */
    void addField(FieldMetadata fieldMetadata);

    /**
     * @return The {@link List} of users allowed to write to this type.
     */
    List<String> getWriteUsers();

    /**
     * @return The {@link List} of users this type should be hidden to.
     */
    List<String> getHideUsers();

    /**
     * @return The {@link List} of users that can't create an instance of this type.
     */
    List<String> getDenyCreate();

    /**
     * @param type Type of delete (physical delete, logical delete aka. 'send-to-trash delete').
     * @return The {@link List} of users that can't delete an instance of this type.
     */
    List<String> getDenyDelete(DeleteType type);

    /**
     * @return Schematron validation rules for this type ready for immediate use (no need to unescape XML characters).
     * Returns an empty string if no schematron rule was specified for this type.
     */
    String getSchematron();
    
    enum DeleteType {
        /**
         * Logical delete (a.k.a. send to trash)
         */
        LOGICAL,
        /**
         * Physical delete (permanent delete).
         */
        PHYSICAL
    }
}
