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

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.amalto.commons.core.datamodel.synchronization.DMUpdateEvent;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.storage.Storage;
import com.amalto.core.util.Util;

/**
 *
 */
public class DataModelUpdateMDB implements MessageDrivenBean, MessageListener {

    private static final Logger LOGGER = Logger.getLogger(DataModelUpdateMDB.class);

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
                    String updatedDataModelName = updateEvent.getDataModelPK();
                    if (DataModelPOJO.isUserDataModel(updatedDataModelName)) { // Do not update system data clusters
                        if (DMUpdateEvent.EVENT_TYPE_UPDATE.equals(updateEvent.getEventType())) {
                            // TMDM-4621: Update operation has to be synchronous
                            // No operation, move function to com.amalto.core.objects.datamodel.ejb.DataModelPOJO.store(String)
                        } else if (DMUpdateEvent.EVENT_TYPE_INIT.equals(updateEvent.getEventType())) {
                            try {
                                DataClusterCtrlLocal dataClusterControl = Util.getDataClusterCtrlLocal();
                                // Only create storage when both (data model) and (data container) exist.
                                if (dataClusterControl.existsDataCluster(new DataClusterPOJOPK(updatedDataModelName)) != null) {
                                    Server server = ServerContext.INSTANCE.get();
                                    StorageAdmin storageAdmin = server.getStorageAdmin();
                                    storageAdmin.create(updatedDataModelName, updatedDataModelName, Storage.DEFAULT_DATA_SOURCE_NAME, null);
                                }
                            } catch (Exception e) {
                                LOGGER.error("Exception occurred during data model initialization.", e);
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
