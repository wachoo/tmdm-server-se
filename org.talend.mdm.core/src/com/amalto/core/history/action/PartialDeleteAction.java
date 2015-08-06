/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.history.action;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.save.DOMDocument;

public class PartialDeleteAction extends AbstractFieldAction {

    private static final Logger LOGGER = Logger.getLogger(PartialDeleteAction.class);

    private final String toDeletePivot;

    private final String toDeleteKey;

    private final String toDeleteValue;

    private int deletedIndex = 0;

    public PartialDeleteAction(Date date, String source, String userName, String pivot, String key, String value) {
        super(date, source, userName, null);
        this.toDeletePivot = pivot;
        this.toDeleteKey = key;
        this.toDeleteValue = value;
    }

    @Override
    public MutableDocument perform(MutableDocument document) {
        Accessor pivotAccessor = document.createAccessor(toDeletePivot);
        if (document instanceof DOMDocument) {// for update report
            pivotAccessor.delete();
        } else {
            for (int i = pivotAccessor.size(); i >= 1; i--) {
                String path = toDeletePivot + '[' + i + ']';
                Accessor keyAccessor = document.createAccessor(path + toDeleteKey);
                String keyValue = keyAccessor.get();
                if (toDeleteValue.equals(keyValue)) {
                    if (StringUtils.isEmpty(toDeleteKey)) {
                        keyAccessor.delete();
                    } else {
                        document.createAccessor(path).delete();
                    }
                    document.clean();
                    deletedIndex = i;
                    break;
                }
            }
            if (deletedIndex == 0) {
                LOGGER.warn("CAN'T perform partial delete with nonexistent value='" + toDeleteValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        return document;
    }

    @Override
    public MutableDocument undo(MutableDocument document) {
        if (!StringUtils.isEmpty(toDeleteKey)) {
            LOGGER.warn("CAN'T undo partial delete with nonempty key='" + toDeleteKey + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            Accessor pivotAccessor = document.createAccessor(toDeletePivot);
            if (document instanceof DOMDocument) {// for update report
                pivotAccessor.createAndSet(toDeleteValue);
            } else {
                if (deletedIndex > 0) {// successfully deleted
                    for (int i = pivotAccessor.size() + 1; i >= deletedIndex; i--) {
                        Accessor keyAccessor = document.createAccessor(toDeletePivot + '[' + i + ']' + toDeleteKey);
                        if (!keyAccessor.exist()) {
                            keyAccessor.create();
                        }
                        if (i == deletedIndex) {
                            keyAccessor.set(toDeleteValue);
                        } else {
                            Accessor keyAccessorPre = document.createAccessor(toDeletePivot + '[' + (i - 1) + ']' + toDeleteKey);
                            keyAccessor.set(keyAccessorPre.get());
                        }
                    }
                }
            }
        }
        return document;
    }

    @Override
    public MutableDocument addModificationMark(MutableDocument document) {
        return document;
    }

    @Override
    public MutableDocument removeModificationMark(MutableDocument document) {
        return document;
    }

    @Override
    public boolean isAllowed(Set<String> roles) {
        boolean isAllowed = false;
        List<String> allowedUserRoles = getField().getWriteUsers();
        for (String role : roles) {
            if (allowedUserRoles.contains(role)) {
                isAllowed = true;
                break;
            }
        }
        return isAllowed;
    }

    @Override
    public String getDetails() {
        return "partial delete '" + toDeletePivot + toDeleteKey + "' with value '" + toDeleteValue + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
    }

    @Override
    public String toString() {
        return "PartialDeleteAction{" +  //$NON-NLS-1$
                "pivot='" + toDeletePivot + "'" +  //$NON-NLS-1$ //$NON-NLS-2$
                ", key='" + toDeleteKey + "'"  //$NON-NLS-1$ //$NON-NLS-2$
                + ", value='" + toDeleteValue +  //$NON-NLS-1$
                "'}";  //$NON-NLS-1$
    }

}
