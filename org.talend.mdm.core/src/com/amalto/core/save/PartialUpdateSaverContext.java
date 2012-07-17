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

package com.amalto.core.save;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.context.DocumentSaver;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class PartialUpdateSaverContext implements DocumentSaverContext {

    private final DocumentSaverContext delegate;

    private final boolean overwrite;

    private UserAction userAction;

    private String pivot;

    private final String key;

    private PartialUpdateSaverContext(DocumentSaverContext delegate, String pivot, String key, boolean overwrite, UserAction userAction) {
        this.delegate = delegate;
        this.pivot = pivot;
        this.key = key;
        this.overwrite = overwrite;
        this.userAction = userAction;
    }

    public static DocumentSaverContext decorate(DocumentSaverContext context, String pivot, String key, boolean overwrite) {
        if (pivot == null) {
            throw new IllegalArgumentException("Pivot argument can not be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Key argument can not be null.");
        }
        if (pivot.length() > 1) {
            return new PartialUpdateSaverContext(context, pivot, key, overwrite, UserAction.PARTIAL_UPDATE);
        } else {
            return new PartialUpdateSaverContext(context, pivot, key, overwrite, UserAction.UPDATE);
        }
    }

    public DocumentSaver createSaver() {
        return delegate.createSaver();
    }

    public MutableDocument getDatabaseDocument() {
        return delegate.getDatabaseDocument();
    }

    public MutableDocument getDatabaseValidationDocument() {
        return delegate.getDatabaseValidationDocument();
    }

    public MutableDocument getUserDocument() {
        return delegate.getUserDocument();
    }

    public void setUserDocument(MutableDocument document) {
        delegate.setUserDocument(document);
    }

    public List<Action> getActions() {
        return delegate.getActions();
    }

    public void setActions(List<Action> actions) {
        delegate.setActions(actions);
    }

    public ComplexTypeMetadata getType() {
        return delegate.getType();
    }

    public String getDataCluster() {
        return delegate.getDataCluster();
    }

    public String getDataModelName() {
        return delegate.getDataModelName();
    }

    public String getRevisionID() {
        return delegate.getRevisionID();
    }

    public void setDatabaseDocument(MutableDocument databaseDocument) {
        String localName = databaseDocument.asDOM().getDocumentElement().getLocalName();
        pivot = StringUtils.substringAfter(pivot, localName + '/');
        delegate.setDatabaseDocument(databaseDocument);
    }

    public void setDatabaseValidationDocument(MutableDocument databaseValidationDocument) {
        delegate.setDatabaseValidationDocument(databaseValidationDocument);
    }

    public void setRevisionId(String revisionID) {
        delegate.setRevisionId(revisionID);
    }

    public void setType(ComplexTypeMetadata type) {
        delegate.setType(type);
    }

    public boolean hasMetAutoIncrement() {
        return delegate.hasMetAutoIncrement();
    }

    public void setHasMetAutoIncrement(boolean hasMetAutoIncrement) {
        delegate.setHasMetAutoIncrement(hasMetAutoIncrement);
    }

    public boolean preserveOldCollectionValues() {
        return !overwrite;
    }

    public MutableDocument getUpdateReportDocument() {
        return delegate.getUpdateReportDocument();
    }

    public void setUpdateReportDocument(MutableDocument updateReportDocument) {
        delegate.setUpdateReportDocument(updateReportDocument);
    }

    @Override
    public UserAction getUserAction() {
        return userAction;
    }

    @Override
    public void setUserAction(UserAction userAction) {
        this.userAction = userAction;
    }

    @Override
    public String getPartialUpdatePivot() {
        return pivot;
    }

    @Override
    public String getPartialUpdateKey() {
        return key;
    }

    public String[] getId() {
        return delegate.getId();
    }

    public void setId(String[] id) {
        delegate.setId(id);
    }
}
