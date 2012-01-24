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

    private InitialContext ic;

    public ServiceLocator() throws IllegalStateException {
        try {
            ic = new InitialContext();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
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
}
