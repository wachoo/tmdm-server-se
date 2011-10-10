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
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class SessionAwareAsyncCallback<T> implements AsyncCallback<T> {

    public final void onFailure(Throwable caught) {
        if (Log.isErrorEnabled())
            Log.error(caught.toString());

        if (caught instanceof SessionTimeoutException) {
            MessageBox.alert(BaseMessagesFactory.getMessages().warning_title(), BaseMessagesFactory.getMessages()
                    .session_timeout_error(), new Listener<MessageBoxEvent>() {

                public void handleEvent(MessageBoxEvent be) {
                    Cookies.removeCookie("JSESSIONID"); //$NON-NLS-1$
                    Cookies.removeCookie("JSESSIONIDSSO"); //$NON-NLS-1$
                    Window.Location.replace("/talendmdm/secure/");//$NON-NLS-1$
                }
            });
        } else {
            doOnFailure(caught);
        }
    }

    protected void doOnFailure(Throwable caught) {
        String errorMsg = caught.getLocalizedMessage();
        if (errorMsg == null) {
            if (Log.isDebugEnabled())
                errorMsg = caught.toString(); // for debugging purpose
            else
                errorMsg = BaseMessagesFactory.getMessages().unknown_error();
        }
        MessageBox.alert(BaseMessagesFactory.getMessages().error_title(), errorMsg, null);
    }
}
