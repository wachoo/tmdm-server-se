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

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereCondition;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.util.*;

import static com.amalto.core.integrity.FKIntegrityCheckResult.ALLOWED;
import static com.amalto.core.integrity.FKIntegrityCheckResult.FORBIDDEN;
import static com.amalto.core.integrity.FKIntegrityCheckResult.FORBIDDEN_OVERRIDE_ALLOWED;

/**
 * <p>
 * Entry point for all FK integrity checks. See below example of use:
 * </p>
 * <p>
 * <code>
 * boolean override = false;<br/>
 * boolean isAllowed = FKIntegrityChecker.getInstance().allowDelete(dataClusterPK, concept, ids, override);<br/>
 * if(!isAllowed) {<br/>
 * &nbsp;&nbsp;Set&lt;ReferenceFieldMetadata&gt; fieldToCheck = getForeignKeyList(concept, clusterName);<br/>
 * &nbsp;&nbsp;System.out.println("Could not delete because following fields points to instance:");<br/>
 * &nbsp;&nbsp;// Iterate over fieldToCheck...<br/>
 * }<br/>
 * </code>
 * </p>
 */
public class FKIntegrityChecker {

    private final static FKIntegrityChecker instance = new FKIntegrityChecker();

    private final static Logger logger = Logger.getLogger(FKIntegrityChecker.class);

    private FKIntegrityChecker() {
    }

    /**
     * @return Returns singleton instance of {@link FKIntegrityChecker}.
     */
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
     *                    <b>must</b> return {@link FKIntegrityCheckResult#FORBIDDEN_OVERRIDE_ALLOWED} in this case).
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
                throw new XtentisException("Value '" + policy + "' is not supported.");  //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <p>
     * Returns what kind of integrity check is allowed when deleting an instance of <code>concept</code> with id <code>ids</code>.
     * </p>
     * <p>
     * <b>Note:</b> The data model name will be extracted from the document stored in database.
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
        // Extract data model from database
        String dataModel;
        try {
            ItemPOJOPK pk = new ItemPOJOPK(new DataClusterPOJOPK(clusterName), concept, ids);
            ItemPOJO item = Util.getItemCtrl2Local().getItem(pk);
            if (item == null) {
                String id = ""; //$NON-NLS-1$
                for (String currentIdValue : ids) {
                    id += "[" + currentIdValue + "]"; //$NON-NLS-1$ //$NON-NLS-2$
                }

                throw new RuntimeException("Document with id '" //$NON-NLS-1$
                        + id
                        + "' (concept name: '" //$NON-NLS-1$
                        + concept
                        + "') has already been deleted."); //$NON-NLS-1$
            } else {
                dataModel = item.getDataModelName();
            }
        } catch (Exception e) {
            throw new XtentisException(e);
        }

        // Gets field(s) to check
        Set<ReferenceFieldMetadata> fieldToCheck = getForeignKeyList(concept, dataModel);

        // Query pk where fk could be.
        String queryId = "";
        for (String id : ids) {
            queryId += '[' + id + ']';
        }
        LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
        conceptPatternsToClusterName.put(".*", clusterName);

        // Sort all fields by FK integrity policy
        Map<FKIntegrityCheckResult, Set<FieldMetadata>> checkResultToFields = new HashMap<FKIntegrityCheckResult, Set<FieldMetadata>>();
        for (ReferenceFieldMetadata referenceFieldMetadata : fieldToCheck) {
            boolean allowOverride = referenceFieldMetadata.allowFKIntegrityOverride();
            TypeMetadata currentType = referenceFieldMetadata.getContainingType();
            IWhereItem whereItem = new WhereCondition(referenceFieldMetadata.getContainingType().getName() + '/' + referenceFieldMetadata.getName(), WhereCondition.EQUALS, queryId, WhereCondition.NO_OPERATOR);
            long count = Util.getXmlServerCtrlLocal().countItems(new LinkedHashMap(), conceptPatternsToClusterName, currentType.getName(), whereItem);

            if (count > 0) {
                if(referenceFieldMetadata.isFKIntegrity()) {
                    if (allowOverride) {
                        get(checkResultToFields, FORBIDDEN_OVERRIDE_ALLOWED).add(referenceFieldMetadata);
                    } else {
                        get(checkResultToFields, FORBIDDEN).add(referenceFieldMetadata);
                    }
                } else {
                    // FK does not enforce FK integrity so it's allowed.
                    get(checkResultToFields, ALLOWED).add(referenceFieldMetadata);
                }
            } else {
                get(checkResultToFields, ALLOWED).add(referenceFieldMetadata);
            }
        }

        if (checkResultToFields.isEmpty()) {
            // No FK pointing to record was found... returns allowed.
            return ALLOWED;
        }

        // Interpretation of results
        if (hasOnly(FORBIDDEN, checkResultToFields, fieldToCheck)) {
            return FORBIDDEN;
        } else if (hasOnly(FORBIDDEN_OVERRIDE_ALLOWED, checkResultToFields, fieldToCheck)) {
            return FORBIDDEN_OVERRIDE_ALLOWED;
        } else if (hasOnly(ALLOWED, checkResultToFields, fieldToCheck)) {
            return ALLOWED;
        } else {
            // Mixed results (some fields are forbidden and/or forbidden allowed and/or allowed)
            FKIntegrityCheckResult conflictResolution;
            if (has(FORBIDDEN, checkResultToFields)) {
                conflictResolution = FORBIDDEN;
            } else if (has(FORBIDDEN_OVERRIDE_ALLOWED, checkResultToFields)) {
                conflictResolution = FORBIDDEN_OVERRIDE_ALLOWED;
            } else if (has(ALLOWED, checkResultToFields)) {
                conflictResolution = ALLOWED;
            } else {
                throw new IllegalStateException("Cannot infer FK integrity check from data model."); //$NON-NLS-1$
            }

            // Log in server's log how conflict was solved.
            logConflictResolution(checkResultToFields, conflictResolution);

            return conflictResolution;
        }
    }

