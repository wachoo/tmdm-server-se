package com.amalto.core.objects.transformers.v2.ejb;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.naming.InitialContext;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerGlobalContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginV2LocalInterface;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TransformerProcessStep;
import com.amalto.core.objects.transformers.v2.util.TransformerVariablesMapping;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.transformers.v2.util.TypedContent_Do_Not_Process;
import com.amalto.core.objects.transformers.v2.util.TypedContent_Drop_Variable;
import com.amalto.core.objects.transformers.v2.util.TypedContent_Use_Default;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.JobActionInfo;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 * @ejb.bean 	name="TransformerV2Ctrl"
 *           	display-name="Name for TransformerCtrl"
 *           	description="Description for TransformerCtrl"
 *           	jndi-name="amalto/remote/core/transformerv2ctrl"
 * 		  		local-jndi-name = "amalto/local/core/transformerv2ctrl"
 *           	type="Stateless"
 *           	view-type="both"
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
 */
public class TransformerV2CtrlBean implements SessionBean, TimedObject, TransformerPluginCallBack{
	
	public static final long serialVersionUID = 1986745965402456L;
	
	public static final String DEFAULT_VARIABLE = "_DEFAULT_";
	
	//YO! A static HashMap.....
	static HashMap<String, EJBLocalHome> pluginHomes = new HashMap<String, EJBLocalHome>();
	
	private SessionContext sessionContext;
	
	
    /**
     * TransformerCtrlBean.java
     * Constructor
     * 
     */
    public TransformerV2CtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    	sessionContext=ctx;
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
     * Creates or updates a Transformer
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerV2POJOPK putTransformer(TransformerV2POJO transformer) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putTransformer() "+transformer.getName());;
    	
