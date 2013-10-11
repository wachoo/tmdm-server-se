/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.TypeMetadata;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.MetadataUtils;
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
import com.amalto.core.util.AutoIncrementGenerator;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.OutputReport;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

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

    @Override
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

    @Override
    public boolean exist(String dataCluster, String typeName, String revisionId, String[] key) {
        return get(dataCluster, typeName, revisionId, key) != null;
    }

    @Override
    public MetadataRepository getMetadataRepository(String dataModelName) {
        try {
            MetadataRepositoryAdmin admin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
            return admin.get(dataModelName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public String getUniverse() {
        try {
            return LocalUser.getLocalUser().getUniverse().getName();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutputReport invokeBeforeSaving(DocumentSaverContext context, MutableDocument updateReportDocument) {
        try {
            return Util.beforeSaving(context.getType().getName(), context.getDatabaseDocument().exportToString(),
                    updateReportDocument.exportToString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getCurrentUserRoles() {
        try {
            // get user roles from current user.
            return LocalUser.getLocalUser().getRoles();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUserName() {
        try {
            return LocalUser.getLocalUser().getUsername();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existCluster(String revisionID, String dataClusterName) {
        try {
            return database.existCluster(revisionID, dataClusterName);
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getConceptRevisionID(String typeName) {
        try {
            return LocalUser.getLocalUser().getUniverse().getConceptRevisionID(typeName);
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetLocalUsers() {
        try {
            LocalUser.resetLocalUsers();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initAutoIncrement() {
        AutoIncrementGenerator.init();
    }

    @Override
    public void routeItem(String dataCluster, String typeName, String[] id) {
        try {
            RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
            DataClusterPOJOPK dataClusterPOJOPK = new DataClusterPOJOPK(dataCluster);
            ctrl.route(new ItemPOJOPK(dataClusterPOJOPK, typeName, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void invalidateTypeCache(String dataModelName) {
        XmlSchemaValidator.invalidateCache(dataModelName);
        synchronized (schemasAsString) {
            schemasAsString.remove(dataModelName);
        }
        synchronized (LoadServlet.typeNameToKeyDef) {
            LoadServlet.typeNameToKeyDef.clear();
        }
    }

    @Override
    public void saveAutoIncrement() {
        AutoIncrementGenerator.saveToDB();
    }

    @Override
    public String nextAutoIncrementId(String universe, String dataCluster, String conceptName) {
        long autoIncrementId = -1;
        String concept = null;
        String field = null;
        if (conceptName.contains(".")) { //$NON-NLS-1$
            String[] conceptArray = conceptName.split("\\."); //$NON-NLS-1$
            concept = conceptArray[0];
            field = conceptArray[1];
        } else {
            concept = conceptName;
        }
        MetadataRepository metadataRepository = getMetadataRepository(dataCluster);
        if (metadataRepository != null) {
            ComplexTypeMetadata complexType = metadataRepository.getComplexType(concept);
            if (complexType != null) {
                TypeMetadata superType = MetadataUtils.getSuperConcreteType(complexType);
                if (superType != null) {
                    concept = superType.getName();
                }
                String autoIncrementFiledName = field != null ? concept + "." + field : concept; //$NON-NLS-1$
                autoIncrementId = AutoIncrementGenerator.generateNum(universe, dataCluster, autoIncrementFiledName);
            }
        }
        return String.valueOf(autoIncrementId);
    }

    @Override
    public String getLegitimateUser() {
        // web service caller
        if (userName != null) {
            return userName;
        }
        return getUserName();
    }

}
