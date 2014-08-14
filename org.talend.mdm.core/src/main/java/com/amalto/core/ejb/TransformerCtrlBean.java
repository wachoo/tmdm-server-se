package com.amalto.core.ejb;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.naming.InitialContext;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocalHome;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerProcessStep;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.JobActionInfo;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.TransformerPluginCallBack;
import com.amalto.core.util.TransformerPluginContext;
import com.amalto.core.util.TransformerPluginSpec;
import com.amalto.core.util.TypedContent;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.WSByteArray;
import com.amalto.core.webservice.WSExtractedContent;
import com.amalto.core.webservice.WSPipeline;
import com.amalto.core.webservice.WSPipelineTypedContentEntry;



/**
 * @author bgrieder
 * @deprecated - use TransformerV2 package
 * 
 * @ejb.bean name="TransformerCtrl"
 *        	display-name="Name for TransformerCtrl"
 *          description="Description for TransformerCtrl"
 *          jndi-name="amalto/remote/core/transformerctrl"
 * 		  	local-jndi-name = "amalto/local/core/transformerctrl"
 *          type="Stateless"
 *          view-type="both"
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
public class TransformerCtrlBean implements SessionBean, TimedObject{
  
	public static String CTX_PIPELINE = "com.amalto.core.pipeline";	
	public static String CTX_PKS = "com.amalto.core.ejb.itemctrl.pks";
	public static String CTX_EXECUTE_CALLBACK = "com.amalto.core.ejb.transformerctrl.executeCallBack";
	public static String CTX_PROCESS_CALLBACK = "com.amalto.core.ejb.transformerctrl.processCallBack";
	public static String CTX_TRANSFORMER = "com.amalto.core.ejb.transformerctrl.transformer";	
	
	public static String CTX_PLUGINS = "com.amalto.transformerctrl.instantiated.plugins";
	public static String CTX_COUNTER = "com.amalto.transformerctrl.counter";
	public static String CTX_TIME = "com.amalto.transformerctrl.time";
	public static String CTX_JOB = "com.amalto.transformerctrl.job";
	public static String CTX_DECISION_TABLE = "com.amalto.core.ejb.transformerctrl.decisionTable";
	public static String CTX_TRANSFORMER_DONE = "com.amalto.core.transformerctrl.ready";
	public static String CTX_PLUGIN_NUMBER = "com.amalto.core.ejb.transformerctrl.pluginNumber";
	public static String CTX_OUTPUT_VARIABLES = "com.amalto.core.ejb.transformerctrl.outputVariables";

	public static final long serialVersionUID = 1986745902456L;
	
	public static String DEFAULT_VARIABLE = "_DEFAULT_";
	
	private static String JOB_FILE = "processFileUsingTransformer";
	private static String JOB_BYTES = "processBytesUsingTransformer";
	
	
	private SessionContext context;
	
    /**
     * TransformerCtrlBean.java
     * Constructor
     * 
     */
    public TransformerCtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	context=ctx;
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
    public TransformerPOJOPK putTransformer(TransformerPOJO transformer) throws XtentisException{  
    	
    	org.apache.log4j.Category.getInstance(this.getClass()).debug("putTransformer() ");
    	
        try {
        	TransformerV2POJOPK pkV2 = Util.getTransformerV2CtrlLocal().putTransformer(TransformerPOJO.V1toV2(transformer));
        	return new TransformerPOJOPK(pkV2.getUniqueId());
        	
            /*** V1 STUFF 
        	//Check and compile the parameters one by one
        	ArrayList<TransformerPluginSpec> specs = transformer.getPluginSpecs();
        	for (Iterator iter = specs.iterator(); iter.hasNext(); ) {
				TransformerPluginSpec spec = (TransformerPluginSpec) iter.next();
		    	//get the plugin
				Object plugin= null;
				try {
					plugin = Util.retrieveComponent(null, spec.getPluginJNDI());
				} catch (Exception e) {
					throw new XtentisException(
							"The plugin "+
							spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
							" cannot be found"
					);
				}
				try {
					spec.setParameters((String) 
							Util.getMethod(plugin, "compileParameters").invoke(
									plugin,
									new Object[] {spec.getParameters()}
							)
					);
				} catch (Exception e) {
					throw new XtentisException(
							"The parameters of plugin "+
							spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
							" did not validate : "+e.getLocalizedMessage()
					);
				}
			}//for
        	
        	TransformerPOJOPK pk = new TransformerPOJOPK(transformer.store());
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            
            return pk;
            ****/
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Transfomer "+transformer.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
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
    public TransformerPOJO getTransformer(TransformerPOJOPK pk) throws XtentisException{

        try {
        	return TransformerPOJO.V2toV1(Util.getTransformerV2CtrlLocal().getTransformer(new TransformerV2POJOPK(pk.getUniqueId())));
        	
        	/*** OLD V1
        	TransformerPOJO transformer =  (TransformerPOJO)ObjectPOJO.load(TransformerPOJO.class,pk);
        	if (transformer == null) throw new XtentisException("the Transformer does not exist in storage");
        	
        	//decompile the parameters one by one
        	ArrayList<TransformerPluginSpec> specs = transformer.getPluginSpecs();
        	if (specs!=null) {
	        	for (Iterator iter = specs.iterator(); iter.hasNext(); ) {
					TransformerPluginSpec spec = (TransformerPluginSpec) iter.next();
			    	//get the plugin
					Object plugin= null;
					try {
						plugin = Util.retrieveComponent(null, spec.getPluginJNDI());
					} catch (Exception e) {
						throw new XtentisException(
								"The plugin "+
								spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
								" cannot be found"
						);
					}
					try {
						spec.setParameters((String) 
								Util.getMethod(plugin, "decompileParameters").invoke(
										plugin,
										new Object[] {spec.getParameters()}
								)
						);
					} catch (Exception e) {
						throw new XtentisException(
								"The parameters of plugin "+
								spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
								" could not be decompiled : "+e.getLocalizedMessage()
						);
					}
				}//for
        	}        	
        	return transformer;
        	***/
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Transformer "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
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
    public TransformerPOJO getTransformerV1(TransformerPOJOPK pk) throws XtentisException{

        try {
        	TransformerPOJO transformer =  ObjectPOJO.load(TransformerPOJO.class,pk);
        	if (transformer == null) throw new XtentisException("the Transformer does not exist in storage");
        	
			/* shunt decompilation
        	//decompile the parameters one by one
        	ArrayList<TransformerPluginSpec> specs = transformer.getPluginSpecs();
        	if (specs!=null) {
	        	for (Iterator iter = specs.iterator(); iter.hasNext(); ) {
					TransformerPluginSpec spec = (TransformerPluginSpec) iter.next();
			    	//get the plugin
					Object plugin= null;
					try {
						plugin = Util.retrieveComponent(null, spec.getPluginJNDI());
					} catch (Exception e) {
						throw new XtentisException(
								"The plugin "+
								spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
								" cannot be found"
						);
					}
					try {
						spec.setParameters((String) 
								Util.getMethod(plugin, "decompileParameters").invoke(
										plugin,
										new Object[] {spec.getParameters()}
								)
						);
						
					} catch (Exception e) {
						throw new XtentisException(
								"The parameters of plugin "+
								spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
								" could not be decompiled : "+e.getLocalizedMessage()
						);
					}
				}//for
				
        	}  
        	*/      	
        	return transformer;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Transformer "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
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
    public TransformerPOJO existsTransformer(TransformerPOJOPK pk)    throws XtentisException{
    	
        try {
        	TransformerV2POJO transformerV2 = Util.getTransformerV2CtrlLocal().existsTransformer(new TransformerV2POJOPK(pk.getUniqueId()));
        	if (transformerV2==null) return null;
        	return TransformerPOJO.V2toV1(transformerV2);
        	/***
        	return (TransformerPOJO)ObjectPOJO.load(TransformerPOJO.class,pk);
        	****/
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String info = "Could not check whether this Transformer exists:  "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).debug(info);
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
    public TransformerPOJOPK removeTransformer(TransformerPOJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Category.getInstance(this.getClass()).debug("Removing "+pk.getUniqueId());

        try {
        	return new TransformerPOJOPK(
        			Util.getTransformerV2CtrlLocal().removeTransformer(new TransformerV2POJOPK(pk.getUniqueId())).getUniqueId()
        	);
        	/***
        	return new TransformerPOJOPK(ObjectPOJO.remove(TransformerPOJO.class, pk));
        	***/
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Transformer "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    
    

    
    /**
	 * Executes theTransformer
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public void execute(
    		TransformerPOJOPK transformerPK,
    		TypedContent content,
    		TransformerPluginContext context,
    		TransformerPluginCallBack callBack
		)throws XtentisException{
    	
        try {

        	TransformerContext newContext = TransformerPluginContext.getNewTransformerContext(context,transformerPK);
        	newContext.put(CTX_EXECUTE_CALLBACK, callBack);
          	
        	Util.getTransformerV2CtrlLocal().execute(
        			newContext,
        			TypedContent.getNewTypedContent(content),
        			new TransformerCallBack() {
        				public void contentIsReady(TransformerContext context) throws XtentisException {
        					org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute contentIsReady() ");
        					TransformerPluginCallBack callBack = (TransformerPluginCallBack)context.get(CTX_EXECUTE_CALLBACK);
        					try {
	        					callBack.contentIsReady(
	        							0, 
	        							TypedContent.getOldTypedContent(context.getFromPipeline("_DEFAULT_")), 
	        							TransformerPluginContext.getOldTransformerContext(context)
	        					);
        					} catch (IOException e) {
        				    	e.printStackTrace();
        			    	    String err = "Unable to call the execute call back 'content is ready' of Transformer: "+context.getTransformerV2POJOPK().getUniqueId()
        			    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
        			    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
        			    	    throw new XtentisException(err);
        					}
        				}
        				public void done(TransformerContext context) throws XtentisException {
        					org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute done() ");
        					TransformerPluginCallBack callBack = (TransformerPluginCallBack)context.get(CTX_EXECUTE_CALLBACK);
        					try {
	        					callBack.done(
	        							0, 
	        							TransformerPluginContext.getOldTransformerContext(context)
	        					);
        					} catch (IOException e) {
        				    	e.printStackTrace();
        			    	    String err = "Unable to call the execute the call back 'done' of Transformer: "+context.getTransformerV2POJOPK().getUniqueId()
        			    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
        			    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
        			    	    throw new XtentisException(err);
        					}
        				}//done
        				
        			}//callback
        	);
        	/*
        	TransformerPOJO transformerPOJO = (TransformerPOJO) context.get(CTX_TRANSFORMER);
        	if (transformerPOJO==null) {  //Null if 'execute' is called directly, not via 'process'
	        	transformerPOJO = getTransformer(transformerPK);
	        	if (transformerPOJO == null) {
	        		throw new XtentisException(
	        				"Transformer "+transformerPK.getUniqueId()+" does not exist." +
	        				" Cannot execute transformation."
	        		);
	        	}
	        	context.put(CTX_TRANSFORMER, transformerPOJO);
        	}
        	context.put(CTX_EXECUTE_CALLBACK, callBack);
        	context.put(CTX_PLUGIN_NUMBER, new Integer(0));
    		context.put(CTX_COUNTER, new Long(0));
        	
        	//initialize the pipe
        	HashMap<String, TypedContent> pipeline = new HashMap<String, TypedContent>();
        	pipeline.put(DEFAULT_VARIABLE, content);
        	context.put(CTX_PIPELINE, pipeline);
        	
        	//initilalize the Map of running plugins
        	HashMap<Integer, Object> instantiatedPlugins = new HashMap<Integer, Object>();
        	context.put(CTX_PLUGINS, instantiatedPlugins);
        	
        	//and run the plugin
        	if (
        			(transformerPOJO.getPluginSpecs()!=null) && 
        			(transformerPOJO.getPluginSpecs().size()>0)
        		)
        		executeNextPlugin(
        				0, 
        				context,
        				new PluginCallback() //Generic local callback class for plugins - see below
        		);
        	*/
        }	catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to execute the Transformer: "+transformerPK.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
	
    
    protected void executeNextPlugin(
    	int pluginHandle,
		TransformerPluginContext context,
		PluginCallback pluginsCallback
	)throws XtentisException{
    		
    	//fetch the Transformer and the instantiated plugins
    	TransformerPOJO transformerPOJO = (TransformerPOJO)context.get(CTX_TRANSFORMER);
    	HashMap<Integer, Object> instantiatedPlugins = (HashMap<Integer, Object>)context.get(CTX_PLUGINS);
    	
    	//fetch the plugin Spec
    	int pluginNumber = pluginHandle;
    	TransformerPluginSpec pluginSpec = transformerPOJO.getPluginSpecs().get(pluginNumber);
    	
    	//check that the input is actually available in the pipeline
    	HashMap<String, TypedContent> pipeline = (HashMap<String, TypedContent>) context.get(CTX_PIPELINE);
    	
    	//fetch the input
    	String input = ((pluginSpec.getInput() == null) || "".equals(pluginSpec.getInput())) ? DEFAULT_VARIABLE : pluginSpec.getInput();
    	TypedContent content = pipeline.get(input);
    	if (content==null) {
	    	//No input content in the pipeline --> stop that plugin
    		org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeNextPlugin() No Input available in pipeline for plugin "+pluginSpec.getDescription());
    		pluginsCallback.stopped(pluginNumber, context);
    		return;
    	}
    	
    	
    	//we have content available as input an process it
    	
    	//get the plugin parameters
    	String parameters = pluginSpec.getParameters();
    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeNextPlugin() "+pluginSpec.getDescription()+" --  "+pluginSpec.getPluginJNDI());
    	
    	//Check if plugin is already instantiated and initialized
    	Object plugin = null;
    	if ((plugin=instantiatedPlugins.get(new Integer(pluginNumber)))==null) {
    		
    		try {
	    		//fetch and initialize the plugin 
		    	plugin = Util.retrieveComponent(null, pluginSpec.getPluginJNDI());
		    	
	        	//the plugin handle is the plugin number
	        	context = (TransformerPluginContext)
		        	Util.getMethod(plugin, "init").invoke(
						plugin,
						new Object[] {
				        	pluginNumber,							
		        			parameters,
		        			context,
		        			pluginsCallback
						}
		        	);
    		} catch (XtentisException e) {
		    	String paths[]  = pluginSpec.getPluginJNDI().split("\\/");
		    	String pluginName = paths[paths.length-1];
	    		String err = 
					"Transformer Plugin "+pluginName+" cannot be initialized: "+e.getMessage();
	    		throw new XtentisException(err);
    		} catch (InvocationTargetException e) {
		    	String paths[]  = pluginSpec.getPluginJNDI().split("\\/");
		    	String pluginName = paths[paths.length-1];
	    		String err = 
					"Transformer Plugin "+pluginName+" cannot be initialized: "+
					e.getCause().getClass().getName()+": "+e.getCause().getMessage();
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error("executeNextPlugin() "+err);
	    		throw new XtentisException(err);
    		}catch (IllegalAccessException e) {
		    	String paths[]  = pluginSpec.getPluginJNDI().split("\\/");
		    	String pluginName = paths[paths.length-1];
	    		String err = 
					"Transformer Plugin "+pluginName+" cannot be initialized. Init method not found: "+e.getMessage();
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error("executeNextPlugin() "+err);
	    		throw new XtentisException(err);
    		}
        	/*
    		Method[] methods = plugin.getClass().getMethods();
    		for (int i = 0; i < methods.length; i++) {
    			System.out.println("Method: "+methods[i].getName());
    		}
    		*/
        	
        	//update the Map of running plugins
        	instantiatedPlugins.put(new Integer(pluginNumber), plugin);	    	
    	}
    	
    	
    	
        try {    	

        	        	
			//Check that plugin accepts content type
        	boolean acceptsContent = ((Boolean) 
			Util.getMethod(plugin, "acceptsContentType").invoke(
					plugin,
					new Object[] {content.getContentType(), parameters}
				)).booleanValue();
        	if (! acceptsContent) {
		    	String paths[]  = pluginSpec.getPluginJNDI().split("\\/");
		    	String pluginName = paths[paths.length-1];
        		String err = 
    				"Transformer Plugin "+pluginName+
    				" does not accept the content type "+content.getContentType();
        		org.apache.log4j.Logger.getLogger(this.getClass()).error("Execute Next Plugin: "+err);
        		throw new XtentisException(err);
        	}        	
        	
        	//execute the plugin
        	Util.getMethod(plugin, "execute").invoke(
    				plugin,
    				new Object[] {context, content}
    		);
	    } catch (InvocationTargetException e) {
	    	if (e.getCause().getClass().equals(XtentisException.class)) throw new XtentisException(e.getCause().getMessage());
	    	String paths[]  = pluginSpec.getPluginJNDI().split("\\/");
	    	String pluginName = paths[paths.length-1];
	    	throw new XtentisException("The call to plugin "+pluginName+" failed in Transformer "+transformerPOJO.getName()+". "+e.getCause().getMessage());
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
	    	String paths[]  = pluginSpec.getPluginJNDI().split("\\/");
	    	String pluginName = paths[paths.length-1];
    	    String err = "Unable to execute the plugin "+pluginName+" in Transformer: "+transformerPOJO.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
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
    public Collection<TransformerPOJOPK> getTransformerPKs(String regex) throws XtentisException {
    	try {
	    	Collection<TransformerV2POJOPK> c = Util.getTransformerV2CtrlLocal().getTransformerPKs(regex);
	    	ArrayList<TransformerPOJOPK> l = new ArrayList<TransformerPOJOPK>();
	    	for (Iterator<TransformerV2POJOPK> iter = c.iterator(); iter.hasNext(); ) {
				l.add(new TransformerPOJOPK(iter.next()));
			}
	    	return l;
	    } catch (Exception e) {
    	    String err = "Unable to get the transformer PKs for regular expression "+regex
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
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
    public Collection<TransformerPOJOPK> getTransformerV1PKs(String regex) throws XtentisException {
    	try {
        	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(TransformerPOJO.class,regex);
        	ArrayList<TransformerPOJOPK> l = new ArrayList<TransformerPOJOPK>();
        	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
    			l.add(new TransformerPOJOPK(iter.next()));
    		}
        	return l;
	    } catch (Exception e) {
    	    String err = "Unable to get the transformer V1 PKs for regular expression "+regex
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }

    /**
     * Simplified Process items using a Transformer and a Decision Table
     * @see #process(TypedContent, TransformerPOJOPK, HashMap, String, TransformerPluginContext, TransformerPluginCallBack) below
     * 	NONE: well, leave it in the pipeline as such
     * 	DISCARD: remove it from the pipeline
     * 	PROCESS(DataCluster,DataModel[,overwrite]): project the result to DataCluster using DataModel. By default will overwrite an existing items unless overwrite is set to false;
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerPluginContext process (
    		TypedContent content,
    		TransformerPOJOPK transformerPOJOPK,
    		HashMap<String,String> decisionTable
    ) throws XtentisException{
    	try {
	    	TransformerPluginContext context = new TransformerPluginContext();
	    	context.put(CTX_TRANSFORMER_DONE, new Boolean(false));
	    	process(
	    			content,
	    			transformerPOJOPK, 
	    			decisionTable,
	    			LocalUser.getLocalUser().getUsername(),
	    			context,
	    			new TransformerPluginCallBack() {
	        			public void contentIsReady(int pluginHandle, TypedContent content, TransformerPluginContext context) throws XtentisException {
	        				org.apache.log4j.Logger.getLogger(this.getClass()).debug("simple process contentIsReady() ");
	        			}
	        			public void done(int pluginHandle, TransformerPluginContext context) throws XtentisException {
	        				org.apache.log4j.Logger.getLogger(this.getClass()).debug("simple process done() ");
	        				context.put(CTX_TRANSFORMER_DONE, new Boolean(true));
	        			}
	        			public void stopped(int pluginHandle, TransformerPluginContext context) throws XtentisException {
	        			}
	    			}
	    	);
			while (! ((Boolean)context.get(CTX_TRANSFORMER_DONE)).booleanValue()) {
				try {Thread.sleep(50);} catch (InterruptedException e) {}
			}
	    	return context;
	    	
	    } catch (XtentisException e) {
	    	throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to process the data to transformer "+transformerPOJOPK.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    
    /**
     * Process items using a Transformer and a Decision Table
     * The Decision Table fixes for each output variable the action to take after a full transformer plugin cycle has been run
     * 	NONE: well, leave it in the pipeline as such
     * 	DISCARD: remove it from the pipeline
     * 	PROJECT(DataCluster,DataModel[,overwrite]): project the result to DataCluster using DataModel. By default will overwrite an existing items unless overwrite is set to false;
     * The pks of the element projected can be found in the Context entry com.amalto.core.ejb.itemctrl.pks
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerPluginContext process(
    		TypedContent content,
    		TransformerPOJOPK transformerPOJOPK,
    		HashMap<String,String> decisionTable,
    		String username,
    		TransformerPluginContext context,
    		TransformerPluginCallBack processCallBack
    ) 
    throws XtentisException{          
    	
        try {
        	
        	org.apache.log4j.Category.getInstance(this.getClass()).debug("process "+transformerPOJOPK.getUniqueId());
        	context.put("bloody_old_callback", processCallBack);
        	
        	process_new(
        			TypedContent.getNewTypedContent(content), 
        			decisionTable, 
        			username, 
        			TransformerPluginContext.getNewTransformerContext(context,transformerPOJOPK),
        			new TransformerCallBack (){
        				public void contentIsReady(TransformerContext context) throws XtentisException {
        					TransformerPluginCallBack callBack = (TransformerPluginCallBack)context.get("bloody_old_callback");
        					try {
	        					callBack.contentIsReady(
	        							0, 
	        							TypedContent.getOldTypedContent(context.getFromPipeline("_DEFAULT_")), 
	        							TransformerPluginContext.getOldTransformerContext(context)
	        					);
        					} catch (IOException e) {
        				    	e.printStackTrace();
        			    	    String err = "Unable to call the execute call back 'content is ready' of Transformer: "+context.getTransformerV2POJOPK().getUniqueId()
        			    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
        			    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
        			    	    throw new XtentisException(err);
        					}
        				}
        				public void done(TransformerContext context) throws XtentisException {
        					org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute done() ");
        					TransformerPluginCallBack callBack = (TransformerPluginCallBack)context.get("bloody_old_callback");
        					try {
	        					callBack.done(
	        							0, 
	        							TransformerPluginContext.getOldTransformerContext(context)
	        					);
        					} catch (IOException e) {
        				    	e.printStackTrace();
        			    	    String err = "Unable to call the execute the call back 'done' of Transformer: "+context.getTransformerV2POJOPK().getUniqueId()
        			    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
        			    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
        			    	    throw new XtentisException(err);
        					}
        				}//done

        			}
        	);
        	
        	
        	return context;
        	/*
           	
        	TransformerPOJO transformerPOJO = getTransformer(transformerPOJOPK);
        	context.put(CTX_TRANSFORMER, transformerPOJO);
        	
        	context.put(CTX_DECISION_TABLE, decisionTable);
        	context.put(CTX_PROCESS_CALLBACK, processCallBack);
        	context.put(CTX_PKS,new ArrayList<ItemPOJOPK>());
        	context.put(CTX_COUNTER, new Long(1));
        	
        	
        	//re-init projection caches
        	dataModels = new HashMap<String, DataModelPOJO>();
        	dataModelsXSD = new HashMap<String, Document>();
        	
			//buil a list of all the output variables      	
        	ArrayList<TransformerPluginSpec> specs = transformerPOJO.getPluginSpecs();
			HashSet<String> outputs  = new HashSet<String>();
			for (Iterator iter = specs.iterator(); iter.hasNext(); ) {
				TransformerPluginSpec spec = (TransformerPluginSpec) iter.next();
				outputs.add(spec.getOutput());
			}
			context.put(CTX_OUTPUT_VARIABLES, outputs);
        	
        	execute(
        			transformerPOJOPK, 
        			content, 
        			context, 
        			new TransformerPluginCallBack() {
        				
        				public void contentIsReady(int pluginHandle, TypedContent content,  TransformerPluginContext context) throws XtentisException {
        					//read the context
		        			TransformerPluginCallBack processCallBack =(TransformerPluginCallBack) context.get(CTX_PROCESS_CALLBACK);
		        			HashMap<String,String> decisionTable = (HashMap<String,String>) context.get(CTX_DECISION_TABLE);
		        			TransformerPOJO transformerPOJO = (TransformerPOJO)context.get(CTX_TRANSFORMER);
		        			HashMap<String, TypedContent> pipeline = (HashMap<String, TypedContent>)context.get(CTX_PIPELINE);
							long counter = ((Long)context.get(CTX_COUNTER)).longValue();

        					//Process the decisions on the variables in the plugin specs
        					HashSet<String> outputs = (HashSet<String>) context.get(CTX_OUTPUT_VARIABLES);
        					
        					for (Iterator iter = outputs.iterator(); iter.hasNext(); ) {
								String output = (String) iter.next();
								if (output==null) output = DEFAULT_VARIABLE;
								String decision = "IGNORE";
								if (decisionTable != null) decision = decisionTable.get(output);
								if (decision == null) decision = "IGNORE";
        						        						
	        					try {
	        						if ("DISCARD".equals(decision)) {
	        							org.apache.log4j.Logger.getLogger(this.getClass()).debug("process:contentIsReady() Variable: "+output+" --> "+decision);
	        							pipeline.remove(output);
	        						} else if (decision.startsWith("PROJECT")) {
		        						ItemPOJOPK pk = project(pipeline.get(output), decision);
		        						org.apache.log4j.Logger.getLogger(this.getClass()).debug("process:contentIsReady() Variable: "+output+" --> "+decision+" ---> "+pk.getUniqueID());
		        						if (pk!=null) {
					        				//insert the PK in the context
					        				Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>)context.get(CTX_PKS);
					        				pks.add(pk);
		        						}
	        						} else if ("TO CONSOLE".equals(decision)) {
	        							//we add a  variable with a name indexed by the loop number
	        							TypedContent tc = pipeline.get(output);
	        							String variable = output+"/"+new DecimalFormat("000000000").format(counter);
	        							org.apache.log4j.Logger.getLogger(this.getClass()).debug("contentIsReady() TO CONSOLE variable "+variable+" --> "+TypedContent.getNewTypedContent(tc).toString());
	        							pipeline.put(variable, tc);
	        						} else {
	        							//All Other cases: do Nothing
	        						}
			        	    		//Call the global call back
			        				processCallBack.contentIsReady(pluginHandle, content, context);
	        					} catch (XtentisException e) {
	        						e.printStackTrace();
	        				    	throw (e);
	        				    } catch (Exception e) {
	        			    	    String err = "Unable to run the decision '"+decision+"' on Transformer "+transformerPOJO.getName()
	        			    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
	        			    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
	        			    	    throw new XtentisException(err);
	        				    }       					
        					}//for process variables
        					
        				}//contentIsReady
        						        		
		        		public void done(int pluginHandle, TransformerPluginContext context) throws XtentisException {
		        			org.apache.log4j.Logger.getLogger(this.getClass()).debug("process:done() ");
	        				TransformerPluginCallBack processCallBack =
	        					(TransformerPluginCallBack) context.get(
	        							CTX_PROCESS_CALLBACK
	        					);
	        				processCallBack.done(pluginHandle, context);
		        		}//done

						public void stopped(int pluginHandle, TransformerPluginContext context) throws XtentisException {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug("process:stopped() ");
	        				TransformerPluginCallBack processCallBack =
	        					(TransformerPluginCallBack) context.get(
	        							CTX_PROCESS_CALLBACK
	        					);
		        			processCallBack.stopped(pluginHandle, context);
						}//stopped
        				
        			}//new
        	);//execute
        	
        	return context;
			*/
  
	    } catch (XtentisException e) {
	    	throw (e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to process an item on Transformer '"+transformerPOJOPK.getUniqueId()+"'"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
    
    
    private TransformerContext process_new(
    		com.amalto.core.objects.transformers.v2.util.TypedContent content,
    		HashMap<String,String> decisionTable,
    		String username,
    		TransformerContext context,
    		TransformerCallBack processCallBack
    ) 
    throws XtentisException{          
    	
        try {
        	
        	org.apache.log4j.Category.getInstance(this.getClass()).debug("process "+context.getTransformerV2POJOPK().getUniqueId());
           	
        	context.put(CTX_DECISION_TABLE, decisionTable);
        	context.put(CTX_PROCESS_CALLBACK, processCallBack);
        	context.put(CTX_COUNTER, new Long(0));
        	
        	
        	//re-init projection caches
        	dataModels = new HashMap<String, DataModelPOJO>();
        	dataModelsXSD = new HashMap<String, Document>();
        	
			//buil a list of all the output variables  // only the first output varable here
        	TransformerV2CtrlLocal ctrl = ((TransformerV2CtrlLocalHome)new InitialContext().lookup(TransformerV2CtrlLocalHome.JNDI_NAME)).create();
        	TransformerV2POJO transformerPOJO = ctrl.getTransformer(context.getTransformerV2POJOPK());
        	ArrayList<TransformerProcessStep> steps = transformerPOJO.getProcessSteps();
			HashSet<String> outputs  = new HashSet<String>();
			for (Iterator<TransformerProcessStep> iter = steps.iterator(); iter.hasNext(); ) {
				TransformerProcessStep step = iter.next();
				outputs.add(step.getOutputMappings().get(0).getPipelineVariable());
			}
			context.put(CTX_OUTPUT_VARIABLES, outputs);
			
			Collection<ItemPOJOPK> pksList = new ArrayList<ItemPOJOPK>();
			context.put(CTX_PKS, pksList);
        	
			ctrl.execute( 
        			context,
        			content,
        			new TransformerCallBack() {
        				
        				public void contentIsReady(TransformerContext context) throws XtentisException {
        					
        					TransformerCallBack processCallBack =(TransformerCallBack) context.get(CTX_PROCESS_CALLBACK);
		        			HashMap<String,String> decisionTable = (HashMap<String,String>) context.get(CTX_DECISION_TABLE);
							long counter = ((Long)context.get(CTX_COUNTER)).longValue()+1;
							context.put(CTX_COUNTER, counter);
        					
         					//Process the decisions on the variables in the plugin specs
        					HashSet<String> outputs = (HashSet<String>) context.get(CTX_OUTPUT_VARIABLES);
        					
        					for (Iterator<String> iter = outputs.iterator(); iter.hasNext(); ) {
								String output = iter.next();
								if (output==null) output = DEFAULT_VARIABLE;
								String decision = "IGNORE";
								if (decisionTable != null) decision = decisionTable.get(output);
								if (decision == null) decision = "IGNORE";
        						        						
	        					try {
	        						if ("DISCARD".equals(decision)) {
	        							org.apache.log4j.Logger.getLogger(this.getClass()).debug("process:contentIsReady() Variable: "+output+" --> "+decision);
	        							context.removeFrompipeline(output);
	        						} else if (decision.startsWith("PROJECT")) {
	        							if (context.getFromPipeline(output).getContentType()!=null) { //likely an DO_NOPT_PROCESS in V2
			        						ItemPOJOPK pk = project(TypedContent.getOldTypedContent(context.getFromPipeline(output)), decision);
			        						org.apache.log4j.Logger.getLogger(this.getClass()).debug("process:contentIsReady() Variable: "+output+" --> "+decision+" ---> "+pk.getUniqueID());
			        						if (pk!=null) {
						        				//insert the PK in the context
						        				Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>)context.get(CTX_PKS);
						        				pks.add(pk);
			        						}
	        							}
	        						} else if ("TO CONSOLE".equals(decision)) {
	        							//we add a  variable with a name indexed by the loop number
	        							com.amalto.core.objects.transformers.v2.util.TypedContent tc = context.getFromPipeline(output);
	        							String variable = output+"/"+new DecimalFormat("000000000").format(counter);
	        							org.apache.log4j.Logger.getLogger(this.getClass()).debug("contentIsReady() TO CONSOLE variable "+variable+" --> "+tc.toString());
	        							context.putInPipeline(variable, tc);
	        						} else {
	        							//All Other cases: do Nothing
	        						}
			        	    		//Call the global call back
			        				processCallBack.contentIsReady(context);
	        					} catch (XtentisException e) {
	        						e.printStackTrace();
	        				    	throw (e);
	        				    } catch (Exception e) {
	        				    	e.printStackTrace();
	        			    	    String err = "Unable to run the decision '"+decision+"' on Transformer "+context.getTransformerV2POJOPK().getUniqueId()
	        			    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
	        			    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
	        			    	    throw new XtentisException(err);
	        				    }       					
        					}//for process variables
        				}

        				public void done(TransformerContext context) throws XtentisException {
        					TransformerCallBack processCallBack =(TransformerCallBack) context.get(CTX_PROCESS_CALLBACK);
        					processCallBack.done(context);
        				}        						        		        				
        			}//new
        	);//execute
        	
        	return context;

  
	    } catch (XtentisException e) {
	    	throw (e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to process an item on Transformer '"+context.getTransformerV2POJOPK().getUniqueId()+"'"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
    
    /**
     * Simplified Process Bytes using a Transformer and a Decision Table 
     * Runs as a Background Job
     * @see #process(TypedContent, TransformerPOJOPK, HashMap, String, TransformerPluginContext, TransformerPluginCallBack) for parameter details
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJOPK processBytesAsBackgroundJob (
    		byte[] bytes,
    		String contentType,
    		TransformerPOJOPK transformerPOJOPK,
    		HashMap<String,String> decisionTable
    ) throws XtentisException{
    	try {
    		//create a Background Job
    		BackgroundJobPOJO bgPOJO = new BackgroundJobPOJO();
    		bgPOJO.setDescription("Process Bytes using Transformer "+transformerPOJOPK.getUniqueId()+" as a Background Job");
    		bgPOJO.setId("@dep"+bgPOJO.getId()); //used by WSXtentisBean to determine that it must use the old WS Form
    		bgPOJO.setMessage("Scheduling the job");
    		bgPOJO.setPercentage(-1);
    		bgPOJO.setSerializedObject(null);
    		bgPOJO.setStatus(BackgroundJobPOJO._SCHEDULED_);
    		bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
    		try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
    		//Instantiate processing info with bytes
    		ProcessInfo processInfo = new ProcessInfo(null,bytes,contentType,transformerPOJOPK,decisionTable,LocalUser.getLocalUser().getUsername());
    		//launch job in background
    		JobActionInfo actionInfo = new JobActionInfo(bgPOJO.getId(), new UniversePOJO(), JOB_BYTES,processInfo);
    		createTimer(actionInfo);
    		return new BackgroundJobPOJOPK(bgPOJO.getPK());
//	    } catch (XtentisException e) {
//	    	throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to process the data to transformer "+transformerPOJOPK.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }

    
    /**
     * Simplified Process File using a Transformer and a Decision Table 
     * Runs as a Background Job
     * @see #process(TypedContent, TransformerPOJOPK, HashMap, String, TransformerPluginContext, TransformerPluginCallBack) for parameter details
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJOPK processFileAsBackgroundJob (
    		String filename,
    		String contentType,
    		TransformerPOJOPK transformerPOJOPK,
    		HashMap<String,String> decisionTable
    ) throws XtentisException{
    	try {
    		//create a Background Job
    		BackgroundJobPOJO bgPOJO = new BackgroundJobPOJO();
    		bgPOJO.setDescription("Process File \""+filename+"\" using Transformer "+transformerPOJOPK.getUniqueId()+" as a Background Job");
    		bgPOJO.setId("@dep"+bgPOJO.getId()); //used by WSXtentisBean to determine that it must use the old WS Form
    		bgPOJO.setMessage("Scheduling the job");
    		bgPOJO.setPercentage(-1);
    		bgPOJO.setSerializedObject(null);
    		bgPOJO.setStatus(BackgroundJobPOJO._SCHEDULED_);
    		bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
    		try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
    		//Instantiate processing info with a filename
    		ProcessInfo processInfo = new ProcessInfo(filename,null, contentType,transformerPOJOPK,decisionTable,LocalUser.getLocalUser().getUsername());
    		//launch job in background
    		JobActionInfo actionInfo = new JobActionInfo(bgPOJO.getId(), new UniversePOJO(), JOB_FILE,processInfo); 
    		createTimer(actionInfo);
    		return new BackgroundJobPOJOPK(bgPOJO.getPK());
//	    } catch (XtentisException e) {
//	    	throw (e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to process the data to transformer "+transformerPOJOPK.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }

    
	private static Pattern dp = Pattern.compile("PROJECT\\s*\\(([^,]+),([^,]+)(,(.+))?\\)\\s*",Pattern.DOTALL | Pattern.CASE_INSENSITIVE); 
	private HashMap<String, DataModelPOJO> dataModels = new HashMap<String, DataModelPOJO>(); //dataModels Cache
	private HashMap<String, Document> dataModelsXSD = new HashMap<String, Document>(); //dataModels Cache
    
    protected ItemPOJOPK project(TypedContent content, String projectInstruction) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("project() ");
    	try {
			//check that the last content-type is text/xml
			if (!content.getContentType().startsWith("text/xml")) {
				String err = "The last plugin must generate XML when projecting to the repository";
				org.apache.log4j.Logger.getLogger(this.getClass()).error("contentIsReady() "+err);
				throw new XtentisException(err);
			}
			
			//process the decision

			Matcher dm = dp.matcher(projectInstruction);
			if (!dm.matches()) {
				String err = "The PROJECT Instruction ["+projectInstruction+"] has an incorrect syntax." +
						"\nThe correct syntax is PROJECT(DataCluster, Data Model [,overwrite])." +
						"\nOverwrite is true by default and takes the values true or false ";
				org.apache.log4j.Logger.getLogger(this.getClass()).error("contentIsReady() "+err);
				throw new XtentisException(err);
			}
			String dataCluster = dm.group(1);
			String dataModel = dm.group(2);
			boolean overwrite = true;
			if (dm.groupCount()==5) {
				if ("FALSE".equals(dm.group(4).toUpperCase())) overwrite = false;
			}
			
			//check whether we use the content bytes (the favorite option) or we read the stream
			byte[] bytes = null;
			if (content.getBytes() != null) {
				bytes = content.getBytes();
			} else {
				if (content.getStream()!=null) {
					//read the stream 
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					int b;
					while ((b=content.getStream().read())!=-1) bos.write(b);
					bytes = bos.toByteArray();
				} else {
					String err = "The PROJECT Instruction ["+projectInstruction+"] cannot be processed." +
					"\nThe output variable has no content.";
					org.apache.log4j.Logger.getLogger(this.getClass()).error("contentIsReady() "+err);
					throw new XtentisException(err);
				}
			}

			//extract charset			
			String charset=Util.extractCharset(content.getContentType());
			
			//parse result
			//FIXME: can we do better that DOM parsing here?
			Element newItem = null;
			try {
				newItem = Util.parse(new String(bytes, charset)).getDocumentElement();
			} catch (Exception e) {
				throw new XtentisException("The processed output is not a valid XML and cannot be project to Data Cluster "+dataCluster+". "+e.getMessage());
			}
			
			//get conceptName
			String conceptName = newItem.getLocalName();
    		
			//get the DataModel
    		DataModelPOJO dataModelPOJO = dataModels.get(dataModel);
    		if (dataModelPOJO == null) {
    			dataModelPOJO = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(dataModel));
    			dataModels.put(dataModel,dataModelPOJO);
    			dataModelsXSD.put(dataModel,Util.parse(dataModelPOJO.getSchema()));
    		}
	        
    		//find the key for the business concept --> it will become the element id
    		Document xsd = dataModelsXSD.get(dataModel);
            XSDKey conceptKey = Util.getBusinessConceptKey(
            		xsd,
					conceptName
			);
			//get key values
			String[] itemKeyValues = Util.getItemKeyValues(
       			newItem,
   				conceptKey
			);
			
			//get itemCtrl Bean
			ItemCtrl2Local itemCtrl = Util.getItemCtrl2Local();
			
			if (!overwrite) {
				//then check if exists
				boolean existsItem = (itemCtrl.existsItem(new ItemPOJOPK(new DataClusterPOJOPK(dataCluster),conceptName,itemKeyValues))!=null);
				if (existsItem) return null; //en of the story
			}
			
			//write the item
			
			//create the item
			ItemPOJO newItemPOJO = 	new ItemPOJO(
					new DataClusterPOJOPK(dataCluster),
					conceptName,
					itemKeyValues,
					System.currentTimeMillis(),
					newItem
			); 
			
			//now perform updates
			return 
				itemCtrl.putItem(
						newItemPOJO,
						dataModelPOJO
				);
    	} catch (XtentisException e) {
    		e.printStackTrace();
	    	throw (e);
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to project the item using PROJECT instruction "+projectInstruction
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }

    
    
   
    
    /*****************************************************************
     *  T I M E R
    *****************************************************************/
    
    /**
     * 
     * @param intervalDuration
     * @return {@link TimerHandle}
     */
    private TimerHandle createTimer(JobActionInfo actionInfo) {
    	org.apache.log4j.Category.getInstance(this.getClass()).debug("createTimer() ");
        TimerService timerService =  context.getTimerService();
        Timer timer = timerService.createTimer(150,actionInfo);  //0,15 second
        TimerHandle th = timer.getHandle();
        return th;
    }
    
    /*
    private void stopTimers() {
    	org.apache.log4j.Category.getInstance(this.getClass()).debug("stopTimers() ");
    	Collection timers = context.getTimerService().getTimers();
    	for (Iterator iter = timers.iterator(); iter.hasNext();) {
			Timer timer = (Timer) iter.next();
			timer.cancel();
		}
    }
    */
    
    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss-SSS z");
        
    ///for the AsBackgroundJob Methods
	public void ejbTimeout(Timer timer) {
		JobActionInfo actionInfo = (JobActionInfo) timer.getInfo();
		
		//recover process parameters
		ProcessInfo processInfo = (ProcessInfo) actionInfo.getInfo(); 
		//username
		String username = processInfo.getUsername();
		//Start Process
		TransformerPluginContext context = new TransformerPluginContext();
		//initialize pipeline
		HashMap<String, TypedContent> pipeline = new HashMap<String, TypedContent>();
		context.put(CTX_PIPELINE, pipeline);
		//update time and counter
		context.put(CTX_TIME, new Long(System.currentTimeMillis()));


		try {
			//update Back Ground Job
			BackgroundJobPOJO bgPOJO = Util.getBackgroundJobCtrlLocal().getBackgroundJob(new BackgroundJobPOJOPK(actionInfo.getJobId()));
			bgPOJO.setMessage("Starting processing");
			bgPOJO.setStatus(BackgroundJobPOJO._RUNNING_);
			bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
			try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
			context.put(CTX_JOB, bgPOJO);
	
			TypedContent content;
			if (JOB_FILE.equals(actionInfo.getAction())) {
				 content = new TypedContent(new FileInputStream(processInfo.getFilename()),null,processInfo.getContentType());
			} else {
				content = new TypedContent(null, processInfo.getBytes(), processInfo.getContentType());
			}
			
			//String username  = LocalUser.getLocalUser().getUsername();

			context = process(
				content,
				processInfo.getTransformerPOJOPK(),
				processInfo.getDecisionTable(),
				username,
				context,
				new TransformerPluginCallBack() {
					public void contentIsReady(int pluginHandle, TypedContent content, TransformerPluginContext context) throws XtentisException {
						long counter = ((Long)context.get(CTX_COUNTER)).longValue();
						if (counter%100==0) {
							long time = System.currentTimeMillis() - ((Long)context.get(CTX_TIME)).longValue();
							int processRate = (int)((double)(counter*1000)/(double)time);
							BackgroundJobPOJO bgPOJO = (BackgroundJobPOJO)context.get(CTX_JOB);
							bgPOJO.setMessage("Processed item "+counter+" at "+processRate+" items per second");
							bgPOJO.setStatus(BackgroundJobPOJO._RUNNING_);
							bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
							try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
						}
					}
					public void done(int pluginHandle, TransformerPluginContext context) throws XtentisException {
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("FileProcessAsJob done() ");
						long counter = ((Long)context.get(CTX_COUNTER)).longValue();
						long time = System.currentTimeMillis() - ((Long)context.get(CTX_TIME)).longValue();
						int processRate = (int)((double)(counter*1000)/(double)time);
						BackgroundJobPOJO bgPOJO = (BackgroundJobPOJO)context.get(CTX_JOB);
						//Add the Item PKs to the pipeline as comma seperated lines
						HashMap<String, TypedContent> pipeline = (HashMap<String, TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
						String pksAsLine = "";
						Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>)context.get(TransformerCtrlBean.CTX_PKS);
						for (Iterator<ItemPOJOPK> iter = pks.iterator(); iter.hasNext(); ) {
							ItemPOJOPK pk = iter.next();
							if(!"".equals(pksAsLine)) pksAsLine += "\n";
							pksAsLine += pk.getConceptName()+","+Util.joinStrings(pk.getIds(), ",");
						}
						try {
							pipeline.put(
									"Items Projected to the Data Manager", 
									new TypedContent(
											null,
											pksAsLine.getBytes("UTF-8"),
											"text/plain; charset=\"utf-8\""
									)
							);
						}catch (Exception e) {}
						
						try {
							bgPOJO.setMessage("Processing successfully completed at item "+counter+" (running at "+processRate+" items per second)");
							bgPOJO.setStatus(BackgroundJobPOJO._COMPLETED_);
							bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
							bgPOJO.setSerializedObject(getSerializedPipeline(pipeline));
							try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
						} catch (Exception e) {
							e.printStackTrace();
							String err = "Transformer Done but unable to store the pipeline in the background object: "
												+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
				    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
				    	    throw new XtentisException(err);
						}
					}
					public void stopped(int pluginHandle, TransformerPluginContext context) throws XtentisException {
						long counter = ((Long)context.get(CTX_COUNTER)).longValue();
						long time = System.currentTimeMillis() - ((Long)context.get(CTX_TIME)).longValue();
						int processRate = (int)((double)(counter*1000)/(double)time);
						BackgroundJobPOJO bgPOJO = (BackgroundJobPOJO)context.get(CTX_JOB);
						bgPOJO.setMessage("Processing item "+counter+" (running at "+processRate+" items per second)");
						bgPOJO.setStatus(BackgroundJobPOJO._RUNNING_);
						bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
						try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
					}
				}
			);
		} catch (Exception e) {
			try {
				if (! (e instanceof XtentisException)) {
		    	    String err = "Unable to Process the File Using Transformer "+processInfo.getTransformerPOJOPK().getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
				}
				//Update Background job and try to put pipeline
				BackgroundJobPOJO bgPOJO = Util.getBackgroundJobCtrlLocal().getBackgroundJob(new BackgroundJobPOJOPK(actionInfo.getJobId()));
				bgPOJO.setMessage("Error Processing the "+(actionInfo.getAction() == JOB_FILE ? "File":"Bytes")+" using a Transformer. "+e.getMessage());
				bgPOJO.setStatus(BackgroundJobPOJO._STOPPED_);
				bgPOJO.setTimestamp(sdf.format(new Date(System.currentTimeMillis())));
				bgPOJO.setSerializedObject(getSerializedPipeline((HashMap<String, TypedContent>)context.get(CTX_PIPELINE)));
				try { Util.getBackgroundJobCtrlLocal().putBackgroundJob(bgPOJO); } catch (Exception unlikely) {unlikely.printStackTrace();}
			} catch (Exception ex) {}
		}
				
	
	}
	
	
	/**
	 * Returns a Marshaled Pipeline
	 * @param pipeline
	 * @return a Castor Marshaled WSPipeline Object
	 * @throws MarshalException
	 * @throws ValidationException
	 * @throws UnsupportedEncodingException
	 */
	protected byte[] getSerializedPipeline(HashMap<String, TypedContent> pipeline) throws MarshalException,ValidationException,UnsupportedEncodingException{
		WSPipeline wsPipeline = new WSPipeline();
		ArrayList<WSPipelineTypedContentEntry> entries = new ArrayList<WSPipelineTypedContentEntry>();
		synchronized (pipeline) {
			Set<String> keys = pipeline.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
				String variable = iter.next();
				TypedContent content = pipeline.get(variable);
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("getSerializablePipeline() Variable "+variable);
				byte[] bytes = null;
				if (content.getBytes()!=null) {
					bytes = content.getBytes();
				} else {
					try {
						bytes = Util.getBytesFromStream(content.getStream());
					} catch (IOException e) {
						//the stream was already  consumed - Fire a warning
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("getSerializablePipeline() variable '"+variable+"': the stream was already consumed");
						bytes=null;
					}
				}
				WSExtractedContent wsContent = new WSExtractedContent();
				wsContent.setContentType(content.getContentType());
				wsContent.setWsByteArray(new WSByteArray(bytes));
				entries.add(new WSPipelineTypedContentEntry(variable,wsContent));
			}
		}
		wsPipeline.setTypedContentEntry(entries.toArray(new WSPipelineTypedContentEntry[entries.size()]));
		StringWriter sw = new StringWriter();
		Marshaller.marshal(wsPipeline, sw);
		return sw.toString().getBytes("UTF8");
	}

    
	
	
	
	
	/**
	 * The generic callback used by executeNextPlugin
	 * @author bgrieder
	 * @deprecated
	 *
	 */
	class PluginCallback implements TransformerPluginCallBack{
		
		//the pluginHandle is the plugin number
		public void contentIsReady(int pluginHandle, TypedContent content, TransformerPluginContext context) throws XtentisException {			
			TransformerPOJO transformerPOJO = (TransformerPOJO)context.get(CTX_TRANSFORMER);
			int pluginNumber = pluginHandle;
			//insert the result in the pipeline
			HashMap<String, TypedContent> pipeline = (HashMap<String, TypedContent>)context.get(CTX_PIPELINE);
			TransformerPluginSpec pluginSpec = transformerPOJO.getPluginSpecs().get(pluginNumber);
			String output = ((pluginSpec.getOutput() == null) || "".equals(pluginSpec.getOutput())) ? DEFAULT_VARIABLE : pluginSpec.getOutput();
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("contentIsReady() Inserting result of plugin '"+pluginSpec.getDescription()+"' in pipeline as variable "+output);
			pipeline.put(output, content);
			
			//see if we have more plugins to go
			if (pluginNumber == transformerPOJO.getPluginSpecs().size()-1) {
				long counter =  ((Long)context.get(CTX_COUNTER)).longValue();
				context.put(CTX_COUNTER, new Long(++counter));
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeNextPlugin:contentIsReady() END Transformer counter "+counter);
				//Finished - call the global call back
				try {
        			TransformerPluginCallBack globalCallBack =(TransformerPluginCallBack) context.get(CTX_EXECUTE_CALLBACK);
					globalCallBack.contentIsReady(pluginNumber, content, context);
				} catch (XtentisException e) {
					throw(e);
				} catch (Exception e) {
		    	    String err = "Unable to execute the global call back from plugin number "+pluginNumber
		    	    +" in Transformer "+transformerPOJO.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
		    	    throw new XtentisException(err);
				} 
			} else {
				context.put(CTX_PLUGIN_NUMBER, new Integer(pluginNumber+1));
				//we go on
				executeNextPlugin(
						pluginNumber+1,
						context,
						this
				);
			}//if
		}//content is Ready
		
		public void done(int pluginHandle, TransformerPluginContext context) throws XtentisException {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeNextPlugin:done() plugin "+pluginHandle);
			//check wehther this is the last done
			if (pluginHandle==0) { //yes
				//perform the pipeline clean-up by running the transformer plugins end procedures
				HashMap<Integer, Object> instantiatedPlugins = (HashMap<Integer, Object>) context.get(CTX_PLUGINS);
				Set<Integer> pluginNumbers = instantiatedPlugins.keySet();
				for (Iterator<Integer> iter = pluginNumbers.iterator(); iter.hasNext(); ) {
					Integer number = iter.next();
					Object plugin = instantiatedPlugins.get(number);
					try {
	    	        	context = (TransformerPluginContext)
    		        	Util.getMethod(plugin, "end").invoke(
    						plugin,
    						new Object[] {
    		        			context
    						}
    		        	);
					} catch (Exception e) {
						//only debug for that
						org.apache.log4j.Logger.getLogger(this.getClass()).debug(
								"done() Could not clean up plugin spec "+number+ " "+
								e.getClass().getName()+": "+e.getMessage()
						);
					}
				}
				//execute the Done of the Transformer Call Back
				TransformerPluginCallBack globalCallBack =(TransformerPluginCallBack) context.get(CTX_EXECUTE_CALLBACK);
				globalCallBack.done(pluginHandle,context);
			}
		}

		public void stopped(int pluginHandle, TransformerPluginContext context) throws XtentisException {
			TransformerPOJO transformerPOJO = (TransformerPOJO)context.get(CTX_TRANSFORMER);
			int pluginNumber = pluginHandle;
			TransformerPluginSpec pluginSpec= transformerPOJO.getPluginSpecs().get(pluginHandle);
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("stopped() Plugin '"+pluginSpec.getDescription()+"'");

			//go on with processing
			//see if we have more plugins to go
			if (pluginNumber == transformerPOJO.getPluginSpecs().size()-1) {
				long counter =  ((Long)context.get(CTX_COUNTER)).longValue();
				context.put(CTX_COUNTER, new Long(++counter));
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeNextPlugin:stopped() END Transformer counter "+counter);
				//Finished - call the global call back
				try {
        			TransformerPluginCallBack globalCallBack =(TransformerPluginCallBack) context.get(CTX_EXECUTE_CALLBACK);
					globalCallBack.stopped(pluginHandle, context);
				} catch (XtentisException e) {
					throw(e);
				} catch (Exception e) {
		    	    String err = "Unable to execute the global call back from plugin number "+pluginNumber
		    	    +" in Transformer "+transformerPOJO.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
		    	    throw new XtentisException(err);
				} 
			} else {
				context.put(CTX_PLUGIN_NUMBER, new Integer(pluginNumber+1));
				//we go on
				executeNextPlugin(
						pluginNumber+1,
						context,
						this
				);
			}//if more plugins to go
			
			
			/*
			if (pluginHandle == 0) {
				TransformerPluginCallBack globalCallBack =(TransformerPluginCallBack) context.get(CTX_EXECUTE_CALLBACK);
				globalCallBack.stopped(pluginHandle, context);
			} else {
				HashMap<Integer, Object> instantiatedPlugins = (HashMap<Integer, Object>)context.get(CTX_PLUGINS);			    	
		    	//Check if plugin is already instantiated and initialized
		    	Object plugin = instantiatedPlugins.get(new Integer(pluginHandle-1));
		    	try {
		        	Util.getMethod(plugin, "stop").invoke(
						plugin,
						new Object[] {context}
		        	);
		    	} catch (Exception e) {
		    		TransformerPOJO transformerPOJO = (TransformerPOJO)context.get(CTX_TRANSFORMER);
		    	    String err = "Unable to propagate the STOP signal to plugin number "+pluginHandle
		    	    +" in Transformer "+transformerPOJO.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
		    	    throw new XtentisException(err);
				} 
			}
			*/
		}
			
	}
	
	
	
	
	/**
	 * Used to process FIles in Background Jobs
	 * @author bgrieder
	 *
	 */
    class ProcessInfo implements Serializable {
		private String filename;
		private byte[] bytes;
		private String contentType;
		private TransformerPOJOPK transformerPOJOPK;
		private HashMap<String,String> decisionTable;
		private String username;
		
		public ProcessInfo() {
			super();
		}
		
		public ProcessInfo(String filename, byte[] bytes, String contentType, TransformerPOJOPK transformerPOJOPK, HashMap<String, String> decisionTable, String username) {
			super();
			this.filename = filename;
			this.bytes = bytes;
			this.contentType = contentType;
			this.transformerPOJOPK = transformerPOJOPK;
			this.decisionTable = decisionTable;
			this.username = username;
		}
		
		public String getContentType() {
			return contentType;
		}
		public void setContentType(String contentType) {
			this.contentType = contentType;
		}
		public HashMap<String, String> getDecisionTable() {
			return decisionTable;
		}
		public void setDecisionTable(HashMap<String, String> decisionTable) {
			this.decisionTable = decisionTable;
		}
		public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
		public TransformerPOJOPK getTransformerPOJOPK() {
			return transformerPOJOPK;
		}
		public void setTransformerPOJOPK(TransformerPOJOPK transformerPOJOPK) {
			this.transformerPOJOPK = transformerPOJOPK;
		}
		public byte[] getBytes() {
			return bytes;
		}
		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		
    }
	
		

}