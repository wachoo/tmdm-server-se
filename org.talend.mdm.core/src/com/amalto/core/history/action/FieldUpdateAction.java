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

package com.amalto.core.history.action;

import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;

import java.util.Date;

/**
 * Action performed on a field with maxOccurs = 1.
 */
public class FieldUpdateAction extends AbstractAction {

    private final String field;

    private final String oldValue;

    private final String newValue;

    public FieldUpdateAction(Date date, String source, String userName, String field, String oldValue, String newValue) {
        super(date, source, userName);
        this.field = field;
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
                    accessor.create();
                }
                accessor.set(newValue);
            }
        } else {
            accessor.create();
            accessor.set(newValue);
        }
        return document;
    }

    public MutableDocument undo(MutableDocument document) {
        Accessor accessor = document.createAccessor(field);
        if (oldValue != null) {
            if (newValue == null) {
                accessor.create();
                accessor.set(oldValue);
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
        accessor.markModified();
        return document;
    }

    public MutableDocument removeModificationMark(MutableDocument document) {
        Accessor accessor = document.createAccessor(field);
        accessor.markUnmodified();
        return document;
    }
}
