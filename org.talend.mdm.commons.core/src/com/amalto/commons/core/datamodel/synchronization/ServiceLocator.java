// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * DOC HSHU class global comment. Detailled comment
 * 
 * JMS Service Locator
 */
public class ServiceLocator {

    private static final String NAMING_URL = "localhost";

    private static final String NAMING_INTERFACES = "org.jboss.naming:org.jnp.interfaces";

    private static final String NAMING_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";

    private InitialContext ic;

    public ServiceLocator() {
        ic = this.getInitialContext();
    }

    /**
     * @param qConnFactoryName
     * @return QueueConnectionFactory
     */
    public QueueConnectionFactory getQueueConnectionFactory(String qConnFactoryName) throws ServiceLocatorException {
        QueueConnectionFactory factory = null;
        try {
            factory = (QueueConnectionFactory) ic.lookup(qConnFactoryName);
        } catch (NamingException ne) {
            throw new ServiceLocatorException(ne);
        } catch (Exception e) {
            throw new ServiceLocatorException(e);
        }
        return factory;
    }

    /**
     * @param queueName
     * @return Queue
     */
    public Queue getQueue(String queueName) throws ServiceLocatorException {
        Queue queue = null;
        try {
            queue = (Queue) ic.lookup(queueName);
        } catch (NamingException ne) {
            throw new ServiceLocatorException(ne);
        } catch (Exception e) {
            throw new ServiceLocatorException(e);
        }
        return queue;
    }

    /**
     * @param topicConnFactoryName
     * @return TopicConnectionFactory
     */
    public TopicConnectionFactory getTopicConnectionFactory(String topicConnFactoryName) throws ServiceLocatorException {
        TopicConnectionFactory factory = null;
        try {
            factory = (TopicConnectionFactory) ic.lookup(topicConnFactoryName);
        } catch (NamingException ne) {
            throw new ServiceLocatorException(ne);
        } catch (Exception e) {
            throw new ServiceLocatorException(e);
        }
        return factory;
    }

    /**
     * @param topicName
     * @return Topic
     */
    public Topic getTopic(String topicName) throws ServiceLocatorException {
        Topic topic = null;
        try {
            topic = (Topic) ic.lookup(topicName);
        } catch (NamingException ne) {
            throw new ServiceLocatorException(ne);
        } catch (Exception e) {
            throw new ServiceLocatorException(e);
        }
        return topic;
    }

    /**
     *get context
     */
    public InitialContext getInitialContext() {
        Properties p = new Properties();
        try {
            p.put("java.naming.factory.initial", NAMING_CONTEXT_FACTORY);
            p.put("java.naming.factory.url.pkgs", NAMING_INTERFACES);
            p.put("java.naming.provider.url", NAMING_URL);
            return new InitialContext(p);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
