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

import com.amalto.core.history.FieldAction;

public class ConcatAction extends FieldUpdateAction {

    private ConcatAction(FieldUpdateAction delegate) {
        super(delegate.date,
                delegate.source,
                delegate.userName,
                delegate.path,
                delegate.oldValue,
                delegate.newValue,
                delegate.updatedField);
    }

    @Override
    protected String getNewValue() {
        return super.getOldValue() + super.getNewValue();
    }

    public static FieldAction concat(FieldUpdateAction action) {
        return new ConcatAction(action);
    }
}
