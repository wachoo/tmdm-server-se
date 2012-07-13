package com.amalto.core.query.user;

public class UserStagingQueryBuilder {
    public static TypedExpression status() {
        return new StagingStatus();
    }
}
