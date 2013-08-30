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

import com.amalto.core.history.Action;

public class BooleanActions {

    public static Action preferTrue(FieldUpdateAction action) {
        String newValueAsString = action.getNewValue();
        String oldValueAsString = action.getOldValue();
        boolean newValue = newValueAsString.isEmpty() ? Boolean.TRUE : Boolean.parseBoolean(newValueAsString);
        boolean oldValue = oldValueAsString.isEmpty() ? Boolean.TRUE : Boolean.parseBoolean(oldValueAsString);
        if (newValue) {
            return action;
        } else if (oldValue) {
            return NoOpAction.instance();
        } else {
            return NoOpAction.instance();
        }
    }

    public static Action preferFalse(FieldUpdateAction action) {
        String newValueAsString = action.getNewValue();
        String oldValueAsString = action.getOldValue();
        boolean newValue = newValueAsString.isEmpty() ? Boolean.FALSE : Boolean.parseBoolean(newValueAsString);
        boolean oldValue = oldValueAsString.isEmpty() ? Boolean.FALSE : Boolean.parseBoolean(oldValueAsString);
        if (!newValue) {
            return action;
        } else if (!oldValue) {
            return NoOpAction.instance();
        } else {
            return NoOpAction.instance();
        }
    }
}
