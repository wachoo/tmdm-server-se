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
import com.amalto.core.save.context.SaverContextFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SaverSession {

    private final SaverContextFactory contextFactory;

    private final Map<String, Set<ItemPOJO>> itemsPerDataCluster = new HashMap<String, Set<ItemPOJO>>();

    private SaverSession() {
        contextFactory = new SaverContextFactory();
    }

    public static SaverSession newSession() {
        return new SaverSession();
    }

    public SaverContextFactory getContextFactory() {
        return contextFactory;
    }

    public void end() {
        end(new DefaultCommitter());
    }

    public void end(Committer committer) {
        for (Map.Entry<String, Set<ItemPOJO>> currentTransaction : itemsPerDataCluster.entrySet()) {
            String dataCluster = currentTransaction.getKey();
            committer.begin(dataCluster);
            for (ItemPOJO currentItemToCommit : currentTransaction.getValue()) {
                committer.save(currentItemToCommit, currentItemToCommit.getDataModelRevision()); // TODO Use data model revision for revision id?
            }
            committer.commit(dataCluster);
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

    public interface Committer {

        void begin(String dataCluster);

        void commit(String dataCluster);

        void save(ItemPOJO item, String revisionId);
    }

}
