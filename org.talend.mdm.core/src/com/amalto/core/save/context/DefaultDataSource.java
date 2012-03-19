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
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.OutputReport;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

class DefaultDataSource implements SaverSource {

    private final XmlServerSLWrapperLocal database;

    private final DataModelCtrlLocal dataModel;

    private final String dataClusterName;

    private final String dataModelName;

    private final String revisionId;

    private MetadataRepository repository;

    private String schemaAsString;

    public DefaultDataSource(String dataClusterName, String dataModelName, String revisionId) {
        this.dataClusterName = dataClusterName;
        this.dataModelName = dataModelName;
        this.revisionId = revisionId;

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
    }

    public InputStream get(String[] key) {
        try {
            String documentAsString = database.getDocumentAsString(revisionId, dataClusterName, Util.joinStrings(key, "."));
            if (documentAsString != null) {
                return new ByteArrayInputStream(documentAsString.getBytes("UTF-8"));
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exist(String[] key) {
        return get(key) == null;
    }

    public MetadataRepository getMetadataRepository() {
        try {
            if (repository == null) {
                repository = new MetadataRepository();
                repository.load(getSchema(dataModelName));
            }
            return repository;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getSchema(String dataModelName) {
        try {
            if (schemaAsString == null) {
                schemaAsString = dataModel.getDataModel(new DataModelPOJOPK(this.dataModelName)).getSchema();
            }
            return new ByteArrayInputStream(schemaAsString.getBytes("UTF-8"));
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

    public void save(ItemPOJO item) {
        try {
            item.store();
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
            return LocalUser.getLocalUser().getRoles();
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

}
