/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history.action;

import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import java.util.Date;

/**
 * Action performed on a field with maxOccurs = 1.
 */
public class FieldInsertAction extends FieldUpdateAction {

    public FieldInsertAction(Date date, String source, String userName, String field, String oldValue, String newValue, FieldMetadata updatedField) {
        super(date, source, userName, field, oldValue, newValue, updatedField);
    }

    public MutableDocument perform(MutableDocument document) {
        Accessor accessor = document.createAccessor(field);
        if (oldValue != null) {
            if (newValue == null) {
                accessor.delete();
            } else {
                 // Only difference with FieldUpdateAction
                accessor.insert();
                accessor.set(newValue);
            }
        } else {
            accessor.createAndSet(newValue);
        }
        return document;
    }

    public String getDetails() {
        return "insert field '" + updatedField.getName() + "' of type '" + updatedField.getContainingType().getName() + "'";
    }

    @Override
    public String toString() {
        return "FieldInsertAction{" +
                "path='" + field + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                '}';
    }
}
