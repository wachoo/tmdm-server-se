// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.server;

import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class MetadataRepositoryAdminImpl implements MetadataRepositoryAdmin {

    private final Map<String, MetadataRepository> metadataRepository = new HashMap<String, MetadataRepository>();

    private final DataModelCtrlLocal dataModelControl;

    MetadataRepositoryAdminImpl() {
        try {
            dataModelControl = Util.getDataModelCtrlLocal();
            Collection<DataModelPOJOPK> allDataModelNames = dataModelControl.getDataModelPKs(".*"); //$NON-NLS-1$

            Map<String, XSystemObjects> xDataClustersMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
            for (DataModelPOJOPK dataModelName : allDataModelNames) {
                if (!xDataClustersMap.containsKey(dataModelName.getUniqueId())) {
                    get(dataModelName.getUniqueId());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        synchronized (metadataRepository) {
            for (String repositoryId : metadataRepository.keySet()) {
                remove(repositoryId);
            }
        }
    }

    public boolean exist(String metadataRepositoryId) {
        assertMetadataRepositoryId(metadataRepositoryId);
        synchronized (metadataRepository) {
            try {
                DataModelPOJOPK pk = new DataModelPOJOPK(metadataRepositoryId);
                return dataModelControl.existsDataModel(pk) != null;
            } catch (XtentisException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public MetadataRepository get(String metadataRepositoryId) {
        if (metadataRepositoryId.endsWith(StorageAdmin.STAGING_PREFIX)) {
            metadataRepositoryId = StringUtils.substringBeforeLast(metadataRepositoryId, "#");
        }

        assertMetadataRepositoryId(metadataRepositoryId);
        synchronized (metadataRepository) {
            MetadataRepository repository = metadataRepository.get(metadataRepositoryId);

            if (repository == null) {
                try {
                    DataModelPOJOPK pk = new DataModelPOJOPK(metadataRepositoryId);
                    DataModelPOJO dataModel;
                    try {
                        dataModel = dataModelControl.getDataModel(pk);
                    } catch (XtentisException e) {
                        throw new RuntimeException(e);
                    }
                    String schemaAsString = dataModel.getSchema();

                    repository = new MetadataRepository();
                    repository.load(new ByteArrayInputStream(schemaAsString.getBytes("UTF-8")));
                    metadataRepository.put(metadataRepositoryId, repository);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return repository;
        }
    }

    public void remove(String metadataRepositoryId) {
        assertMetadataRepositoryId(metadataRepositoryId);
        synchronized (metadataRepository) {
            MetadataRepository repository = get(metadataRepositoryId);
            repository.close();
            metadataRepository.remove(metadataRepositoryId);
        }
    }

    public void update(String metadataRepositoryId) {
        assertMetadataRepositoryId(metadataRepositoryId);
        synchronized (metadataRepository) {
            remove(metadataRepositoryId);
            get(metadataRepositoryId);
        }
    }

    private static void assertMetadataRepositoryId(String metadataRepositoryId) {
        if (metadataRepositoryId == null) {
            throw new IllegalArgumentException("Metadata repository id cannot be null");
        }
    }
}
