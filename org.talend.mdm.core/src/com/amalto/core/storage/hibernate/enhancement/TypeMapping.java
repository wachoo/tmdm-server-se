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

package com.amalto.core.storage.hibernate.enhancement;

import com.amalto.core.metadata.*;
import com.amalto.core.storage.hibernate.MappingMetadataRepository;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.NotImplementedException;

import java.util.Collection;
import java.util.List;

/**
 * Represents type mapping between data model as specified by the user and data model as used by hibernate storage.
 */
public class TypeMapping implements ComplexTypeMetadata {

    private final ComplexTypeMetadata flatten;

    private final ComplexTypeMetadata user;

    private final MappingMetadataRepository mappingMetadataRepository;

    private final BidiMap map = new DualHashBidiMap();

    private ComplexTypeMetadata selected;

    private boolean isFrozen;

    public TypeMapping(ComplexTypeMetadata user, MappingMetadataRepository mappingMetadataRepository) {
        this.user = user;
        this.mappingMetadataRepository = mappingMetadataRepository;
        this.flatten = (ComplexTypeMetadata) user.copyShallow();
        selected = flatten;
    }

    public TypeMapping(ComplexTypeMetadata user, ComplexTypeMetadata flatten, MappingMetadataRepository mappingMetadataRepository) {
        this.user = user;
        this.mappingMetadataRepository = mappingMetadataRepository;
        this.flatten = flatten;
        selected = flatten;
    }

    public MappingMetadataRepository getMappingMetadataRepository() {
        return mappingMetadataRepository;
    }

    public void toFlatten() {
        selected = flatten;
    }

    public void toUser() {
        selected = user;
    }

    void map(FieldMetadata user, FieldMetadata flatten) {
        map.put(user, flatten);
    }

    public FieldMetadata getFlatten(FieldMetadata from) {
        FieldMetadata flattenField = (FieldMetadata) map.get(from);
        // TODO Get field by name if "from" reference isn't 'instance equals' (but the 'why' part of it should be investigated).
        if (flattenField == null) {
            MapIterator values = map.mapIterator();
            while (values.hasNext()) {
                values.next();
                FieldMetadata keyAsField = (FieldMetadata) values.getKey();
                if (keyAsField.getName().equals(from.getName())) {
                    flattenField = (FieldMetadata) values.getValue();
                }
            }
        }
        return flattenField;
    }

    public FieldMetadata getUser(FieldMetadata to) {
        return (FieldMetadata) map.getKey(to);
    }

    ComplexTypeMetadata getType() {
        return selected;
    }

    public void addSuperType(TypeMetadata superType, MetadataRepository repository) {
        getType().addSuperType(superType, repository);
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return getType().getSuperTypes();
    }

    public String getName() {
        return getType().getName();
    }

    public String getNamespace() {
        return getType().getNamespace();
    }

    public boolean isAbstract() {
        return getType().isAbstract();
    }

    public FieldMetadata getField(String fieldName) {
        return getType().getField(fieldName);
    }

    public List<FieldMetadata> getKeyFields() {
        return getType().getKeyFields();
    }

    public List<FieldMetadata> getFields() {
        return getType().getFields();
    }

    public boolean isAssignableFrom(TypeMetadata type) {
        return getType().isAssignableFrom(type);
    }

    public TypeMetadata copy(MetadataRepository repository) {
        throw new NotImplementedException(); // Not supported
    }

    public TypeMetadata copyShallow() {
        return getType().copyShallow();
    }

    public TypeMetadata freeze() {
        if (isFrozen) {
            return this;
        }
        isFrozen = true;
        flatten.freeze();
        user.freeze();
        return this;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return getType().accept(visitor);
    }

    @Override
    public String toString() {
        return getType().toString();
    }

    public void addField(FieldMetadata fieldMetadata) {
        getType().addField(fieldMetadata);
    }

    public List<String> getWriteUsers() {
        return getType().getWriteUsers();
    }

    public List<String> getHideUsers() {
        return getType().getHideUsers();
    }

    public List<String> getDenyCreate() {
        return getType().getDenyCreate();
    }

    public List<String> getDenyDelete(DeleteType type) {
        return getType().getDenyDelete(type);
    }

    public String getSchematron() {
        return getType().getSchematron();
    }

    public void registerKey(FieldMetadata keyField) {
        getType().registerKey(keyField);
    }
}
