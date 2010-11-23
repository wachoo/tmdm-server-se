package org.talend.mdm.webapp.gxt.framework.server.page;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public abstract class PageServiceImpl extends RemoteServiceServlet {

    protected abstract String getPageId();
    
    protected PageSession getPageSession(){
        return new PageSession(getPageId(), this.getThreadLocalRequest().getSession());
    }
   
}
