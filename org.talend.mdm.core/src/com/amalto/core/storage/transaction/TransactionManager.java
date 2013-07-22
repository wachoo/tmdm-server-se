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

/**
 *
 */
public interface TransactionManager {
    void init();

    void close();

    /**
     * Creates a new {@link Transaction} with a expected life time.
     *
     * @param lifetime The expected life time of the new transaction.
     * @return A new transaction ready to be used.
     * @see Transaction#include(com.amalto.core.storage.Storage)
     */
    Transaction create(Transaction.Lifetime lifetime);

    /**
     * @param transactionId A transaction id (see {@link Transaction#getId()}.
     * @return The transaction with given id, or <code>null</code> if not found, or <code>null</code> if transaction
     *         is no longer managed by this transaction manager (i.e. it was committed or rollbacked).
     */
    Transaction get(String transactionId);

    /**
     * Removes a transaction from the scope of this transaction manager.
     *
     * @param transaction The transaction to remove.
     */
    void remove(Transaction transaction);

    /**
     * @return The current {@link Transaction} associated with the current thread. If no transaction is currently linked
     * to current thread, a new transaction with a life time {@link com.amalto.core.storage.transaction.Transaction.Lifetime#AD_HOC}
     * is created.
     */
    Transaction currentTransaction();

    /**
     * Links ('associate') current thread to the <code>transaction</code>. All consecutive calls to {@link #currentTransaction()}
     * will return the <code>transaction</code> parameter.
     * @param transaction A transaction to link to {@link Thread#currentThread()}
     * @return The associated transaction.
     */
    Transaction associate(Transaction transaction);

    /**
     * <p>
     * Removes link from ('dissociate') current thread to the <code>transaction</code>. All consecutive calls to {@link #hasTransaction()}}
     * will return <code>false</code>.
     * </p>
     * <p>
     * If <code>transaction</code> is not associated with current thread, calling this method has no effect.
     * </p>
     * @param transaction A transaction linked to {@link Thread#currentThread()}
     */
    void dissociate(Transaction transaction);

    /**
     * @return <code>true</code> if a transaction is associated with current thread, <code>false</code> otherwise.
     */
    boolean hasTransaction();
}
