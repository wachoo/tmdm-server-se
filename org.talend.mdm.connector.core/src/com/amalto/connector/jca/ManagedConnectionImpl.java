/*
 * Created on 27 nov. 2004
 *
 */
package com.amalto.connector.jca;

import java.io.PrintWriter;
import java.util.Vector;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.resource.spi.ResourceAdapter;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;


/**
 * ManagedConnection.java
 * 
 * @author bgrieder
 */
public class ManagedConnectionImpl implements ManagedConnection {
    
    private Subject subject = null;
    private ConnectionRequestInfo cxRequestInfo = null;
    private ConnectionImpl connection = null; //the underlying connection
    private ResourceAdapter resourceAdapter = null; //the handling resource adapter
    
    private Vector<ConnectionEventListener> listeners = new Vector<ConnectionEventListener>();
    private PrintWriter logWriter = null;
    private ManagedConnectionMetaDataImpl metaData;
    
    /**
     * 
     * ManagedConnection.java
     * Constructor 
     * @see ManagedConnectionFactoryImpl createManagedConnection()
     * @param subject
     * @param cxRequestInfo
     */
    public ManagedConnectionImpl(Subject subject, ConnectionRequestInfo cxRequestInfo) {
//    	org.apache.log4j.Category.getInstance(this.getClass()).debug("ManagedConnectionImpl() ");
        
        this.subject = subject;
        this.cxRequestInfo = cxRequestInfo;
        
        this.metaData = new ManagedConnectionMetaDataImpl(this);
    }
    
    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#getConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
     */
    public Object getConnection(Subject subject,
            ConnectionRequestInfo cxRequestInfo) throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getConnection() ");
        
        this.connection = new ConnectionImpl(this,subject, cxRequestInfo); 
        this.connection.setResourceAdapter(getResourceAdapter());
        return connection;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#destroy()
     */
    public void destroy() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("destroy() ");
    	connection = null;
		listeners = new Vector<ConnectionEventListener>();
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#cleanup()
     */
    public void cleanup() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("cleanup() ");
        //deleted because creates an infinite loop with connectionHasClosed 
        //if (this.connection!=null) connection.close();
    }

    /**
     * Called by the underlying transaction when receives a close order
     * This notifies listeners
     */
    public void connectionHasClosed() {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("connectionHasClosed() ");
        ConnectionEvent ce = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
        ce.setConnectionHandle(this.connection);
        
        for (int i=0;i<listeners.size();i++) {
            ConnectionEventListener cel = listeners.get(i);
            cel.connectionClosed(ce);
        }

    }
    
    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#associateConnection(java.lang.Object)
     */
    public void associateConnection(Object conx) throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("associateConnection() ");
        if ((this.connection == null) && (conx instanceof ConnectionImpl))
            this.connection = (ConnectionImpl)conx;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#addConnectionEventListener(javax.resource.spi.ConnectionEventListener)
     */
    public void addConnectionEventListener(ConnectionEventListener listener) {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("addConnectionEventListener() ");
        listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#removeConnectionEventListener(javax.resource.spi.ConnectionEventListener)
     */
    public void removeConnectionEventListener(ConnectionEventListener listener) {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("removeConnectionEventListener() ");
        listeners.remove(listener);
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#getXAResource()
     */
    public XAResource getXAResource() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getXAResource() ");
        return null;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#getLocalTransaction()
     */
    public LocalTransaction getLocalTransaction() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getLocalTransaction() ");
        return null;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#getMetaData()
     */
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getMetaData() ");
        return this.metaData;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#getLogWriter()
     */
    public PrintWriter getLogWriter() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getLogWriter() ");
        return this.logWriter;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnection#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("setLogWriter() ");
        this.logWriter = out;

    }

    public ConnectionRequestInfo getCxRequestInfo() {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getCxRequestInfo() ");
        return cxRequestInfo;
    }
    public Subject getSubject() {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getSubject() ");
        return subject;
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
