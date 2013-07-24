/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.schema.validation.XmlSchemaValidator;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.servlet.LoadServlet;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.util.*;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

public class StorageSaverSource implements SaverSource {

    private final DataModelCtrlLocal dataModel;

    private final String userName;

    private final Map<String, String> schemasAsString = new HashMap<String, String>();

    public StorageSaverSource() {
        this(null);
    }

    public StorageSaverSource(String userName) {
        try {
            dataModel = Util.getDataModelCtrlLocal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.userName = userName;
    }

    private Expression buildQueryByID(Storage storage, String typeName, String[] key) {
        ComplexTypeMetadata type = storage.getMetadataRepository().getComplexType(typeName);
        UserQueryBuilder qb = from(type);
        Collection<FieldMetadata> keyFields = type.getKeyFields();
        int i = 0;
        for (FieldMetadata keyField : keyFields) {
            qb.where(eq(keyField, key[i++]));
        }
        return qb.getExpression();
    }

    @Override
    public Documents get(String dataClusterName, String typeName, String revisionId, String[] key) {
        Documents documents = new Documents();
        Storage storage = getStorage(dataClusterName, revisionId);
        StorageResults results = storage.fetch(buildQueryByID(storage, typeName, key));
        try {
            Iterator<DataRecord> iterator = results.iterator();
            if (!iterator.hasNext()) {
                return null;
            }
            DataRecord record = iterator.next();
            documents.databaseDocument = new StorageDocument(dataClusterName, record);
            documents.databaseValidationDocument = new StorageDocument(dataClusterName, record);
        } finally {
            results.close();
        }
        return documents;
    }

    private Storage getStorage(String dataClusterName, String revisionId) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        return storageAdmin.get(dataClusterName, revisionId);
    }

    @Override
    public boolean exist(String dataCluster, String typeName, String revisionId, String[] key) {
        Storage storage = getStorage(dataCluster, revisionId);
        if (storage == null) {
            return false;
        }
        StorageResults results = storage.fetch(buildQueryByID(storage, typeName, key));
        try {
            Iterator<DataRecord> iterator = results.iterator();
            return iterator.hasNext();
        } finally {
            results.close();
        }
    }

    @Override
    public MetadataRepository getMetadataRepository(String dataModelName) {
        Storage storage = getStorage(dataModelName, null);
        return storage.getMetadataRepository();
    }

    @Override
    public InputStream getSchema(String dataModelName) {
        try {
            synchronized (schemasAsString) {
                if (schemasAsString.get(dataModelName) == null) {
                    String schemaAsString = dataModel.getDataModel(new DataModelPOJOPK(dataModelName)).getSchema();
                    schemasAsString.put(dataModelName, schemaAsString);
                }
            }
            return new ByteArrayInputStream(schemasAsString.get(dataModelName).getBytes("UTF-8")); //$NON-NLS-1$
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getUniverse() {
        try {
            return LocalUser.getLocalUser().getUniverse().getName();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public OutputReport invokeBeforeSaving(DocumentSaverContext context, MutableDocument updateReportDocument) {
        try {
            return Util.beforeSaving(context.getUserDocument().getType().getName(),
                    context.getDatabaseDocument().exportToString(),
                    updateReportDocument.exportToString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getCurrentUserRoles() {
        try {
            // get user roles from current user.
            return LocalUser.getLocalUser().getRoles();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserName() {
        try {
            return LocalUser.getLocalUser().getUsername();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existCluster(String revisionID, String dataClusterName) {
        return true;
    }

    public String getConceptRevisionID(String typeName) {
        try {
            return LocalUser.getLocalUser().getUniverse().getConceptRevisionID(typeName);
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetLocalUsers() {
        try {
            LocalUser.resetLocalUsers();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public void initAutoIncrement() {
        AutoIncrementGenerator.init();
    }

    public void routeItem(String dataCluster, String typeName, String[] id) {
        try {
            RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
            DataClusterPOJOPK dataClusterPOJOPK = new DataClusterPOJOPK(dataCluster);
            ctrl.route(new ItemPOJOPK(dataClusterPOJOPK, typeName, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void invalidateTypeCache(String dataModelName) {
        XmlSchemaValidator.invalidateCache(dataModelName);
        synchronized (schemasAsString) {
            schemasAsString.remove(dataModelName);
        }
        synchronized (LoadServlet.typeNameToKeyDef) {
            LoadServlet.typeNameToKeyDef.clear();
        }
    }

    public void saveAutoIncrement() {
        AutoIncrementGenerator.saveToDB();
    }

    public String nextAutoIncrementId(String universe, String dataCluster, String conceptName) {
        return String.valueOf(AutoIncrementGenerator.generateNum(universe, dataCluster, conceptName));
    }

    public String getLegitimateUser() {
        // web service caller
        if (userName != null) {
            return userName;
        }
        return getUserName();
    }
}