        try {
        	//Check and compile the parameters one by one
        	ArrayList<TransformerProcessStep> specs = transformer.getProcessSteps();
        	for (Iterator<TransformerProcessStep> iter = specs.iterator(); iter.hasNext(); ) {
        		TransformerProcessStep step = iter.next();
		    	//get the plugin
        		TransformerPluginV2LocalInterface plugin= getPlugin(step.getPluginJNDI());
				step.setCompiledParameters(plugin.compileParameters(step.getParameters())); 
			}//for
        	
        	TransformerV2POJOPK pk = new TransformerV2POJOPK(transformer.store());
            if (pk == null) {
        	    String err = "Unable to create/update the Transfomer. "+transformer.getLastError();
        	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
            	throw new XtentisException(transformer.getLastError());
            }
            
            return pk;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Transfomer "+transformer.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }

    }
    
     
    /**
     * Get item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerV2POJO getTransformer(TransformerV2POJOPK pk) throws XtentisException{

        try {
        	TransformerV2POJO transformer =  ObjectPOJO.load(TransformerV2POJO.class,pk);
        	if (transformer == null) {
        		String err= "The Transformer "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	
        	return transformer;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Transformer "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get a Transformer - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerV2POJO existsTransformer(TransformerV2POJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(TransformerV2POJO.class,pk);
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Transformer exists:  "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug("existsTransformer() "+info, e);
    	   return null;
	    }
    }
    

    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerV2POJOPK removeTransformer(TransformerV2POJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("removeTransformer() "+pk.getUniqueId());

        try {
        	return new TransformerV2POJOPK(ObjectPOJO.remove(TransformerV2POJO.class, pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Transformer "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }    
    
    
    /**
	 * Retrieve all Transformer PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<TransformerV2POJOPK> getTransformerPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(TransformerV2POJO.class,regex);
    	ArrayList<TransformerV2POJOPK> l = new ArrayList<TransformerV2POJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new TransformerV2POJOPK(iter.next()));
		}
    	return l;
    }
    
    /**
     * Read an item and process it through a transformer.
     * The content of the item is mapped to the {@link #DEFAULT_VARIABLE} variable
     * @param transformerV2POJOPK
     * @param itemPOJOPK
     * @return
     * 		The pipeline after the transformer is run
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerContext extractThroughTransformer(
		TransformerV2POJOPK transformerV2POJOPK,
		ItemPOJOPK itemPOJOPK
	) throws XtentisException{
    	
    	TransformerContext ctx = new TransformerContext(transformerV2POJOPK);
    	
    	try {
	        ItemPOJO item = Util.getItemCtrl2Local().getItem(itemPOJOPK);
	        ctx.putInPipeline(
	        	TransformerV2CtrlBean.DEFAULT_VARIABLE, 
	        	new TypedContent(
	        		item.getProjectionAsString().getBytes("UTF-8"),
	        		"text/xml; charset=utf-8"
	        	)
	        );
        } catch (Exception e) {
        	String err = "Unable to extract '"+itemPOJOPK.getUniqueID()+"' through the Transformer '"+transformerV2POJOPK.getUniqueId()+"'";
        	org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
        	throw new XtentisException(err);
        } 
    	
    	return executeUntilDone(ctx);
    	
    }
    
    
    /**
	 * Executes theTransformer
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public BackgroundJobPOJOPK executeAsJob(
    		TransformerContext context,
    		TransformerCallBack callBack
		)throws XtentisException{
    	
    	try{
			//create a Background Job
			BackgroundJobPOJO bgPOJO = new BackgroundJobPOJO();
			bgPOJO.setDescription("Execute Transformer "+context.getTransformerV2POJOPK().getUniqueId()+" as a Background Job");
			bgPOJO.setMessage("Scheduling the job");
			bgPOJO.setPercentage(-1);
			bgPOJO.setSerializedObject(null);
			bgPOJO.setStatus(BackgroundJobPOJO._SCHEDULED_);
			bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
			bgPOJO.store();
			//launch job in background
			JobActionInfo actionInfo = new JobActionInfo(
				bgPOJO.getId(),
				LocalUser.getLocalUser().getUniverse(),
				context.getTransformerV2POJOPK().getUniqueId(), //action
				context
			); 
			createTimer(actionInfo);
			return new BackgroundJobPOJOPK(bgPOJO.getPK());
        	
        }	catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to execute the Transformer: "+context.getTransformerV2POJOPK().getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    
    /**
	 * Executes the Transformer Asynchronously by specifying the universe<br/>
	 * The user must have the 'administration" role
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public void execute(
    		UniversePOJO universe,
    		TransformerContext context,
    		TransformerCallBack callBack
		)throws XtentisException{
    	
    	ILocalUser user = LocalUser.getLocalUser();
    	
    	if (! user.getRoles().contains("administration")) {
    		String err = "The user '"+LocalUser.getLocalUser().getUsername()+"' does not have the 'administration' role";
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
    	}

    	//switch the Universe
    	user.setUniverse(universe);
    	
    	
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("Executing as Job Transformer '"+context.getTransformerV2POJOPK().getUniqueId()+"' " +
			"with user '"+user.getUsername()+"' in Universe '"+user.getUniverse().getName()+"'"
		);

    	execute(context, callBack);
    	
    }
    
    /**
	 * Executes the Transformer Asynchronously
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public void execute(
    		TransformerContext context,
    		TransformerCallBack callBack
		)throws XtentisException{
    	
    	//transform the context as a global context if not already done
    	//The context will be a TransformerContext if called directly, 
    	//a TransformerGlobalCOntext if called from process
    	TransformerGlobalContext globalContext = null;
    	if (context instanceof TransformerGlobalContext) {
    		globalContext = (TransformerGlobalContext) context;
    	} else {
    		globalContext = new TransformerGlobalContext(context);
    	}
    	
    	//sets the callback in the transformers used by the plugins when the content is ready
    	//{@link contentIsReady}
    	globalContext.setExecuteCallBack(callBack);
    	
        try {
        	//and run the plugin
        	TransformerV2POJO transformerPOJO = globalContext.getTransformerPOJO();
        	if (
        			(transformerPOJO.getProcessSteps()!=null) && 
        			(transformerPOJO.getProcessSteps().size()>0)
        		) {
        		executePlugin(globalContext,0);
        		//signal done to the call back
        		callBack.done(globalContext);
        	}
        	
        }	catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to execute the Transformer: "+context.getTransformerV2POJOPK().getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    } finally {
	    	//End the plugins
	    	try {
	    		LinkedHashMap<Integer, TransformerPluginV2LocalInterface> instantiatedPlugins = globalContext.getInstantiatedPlugins();
	    		Set<Integer> pluginNumbers = instantiatedPlugins.keySet();
	    		for (Iterator<Integer> iter = pluginNumbers.iterator(); iter.hasNext(); ) {
					Integer pluginNumber = iter.next();
					TransformerPluginV2LocalInterface plugin = instantiatedPlugins.get(pluginNumber);
					plugin.end(globalContext.getTransformerPluginContext(pluginNumber.intValue()));
				}
	    	} catch (Exception e) {
	    		String err = "Error ending plugins: "+e.getClass().getName()+": "+e.getMessage();
	    		org.apache.log4j.Logger.getLogger(this.getClass()).warn("endPlugins() "+err);
	    	}
	    }
    }
	

    /**
	 * Executes the Transformer Synchronously
	 * The Typed Content passed is stored in the DEFAULT pipeline variable
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public void execute(
    		TransformerContext context,
    		TypedContent content,
    		TransformerCallBack callBack
		)throws XtentisException{
    	
    	//Store the content in the default
    	if (content != null) context.putInPipeline(DEFAULT_VARIABLE, content);
    	//exzecute the Transformer
    	execute(context, callBack);
    }

    
    /**
	 * Executes the Transformer and returns only when it is done
	 * 
	 * @throws XtentisException
	 * @return the TransformerCOntext at the end of the execution
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public TransformerContext executeUntilDone(
    		TransformerContext context
		)throws XtentisException{
    	
    	//Set ready variable
    	context.put("com.amalto.core.objects.transformers.v2.transformerCtrlV2.ready", Boolean.FALSE);
    	
    	//execute the Transformer
    	execute(context, new TransformerCallBack() {
    		public void contentIsReady(TransformerContext context) throws XtentisException {}
    		public void done(TransformerContext context) throws XtentisException {
    			context.put("com.amalto.core.objects.transformers.v2.transformerCtrlV2.ready", Boolean.TRUE);
    		}
    	});
    	while (context.get("com.amalto.core.objects.transformers.v2.transformerCtrlV2.ready").equals(Boolean.FALSE)) {
    		try {Thread.sleep(100);} catch (InterruptedException e) {}
    	}
    	
    	context.remove("com.amalto.core.objects.transformers.v2.transformerCtrlV2.ready");
    	return context;
    }
    
    
    
    
    /**
	 * Executes the Transformer and returns only when it is done
	 * The Typed Content passed is stored in the DEFAULT pipeline variable
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public TransformerContext executeUntilDone(
    		TransformerContext context,
    		TypedContent content
		)throws XtentisException{	
    	//Store the content in the default pipeline variable
    	context.putInPipeline(DEFAULT_VARIABLE, content);
    	//exzecute the Transformer
    	return executeUntilDone(context);
    }

    
    
    /**
     * Executes a plugin
     * @param globalContext
     * @param pluginNumber
     * @throws XtentisException
     */
    protected void executePlugin(
		TransformerGlobalContext globalContext,
		int pluginNumber
	)throws XtentisException{
    		
    	//fetch the Transformer and the instantiated plugins
    	TransformerV2POJO transformerPOJO = globalContext.getTransformerPOJO();
    	
    	//fetch the Process Step
    	TransformerProcessStep processStep = transformerPOJO.getProcessSteps().get(pluginNumber);
    	
    	//fetch the Plugin context
    	TransformerPluginContext pluginContext = globalContext.getTransformerPluginContext(pluginNumber);
    	
    	
    	/*******************************************************************
    	 * Initialize the Plugin  - if not already done
    	 *******************************************************************/
    	HashMap<Integer, TransformerPluginV2LocalInterface> instantiatedPlugins = globalContext.getInstantiatedPlugins();
    	
    	//get the plugin parameters
    	String parameters = processStep.getCompiledParameters();
    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("executePlugin() "+processStep.getDescription()+" -- Initializing plugin "+processStep.getPluginJNDI());
    	
    	//Check if plugin is already instantiated and initialized
    	TransformerPluginV2LocalInterface plugin = null;
    	if ((plugin=instantiatedPlugins.get(new Integer(pluginNumber)))==null) {
    		
    		try {
	    		//fetch and initialize the plugin 
		    	plugin = getPlugin(processStep.getPluginJNDI());
		    	
		    	//set global context to each plugin by aiming
		    	plugin.setGlobalContext(globalContext);
	        	//the plugin handle is the plugin number
		    	plugin.init(
					globalContext.getTransformerPluginContext(pluginNumber),
        			parameters
	        	);
    		} catch (XtentisException e) {
    			throw(e);
    		} 
        	
        	//update the Map of running plugins
        	instantiatedPlugins.put(new Integer(pluginNumber), plugin);	    	
    	}
    	
    	/*******************************************************************
    	 * Check the input mappings
    	 *  Stop this Step if a mandatory Input mapping is missing
    	 *******************************************************************/    	
        try {
        	//fetch descriptors
        	ArrayList<TransformerPluginVariableDescriptor> descriptors = plugin.getInputVariableDescriptors("en");
        	//loop over the input variables - map content and determine if all mandatory variables have content
        	for (Iterator<TransformerPluginVariableDescriptor> iter = descriptors.iterator(); iter.hasNext(); ) {
				TransformerPluginVariableDescriptor descriptor = iter.next();
				TypedContent content = getMappedInputVariable(descriptor, processStep, globalContext);
				if (content instanceof TypedContent_Do_Not_Process) {
					//we stop the plugin processing
					String msg = 
		    			"Transformer step '"+processStep.getDescription()+"' of Transformer '"+globalContext.getTransformerV2POJOPK().getUniqueId()+"': "+
						"not executed --> input variable '"+descriptor.getVariableName()+"' is marked as stopped";
					org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeNextPlugin() "+msg);
					return;
				}
				//insert the plugin input variable content in the plugin context
				pluginContext.put(descriptor.getVariableName(), content);
        	}
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    		String err = 
    			"Transformer step '"+processStep.getDescription()+"' of Transformer '"+globalContext.getTransformerV2POJOPK().getUniqueId()+"':"+
				"the mapping of the input variables failed "+e.getClass().getName()+": "+e.getMessage();
    		org.apache.log4j.Logger.getLogger(this.getClass()).error("executeNextplugin() "+err);
    		throw new XtentisException(err);
	    }
        	
    	/*******************************************************************
    	 * Execute the plugin
    	 *******************************************************************/
        try {    	
        	//content will be sent through the contentIsReady method below.
        	pluginContext.setPluginCallBack(this);      
        	//check if disabled should not exe this plugin by aiming
        	if(!processStep.isDisabled()){      	
	        	//execute the plugin
	        	plugin.execute(pluginContext);
        	}else{
        		//sigal content is ready by aiming
        		pluginContext.getPluginCallBack().contentIsReady(pluginContext);
        	}
	    } catch (XtentisException e) {
	    	throw(e);
	    }

	    //Done
    }
        
