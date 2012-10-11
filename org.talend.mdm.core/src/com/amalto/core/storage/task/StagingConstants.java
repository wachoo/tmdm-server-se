package com.amalto.core.storage.task;

/**
 * <ul>
 * <li>000: New</li>
 * <li>201: Processed by 'identify clusters' successfully.</li>
 * <li>202: Processed by 'merge clusters' successfully.</li>
 * <li>203: Processed by 'merge clusters' but group is bigger than 1 -> manual merge required.</li>
 * <li>204: Processed by 'merge clusters' and this record is the "golden" record to be used in validation.</li>
 * <li>205: Processed by 'validate & save'.</li>
 * <li>401: Processed by 'identify clusters' but failed.</li>
 * <li>402: Processed by 'merge clusters' but failed.</li>
 * <li>403: Processed by 'validate & save' but failed due to validation issue.</li>
 * <li>404: Processed by 'validate & save' but failed due to database constraint issue.</li>
 * </ul>
 */
public interface StagingConstants {
    String NEW = "000"; //$NON-NLS-1$
    String SUCCESS = "200"; //$NON-NLS-1$
    String SUCCESS_IDENTIFIED_CLUSTERS = "201"; //$NON-NLS-1$
    String SUCCESS_MERGE_CLUSTERS = "202"; //$NON-NLS-1$
    String SUCCESS_MERGE_CLUSTER_TO_RESOLVE = "203"; //$NON-NLS-1$
    String SUCCESS_MERGED_RECORD = "204"; //$NON-NLS-1$
    String SUCCESS_VALIDATE = "205"; //$NON-NLS-1$
    String FAIL = "400"; //$NON-NLS-1$
    String FAIL_IDENTIFIED_CLUSTERS = "401"; //$NON-NLS-1$
    String FAIL_MERGE_CLUSTERS = "402"; //$NON-NLS-1$
    String FAIL_VALIDATE_VALIDATION = "403"; //$NON-NLS-1$
    String FAIL_VALIDATE_CONSTRAINTS = "404"; //$NON-NLS-1$
}
