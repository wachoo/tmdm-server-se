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

class SystemContext implements DocumentSaverContext {

    private final String dataCluster;

    private final String dataModelName;

    private final List<Action> actions = new LinkedList<Action>();

    private String revisionId;

    private String[] id = new String[0];

    private MutableDocument userDocument;

    private MutableDocument databaseDocument;

    private MutableDocument databaseValidationDocument;

    private ComplexTypeMetadata type;

    private boolean isCreate;

    public SystemContext(String dataCluster, String dataModelName, MutableDocument document) {
        this.dataCluster = dataCluster;
        this.dataModelName = dataModelName;
        this.userDocument = document;
    }

    public DocumentSaver createSaver() {
        DocumentSaver saver = SaverContextFactory.invokeSaverExtension(new Save());
        return new Init(new ID(new SystemActions(new ApplyActions(saver))));
    }

    public MutableDocument getDatabaseDocument() {
        return databaseDocument;
    }

    public MutableDocument getDatabaseValidationDocument() {
        return databaseValidationDocument;
    }

    public MutableDocument getUserDocument() {
        return userDocument;
    }

    public void setUserDocument(MutableDocument document) {
        this.userDocument = document;
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
        return dataModelName;
    }

    public String getRevisionID() {
        return revisionId;
    }

    public void setDatabaseDocument(MutableDocument databaseDocument) {
        this.databaseDocument = databaseDocument;
    }

    public void setDatabaseValidationDocument(MutableDocument databaseValidationDocument) {
        this.databaseValidationDocument = databaseValidationDocument;
    }

    public void setRevisionId(String revisionID) {
        this.revisionId = revisionID;
    }

    public void setType(ComplexTypeMetadata type) {
        this.type = type;
    }

    public boolean isReplace() {
        // System documents are always replaced.
        return true;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean isCreate) {
        this.isCreate = isCreate;
    }

    public boolean hasMetAutoIncrement() {
        return false;
    }

    public void setHasMetAutoIncrement(boolean hasMetAutoIncrement) {
    }

    public String[] getId() {
        return id;
    }

    public void setId(String[] id) {
        this.id = id;
    }

}
