package com.amalto.core.storage.hibernate;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.hibernate.search.backend.Work;
import org.hibernate.search.backend.WorkQueue;
import org.hibernate.search.backend.WorkType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

public class JMSHolder {

    public static String messageCorrelationId;

    public static final TopicSession session;

    private static final String DEFAULT_CONNECTION_FACTORY = "ConnectionFactory"; //$NON-NLS-1$

    private final static String DEFAULT_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory"; //$NON-NLS-1$

    private final static String DEFAULT_PROVIDER_URL = "tcp://localhost:61616"; //$NON-NLS-1$

    private static final String DEFAULT_TOPIC = "mdm/search/lucene"; //$NON-NLS-1$

    private static final Logger LOG = Logger.getLogger(JMSHolder.class);

    private static final TopicPublisher publisher;

    private static WorkQueue workQueue;

    private static Topic luceneWorkTopic;

    static {
        String ipAddress = "UNKNOWN"; //$NON-NLS-1$
        try {
            ipAddress = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.error("Cannot get host name. JMS replication will not work correctly.", e);
        }
        final String workingDirectory = System.getProperty("user.dir");
        JMSHolder.messageCorrelationId = ipAddress + "$" + workingDirectory; // TODO: hashCode? Is this safe across multiple JVMs
        workQueue = new WorkQueue();
        LOG.info("JMS message identifier: " + messageCorrelationId); //$NON-NLS-1$
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
            Context ctx = new InitialContext(props);
            // Look up for topic
            TopicConnectionFactory factory = (TopicConnectionFactory) ctx
                    .lookup(connectionFactory == null ? DEFAULT_CONNECTION_FACTORY : connectionFactory);
            TopicConnection conn = factory.createTopicConnection();
            luceneWorkTopic = (Topic) ctx.lookup(topic == null ? DEFAULT_TOPIC : topic);
            session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
            publisher = session.createPublisher(luceneWorkTopic);
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect to JMS.", e);
        }
    }

    public static <T> void addWorkToQueue(final Class<T> entityClass, final Serializable id, String name, final WorkType workType) {
        JMSHolder.workQueue.add(new MDMWork(entityClass, id, workType, name));
    }

    public static void sendWorkToTopic() {
        try {
            final List<Work> luceneWork = JMSHolder.workQueue.getQueue();
            if (luceneWork != null && !luceneWork.isEmpty()) {
                // Create & send message
                final Message message = session.createMessage();
                message.setStringProperty("storageName", ((MDMWork) luceneWork).getStorageName()); //$NON-NLS-1$
                message.setObjectProperty("work", luceneWork); //$NON-NLS-1$
                message.setJMSCorrelationID(messageCorrelationId);
                publisher.send(luceneWorkTopic, message);
            }
        } catch (Exception exception) {
            LOG.warn("Failed to send Lucene work to pre-defined topic");
        } finally {
            JMSHolder.workQueue.clear();
        }
    }
}