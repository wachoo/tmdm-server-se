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
import org.w3c.dom.Element;

class Save implements DocumentSaver {
    public void save(SaverSession session, DocumentSaverContext context) {
        DataClusterPOJOPK dataCluster = new DataClusterPOJOPK(context.getDataCluster());
        String typeName = context.getType().getName();
        String[] ids = context.getId();

        Element documentElement = context.getDatabaseDocument().asDOM().getDocumentElement();
        ItemPOJO item = new ItemPOJO(dataCluster, typeName, ids, System.currentTimeMillis(), documentElement);
        // Data model name is rather important! (used by FK integrity checks for instance).
        item.setDataModelName(context.getDataModelName());
        item.setDataModelRevision(context.getRevisionID()); // TODO Is data model revision ok?

        session.save(context.getDataCluster(), item);
    }

    public String[] getSavedId() {
        throw new NotImplementedException(); // TODO
    }

    public String getSavedConceptName() {
        throw new NotImplementedException(); // TODO
    }
}