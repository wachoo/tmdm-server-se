/*
 * Created on 11 déc. 2004
 *
 */
package com.amalto.connector.jca;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionMetaData;

/**
 * @author bgrieder
 * 
 */
public class ConnectionMetaDataImpl implements ConnectionMetaData {

    private ConnectionImpl connection = null;
    
    
    /**
     * Constructor
     */
    public ConnectionMetaDataImpl(ConnectionImpl connection) {
        super();
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("ConnectionMetaDataImpl() ");
        
        this.connection = connection;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.ConnectionMetaData#getEISProductName()
     */
    public String getEISProductName() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getEISProductName() ");
        return connection.getManagedCx().getMetaData().getEISProductName();
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.ConnectionMetaData#getEISProductVersion()
     */
    public String getEISProductVersion() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getEISProductVersion() ");
        return connection.getManagedCx().getMetaData().getEISProductVersion();
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.ConnectionMetaData#getUserName()
     */
    public String getUserName() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getUserName() ");
        return connection.getManagedCx().getMetaData().getUserName();
    }

}
