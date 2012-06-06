package com.amalto.core.storage.task;

/**
 * Created with IntelliJ IDEA.
 * User: francois
 * Date: 17/05/12
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public interface StagingConstants {
    String NEW = "000";
    String SUCCESS_IDENTIFIED_CLUSTERS = "201";
    String SUCCESS_MERGE_CLUSTERS = "202";
    String SUCCESS_VALIDATE = "203";
    String FAIL_IDENTIFIED_CLUSTERS = "401";
    String FAIL_MERGE_CLUSTERS = "402";
    String FAIL_VALIDATE_VALIDATION = "403";
    String FAIL_VALIDATE_CONSTRAINTS = "404";

    /*
    000: New
201: Processed by 'identify clusters' successfully.
202: Processed by 'merge clusters' successfully.
203: Processed by 'validate'.
401: Processed by 'identify clusters' but failed.
402: Processed by 'merge clusters' but failed.
403: Processed by 'validate & save' but failed due to validation issue.
404: Processed by 'validate & save' but failed due to database constraint issue.
     */

}
