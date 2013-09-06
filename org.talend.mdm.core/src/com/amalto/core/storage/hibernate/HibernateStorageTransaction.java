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

import com.amalto.core.load.io.ResettableStringWriter;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.exception.ConstraintViolationException;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import com.amalto.core.storage.record.ObjectDataRecordReader;
import com.amalto.core.storage.transaction.StorageTransaction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.hibernate.FlushMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.engine.EntityKey;
import org.hibernate.impl.SessionImpl;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;

import java.util.Set;

class HibernateStorageTransaction extends StorageTransaction {

    private static final Logger LOGGER = Logger.getLogger(HibernateStorageTransaction.class);
    protected static final int TRANSACTION_DUMP_MAX = 10;

    private final HibernateStorage storage;

    private final Session session;

    public HibernateStorageTransaction(HibernateStorage storage, Session session) {
        this.storage = storage;
        this.session = session;
    }

    @Override
    public Storage getStorage() {
        return storage;
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
            } catch (Exception e) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Transaction failed, dumps transaction content for diagnostic.");
                    dumpTransactionContent(session, storage); // Dumps all the faulty session information.
                }
                if (e instanceof org.hibernate.exception.ConstraintViolationException) {
                    throw new ConstraintViolationException(e);
                } else {
                    throw new RuntimeException(e);
                }
            }
            super.commit();
            if (session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Dumps all current entities in <code>session</code> using data model information from <code>storage</code>.
     *
     * @param session The Hibernate session that failed to be committed.
     * @param storage A {@link com.amalto.core.storage.hibernate.HibernateStorage} that can be used to retrieve metadata information for all objects in
     *                <code>session</code>.
     */
    private static void dumpTransactionContent(Session session,
                                               HibernateStorage storage) {
        Level currentLevel = Level.INFO;
        if (LOGGER.isEnabledFor(currentLevel)) {
            Set<EntityKey> failedKeys = session.getStatistics().getEntityKeys();
            int i = 1;
            ObjectDataRecordReader reader = new ObjectDataRecordReader();
            MappingRepository mappingRepository = storage.getTypeEnhancer().getMappings();
            StorageClassLoader classLoader = storage.getClassLoader();
            DataRecordXmlWriter writer = new DataRecordXmlWriter();
            ResettableStringWriter xmlContent = new ResettableStringWriter();
            ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
            for (EntityKey failedKey : failedKeys) {
                String entityTypeName = StringUtils.substringAfterLast(failedKey.getEntityName(), "."); //$NON-NLS-1$
                LOGGER.log(currentLevel, "Entity #" + i++ + " (type=" + entityTypeName + ", id=" + failedKey.getIdentifier() + ")");
                try {
                    Thread.currentThread().setContextClassLoader(classLoader);
                    Wrapper o = (Wrapper) ((SessionImpl) session).getPersistenceContext().getEntity(failedKey);
                    if (o != null) {
                        ComplexTypeMetadata type = classLoader.getTypeFromClass(classLoader.loadClass(failedKey.getEntityName()));
                        if (type != null) {
                            DataRecord record = reader.read(mappingRepository.getMappingFromDatabase(type), o);
                            writer.write(record, xmlContent);
                            LOGGER.log(currentLevel, xmlContent);
                        } else {
                            LOGGER.warn("Could not find data model type for object " + o);
                        }
                    } else {
                        LOGGER.warn("Could not find an object for entity " + failedKey);
                    }
                } catch (ObjectNotFoundException missingRefException) {
                    LOGGER.log(currentLevel, "Can not log entity: contains a unresolved reference to '"
                            + missingRefException.getEntityName() + "' with id '"
                            + missingRefException.getIdentifier() + "'");
                } catch (Exception serializationException) {
                    LOGGER.log(currentLevel, "Failed to log entity content for type " + entityTypeName + " (enable DEBUG for exception details).");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Serialization exception occurred.", serializationException);
                    }
                } finally {
                    xmlContent.reset();
                    Thread.currentThread().setContextClassLoader(previousClassLoader);
                }
                if (i > TRANSACTION_DUMP_MAX) {
                    if (!LOGGER.isDebugEnabled()) {
                        LOGGER.log(currentLevel, "and " + (failedKeys.size() - i) + " more... (enable DEBUG for full dump)");
                        return;
                    } else {
                        currentLevel = Level.DEBUG; // Continue the dump but with a DEBUG level
                    }
                }
            }
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
                } else {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Transaction is being rollbacked. Transaction content:");
                        dumpTransactionContent(session, storage); // Dumps all content in the current transaction.
                    }
                }
                session.clear();
                if (!transaction.wasRolledBack()) {
                    transaction.rollback();
                } else {
                    LOGGER.warn("Transaction was already rollbacked.");
                }
            } finally {
                super.rollback();
                session.close();
            }
        }
    }

    public Session getSession() {
        return session;
    }
}