    /**
     * Returns the content to be mapped to the input of a plugin
     * @param descriptor
     * @param processStep
     * @param context
     * @return the TypedContent to be mapped
     * @throws XtentisException
     */
    private TypedContent getMappedInputVariable (
    		TransformerPluginVariableDescriptor descriptor,
    		TransformerProcessStep processStep,
    		TransformerContext context
    		)  throws XtentisException{
    	
    	try {
    	
			String pluginVariable = descriptor.getVariableName();
			
			//find input in mappings and detemine pipeline variable name
			TransformerVariablesMapping mapping = null;
			for (Iterator<TransformerVariablesMapping> iterator = processStep.getInputMappings().iterator(); iterator.hasNext(); ) {
				TransformerVariablesMapping mpg = iterator.next();
				if (pluginVariable.equals(mpg.getPluginVariable())) {
					mapping = mpg;
					break;
				}
			}
			
			//Mapping not found
			if (mapping==null) {
				//The input variable is mandatory - it is a design time error
				if (descriptor.isMandatory()) {
		    		String err = 
		    			"Transformer step '"+processStep.getDescription()+"' of Transformer '"+context.getTransformerV2POJOPK().getUniqueId()+"':"+
						"The input variable '"+descriptor.getVariableName()+"' of plugin '"+processStep.getPluginJNDI()+
						"' is mandatory and was not mapped during the Transformer '"+context.getTransformerV2POJOPK().getUniqueId()+"' design.";
		    		org.apache.log4j.Logger.getLogger(this.getClass()).error("getMappedInputVariable() "+err);
		    		throw new XtentisException(err);
				}
				//not mandatory --> return the special USE_DEFAULT Typed Content
				return new TypedContent_Use_Default();
			}
			
			String pipelineVariable = mapping.getPipelineVariable();
			if (pipelineVariable == null) pipelineVariable = TransformerV2CtrlBean.DEFAULT_VARIABLE;
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getMappedInputVariable() Mapping pipeline "+pipelineVariable+" ---> "+pluginVariable);
			
			//Mapping is found --> retrieve the pipeline content;
			TypedContent content = context.getFromPipeline(pipelineVariable);
			
			//if content is null, the variable was never initialized, do not process it
			if (content == null) {
				return new TypedContent_Do_Not_Process();
			}
			
			//if content is marked as do not process, well.... do not process
			if (content instanceof TypedContent_Do_Not_Process) {
				return content;
			}
			
			//check if content-types match
			boolean match = false;
			for (Iterator<Pattern> iter = descriptor.getContentTypesRegex().iterator(); iter.hasNext(); ) {
				Pattern pattern = iter.next();
				if (pattern.matcher(Util.extractTypeAndSubType(content.getContentType())).matches()) {
					match = true;
					break;
				}
			}
			if (! match) {
	    		String err = 
					"Transformer step '"+processStep.getDescription()+"' of Transformer '"+context.getTransformerV2POJOPK().getUniqueId()+"':"+
					"The input variable '"+descriptor.getVariableName()+"' of plugin '"+processStep.getPluginJNDI()+
					"' cannot accept the pipeline variable '"+mapping.getPipelineVariable()+"' content-type of '"+content.getContentType()+"'";
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error("getMappedInputVariable() "+err);
	    		throw new XtentisException(err);
			}
			
			//check possible values regex
			if (	(descriptor.getPossibleValuesRegex()!=null) && 
					(descriptor.getPossibleValuesRegex().size() !=0) &&
					(! descriptor.getPossibleValuesRegex().contains(".*")) &&
					(Util.extractTypeAndSubType(content.getContentType()).startsWith("text"))
				) {
				String charset = Util.extractCharset(content.getContentType());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int b;
				while((b = content.getContentStream().read())!=-1) bos.write(b);
				String text = new String(bos.toByteArray(),charset);
				match = false;
				for (Iterator<Pattern> iter = descriptor.getContentTypesRegex().iterator(); iter.hasNext(); ) {
					Pattern pattern = iter.next();
					if (pattern.matcher(text).matches()) {
						match = true;
						break;
					}
				}
				if (! match) {
		    		String err = 
						"Transformer step '"+processStep.getDescription()+"' of Transformer '"+context.getTransformerV2POJOPK().getUniqueId()+"':"+
						"The input variable '"+descriptor.getVariableName()+"' of plugin '"+processStep.getPluginJNDI()+
						"' cannot accept the pipeline variable '"+mapping.getPipelineVariable()+"' value of '"+text+"'";
		    		org.apache.log4j.Logger.getLogger(this.getClass()).error("getMappedInputVariable() "+err);
		    		throw new XtentisException(err);
				}
			}
			
			return content;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    		String err = 
    			"Transformer step '"+processStep.getDescription()+"' of Transformer '"+context.getTransformerV2POJOPK().getUniqueId()+"':"+
				"the mapping of the input variables failed "+e.getClass().getName()+": "+e.getMessage();
    		org.apache.log4j.Logger.getLogger(this.getClass()).error("executeNextplugin() "+err);
    		throw new XtentisException(err);
	    }
			
    }
    
    
    /*****************************************************************
     *  TransformerPluginCallBack Implementation
    *****************************************************************/

