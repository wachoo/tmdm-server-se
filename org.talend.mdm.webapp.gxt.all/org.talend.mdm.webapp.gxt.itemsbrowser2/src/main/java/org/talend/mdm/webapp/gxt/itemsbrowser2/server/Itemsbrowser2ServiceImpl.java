package org.talend.mdm.webapp.gxt.itemsbrowser2.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.talend.mdm.webapp.gxt.common.PortManager;
import org.talend.mdm.webapp.gxt.framework.server.page.PageServiceImpl;
import org.talend.mdm.webapp.gxt.framework.shared.NavFolder;
import org.talend.mdm.webapp.gxt.itemsbrowser2.client.Itemsbrowser2Service;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSPing;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSString;
import org.talend.mdm.webapp.gxt.service.open.webservices.XtentisPort_PortType;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class Itemsbrowser2ServiceImpl extends PageServiceImpl implements Itemsbrowser2Service {
    
    private static Log log = LogFactory.getLog(Itemsbrowser2ServiceImpl.class);
    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.gxt.framework.server.page.PageServiceImpl#getPageId()
     */
    @Override
    protected String getPageId() {
        return "itemsbrowser.ItemsBrowser";
    }

    public String pingMDM(String input) throws Exception {

        NavFolder root = new NavFolder("-1", "root", NavFolder._ROOT_);

        String defaultPingEcho = (String) getPageSession().getAttribute("default_ping_echo");
        if (defaultPingEcho != null && defaultPingEcho.length() > 0) {
            log.info("Reuse value from page session. ");
            return defaultPingEcho;
        } else {
            XtentisPort_PortType port = PortManager.getInstance().getPort();
            WSString wsString = port.ping(new WSPing(input));
            getPageSession().setAttribute("default_ping_echo",wsString.getValue());
            return wsString.getValue();
        }

    }

}
