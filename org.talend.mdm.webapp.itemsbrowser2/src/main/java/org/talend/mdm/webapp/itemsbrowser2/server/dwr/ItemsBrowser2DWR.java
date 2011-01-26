package org.talend.mdm.webapp.itemsbrowser2.server.dwr;

import org.apache.log4j.Logger;

import com.amalto.webapp.core.bean.Configuration;

/**
 * cluster
 * 
 * 
 * @author starkey
 * 
 */

public class ItemsBrowser2DWR {

    private static final Logger LOG = Logger.getLogger(ItemsBrowser2DWR.class);

    public ItemsBrowser2DWR() {
        super();
    }

    public String getCluster(){
        try {
            Configuration config = Configuration.getInstance();
            return config.getCluster();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
    }
}