    /**
     * Implementation of {@link TransformerPluginCallBack#contentIsReady(TransformerPluginContext)}
     */
	public void contentIsReady(TransformerPluginContext pluginContext) throws XtentisException {
		
		//fetch the process step
		TransformerGlobalContext globalContext = pluginContext.getTransformerGlobalContext();
		TransformerProcessStep processStep = pluginContext.getTransformerPOJO().getProcessSteps().get(pluginContext.getPluginNumber());
//		TransformerPluginV2LocalInterface plugin = globalContext.getInstantiatedPlugins().get(new Integer(pluginContext.getPluginNumber()));
		
		/*******************************************************************
    	 * Map the output variables to the pipeline
    	 *******************************************************************/
        try {
        	
        	//fetch output Mappings
        	ArrayList<TransformerVariablesMapping> outputMappings = processStep.getOutputMappings();
        	
        	//first create all the pipeline variables
        	for (Iterator<TransformerVariablesMapping> iter = outputMappings.iterator(); iter.hasNext(); ) {
				TransformerVariablesMapping mapping = iter.next();
				if (mapping.getPipelineVariable()!=null) {
					String pluginvariable = mapping.getPluginVariable();
					if (pluginvariable!=null) {
						//map the output of the plugin to the pipeline variable
						TypedContent content = (TypedContent)pluginContext.get(pluginvariable);
						//check content null
						if(content !=null){
						if (content instanceof TypedContent_Drop_Variable) {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug(
									"contentIsReady() Dropping pipeline variable: "+mapping.getPipelineVariable()
							);
							globalContext.removeFrompipeline(mapping.getPipelineVariable());
						} else {
							String s =content.toString();
							org.apache.log4j.Logger.getLogger(this.getClass()).debug(
									"contentIsReady() Mapping plugin variable: "+pluginvariable+" -----> pipeline: "+mapping.getPipelineVariable()
									+"   Content: "+s.substring(0, Math.min(0, s.length()))+"..."
							);
							globalContext.putInPipeline(mapping.getPipelineVariable(), content);
						}
						}
					} else if (mapping.getHardCoding() !=null) {
						//harcode the value into the pipeline
						if (mapping.getHardCoding() instanceof TypedContent_Drop_Variable) {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug(
									"contentIsReady() Hard Dropping pipeline variable: "+mapping.getPipelineVariable()
							);
							globalContext.removeFrompipeline(mapping.getPipelineVariable());
						} else {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug(
									"contentIsReady() Hard Coding the result in pipeline variable: "+mapping.getPipelineVariable()
									+"   Content: "+mapping.getHardCoding().getContentType().toString().substring(0, 100)+"..."
							);
							globalContext.putInPipeline(mapping.getPipelineVariable(), mapping.getHardCoding());
						}
					} else {
						String err = "Output Mapping incorrect for pipeline variable "+mapping.getPipelineVariable()+" in process step "+processStep.getDescription()+": "
											+"both the plugin variable name and the hard codings are empty";
						org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
						throw new XtentisException(err);
					}
				}
			}
        	
      
	    } catch (XtentisException e) {
	    	throw(e);
	    }
	    

		/*******************************************************************
    	 * If more plugins --> Execute the next Plugin
    	 * If not, signal to the main callback that content is ready
    	 *******************************************************************/
	    if (pluginContext.getPluginNumber()+1<pluginContext.getTransformerPOJO().getProcessSteps().size()) {
	    	executePlugin(globalContext, pluginContext.getPluginNumber()+1);
	    	return;
	    }
	    
	    //end of transformer branch, signal that content is ready
		String msg = 
			"End of current branch of Transformer '"+pluginContext.getTransformerV2POJOPK().getUniqueId()+"' calling execute callback";
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("contentIsReady() "+msg);
		
	    globalContext.getExecuteCallBack().contentIsReady(globalContext);
	}
   
    
    /*****************************************************************
     *  T I M E R
    *****************************************************************/
    
