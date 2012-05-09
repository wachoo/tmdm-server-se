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

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

class Save implements DocumentSaver {

    private String[] savedId = new String[0];

    public void save(SaverSession session, DocumentSaverContext context) {
        DataClusterPOJOPK dataCluster = new DataClusterPOJOPK(context.getDataCluster());
        String typeName = context.getType().getName();
        savedId = context.getId();
        if (savedId.length == 0) {
            throw new IllegalStateException("No ID information to save instance of '" + typeName + "'");
        }
        Element documentElement = context.getDatabaseDocument().asDOM().getDocumentElement();
        ItemPOJO item = new ItemPOJO(dataCluster, typeName, savedId, System.currentTimeMillis(), documentElement);
        // Data model name is rather important! (used by FK integrity checks for instance).
        item.setDataModelName(context.getDataModelName());
        item.setDataModelRevision(context.getRevisionID()); // TODO Is data model revision ok?
        session.save(context.getDataCluster(), item, context.hasMetAutoIncrement());
    }

    public String[] getSavedId() {
        return savedId;
    }

    public String getSavedConceptName() {
        throw new UnsupportedOperationException();
    }

    public String getBeforeSavingMessage() {
        return StringUtils.EMPTY;
    }
}