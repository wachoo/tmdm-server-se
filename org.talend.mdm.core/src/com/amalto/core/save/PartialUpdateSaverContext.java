/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.context.DocumentSaver;

public class PartialUpdateSaverContext extends AbstractDocumentSaverContext {

    private final DocumentSaverContext delegate;

    private final boolean overwrite;

    private UserAction userAction;

    private String pivot;

    private final String key;

    private PartialUpdateSaverContext(DocumentSaverContext delegate, String pivot, String key, boolean overwrite,
                                      UserAction userAction) {
        this.delegate = delegate;
        this.pivot = pivot;
        this.key = key;
        this.overwrite = overwrite;
        this.userAction = userAction;
    }

    public static DocumentSaverContext decorate(DocumentSaverContext context, String pivot, String key, boolean overwrite) {
        if (pivot == null) {
            pivot = StringUtils.EMPTY;
        }
        if (key == null) {
            key = StringUtils.EMPTY;
        }
        if (pivot.length() > 1) {
            return new PartialUpdateSaverContext(context, pivot, key, overwrite, UserAction.PARTIAL_UPDATE);
        } else {
            return new PartialUpdateSaverContext(context, pivot, key, overwrite, UserAction.UPDATE);
        }
    }

    @Override
    public DocumentSaver createSaver() {
        return delegate.createSaver();
    }

    @Override
    public MutableDocument getDatabaseDocument() {
        return delegate.getDatabaseDocument();
    }

    @Override
    public MutableDocument getDatabaseValidationDocument() {
        return delegate.getDatabaseValidationDocument();
    }

    @Override
    public MutableDocument getUserDocument() {
        return delegate.getUserDocument();
    }

    @Override
    public void setUserDocument(MutableDocument document) {
        delegate.setUserDocument(document);
    }

    @Override
    public List<Action> getActions() {
        return delegate.getActions();
    }

    @Override
    public void setActions(List<Action> actions) {
        delegate.setActions(actions);
    }

    @Override
    public ComplexTypeMetadata getType() {
        return delegate.getType();
    }

    @Override
    public String getDataCluster() {
        return delegate.getDataCluster();
    }

    @Override
    public String getDataModelName() {
        return delegate.getDataModelName();
    }

    @Override
    public String getRevisionID() {
        return delegate.getRevisionID();
    }

    @Override
    public void setDatabaseDocument(MutableDocument databaseDocument) {
        String localName = databaseDocument.asDOM().getDocumentElement().getLocalName();
        pivot = StringUtils.substringAfter(pivot, localName + '/');
        delegate.setDatabaseDocument(databaseDocument);
    }

    @Override
    public void setDatabaseValidationDocument(MutableDocument databaseValidationDocument) {
        delegate.setDatabaseValidationDocument(databaseValidationDocument);
    }

    @Override
    public void setRevisionId(String revisionID) {
        delegate.setRevisionId(revisionID);
    }

    @Override
    public void setType(ComplexTypeMetadata type) {
        delegate.setType(type);
    }

    @Override
    public boolean hasMetAutoIncrement() {
        return delegate.hasMetAutoIncrement();
    }

    @Override
    public void setHasMetAutoIncrement(boolean hasMetAutoIncrement) {
        delegate.setHasMetAutoIncrement(hasMetAutoIncrement);
    }

    @Override
    public boolean preserveOldCollectionValues() {
        return !overwrite;
    }

    @Override
    public MutableDocument getUpdateReportDocument() {
        return delegate.getUpdateReportDocument();
    }

    @Override
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

    @Override
    public String[] getId() {
        return delegate.getId();
    }

    @Override
    public void setId(String[] id) {
        delegate.setId(id);
    }
}
