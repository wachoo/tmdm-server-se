package com.amalto.core.storage.task;

/**
 * <ul>
 * <li>000: New</li>
 * <li>201: Processed by 'identify clusters' successfully.</li>
 * <li>202: Processed by 'merge clusters' successfully.</li>
 * <li>203: Processed by 'validate'.</li>
 * <li>401: Processed by 'identify clusters' but failed.</li>
 * <li>402: Processed by 'merge clusters' but failed.</li>
 * <li>403: Processed by 'validate & save' but failed due to validation issue.</li>
 * <li>404: Processed by 'validate & save' but failed due to database constraint issue.</li>
 * </ul>
 */
public interface StagingConstants {
    String NEW = "000"; //$NON-NLS-1$
    String SUCCESS_IDENTIFIED_CLUSTERS = "201"; //$NON-NLS-1$
    String SUCCESS_MERGE_CLUSTERS = "202"; //$NON-NLS-1$
    String SUCCESS_VALIDATE = "203"; //$NON-NLS-1$
    String FAIL_IDENTIFIED_CLUSTERS = "401"; //$NON-NLS-1$
    String FAIL_MERGE_CLUSTERS = "402"; //$NON-NLS-1$
    String FAIL_VALIDATE_VALIDATION = "403"; //$NON-NLS-1$
    String FAIL_VALIDATE_CONSTRAINTS = "404"; //$NON-NLS-1$
}
