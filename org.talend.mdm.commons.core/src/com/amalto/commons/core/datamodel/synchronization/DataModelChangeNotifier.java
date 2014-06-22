// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.commons.core.datamodel.synchronization;

import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.log4j.Logger;

public class DataModelChangeNotifier {

    private static final String topicName = "topic/testTopic"; // FIXME: do not use the test topic

    private static final String topicConnectionName = "java:/JmsXA";

    private static final Logger LOGGER = Logger.getLogger(DataModelChangeNotifier.class);

    private static final ServiceLocator locator = new ServiceLocator();

    private final TopicConnectionFactory topicConnectionFactory;

    private final Topic topic;

    TopicConnection topicConnection = null;

    TopicSession topicSession = null;

    TopicPublisher topicPublisher = null;

    private final List<DMUpdateEvent> messageList = new LinkedList<DMUpdateEvent>();

    public DataModelChangeNotifier() {
        try {
            topicConnectionFactory = locator.getTopicConnectionFactory(topicConnectionName);
            topic = locator.getTopic(topicName);
        } catch (ServiceLocatorException e) {
            throw new IllegalStateException("Can not find the target objects, please make sure they already bounded!", e);
        }
    }

    public void addUpdateMessage(DMUpdateEvent dmUpdateEvent) {
        synchronized (messageList) {
            messageList.add(dmUpdateEvent);
        }
    }

    public void sendMessages() {
        try {
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            if (topicSession == null) {
                LOGGER.error("Can't create JMS session. Event notifications disabled.");
                return;
            }
            topicPublisher = topicSession.createPublisher(topic);
            // send messages
            for (DMUpdateEvent dmUpdateEvent : messageList) {
                ObjectMessage message = topicSession.createObjectMessage(dmUpdateEvent);
                topicPublisher.publish(message);
            }
        } catch (JMSException e) {
            LOGGER.error("Unexpected JMS exception.", e);
        } finally {
            if (topicConnection != null) {
                try {
                    topicConnection.close();
                } catch (JMSException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("JMS close issue.", e);
                    }
                }
            }
        }
    }

}
