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

package org.talend.mdm.server.api;

/**
 *
 */
public interface DataModel {
    /**
     * Creates or updates a DataModel
     * @throws com.amalto.core.util.XtentisException
     */
    com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK putDataModel(com.amalto.core.objects.datamodel.ejb.DataModelPOJO dataModel) throws com.amalto.core.util.XtentisException;

    /**
     * Get Data Model
     * @throws com.amalto.core.util.XtentisException
     */
    com.amalto.core.objects.datamodel.ejb.DataModelPOJO getDataModel(com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK pk) throws com.amalto.core.util.XtentisException;

    /**
     * Get a DataModel - no exception is thrown: returns null if not found
     * @throws com.amalto.core.util.XtentisException
     */
    com.amalto.core.objects.datamodel.ejb.DataModelPOJO existsDataModel(com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK pk) throws com.amalto.core.util.XtentisException;

    /**
     * Remove an Data Model
     * @throws com.amalto.core.util.XtentisException
     */
    com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK removeDataModel(com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK pk) throws com.amalto.core.util.XtentisException;

    /**
     * Retrieve all DataModel PKs
     * @throws com.amalto.core.util.XtentisException
     */
    java.util.Collection getDataModelPKs(String regex) throws com.amalto.core.util.XtentisException;

    /**
     * Checks the datamodel - returns the "corrected schema"
     * @throws com.amalto.core.util.XtentisException
     */
    String checkSchema(String schema) throws com.amalto.core.util.XtentisException;

    /**
     * Put a Business Concept Schema
     * @throws com.amalto.core.util.XtentisException
     * @return its name
     */
    String putBusinessConceptSchema(com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK pk, String conceptSchemaString) throws com.amalto.core.util.XtentisException;

    /**
     * Delete a Business Concept
     * @throws com.amalto.core.util.XtentisException
     * @return its name
     */
    String deleteBusinessConcept(com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK pk, String businessConceptName) throws com.amalto.core.util.XtentisException;

    /**
     * Find all Business Concepts names
     * @throws com.amalto.core.util.XtentisException
     */
    String[] getAllBusinessConceptsNames(com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK pk) throws com.amalto.core.util.XtentisException;
}
