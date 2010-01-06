package com.amalto.core.objects.transformers.v2.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.ejb.local.ItemCtrl2LocalHome;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlLocal;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlLocalHome;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocalHome;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocalHome;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocalHome;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocalHome;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocalHome;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocalHome;
import com.amalto.core.objects.transformers.v2.util.TransformerGlobalContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.view.ejb.local.ViewCtrlLocal;
import com.amalto.core.objects.view.ejb.local.ViewCtrlLocalHome;
import com.amalto.core.util.XtentisException;


/**
 * @author Bruno Grieder
 * 
 * @ejb.bean
 *   		name="TransformerPluginV2Ctrl"
 *          type="Stateless"
 * 			view-type="local"
 * 			generate="false"
 * 
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 */
public abstract class TransformerPluginV2CtrlBean implements SessionBean {
	
	/**
	 * add one global context shared among the plugins by aiming
	 */
	protected TransformerGlobalContext globalContext;
	
    /**
     * 	   
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public void setGlobalContext(TransformerGlobalContext gcontext){
		this.globalContext=gcontext;
	}
    /**
     * 	   
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */	
	public TransformerGlobalContext getGlobalContext(){
		return globalContext;
	}
	
    /**
     * InboundPluginCtrlBean.java
     * Constructor
     * 
     */
    public TransformerPluginV2CtrlBean() {
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
     * Configuration received from outbound, typically a portlet
     * The default implementation stores the configuration string "as is"
     * 	
     * @throws XtentisException
     * 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void putConfiguration(String configuration) throws XtentisException {
    	storeConfiguration(configuration);
    }

    /**
     * Retrieves the configuration
     * The default implementation renders the configuration string "as stored"
     * and ignore the optional parameter 
     * 	
     * @throws XtentisException
     * 
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
     * @throws XtentisException
     * 
     */
    protected void storeConfiguration(String configuration) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("storeConfiguration() "+getPluginName());

    	try {
    		TransformerPluginV2POJO plugin = null;
            try {
	            plugin = ObjectPOJO.load(
	            		TransformerPluginV2POJO.class, 
	            		new ObjectPOJOPK(new String[]{getJNDIName()})
	            );
            } catch (Exception e) {
            }
    		if (plugin == null) {
    			//attempt to create ie
    			plugin = 
	    			new TransformerPluginV2POJO(
	    							getJNDIName(),
	    							configuration,
	    							""
	    			);
    			plugin.store();
    		} else {
    			plugin.setConfiguration(configuration);
    			plugin.store();
    		}
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to save the configuration of plugin "+getPluginName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    } 
    

    
    
    /**
     * Configuration received from outbound, typically a portlet
     * The default implementation stores the configuration string "as is"
     * 	
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void putServiceData(String serviceData) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("putServiceData() to"+getPluginName()+": "+serviceData);
    	storeServiceData(serviceData);
    }
    
    /**
     * Saves the servicedata for this service
     * The configuration can be an xml or a serialized object converted to a Base64 String
     * The serviceData are typically received from a service
     *
     * @throws XtentisException
     * 
     */
    protected void storeServiceData(String serviceData) throws XtentisException {

    	try {
    		TransformerPluginV2POJO plugin = null;
            try {
	            plugin = ObjectPOJO.load(
	            		TransformerPluginV2POJO.class, 
	            		new ObjectPOJOPK(new String[]{getJNDIName()})
	            );
            } catch (Exception e) {
            }
    		if (plugin == null) {
				//attempt to create ie
				plugin = 
	    			new TransformerPluginV2POJO(
	    							getJNDIName(),
	    							"",
	    							serviceData
	    			);
				plugin.store();
    		} else {
    			plugin.setServiceData(serviceData);
    			plugin.store();
    		}
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to save the service data of service "+getPluginName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    } 

    
    /**
     * Loads the configuration from the xml server
     * @throws XtentisException
     * 
     */
    protected String loadConfiguration() throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("loadConfiguration() "+getPluginName());
       	try {
       		TransformerPluginV2POJO plugin = null;
            try {
	            plugin = ObjectPOJO.load(
	            		TransformerPluginV2POJO.class, 
	            		new ObjectPOJOPK(new String[]{getJNDIName()})
	            );
            } catch (Exception e) {
            }
    		if (plugin == null)
    			return null;
    		else
    			return plugin.getConfiguration();
	    } catch (Exception e) {
    	    String err = "Unable to load the configuration of plugin "+getPluginName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    	
    }
    
    /**
     * Loads the configuration from the xml server
     * @throws XtentisException
     * 
     */
    protected String loadServiceData() throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("loadServiceData() "+getJNDIName());
       	try {
       		TransformerPluginV2POJO plugin = null;
            try {
	            plugin = ObjectPOJO.load(
	            		TransformerPluginV2POJO.class, 
	            		new ObjectPOJOPK(new String[]{getJNDIName()})
	            );
            } catch (Exception e) {
            }
    		if (plugin == null)
    			return null;
    		else
    			return plugin.getServiceData();
	    } catch (Exception e) {
    	    String err = "Unable to load the service data of service "+getPluginName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    	
    }
    

    
    /****************************************************
     * Plugin Specific
     ****************************************************/    


	/**
     * Returns the unique JNDI name of the service.<br> 
     * The JNDI name must be of the type amalto/local/service/[NAME]
     * where [NAME] matchs the pattern "[a-zA-Z][a-zA-Z0-9]*"
     * and is unique accross services
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getJNDIName() throws XtentisException;

    
    /**
     * Returns the short description of the service in the requested language if available, 
     * the default language otherwise.<br>
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getDescription(String twoLettersLanguageCode) throws XtentisException;

    /**
     * Returns the documentation of the service in the requested language if available, 
     * the default language otherwise.<br>
     * The documentation should provide information on<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;-the role of the plugin<br>
     *  &nbsp;&nbsp;&nbsp;&nbsp;-the parameters with a sample<br>
     *  &nbsp;&nbsp;&nbsp;&nbsp;-the list of the inputs<br>
     *  &nbsp;&nbsp;&nbsp;&nbsp;-the list of the outputs<br>
     *  
     *  @param twoLettersLanguageCode - the ISO 639 language code in small caps
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getDocumentation(String twoLettersLanguageCode) throws XtentisException;

    
    /**
     * Returns a list of the input variables descriptors 
     * @see TransformerPluginVariableDescriptor
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract ArrayList<TransformerPluginVariableDescriptor> getInputVariableDescriptors(String twoLettersLanguageCode) throws XtentisException;

    /**
     * Returns a list of the output variables descriptors
     * @see TransformerPluginVariableDescriptor
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract ArrayList<TransformerPluginVariableDescriptor> getOutputVariableDescriptors(String twoLettersLanguageCode) throws XtentisException;

    
    /**
     * Initializes the plugin. <br>
     * During atransformer execution, the init method is called once per Processing Step before the first execution of the step<br>
     * The plugin can add elements to the context which will be available during the execute phase<br>
     * Processing should be implemented in {@link #execute(TransformerPluginContext)} <br>
     *  
     *  @param context The plugin context
     *  @param compiledParameters - The Processing Step compiled parameters - see {@link #compileParameters(String)}
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void init(
    		TransformerPluginContext context,
    		String compiledParameters
    ) throws XtentisException;

    /**
     * Called by the transformer to request
     * that the plugin processing executes. (the execute phase)<br>
     * <br>
     * When content is ready, the plugin should not try to pass the content directly to the pipeline but<br> 
     * rather push it via the callback available in the context
     * <br>
     * <code> 
     * 	context.getPluginCallBack().contentIsReady(context);
     * </code>
     * 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void execute(TransformerPluginContext context) throws XtentisException;

 
    /**
     * Returns the XML schema for the parameters<br>
     * Can be null
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getParametersSchema() throws XtentisException;

    /**
     * Compile the Processing Step parameters<r>
     * Typically used to check that the parameters are correct when the Transformer is saved<br>
     * The compiled parameters are then passed on the init() call.<br>
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String compileParameters(String parameters) throws XtentisException;
    
    
    /**
     * Called by the transformer when the process completes.<br> 
     * Typically used to clean up pipeline variables or close connections.
     * The default implementation cleans up the TransformerPlugin Context
     *
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void end(TransformerPluginContext context) throws XtentisException {
    	context.removeAll();
    }
        


    /****************************************************
     * Utilities
     ****************************************************/
	    
	public String getPluginName() throws XtentisException{
		String[] paths = getJNDIName().split("\\/");
		return paths[paths.length-1];
	}
	
    
	/**
	 * Find items using xPaths
	 * 
	 * @throws XtentisException
     */   
//    protected Collection xPathsSearch(
//    		String dataClusterName,
//    		String pivotPath,
//    		Collection<String> viewablePaths,
//			IWhereItem where, 
//			int spellTreshold,
//			int skip,
//			int number) 
//    	throws XtentisException{
//    	
//    	try {
//    		return Util.getItemCtrl2LocalHome().create().xPathsSearch(
//    				new DataClusterPOJOPK(dataClusterName), 
//    				pivotPath, 
//    				viewablePaths, 
//    				where, 
//    				spellTreshold, 
//    				skip, 
//    				number
//    		);
//	    } catch (XtentisException xe) {
//	    	throw(xe);
//	    } catch (Exception e) {
//    	    String err = "Unable to run an xPathSearch from plugin "+getPluginName()
//    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
//    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
//    	    throw new XtentisException(err);
//	    }
//    	
//    }
    

	/********************************************************************************************
	 * 
	 * EJB Session beans getters
	 * 
	 ********************************************************************************************/
	
	//The only Static HashMap around (hopefully)
	private HashMap<String,EJBLocalHome> localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();

	public void flushLocalHomes() throws NamingException{
		localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();
	}
	
	public EJBLocalHome getLocalHome(String jndi) throws NamingException{
		EJBLocalHome localHome = null;
		localHome = localHomes.get(jndi);
		if (localHome == null) {
			localHome = (EJBLocalHome)new InitialContext().lookup(jndi);
			localHomes.put(jndi, localHome);
		}
//		dumpClass(localHome.getClass());
		return localHome;
	}
    
	
	protected  ItemCtrl2LocalHome getItemCtrl2LocalHome() throws NamingException {
		return (ItemCtrl2LocalHome) getLocalHome(ItemCtrl2LocalHome.JNDI_NAME);
	}
	protected  ItemCtrl2Local getItemCtrl2Local() throws NamingException,CreateException {
		return getItemCtrl2LocalHome().create();
	}

	protected  DataModelCtrlLocalHome getDataModelCtrlLocalHome() throws NamingException {
		return (DataModelCtrlLocalHome) getLocalHome(DataModelCtrlLocalHome.JNDI_NAME);
	}
	protected  DataModelCtrlLocal getDataModelCtrlLocal() throws NamingException,CreateException {
		return getDataModelCtrlLocalHome().create();
	}

	protected  DataClusterCtrlLocalHome getDataClusterCtrlLocalHome() throws NamingException {
		return (DataClusterCtrlLocalHome) getLocalHome(DataClusterCtrlLocalHome.JNDI_NAME);
	}
	protected  DataClusterCtrlLocal getDataClusterCtrlLocal() throws NamingException,CreateException {
		return getDataClusterCtrlLocalHome().create();
	}	

	protected  ViewCtrlLocalHome getViewCtrlLocalHome() throws NamingException {
		return (ViewCtrlLocalHome) getLocalHome(ViewCtrlLocalHome.JNDI_NAME);
	}
	protected  ViewCtrlLocal getViewCtrlLocal() throws NamingException,CreateException {
		return getViewCtrlLocalHome().create();
	}		
	
	protected  RoutingRuleCtrlLocalHome getRoutingRuleCtrlLocalHome() throws NamingException {
		return (RoutingRuleCtrlLocalHome) getLocalHome(RoutingRuleCtrlLocalHome.JNDI_NAME);
	}
	protected  RoutingRuleCtrlLocal getRoutingRuleCtrlLocal() throws NamingException,CreateException {
		return getRoutingRuleCtrlLocalHome().create();
	}		

	protected  StoredProcedureCtrlLocalHome getStoredProcedureCtrlLocalHome() throws NamingException {
		return (StoredProcedureCtrlLocalHome) getLocalHome(StoredProcedureCtrlLocalHome.JNDI_NAME);
	}
	protected  StoredProcedureCtrlLocal getStoredProcedureCtrlLocal() throws NamingException,CreateException {
		return getStoredProcedureCtrlLocalHome().create();
	}
	
	protected  TransformerV2CtrlLocalHome getTransformerV2CtrlLocalHome() throws NamingException {
		return (TransformerV2CtrlLocalHome) getLocalHome(TransformerV2CtrlLocalHome.JNDI_NAME);
	}
	protected  TransformerV2CtrlLocal getTransformerV2CtrlLocal() throws NamingException,CreateException {
		return getTransformerV2CtrlLocalHome().create();
	}		

	
	protected  MenuCtrlLocalHome getMenuCtrlLocalHome() throws NamingException {
		return (MenuCtrlLocalHome) getLocalHome(MenuCtrlLocalHome.JNDI_NAME);
	}
	protected  MenuCtrlLocal getMenuCtrlLocal() throws NamingException,CreateException {
		return getMenuCtrlLocalHome().create();
	}		
	
	protected  BackgroundJobCtrlLocalHome getBackgroundJobCtrlLocalHome() throws NamingException {
		return (BackgroundJobCtrlLocalHome) getLocalHome(BackgroundJobCtrlLocalHome.JNDI_NAME);
	}
	protected  BackgroundJobCtrlLocal getBackgroundJobCtrlLocal() throws NamingException,CreateException {
		return getBackgroundJobCtrlLocalHome().create();
	}		
	
	protected  ConfigurationInfoCtrlLocalHome getConfigurationInfoCtrlLocalHome() throws NamingException {
		return (ConfigurationInfoCtrlLocalHome) getLocalHome(ConfigurationInfoCtrlLocalHome.JNDI_NAME);
	}
	protected  ConfigurationInfoCtrlLocal getConfigurationInfoCtrlLocal() throws NamingException,CreateException {
		return getConfigurationInfoCtrlLocalHome().create();
	}		
	
	
	private  ConnectionFactory cachedConnectionFactory = null;
    protected  Connection getConnection(String JNDIName) throws XtentisException {
    	try {
    		if (cachedConnectionFactory == null)
    			cachedConnectionFactory = (ConnectionFactory)(new InitialContext()).lookup(JNDIName);
    		return cachedConnectionFactory.getConnection();
    	} catch (Exception e) {
    		String err= "JNDI lookup error: "+e.getClass().getName() + ": " + e.getLocalizedMessage();			
			throw new XtentisException(err);
    	}
    }
	
	


	
   
}