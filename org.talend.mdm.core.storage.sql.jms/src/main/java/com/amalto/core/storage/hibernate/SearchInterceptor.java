package com.amalto.core.storage.hibernate;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

public class SearchInterceptor extends EmptyInterceptor {

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        if (tx.wasCommitted()) {
            JMSHolder.sendWorkToTopic();
        }
    }
}