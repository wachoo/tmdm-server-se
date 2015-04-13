package com.amalto.core.storage.hibernate;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.transaction.StorageTransaction;
import com.amalto.core.storage.transaction.TransactionManager;
import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.backend.AddLuceneWork;
import org.hibernate.search.backend.DeleteLuceneWork;
import org.hibernate.search.backend.LuceneWork;

public class SearchIndexListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(SearchIndexListener.class);

    public void onMessage(Message message) {
        try {
            if (isValidMessage(message)) {
                final String storageName = message.getStringProperty("storage");//$NON-NLS-1$
                final List<LuceneWork> works = (List<LuceneWork>) message.getObjectProperty("work"); //$NON-NLS-1$
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Received message contains" + ((ObjectMessage) message).getObject().getClass());
                    LOG.debug("Received message contains " + works.size() + "message(s)");
                }
                rebuildIndexInBatchMode(storageName, works);
            }
        } catch (JMSException e) {
            LOG.debug("Cannot build index.", e);
        }
    }

    public synchronized void rebuildIndexInBatchMode(String storageName, List<LuceneWork> workQueue) {
        // Get a session for storage to index
        final Server context = ServerContext.INSTANCE.get();
        final TransactionManager transactionManager = context.getTransactionManager();
        final StorageAdmin storageAdmin = context.getStorageAdmin();
        StorageType storageType = StorageType.MASTER;
        if (storageName.endsWith(StorageAdmin.STAGING_SUFFIX)) {
            storageType = StorageType.STAGING;
        }
        final Storage storage = storageAdmin.get(storageName, storageType, null);
        final StorageTransaction storageTransaction = transactionManager.currentTransaction().include(storage);
        if (HibernateStorageTransaction.class.isAssignableFrom(storageTransaction.getClass())) {
            throw new IllegalArgumentException("Storage is not a RDBMS storage (got a " + storageTransaction.getClass() + " transaction).");
        }
        // Perform full text index tasks on this session
        final Session session = ((HibernateStorageTransaction) storageTransaction).getSession();
        final FullTextSession fullTextSession = Search.getFullTextSession(session);
        fullTextSession.setFlushMode(FlushMode.MANUAL);
        fullTextSession.setCacheMode(CacheMode.IGNORE);
        final Transaction tx = fullTextSession.beginTransaction();
        for (LuceneWork luceneWork : workQueue) {
            if (luceneWork instanceof DeleteLuceneWork) {
                LOG.info("Remove index for class " + luceneWork.getEntityClass() + " with id = " + luceneWork.getIdInString());
                fullTextSession.purge(luceneWork.getEntityClass(), luceneWork.getId());
                fullTextSession.flushToIndexes();
            } else if (luceneWork instanceof AddLuceneWork) {
                LOG.info("Rebuild index for class " + luceneWork.getEntityClass() + " with id = " + luceneWork.getIdInString());
                Object indexedObject = fullTextSession.load(luceneWork.getEntityClass(), luceneWork.getId());
                if (indexedObject != null) {
                    fullTextSession.index(indexedObject);
                    fullTextSession.flushToIndexes();
                }
            }
        }
        fullTextSession.flushToIndexes();
        fullTextSession.clear();
        tx.commit();
        fullTextSession.close();
    }

    private static boolean isValidMessage(Message message) throws JMSException {
        return message instanceof ObjectMessage && !message.getJMSCorrelationID().equals(JMSHolder.messageCorrelationId)
                && (((ObjectMessage) message).getObject() != null);
    }
}
