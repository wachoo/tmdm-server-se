package com.amalto.core.objects.transformers.v2.ejb;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.security.Principal;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
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
import javax.security.auth.Subject;

import com.amalto.core.delegator.BeanDelegatorContainer;
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
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
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
    	BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().setSessionContext(ctx);
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
    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().putTransformer(transformer);

    }
    
     
    /**
     * Get item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerV2POJO getTransformer(TransformerV2POJOPK pk) throws XtentisException{

    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().getTransformer(pk);
    }
    
    /**
     * Get a Transformer - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerV2POJO existsTransformer(TransformerV2POJOPK pk)    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().existsTransformer(pk);
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
    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().removeTransformer(pk);
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
    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().getTransformerPKs(regex);
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
    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().extractThroughTransformer(transformerV2POJOPK, itemPOJOPK);
    	
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
    	
    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().executeAsJob(context, callBack);
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
    	BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().execute(universe, context, callBack);
    	
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
    	 BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().execute(context, callBack);
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
    	
    	 BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().execute(context, content, callBack);
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
    	
    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().executeUntilDone(context);
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
    	return BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().executeUntilDone(context, content);
    }

    
    
 
 
    /*****************************************************************
     *  TransformerPluginCallBack Implementation
    *****************************************************************/

    /**
     * Implementation of {@link TransformerPluginCallBack#contentIsReady(TransformerPluginContext)}
     */
	public void contentIsReady(TransformerPluginContext pluginContext) throws XtentisException {
		BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().contentIsReady(pluginContext);
	}
   
    
  
    /**
     * Run execute as a background job
     */
	public void ejbTimeout(Timer timer) {
		BeanDelegatorContainer.getUniqueInstance().getTransformerV2CtrlBeanDelegator().ejbTimeout(timer);
	}
}