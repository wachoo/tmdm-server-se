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

package com.amalto.core.metadata;

import org.apache.log4j.Logger;

public class DefaultValidationHandler implements ValidationHandler {

    public static final Logger LOGGER = Logger.getLogger(DefaultValidationHandler.class);

    public static final DefaultValidationHandler INSTANCE = new DefaultValidationHandler();

    private DefaultValidationHandler() {
    }

    @Override
    public void error(String message) {
        throw new RuntimeException(message);
    }

    @Override
    public void fatal(String message) {
        throw new RuntimeException(message);
    }

    @Override
    public void warning(String message) {
        LOGGER.warn(message);
    }
}
