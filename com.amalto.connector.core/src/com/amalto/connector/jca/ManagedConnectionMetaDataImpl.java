/*
 * Created on 27 nov. 2004
 *
 */
package com.amalto.connector.jca;

import java.lang.reflect.Method;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionMetaData;

import com.amalto.connector.interfaces.IXtentisResourceAdapter;


/**
 * ManagedConnectionMetaData.java
 * 
 * @author bgrieder
 */
public class ManagedConnectionMetaDataImpl implements ManagedConnectionMetaData {


    private ManagedConnectionImpl managedCx = null;
    
    /**
     * Constructor
     * @param managedCx
     */
    public ManagedConnectionMetaDataImpl(ManagedConnectionImpl managedCx) {
//    	org.apache.log4j.Category.getInstance(this.getClass()).debug("ManagedConnectionMetaData() ");
        this.managedCx = managedCx;
    }
    
    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionMetaData#getEISProductName()
     */
    public String getEISProductName() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getEISProductName()");
       return "Xtentis JCA Adapter";
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionMetaData#getEISProductVersion()
     */
    public String getEISProductVersion() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getEISProductVersion() ");
        return ((IXtentisResourceAdapter)managedCx.getResourceAdapter()).getVersion();
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionMetaData#getMaxConnections()
     */
    public int getMaxConnections() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getMaxConnections() ");
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionMetaData#getUserName()
     */
    public String getUserName() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getUserName() ");
        ConnectionRequestInfo cri = this.managedCx.getCxRequestInfo();
        if (cri != null) {
            Method m = null;
            try {
                m = cri.getClass().getMethod("getUserName",null);
            } catch (Exception e1) {
                try {
                    m = cri.getClass().getMethod("getUsername", null);
                } catch (Exception e2) {}
            }
            if (m!=null) {
                try {
                    return (String) m.invoke(cri, null);
                } catch (Exception e3) {
                    return null;
                }
            }
        }//if
        return null;
    }//getUserName

}
