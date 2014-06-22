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
package org.talend.mdm.webapp.stagingareacontrol.client.rest;

import java.util.Date;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.MediaType;
import org.restlet.client.data.Method;
import org.restlet.client.resource.ClientResource;
import org.talend.mdm.webapp.stagingareacontrol.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class ClientResourceWrapper {

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.TEXT_XML;

    protected ClientResource client;

    protected Method method;

    protected String uri;

    private Object postEntity;

    public ClientResourceWrapper() {

    }

    public ClientResourceWrapper(Method method, String uri) {
        init(method, uri);
    }

    public void init(Method method, String uri) {
        this.method = method;
        this.uri = uri;
        String timestamp = "timestamp=" + new Date().getTime(); //$NON-NLS-1$
        if (uri.lastIndexOf('?') == -1) {
            timestamp = "?" + timestamp; //$NON-NLS-1$
        } else {
            timestamp = "&" + timestamp; //$NON-NLS-1$
        }
        client = new ClientResource(method, uri + timestamp); //
    }

    public void setCallback(final ResourceCallbackHandler callbackHandler) {
        client.setOnResponse(new Uniform() {

            public void handle(Request request, Response response) {
                int statusCode = response.getStatus().getCode();
                if (statusCode >= 200 && statusCode <= 299 || statusCode == 1223) {
                    callbackHandler.process(request, response);
                } else {
                    MessageBox.alert(MessagesFactory.getMessages().server_error(),
                            MessagesFactory.getMessages().server_error_notification(), null);
                }
            }
        });
    }

    public void setPostEntity(Object postEntity) {
        this.postEntity = postEntity;
    }

    public void request() {
        request(null);
    }

    public void request(MediaType mediaType) {
        if (mediaType == null)
            mediaType = DEFAULT_MEDIA_TYPE;

        if (method.equals(Method.GET))
            client.get(mediaType);
        else if (method.equals(Method.POST))
            client.post(postEntity, mediaType);
        else if (method.equals(Method.DELETE))
            client.delete(mediaType);

        // TODO: to support more HTTP methods
    }

}
