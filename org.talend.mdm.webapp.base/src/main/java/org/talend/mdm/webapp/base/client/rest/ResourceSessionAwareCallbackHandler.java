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
package org.talend.mdm.webapp.base.client.rest;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.MediaType;
import org.talend.mdm.webapp.base.client.i18n.BaseMessages;
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

public abstract class ResourceSessionAwareCallbackHandler implements ResourceCallbackHandler {

    protected BaseMessages messages = BaseMessagesFactory.getMessages();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.webapp.stagingareacontrol.client.rest.ResourceCallbackHandler#process(org.restlet.client.Request,
     * org.restlet.client.Response)
     */
    @Override
    public void process(Request request, Response response) {
        try {
            // Has session expired? FIXME is there any other way to do the determination?
            if (response != null && response.getEntity() != null && response.getEntity().getMediaType() != null
                    && response.getEntity().getMediaType().equals(MediaType.TEXT_HTML)) {
                if (response.getEntity().getText() != null
                        && response.getEntity().getText().contains("<title>Redirection to secure site</title>")//$NON-NLS-1$
                        && response.getEntity().getText()
                                .contains("<meta http-equiv=\"refresh\" content=\"0; url=/talendmdm/secure\"")) {//$NON-NLS-1$

                    MessageBox.alert(BaseMessagesFactory.getMessages().warning_title(), BaseMessagesFactory.getMessages()
                            .session_timeout_error(), new Listener<MessageBoxEvent>() {

                        @Override
                        public void handleEvent(MessageBoxEvent be) {
                            Cookies.removeCookie("JSESSIONID"); //$NON-NLS-1$
                            Cookies.removeCookie("JSESSIONIDSSO"); //$NON-NLS-1$
                            Window.Location.replace("/talendmdm/secure/");//$NON-NLS-1$
                        }
                    });
                    return;

                }

            }

            doProcess(request, response);
        } catch (Exception e) {
            alertStagingError(e);
        }

    }

    public abstract void doProcess(Request request, Response response) throws Exception;

    protected void alertStagingError(Throwable e) {
        String errorTitle = messages.service_rest_error();
        String errorDetail;
        if (e.getMessage() == null || e.getMessage().trim().length() == 0) {
            errorDetail = messages.service_rest_exception();
        } else {
            errorDetail = messages.service_rest_exception() + "</br>" + messages.underlying_cause() //$NON-NLS-1$
                    + "<div style='width:300px; height:80px; overflow:auto; margin-top: 5px; margin-left: 50px; border: dashed 1px #777777;'>" //$NON-NLS-1$
                    + Format.htmlEncode(e.getMessage()) + "</div>"; //$NON-NLS-1$
        }
        Dialog dialog = MessageBox.alert(errorTitle, errorDetail, null).getDialog();
        dialog.setWidth(400);
        dialog.center();
    }

}
