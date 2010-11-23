package org.talend.mdm.webapp.gxt.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A bean representing configurable properties
 * 
 */
public class Configuration {

    /** the log used by this class */
    private static Log log = LogFactory.getLog(Configuration.class);

    private String mdmServerPort;

    public Configuration() {
    }

    public String getMdmServerPort() {
        return mdmServerPort==null?"8080":mdmServerPort.trim();
    }

    public void setMdmServerPort(String mdmServerPort) {
        this.mdmServerPort = mdmServerPort;
    }

}
