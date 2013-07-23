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

package com.amalto.core.storage.hibernate;

import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.exception.ConstraintViolationException;
import com.amalto.core.storage.transaction.StorageTransaction;
import com.amalto.core.storage.transaction.TransactionManager;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

public class HibernateStorageTransaction implements StorageTransaction {

    private static final Logger LOGGER = Logger.getLogger(HibernateStorageTransaction.class);

    private final Storage storage;

    private final Session session;

    private boolean isAutonomous;

    public HibernateStorageTransaction(Storage storage, Session session) {
        this.storage = storage;
        this.session = session;
    }

    @Override
    public void begin() {
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            session.beginTransaction();
            session.setFlushMode(FlushMode.AUTO);
        }
    }

    @Override
    public void commit() {
        if (isAutonomous) {
            Transaction transaction = session.getTransaction();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[" + this + "] Transaction #" + transaction.hashCode() + " -> Commit "
                        + session.getStatistics().getEntityCount() + " record(s).");
            }
            if (!transaction.isActive()) {
                throw new IllegalStateException("Can not commit transaction, no transaction is active.");
            }
            try {
                if (!transaction.wasCommitted()) {
                    transaction.commit();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("[" + this + "] Transaction #" + transaction.hashCode() + " -> Commit done.");
                    }
                } else {
                    LOGGER.warn("Transaction was already committed.");
                }
            } catch (ConstraintViolationException e) {
                throw new ConstraintViolationException(e);
            }
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            transactionManager.currentTransaction().exclude(storage);
            session.close();
        }
    }

    @Override
    public void rollback() {
        if (isAutonomous) {
            try {
                Transaction transaction = session.getTransaction();
                if (!transaction.isActive()) {
                    LOGGER.warn("Can not rollback transaction, no transaction is active.");
                    return;
                }
                session.clear();
                if (!transaction.wasRolledBack()) {
                    transaction.rollback();
                } else {
                    LOGGER.warn("Transaction was already rollbacked.");
                }
            } finally {
                TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
                transactionManager.currentTransaction().exclude(storage);
                session.close();
            }
        }
    }

    @Override
    public StorageTransaction autonomous() {
        isAutonomous = true;
        return this;
    }

    @Override
    public StorageTransaction dependent() {
        isAutonomous = false;
        return this;
    }

    public Session getSession() {
        return session;
    }
}
