package com.amalto.core.query.user;

/**
 * Represents a condition that returns always the same value. This can be used as filler for binary logic operators or
 * to invalidate queries (i.e. "select type where false" should return no result).
 */
public interface ConstantCondition extends Condition {

    /**
     * @return The condition constant value (<code>true</code> is condition is true, <code>false</code> otherwise).
     */
    boolean value();

}
