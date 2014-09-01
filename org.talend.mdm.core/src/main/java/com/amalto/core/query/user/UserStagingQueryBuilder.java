package com.amalto.core.query.user;

import com.amalto.core.query.user.metadata.*;

public class UserStagingQueryBuilder {
    /**
     * @return A {@link TypedExpression} that represents the staging area status of the record.
     * @see org.talend.mdm.storage.task.StagingConstants
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

    /**
     * @return A {@link TypedExpression} that represents the staging area block key.
     */
    public static TypedExpression blockKey() {
        return StagingBlockKey.INSTANCE;
    }

    /**
     * @return A {@link TypedExpression} that represents the number of similar record in group (>= 1).
     */
    public static TypedExpression groupSize() {
        return GroupSize.INSTANCE;
    }
}
