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
package com.amalto.webapp.core.util;

import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.apache.log4j.Logger;

import com.amalto.webapp.core.bean.Configuration;

public final class SessionListener implements ServletContextListener, HttpSessionAttributeListener, HttpSessionListener {

    private static final Logger logger = Logger.getLogger(SessionListener.class);

    private static BidiMap registeredSessions = new TreeBidiMap();

    private static Map<String, Configuration> registeredConfigurations = new WeakHashMap<String, Configuration>();

    public synchronized static void registerUser(String user, String session) throws WebappRepeatedLoginException {
        String userInsensitiveCase = user.toLowerCase();
        String registeredSession = (String) registeredSessions.get(userInsensitiveCase);
        if (registeredSession != null) {
            if (!registeredSession.equals(session)) {
                throw new WebappRepeatedLoginException();
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Registering session " + session + " with user " + user); //$NON-NLS-1$ //$NON-NLS-2$
            }
            registeredSessions.put(userInsensitiveCase, session);
        }
    }

    public static synchronized void unregisterUser(String user) {
        String userInsensitiveCase = user.toLowerCase();
        if (!registeredSessions.containsKey(userInsensitiveCase)) {
            if (logger.isDebugEnabled()) {
                logger.warn("No session registered with user " + user); //$NON-NLS-1$
            }
        } else {
            String session = (String) registeredSessions.remove(userInsensitiveCase);
            registeredConfigurations.remove(session);
            if (logger.isDebugEnabled()) {
                logger.debug("Unregistered session " + session + " with user " + user); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    public synchronized static void registerConfiguration(String session, Configuration configuration) {
        if (logger.isDebugEnabled()) {
            if (registeredConfigurations.containsKey(session)) {
                logger.debug("Changing configuration for session " + session); //$NON-NLS-1$
            } else {
                logger.debug("Registering configuration for session " + session); //$NON-NLS-1$
            }
        }
        registeredConfigurations.put(session, configuration);
    }

    public synchronized static Configuration getRegisteredConfiguration(String session) {
        Configuration configuration = registeredConfigurations.get(session);
        if (logger.isDebugEnabled()) {
            if (configuration == null) {
                logger.debug("No configuration registered for session " + session); //$NON-NLS-1$
            }
        }
        return configuration;
    }

    private synchronized static void unregisterSession(String session) {
        registeredConfigurations.remove(session);

        if (!registeredSessions.containsValue(session)) {
            if (logger.isDebugEnabled()) {
                logger.warn("Session " + session + " is not registered"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return;
        }

        String user = (String) registeredSessions.getKey(session);
        if (user == null) {
            if (logger.isDebugEnabled()) {
                logger.warn("Session " + session + " is not registered with a user"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return;
        } else {
            registeredSessions.remove(user);
            if (logger.isDebugEnabled()) {
                logger.debug("Unregistered session " + session + " with user " + user); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("attributeAdded('" + event.getSession().getId() + "', '" + event.getName() + "', '" + event.getValue() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + "')"); //$NON-NLS-1$
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("attributeRemoved('" + event.getSession().getId() + "', '" + event.getName() + "', '" + event.getValue() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + "')"); //$NON-NLS-1$
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("attributeReplaced('" + event.getSession().getId() + "', '" + event.getName() + "', '" + event.getValue() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + "')"); //$NON-NLS-1$
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("contextDestroyed()"); //$NON-NLS-1$
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("contextInitialized()"); //$NON-NLS-1$
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        String sessionId = session.getId();
        if (logger.isDebugEnabled()) {
            logger.debug("Session " + sessionId + " created"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        String sessionId = session.getId();
        if (logger.isDebugEnabled()) {
            logger.debug("Session " + sessionId + " destroyed"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        unregisterSession(sessionId);
    }
}
