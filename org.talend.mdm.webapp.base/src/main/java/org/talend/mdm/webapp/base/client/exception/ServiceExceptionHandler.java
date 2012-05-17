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
package org.talend.mdm.webapp.base.client.exception;

import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;
import org.talend.mdm.webapp.base.shared.TypeModelNotFoundException;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

/**
 * DOC Administrator  class global comment. Detailed comment
 */
public class ServiceExceptionHandler {
    
    private Throwable caught;

    public ServiceExceptionHandler(Throwable caught) {
        super();
        this.caught = caught;
    }

    /**
     * DOC hshu Comment method "work".
     */
    public boolean work() {

        if (caught == null)
            return true;

        if (caught instanceof SessionTimeoutException) {
            MessageBox.alert(BaseMessagesFactory.getMessages().warning_title(), BaseMessagesFactory.getMessages()
                    .session_timeout_error(), new Listener<MessageBoxEvent>() {

                public void handleEvent(MessageBoxEvent be) {
                    Cookies.removeCookie("JSESSIONID"); //$NON-NLS-1$
                    Cookies.removeCookie("JSESSIONIDSSO"); //$NON-NLS-1$
                    Window.Location.replace("/talendmdm/secure/");//$NON-NLS-1$
                }
            });
            return true;
        } else if (caught instanceof TypeModelNotFoundException) {
            MessageBox.alert(BaseMessagesFactory.getMessages().error_title(), BaseMessagesFactory.getMessages()
                    .typemode_notfound_error(((TypeModelNotFoundException) caught).getTypePathParameter()), null);
            return true;
        }
        
        return false;
    }

    public static void doOnFailure(Throwable caught) {
        String errorMsg = caught.getLocalizedMessage();
        if (errorMsg == null || "".equals(errorMsg)) { //$NON-NLS-1$
            if (Log.isDebugEnabled())
                errorMsg = caught.toString(); // for debugging purpose
            else
                errorMsg = BaseMessagesFactory.getMessages().unknown_error();
        }
        MessageBox.alert(BaseMessagesFactory.getMessages().error_title(), errorMsg, null);
    }
    
}
