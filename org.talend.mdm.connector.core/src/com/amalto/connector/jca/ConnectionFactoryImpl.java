package com.amalto.connector.jca;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;



/**
 * 
 * @author bgrieder
 */
public class ConnectionFactoryImpl implements ConnectionFactory{

    
    /** The ManagedConnectionFactory that created this ConnectionFactory */
    private ManagedConnectionFactory mcf = null;

    /** The ConnectionManager associated with this ConnectionFactory */
    private ConnectionManager cxManager = null;
    
    /** The Singleton instace on the Recordfactory **/
    private RecordFactoryImpl recordFactory = null;
    
    private Reference reference = null;

    /**
     * This adapter metaData
     */
    private ResourceAdapterMetaData metaData = null;
    
    
    /**
     * Create a ResourceAdapter Connection Factory with the given ManagedConectionFactory and
     * ConnectionManager instance. 
     * @see ManagedConnectionFactory
     *
     * @param mcf ManagedConnectionFactory instance
     * @param cxManager ConnectionManager instance if any
     */
    public ConnectionFactoryImpl(ManagedConnectionFactory mcf, ConnectionManager cxManager) {
        super();
        this.mcf = mcf;
        this.cxManager = cxManager;
    }

    /*
     *  (non-Javadoc)
     * @see javax.resource.cci.ConnectionFactory#getConnection()
     */
    public Connection getConnection() throws ResourceException {
        Connection conx = (Connection) cxManager.allocateConnection(mcf, null);
        org.apache.log4j.Logger.getLogger(this.getClass()).info("getConnection() : Connection established by user "
        		+conx.getMetaData().getUserName()+" to "
        		+conx.getMetaData().getEISProductName());
        return conx;
    }
    
    
    /*
     *  (non-Javadoc)
     * @see javax.resource.cci.ConnectionFactory#getConnection(javax.resource.cci.ConnectionSpec)
     */
    public Connection getConnection(ConnectionSpec properties) throws ResourceException {
        
        // an username/password connection request info
        ConnectionSpecImpl cons = (ConnectionSpecImpl) properties;
        
        ConnectionRequestInfoImpl crinfo = new ConnectionRequestInfoImpl();
        crinfo.setUsername(cons.getUsername());
        crinfo.setPassword(cons.getPassword());
        
        Connection conx = (Connection) cxManager.allocateConnection(mcf, crinfo);
        org.apache.log4j.Logger.getLogger(this.getClass()).info("getConnection(): Connection established by user "
        		+conx.getMetaData().getUserName()+" to "
        		+conx.getMetaData().getEISProductName());
        return conx;
    }
    
    
    /*
     *  (non-Javadoc)
     * @see javax.resource.cci.ConnectionFactory#getMetaData()
     */
    public ResourceAdapterMetaData getMetaData() throws ResourceException {    
        
        if (this.metaData== null) metaData = new ResourceAdapterMetaDataImpl();
        return this.metaData;
    }

    
    /*
     *  (non-Javadoc)
     * @see javax.resource.cci.ConnectionFactory#getRecordFactory()
     */
    public RecordFactory getRecordFactory() throws ResourceException {    
        if (this.recordFactory == null) this.recordFactory = new RecordFactoryImpl();
        return this.recordFactory;
    }

    
    /*
     *  (non-Javadoc)
     * @see javax.resource.Referenceable#setReference(javax.naming.Reference)
     */
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    
    /*
     *  (non-Javadoc)
     * @see javax.naming.Referenceable#getReference()
     */
    public Reference getReference() throws NamingException {
        return this.reference;
    }
}
