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
package org.talend.mdm.webapp.base.client;

import org.talend.mdm.webapp.base.client.exception.SessionTimeoutException;
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

public abstract class SessionAwareAsyncCallback<T> implements AsyncCallback<T> {

    public final void onFailure(Throwable caught) {
        if (Log.isErrorEnabled())
            Log.error(caught.toString());

        if (sessionExpired(caught)) {
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
        if (errorMsg == null || "".equals(errorMsg)) { //$NON-NLS-1$
            if (Log.isDebugEnabled()) {
                errorMsg = caught.toString(); // for debugging purpose
            } else {
                errorMsg = BaseMessagesFactory.getMessages().unknown_error();
            }
            // see TMDM-TMDM-4411 if call async method,StatusCodeException will be thrown when mdmserver down
        } else if (caught instanceof com.google.gwt.user.client.rpc.StatusCodeException) {
            if (Log.isDebugEnabled()) {
                errorMsg = caught.toString(); // for debugging purpose
            } else {
                errorMsg = BaseMessagesFactory.getMessages().unknown_error();
            }
        }
        errorMsg = Format.htmlEncode(errorMsg);
        MessageBox.alert(BaseMessagesFactory.getMessages().error_title(), errorMsg, null);
    }

    private boolean sessionExpired(Throwable caught) {
        if (caught instanceof InvocationException) {
            if (caught instanceof StatusCodeException)
                // In some cases, when session expires, instead of redirecting the server will throw the exception
                // "HTTP Status 403 -The request body was too large to be cached during the authentication process"
                // See http://lists.jboss.org/pipermail/jboss-user/2006-November/020832.html
                return ((StatusCodeException) caught).getStatusCode() == 403;
            String msg = caught.getMessage();
            // FIXME Is there a better way to detect session expiration?
            // When session expires container will use configured login-config element in web.xml, and return content of
            // loginAgent.html for the browser to redirect to the login page. However, since GWT RPC response cannot be
            // HTML, the InvocationException is thrown with the HTML content as the message.
            return msg == null ? false : msg.contains("<meta http-equiv=\"refresh\" content=\"0; url=/talendmdm/secure\""); //$NON-NLS-1$
        } else if (caught instanceof SessionTimeoutException || (caught.getMessage() != null && caught.getMessage().contains("<meta http-equiv=\"refresh\" content=\"0; url=/talendmdm/secure\""))) //$NON-NLS-1$
            return true;
        else
            return false;
    }
}
