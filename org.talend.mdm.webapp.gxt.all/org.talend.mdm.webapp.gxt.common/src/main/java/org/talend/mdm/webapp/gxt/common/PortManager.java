/*
 * Created on 22 sept. 2005
 */
package org.talend.mdm.webapp.gxt.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.talend.mdm.webapp.gxt.service.open.XtentisRMIPort;
import org.talend.mdm.webapp.gxt.service.open.webservices.XtentisBindingStub;
import org.talend.mdm.webapp.gxt.service.open.webservices.XtentisPort_PortType;
import org.talend.mdm.webapp.gxt.service.open.webservices.XtentisServiceLocator;



/**
 * @author Starkey
 */
public class PortManager {

    /** the log used by this class */
    private static Log log = LogFactory.getLog(PortManager.class);

    /** the singleton instance of this class */
    private static PortManager instance = new PortManager();

    private Configuration configuration = null;

    private String port = "8080";
    
    private String endpoint_address = "http://localhost:" + port + "/talend/TalendPort";
    
    public static final int _AUTO_ = 0;

    public static final int _FORCE_RMI_ = 1;

    public static final int _FORCE_WEB_SERVICE_ = 2;

    /**
     * Creates a new instance - private constructor for the singleton pattern.
     */
    private PortManager() {
    }

    /**
     * Gets the singleton instance of this class.
     * 
     * @return the singleton BlogManager instance
     */
    public static PortManager getInstance() {
        return instance;
    }

    
    public Configuration getConfiguration() {
        return configuration;
    }

    
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        this.port = configuration.getMdmServerPort();
        this.port = this.port == null ? "8080" : this.port;
    }
    
    
    /*********************************************************************
     * WEB SERVICES
     *********************************************************************/

    public XtentisPort_PortType getPort() throws XtentisWebappException {
        AjaxSubject as;
        try {
            as = JAASHelper.getAjaxSubject();
        } catch (Exception e) {
            throw new XtentisWebappException("Unable to access the logged user data");
        }
        if (as == null)
            throw new XtentisWebappException("Session Expired");
        // log.debug("getPort() ");
        String[] mdm = as.getMDMData();
        String url = "http://" + mdm[0] + ":" + port + "/talend/TalendPort";
        return getPort(url, mdm[1], mdm[2]);
    }

    public XtentisPort_PortType getPort(String username, String password) throws XtentisWebappException {
        return getPort(endpoint_address, username, password, _AUTO_);
    }

    public XtentisPort_PortType getPort(String endpointAddress, String username, String password) throws XtentisWebappException {
        return getPort(endpointAddress, username, password, _AUTO_);
    }
    
    private XtentisPort_PortType getPort(String username, String password, int force) throws XtentisWebappException {
        return getPort(endpoint_address, username, password, force);
    }

    private XtentisPort_PortType getPort(String endpointAddress, String username, String password, int force)
            throws XtentisWebappException {

        if (force == _FORCE_RMI_)
            return getRMIEndPoint();
        if (force == _FORCE_WEB_SERVICE_)
            return getWSPort(endpointAddress, username, password);

        // Auto
        if (endpointAddress.contains("localhost")&&isInTheSameContainer())
            return getRMIEndPoint();

        return getWSPort(endpointAddress, username, password);

    }

    private boolean isInTheSameContainer() {
        boolean isInTheSameContainer=true;
        try {
            Class newoneClass = Class.forName("com.amalto.core.util.Util");
        } catch (Throwable e) {
            return false;//FIXME
        }
        return isInTheSameContainer;
    }

    private XtentisPort_PortType getWSPort(String endpointAddress, String username, String password) throws XtentisWebappException {
        try {            
            log.info("Using WS Port");
            // Init Web Service call
            XtentisServiceLocator xtentisService = new XtentisServiceLocator();
            xtentisService.setXtentisPortEndpointAddress(endpointAddress);
            XtentisPort_PortType xtentisWS = xtentisService.getXtentisPort();
            XtentisBindingStub stub = (XtentisBindingStub) xtentisWS;

            // Authentication
            stub.setUsername(username);
            stub.setPassword(password);
            
            return (XtentisPort_PortType) stub;
        } catch (Exception e) {
            e.printStackTrace();
            throw new XtentisWebappException("Unable to access endpoint at: " + endpointAddress + ": " + e.getLocalizedMessage());
        }
    }

    private XtentisPort_PortType getRMIEndPoint() throws XtentisWebappException {
        log.info("Using RMI Port");
        return new XtentisRMIPort();
        
    }

}
