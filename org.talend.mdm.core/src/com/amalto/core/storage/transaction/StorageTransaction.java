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

import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;

/**
 * Represents a transaction for a given {@link com.amalto.core.storage.Storage}. A {@link Transaction} may contain
 * several {@link StorageTransaction} instances (in case the transaction implies several storages or MDM data containers).
 * @see Transaction
 */
public abstract class StorageTransaction {

    protected boolean isAutonomous;

    /**
     * @return The {@link Storage} managed by this storage transaction.
     */
    public abstract Storage getStorage();

    /**
     * Starts a transaction on the underlying storage.
     */
    public abstract void begin();

    /**
     * Commits pending changes to the underlying storage.
     * @see #autonomous()
     * @see #dependent()
     */
    public void commit() {
        if (isAutonomous) {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            transactionManager.currentTransaction().exclude(getStorage());
        }
    }

    /**
     * Rollbacks (revert) all pending changes on the underlying storage. After this method completes, the method
     * {@link #hasFailed()} <b>must</b> return <code>false</code>.
     * @see #autonomous()
     * @see #dependent()
     */
    public void rollback() {
        if (isAutonomous) {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            transactionManager.currentTransaction().exclude(getStorage());
        }
    }

    /**
     * @return A {@link StorageTransaction} that may participate in a {@link Transaction} and returned storage transaction
     *         <b>DO</b> decide to commit. In fact returned transaction is expected to perform as if it is the only running
     *         transaction in MDM, thus {@link #commit()} or {@link #rollback()} <b>MUST</b> perform action when called.
     */
    public StorageTransaction autonomous() {
        isAutonomous = true;
        return this;
    }

    /**
     * @return A {@link StorageTransaction} that participates in a {@link Transaction} and returned storage transaction
     * <b>DO NOT</b> decide to commit. In fact returned transaction is expected to perform no action when {@link #commit()}
     * or {@link #rollback()} is called.
     */
    public StorageTransaction dependent() {
        isAutonomous = false;
        return this;
    }

    /**
     * @return <code>true</code> if any {@link #commit()} previously failed and no {@link #rollback()} was consequently
     * called. <code>false</code> if no commit was previously done OR if commit completed without any problem OR if
     * rollback was called to revert all commit changes.
     */
    public abstract boolean hasFailed();
}
