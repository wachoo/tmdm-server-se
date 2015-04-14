package com.amalto.core.storage.hibernate;

import java.util.List;

import javax.jms.*;

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

import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.transaction.StorageTransaction;

class SearchIndexListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(SearchIndexListener.class);

    static {
        try {
            final TopicSubscriber subscriber = JMSHolder.session.createSubscriber(JMSHolder.luceneWorkTopic);
            subscriber.setMessageListener(new SearchIndexListener());
        } catch (JMSException e) {
            throw new RuntimeException("Unable to subscribe to JMS topic.", e);
        }
    }

    private SearchIndexListener() {
    }

    private static boolean isValidMessage(Message message) throws JMSException {
        return message instanceof ObjectMessage && !message.getJMSCorrelationID().equals(JMSHolder.messageCorrelationId)
                && (((ObjectMessage) message).getObject() != null);
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (isValidMessage(message)) {
                final String storageName = message.getStringProperty("storageName");//$NON-NLS-1$
                final List<LuceneWork> works = (List<LuceneWork>) message.getObjectProperty("work"); //$NON-NLS-1$
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Received message for storage '" + storageName + "' (contains " + works.size() + " work(s))");
                }
                // Get a session for storage to index
                // Perform full text index tasks on this session
                synchronized (this) {
                    final Server context = ServerContext.INSTANCE.get();
                    final StorageAdmin storageAdmin = context.getStorageAdmin();
                    StorageType storageType = StorageType.MASTER;
                    if (storageName.endsWith(StorageAdmin.STAGING_SUFFIX)) {
                        storageType = StorageType.STAGING;
                    }
                    final Storage storage = storageAdmin.get(storageName, storageType, null);
                    final StorageTransaction storageTransaction = storage.newStorageTransaction();
                    if (HibernateStorageTransaction.class.isAssignableFrom(storageTransaction.getClass())) {
                        throw new IllegalArgumentException("Transaction is not a RDBMS transaction (got a "
                                + storageTransaction.getClass() + " transaction).");
                    }
                    if (!(storage.asInternal() instanceof HibernateStorage)) {
                        throw new IllegalArgumentException("Storage is not a RDBMS storage (got a "
                                + storage.asInternal().getClass() + ").");
                    }
                    // Start transaction
                    storageTransaction.begin();
                    // Set context class loader for indexing
                    final StorageClassLoader classLoader = ((HibernateStorage) storage.asInternal()).getClassLoader();
                    final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
                    try {
                        Thread.currentThread().setContextClassLoader(classLoader);
                        final Session session = ((HibernateStorageTransaction) storageTransaction).getSession();
                        final FullTextSession fullTextSession = Search.getFullTextSession(session);
                        fullTextSession.setFlushMode(FlushMode.MANUAL);
                        fullTextSession.setCacheMode(CacheMode.IGNORE);
                        final Transaction tx = fullTextSession.beginTransaction();
                        for (LuceneWork luceneWork : works) {
                            if (luceneWork instanceof DeleteLuceneWork) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Remove index for class " + luceneWork.getEntityClass() + " with id = "
                                            + luceneWork.getIdInString());
                                }
                                fullTextSession.purge(luceneWork.getEntityClass(), luceneWork.getId());
                                fullTextSession.flushToIndexes();
                            } else if (luceneWork instanceof AddLuceneWork) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Rebuild index for class " + luceneWork.getEntityClass() + " with id = "
                                            + luceneWork.getIdInString());
                                }
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
                        storageTransaction.commit();
                    } catch (Exception e) {
                        storageTransaction.rollback();
                        throw new RuntimeException("Unable to process index work.", e);
                    } finally {
                        Thread.currentThread().setContextClassLoader(previousClassLoader);
                    }
                }
            }
        } catch (JMSException e) {
            LOG.debug("Cannot build index.", e);
        }
    }
}
