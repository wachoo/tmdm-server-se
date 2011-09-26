// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.client.exception;

import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;

public class ServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServiceException() {
        this(null);
    }

    public ServiceException(String message) {
        super(message == null ? BaseMessagesFactory.getMessages().unknown_error() : message);
    }
}
