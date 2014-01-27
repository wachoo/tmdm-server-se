/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.server;

/**
 *
 */
public interface RoutingEngine {
    /**
     * Routes a document
     * @return the list of routing rules PKs that matched
     * @throws com.amalto.core.util.XtentisException
     */
    com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK[] route(com.amalto.core.ejb.ItemPOJOPK itemPOJOPK) throws com.amalto.core.util.XtentisException;

    /**
     * Starts/restarts the router
     * @throws com.amalto.core.util.XtentisException
     */
    void start() throws com.amalto.core.util.XtentisException;

    /**
     * Stops the routing queue
     * @throws com.amalto.core.util.XtentisException
     */
    void stop() throws com.amalto.core.util.XtentisException;

    /**
     * Toggle suspend a routing queue
     * @throws com.amalto.core.util.XtentisException
     */
    void suspend(boolean suspend) throws com.amalto.core.util.XtentisException;

    /**
     * Toggle suspend a routing queue
     * @throws com.amalto.core.util.XtentisException
     */
    int getStatus() throws com.amalto.core.util.XtentisException;
}
