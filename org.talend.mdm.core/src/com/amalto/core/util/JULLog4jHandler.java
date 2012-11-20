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
package com.amalto.core.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JULLog4jHandler extends Handler {

    private static org.apache.log4j.Logger log4jLogger = org.apache.log4j.LogManager.getLogger(JULLog4jHandler.class);

    /**
     * Redirect java.util.logging to Log4J.
     */
    public static void JULToLog4j(Logger logger, int log4jLevel, boolean removeExistingHandlers) {

        if (removeExistingHandlers) {
            // remove all handlers
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
        }
        // add our own
        logger.addHandler(new JULLog4jHandler());
        Level level = getJULLevelFromLog4jLevel(log4jLevel);
        logger.setLevel(level);
        String loggerName = logger.getParent() == null ? "root" : logger.getName(); //$NON-NLS-1$
        log4jLogger.info("JUL " + loggerName + " logger set to " + level.getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static Level getJULLevelFromLog4jLevel(int log4jLevel) {
        switch (log4jLevel) {
        case org.apache.log4j.Level.ALL_INT:
            return Level.ALL;
        case org.apache.log4j.Level.DEBUG_INT:
            return Level.FINE;
        case org.apache.log4j.Level.TRACE_INT:
            return Level.FINEST;
        case org.apache.log4j.Level.ERROR_INT:
        case org.apache.log4j.Level.FATAL_INT:
            return Level.SEVERE;
        case org.apache.log4j.Level.INFO_INT:
            return Level.INFO;
        case org.apache.log4j.Level.WARN_INT:
            return Level.WARNING;
        default:
            return Level.OFF;
        }
    }

    public JULLog4jHandler() {
        super();
        setFormatter(new SimpleFormatter());
    }

    @Override
    public void close() throws SecurityException {
        // do nothing
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public void publish(LogRecord record) {
        org.apache.log4j.Logger log4jLogger = JULLog4jHandler.log4jLogger;
        String loggerName = record.getLoggerName();

        if (loggerName != null) {
            log4jLogger = org.apache.log4j.LogManager.getLogger(loggerName);
        }

        int level = record.getLevel().intValue();
        String message = getFormatter().formatMessage(record);
        Throwable throwable = record.getThrown();

        if (level <= Level.CONFIG.intValue()) {
            if (throwable != null) {
                log4jLogger.debug(message, throwable);
            } else {
                log4jLogger.debug(message);
            }
        } else if (level == Level.INFO.intValue()) {
            if (throwable != null) {
                log4jLogger.info(message, throwable);
            } else {
                log4jLogger.info(message);
            }
        } else if (level == Level.WARNING.intValue()) {
            if (throwable != null) {
                log4jLogger.warn(message, throwable);
            } else {
                log4jLogger.warn(message);
            }
        } else if (level == Level.SEVERE.intValue()) {
            if (throwable != null) {
                log4jLogger.error(message, throwable);
            } else {
                log4jLogger.error(message);
            }
        }
    }
}
