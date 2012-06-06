package com.amalto.core.query.user;

/**
 * Created with IntelliJ IDEA.
 * User: francois
 * Date: 17/05/12
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class UserStagingQueryBuilder {
    public static TypedExpression status() {
        return new StagingStatus();
    }
}
