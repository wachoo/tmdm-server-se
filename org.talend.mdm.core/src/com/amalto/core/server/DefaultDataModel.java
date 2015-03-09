/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.server;

import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.ObjectPOJOPK;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import com.amalto.core.objects.datamodel.DataModelPOJO;
import com.amalto.core.objects.datamodel.DataModelPOJOPK;
import com.amalto.core.util.XtentisException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import com.amalto.core.server.api.DataModel;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultDataModel implements DataModel {

    private static final Logger LOGGER = Logger.getLogger(DefaultDataModel.class);

    @Override
    public DataModelPOJOPK putDataModel(DataModelPOJO dataModel) throws XtentisException {
        try {
            if ((dataModel.getSchema() == null) || "".equals(dataModel.getSchema())) { //$NON-NLS-1$
                // put an empty schema
                dataModel.setSchema("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //$NON-NLS-1$
                        "<xsd:schema " + //$NON-NLS-1$
                        "	elementFormDefault=\"qualified\"" + //$NON-NLS-1$
                        "	xml:lang=\"EN\"" + //$NON-NLS-1$
                        "	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + //$NON-NLS-1$
                        "</xsd:schema>" //$NON-NLS-1$
                );
            }
            ObjectPOJOPK pk = dataModel.store();
            if (pk == null) {
                throw new XtentisException("Unable to create the Data Model"); //$NON-NLS-1$
            }
            return new DataModelPOJOPK(pk);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to create/update the Data Model '" + dataModel.getName() + '\''; //$NON-NLS-1$
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }

    }

    @Override
    public DataModelPOJO getDataModel(DataModelPOJOPK pk) throws XtentisException {
        if (pk == null || pk.getUniqueId() == null) {
            throw new XtentisException("The Data Model can't be empty!"); //$NON-NLS-1$
        }
        try {
            DataModelPOJO sp = ObjectPOJO.load(DataModelPOJO.class, pk);
            if (sp == null && pk.getUniqueId() != null && !"null".equals(pk.getUniqueId())) { //$NON-NLS-1$
                String err = "The Data Model '" + pk.getUniqueId() + "' does not exist."; //$NON-NLS-1$ //$NON-NLS-2$
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            return sp;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the Data Model '" + pk.toString() + '\''; //$NON-NLS-1$;
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    @Override
    public DataModelPOJO existsDataModel(DataModelPOJOPK pk) throws XtentisException {
        try {
            return ObjectPOJO.load(DataModelPOJO.class, pk);
        } catch (XtentisException e) {
            return null;
        } catch (Exception e) {
            String err = "Could not check whether this Data Model exists: '" + pk.getUniqueId() + '\''; //$NON-NLS-1$
            LOGGER.error(err, e);
            return null;
        }
    }

    @Override
    public DataModelPOJOPK removeDataModel(DataModelPOJOPK pk) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing " + pk.getUniqueId()); //$NON-NLS-1$
        }

        try {
            return new DataModelPOJOPK(ObjectPOJO.remove(DataModelPOJO.class, pk));
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to remove the DataModel '" + pk.toString() + '\''; //$NON-NLS-1$
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }

    }

    @Override
    public Collection<DataModelPOJOPK> getDataModelPKs(String regex) throws XtentisException {
        Collection<ObjectPOJOPK> dataModelPKs = ObjectPOJO.findAllPKs(DataModelPOJO.class, regex);
        ArrayList<DataModelPOJOPK> l = new ArrayList<DataModelPOJOPK>();
        for (ObjectPOJOPK dataModelPK : dataModelPKs) {
            l.add(new DataModelPOJOPK(dataModelPK));
        }
        return l;
    }

    @Override
    public String checkSchema(String schema) throws XtentisException {
        return schema;
    }

    @Override
    public String putBusinessConceptSchema(DataModelPOJOPK pk, String conceptSchemaString) throws XtentisException {
        throw new NotImplementedException();
    }

    @Override
    public String deleteBusinessConcept(DataModelPOJOPK pk, String businessConceptName) throws XtentisException {
        throw new NotImplementedException();
    }

    @Override
    public String[] getAllBusinessConceptsNames(DataModelPOJOPK pk) throws XtentisException {
        MetadataRepositoryAdmin metadataRepositoryAdmin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
        MetadataRepository repository = metadataRepositoryAdmin.get(pk.getUniqueId());
        Collection<ComplexTypeMetadata> userComplexTypes = repository.getUserComplexTypes();
        String[] businessConceptNames = new String[userComplexTypes.size()];
        int i = 0;
        for (ComplexTypeMetadata currentType : userComplexTypes) {
            businessConceptNames[i++] = currentType.getName();
        }
        return businessConceptNames;
    }
}
