/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.transaction;

import com.amalto.core.storage.Storage;

/**
 * A MDM transaction: it composed of multiple underlying {@link StorageTransaction}, each of them handles {@link Storage}
 * specific transaction operations.
 */
public interface Transaction {
    /**
     * Configures what is the transaction "life time": how long it should remain active in MDM server.
     */
    public enum Lifetime {
        /**
         * A short life transaction: usually life time is bound to the HTTP request life time.
         */
        AD_HOC,
        /**
         * A "long" transaction: transaction was externally started and must remain active till commit/rollback actions
         * are called.
         */
        LONG
    }

    /**
     * @return A unique identifier for this MDM transaction.
     */
    String getId();

    /**
     * Begin the transaction: all underlying {@link StorageTransaction#begin()} get also called.
     */
    void begin();

    /**
     * Commit the transaction: all underlying {@link StorageTransaction#commit()} get also called.
     */
    void commit();

    /**
     * Rollback the transaction: all underlying {@link StorageTransaction#rollback()} get also called.
     */
    void rollback();

    /**
     * Includes a {@link Storage} inside the scope of this transaction.
     *
     * @param storage A storage implementation.
     * @return The {@link StorageTransaction} instance that handles all operations relative to the <code>storage</code>
     *         in this transaction.
     * @see #exclude(com.amalto.core.storage.Storage)
     */
    StorageTransaction include(Storage storage);

    /**
     * Excludes a {@link Storage} inside the scope of this transaction. If transaction lifetime is {@link Lifetime#AD_HOC}
     * transaction <b>MUST</b> be removed from the active transactions in {@link TransactionManager}.
     *
     * @param storage A storage implementation.
     * @return The {@link StorageTransaction} instance that handles all operations relative to the <code>storage</code>
     *         in this transaction.
     * @see #include(com.amalto.core.storage.Storage)
     */
    StorageTransaction exclude(Storage storage);

}
