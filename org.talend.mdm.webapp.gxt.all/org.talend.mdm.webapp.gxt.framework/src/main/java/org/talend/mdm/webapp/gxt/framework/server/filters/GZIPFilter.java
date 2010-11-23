package org.talend.mdm.webapp.gxt.framework.server.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GZIPFilter implements Filter {

  public void doFilter(ServletRequest req, ServletResponse res,
                       FilterChain chain) throws IOException, ServletException {

      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      String ae = request.getHeader("Accept-Encoding");
      if (ae != null && ae.indexOf("gzip") != -1) {
        GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);
        chain.doFilter(req, wrappedResponse);
        wrappedResponse.finishResponse();
      } else {
        chain.doFilter(req, res);
      }
  }

  public void init(FilterConfig filterConfig) {
  }

  public void destroy() {
  }

}
