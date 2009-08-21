package com.amalto.core.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.util.TransformerPluginCallBack;
import com.amalto.core.util.TransformerPluginContext;
import com.amalto.core.util.TypedContent;
import com.amalto.core.util.XtentisException;


/**
 * @author bgrieder
 * @deprecated - use TransformerV2 package
 * 
 * @ejb.bean
 *   	name="TransformerPluginCtrl"
 *      type="Stateless"
 * 		view-type="local"
 * 		generate="false"
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
public abstract class TransformerPluginCtrlBean implements SessionBean {
  
    
    /**
     * InboundPluginCtrlBean.java
     * Constructor
     * 
     */
    public TransformerPluginCtrlBean() {
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
     * @throws EJBException
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
     * @throws EJBException
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
     * @throws EJBException
     * 
     */
    protected void storeConfiguration(String configuration) throws XtentisException {
    	org.apache.log4j.Category.getInstance(this.getClass()).debug("storeConfiguration() "+getPluginName());

    	try {
    		TransformerPluginPOJO plugin = ObjectPOJO.load(
            		TransformerPluginPOJO.class, 
            		new ObjectPOJOPK(new String[]{getJNDIName()})
            );
    		if (plugin == null) {
    			//attempt to create ie
    			plugin = 
	    			new TransformerPluginPOJO(
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
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
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
    	org.apache.log4j.Category.getInstance(this.getClass()).debug("putServiceData() to"+getPluginName()+": "+serviceData);
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
    protected void storeServiceData(String serviceData) throws XtentisException {

    	try {
    		TransformerPluginPOJO plugin = null;
            try {
	            plugin = ObjectPOJO.load(
	            		TransformerPluginPOJO.class, 
	            		new ObjectPOJOPK(new String[]{getJNDIName()})
	            );
            } catch (Exception e) {
            }
            if (plugin == null) {
            	//attempt to create it
				plugin = 
	    			new TransformerPluginPOJO(
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
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    } 

    
    /**
     * Loads the configuration from the xml server
     * @throws EJBException
     * 
     */
    protected String loadConfiguration() throws XtentisException {
    	org.apache.log4j.Category.getInstance(this.getClass()).debug("loadConfiguration() "+getPluginName());
       	try {
       		TransformerPluginPOJO plugin = null;
            try {
	            plugin = ObjectPOJO.load(
	            		TransformerPluginPOJO.class, 
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
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    	
    }
    
    /**
     * Loads the configuration from the xml server
     * @throws EJBException
     * 
     */
    protected String loadServiceData() throws XtentisException {
    	org.apache.log4j.Category.getInstance(this.getClass()).debug("loadServiceData() "+getJNDIName());
       	try {
       		TransformerPluginPOJO plugin = null;
            try {
	            plugin = ObjectPOJO.load(
	            		TransformerPluginPOJO.class, 
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
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    	
    }
    
    
    /****************************************************
     * Plugin Specific
     ****************************************************/    
    
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
     * To be Implemented.
     * Returns the description of the service.
     * Can be null
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getDocumentation(String twoLettersLanguageCode) throws XtentisException;
    
    /**
     * Initializes the plugin. 
     * The plugin must returns the context passed as parameter. 
     * The plugin can add elements to the context
     * Processing should not start before start() is called
     * The plugin should call the callback contentReady  method 
     * when content is ready for output and passed back the pluginHandle.
     * The plugin must call the callback done method when it has finished processing. 
     *  
     *  @return the Context
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract TransformerPluginContext init(
    		int pluginHandle,
    		String parameters,
    		TransformerPluginContext context,
    		TransformerPluginCallBack callBack
    ) throws XtentisException;

    /**
     * Called by the transformer to request 
     * that the plugin processing executes
     *
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract void execute(TransformerPluginContext context, TypedContent content) throws XtentisException;
    
    /**
     * Called by the transformer to request 
     * that the plugin processing stops
     *
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */    
    public abstract void stop(TransformerPluginContext context) throws XtentisException;
    
    
    /**
     * Called by the transformer when the process completes 
     * Typically used to clean up pipeline variables
     *
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract TransformerPluginContext end(TransformerPluginContext context) throws XtentisException;
    
    /**
     * To be Implemented.
     * Check wheteher a particular Content-type is accepted as Input (small caps)
     * Can be null
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract boolean acceptsContentType(String contentType, String parameters) throws XtentisException;

    
    /**
     * To be Implemented.
     * The plugin output content-type. It may include charset information e.g.
     * text/plain; charset=utf-8
     * Can be null
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getOutputContentType(String parameters) throws XtentisException;
    

    /**
     * To be Implemented.
     * Returns the XML schema for the parameters
     * Can be null
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String getParametersSchema() throws XtentisException;

    
    
    /**
     * To be Implemented.
     * Compile the parameters in the form it must be saved for use by the plugin
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String compileParameters(String parameters) throws XtentisException;
    
    /**
     * To be Implemented.
     * Decompile the parameters from thr form it is saved in to the human readable form
     *  
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract String decompileParameters(String parameters) throws XtentisException;
    
    
    
	public String getPluginName() throws XtentisException{
		String[] paths = getJNDIName().split("\\/");
		return paths[paths.length-1];
	}
	

	
    /****************************************************
     * Variables Management
     ****************************************************/
	
	protected void putLocal(TransformerPluginContext context, String variableName, Object value) {
		int pluginHandle = ((Integer)context.get(TransformerCtrlBean.CTX_PLUGIN_NUMBER)).intValue();
		context.put("_plugin_"+pluginHandle+"."+variableName, value);
	}
	
	protected Object getLocal(TransformerPluginContext context, String variableName) {
		int pluginHandle = ((Integer)context.get(TransformerCtrlBean.CTX_PLUGIN_NUMBER)).intValue();
		return context.get("_plugin_"+pluginHandle+"."+variableName);
	}
	
	protected Object removeLocal(TransformerPluginContext context, String variableName, Object value) {
		int pluginHandle = ((Integer)context.get(TransformerCtrlBean.CTX_PLUGIN_NUMBER)).intValue();
		return context.remove("_plugin_"+pluginHandle+"."+variableName);
	}

	protected void removeAllLocal(TransformerPluginContext context) {
		int pluginHandle = ((Integer)context.get(TransformerCtrlBean.CTX_PLUGIN_NUMBER)).intValue();
		ArrayList<String> keys = new ArrayList<String>(context.getKeys());
		for (Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
			String key = iter.next();
			if (key.startsWith("_plugin_"+pluginHandle+"."))
				context.remove(key);
		}
	}
	
	protected void dumpLocal(TransformerPluginContext context) {
		int pluginHandle = ((Integer)context.get(TransformerCtrlBean.CTX_PLUGIN_NUMBER)).intValue();
		Set<String> keys = context.getKeys();
		for (Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
			String key = iter.next();
			if (key.startsWith("_plugin_"+pluginHandle+"."))
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("dumpLocal() "+key+" -"+context.get(key));
		}
	}
	
	
	
	
	protected void putInPipeline(TransformerPluginContext context, String variableName, TypedContent content) {
		HashMap<String, TypedContent> pipeline = (HashMap<String, TypedContent>) context.get(TransformerCtrlBean.CTX_PIPELINE);
		pipeline.put(variableName, content);
	}
	
	protected Object getFromPipeline(TransformerPluginContext context, String variableName) {
		HashMap<String, TypedContent> pipeline = (HashMap<String, TypedContent>) context.get(TransformerCtrlBean.CTX_PIPELINE);
		return pipeline.get(variableName);
	}
	
	protected Object removeFomPipeline(TransformerPluginContext context, String variableName, Object value) {
		HashMap<String, TypedContent> pipeline = (HashMap<String, TypedContent>) context.get(TransformerCtrlBean.CTX_PIPELINE);
		return pipeline.remove(variableName);
	}

	
    /****************************************************
     * Utilities
     ****************************************************/
	
//	/**
//	 * Find items using xPaths
//	 * 
//	 * @throws XtentisException
//     */   
//    protected Collection<String> xPathsSearch(
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
//    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
//    	    throw new XtentisException(err);
//	    }
//    	
//    }
	
   
}