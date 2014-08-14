/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.amalto.core.history.DeleteType;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.history.Document;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.server.XmlServer;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class DefaultCommitter implements SaverSession.Committer {

    private final XmlServer xmlServerCtrlLocal;

    public DefaultCommitter() {
        try {
            xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public void begin(String dataCluster) {
        try {
            if (xmlServerCtrlLocal.supportTransaction()) {
                xmlServerCtrlLocal.start(dataCluster);
            }
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public void commit(String dataCluster) {
        try {
            if (xmlServerCtrlLocal.supportTransaction()) {
                xmlServerCtrlLocal.commit(dataCluster);
            }
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback(String dataCluster) {
        try {
            if (xmlServerCtrlLocal.supportTransaction()) {
                xmlServerCtrlLocal.rollback(dataCluster);
            }
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Document document) {
        try {
            ComplexTypeMetadata type = document.getType();
            boolean putInCache = type.getSuperTypes().isEmpty() && type.getSubTypes().isEmpty();
            Collection<FieldMetadata> keyFields = type.getKeyFields();
            List<String> ids = new LinkedList<String>();
            for (FieldMetadata keyField : keyFields) {
                String keyFieldName = keyField.getName();
                Accessor keyAccessor = ((MutableDocument)document).createAccessor(keyFieldName);
                if (!keyAccessor.exist()) {
                    throw new RuntimeException("Unexpected state: '"+ type +"' does not have value for key '" + keyFieldName + "'.");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
                }
                ids.add(keyAccessor.get());
            }
            ItemPOJO item = new ItemPOJO(new DataClusterPOJOPK(document.getDataCluster()),
                    type.getName(),
                    ids.toArray(new String[ids.size()]), // it need to set ids value
                    System.currentTimeMillis(),
                    document.exportToString());
            item.setTaskId(document.getTaskId());
            item.setDataModelName(document.getDataModel()); // it need to set dataModelName
            item.store(document.getRevision(), putInCache);
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Document item, DeleteType type) {

    }
}
