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

import java.util.LinkedList;
import java.util.List;

class UserContext implements DocumentSaverContext {

    private static final boolean invokeValidation = true;

    private final List<Action> actions = new LinkedList<Action>();

    private final ComplexTypeMetadata type;

    private String revisionId = null;

    private final String dataCluster;

    private final String dataModel;

    private final SaverSource dataSource;

    private final boolean invokeBeforeSaving;

    private final boolean updateReport;

    private String[] id;

    private MutableDocument userDocument;

    private MutableDocument dataBaseDocument;

    private MutableDocument dataBaseValidationDocument;

    UserContext(String dataCluster, String dataModel, MutableDocument userDocument, ComplexTypeMetadata type, SaverSource dataSource, boolean updateReport, boolean invokeBeforeSaving) {
        this.userDocument = userDocument;
        this.type = type;
        this.dataCluster = dataCluster;
        this.dataModel = dataModel;
        this.dataSource = dataSource;
        this.invokeBeforeSaving = invokeBeforeSaving;
        this.updateReport = updateReport;
    }

    public DocumentSaver createSaver() {
        DocumentSaver saver = new Save();

        if (invokeValidation) {
            saver = new Validation(saver);
        }

        saver = new ApplyActions(saver);

        if (invokeBeforeSaving) {
            saver = new BeforeSaving(saver);
        }
        if (updateReport) {
            saver = new UpdateReport(saver);
        }

        return new Checks(new ID(new GenerateActions(new Security(saver))));
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

    public SaverSource getSaverSource() {
        return dataSource;
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

    public String[] getId() {
        return id;
    }

    public void setId(String[] id) {
        this.id = id;
    }
}
