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

    private final String foreignKeyInfo;

    private final TypeMetadata containingType;

    private final boolean allowFKIntegrityOverride;

    private final boolean isFKIntegrity;

    public ReferenceFieldMetadata(String foreignKeyInfo, TypeMetadata containingType, boolean allowFKIntegrityOverride, boolean FKIntegrity) {
        this.foreignKeyInfo = foreignKeyInfo;
        this.containingType = containingType;
        this.allowFKIntegrityOverride = allowFKIntegrityOverride;
        isFKIntegrity = FKIntegrity;
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

    public boolean isFKIntegrity() {
        return isFKIntegrity;
    }

    public boolean allowFKIntegrityOverride() {
        return allowFKIntegrityOverride;
    }

    public abstract String getForeignIdType();
}