    /**
     * 
     * @param intervalDuration
     * @return a TimerHandle
     */
    private TimerHandle createTimer(JobActionInfo actionInfo) {
        TimerService timerService =  sessionContext.getTimerService();
        //Get Local User Token
        String token=Util.getUsernameAndPasswordToken();
		actionInfo.setUserToken(token);
        
        Timer timer = timerService.createTimer(150,actionInfo);  //0,15 second
        TimerHandle th = timer.getHandle();
        return th;
    }
    
    
    protected final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss-SSS z");
        
    /**
     * Run execute as a background job
     */
	public void ejbTimeout(Timer timer) {
		JobActionInfo actionInfo = (JobActionInfo) timer.getInfo();
		
		//The name of the transformer is stored in the action
		String transformerName = actionInfo.getAction();
		
		TransformerGlobalContext globalContext = null;

		try {
			
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			
			//recover process parameters
			globalContext = new TransformerGlobalContext((TransformerContext)actionInfo.getInfo());
			
			//update Back Ground Job
			BackgroundJobPOJO bgPOJO = Util.getBackgroundJobCtrlLocal().getBackgroundJob(new BackgroundJobPOJOPK(actionInfo.getJobId()));
			bgPOJO.setMessage("Starting processing");
			bgPOJO.setStatus(BackgroundJobPOJO._RUNNING_);
			bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
			try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
			globalContext.setJob(bgPOJO);
			
			//set user token
			String userToken=actionInfo.getUserToken();
			if(userToken!=null)globalContext.setUserToken(userToken);
			
			//Execute
			ctrl.execute(
				actionInfo.getUniverse(),
				globalContext,
				new TransformerCallBack() {
					public void contentIsReady(TransformerContext globalContext) throws XtentisException {
						long counter = ((TransformerGlobalContext)globalContext).getIterationNumber();
						org.apache.log4j.Logger.getLogger(this.getClass()).trace("contentIsReady() item "+counter);
						if (counter%100==0) {
							long time = System.currentTimeMillis() - ((TransformerGlobalContext)globalContext).getStartTime();
							int processRate = (int)((double)(counter*1000)/(double)time);
							BackgroundJobPOJO bgPOJO = ((TransformerGlobalContext)globalContext).getJob();
							bgPOJO.setMessage("Processed item "+counter+" at "+processRate+" items per second");
							bgPOJO.setStatus(BackgroundJobPOJO._RUNNING_);
							bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
							try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
						}
					}
					public void done(TransformerContext globalContext) throws XtentisException {
						org.apache.log4j.Logger.getLogger(this.getClass()).trace("done() ");
						long counter = ((TransformerGlobalContext)globalContext).getIterationNumber();
						long time = System.currentTimeMillis() - ((TransformerGlobalContext)globalContext).getStartTime();
						int processRate = (int)((double)(counter*1000)/(double)time);
						BackgroundJobPOJO bgPOJO = ((TransformerGlobalContext)globalContext).getJob();;
						//Add the Item PKs to the pipeline as comma separated lines
						String pksAsLine = "";
						Collection<ItemPOJOPK> pks = ((TransformerGlobalContext)globalContext).getProjectedPKs();
						synchronized(pks) {
							for (Iterator<ItemPOJOPK> iter = pks.iterator(); iter.hasNext(); ) {
								ItemPOJOPK pk = iter.next();
								if(!"".equals(pksAsLine)) pksAsLine += "\n";
								//pksAsLine += pk.getConceptName()+"."+Util.joinStrings(pk.getIds(), ".");
								pksAsLine += pk.getUniqueID();
							}
						}
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("done() Projected PKs\n"+pksAsLine);
						
						try {
							globalContext.putInPipeline(
									"records saved in the database", 
									new TypedContent(
											pksAsLine.getBytes("UTF-8"),
											"text/plain; charset=\"utf-8\""
									)
							);
						}catch (Exception e) {}
						
						try {
							bgPOJO.setMessage("Processing successfully completed at item "+counter+" (running at "+processRate+" items per second)");
							bgPOJO.setStatus(BackgroundJobPOJO._COMPLETED_);
							bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
							bgPOJO.setSerializedObject(null);
							bgPOJO.setPipeline(globalContext.getPipelineClone());
							try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
						} catch (Exception e) {
							String err = "Transformer Done but unable to store the result in the background object: "
												+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
				    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
				    	    throw new XtentisException(err);
						}
						
					}//done
				}//callback
			);
			

		} catch (Exception e) {
			try {
			    String err = "Unable to Process the Transformer '"+transformerName+"'"
					+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
				if (e instanceof XtentisException) {
					org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE. "+err, e);
				} else {
					org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
				}
				//Update Background job and try to put pipeline
				BackgroundJobPOJO bgPOJO = Util.getBackgroundJobCtrlLocal().getBackgroundJob(new BackgroundJobPOJOPK(actionInfo.getJobId()));
				bgPOJO.setMessage("Error processing Transformer. '"+transformerName+"': "+e.getMessage());
				bgPOJO.setStatus(BackgroundJobPOJO._STOPPED_);
				bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
				bgPOJO.setSerializedObject(null);
				bgPOJO.setPipeline(globalContext ==  null ? null : globalContext.getPipelineClone());
				try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
			} catch (Exception ex) {}
		}
	}
	


    
    private TransformerPluginV2LocalInterface getPlugin(String JNDI) throws XtentisException{
    	try {
    		EJBLocalHome pluginHome=null;
    		InitialContext ctx = new InitialContext();
    		
    		if (Util.USE_HOME_CACHES) {
    			pluginHome = pluginHomes.get(JNDI);
		    	if (pluginHome==null) {
		    		pluginHome = (EJBLocalHome)ctx.lookup(JNDI);
		    		//Util.dumpClass(pluginHome.getClass());
		    		pluginHomes.put(JNDI, pluginHome);
		    	}
    		} else {
    			pluginHome = (EJBLocalHome)ctx.lookup(JNDI);
    		}
	        //find create 
	        Method[] m = pluginHome.getClass().getMethods();
	        Method create = null;
	        for (int i = 0; i < m.length; i++) {
				if ("create".equals(m[i].getName())) {
					create = m[i];
					break;
				}
			}
	        if (create == null) {
	        	String err = "Transformer: unable to find create method on plugin \""+JNDI+"\"";
	        	org.apache.log4j.Logger.getLogger(this.getClass()).error("getPlugin() "+err);
	    		throw new XtentisException(err);        	
	        }
	        
	        //call it
	        Object plugin = create.invoke(pluginHome,(Object[])null);
//	        Util.dumpClass(plugin.getClass());
	    	return (TransformerPluginV2LocalInterface)plugin;
	    	
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    		String err = 
    			"Transformer. Unable to instantiate the plugin  '"+JNDI+"': ";
    		if (e.getCause()!=null) {
    			err+="caused by "+e.getCause().getClass().getName()+": "+e.getCause().getMessage();
    		} else {
				err+=e.getClass().getName()+": "+e.getMessage();
    		}
    		org.apache.log4j.Logger.getLogger(this.getClass()).error("getPlugin() "+err);
    		throw new XtentisException(err);
	    }
    }

    
}