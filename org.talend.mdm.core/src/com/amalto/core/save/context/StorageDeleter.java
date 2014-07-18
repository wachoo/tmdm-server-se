package com.amalto.core.save.context;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.UserAction;
import com.amalto.core.storage.Storage;

import java.util.List;

public class StorageDeleter implements DocumentSaverContext {

    private final Storage storage;

    private final String[] ids;

    private final boolean overwrite;

    private final UserAction userAction;

    private final boolean invokeBeforeDeleting;

    public StorageDeleter(Storage storage, String[] ids, boolean overwrite, UserAction userAction, boolean invokeBeforeDeleting) {
        this.storage = storage;
        this.ids = ids;
        this.overwrite = overwrite;
        this.userAction = userAction;
        this.invokeBeforeDeleting = invokeBeforeDeleting;
    }

    @Override
    public String getChangeSource() {
        return null;
    }

    @Override
    public DocumentSaver createSaver() {
        return null;
    }

    @Override
    public MutableDocument getDatabaseDocument() {
        return null;
    }

    @Override
    public MutableDocument getUserDocument() {
        return null;
    }

    @Override
    public void setUserDocument(MutableDocument document) {

    }

    @Override
    public List<Action> getActions() {
        return null;
    }

    @Override
    public void setActions(List<Action> actions) {

    }

    @Override
    public String[] getId() {
        return new String[0];
    }

    @Override
    public void setId(String[] id) {

    }

    @Override
    public String getDataCluster() {
        return null;
    }

    @Override
    public String getDataModelName() {
        return null;
    }

    @Override
    public String getRevisionID() {
        return null;
    }

    @Override
    public void setDatabaseDocument(MutableDocument databaseDocument) {

    }

    @Override
    public void setRevisionId(String revisionID) {

    }

    @Override
    public void setTaskId(String taskId) {

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

    }

    @Override
    public UserAction getUserAction() {
        return null;
    }

    @Override
    public void setUserAction(UserAction userAction) {

    }

    @Override
    public String getPartialUpdatePivot() {
        return null;
    }

    @Override
    public String getPartialUpdateKey() {
        return null;
    }

    @Override
    public int getPartialUpdateIndex() {
        return 0;
    }

    @Override
    public boolean generateTouchActions() {
        return false;
    }
}
