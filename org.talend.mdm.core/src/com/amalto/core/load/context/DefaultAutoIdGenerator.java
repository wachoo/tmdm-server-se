/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load.context;

import com.amalto.core.util.AutoIncrementGenerator;import com.amalto.core.util.LocalUser;import com.amalto.core.util.XtentisException;

/**
 *
 */
public class DefaultAutoIdGenerator implements AutoIdGenerator {
    public String generateAutoId(String dataClusterName, String conceptName) {
        // TODO check if uuid key exist
        try {
            String universe = LocalUser.getLocalUser().getUniverse().getName();
            long id = AutoIncrementGenerator.generateNum(universe,
                    dataClusterName,
                    conceptName);
            return String.valueOf(id);
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }
}