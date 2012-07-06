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

import java.util.List;

public class PartialUpdateSaverContext implements DocumentSaverContext {

    private final DocumentSaverContext delegate;

    private final boolean overwrite;

    private PartialUpdateSaverContext(DocumentSaverContext delegate, boolean overwrite) {
        this.delegate = delegate;
        this.overwrite = overwrite;
    }

    public static PartialUpdateSaverContext decorate(DocumentSaverContext context, boolean overwrite) {
        return new PartialUpdateSaverContext(context, overwrite);
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

    public boolean isReplace() {
        return delegate.isReplace();
    }

    public boolean isCreate() {
        return delegate.isCreate();
    }

    public void setCreate(boolean isCreate) {
        delegate.setCreate(isCreate);
    }

    public boolean hasMetAutoIncrement() {
        return delegate.hasMetAutoIncrement();
    }

    public void setHasMetAutoIncrement(boolean hasMetAutoIncrement) {
        delegate.setHasMetAutoIncrement(hasMetAutoIncrement);
    }

    public void setTaskId(String taskId) {
        delegate.setTaskId(taskId);
    }

    public String getTaskId() {
        return delegate.getTaskId();
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

    public String[] getId() {
        return delegate.getId();
    }

    public void setId(String[] id) {
        delegate.setId(id);
    }
}
