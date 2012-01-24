// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;

import org.apache.log4j.Logger;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class DatamodelChangeNotifier {

    private static final String topicName = "topic/testTopic";// FIXME: do not use the test topic

    private static final String topicConnectionName = "java:/JmsXA";

    Context jndiContext = null;

    TopicConnectionFactory topicConnectionFactory = null;

    TopicConnection topicConnection = null;

    TopicSession topicSession = null;

    Topic topic = null;

    TopicPublisher topicPublisher = null;

    private ServiceLocator locacor = new ServiceLocator();

    private List<DMUpdateEvent> messageList = null;

    /**
     * DOC HSHU DatamodelChangeNotifier constructor comment.
     */
    public DatamodelChangeNotifier() {
        locacor = new ServiceLocator();
        messageList = new ArrayList<DMUpdateEvent>();
        init();
    }

    public static void main(String[] args) {
        DatamodelChangeNotifier dmUpdateEventNotifer = new DatamodelChangeNotifier();
        dmUpdateEventNotifer.addUpdateMessage(new DMUpdateEvent("DStar"));
        dmUpdateEventNotifer.addUpdateMessage(new DMUpdateEvent("Test"));
        dmUpdateEventNotifer.sendMessages();
    }

    private boolean init() {

        try {
            topicConnectionFactory = locacor.getTopicConnectionFactory(topicConnectionName);
            topic = (Topic) locacor.getTopic(topicName);
        } catch (ServiceLocatorException e) {
            Logger.getLogger(this.getClass()).info(
                    "Can not find the target objects, please make sure they already bounded! " + e.toString());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void addUpdateMessage(DMUpdateEvent dmUpdateEvent) {
        this.messageList.add(dmUpdateEvent);
    }

    public void sendMessages() {

        try {
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topicPublisher = topicSession.createPublisher(topic);

            // send messages
            for (DMUpdateEvent dmUpdateEvent : messageList) {

                ObjectMessage message = topicSession.createObjectMessage(dmUpdateEvent);
                topicPublisher.publish(message);

            }

            /*
             * send an empty message to stop
             */
            // topicPublisher.publish(topicSession.createMessage());

        } catch (JMSException e) {
            Logger.getLogger(this.getClass()).error("Caught: " + e.toString());
        } finally {
            if (topicConnection != null) {
                try {
                    topicConnection.close();
                } catch (JMSException e) {
                }
            }
        }
    }

}
