package com.amalto.core.query.user;

public class UserStagingQueryBuilder {
    /**
     * @return A {@link TypedExpression} that represents the staging area status of the record.
     * @see com.amalto.core.storage.task.StagingConstants
     */
    public static TypedExpression status() {
        return new StagingStatus();
    }
}
