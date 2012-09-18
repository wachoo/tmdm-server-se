package com.amalto.core.query.user;

public class UserStagingQueryBuilder {
    /**
     * @return A {@link TypedExpression} that represents the staging area status of the record.
     * @see com.amalto.core.storage.task.StagingConstants
     */
    public static TypedExpression status() {
        return StagingStatus.INSTANCE;
    }

    /**
     * @return A {@link TypedExpression} that represents the staging area source of the record (this is user provided
     * value in staging area).
     */
    public static TypedExpression source() {
        return StagingSource.INSTANCE;
    }

    /**
     * @return A {@link TypedExpression} that represents the staging area last error of the record (the last exception
     * that happened during record validation).
     */
    public static TypedExpression error() {
        return StagingError.INSTANCE;
    }
}
