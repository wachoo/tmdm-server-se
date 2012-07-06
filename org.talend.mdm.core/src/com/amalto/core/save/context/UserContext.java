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
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.DocumentSaverContext;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

class UserContext implements DocumentSaverContext {

    private final List<Action> actions = new LinkedList<Action>();

    private final String dataCluster;

    private final String dataModel;

    private final boolean validate;

    private final boolean invokeBeforeSaving;

    private final boolean updateReport;

    private final boolean isReplace;

    private ComplexTypeMetadata type;

    private String revisionId = null;

    private String[] id = new String[0];

    private MutableDocument userDocument;

    private MutableDocument dataBaseDocument;

    private MutableDocument dataBaseValidationDocument;

    private boolean isCreate;

    private boolean hasMetAutoIncrement;

    private String taskId = StringUtils.EMPTY;

    UserContext(String dataCluster, String dataModel, MutableDocument userDocument, boolean isReplace, boolean validate, boolean updateReport, boolean invokeBeforeSaving) {
        this.userDocument = userDocument;
        this.dataCluster = dataCluster;
        this.dataModel = dataModel;
        this.validate = validate;
        this.invokeBeforeSaving = invokeBeforeSaving;
        this.updateReport = updateReport;
        this.isReplace = isReplace;
    }

    public DocumentSaver createSaver() {
        DocumentSaver saver = SaverContextFactory.invokeSaverExtension(new Save());
        if (validate) {
            saver = new Validation(saver);
        }
        saver = new ApplyActions(saver); // Apply actions is mandatory
        if (invokeBeforeSaving) {
            saver = new BeforeSaving(saver);
        }
        if (updateReport) {
            saver = new UpdateReport(saver);
        }
        return new Init(new ID(new GenerateActions(new Security(saver))));
    }

    public MutableDocument getDatabaseDocument() {
        return dataBaseDocument;
    }

    public MutableDocument getDatabaseValidationDocument() {
        return dataBaseValidationDocument;
    }

    public MutableDocument getUserDocument() {
        return userDocument;
    }

    public void setUserDocument(MutableDocument document) {
        userDocument = document;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions.clear();
        this.actions.addAll(actions);
    }

    public ComplexTypeMetadata getType() {
        return type;
    }

    public String getDataCluster() {
        return dataCluster;
    }

    public String getDataModelName() {
        return dataModel;
    }

    public String getRevisionID() {
        return revisionId;
    }

    public void setDatabaseDocument(MutableDocument databaseDocument) {
        this.dataBaseDocument = databaseDocument;
    }

    public void setDatabaseValidationDocument(MutableDocument databaseValidationDocument) {
        this.dataBaseValidationDocument = databaseValidationDocument;
    }

    public void setRevisionId(String revisionID) {
        this.revisionId = revisionID;
    }

    public void setType(ComplexTypeMetadata type) {
        this.type = type;
    }

    public boolean isReplace() {
        return isReplace;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean isCreate) {
        this.isCreate = isCreate;
    }

    public boolean hasMetAutoIncrement() {
        return hasMetAutoIncrement;
    }

    public void setHasMetAutoIncrement(boolean hasMetAutoIncrement) {
        this.hasMetAutoIncrement = hasMetAutoIncrement;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean preserveOldCollectionValues() {
        return false;
    }

    public MutableDocument getUpdateReportDocument() {
        // See ReportDocumentSaverContext.decorate() if you wish this context to support update report creation.
        throw new UnsupportedOperationException("No supported in this implementation.");
    }

    public void setUpdateReportDocument(MutableDocument updateReportDocument) {
        // See ReportDocumentSaverContext.decorate() if you wish this context to support update report creation.
        throw new UnsupportedOperationException("No supported in this implementation.");
    }

    public String[] getId() {
        return id;
    }

    public void setId(String[] id) {
        this.id = id;
    }
}
