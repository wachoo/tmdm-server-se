/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import com.amalto.core.save.DocumentSaverContext;
import org.apache.commons.lang.StringUtils;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.save.UserAction;

class SystemContext implements DocumentSaverContext {

    private final String dataCluster;

    private final String dataModelName;

    private final List<Action> actions = new LinkedList<Action>();

    private String revisionId;

    private String[] id = new String[0];

    private MutableDocument userDocument;

    private MutableDocument databaseDocument;

    private UserAction userAction;

    public SystemContext(String dataCluster, String dataModelName, MutableDocument document, UserAction userAction) {
        this.dataCluster = dataCluster;
        this.dataModelName = dataModelName;
        this.userDocument = document;
        this.userAction = userAction;
    }

    @Override
    public String getChangeSource() {
        return StringUtils.EMPTY;
    }

    @Override
    public DocumentSaver createSaver() {
        DocumentSaver saver = SaverContextFactory.invokeSaverExtension(new Save());
        return new Init(new ID(new GenerateActions(new ApplyActions(saver))));
    }

    @Override
    public MutableDocument getDatabaseDocument() {
        return databaseDocument;
    }

    @Override
    public MutableDocument getUserDocument() {
        return userDocument;
    }

    @Override
    public void setUserDocument(MutableDocument document) {
        this.userDocument = document;
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
    public String getDataCluster() {
        return dataCluster;
    }

    @Override
    public String getDataModelName() {
        return dataModelName;
    }

    @Override
    public String getRevisionID() {
        return revisionId;
    }

    @Override
    public void setDatabaseDocument(MutableDocument databaseDocument) {
        this.databaseDocument = databaseDocument;
    }

    @Override
    public void setRevisionId(String revisionID) {
        this.revisionId = revisionID;
    }

    @Override
    public void setTaskId(String taskId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTaskId() {
        return null;
    }

    @Override
    public boolean preserveOldCollectionValues() {
        return false;
    }

    @Override
    public MutableDocument getUpdateReportDocument() {
        return null;
    }

    @Override
    public void setUpdateReportDocument(MutableDocument updateReportDocument) {
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
    public int getPartialUpdateIndex() {
        return -1;
    }

    @Override
    public boolean generateTouchActions() {
        return true;
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
