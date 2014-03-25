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

package com.amalto.core.save.context;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.ReportDocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class SystemActions implements DocumentSaver {

    private final DocumentSaver next;

    SystemActions(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        MutableDocument userDocument = context.getUserDocument();
        // Get source of modification (only if we're in the context of an update report).
        String source;
        if (context instanceof ReportDocumentSaverContext) {
            source = ((ReportDocumentSaverContext) context).getChangeSource();
        } else {
            source = StringUtils.EMPTY;
        }
        Date date = new Date(System.currentTimeMillis());
        String userName = session.getSaverSource().getUserName();

        // Consider all system documents as creation (no need to update field by field).
        List<Action> actions = Collections.<Action>singletonList(new OverrideCreateAction(date, source, userName, userDocument));
        context.setActions(actions);

        // Get ID from the system document
        Collection<FieldMetadata> keyFields = context.getUserDocument().getType().getKeyFields();
        String[] ids = new String[keyFields.size()];
        int i = 0;
        for (FieldMetadata keyField : keyFields) {
            Accessor accessor = context.getUserDocument().createAccessor(keyField.getName());
            ids[i++] = accessor.get();
        }
        context.setId(ids);

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
