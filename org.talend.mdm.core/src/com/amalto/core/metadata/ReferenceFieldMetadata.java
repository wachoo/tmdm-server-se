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

public abstract class ReferenceFieldMetadata implements FieldMetadata {

    protected final String name;

    protected final FieldMetadata referencedField;

    protected final String foreignKeyInfo;

    protected final boolean allowFKIntegrityOverride;

    protected final boolean isFKIntegrity;

    private TypeMetadata referencedType;

    protected TypeMetadata containingType;

    public ReferenceFieldMetadata(String name,
                                  TypeMetadata containingType,
                                  TypeMetadata referencedType,
                                  FieldMetadata referencedField,
                                  boolean allowFKIntegrityOverride,
                                  boolean FKIntegrity,
                                  String foreignKeyInfo) {
        this.name = name;
        this.referencedField = referencedField;
        this.foreignKeyInfo = foreignKeyInfo;
        this.containingType = containingType;
        this.allowFKIntegrityOverride = allowFKIntegrityOverride;
        isFKIntegrity = FKIntegrity;
        this.referencedType = referencedType;
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

    public String getForeignIdType() {
        return referencedField.getType();
    }

    public TypeMetadata getReferencedType() {
        return referencedType;
    }

    public String getType() {
        return referencedField.getType();
    }
}