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

import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Timer to measure elapsed time of any process or between steps.
 * <p/>
 * $Id: TimeMeasure.java 21728 2009-02-09 10:23:23Z plegall $
 */
public class TimeMeasure {

    private static final Logger log = Logger.getLogger(TimeMeasure.class);

    private static final HashMap<String, TimeStack> timers = new HashMap<String, TimeStack>();

    private static int indent = 0;

    /**
     * measureActive is true by default. A true value means that all methods calls are processed else no one.
     */
    public static boolean measureActive = true;

    /**
     * display is true by default. A true value means that all information are displayed.
     */
    public static boolean isDebugEnabled = log.isDebugEnabled();

    public static boolean displaySteps = true;

    public static void begin(String idTimer) {
        if (!measureActive) {
            return;
        }
        if (timers.containsKey(idTimer)) {
            if (isDebugEnabled) {
                log.debug(indent(indent) + "Warning (start): timer " + idTimer + " already exists"); //$NON-NLS-1$  //$NON-NLS-2$
            }
        } else {
            indent++;
            TimeStack times = new TimeStack();
            timers.put(idTimer, times);
            if (isDebugEnabled) {
                log.debug(indent(indent) + "Start '" + idTimer + "' ..."); //$NON-NLS-1$  //$NON-NLS-2$
            }
        }
    }

    public static long end(String idTimer) {
        if (!measureActive) {
            return 0;
        }
        if (!timers.containsKey(idTimer)) {
            if (isDebugEnabled) {
                log.debug(indent(indent) + "Warning (end): timer " + idTimer + " doesn't exist"); //$NON-NLS-1$  //$NON-NLS-2$
            }
            return -1;
        } else {
            TimeStack times = timers.get(idTimer);
            timers.remove(idTimer);
            if (times.hasManySteps()) {
                long elapsedTimeSinceLastRequest = times.getElapsedTimeSinceLastRequest();
                if (isDebugEnabled && displaySteps) {
                    log.debug(indent(indent) + "End '" + idTimer + "', elapsed time since last request: " //$NON-NLS-1$  //$NON-NLS-2$
                            + elapsedTimeSinceLastRequest + " ms "); //$NON-NLS-1$
                }
            }
            long totalElapsedTime = times.getTotalElapsedTime();
            if (isDebugEnabled) {
                log.debug(indent(indent) + "End '" + idTimer + "', total elapsed time: " + totalElapsedTime + " ms "); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
            }
            indent--;
            return totalElapsedTime;
        }
    }

    /**
     * DOC amaumont Comment method "timeStep".
     *
     * @param idTimer Id of a timer
     * @param stepName Unique name for a step
     * @return elapsed time since previous step in ms
     */
    public static long step(String idTimer, String stepName) {
        if (!measureActive) {
            return 0;
        }
        if (!timers.containsKey(idTimer)) {
            if (isDebugEnabled) {
                log.debug(indent(indent) + "Warning (end): timer " + idTimer + " does'nt exist"); //$NON-NLS-1$  //$NON-NLS-2$
            }
            return -1;
        } else {
            TimeStack times = timers.get(idTimer);
            long time = times.getElapsedTimeSinceLastRequest();
            times.addStep(false);
            if (isDebugEnabled && displaySteps) {
                log.debug(indent(indent) + "-> '" + idTimer + "', step name '" + stepName //$NON-NLS-1$  //$NON-NLS-2$
                        + "', elapsed time since previous step: " + time + " ms "); //$NON-NLS-1$  //$NON-NLS-2$
            }
            return time;
        }
    }

    public static void pause(String idTimer) {
        if (!measureActive) {
            return;
        }
        if (!timers.containsKey(idTimer)) {
            if (isDebugEnabled) {
                log.debug(indent(indent) + "Warning (end): timer " + idTimer + " does'nt exist"); //$NON-NLS-1$  //$NON-NLS-2$
            }
        } else {
            TimeStack times = timers.get(idTimer);
            times.addStep(true);
        }
    }

    public static void resume(String idTimer) {
        if (!measureActive) {
            return;
        }
        if (!timers.containsKey(idTimer)) {
            if (isDebugEnabled) {
                log.debug(indent(indent) + "Warning (end): timer " + idTimer + " does'nt exist"); //$NON-NLS-1$  //$NON-NLS-2$
            }
        } else {
            TimeStack times = timers.get(idTimer);
            times.addStep(false);
        }
    }

    public static String indent(final int i) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < i; j++) {
            stringBuilder.append("  "); //$NON-NLS-1$
        }
        return stringBuilder.toString();
    }
}
