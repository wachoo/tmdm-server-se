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


public class MDMServerException extends Exception {

    private final ServerMessages serverMessage;

    public MDMServerException(ServerMessages serverMessage, String message) {
        super(message);
        this.serverMessage = serverMessage;
    }

    public MDMServerException(ServerMessages serverMessage, Throwable cause) {
        super(cause);
        this.serverMessage = serverMessage;
    }

    public MDMServerException(ServerMessages serverMessage, String message, Throwable cause) {
        super(message, cause);
        this.serverMessage = serverMessage;
    }

    public ServerMessages getServerMessage() {
        return serverMessage;
    }

    public int getMessageCode() {
        return serverMessage.getCode();
    }

    public static MDMServerException extractMDMException(Throwable cause) {
        while (cause != null) {
            if (cause instanceof MDMServerException) {
                return (MDMServerException) cause;
            }
            cause = cause.getCause();
        }
        return null;
    }
}
