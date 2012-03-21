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

package com.amalto.core.save.context;

import com.amalto.core.history.Action;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;

import java.util.List;
import java.util.Set;

class Security implements DocumentSaver {

    private final DocumentSaver next;

    public Security(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        List<Action> actions = context.getActions();
        Set<String> currentUserRoles = session.getSaverSource().getCurrentUserRoles();
        for (Action action : actions) {
            if (!action.isAllowed(currentUserRoles)) {
                throw new IllegalArgumentException("User is not allowed to change data."); // TODO Details
            }
        }

        next.save(session, context);
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }
}
