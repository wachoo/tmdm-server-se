package com.amalto.core.storage;

/**
 * A special DispatchWrapper: this wrapper always uses RDBMS for both system and user master data.
 */
public class SQLWrapper extends DispatchWrapper {
    public SQLWrapper() {
        super(new SystemStorageWrapper(), new StorageWrapper());
    }
}
