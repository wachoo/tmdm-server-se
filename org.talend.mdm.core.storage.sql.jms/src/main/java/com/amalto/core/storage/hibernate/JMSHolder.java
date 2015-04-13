package com.amalto.core.storage.hibernate;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Properties;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.hibernate.search.backend.WorkType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

class JMSHolder {

    private static final String DEFAULT_CONNECTION_FACTORY = "ConnectionFactory"; //$NON-NLS-1$

    private final static String DEFAULT_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory"; //$NON-NLS-1$

    private final static String DEFAULT_PROVIDER_URL = "tcp://localhost:61616"; //$NON-NLS-1$

    private static final String DEFAULT_TOPIC = "mdm.search.lucene"; //$NON-NLS-1$

    private static final Logger LOG = Logger.getLogger(JMSHolder.class);

    private static final java.util.Queue<StorageWork> workQueue = new LinkedList<StorageWork>();

    public static String messageCorrelationId;

    public static TopicSession session;

    public static Topic luceneWorkTopic;

    private static TopicPublisher publisher;

    private static boolean initDone = false;

    synchronized static void init() {
        if (!initDone) {
            String ipAddress = "UNKNOWN"; //$NON-NLS-1$
            try {
                ipAddress = java.net.InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                LOG.error("Cannot get host name. JMS replication will not work correctly.", e);
            }
            final String workingDirectory = System.getProperty("user.dir"); //$NON-NLS-1$
            messageCorrelationId = ipAddress + "$" + workingDirectory;
            LOG.info("MDM Node message identifier: " + messageCorrelationId); //$NON-NLS-1$
            try {
                // Connects to JMS
                Properties props = new Properties();
                final Properties properties = MDMConfiguration.getConfiguration();
                final String contextFactory = (String) properties.get("hibernate.search.jms.context_factory"); //$NON-NLS-1$
                final String providerUrl = (String) properties.get("hibernate.search.jms.provider_url"); //$NON-NLS-1$
                final String topic = (String) properties.get("hibernate.search.jms.topic"); //$NON-NLS-1$
                final String connectionFactory = (String) properties.get("hibernate.search.jms.connection_factory"); //$NON-NLS-1$
                props.setProperty(Context.INITIAL_CONTEXT_FACTORY, contextFactory == null ? DEFAULT_CONTEXT_FACTORY : contextFactory);
                props.setProperty(Context.PROVIDER_URL, providerUrl == null ? DEFAULT_PROVIDER_URL : providerUrl);
                props.setProperty("topic.topicName", topic == null ? DEFAULT_TOPIC : topic); //$NON-NLS-1$ //$NON-NLS-2$
                Context ctx = new InitialContext(props);
                // Look up for topic
                TopicConnectionFactory factory = (TopicConnectionFactory) ctx
                        .lookup(connectionFactory == null ? DEFAULT_CONNECTION_FACTORY : connectionFactory);
                TopicConnection conn = factory.createTopicConnection();
                conn.setClientID(messageCorrelationId);
                luceneWorkTopic = (Topic) ctx.lookup("topicName"); //$NON-NLS-1$
                session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
                // Create publisher (for Lucene index update events).
                publisher = session.createPublisher(luceneWorkTopic);
                // Create subscriber (to listen for other MDM node events).
                TopicSubscriber subscriber = session.createDurableSubscriber(JMSHolder.luceneWorkTopic, messageCorrelationId);
                subscriber.setMessageListener(new SearchIndexListener());
                conn.start(); // <- Start receiving messages
            } catch (Exception e) {
                throw new RuntimeException("Unable to connect to JMS.", e);
            }
            initDone = true;
        }
    }

    public static <T> void addWorkToQueue(final Class<T> entityClass, final Serializable id, String storageName,
            final WorkType workType) {
        workQueue.offer(new StorageWork<T>(entityClass, id, workType, storageName));
    }

    public static void sendWorkToTopic() {
        try {
            if (!workQueue.isEmpty()) {
                // Create & send message
                final Message message = session.createObjectMessage((Serializable) workQueue);
                message.setStringProperty("storageName", workQueue.peek().getStorageName()); //$NON-NLS-1$
                message.setJMSCorrelationID(messageCorrelationId);
                publisher.send(luceneWorkTopic, message);
            }
        } catch (Exception exception) {
            LOG.warn("Failed to send Lucene work to pre-defined topic (enable DEBUG for details).");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failed to send Lucene work to pre-defined topic due to exception.", exception);
            }
        } finally {
            workQueue.clear();
        }
    }
}