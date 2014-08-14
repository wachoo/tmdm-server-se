package com.amalto.core.storage;

/**
 * A special DispatchWrapper: this wrapper always uses RDBMS for both system and user master data.
 */
// Dynamically called! Don't remove!
public class SQLWrapper extends DispatchWrapper {

    // MDM instantiates several times this class, keep constructor parameters as constant limits new instances.
    private static final SystemStorageWrapper INTERNAL = new SystemStorageWrapper();

    // MDM instantiates several times this class, keep constructor parameters as constant limits new instances.
    private static final StorageWrapper USER = new StorageWrapper();

    public SQLWrapper() {
        super(INTERNAL, USER);
    }
}
