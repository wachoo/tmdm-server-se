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
import org.apache.commons.lang.NotImplementedException;

import java.math.BigDecimal;

public class NumberActions {

    public static Action max(FieldUpdateAction action) {
        BigDecimal newValue = new BigDecimal(action.getNewValue());
        BigDecimal oldValue = new BigDecimal(action.getOldValue());
        if (newValue.longValue() > oldValue.longValue()) {
            return action;
        } else {
            return NoOpAction.instance();
        }
    }

    public static Action min(FieldUpdateAction action) {
        BigDecimal newValue = new BigDecimal(action.getNewValue());
        BigDecimal oldValue = new BigDecimal(action.getOldValue());
        if (newValue.longValue() < oldValue.longValue()) {
            return action;
        } else {
            return NoOpAction.instance();
        }
    }

    public static Action mean(FieldUpdateAction action) {
        throw new NotImplementedException();
    }

    public static Action sum(FieldUpdateAction action) {
        throw new NotImplementedException();
    }
}
