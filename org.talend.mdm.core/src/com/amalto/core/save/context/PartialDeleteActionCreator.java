/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.save.context;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.action.PartialDeleteAction;

public class PartialDeleteActionCreator {

    private final Date date;

    private final String source;

    private final String userName;

    private final String toDeletePivot;

    private final String toDeleteKey;

    private final MutableDocument toDeleteDocument;

    public PartialDeleteActionCreator(Date date, String source, String userName, String pivot, String key,
            MutableDocument document) {
        this.date = date;
        this.source = source;
        this.userName = userName;
        if (pivot.endsWith("/")) { //$NON-NLS-1$
            this.toDeletePivot = pivot.substring(0, pivot.length() - 1);
        } else {
            this.toDeletePivot = pivot;
        }
        if (!".".equals(key) && !StringUtils.isEmpty(key)) { //$NON-NLS-1$
            this.toDeleteKey = key.startsWith("/") ? key : "/" + key; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            this.toDeleteKey = StringUtils.EMPTY;
        }
        this.toDeleteDocument = document;
    }

    public List<Action> create() {
        List<Action> actions = new LinkedList<Action>();
        Accessor pivotAccessor = toDeleteDocument.createAccessor(toDeletePivot);
        for (int i = 1; i <= pivotAccessor.size(); i++) {
            String path = toDeletePivot + '[' + i + ']';
            Accessor keyAccessor = toDeleteDocument.createAccessor(path + toDeleteKey);
            String keyValue = keyAccessor.get();
            if (keyValue != null) {
                actions.add(new PartialDeleteAction(date, source, userName, toDeletePivot, toDeleteKey, keyValue));
            }
        }
        return actions;
    }
}
