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

import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.transaction.StorageTransaction;

class SearchIndexListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(SearchIndexListener.class);

    SearchIndexListener() {
    }

    private static boolean isValidMessage(Message message) throws JMSException {
        return message instanceof ObjectMessage // It's a ObjectMessage
                && !message.getJMSCorrelationID().equals(JMSHolder.messageCorrelationId) // ... we are *not* the sender
                                                                                         // of the message ...
                && (((ObjectMessage) message).getObject() != null); // ... and it has some content
    }

    @Override
    public void onMessage(Message message) {
        try {
            final String storageName = message.getStringProperty("storageName");//$NON-NLS-1$
            final Server context = ServerContext.INSTANCE.get();
            final StorageAdmin storageAdmin = context.getStorageAdmin();
            StorageType storageType = StorageType.MASTER;
            if (storageName.endsWith(StorageAdmin.STAGING_SUFFIX)) {
                storageType = StorageType.STAGING;
            }
            final Storage storage = storageAdmin.get(storageName, storageType, null);
            final StorageTransaction storageTransaction = storage.newStorageTransaction();
            if (!HibernateStorageTransaction.class.isAssignableFrom(storageTransaction.getClass())) {
                throw new IllegalArgumentException("Transaction is not a RDBMS transaction (got a "
                        + storageTransaction.getClass() + " transaction).");
            }
            if (!(storage.asInternal() instanceof HibernateStorage)) {
                throw new IllegalArgumentException("Storage is not a RDBMS storage (got a "
                        + storage.asInternal().getClass() + ").");
            }
            final StorageClassLoader classLoader = ((HibernateStorage) storage.asInternal()).getClassLoader();
            final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                // Set context class loader for indexing
                Thread.currentThread().setContextClassLoader(classLoader);
                if (isValidMessage(message)) {
                    final List<StorageWork> works = (List<StorageWork>) ((ObjectMessage) message).getObject(); //$NON-NLS-1$
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Received message for storage '" + storageName + "' (contains " + works.size() + " work(s))");
                    }
                    // Get a session for storage to index
                    // Perform full text index tasks on this session
                    synchronized (this) {
                        // Start transaction
                        storageTransaction.begin();
                        final Session session = ((HibernateStorageTransaction) storageTransaction).getSession();
                        final FullTextSession fullTextSession = Search.getFullTextSession(session);
                        fullTextSession.setFlushMode(FlushMode.MANUAL);
                        fullTextSession.setCacheMode(CacheMode.IGNORE);
                        final Transaction tx = fullTextSession.beginTransaction();
                        for (StorageWork luceneWork : works) {
                            switch (luceneWork.getType()) {
                            case ADD:
                            case UPDATE:
                            case COLLECTION:
                            case INDEX:
                                Object indexedObject = fullTextSession.load(luceneWork.getEntityType(), luceneWork.getId());
                                if (indexedObject != null) {
                                    fullTextSession.index(indexedObject);
                                    fullTextSession.flushToIndexes();
                                }
                                break;
                            case PURGE:
                            case DELETE:
                            case PURGE_ALL:
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Remove index for class " + luceneWork.getEntityType() + " with id = "
                                            + luceneWork.getId());
                                }
                                fullTextSession.purge(luceneWork.getEntityType(), luceneWork.getId());
                                fullTextSession.flushToIndexes();
                                break;
                            }
                        }
                        fullTextSession.flushToIndexes();
                        fullTextSession.clear();
                        tx.commit();
                        storageTransaction.commit();
                    }
                }
            } finally {
                Thread.currentThread().setContextClassLoader(previousClassLoader);
            }
        } catch (JMSException e) {
            LOG.debug("Cannot build index.", e);
        }
    }
}
