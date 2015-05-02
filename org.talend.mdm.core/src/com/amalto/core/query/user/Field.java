/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.query.user;

import java.util.Collections;
import java.util.List;

import org.talend.mdm.commmon.metadata.CompoundFieldMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.TypeMetadata;

import com.amalto.core.metadata.MetadataUtils;

public class Field implements TypedExpression {

    private final FieldMetadata fieldMetadata;

    private List<FieldMetadata> path = Collections.emptyList();

    public Field(FieldMetadata fieldMetadata) {
        if (fieldMetadata == null) {
            throw new IllegalArgumentException("Field can not be null.");
        }
        this.fieldMetadata = fieldMetadata;
    }

    public FieldMetadata getFieldMetadata() {
        return fieldMetadata;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Expression normalize() {
        return this;
    }

    @Override
    public String getTypeName() {
        TypeMetadata type = MetadataUtils.getSuperConcreteType(fieldMetadata.getType());
        return type.getName();
    }

    public void setPath(List<FieldMetadata> path) {
        this.path = path;
    }

    public List<FieldMetadata> getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Field)) {
            return false;
        }
        Field field = (Field) o;
        return !(fieldMetadata != null ? !fieldMetadata.getName().equals(field.fieldMetadata.getName()) : field.fieldMetadata != null);
    }

    @Override
    public int hashCode() {
        if (fieldMetadata != null) {
            if (fieldMetadata instanceof CompoundFieldMetadata) {
                int hashCode = 0;
                FieldMetadata[] fields = ((CompoundFieldMetadata) fieldMetadata).getFields();
                for (FieldMetadata field : fields) {
                    hashCode = hashCode + field.getName().hashCode();
                }
                return hashCode;
            } else {
                return fieldMetadata.getName().hashCode();
            }
        } else {
            return 0;
        }
    }
}
