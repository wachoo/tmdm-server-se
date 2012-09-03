/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.server;

import com.amalto.commons.core.datamodel.synchronization.DMUpdateEvent;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.storage.Storage;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.*;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class DataModelUpdateMDB implements MessageDrivenBean, MessageListener {

    private static final Logger LOGGER = Logger.getLogger(DataModelUpdateMDB.class);

    private static final Map<String, XSystemObjects> SYSTEM_OBJECTS = XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);

    private QueueConnection connection;

    private QueueSession session;

    public DataModelUpdateMDB() {
    }

    public void setMessageDrivenContext(MessageDrivenContext ctx) {
    }

    public void ejbCreate() {
        try {
            InitialContext initialContext = new InitialContext();
            Object tmp = initialContext.lookup("java:/XAConnectionFactory"); //$NON-NLS-1$
            QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
            connection = qcf.createQueueConnection();
            session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            connection.start();
        } catch (Exception e) {
            throw new EJBException("Failed to init DataModelUpdateMDB.", e);
        }
    }

    public void ejbRemove() {
        try {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            LOGGER.error("Failed to remove DataModelUpdateMDB.", e);
        }
    }

    public void onMessage(Message msg) {
        try {
            if (msg instanceof ObjectMessage) {
                Serializable object = ((ObjectMessage) msg).getObject();

                if (object instanceof DMUpdateEvent) {
                    DMUpdateEvent updateEvent = (DMUpdateEvent) object;
                    if (DMUpdateEvent.EVENT_TYPE_UPDATE.equals(updateEvent.getEventType())) {
                        String updatedDataModelName = updateEvent.getDataModelPK();

                        if (!SYSTEM_OBJECTS.containsKey(updatedDataModelName)) { // Do not update system data clusters
                            Server server = ServerContext.INSTANCE.get();
                            MetadataRepositoryAdmin metadataRepositoryAdmin = server.getMetadataRepositoryAdmin();
                            metadataRepositoryAdmin.remove(updatedDataModelName);
                            MetadataRepository repository = metadataRepositoryAdmin.get(updatedDataModelName);

                            StorageAdmin storageAdmin = server.getStorageAdmin();
                            Storage storage = storageAdmin.get(updatedDataModelName);
                            if (storage != null) {
                                // Storage already exists so update it.
                                Set<FieldMetadata> indexedFields = metadataRepositoryAdmin.getIndexedFields(updatedDataModelName);
                                storage.prepare(repository, indexedFields, true, false);
                            } else {
                                LOGGER.warn("No SQL storage defined for data model '" + updatedDataModelName + "'. No SQL storage to update.");
                            }

                            Storage stagingStorage = storageAdmin.get(updatedDataModelName + StorageAdmin.STAGING_SUFFIX);
                            if (stagingStorage != null) {
                                // Storage already exists so update it.
                                Set<FieldMetadata> indexedFields = metadataRepositoryAdmin.getIndexedFields(updatedDataModelName);
                                stagingStorage.prepare(repository, indexedFields, true, false);
                            } else {
                                LOGGER.warn("No SQL staging storage defined for data model '" + updatedDataModelName + "'. No SQL staging storage to update.");
                            }
                        }
                    }
                }
            }
        } catch (JMSException e) {
            LOGGER.error("Error during message receive", e);
        }
    }

}
