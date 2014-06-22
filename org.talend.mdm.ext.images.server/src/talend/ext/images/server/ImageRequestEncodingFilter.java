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
package talend.ext.images.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <p>
 * Set configured encoding to request encoding, if client already specified a character encoding, it will be override
 * </p>
 */
public class ImageRequestEncodingFilter implements Filter{

    private static final String ENCODING_PARAMETER = "encoding";
    
	private String encoding;

    public void init(FilterConfig filterConfig) throws ServletException {

        this.encoding = filterConfig.getInitParameter(ENCODING_PARAMETER); //$NON-NLS-1$

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        // always override
        if (encoding != null)
            request.setCharacterEncoding(encoding);

        chain.doFilter(request, response);

    }

    public void destroy() {
        this.encoding = null;
    }

}
