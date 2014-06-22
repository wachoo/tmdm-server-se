/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.load.action.LoadAction;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.AbstractDocumentSaverContext;
import com.amalto.core.save.UserAction;
import com.amalto.core.util.XSDKey;

class BulkLoadContext extends AbstractDocumentSaverContext {

    private final String dataCluster;

    private final String dataModelName;

    private final BulkLoadSaver bulkLoadSaver;

    public BulkLoadContext(String dataCluster, String dataModelName, XSDKey keyMetadata, InputStream documentStream,
            LoadAction loadAction, XmlServerSLWrapperLocal server) {
        this.dataCluster = dataCluster;
        this.dataModelName = dataModelName;
        bulkLoadSaver = new BulkLoadSaver(loadAction, documentStream, keyMetadata, server);
    }

    @Override
    public DocumentSaver createSaver() {
        return bulkLoadSaver;
    }

    @Override
    public MutableDocument getDatabaseDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableDocument getDatabaseValidationDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableDocument getUserDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserDocument(MutableDocument document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Action> getActions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setActions(List<Action> actions) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComplexTypeMetadata getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId(String[] id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDataCluster() {
        return dataCluster;
    }

    @Override
    public String getDataModelName() {
        return dataModelName;
    }

    @Override
    public String getRevisionID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDatabaseDocument(MutableDocument databaseDocument) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDatabaseValidationDocument(MutableDocument databaseValidationDocument) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRevisionId(String revisionID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setType(ComplexTypeMetadata type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMetAutoIncrement() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHasMetAutoIncrement(boolean hasMetAutoIncrement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTaskId(String taskId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTaskId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean preserveOldCollectionValues() {
        return false;
    }

    @Override
    public MutableDocument getUpdateReportDocument() {
        throw new UnsupportedOperationException("No supported in this implementation.");
    }

    @Override
    public void setUpdateReportDocument(MutableDocument updateReportDocument) {
        throw new UnsupportedOperationException("No supported in this implementation.");
    }

    @Override
    public UserAction getUserAction() {
        return UserAction.REPLACE;
    }

    @Override
    public void setUserAction(UserAction userAction) {
        // Only REPLACE for this context.
    }

    @Override
    public String getPartialUpdatePivot() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getPartialUpdateKey() {
        return StringUtils.EMPTY;
    }
}
