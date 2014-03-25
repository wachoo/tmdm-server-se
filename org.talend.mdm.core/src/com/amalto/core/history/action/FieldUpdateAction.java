/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import java.util.List;
import java.util.Set;

/**
 * Action performed on a field with maxOccurs = 1.
 */
public class FieldUpdateAction extends AbstractAction {
    
    public static final String MODIFY_ADD_MARKER_VALUE = "tree-node-add"; //$NON-NLS-1$
    
    public static final String MODIFY_UPDATE_MARKER_VALUE = "tree-node-update"; //$NON-NLS-1$
    
    public static final String MODIFY_REMOVE_MARKER_VALUE = "tree-node-remove"; //$NON-NLS-1$

    protected final String field;

    protected final String oldValue;

    protected final String newValue;

    protected final FieldMetadata updatedField;

    public FieldUpdateAction(Date date, String source, String userName, String field, String oldValue, String newValue, FieldMetadata updatedField) {
        super(date, source, userName);
        this.field = field;
        this.updatedField = updatedField;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public MutableDocument perform(MutableDocument document) {
        Accessor accessor = document.createAccessor(field);
        if (oldValue != null) {
            if (newValue == null) {
                accessor.delete();
            } else {
                if (!accessor.exist()) {
                    accessor.createAndSet(newValue);
                } else {
                    accessor.set(newValue);
                }
            }
        } else {
            accessor.createAndSet(newValue);
        }
        return document;
    }

    public MutableDocument undo(MutableDocument document) {
        Accessor accessor = document.createAccessor(field);
        if (oldValue != null) {
            if (newValue == null) {
                accessor.createAndSet(oldValue);
            } else {
                accessor.set(oldValue);
            }
        } else { // old value == null
            accessor.delete();
        }
        return document;
    }

    public MutableDocument addModificationMark(MutableDocument document) {
        Accessor accessor = document.createAccessor(field);
        if (accessor.exist()) {
            if (oldValue != null) {
                if (newValue == null) {
                    accessor.markModified(Accessor.Marker.REMOVE);
                } else {
                    accessor.markModified(Accessor.Marker.UPDATE);
                }
            } else {
                if (newValue != null) {
                    accessor.markModified(Accessor.Marker.ADD);
                }
            }
        }
        return document;
    }

    public MutableDocument removeModificationMark(MutableDocument document) {
        Accessor accessor = document.createAccessor(field);
        accessor.markUnmodified();
        return document;
    }

    public boolean isAllowed(Set<String> roles) {
        boolean isAllowed = false;
        List<String> allowedUserRoles = updatedField.getWriteUsers();
        for (String role : roles) {
            if (allowedUserRoles.contains(role)) {
                isAllowed = true;
                break;
            }
        }
        return isAllowed;
    }

    public String getDetails() {
        return "update field '" + updatedField.getName() + "' of type '" + updatedField.getContainingType().getName() + "'";
    }

    @Override
    public String toString() {
        return "FieldUpdateAction{" +
                "path='" + field + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                '}';
    }
}
