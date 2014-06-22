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

package com.amalto.core.save;

import org.apache.commons.lang.StringUtils;

public class SaveException extends RuntimeException {

    private final String beforeSavingMessage;

    public SaveException(Throwable cause) {
        this(StringUtils.EMPTY, cause);
    }

    public SaveException(String beforeSavingMessage, Throwable cause) {
        super("Exception occurred during save: " + beforeSavingMessage, cause);
        this.beforeSavingMessage = beforeSavingMessage;
    }

    public String getBeforeSavingMessage() {
        return beforeSavingMessage;
    }
}
