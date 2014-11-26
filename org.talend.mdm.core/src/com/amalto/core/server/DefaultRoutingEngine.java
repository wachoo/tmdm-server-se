/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.server;

import com.amalto.core.objects.ItemPOJOPK;
import com.amalto.core.objects.routing.RoutingRulePOJOPK;
import com.amalto.core.util.XtentisException;
import com.amalto.core.server.api.RoutingEngine;

public class DefaultRoutingEngine implements RoutingEngine {
    @Override
    public RoutingRulePOJOPK[] route(ItemPOJOPK itemPOJOPK) throws XtentisException {
        return new RoutingRulePOJOPK[0];
    }

    @Override
    public void start() throws XtentisException {
    }

    @Override
    public void stop() throws XtentisException {
    }

    @Override
    public void suspend(boolean suspend) throws XtentisException {
    }

    @Override
    public int getStatus() throws XtentisException {
        return 0;
    }
}
