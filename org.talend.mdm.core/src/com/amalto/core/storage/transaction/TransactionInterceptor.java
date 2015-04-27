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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.service.model.MessageInfo;
import org.w3c.dom.Element;

import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import com.amalto.core.server.ServerContext;

public abstract class TransactionInterceptor extends AbstractPhaseInterceptor<Message> {

    protected static final ThreadLocal<TransactionState> states = new ThreadLocal<>();

    private static final String TRANSACTION_ID = "transaction-id"; //$NON-NLS-1$

    private static final String LOGOUT_OPERATION_NAME = "WSLogout"; //$NON-NLS-1$

    private static final String PING_OPERATION_NAME = "ping"; //$NON-NLS-1$

    public TransactionInterceptor(String phase) {
        super(phase);
    }

    public TransactionState getState(Message message) throws Fault {
        if (message != null) {
            final SoapMessage soapMessage = (SoapMessage) message;
            final MessageInfo messageInfo = soapMessage.get(MessageInfo.class);
            if (messageInfo != null) {
                String messageOperationName = messageInfo.getName().getLocalPart();
                if (PING_OPERATION_NAME.equals(messageOperationName) || LOGOUT_OPERATION_NAME.equals(messageOperationName)) {
                    // No need for transactions for ping and logout.
                    return NoOpTransactionState.INSTANCE;
                }
                final List<Header> headers = soapMessage.getHeaders();
                if (headers != null) {
                    for (Header header : headers) {
                        QName name = header.getName();
                        if (name != null && SkipAttributeDocumentBuilder.TALEND_NAMESPACE.equals(name.getNamespaceURI())
                                && TRANSACTION_ID.equals(name.getLocalPart())) {
                            String transactionID = ((Element) header.getObject()).getTextContent();
                            return new ExplicitTransaction(transactionID);
                        }
                    }
                }
            }
        }
        return NoOpTransactionState.INSTANCE;
    }

    protected interface TransactionState {

        void preRequest();

        void postRequest();

        void cancelRequest();
    }

    private static class ExplicitTransaction implements TransactionState {

        private final String transactionID;

        public ExplicitTransaction(String transactionID) {
            this.transactionID = transactionID;
        }

        @Override
        public void preRequest() {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            Transaction transaction = transactionManager.get(transactionID);
            if (transaction == null) {
                transaction = transactionManager.create(Transaction.Lifetime.LONG, transactionID);
                transaction.begin();
            }
            transactionManager.associate(transaction);
        }

        @Override
        public void postRequest() {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            Transaction transaction = transactionManager.get(transactionID);
            if (transaction == null) {
                throw new IllegalStateException("Transaction '" + transactionID + "' no longer exists.");
            }
            transactionManager.dissociate(transaction);
        }

        @Override
        public void cancelRequest() {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            Transaction transaction = transactionManager.get(transactionID);
            if (transaction == null) {
                throw new IllegalStateException("Transaction '" + transactionID + "' no longer exists.");
            }
            transactionManager.dissociate(transaction);
        }
    }

    private static class NoOpTransactionState implements TransactionState {

        private static final TransactionState INSTANCE = new NoOpTransactionState();

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
}
