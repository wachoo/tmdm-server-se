/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load.context;

import com.amalto.core.server.XmlServer;
import com.amalto.core.util.AutoIncrementGenerator;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import java.util.UUID;

public class UUIDIdGenerator implements AutoIdGenerator {

    public String generateId(String dataClusterName, String conceptName, String keyElementName) {
        return UUID.randomUUID().toString();
    }

    public void saveState(XmlServer server) {
        // Nothing to do.
    }
}