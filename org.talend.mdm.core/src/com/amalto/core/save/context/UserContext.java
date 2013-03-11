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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.AbstractDocumentSaverContext;
import com.amalto.core.save.UserAction;

class UserContext extends AbstractDocumentSaverContext {

    private final List<Action> actions = new LinkedList<Action>();

    private final String dataCluster;

    private final String dataModel;

    private final boolean validate;

    private final boolean invokeBeforeSaving;

    private final boolean updateReport;

    private UserAction userAction;

    private ComplexTypeMetadata type;

    private String revisionId = null;

    private String[] id = new String[0];

    private MutableDocument userDocument;

    private MutableDocument dataBaseDocument;

    private MutableDocument dataBaseValidationDocument;

    private boolean hasMetAutoIncrement;

    private String taskId = StringUtils.EMPTY;

    UserContext(String dataCluster, String dataModel, MutableDocument userDocument, UserAction userAction, boolean validate,
            boolean updateReport, boolean invokeBeforeSaving) {
        this.userDocument = userDocument;
        this.dataCluster = dataCluster;
        this.dataModel = dataModel;
        this.userAction = userAction;
        this.validate = validate;
        this.invokeBeforeSaving = invokeBeforeSaving;
        this.updateReport = updateReport;
    }

    @Override
    public DocumentSaver createSaver() {
        DocumentSaver saver = SaverContextFactory.invokeSaverExtension(new Save());
        if (validate) {
            saver = new Validation(saver);
        }
        if (invokeBeforeSaving) {
            saver = new BeforeSaving(saver);
        }
        saver = new ApplyActions(saver); // Apply actions is mandatory
        if (updateReport) {
            saver = new UpdateReport(saver);
        }
        return new Init(new ID(new GenerateActions(new Security(saver))));
    }

    @Override
    public MutableDocument getDatabaseDocument() {
        return dataBaseDocument;
    }

    @Override
    public MutableDocument getDatabaseValidationDocument() {
        return dataBaseValidationDocument;
    }

    @Override
    public MutableDocument getUserDocument() {
        return userDocument;
    }

    @Override
    public void setUserDocument(MutableDocument document) {
        userDocument = document;
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public void setActions(List<Action> actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    @Override
    public ComplexTypeMetadata getType() {
        return type;
    }

    @Override
    public String getDataCluster() {
        return dataCluster;
    }

    @Override
    public String getDataModelName() {
        return dataModel;
    }

    @Override
    public String getRevisionID() {
        return revisionId;
    }

    @Override
    public void setDatabaseDocument(MutableDocument databaseDocument) {
        this.dataBaseDocument = databaseDocument;
    }

    @Override
    public void setDatabaseValidationDocument(MutableDocument databaseValidationDocument) {
        this.dataBaseValidationDocument = databaseValidationDocument;
    }

    @Override
    public void setRevisionId(String revisionID) {
        this.revisionId = revisionID;
    }

    @Override
    public void setType(ComplexTypeMetadata type) {
        this.type = type;
    }

    @Override
    public boolean hasMetAutoIncrement() {
        return hasMetAutoIncrement;
    }

    @Override
    public void setHasMetAutoIncrement(boolean hasMetAutoIncrement) {
        this.hasMetAutoIncrement = hasMetAutoIncrement;
    }

    @Override
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public boolean preserveOldCollectionValues() {
        return false;
    }

    @Override
    public MutableDocument getUpdateReportDocument() {
        // See ReportDocumentSaverContext.decorate() if you wish this context to support update report creation.
        throw new UnsupportedOperationException("No supported in this implementation.");
    }

    @Override
    public void setUpdateReportDocument(MutableDocument updateReportDocument) {
        // See ReportDocumentSaverContext.decorate() if you wish this context to support update report creation.
        throw new UnsupportedOperationException("No supported in this implementation.");
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
        return StringUtils.EMPTY;
    }

    @Override
    public String getPartialUpdateKey() {
        return StringUtils.EMPTY;
    }

    @Override
    public String[] getId() {
        return id;
    }

    @Override
    public void setId(String[] id) {
        this.id = id;
    }
}
