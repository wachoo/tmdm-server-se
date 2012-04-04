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

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.save.context.DefaultSaverSource;
import com.amalto.core.save.context.SaverContextFactory;
import com.amalto.core.save.context.SaverSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SaverSession {

    private static final Map<String, SaverSource> saverSourcePerUser = new HashMap<String, SaverSource>();

    private final SaverContextFactory contextFactory;

    private final Map<String, Set<ItemPOJO>> itemsPerDataCluster = new HashMap<String, Set<ItemPOJO>>();

    private final SaverSource dataSource;

    private static SaverSource defaultSaverSource;

    private SaverSession(SaverSource dataSource) {
        this.dataSource = dataSource;
        contextFactory = new SaverContextFactory();
    }

    /**
     * @param saverSource A custom implementation of {@link SaverSource}.
     * @return A {@link SaverSession} with a custom {@link SaverSource} implementation.
     */
    public static SaverSession newSession(SaverSource saverSource) {
        return new SaverSession(saverSource);
    }

    /**
     * @return A {@link SaverSession} with default user.
     */
    public static synchronized SaverSession newSession() {
        if (defaultSaverSource == null) {
            defaultSaverSource = new DefaultSaverSource();
        }
        return new SaverSession(defaultSaverSource);
    }

    /**
     * @param userName A MDM user name
     * @return A {@link SaverSession} with the user name passed as parameter.
     */
    public static SaverSession newUserSession(String userName) {
        SaverSource saverSource = saverSourcePerUser.get(userName);
        if (saverSource == null) {
            saverSource = new DefaultSaverSource(userName);
            saverSourcePerUser.put(userName, saverSource);
        }
        SaverSource dataSource = saverSource;
        return new SaverSession(dataSource);
    }

    public SaverContextFactory getContextFactory() {
        return contextFactory;
    }

    public void begin(String dataCluster) {
        begin(dataCluster, new DefaultCommitter());
    }
    
    public void begin(String dataCluster, Committer committer) {
        committer.begin(dataCluster);
    } 

    public void end() {
        end(new DefaultCommitter());
    }

    public void end(Committer committer) {
        for (Map.Entry<String, Set<ItemPOJO>> currentTransaction : itemsPerDataCluster.entrySet()) {
            String dataCluster = currentTransaction.getKey();
            begin(dataCluster, committer);
            for (ItemPOJO currentItemToCommit : currentTransaction.getValue()) {
                committer.save(currentItemToCommit, currentItemToCommit.getDataModelRevision()); // TODO Use data model revision for revision id?
            }
            committer.commit(dataCluster);
        }

        // If any change was made to data cluster "UpdateReport", route committed update reports.
        Set<ItemPOJO> updateReport = itemsPerDataCluster.get("UpdateReport"); //$NON-NLS-1$
        if (updateReport != null) {
            SaverSource saverSource = getSaverSource();
            for (ItemPOJO updateReportPOJO : updateReport) {
                saverSource.routeItem(updateReportPOJO.getDataClusterPOJOPK().getUniqueId(), updateReportPOJO.getConceptName(), updateReportPOJO.getItemIds());
            }
        }
    }

    public void save(String dataCluster, ItemPOJO itemToSave) {
        Set<ItemPOJO> itemsToSave = itemsPerDataCluster.get(dataCluster);
        if (itemsToSave == null) {
            itemsToSave = new HashSet<ItemPOJO>();
            itemsPerDataCluster.put(dataCluster, itemsToSave);
        }
        itemsToSave.add(itemToSave);
    }

    public SaverSource getSaverSource() {
        return dataSource;
    }

    public void abort() {
        abort(new DefaultCommitter());
    }

    public void abort(Committer committer) {
        for (Map.Entry<String, Set<ItemPOJO>> currentTransaction : itemsPerDataCluster.entrySet()) {
            String dataCluster = currentTransaction.getKey();
            committer.rollback(dataCluster);
        }
    }

    public interface Committer {

        void begin(String dataCluster);

        void commit(String dataCluster);

        void save(ItemPOJO item, String revisionId);

        void rollback(String dataCluster);
    }

}
