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

public class ReferenceFieldMetadata extends AbstractMetadataExtensible implements FieldMetadata {

    private final boolean isKey;

    private final boolean isMany;

    private final boolean allowFKIntegrityOverride;

    private final boolean isFKIntegrity;

    private final List<String> hideUsers;

    private TypeMetadata fieldType;
    private final List<String> writeUsers;

    private final boolean isMandatory;

    private final String name;

    private final TypeMetadata declaringType;

    private FieldMetadata referencedField;

    private FieldMetadata foreignKeyInfo;

    private ComplexTypeMetadata referencedType;

    private ComplexTypeMetadata containingType;

    private boolean isFrozen;

    private int cachedHashCode;

    public ReferenceFieldMetadata(ComplexTypeMetadata containingType,
                                  boolean isKey,
                                  boolean isMany,
                                  boolean isMandatory,
                                  String name,
                                  ComplexTypeMetadata referencedType,
                                  FieldMetadata referencedField,
                                  FieldMetadata foreignKeyInfo,
                                  boolean fkIntegrity,
                                  boolean allowFKIntegrityOverride,
                                  TypeMetadata fieldType,
                                  List<String> allowWriteUsers,
                                  List<String> hideUsers) {
        this.isMandatory = isMandatory;
        this.name = name;
        this.referencedField = referencedField;
        this.foreignKeyInfo = foreignKeyInfo;
        this.containingType = containingType;
        this.declaringType = containingType;
        this.allowFKIntegrityOverride = allowFKIntegrityOverride;
        this.isFKIntegrity = fkIntegrity;
        this.referencedType = referencedType;
        this.isKey = isKey;
        this.isMany = isMany;
        this.referencedType = referencedType;
        this.fieldType = fieldType;
        this.writeUsers = allowWriteUsers;
        this.hideUsers = hideUsers;
    }

    public FieldMetadata getReferencedField() {
        return referencedField;
    }

    public ComplexTypeMetadata getReferencedType() {
        return referencedType;
    }

    public String getName() {
        return name;
    }

    public boolean hasForeignKeyInfo() {
        return foreignKeyInfo != null;
    }

    public FieldMetadata getForeignKeyInfoField() {
        return foreignKeyInfo;
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
        TypeMetadata currentType = fieldType;
        while (!currentType.getSuperTypes().isEmpty()) {
            currentType = currentType.getSuperTypes().iterator().next();
        }
        if (!"string".equals(currentType.getName())) { //$NON-NLS-1$
            /*
            handler.error("FK field '" + getName() + "' is invalid because it isn't typed as string (nor a string restriction).");
            return this;
            */
        }
        if (foreignKeyInfo != null) {
            foreignKeyInfo = foreignKeyInfo.freeze(handler);
        }
        referencedField = referencedField.freeze(handler);
        referencedType = (ComplexTypeMetadata) referencedType.freeze(handler);
        return this;
    }

    public void promoteToKey() {
        throw new UnsupportedOperationException("FK field can't be promoted to key.");
    }

    public TypeMetadata getDeclaringType() {
        return containingType;
    }

    public boolean isFKIntegrity() {
        return isFKIntegrity;
    }

    public boolean allowFKIntegrityOverride() {
        return allowFKIntegrityOverride;
    }

    public void adopt(ComplexTypeMetadata metadata, MetadataRepository repository) {
        FieldMetadata copy = copy(repository);
        copy.setContainingType(metadata);
        metadata.addField(copy);
    }

    public TypeMetadata getType() {
        return referencedField.getType();
    }

    public boolean isKey() {
        return isKey;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public FieldMetadata copy(MetadataRepository repository) {
        ComplexTypeMetadata referencedTypeCopy = (ComplexTypeMetadata) referencedType.copy(repository);
        FieldMetadata referencedFieldCopy = referencedField.copy(repository);
        FieldMetadata foreignKeyInfoCopy = hasForeignKeyInfo() ? foreignKeyInfo.copy(repository) : null;
        ComplexTypeMetadata containingTypeCopy = (ComplexTypeMetadata) containingType.copy(repository);
        return new ReferenceFieldMetadata(containingTypeCopy,
                isKey,
                isMany,
                isMandatory,
                name,
                referencedTypeCopy,
                referencedFieldCopy,
                foreignKeyInfoCopy,
                isFKIntegrity,
                allowFKIntegrityOverride,
                fieldType,
                writeUsers,
                hideUsers);
    }

    @Override
    public String toString() {
        return "Reference {" + //$NON-NLS-1$
                "containing type= " + containingType + //$NON-NLS-1$
                ", declaring type=" + declaringType + //$NON-NLS-1$
                ", name='" + name + '\'' + //$NON-NLS-1$
                ", isKey=" + isKey + //$NON-NLS-1$
                ", is many=" + isMany + //$NON-NLS-1$
                ", referenced type= " + referencedType + //$NON-NLS-1$
                ", referenced field= " + referencedField + //$NON-NLS-1$
                ", foreign key info='" + foreignKeyInfo + '\'' + //$NON-NLS-1$
                ", allow FK integrity override= " + allowFKIntegrityOverride + //$NON-NLS-1$
                ", check FK integrity= " + isFKIntegrity + //$NON-NLS-1$
                '}';
    }

    public boolean isMany() {
        return isMany;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public List<String> getHideUsers() {
        return hideUsers;
    }

    public List<String> getWriteUsers() {
        return writeUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceFieldMetadata)) return false;

        ReferenceFieldMetadata that = (ReferenceFieldMetadata) o;

        if (allowFKIntegrityOverride != that.allowFKIntegrityOverride) return false;
        if (isFKIntegrity != that.isFKIntegrity) return false;
        if (isKey != that.isKey) return false;
        if (isMandatory != that.isMandatory) return false;
        if (isMany != that.isMany) return false;
        if (containingType != null ? !containingType.equals(that.containingType) : that.containingType != null)
            return false;
        if (declaringType != null ? !declaringType.equals(that.declaringType) : that.declaringType != null)
            return false;
        if (foreignKeyInfo != null ? !foreignKeyInfo.equals(that.foreignKeyInfo) : that.foreignKeyInfo != null)
            return false;
        if (hideUsers != null ? !hideUsers.equals(that.hideUsers) : that.hideUsers != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (writeUsers != null ? !writeUsers.equals(that.writeUsers) : that.writeUsers != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        if (isFrozen && cachedHashCode > 0) {
            return cachedHashCode;
        }
        int result = (isKey ? 1 : 0);
        result = 31 * result + (isMany ? 1 : 0);
        result = 31 * result + (foreignKeyInfo != null ? foreignKeyInfo.hashCode() : 0);
        result = 31 * result + (containingType != null ? containingType.hashCode() : 0);
        result = 31 * result + (declaringType != null ? declaringType.hashCode() : 0);
        result = 31 * result + (allowFKIntegrityOverride ? 1 : 0);
        result = 31 * result + (isFKIntegrity ? 1 : 0);
        result = 31 * result + (isMandatory ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        cachedHashCode = result;
        return result;
    }
}