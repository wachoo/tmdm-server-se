package com.amalto.core.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.ejb.remote.ServicePK;
import com.amalto.core.ejb.remote.ServiceValue;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


/**
 * @author Bruno Grieder
 * 
 * @ejb.bean 	name="Service"
 *           	display-name="Service"
 *           	description="Service"
 *           	jndi-name="amalto/remote/core/service"
 * 		  		local-jndi-name = "amalto/local/core/service"
 *           	type="BMP"
 *           	view-type="local"
 *           	reentrant="true"
 * 
 * @ejb.value-object 
 * 
 * @ejb.pk
 * 
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 */
public abstract class ServiceBean implements EntityBean {
	
	// * @ejb.ejb-ref 
	// * 		ejb-name = "XmlServerSLWrapper" 
	// * 		ref-name = "ejb/XmlServerSLWrapperLocal" 
	// * 		view-type = "local"
	
	
	private static String CLUSTER = "amaltoOBJECTSservices";
   	
    EntityContext context;
    
    /**
     * 
     */
    public ServiceBean() {
        super();
        
    }

    /* (non-Javadoc)
     * @see javax.ejb.EntityBean#setEntityContext(javax.ejb.EntityContext)
     */
    public void setEntityContext(EntityContext ctx)
        throws EJBException,
        RemoteException {
        context = ctx;
    }
    /* (non-Javadoc)
     * @see javax.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() throws EJBException, RemoteException {
        context = null;
    }

    /* (non-Javadoc)
     * @see javax.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
    }

    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public ServicePK ejbCreate(ServiceValue vo) throws javax.ejb.CreateException {
        
        try {            
	    	//get the universe and revision ID
	    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
	    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            
           	setServiceName(vo.getServiceName());
            setConfiguration(vo.getConfiguration());
            setServiceData(vo.getServiceData());
             
            //create the doc
            server.start(CLUSTER);
            long res = server.putDocumentFromString(
            	serialize(), 
            	getServiceName(),
				CLUSTER,
				revisionID
			);

            if (res==-1) {
                server.rollback(CLUSTER);
                throw new CreateException("Check the XML Server Wrapper logs");
            } else {
                server.commit(CLUSTER);
            }

            return vo.getPrimaryKey();
            
        } catch (XtentisException e) {
        	throw new CreateException(e.getMessage());
	    } catch (Exception e) {
    	    String err = "Unable to create the service: "+vo.getServiceName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new CreateException(err);
	    }
    }

    /**
     * Post Create method
     */
    public void ejbPostCreate(ServiceValue vo) throws javax.ejb.CreateException {
        //org.apache.log4j.Category.getInstance(this.getClass()).debug("ejbPostCreate() ");
    }

