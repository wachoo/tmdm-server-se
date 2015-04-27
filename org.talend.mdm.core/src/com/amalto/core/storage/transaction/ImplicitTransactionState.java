// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.storage.transaction;

import com.amalto.core.server.ServerContext;

public class ImplicitTransactionState implements TransactionState {

    public static final TransactionState INSTANCE = new ImplicitTransactionState();

    private ImplicitTransactionState() {
    }

    @Override
    public void preRequest() {
    }

    @Override
    public void postRequest() {
        TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
        if (transactionManager.hasTransaction()) {
            throw new IllegalStateException("A non-transactional (auto-commit) operation has an active " +
                    "transaction after operation completion.");
        }
    }

    @Override
    public void cancelRequest() {
        TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
        if (transactionManager.hasTransaction()) {
            throw new IllegalStateException("A non-transactional (auto-commit) operation has an active " +
                    "transaction after operation completion.");
        }
    }
}
