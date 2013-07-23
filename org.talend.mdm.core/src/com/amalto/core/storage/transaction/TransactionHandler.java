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

import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import com.amalto.core.server.ServerContext;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.*;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import java.util.Iterator;

public class TransactionHandler implements Handler {

    private static final Logger LOGGER = Logger.getLogger(TransactionHandler.class);

    private static final String TRANSACTION_ID = "transaction-id"; //$NON-NLS-1$

    private static TransactionState getState(MessageContext messageContext) {
        try {
            if (messageContext instanceof SOAPMessageContext) {
                TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
                SOAPMessage message = ((SOAPMessageContext) messageContext).getMessage();
                if (message != null) {
                    SOAPHeader soapHeader = message.getSOAPHeader();
                    if (soapHeader != null) {
                        Iterator iterator = soapHeader.extractAllHeaderElements();
                        while (iterator.hasNext()) {
                            SOAPHeaderElement element = (SOAPHeaderElement) iterator.next();
                            Name name = element.getElementName();
                            if (name != null
                                    && SkipAttributeDocumentBuilder.TALEND_NAMESPACE.equals(name.getURI())
                                    && TRANSACTION_ID.equals(name.getLocalName())) {
                                String transactionID = element.getValue();
                                if(transactionManager.get(transactionID) != null) {
                                    return new ExplicitTransaction(transactionID);
                                } else {
                                    LOGGER.warn("Transaction #" + transactionID + " does not exist or no longer exists."); // TODO Warn or exception?
                                    return new ImplicitTransaction();
                                }
                            }
                        }
                    }
                }
            }
            return new ImplicitTransaction();
        } catch (SOAPException e) {
            LOGGER.error("Unexpected SOAP handler exception.", e);
            return new ImplicitTransaction();
        }
    }

    @Override
    public QName[] getHeaders() {
        return new QName[] {new QName(SkipAttributeDocumentBuilder.TALEND_NAMESPACE, TRANSACTION_ID)};
    }

    @Override
    public void init(HandlerInfo handlerInfo) throws JAXRPCException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws JAXRPCException, SOAPFaultException {
        TransactionState state = getState(messageContext);
        state.preRequest();
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        TransactionState state = getState(messageContext);
        state.postRequest();
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        TransactionState state = getState(messageContext);
        state.cancelRequest();
        return true;
    }

    static interface TransactionState {
        void preRequest();

        void postRequest();

        void cancelRequest();
    }

    static class ImplicitTransaction implements TransactionState {

        private Transaction currentTransaction;

        @Override
        public void preRequest() {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            currentTransaction = transactionManager.create(Transaction.Lifetime.LONG);
            transactionManager.associate(currentTransaction);
        }

        @Override
        public void postRequest() {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            transactionManager.currentTransaction().commit();
        }

        @Override
        public void cancelRequest() {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            transactionManager.currentTransaction().rollback();
        }
    }

    static class ExplicitTransaction implements TransactionState {

        private final String transactionID;

        public ExplicitTransaction(String transactionID) {
            this.transactionID = transactionID;
        }

        @Override
        public void preRequest() {
            TransactionManager transactionManager = ServerContext.INSTANCE.get().getTransactionManager();
            Transaction transaction = transactionManager.get(transactionID);
            transactionManager.associate(transaction);
        }

        @Override
        public void postRequest() {
            // Nothing to do: transaction was explicit started and must be ended by caller.
        }

        @Override
        public void cancelRequest() {
            // Nothing to do: transaction was explicit started and must be ended by caller.
        }
    }
}
