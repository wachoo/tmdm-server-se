package com.amalto.connector.jca;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.security.auth.Subject;
/*
 * Created on 27 nov. 2004
 *
 */

/**
 * 
 * ManagedConnectionFactory.java
 * 
 * @author bgrieder
 */
public class ManagedConnectionFactoryImpl implements ManagedConnectionFactory,ResourceAdapterAssociation {
	

    //The Log Writer
	private PrintWriter logWriter = null;
	
	//The Resource Adapter
	private ResourceAdapter resourceAdapter = null;
	
    /**
     * Constructor
     */
    public ManagedConnectionFactoryImpl() {
        super();
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("ManagedConnectionFactoryImpl() ");
    }
    
    
    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory()
     */
    public Object createConnectionFactory() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("createConnectionFactory() ");
        return new ConnectionFactoryImpl(this, null);
    }
    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionFactory#createConnectionFactory(javax.resource.spi.ConnectionManager)
     */
    public Object createConnectionFactory(ConnectionManager cxManager)
            throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("createConnectionFactory() with connection manager ");
        return new ConnectionFactoryImpl(this, cxManager);
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionFactory#createManagedConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
     */
    public ManagedConnection createManagedConnection(Subject subject,
            ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("createManagedConnection() ");
    	ManagedConnectionImpl bmc = new ManagedConnectionImpl(subject,cxRequestInfo);
    	bmc.setLogWriter(this.getLogWriter());
    	bmc.setResourceAdapter(this.getResourceAdapter());
    	
//    	org.apache.log4j.Category.getInstance(this.getClass()).debug("Managed connection created with "+
//    	        ((subject ==null)? "NO subject - " : subject.toString()) +
//    	        ((cxRequestInfo == null)? " NO cxRequestInfo" : cxRequestInfo.toString()));
    	
    	return bmc;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionFactory#matchManagedConnections(java.util.Set, javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
     */
    public ManagedConnection matchManagedConnections(
            Set connectionSet,
            Subject subject, 
            ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("matchManagedConnections() ");
        
        //They are all the same - return the first one
        Iterator<Object> iterator = connectionSet.iterator();
		for (;iterator.hasNext();) {
		    Object m = iterator.next();
		    if (m instanceof ManagedConnectionImpl) 
		    	return (ManagedConnectionImpl)m;
		}
		return null;
        
        /*
        ManagedConnectionImpl mc = null;
		Iterator iterator = connectionSet.iterator();
		for (;iterator.hasNext();) {
		    Object m = iterator.next();
		    if (m instanceof ManagedConnectionImpl) {
		        mc = (ManagedConnectionImpl) m;
		        if (	(mc.getSubject() != null)
		                && mc.getSubject().equals(subject)
		                && (mc.getCxRequestInfo() !=null)
		                && mc.getCxRequestInfo().equals(cxRequestInfo)) {
		            return mc;
		        }
		    }//instance
		}//for
		return null;
		*/
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionFactory#getLogWriter()
     */
    public PrintWriter getLogWriter() throws ResourceException {
        return this.logWriter;
    }

    /* (non-Javadoc)
     * @see javax.resource.spi.ManagedConnectionFactory#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }


	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapterAssociation#getResourceAdapter()
	 */
	public ResourceAdapter getResourceAdapter() {
//		org.apache.log4j.Category.getInstance(this.getClass()).debug("getResourceAdapter() ");
		return resourceAdapter;
	}


	/* (non-Javadoc)
	 * @see javax.resource.spi.ResourceAdapterAssociation#setResourceAdapter(javax.resource.spi.ResourceAdapter)
	 */
	public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
//		org.apache.log4j.Category.getInstance(this.getClass()).debug("setResourceAdapter() ");
		this.resourceAdapter = ra;
	}
    
}