    /* (non-Javadoc)
     * @see javax.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() throws EJBException, RemoteException {
        
        ServicePK pk = (ServicePK)context.getPrimaryKey();
        try {
            
	    	//get the universe and revision ID
	    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
	    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();

            //retrieve the Service
            String Service = server.getDocumentAsString(revisionID, CLUSTER, pk.getServiceName());
                        
            if (Service==null) {
                throw new EJBException("Service not found: "+pk.getServiceName()+" in cluster "+CLUSTER);
            }
           
            //parse the results
            Document d = Util.parse(Service);
                      
            //Build the result
            Element root = d.getDocumentElement();

            setServiceName(pk.getServiceName());
            setConfiguration(Util.getFirstTextNode(root, "configuration"));
            setServiceData(Util.getFirstTextNode(root, "servicedata"));
	        	   
        } catch (XtentisException e) {
        	throw new EJBException(e.getMessage());
	    } catch (Exception e) {
    	    String err = "Unable to load the service: "+getServiceName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new EJBException(err);
	    } 
    }

    /* (non-Javadoc)
     * @see javax.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove()
        throws RemoveException,
        EJBException,
        RemoteException {
        
    	ServicePK pk = getServiceValue().getPrimaryKey();
        
        try {

	    	//get the universe and revision ID
	    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
	    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
 
            //remove the doc
            long res = server.deleteDocument(revisionID, CLUSTER,pk.getServiceName());
            if (res==-1) throw new EJBException("Check the Xml Server Wrapper logs");
        
        } catch (XtentisException e) {
        	throw new RemoveException(e.getMessage());
	    } catch (Exception e) {
    	    String err = "Unable to remove the service "+getServiceName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new RemoveException(err);
	    }  

    }

    /* (non-Javadoc)
     * @see javax.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() throws EJBException, RemoteException {
        	
		try {
	    	//get the universe and revision ID
	    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
	    	
	        //get the xml server wrapper
	        XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
			
            //store
            server.start(CLUSTER);
            if ( -1 == server.putDocumentFromString(
                	serialize(), 
                	getServiceName(),
					CLUSTER,
					revisionID
				))
            	throw new EJBException("Check the Xml Sever Wrapper logs");
            server.commit(CLUSTER);
		} catch (XtentisException e) {
			throw new EJBException(e.getMessage());
	    } catch (Exception e) {
    	    String err = "Unable to store the service "+getServiceName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new EJBException(err);
	    } 

    }
    
    
    public ServicePK ejbFindByPrimaryKey(ServicePK primaryKey) throws FinderException{
        
        try {
	    	//get the universe and revision ID
	    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
	    	
	        //get the xml server wrapper
	        XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
	        server.createCluster(revisionID, CLUSTER);
	        //attempt to retrieve the Service
	        String document = server.getDocumentAsString(
	        		revisionID,
	        		CLUSTER, 
	        		primaryKey.getServiceName()
	        );
	        	    
	        if (document==null) throw new ObjectNotFoundException(
	                "The service '"+primaryKey.getServiceName()+"' does not exist"+
	                (revisionID == null ? "" : " for revision "+revisionID)
	        );
	        	        
	        return primaryKey;
	        
        } catch (XtentisException e) {
        	throw new FinderException(e.getMessage());
	    } catch (Exception e) {
		    String err = "Unable to fetch the Service "+primaryKey.getServiceName()
		    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
		    throw new FinderException(err);
	    }       
    }

    public ServicePK ejbFindIfExists(ServicePK primaryKey) throws FinderException{
        
        try {
	    	//get the universe and revision ID
	    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
	    	
	        //get the xml server wrapper
	        XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
	        server.createCluster(revisionID, CLUSTER);
	        //attempt to retrieve the Service	        
	        String document = server.getDocumentAsString(revisionID, CLUSTER, primaryKey.getServiceName());
	       		        
	        return document==null ? null : primaryKey;
	        
        } catch (XtentisException e) {
        	return null;
	    } catch (Exception e) {
		    String debug = "This service does not exist: "+primaryKey.getServiceName()
		    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    org.apache.log4j.Logger.getLogger(this.getClass()).debug("ejbFindIfExists() "+debug, e);
		    return null;
	    }       
    }
    
    public Collection<ServicePK> ejbFindAll() throws FinderException{
        //org.apache.log4j.Category.getInstance(this.getClass()).debug("ejbFindAll() ");
        
         try {
             ArrayList<ServicePK> l = new ArrayList<ServicePK>();
             
 	    	//get the universe and revision ID
 	    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
 	    	if (universe == null) {
 	    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
 	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
 	    		throw new XtentisException(err);
 	    	}
 	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
 	    	
 	        //get the xml server wrapper
 	        XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
	        server.createCluster(revisionID, CLUSTER);
             //retrieve all the docuents
             String[] uris = server.getAllDocumentsUniqueID(revisionID, CLUSTER);
             
             if (uris!=null) {
		         for (int i = 0; i < uris.length; i++) {
	            	 l.add(new ServicePK(uris[i]));
		         }
             }
             
             return l;
        } catch (XtentisException e) {
        	throw new FinderException(e.getMessage());
        } catch (Exception e) {
            String err = "Unable to retrieve all the services"
            		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
            throw new FinderException(err);
        } 
         
     }


    /**
     * @ejb.interface-method   view-type="local"
     * @ejb.value-object 
     * @ejb.persistence
     * @ejb.pk-field   
     * 
     */
    public abstract String getServiceName();
    /**
     *
     */
    public abstract void setServiceName(String name);
    

    
    /**
     * @ejb.interface-method   view-type="local"
     * @ejb.value-object
     * @ejb.persistence 
     */
    public abstract String getConfiguration();
    /**
     * @ejb.interface-method   view-type="local"
     */
    public abstract void setConfiguration(String configuration);


    /**
     * Any Data hat is not configuration
     * @ejb.interface-method   view-type="local"
     * @ejb.value-object
     * @ejb.persistence 
     */
    public abstract String getServiceData();
    /**
     * @ejb.interface-method   view-type="local"
     */
    public abstract void setServiceData(String serviceData);
    
    
    /**
     * @ejb.interface-method   view-type="local"
     */
    public abstract com.amalto.core.ejb.remote.ServiceValue getServiceValue();
    
    
    /**
     * Serializes the object to an xml string
     * @return the xml string
     * 
     */
    private String serialize() throws XtentisException{
   	
        //serialization
        String service =
            "<service>" +
            "	<name><![CDATA["+getServiceName()+"]]></name>" +
            "	<configuration><![CDATA["+(getConfiguration()==null ? "" : getConfiguration())+"]]></configuration>" +
            "	<servicedata><![CDATA["+(getServiceData()==null ? "" : getServiceData())+"]]></servicedata>" +
            "</service>";             
        return service;
    }
    
  
}
