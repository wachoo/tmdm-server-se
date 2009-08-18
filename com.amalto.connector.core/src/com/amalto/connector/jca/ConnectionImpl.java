package com.amalto.connector.jca;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.ResultSetInfo;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ResourceAdapter;
import javax.security.auth.Subject;

/**
 * ConnectionImpl.java
 * 
 * @author bgrieder
 */
public class ConnectionImpl implements Connection {


	/** the metat data of the connection */
	private ConnectionMetaDataImpl metaData = null;
    
//    private Subject subject = null;
//    private ConnectionRequestInfo cxRequestInfo = null;
    private ManagedConnectionImpl managedCx = null; //the calling Managed Connection
    
    private ResourceAdapter resourceAdapter = null; //the handling resource adapter
    
    /**
     * 
     * ConnectionImpl.java
     * Constructor 
     * @see ManagedConnection getConnection()
     * @param subject
     * @param cxRequestInfo
     */
    public ConnectionImpl(
            ManagedConnectionImpl managedCx,
            Subject subject, 
            ConnectionRequestInfo cxRequestInfo) {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("ConnectionImpl() ");
        
        this.managedCx = managedCx;
//        this.subject = subject;
//        this.cxRequestInfo = cxRequestInfo;
        
        //This actually copies the Managed Connection Metadata
        this.metaData = new ConnectionMetaDataImpl(this);
        
    }
    
    /* (non-Javadoc)
     * @see javax.resource.cci.Connection#close()
     */
    public void close() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("close()");
        managedCx.connectionHasClosed();

    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Connection#createInteraction()
     */
    public Interaction createInteraction() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("createInteraction()");
        return new InteractionImpl(this);
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Connection#getLocalTransaction()
     */
    public LocalTransaction getLocalTransaction() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getLocalTransaction()");
        return null;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Connection#getMetaData()
     */
    public ConnectionMetaData getMetaData() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getMetaData()");
        return metaData;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Connection#getResultSetInfo()
     */
    public ResultSetInfo getResultSetInfo() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getResultSetInfo()");
        return null;
    }

    /** 
     * this allows anything linked to the connection like metadata, 
     * to query the managed connection metadata
     */
    public ManagedConnectionImpl getManagedCx() {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getManagedCx() ");
        return managedCx;
    }

	/**
	 * @return Returns the resourceAdapter.
	 */
	public ResourceAdapter getResourceAdapter() {
		return resourceAdapter;
	}

	/**
	 * @param resourceAdapter The resourceAdapter to set.
	 */
	public void setResourceAdapter(ResourceAdapter resourceAdapter) {
		this.resourceAdapter = resourceAdapter;
	}
}
