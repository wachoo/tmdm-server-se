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

import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * JMS Service Locator
 */
public class ServiceLocator {

    private final InitialContext ic;

    public ServiceLocator() throws IllegalStateException {
        try {
            ic = new InitialContext();
        } catch (Exception e) {
            throw new IllegalStateException("Can't acquire initial context", e);
        }
    }

    public TopicConnectionFactory getTopicConnectionFactory(String topicConnFactoryName) throws ServiceLocatorException {
        TopicConnectionFactory factory;
        try {
            factory = (TopicConnectionFactory) ic.lookup(topicConnFactoryName);
        } catch (NamingException e) {
            throw new ServiceLocatorException(e);
        }
        return factory;
    }

    public Topic getTopic(String topicName) throws ServiceLocatorException {
        Topic topic;
        try {
            topic = (Topic) ic.lookup(topicName);
        } catch (Exception e) {
            throw new ServiceLocatorException(e);
        }
        return topic;
    }
}
