package com.amalto.core.history;

/**
 *
 */
public interface DocumentHistory {
    DocumentHistoryNavigator getHistory(String dataClusterName, String dataModelName, String conceptName, String[] id, String revisionId);
}
