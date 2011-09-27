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
package org.talend.mdm.webapp.base.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.talend.mdm.webapp.base.client.exception.SessionTimeoutException;
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;
import org.talend.mdm.webapp.base.server.i18n.BaseMessagesImpl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AbstractService extends RemoteServiceServlet {

    private static final long serialVersionUID = 1L;

    private static final Object lock = new Object[0];

    @Override
    public void init() throws ServletException {
        super.init();
        synchronized (lock) {
            if (BaseMessagesFactory.getMessages() == null)
                BaseMessagesFactory.setMessages(new BaseMessagesImpl());
        }
    }

    @Override
    public final String processCall(String payload) throws SerializationException {
        HttpServletRequest request = getThreadLocalRequest();
        if (request.getSession(false) == null || request.getSession(false).isNew()) {
            // Session is invalid
            return RPC.encodeResponseForFailure(null, new SessionTimeoutException());
        } else {
            return super.processCall(payload);
        }
    }
}
