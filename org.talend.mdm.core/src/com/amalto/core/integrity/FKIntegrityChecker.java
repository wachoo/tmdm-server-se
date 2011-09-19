/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.integrity;

import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereCondition;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Entry point for all FK integrity checks.
 * <p/>
 * <code>
 * boolean override = false;<br/>
 * boolean isAllowed = FKIntegrityChecker.getInstance().allowDelete(dataClusterPK, concept, ids, override);<br/>
 * if(!isAllowed) {<br/>
 * Set<ReferenceFieldMetadata> fieldToCheck = getForeignKeyList(concept, clusterName);<br/>
 * System.out.println("Could not delete because following fields points to instance:");<br/>
 * // Iterate over fieldToCheck...<br/>
 * }<br/>
 * </code>
 */
public class FKIntegrityChecker {

    private final static FKIntegrityChecker instance = new FKIntegrityChecker();

    private FKIntegrityChecker() {
    }

    public static FKIntegrityChecker getInstance() {
        return instance;
    }

    /**
     * <p>
     * Returns a MDM user is allowed to delete an instance of type <code>concept</code> with id <code>ids</code>.
     * </p>
     *
     * @param clusterName An existing cluster name.
     * @param concept     An existing concept name.
     * @param ids         An instance ID (an array of values in case of composite keys).
     * @param override    <code>true</code> if user wants to override fk integrity (but {@link #getFKIntegrityPolicy(String, String, String[])}
     *                    <b>must</b> return {@link FKIntegrityCheckResult#FORBIDDEN_OVERRIDE_ALLOWED} in this case.
     * @return <code>true</code> if user is allowed to delete instance, <code>false</code> otherwise.
     * @throws XtentisException In case of unexpected error.
     */
    public boolean allowDelete(String clusterName, String concept, String[] ids, boolean override) throws XtentisException {
        FKIntegrityCheckResult policy = getFKIntegrityPolicy(clusterName, concept, ids);
        switch (policy) {
            case FORBIDDEN:
                return false;
            case FORBIDDEN_OVERRIDE_ALLOWED:
                return override; // return true if overriding... false otherwise.
            case ALLOWED:
                return true;
            default:
                throw new XtentisException("Value '" + policy + "' is not supported.");
        }
    }

    /**
     * <p>
     * Returns what kind of integrity check is allowed when deleting an instance of <code>concept</code> with id <code>ids</code>.
     * </p>
     *
     * @param clusterName An existing cluster name.
     * @param concept     An existing concept name.
     * @param ids         An instance ID (an array of values in case of composite keys).
     * @return A value of {@link FKIntegrityCheckResult} that corresponds to the type of policy that should be enforced.
     * @throws XtentisException In case of unexpected error during check.
     * @see FKIntegrityCheckResult
     */
    public FKIntegrityCheckResult getFKIntegrityPolicy(String clusterName, String concept, String[] ids) throws XtentisException {
        Set<ReferenceFieldMetadata> fieldToCheck = getForeignKeyList(concept, clusterName); // TODO Cluster name is not data model name!

        // Query pk where fk could be.
        String queryId = "";
        for (String id : ids) {
            queryId += '[' + id + ']';
        }
        LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
        conceptPatternsToClusterName.put(".*", clusterName);
        for (ReferenceFieldMetadata referenceFieldMetadata : fieldToCheck) {
            boolean allowOverride = referenceFieldMetadata.allowFKIntegrityOverride();
            TypeMetadata currentType = referenceFieldMetadata.getContainingType();
            IWhereItem whereItem = new WhereCondition(referenceFieldMetadata.getContainingType().getName() + '/' + referenceFieldMetadata.getName(), WhereCondition.EQUALS, queryId, WhereCondition.NO_OPERATOR);
            long count = Util.getXmlServerCtrlLocal().countItems(new LinkedHashMap(), conceptPatternsToClusterName, currentType.getName(), whereItem);

            if (count > 0) {
                if (allowOverride) {
                    return FKIntegrityCheckResult.FORBIDDEN_OVERRIDE_ALLOWED;
                } else {
                    return FKIntegrityCheckResult.FORBIDDEN;
                }
            }
        }

        return FKIntegrityCheckResult.ALLOWED;
    }

    /**
     * Returns a {@link Set} of {@link com.amalto.core.metadata.FieldMetadata} of all fields that <b>point to</b> the
     * concept <code>concept</code>.
     *
     * @param dataModelName A data model name
     * @param concept       A concept name.
     * @return A {@link Set} of {@link com.amalto.core.metadata.FieldMetadata} or empty set if no field points to
     *         <code>concept</code>.
     * @throws XtentisException In case of unexpected error during metadata analysis.
     */
    public Set<ReferenceFieldMetadata> getForeignKeyList(String dataModelName, String concept) throws XtentisException {
        // Get FK(s) to check
        MetadataRepository mr = new MetadataRepository();
        try {
            DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(dataModelName));
            mr.load(new ByteArrayInputStream(dataModel.getSchema().getBytes("utf-8"))); //$NON-NLS-1$
        } catch (Exception e) {
            throw new XtentisException(e);
        }

        return mr.accept(new ForeignKeyIntegrity(mr.getType(concept)));
    }
}
