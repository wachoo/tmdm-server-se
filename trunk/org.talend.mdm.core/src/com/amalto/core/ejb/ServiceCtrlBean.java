package com.amalto.core.ejb;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;

import com.amalto.core.ejb.local.ServiceLocal;
import com.amalto.core.ejb.local.ServiceLocalHome;
import com.amalto.core.ejb.remote.ServicePK;
import com.amalto.core.ejb.remote.ServiceValue;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="ServiceCtrl" 
 *           display-name="Name for ServiceCtrl"
 *           description="Description for ServiceCtrl"
 * 		  	local-jndi-name = "amalto/local/core/servicectrl"
 *           type="Stateless"
 *           view-type="local"
 *           generate = "false"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 * @ejb.ejb-ref 
 * 		ejb-name = "ServiceBean" 
 * 		ref-name = "ejb/ServiceBean" 
 * 		view-type = "local"
 * 
 */
public abstract class ServiceCtrlBean implements SessionBean {
  
	public static final int INBOUND = 1;
	public static final int OUTBOUND = 2;
    
    /**
     * IServiceCtrlBean.java
     * Constructor
     * 
     */
    public ServiceCtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
    }
    
    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {}
    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    

    /**
     * To be Implemented.
     * Returns the unique JNDI name of the service. 
     * The JNDI name must be of the type amalto/local/service/[NAME]
     * where [NAME] matchs the pattern "[a-zA-Z][a-zA-Z0-9]*"
     * and is unique accross services
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getJNDIName() throws XtentisException;

    
    /**
     * To be Implemented.
     * Returns the description of the service.
     * Can be null
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getDescription(String twoLettersLanguageCode) throws XtentisException;


    /**
     * @author achen
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
    	return "";
    }
    /**
     * To be Implemented.
     * Starts if needed the service
     * Can be null
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void start() throws XtentisException;


    
    /**
     * To be Implemented.
     * Stops if needed the service
     * Can be null
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void stop() throws XtentisException;

    
    /**
     * To be Implemented.
     * Returns a status of the service
     * Can be null
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getStatus() throws XtentisException;
    
    /**
     * To be implemented
     * Runs the service. 
     * The object received in an HashMap made of
     * 	-username - String
     * 	-password - String
     * 	-contentType - String
     * 	-charset - String
     * 	-bytes - bytes[]
     * 	-paramameters - HashMap
     * 
     * 	
     * @throws EJBException
     * @return Serializable - a serializable Object to be passed backed to the connector
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract Serializable receiveFromOutbound(HashMap<String,Serializable> map) throws XtentisException;

    /**
     * To be implemented
     * Runs the service. 
     * The item received in an XML String
     * 
     * @param itemPK - the item that triggered a Routing Rule <hich created the Active Routing Order
     * @param routingOrderID - the routing Order ID of the routing rule that called - From 2.19.0, the Routing Order is an ActiveRoutingOrderPOJO
     * @param parameters - the routing rules parameters
     * @return this value is appended at the end of the message field of the Routing Order
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String parameters) throws XtentisException;
    
    
    /**
     * To be implemented
     * To request and get the response from other applications 
     * 
     * @param command - used to call different pull method in service Object
     * @param parameters - incoming parameters, may be in xml format 
     * @param schedulePlanID - the ID of schedule plan, if in schedule mode 
     * @return Serializable - a serializable Object to be passed backed to the system
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract Serializable fetchFromOutbound(String command, String parameters, String schedulePlanID) throws XtentisException;
    
    
    /**
     * Configuration received from outbound, typically a portlet
     * The default implementation stores the configuration string "as is"
     * 	
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void putConfiguration(String configuration) throws XtentisException {
    	storeConfiguration(configuration);
    }

    /**
     * Returns the XML schema for the configuration<br>
     * Can be null
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public String getConfigurationSchema() throws XtentisException{
    	return null;
    }
    /**
     * return default the configuration<br>
     * Can be null
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public String getDefaultConfiguration() throws XtentisException{
    	return null;
    }    
    /**
     * Retrieves the configuration
     * The default implementation renders the configuration string "as stored"
     * and ignore the optional parameter 
     * 	
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String getConfiguration(String optionalParameter) throws XtentisException {
    	return loadConfiguration();
    }
    
    
    /**
     * Saves the servicedata for this service
     * The configuration can be an xml or a serialized object converted to a Base64 String
     * @throws EJBException
     * 
     */
    public void storeConfiguration(String configuration) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("storeConfiguration() "+getServiceName());

    	try {
    		ServiceLocalHome home = getServiceLocalHome();
    		ServiceLocal service =  home.findIfExists(new ServicePK(getJNDIName().replaceAll("\\/", "_").replaceAll(":", "_")));
    		if (service == null) {
    			//attempt to create ie
    			service = home.create(
    					new ServiceValue(
    							getJNDIName().replaceAll("\\/", "_").replaceAll(":", "_"),
    							configuration,
    							""
    					)
    			);
    		} else {
    			service.setConfiguration(configuration);
    		}
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to save the configuration of service "+getServiceName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    } 
    

    
    
    /**
     * Configuration received from outbound, typically a portlet
     * The default implementation stores the configuration string "as is"
     * 	
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void putServiceData(String serviceData) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("putServiceData() to"+getServiceName()+": "+serviceData);
    	storeServiceData(serviceData);
    }
    
    /**
     * Saves the servicedata for this service
     * The configuration can be an xml or a serialized object converted to a Base64 String
     * The serviceData are typically received from a service
     *
     * @throws EJBException
     * 
     */
    public void storeServiceData(String serviceData) throws XtentisException {
//    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("storeServiceData() "+getServiceName());

    	try {
    		ServiceLocalHome home = getServiceLocalHome();
    		ServiceLocal service =  home.findIfExists(new ServicePK(getJNDIName().replaceAll("\\/", "_").replaceAll(":", "_")));
    		if (service == null) {
    			//attempt to create
    			service = home.create(
    					new ServiceValue(
    							getJNDIName(),
    							"",
    							serviceData
    					)
    			);
    		} else {
    			service.setServiceData(serviceData);
    		}
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to save the service data of service "+getServiceName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    } 

    
    /**
     * Loads the configuration from the xml server
     * @throws EJBException
     * 
     */
    public String loadConfiguration() throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("loadConfiguration() "+getServiceName());
       	try {
    		ServiceLocalHome home = getServiceLocalHome();
    		ServiceLocal service =  home.findIfExists(new ServicePK(getJNDIName().replaceAll("\\/", "_").replaceAll(":", "_")));
    		if (service == null)
    			return null;
    		else
    			return service.getConfiguration();
	    } catch (XtentisException xe) {
	    	throw(xe);
	    } catch (Exception e) {
    	    String err = "Unable to load the configuration of service "+getServiceName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    	
    }
    
    /**
     * Loads the configuration from the xml server
     * @throws EJBException
     * 
     */
    public String loadServiceData() throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("loadServiceData() "+getJNDIName());
       	try {
    		ServiceLocalHome home = getServiceLocalHome();
    		ServiceLocal service =  home.findIfExists(new ServicePK(getJNDIName().replaceAll("\\/", "_").replaceAll(":", "_")));
    		if (service == null)
    			return null;
    		else
    			return service.getServiceData();
	    } catch (XtentisException xe) {
	    	throw(xe);
	    } catch (Exception e) {
    	    String err = "Unable to load the service data of service "+getServiceName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    	
    }

    
	private String getServiceName() throws XtentisException{
		String[] paths = getJNDIName().split("\\/");
		return paths[paths.length-1];
	}

	/**
	 * Helper class to retrieve the service local home
	 * @return
	 * 		The {@link ServiceLocalHome}
	 * @throws NamingException
	 */
	protected  ServiceLocalHome getServiceLocalHome() throws NamingException {
		return (ServiceLocalHome) new InitialContext().lookup(ServiceLocalHome.JNDI_NAME);
	}
	
	/********************************************************************************************
	 * 
	 * EJB Session beans getters
	 * 
	 ********************************************************************************************/
	
	
	
//	//The only Static HashMap around (hopefully)
//	private HashMap<String,EJBLocalHome> localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();
//
//	public void flushLocalHomes() throws NamingException{
//		localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();
//	}
	
//	public EJBLocalHome getLocalHome(String jndi) throws NamingException{
//		EJBLocalHome localHome = null;
//		localHome = localHomes.get(jndi);
//		if (localHome == null) {
//			localHome = (EJBLocalHome)new InitialContext().lookup(jndi);
//			localHomes.put(jndi, localHome);
//		}
//		return localHome;
//	}
    
	
	
//	protected  ServiceLocalHome getServiceLocalHome() throws NamingException {
//		return (ServiceLocalHome) getLocalHome(ServiceLocalHome.JNDI_NAME);
//	}
//	
//	protected  ItemCtrl2LocalHome getItemCtrl2LocalHome() throws NamingException {
//		return (ItemCtrl2LocalHome) getLocalHome(ItemCtrl2LocalHome.JNDI_NAME);
//	}
//	protected  ItemCtrl2Local getItemCtrl2Local() throws NamingException,CreateException {
//		return getItemCtrl2LocalHome().create();
//	}
//	/**
//	 * @deprecated use #getItemCtrl2Local()
//	 */
//	protected ItemCtrl2Local getItemCtrlLocal() throws NamingException,CreateException{
//		return getItemCtrl2Local();	   	   
//	}
//
//	protected  DataModelCtrlLocalHome getDataModelCtrlLocalHome() throws NamingException {
//		return (DataModelCtrlLocalHome) getLocalHome(DataModelCtrlLocalHome.JNDI_NAME);
//	}
//	protected  DataModelCtrlLocal getDataModelCtrlLocal() throws NamingException,CreateException {
//		return getDataModelCtrlLocalHome().create();
//	}
//
//	protected  DataClusterCtrlLocalHome getDataClusterCtrlLocalHome() throws NamingException {
//		return (DataClusterCtrlLocalHome) getLocalHome(DataClusterCtrlLocalHome.JNDI_NAME);
//	}
//	protected  DataClusterCtrlLocal getDataClusterCtrlLocal() throws NamingException,CreateException {
//		return getDataClusterCtrlLocalHome().create();
//	}	
//
//	protected  ViewCtrlLocalHome getViewCtrlLocalHome() throws NamingException {
//		return (ViewCtrlLocalHome) getLocalHome(ViewCtrlLocalHome.JNDI_NAME);
//	}
//	protected  ViewCtrlLocal getViewCtrlLocal() throws NamingException,CreateException {
//		return getViewCtrlLocalHome().create();
//	}		
//	
//	protected  RoutingRuleCtrlLocalHome getRoutingRuleCtrlLocalHome() throws NamingException {
//		return (RoutingRuleCtrlLocalHome) getLocalHome(RoutingRuleCtrlLocalHome.JNDI_NAME);
//	}
//	protected  RoutingRuleCtrlLocal getRoutingRuleCtrlLocal() throws NamingException,CreateException {
//		return getRoutingRuleCtrlLocalHome().create();
//	}		
//
//	protected  StoredProcedureCtrlLocalHome getStoredProcedureCtrlLocalHome() throws NamingException {
//		return (StoredProcedureCtrlLocalHome) getLocalHome(StoredProcedureCtrlLocalHome.JNDI_NAME);
//	}
//	protected  StoredProcedureCtrlLocal getStoredProcedureCtrlLocal() throws NamingException,CreateException {
//		return getStoredProcedureCtrlLocalHome().create();
//	}
//	
//	protected  TransformerV2CtrlLocalHome getTransformerV2CtrlLocalHome() throws NamingException {
//		return (TransformerV2CtrlLocalHome) getLocalHome(TransformerV2CtrlLocalHome.JNDI_NAME);
//	}
//	protected  TransformerV2CtrlLocal getTransformerV2CtrlLocal() throws NamingException,CreateException {
//		return getTransformerV2CtrlLocalHome().create();
//	}		
//	
//	protected  RoleCtrlLocalHome getRoleCtrlLocalHome() throws NamingException {
//		return (RoleCtrlLocalHome) getLocalHome(RoleCtrlLocalHome.JNDI_NAME);
//	}
//	protected  RoleCtrlLocal getRoleCtrlLocal() throws NamingException,CreateException {
//		return getRoleCtrlLocalHome().create();
//	}
//	
//	protected  MenuCtrlLocalHome getMenuCtrlLocalHome() throws NamingException {
//		return (MenuCtrlLocalHome) getLocalHome(MenuCtrlLocalHome.JNDI_NAME);
//	}
//	protected  MenuCtrlLocal getMenuCtrlLocal() throws NamingException,CreateException {
//		return getMenuCtrlLocalHome().create();
//	}		
//	
//	protected  BackgroundJobCtrlLocalHome getBackgroundJobCtrlLocalHome() throws NamingException {
//		return (BackgroundJobCtrlLocalHome) getLocalHome(BackgroundJobCtrlLocalHome.JNDI_NAME);
//	}
//	protected  BackgroundJobCtrlLocal getBackgroundJobCtrlLocal() throws NamingException,CreateException {
//		return getBackgroundJobCtrlLocalHome().create();
//	}		
//	
//	protected  ConfigurationInfoCtrlLocalHome getConfigurationInfoCtrlLocalHome() throws NamingException {
//		return (ConfigurationInfoCtrlLocalHome) getLocalHome(ConfigurationInfoCtrlLocalHome.JNDI_NAME);
//	}
//	protected  ConfigurationInfoCtrlLocal getConfigurationInfoCtrlLocal() throws NamingException,CreateException {
//		return getConfigurationInfoCtrlLocalHome().create();
//	}		
	

	//We do not want to cache so that we ca redeploy connectors as needed
//	private  ConnectionFactory cachedConnectionFactory = null;
    protected  Connection getConnection(String JNDIName) throws XtentisException {
    	try {
//    		if (cachedConnectionFactory == null) - we do not want to cache so that we 
//    			cachedConnectionFactory = (ConnectionFactory)(new InitialContext()).lookup(JNDIName);
//    		return cachedConnectionFactory.getConnection();
    		return ((ConnectionFactory)(new InitialContext()).lookup(JNDIName)).getConnection();
    	} catch (Exception e) {
    		String err= "JNDI lookup error: "+e.getClass().getName() + ": " + e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(Util.class).error(err,e);
			throw new XtentisException(err);
    	}
    }
	
	
    
    /*
	private ServiceLocalHome getServiceLocalHome() throws ResourceException{
		try {
			InitialContext ctx = new InitialContext();
			return ((ServiceLocalHome) ctx.lookup("amalto/local/core/service"));
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
	}
	*/
	
	
	/*
	protected RoutingOrderLocalHome getRoutingOrderLocal() throws ResourceException{
		try {
			InitialContext ctx = new InitialContext();
			return (RoutingOrderLocalHome) ctx.lookup("amalto/local/core/routingorder");
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
	}
	
	protected ItemCtrl2Local getItemCtrlLocal() throws ResourceException{
		try {
			return ItemCtrl2Util.getLocalHome().create();
//			InitialContext ctx = new InitialContext();
			//((ItemCtrl2LocalHome) ctx.lookup(ItemCtrl2LocalHome.JNDI_NAME)).create();
			
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
	}
	   
	protected InboundAdaptorLocalHome getInboundAdaptorLocalHome() throws ResourceException{
		try {
			InitialContext ctx = new InitialContext();
			return ((InboundAdaptorLocalHome) ctx.lookup("amalto/local/core/inboundadaptor"));
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
	}

	protected OutboundAdaptorLocalHome getOutboundAdaptorLocalHome() throws ResourceException{
		try {
			InitialContext ctx = new InitialContext();
			return ((OutboundAdaptorLocalHome) ctx.lookup("amalto/local/core/outboundadaptor"));
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
	}
	
	@Deprecated
	protected SourceLocalHome getSourceLocalHome() throws ResourceException{
		try {
			InitialContext ctx = new InitialContext();
			return ((SourceLocalHome) ctx.lookup("amalto/local/core/source"));
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
	}

	@Deprecated
	protected DestinationLocalHome getDestinationLocalHome() throws ResourceException{
		try {
			InitialContext ctx = new InitialContext();
			return ((DestinationLocalHome) ctx.lookup("amalto/local/core/destination"));
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
	}
	
	protected RoutingEngineLocal getRoutingEngineLocal() throws ResourceException{
		try {
//		InitialContext ctx = new InitialContext();
//		return ((RoutingEngineLocalHome) ctx.lookup("amalto/local/core/routingengine")).create();
			return RoutingEngineUtil.getLocalHome().create();
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
	}
	
    protected Connection getConnection(String JNDIName) throws RemoteException {
    	try {
    		return ((ConnectionFactory)(new InitialContext()).lookup(JNDIName)).getConnection();
    	} catch (Exception e) {
    		throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
    	}
    }
    */
    
}