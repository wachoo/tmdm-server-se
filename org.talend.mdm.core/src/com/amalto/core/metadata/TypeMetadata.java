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

/**
 * Representation of a type in MDM. Types can either be:
 * <ul>
 * <li>Simple: such as string, double... and usually represented as {@link SimpleTypeMetadata}</li>.
 * <li>Complex: for user defined entity types and usually represented as {@link ComplexTypeMetadata}</li>.
 * </ul>
 */
public interface TypeMetadata extends MetadataVisitable, MetadataExtensible {

    /**
     * Returns a <b>READ ONLY</b> collection of super types. For adding super type see {@link #addSuperType(TypeMetadata, MetadataRepository)}
     *
     * @return A collection of super types.
     */
    Collection<TypeMetadata> getSuperTypes();

    /**
     * <p>
     * Adds a super type for this type. This causes all fields in super type to be added to this type.
     * </p>
     *
     * @param superType  A type.
     * @param repository Repository needed to copy type/fields references if any.
     */
    void addSuperType(TypeMetadata superType, MetadataRepository repository);

    /**
     * @return Type's name as it can be used for the {@link MetadataRepository#getType(String, String)} method.
     */
    String getName();

    /**
     * Change type name (be careful if this type was already registered to a {@link MetadataRepository} instance).
     */
    void setName(String name);

    /**
     * @return Type's namespace as it can be used for the {@link MetadataRepository#getType(String, String)} method.
     */
    String getNamespace();

    /**
     * @param type A type.
     * @return Returns <code>true</code> if <u>this</u> type can be safely casted to <code>type</code>. This returns <code>true</code>
     *         if <u>this</u> type is a sub type of <code>type</code>.
     */
    boolean isAssignableFrom(TypeMetadata type);

    /**
     * Copies this type to another {@link MetadataRepository}. This method provides "deep copy" as it copies all metadata
     * reachable from this type.
     *
     * @param repository The {@link MetadataRepository} the copy should be added to.
     * @return A copy of the current type registered in <code>repository</code>.
     */
    TypeMetadata copy(MetadataRepository repository);

    /**
     * Copies this type to another {@link MetadataRepository}. This method provides "shallow copy" as it copies only fields
     * declared in this type (and does not navigate through all relationships).
     *
     * @return A copy of the current type.
     */
    TypeMetadata copyShallow();

    /**
     * Mark type as unmodifiable and resolves all information (fields, super type) that <b>must</b> be present in {@link MetadataRepository}
     * when this method is called.
     *
     * @return A {@link TypeMetadata} that can't be modified afterwards.
     * @param handler
     */
    TypeMetadata freeze(ValidationHandler handler);

    /**
     * @return <code>true</code> if this type can be used to create an entity in MDM, <code>false</code> otherwise. A
     *         so called 'reusable' type must return <code>false</code> for this method.
     */
    boolean isInstantiable();

    /**
     * @return <code>true</code> if type can not be modified ({@link #freeze(ValidationHandler)} was previously called), <code>false</code>
     * otherwise.
     */
    boolean isFrozen();
}
