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

import javax.ws.rs.*;

@Path("/transactions") //$NON-NLS-1$
public class TransactionService {

    /**
     * Starts a new transaction and returns the id of the newly created transaction.
     * @return A transaction id (as UUID).
     */
    @PUT
    @Path("/") //$NON-NLS-1$
    public String begin() {
        TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = transactionManager.create(Transaction.Lifetime.LONG);
        return transaction.getId();
    }

    /**
     * Associate calling thread with transaction <code>transactionId</code>.
     * @param transactionId A transaction id.
     */
    @GET
    @Path("{id}/") //$NON-NLS-1$
    public void resume(@PathParam("id") String transactionId) { //$NON-NLS-1$
        TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = transactionManager.get(transactionId);
        if (transaction != null) {
            transactionManager.associate(transaction);
        }
    }

    /**
     * Commit the changes in transaction <code>transactionId</code>.
     * @param transactionId A valid transaction id.
     */
    @POST
    @Path("{id}/") //$NON-NLS-1$
    public void commit(@PathParam("id") String transactionId) { //$NON-NLS-1$
        TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = transactionManager.get(transactionId);
        if (transaction != null) {
            transaction.commit();
        }
    }

    /**
     * Cancels (rollback) all changes done in <code>transactionId</code>.
     * @param transactionId A transaction id.
     */
    @DELETE
    @Path("{id}/") //$NON-NLS-1$
    public void rollback(@PathParam("id") String transactionId) { //$NON-NLS-1$
        TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = transactionManager.get(transactionId);
        if (transaction != null) {
            transaction.rollback();
        }
    }
}
