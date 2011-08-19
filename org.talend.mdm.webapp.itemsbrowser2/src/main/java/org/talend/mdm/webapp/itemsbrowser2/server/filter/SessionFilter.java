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
package org.talend.mdm.webapp.itemsbrowser2.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.talend.mdm.webapp.itemsbrowser2.server.exception.SessionTimeOutException;

/**
 * Servlet Filter implementation class SecurityFilter
 */
public class SessionFilter implements Filter {
    /**
     * Default constructor.
     */
    public SessionFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest _request, ServletResponse _response, FilterChain chain) throws IOException,
            ServletException {
    	HttpServletResponse response = (HttpServletResponse) _response;
        HttpServletRequest request = (HttpServletRequest) _request;
        
    	if(request.getSession(false) != null && !request.getSession(false).isNew()) {
    		chain.doFilter(request, response);
    	}
    	else {
    		throw new SessionTimeOutException("SessionTimeOut");//$NON-NLS-1$
    	}
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
    	
    }

    private void initLoginConfig(FilterConfig fConfig) throws IOException {
    	
    }
}
