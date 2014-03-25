// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.rmi.RemoteException;

public class RemoteExceptionFactory {

    public static RemoteException newDefaultRemoteException(Exception nestedException) {
        return new RemoteException(nestedException.getCause() == null ? nestedException.getLocalizedMessage() : nestedException
                .getCause().getLocalizedMessage(), nestedException);
    }

    public static RemoteException newDefaultRemoteExceptionWithFullCauseDetail(Exception nestedException, boolean ignoreClassName) {

        String errorMessage;
        StringBuilder errorMessageBuf = new StringBuilder();
        errorMessageBuf.append(nestedException.getLocalizedMessage() + "; "); //$NON-NLS-1$

        if (nestedException.getCause() != null) {
            Throwable t = nestedException.getCause();
            errorMessageBuf.append(genCausedByMsg(t, ignoreClassName));

            while ((t = t.getCause()) != null)
                if (t.getLocalizedMessage() != null && !t.getLocalizedMessage().isEmpty())
                    errorMessageBuf.append(genCausedByMsg(t, ignoreClassName));

            errorMessage = errorMessageBuf.toString();
        } else {
            errorMessage = nestedException.getLocalizedMessage();
        }

        return new RemoteException(errorMessage, nestedException);
    }

    // FIXME is there any other better approach?
    private static String genCausedByMsg(Throwable t, boolean ignoreClassName) {
        StringBuilder msg = new StringBuilder();
        msg.append("\n\t[Caused by]: ");//$NON-NLS-1$
        if (!ignoreClassName)
            msg.append(t.getClass().getName() + ": ");//$NON-NLS-1$
        msg.append(t.getLocalizedMessage());
        return msg.toString();
    }

}
