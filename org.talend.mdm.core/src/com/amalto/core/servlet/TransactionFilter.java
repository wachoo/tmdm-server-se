/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.servlet;

import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.transaction.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TransactionFilter implements Filter {

    public static final String TRANSACTION_ID = "transaction-id"; //$NON-NLS-1$

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Server server = ServerContext.INSTANCE.get();
        server.getTransactionManager().init();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        TransactionState state = getState(servletRequest);
        try {
            state.preRequest();
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Throwable t) {
            state.cancelRequest();
            throw new ServletException(t);
        } finally {
            state.postRequest();
        }
    }

    private static TransactionState getState(ServletRequest request) {
        HttpServletRequest httpServlet = (HttpServletRequest) request;
        String transactionId = httpServlet.getHeader(TRANSACTION_ID);
        if (transactionId == null) {
            return ImplicitTransactionState.INSTANCE;
        } else {
            return new ExplicitTransaction(transactionId);
        }
    }

    @Override
    public void destroy() {
        // Destroy of the transaction manager is moved to Server#close();
    }
}