    /**
     * Returns a {@link Set} of {@link com.amalto.core.metadata.FieldMetadata} of all fields that <b>point to</b> the
     * concept <code>concept</code>. Fields are inferred from data model only (thus no need for id in this method).
     *
     * @param dataModelName A data model name
     * @param concept       A concept name.
     * @return A {@link Set} of {@link com.amalto.core.metadata.FieldMetadata} or empty set if no field points to
     *         <code>concept</code>.
     * @throws XtentisException In case of unexpected error during metadata analysis.
     */
    public Set<ReferenceFieldMetadata> getForeignKeyList(String concept, String dataModelName) throws XtentisException {
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

    /**
     * 'Safe' getter for map. If value does not exist, creates it and put it at the right key for next calls.
     *
     * @param map The map to be queried.
     * @param key The key where the caller expects an non-null value.
     * @return The value for <code>key</code> or a newly created value if it didn't exist in <code>map</code>.
     */
    private static Set<FieldMetadata> get(Map<FKIntegrityCheckResult, Set<FieldMetadata>> map, FKIntegrityCheckResult key) {
        Set<FieldMetadata> value = map.get(key);
        if (value == null) {
            value = new HashSet<FieldMetadata>();
            map.put(key, value);
        }
        return value;
    }

    private static boolean has(FKIntegrityCheckResult checkResult, Map<FKIntegrityCheckResult, Set<FieldMetadata>> checkResultToFields) {
        return checkResultToFields.get(checkResult) != null;
    }

    private static void logConflictResolution(Map<FKIntegrityCheckResult, Set<FieldMetadata>> checkResultToFields, FKIntegrityCheckResult conflictResolution) {
        if (logger.isInfoEnabled()) {
            logger.info("Found conflicts in data model relative to FK integrity checks");
            logger.info("= Forbidden deletes =");
            dumpFields(checkResultToFields, FORBIDDEN, logger);
            logger.info("= Forbidden deletes (override allowed) =");
            dumpFields(checkResultToFields, FORBIDDEN_OVERRIDE_ALLOWED, logger);
            logger.info("= Allowed deletes =");
            dumpFields(checkResultToFields, ALLOWED, logger);
            logger.info("Conflict resolution: " + conflictResolution);
        }
    }

    private static void dumpFields(Map<FKIntegrityCheckResult, Set<FieldMetadata>> checkResultToFields, FKIntegrityCheckResult checkResult, Logger logger) {
        Set<FieldMetadata> fields = checkResultToFields.get(checkResult);
        if (fields != null) {
            for (FieldMetadata fieldMetadata : fields) {
                logger.info(fieldMetadata.toString());
            }
        }
    }

    private static boolean hasOnly(FKIntegrityCheckResult checkResult, Map<FKIntegrityCheckResult, Set<FieldMetadata>> checkResultToFields, Set<ReferenceFieldMetadata> fieldToCheck) {
        return checkResultToFields.get(checkResult) != null
                && checkResultToFields.get(checkResult).size() == fieldToCheck.size();
    }
}
