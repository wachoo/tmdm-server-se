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
 *
 */
public class EnumerationFieldMetadata extends AbstractMetadataExtensible implements FieldMetadata {

    private boolean isKey;

    private TypeMetadata fieldType;

    private final List<String> allowWriteUsers;

    private final List<String> hideUsers;

    private TypeMetadata declaringType;

    private final boolean isMany;

    private final boolean isMandatory;

    private ComplexTypeMetadata containingType;

    private String name;

    private boolean isFrozen;

    private int cachedHashCode;

    public EnumerationFieldMetadata(ComplexTypeMetadata containingType,
                                    boolean isKey,
                                    boolean isMany, boolean isMandatory, String name,
                                    TypeMetadata fieldType,
                                    List<String> allowWriteUsers,
                                    List<String> hideUsers) {
        if (isKey && !isMandatory) {
            throw new IllegalArgumentException("Key fields are mandatory");
        }

        this.containingType = containingType;
        this.declaringType = containingType;
        this.isKey = isKey;
        this.isMany = isMany;
        this.isMandatory = isMandatory;
        this.name = name;
        this.fieldType = fieldType;
        this.allowWriteUsers = allowWriteUsers;
        this.hideUsers = hideUsers;
    }

    public String getName() {
        return name;
    }

    public boolean isKey() {
        return isKey;
    }

    public TypeMetadata getType() {
        return fieldType;
    }

    public ComplexTypeMetadata getContainingType() {
        return containingType;
    }

    public void setContainingType(ComplexTypeMetadata typeMetadata) {
        this.containingType = typeMetadata;
    }

    public FieldMetadata freeze(ValidationHandler handler) {
        if (isFrozen) {
            return this;
        }
        isFrozen = true;
        fieldType = fieldType.freeze(handler);
        return this;
    }

    public void promoteToKey() {
        isKey = true;
    }

    public TypeMetadata getDeclaringType() {
        return declaringType;
    }

    public void adopt(ComplexTypeMetadata metadata, MetadataRepository repository) {
        FieldMetadata copy = copy(repository);
        copy.setContainingType(metadata);
        metadata.addField(copy);
    }

    public FieldMetadata copy(MetadataRepository repository) {
        return new EnumerationFieldMetadata(containingType, isKey(), isMany, isMandatory, name, fieldType, allowWriteUsers, hideUsers);
    }

    public List<String> getHideUsers() {
        return hideUsers;
    }

    public List<String> getWriteUsers() {
        return allowWriteUsers;
    }

    public boolean isMany() {
        return isMany;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "EnumerationFieldMetadata{" +  //$NON-NLS-1$
                "declaringType=" + declaringType + //$NON-NLS-1$
                ", containingType=" + containingType + //$NON-NLS-1$
                ", is key=" + isKey + //$NON-NLS-1$
                ", name ='" + name + '\'' + //$NON-NLS-1$
                ", type name ='" + fieldType.getName() + '\'' +  //$NON-NLS-1$
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnumerationFieldMetadata)) {
            return false;
        }

        EnumerationFieldMetadata that = (EnumerationFieldMetadata) o;

        if (isKey != that.isKey) return false;
        if (isMandatory != that.isMandatory) return false;
        if (isMany != that.isMany) return false;
        if (allowWriteUsers != null ? !allowWriteUsers.equals(that.allowWriteUsers) : that.allowWriteUsers != null)
            return false;
        if (containingType != null ? !containingType.equals(that.containingType) : that.containingType != null)
            return false;
        if (declaringType != null ? !declaringType.equals(that.declaringType) : that.declaringType != null)
            return false;
        if (fieldType != null ? !fieldType.equals(that.fieldType) : that.fieldType != null) return false;
        if (hideUsers != null ? !hideUsers.equals(that.hideUsers) : that.hideUsers != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        if (isFrozen && cachedHashCode > 0) {
            return cachedHashCode;
        }
        int result = (isKey ? 1 : 0);
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        result = 31 * result + (allowWriteUsers != null ? allowWriteUsers.hashCode() : 0);
        result = 31 * result + (hideUsers != null ? hideUsers.hashCode() : 0);
        result = 31 * result + (declaringType != null ? declaringType.hashCode() : 0);
        result = 31 * result + (isMany ? 1 : 0);
        result = 31 * result + (isMandatory ? 1 : 0);
        result = 31 * result + (containingType != null ? containingType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        cachedHashCode = result;
        return result;
    }
}
