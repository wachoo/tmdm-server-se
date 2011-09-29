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

public class ReferenceFieldMetadata implements FieldMetadata {

    private final boolean isKey;

    private final boolean isMany;

    private final String name;

    private final FieldMetadata referencedField;

    private final String foreignKeyInfo;

    private final boolean allowFKIntegrityOverride;

    private final boolean isFKIntegrity;

    private TypeMetadata referencedType;

    private TypeMetadata containingType;

    private TypeMetadata declaringType;

    public ReferenceFieldMetadata(TypeMetadata containingType,
                                  boolean isKey,
                                  boolean isMany,
                                  String name,
                                  TypeMetadata referencedType,
                                  FieldMetadata referencedField,
                                  String foreignKeyInfo,
                                  boolean fkIntegrity,
                                  boolean allowFKIntegrityOverride) {
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
    }

    public TypeMetadata getReferencedType() {
        return referencedType;
    }

    public String getName() {
        return name;
    }

    public boolean hasForeignKeyInfo() {
        return foreignKeyInfo != null;
    }

    public String getForeignKeyInfoField() {
        return foreignKeyInfo;
    }

    public TypeMetadata getContainingType() {
        return containingType;
    }

    public void setContainingType(TypeMetadata typeMetadata) {
        this.containingType = typeMetadata;
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

    public void adopt(ComplexTypeMetadata metadata) {
        FieldMetadata copy = copy();
        copy.setContainingType(metadata);
        metadata.addField(copy);
    }

    public String getType() {
        return referencedField.getType();
    }

    public boolean isKey() {
        return isKey;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public FieldMetadata copy() {
        return new ReferenceFieldMetadata(containingType, isKey, isMany, name, referencedType, referencedField, foreignKeyInfo, isFKIntegrity, allowFKIntegrityOverride);
    }

    @Override
    public String toString() {
        return "Reference {" +
                "containing type= " + containingType +
                ", declaring type=" + declaringType +
                ", name='" + name + '\'' +
                ", isKey=" + isKey +
                ", is many=" + isMany +
                ", referenced type= " + referencedType +
                ", referenced field= " + referencedField +
                ", foreign key info='" + foreignKeyInfo + '\'' +
                ", allow FK integrity override= " + allowFKIntegrityOverride +
                ", check FK integrity= " + isFKIntegrity +
                '}';
    }
}