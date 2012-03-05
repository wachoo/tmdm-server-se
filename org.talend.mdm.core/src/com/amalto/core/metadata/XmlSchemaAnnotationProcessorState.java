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

import java.util.LinkedList;
import java.util.List;

class XmlSchemaAnnotationProcessorState {
    private boolean fkIntegrity = true; // Default is to enforce FK integrity
    private boolean fkIntegrityOverride = false; // Default is to disable FK integrity check
    private FieldMetadata foreignKeyInfo = null;
    private TypeMetadata fieldType;
    private boolean isReference;
    private TypeMetadata referencedType;
    private FieldMetadata referencedField;

    private final List<String> hide = new LinkedList<String>();
    private final List<String> allowWrite = new LinkedList<String>();

    public void setFkIntegrity(boolean fkIntegrity) {
        this.fkIntegrity = fkIntegrity;
    }

    public void setFkIntegrityOverride(boolean fkIntegrityOverride) {
        this.fkIntegrityOverride = fkIntegrityOverride;
    }

    public void setForeignKeyInfo(FieldMetadata foreignKeyInfo) {
        this.foreignKeyInfo = foreignKeyInfo;
    }

    public void setFieldType(TypeMetadata fieldType) {
        this.fieldType = fieldType;
    }

    public void setReference(boolean reference) {
        isReference = reference;
    }

    public void setReferencedType(TypeMetadata referencedType) {
        this.referencedType = referencedType;
    }

    public void setReferencedField(FieldMetadata referencedField) {
        this.referencedField = referencedField;
    }

    public boolean isFkIntegrity() {
        return fkIntegrity;
    }

    public boolean isFkIntegrityOverride() {
        return fkIntegrityOverride;
    }

    public FieldMetadata getForeignKeyInfo() {
        return foreignKeyInfo;
    }

    public TypeMetadata getFieldType() {
        return fieldType;
    }

    public boolean isReference() {
        return isReference;
    }

    public TypeMetadata getReferencedType() {
        return referencedType;
    }

    public FieldMetadata getReferencedField() {
        return referencedField;
    }

    public List<String> getHide() {
        return hide;
    }

    public List<String> getAllowWrite() {
        return allowWrite;
    }
}
