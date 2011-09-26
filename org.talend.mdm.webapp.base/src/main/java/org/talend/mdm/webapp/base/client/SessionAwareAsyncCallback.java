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
package org.talend.mdm.webapp.base.client;

import org.talend.mdm.webapp.base.client.exception.SessionTimeoutException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class SessionAwareAsyncCallback<T> implements AsyncCallback<T> {

    public final void onFailure(Throwable caught) {
        if (Log.isErrorEnabled())
            Log.error(caught.toString());
        if (caught instanceof SessionTimeoutException) {
            Window.Location.replace("/talendmdm/secure/");//$NON-NLS-1$
        } else {
            doOnFailure(caught);
        }
    }

    protected abstract void doOnFailure(Throwable caught);
}
