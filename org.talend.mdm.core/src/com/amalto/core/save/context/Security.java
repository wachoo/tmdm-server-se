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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class Security implements DocumentSaver {

    private final DocumentSaver next;

    public Security(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        List<Action> actions = context.getActions();
        SaverSource saverSource = session.getSaverSource();
        Set<String> currentUserRoles = saverSource.getCurrentUserRoles();

        // admin has all rights, so bypass security checks
        boolean bypassSecurityChecks = "admin".equals(saverSource.getUserName()); //$NON-NLS-1$
        // administration has all roles, so bypass security checks
        if (currentUserRoles.contains("administration")) { //$NON-NLS-1$
            bypassSecurityChecks = true;
        }

        if (!bypassSecurityChecks) {
            // First check rights on the type
            List<String> typeWriteUsers = context.getType().getWriteUsers();
            boolean isAllowed = false;
            for (String currentUserRole : currentUserRoles) {
                if (typeWriteUsers.contains(currentUserRole)) {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed) {
                throw new RuntimeException("User '" + saverSource.getUserName() + "' is not allowed to write to type '" + context.getType().getName() + "'.");
            }

            // Then check security on all actions (updates...)
            Set<Action> failedActions = new HashSet<Action>();
            for (Action action : actions) {
                if (!action.isAllowed(currentUserRoles)) {
                    failedActions.add(action);
                }
            }
            if (!failedActions.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                Iterator<Action> iterator = failedActions.iterator();
                while (iterator.hasNext()) {
                    builder.append(iterator.next().getDetails());
                    if (iterator.hasNext()) {
                        builder.append(" / ");  //$NON-NLS-1$
                    }
                }
                throw new IllegalStateException("User '" + saverSource.getUserName() + "' is not allowed to perform following operation(s): " + builder);
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

    public String getBeforeSavingMessage() {
        return next.getBeforeSavingMessage();
    }
}
