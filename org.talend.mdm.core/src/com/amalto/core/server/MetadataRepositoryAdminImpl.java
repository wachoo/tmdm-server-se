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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.objects.view.ejb.local.ViewCtrlLocal;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import java.io.ByteArrayInputStream;
import java.util.*;

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

    public Set<FieldMetadata> getIndexedFields(String dataModelName) {
        synchronized (metadataRepository) {
            try {
                MetadataRepository repository = get(dataModelName);
                ViewCtrlLocal viewCtrlLocal = Util.getViewCtrlLocal();
                Set<FieldMetadata> indexedFields = new HashSet<FieldMetadata>();
                for (ComplexTypeMetadata userType : repository.getUserComplexTypes()) {
                    ViewPOJOPK pk = new ViewPOJOPK("Browse_items_" + userType.getName()); //$NON-NLS-1$
                    ViewPOJO view = viewCtrlLocal.existsView(pk);
                    if (view != null) {
                        ArrayList<String> searchableElements = view.getSearchableBusinessElements().getList();
                        for (String searchableElement : searchableElements) {
                            String fieldName = StringUtils.substringAfter(searchableElement, "/"); //$NON-NLS-1$
                            if (userType.hasField(fieldName)) {
                                indexedFields.add(userType.getField(fieldName));
                            }
                        }
                    }
                }
                return indexedFields;
            } catch (Exception e) {
                throw new RuntimeException("Could not get indexed fields.", e);
            }
        }
    }

    public boolean exist(String metadataRepositoryId) {
        assertMetadataRepositoryId(metadataRepositoryId);
        synchronized (metadataRepository) {
            try {
                DataModelPOJOPK pk = new DataModelPOJOPK(StringUtils.substringBeforeLast(metadataRepositoryId, "#"));
                return dataModelControl.existsDataModel(pk) != null;
            } catch (XtentisException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public MetadataRepository get(String metadataRepositoryId) {
        assertMetadataRepositoryId(metadataRepositoryId);
        synchronized (metadataRepository) {
            MetadataRepository repository = metadataRepository.get(metadataRepositoryId);
            if (repository == null) {
                try {
                    DataModelPOJOPK pk = new DataModelPOJOPK(StringUtils.substringBeforeLast(metadataRepositoryId, "#"));
                    DataModelPOJO dataModel;
                    try {
                        dataModel = dataModelControl.existsDataModel(pk);
                    } catch (XtentisException e) {
                        throw new RuntimeException(e);
                    }
                    if (dataModel == null) {
                        return null; // Expected per interface documentation (if not found, return null).
                    }
                    String schemaAsString = dataModel.getSchema();

                    repository = new MetadataRepository();
                    if (metadataRepositoryId.endsWith(StorageAdmin.STAGING_SUFFIX)) {  // Loads additional types for staging area.
                        repository.load(MetadataRepositoryAdminImpl.class.getResourceAsStream("stagingInternalTypes.xsd"));
                    }
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
            if (!metadataRepositoryId.endsWith(StorageAdmin.STAGING_SUFFIX)) {
                // Remove staging metadata repository.
                remove(metadataRepositoryId + StorageAdmin.STAGING_SUFFIX);
            }
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
