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

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.history.MutableDocument;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.schema.validation.XmlSchemaValidator;
import com.amalto.core.server.DataModel;
import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.XmlServer;
import com.amalto.core.servlet.LoadServlet;
import com.amalto.core.util.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultSaverSource implements SaverSource {

    private final XmlServer database;

    private final DataModel dataModel;

    private final Map<String, String> schemasAsString = new HashMap<String, String>();

    private final String userName;

    public DefaultSaverSource() {
        this(null);
    }

    public DefaultSaverSource(String userName) {
        try {
            database = Util.getXmlServerCtrlLocal();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }

        try {
            dataModel = Util.getDataModelCtrlLocal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.userName = userName;
    }

    public InputStream get(String dataClusterName, String typeName, String revisionId, String[] key) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(dataClusterName).append('.').append(typeName).append('.');
            for (int i = 0; i < key.length; i++) {
                builder.append(key[i]);
                if (i < key.length - 1) {
                    builder.append('.');
                }
            }
            String uniqueId = builder.toString();

            String documentAsString = database.getDocumentAsString(revisionId, dataClusterName, uniqueId, "UTF-8"); //$NON-NLS-1$
            if (documentAsString != null) {
                return new ByteArrayInputStream(documentAsString.getBytes("UTF-8")); //$NON-NLS-1$
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exist(String dataCluster, String typeName, String revisionId, String[] key) {
        return get(dataCluster, typeName, revisionId, key) != null;
    }

    public MetadataRepository getMetadataRepository(String dataModelName) {
        try {
            MetadataRepositoryAdmin admin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
            return admin.get(dataModelName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
            return Util.beforeSaving(context.getType().getName(),
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
        try {
            return database.existCluster(revisionID, dataClusterName);
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
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
