package com.amalto.core.objects.synchronization.ejb;

import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.Stub;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationItemCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationObjectCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationPlanCtrlLocal;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.WSBoolean;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSExistsDBDataCluster;
import com.amalto.core.webservice.WSItemPK;
import com.amalto.core.webservice.WSPing;
import com.amalto.core.webservice.WSPutDBDataCluster;
import com.amalto.core.webservice.WSString;
import com.amalto.core.webservice.WSSynchronizationGetItemXML;
import com.amalto.core.webservice.WSSynchronizationGetObjectXML;
import com.amalto.core.webservice.WSSynchronizationGetUnsynchronizedItemPKs;
import com.amalto.core.webservice.WSSynchronizationGetUnsynchronizedObjectsIDs;
import com.amalto.core.webservice.WSSynchronizationPutItemXML;
import com.amalto.core.webservice.WSSynchronizationPutObjectXML;
import com.amalto.core.webservice.XtentisPort;



/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="SynchronizationPlanCtrl"
 *          display-name="Name for SynchronizationPlanCtrl"
 *          description="Description for SynchronizationPlanCtrl"
 *          jndi-name="amalto/remote/core/synchronizationPlanctrl"
 * 		  	local-jndi-name = "amalto/local/core/synchronizationPlanctrl"
 *          type="Stateless"
 *          view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * 
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 *  
 */
public class SynchronizationPlanCtrlBean implements SessionBean, TimedObject{
  
	public static final long serialVersionUID = 1L;
	private SessionContext sessionContext;
	
    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    	sessionContext = ctx;
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
     * Creates or updates a SynchronizationPlan
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJOPK putSynchronizationPlan(SynchronizationPlanPOJO synchronizationPlan) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putSynchronizationPlan() "+synchronizationPlan.getName());
    	
        try {
            
            ObjectPOJOPK pk = synchronizationPlan.store();
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new SynchronizationPlanPOJOPK(pk);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the SynchronizationPlan "+synchronizationPlan.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
    
    /**
     * Creates or updates a SynchronizationPlan for a particular revision ID<br/>
     * The user must have the role 'administration' to perform this task
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJOPK putSynchronizationPlan(String revisionID, SynchronizationPlanPOJO synchronizationPlan) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putSynchronizationPlan() "+synchronizationPlan.getName());
    	
        try {
            
        	if (! LocalUser.getLocalUser().getRoles().contains("administration")) {
        		String err = "The user '"+LocalUser.getLocalUser().getUsername()+"' does not have the 'administration' role";
        	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        	    throw new XtentisException(err);
        	}
        	
            ObjectPOJOPK pk = synchronizationPlan.store(revisionID);
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new SynchronizationPlanPOJOPK(pk);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the SynchronizationPlan "+synchronizationPlan.getName()+" in revision id '"+revisionID+"'"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
    
     
    /**
     * Get SynchronizationPlan
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJO getSynchronizationPlan(SynchronizationPlanPOJOPK pk) throws XtentisException{

        try {
        	SynchronizationPlanPOJO sp = ObjectPOJO.load(SynchronizationPlanPOJO.class,pk);
        	if (sp == null) {
        		String err= "The Synchronization Plan "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Synchronization Plan "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get Synchronization Plan of a particular revision ID<br/>
     * The user must have the 'administration' role to preform this task
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJO getSynchronizationPlan(String revisionID, SynchronizationPlanPOJOPK pk) throws XtentisException{
    	
        try {
        	if (! LocalUser.getLocalUser().getRoles().contains("administration")) {
        		String err = "The user '"+LocalUser.getLocalUser().getUsername()+"' does not have the 'administration' role";
        	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        	    throw new XtentisException(err);
        	}
        	
        	SynchronizationPlanPOJO sp =  ObjectPOJO.load(revisionID, SynchronizationPlanPOJO.class, pk);
        	if (sp == null) {
        		String err= "The Synchronization Plan "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Synchronization Plan '"+pk.toString()+"' of revision ID '"+revisionID+"'"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    
    /**
     * Get a SynchronizationPlan - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public SynchronizationPlanPOJO existsSynchronizationPlan(SynchronizationPlanPOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(SynchronizationPlanPOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Synchronization Plan exists:  "+pk.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
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
    public SynchronizationPlanPOJOPK removeSynchronizationPlan(SynchronizationPlanPOJOPK pk) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("Removing "+pk.getUniqueId());

        try {
        	return new SynchronizationPlanPOJOPK(ObjectPOJO.remove(SynchronizationPlanPOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Synchronization Plan "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }    
    
    
    
    /**
	 * Retrieve all SynchronizationPlan PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<SynchronizationPlanPOJOPK> getSynchronizationPlanPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(SynchronizationPlanPOJO.class,regex);
    	ArrayList<SynchronizationPlanPOJOPK> l = new ArrayList<SynchronizationPlanPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new SynchronizationPlanPOJOPK(iter.next()));
		}
    	return l;
    }
	
    
    /**
     * Retrieve all the Unique ID Strings of Xtentis Objects of a particular name that match a particular instance pattern
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param objectName
     * @param instancePattern
     * @param synchronizationPlanName
     * 			The synchronization plan objects are NOT synchronized with
     * 			<code>null</code> retrieves all objects
     * @return
     * 		The uniqueID of the object
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ArrayList<String> synchronizationGetAllUnsynchronizedObjectsIDs(
    	String revisionID, 
    	String objectName, 
    	String instancePattern, 
    	String synchronizationPlanName) 
    throws XtentisException{
        try {
        	return ObjectPOJO.findAllUnsynchronizedPKs(revisionID, objectName, instancePattern, synchronizationPlanName); 	
	    } catch (XtentisException e) {
	    	throw e;
	    } catch (Exception e) {
	    	String err = "Error Finding All Unsynchronized PKs: "+e.getMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    
    
    /**
     * Return the marshaled Xtentis Object (e.g. its XML)
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param objectName
     * @param uniqueID
     * 		The unique ID obtain by calling {@link #synchronizationGetAllUnsynchronizedObjectsIDs(String, String, String, String)}
     * @return
     * 		The marshaled version of the object
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String synchronizationGetMarshaledObject(String revisionID, String objectName, String uniqueID) throws XtentisException{
        try {
        	return ObjectPOJO.getMarshaledObject(revisionID, objectName, uniqueID); 	
	    } catch (XtentisException e) {
	    	throw e;
	    } catch (Exception e) {
	    	String err = "Error getting the marshaled object '"+objectName+"' of id '"+uniqueID+"' in revision '"+(revisionID == null ? "[HEAD]": revisionID)+"'";
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Replaces or creates the xtentis object XML. This methods is called by Synchronization Plans
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param objectName
     * @param uniqueID
     * 		The unique ID obtain by calling {@link #synchronizationGetAllUnsynchronizedObjectsIDs(String, String, String, String)}
     * @param xml
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public void synchronizationPutMarshaledObject(String revisionID, String objectName, String uniqueID, String xml) throws XtentisException{
        try {
        	ObjectPOJO.putMarshaledObject(revisionID, objectName, uniqueID, xml); 	
	    } catch (XtentisException e) {
	    	throw e;
	    } catch (Exception e) {
	    	String err = "Error creating/replaceing the marshaled object '"+objectName+"' of id '"+uniqueID+"' in revision '"+(revisionID == null ? "[HEAD]": revisionID)+"'";
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    
    
    /**
     * Retrieve all the {@link ItemPOJOPK}s of Items in a revision 
     * that match a particular concept pattern and a particular instance pattern<br/>
     * <br/>
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param conceptPattern
     * @param instancePattern
     * @param synchronizationPlanPOJOPK
     * 			The synchronization plan objects are NOT synchronized with
     * 			<code>null</code> retrieves all objects
     * @param start
     * @param limit
     * @return
     * 		The uniqueID of the object
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ArrayList<ItemPOJOPK> synchronizationGetAllUnsynchronizedItemPOJOPKs(
    	String revisionID,
    	DataClusterPOJOPK dataClusterPOJOPK,
    	String conceptPattern, 
    	String instancePattern, 
    	SynchronizationPlanPOJOPK synchronizationPlanPOJOPK,
    	long start,
    	int limit
    ) throws XtentisException{
        try {
        	return ItemPOJO.findAllUnsynchronizedPKs(
        		revisionID, 
        		dataClusterPOJOPK,
        		conceptPattern, 
        		instancePattern, 
        		synchronizationPlanPOJOPK,
        		start,
        		limit
        	); 	
	    } catch (XtentisException e) {
	    	throw e;
	    } catch (Exception e) {
	    	String err = "Error Finding All Unsynchronized Item PKs: "+e.getMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Return the marshaled Xtentis Item (e.g. its XML)
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param itemPOJOPK
     * 
     * @return
     * 		The marshaled version of the object
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String synchronizationGetMarshaledItem(
    	String revisionID,
    	ItemPOJOPK itemPOJOPK
    ) throws XtentisException{
        try {
        	ItemPOJO pojo = ItemPOJO.load(revisionID, itemPOJOPK);
        	return pojo == null ? null : pojo.serialize();	
	    } catch (XtentisException e) {
	    	throw e;
	    } catch (Exception e) {
	    	String err = "Error getting the marshaled item '"+itemPOJOPK.getUniqueID()+" in revision '"+(revisionID == null ? "[HEAD]": revisionID)+"'";
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Replaces or creates the xtentis object XML. This methods is called by Synchronization Plans
     * The user running this method must have an 'administration' role
     * 
     * @param revisionID
     * @param xml
     * 		the full Item XML
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ItemPOJOPK synchronizationPutMarshaledItem(
    	String revisionID, 
    	String xml
    ) throws XtentisException{
        try {
        	return ItemPOJO.parse(xml).store(revisionID);
	    } catch (XtentisException e) {
	    	throw e;
	    } catch (Exception e) {
	    	String err = "Error creating/replacing the marshaled item in revision '"+(revisionID == null ? "[HEAD]": revisionID)+"' \n"+xml;
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    
    /**
	 * Execute the Synchronization Plan 
	 * 
	 * @throws XtentisException
	 * @return the status
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */  
    public String[] action(int action, SynchronizationPlanPOJOPK pk) throws XtentisException {
    	//Load the Synchronization Plan
    	SynchronizationPlanPOJO plan = null;
    	try {
    		plan = existsSynchronizationPlan(new SynchronizationPlanPOJOPK(pk));
    	} catch (XtentisException e) {
    		String err = "Unable to load the Plan '"+pk.getUniqueId()+"'";
    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    		throw new XtentisException(err);
    	}
    	
    	if (action == SynchronizationPlanPOJO.ACTION_GET_STATUS) {
    		if (plan==null) {
    			return new String[] {
        			SynchronizationPlanPOJO.STATUS_DOES_NOT_EXIST,
        			"",
        			"-1",
        			"-1"
        		};
    		}
    		return new String[] {
    			plan.getCurrentStatusCode(), 
    			plan.getCurrentStatusMessage(),
    			plan.getLastRunStarted()+"",
    			plan.getLastRunStopped()+""
    		};
    	}
    	
    	//For all other actions we need a plan
    	if (plan==null) {
        	String err = "The Synchronization Plan '"+pk.getUniqueId()+"' does not exist";
    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    		throw new XtentisException(err);
    	}
    	
    	
    	if ((action == SynchronizationPlanPOJO.ACTION_START_FULL) || (action == SynchronizationPlanPOJO.ACTION_START_DIFFERENTIAL)) {

    		//check that is not currently executing (what if scheduled or stopping ?)
    		if (plan.getCurrentStatusCode() == SynchronizationPlanPOJO.STATUS_RUNNING) {
    			String err = "The Synchronization Plan '"+plan.getName()+"' is already running";
    			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    			throw new XtentisException(err);
    		}
    		
    		//Update Plan
    		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_SCHEDULED);
    		plan.setCurrentStatusMessage("Scheduling a "+(action == SynchronizationPlanPOJO.ACTION_START_FULL ? "FULL": "DIFFERENTIAL")+" synchronization");
       		try {
    	        putSynchronizationPlan(plan);
            } catch (XtentisException e) {
            	String err = "Unable to schedule the Plan '"+pk.getUniqueId()+"' to execute";
     	        org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
     	        throw new XtentisException(err);
            }
            
            //build the Timer
            
            //determine this Plan revision ID
            String revisionID = LocalUser.getLocalUser().getUniverse().getXtentisObjectsRevisionIDs().get(
            			ObjectPOJO.getObjectName(SynchronizationPlanPOJO.class)
            );
            
    		ArrayList<String> actionInfo = new ArrayList<String>();
    		actionInfo.add(revisionID == null ? "" : revisionID);
        	actionInfo.add(pk.getUniqueId());
        	actionInfo.add(action == SynchronizationPlanPOJO.ACTION_START_FULL ? "full" : "differential");
            TimerService timerService =  sessionContext.getTimerService();
            timerService.createTimer(150,actionInfo);  //0,15 second
            return new String[] {
            	plan.getCurrentStatusCode(),
            	plan.getCurrentStatusMessage(),
    			plan.getLastRunStarted()+"",
    			plan.getLastRunStopped()+""
            };
    	}
    	
    	
    	if (action == SynchronizationPlanPOJO.ACTION_STOP) {
    		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_STOPPING);
    		plan.setCurrentStatusMessage("Stop requested");
    		try {
    	        putSynchronizationPlan(plan);
            } catch (XtentisException e) {
            	String err = "Unable to request the Plan '"+pk.getUniqueId()+"' to stop";
     	        org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
     	        throw new XtentisException(err);
            }
    		return new String[] {
    			plan.getCurrentStatusCode(),
    			plan.getCurrentStatusMessage(),
    			plan.getLastRunStarted()+"",
    			plan.getLastRunStopped()+""
    		};
    	}
    	
    	if (action == SynchronizationPlanPOJO.ACTION_RESET) {
    		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_FAILED);
    		plan.setCurrentStatusMessage("Plan reset");
    		try {
    	        putSynchronizationPlan(plan);
            } catch (XtentisException e) {
            	String err = "Unable to request the Plan '"+pk.getUniqueId()+"' to reset";
     	        org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
     	        throw new XtentisException(err);
            }
    		return new String[] {
    			plan.getCurrentStatusCode(), 
    			plan.getCurrentStatusMessage(),
    			plan.getLastRunStarted()+"",
    			plan.getLastRunStopped()+""
    		};
    	}

    	throw new XtentisException("Unknown synchronization plan '"+pk.getUniqueId()+"' action '"+action+"'");
    }
    
    
    /*********************************************************************************************************************
     * 
     * SYNCHRONIZATION ALGORITHMS
     * 
     /*********************************************************************************************************************/
    
    protected final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss-SSS z");
    
    
    public void ejbTimeout(Timer timer) {
        ArrayList<String> actionInfo = (ArrayList<String>)timer.getInfo();
        String revisionID = "".equals(actionInfo.get(0)) ? null : actionInfo.get(0);
        String planName = actionInfo.get(1);
        boolean fullSynchronization = "full".equals(actionInfo.get(2)); 
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("ejbTimeout() "+revisionID+"/"+planName+" - "+actionInfo.get(2));
        
        //Load the Synchronization Plan
    	SynchronizationPlanPOJO plan = null;
        try {
        	//get the session ctrl bean to force a JACC user refresh
        	SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
	        plan = ctrl.getSynchronizationPlan(revisionID, new SynchronizationPlanPOJOPK(planName));
	        execute(revisionID, plan, fullSynchronization);
        } catch (Throwable t) {
    		try {
    			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
    			plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_FAILED);
    			plan.setCurrentStatusMessage(t.getMessage());
    			plan.setLastRunStopped(System.currentTimeMillis());
    	        ctrl.putSynchronizationPlan(plan);
            } catch (Throwable th) {
    	        String err = "Unable to update the Plan '"+planName+"' to signify error  ["+t.getMessage()+"]";
    	        org.apache.log4j.Logger.getLogger(this.getClass()).error(err,th);
            } 
        }
        
    }
    
    
    
    private static final int SYNCH_TYPE_FULL = 1;
    private static final int SYNCH_TYPE_OVERWRITE_LOCAL = 2;
    private static final int SYNCH_TYPE_OVERWRITE_REMOTE = 3;
    
    
    /**
     * Main entry point to execute a full or differential synchronization
     * @param revisionID
     * @param plan
     * @param fullSynchronization
     * @throws XtentisException
     */
    private void execute(String revisionID, SynchronizationPlanPOJO plan, boolean fullSynchronization) throws XtentisException {
        try {
        	
        	//check that is not currently executing
        	if (! plan.getCurrentStatusCode().equals(SynchronizationPlanPOJO.STATUS_SCHEDULED)) {
        		String err = "The Synchronization Plan '"+plan.getName()+"' was not correctly scheduled (current status: "+plan.getCurrentStatusCode()+")";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	
        	plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_RUNNING);
        	
        	
        	//If first run or force a full synchronization --> full synchronization
        	if (plan.getLastRunStarted() <= 0 || fullSynchronization) {
        		plan.setCurrentStatusMessage("Starting FULL synchronization");
        		plan.setLastRunStarted(System.currentTimeMillis());
        		SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
        		ctrl.putSynchronizationPlan(revisionID, plan);
        		executeFullSynchronization(revisionID, plan);
        	} else {
        		plan.setCurrentStatusMessage("Starting DIFFERENTIAL synchronization");
        		plan.setLastRunStarted(System.currentTimeMillis());
        		SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
        		ctrl.putSynchronizationPlan(revisionID, plan);
        		executeDifferentialSynchronization(revisionID, plan);
        	}
        	
        	org.apache.log4j.Logger.getLogger(this.getClass()).debug("SYNCHRONIZATION DONE");
        	
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to execute the "+(fullSynchronization ? "full" : "differential")+" synchronization of Plan '"+plan.getName()+"'"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    	 
    }
    
    private void executeFullSynchronization(String revisionID, SynchronizationPlanPOJO plan) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("executeFullSynchronization() Executing Plan '"+plan.getName());

    	try {
        	
        	//Get the remote port
        	XtentisPort remotePort = getWSPort(plan.getRemoteSystemURL(), plan.getRemoteSystemUsername(), plan.getRemoteSystemPassword());
        	remotePort.ping(new WSPing("to"));//ping test
        	//Get the Synchronization Object Ctrl
        	SynchronizationObjectCtrlLocal objectCtrl = Util.getSynchronizationObjectCtrlLocal();
        	
        	//Get the Synchronization Item Ctrl
        	SynchronizationItemCtrlLocal synchroItemCtrl = Util.getSynchronizationItemCtrlLocal();
        	
        	//Get the SYnchronization Plan Ctrl
    		SynchronizationPlanCtrlLocal planCtrl = Util.getSynchronizationPlanCtrlLocal();
        	
    		
    		///////////////
    		// OBJECTS
    		///////////////
    		
        	Set<String> xtentisObjectNames = plan.getXtentisObjectsSynchronizations().keySet();
        	for (Iterator<String> iterator = xtentisObjectNames.iterator(); iterator.hasNext(); ) {
        		String objectName = iterator.next();
        		
        		//reload the plan status to see if STOPPING is required
        		String statusCode = planCtrl.getSynchronizationPlan(revisionID, new SynchronizationPlanPOJOPK(plan.getName())).getCurrentStatusCode();
        		if (SynchronizationPlanPOJO.STATUS_STOPPING.equals(statusCode)) {
        			//stop requested
        			throw new XtentisException("Stop requested by user");
        		}
        		
        		org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeFullSynchronization() Checking "+objectName);
        		
        		ArrayList<SynchronizationPlanObjectLine> lines = plan.getXtentisObjectsSynchronizations().get(objectName).getList();
        		
        		//if we have no line, skip the object
        		if (lines == null || lines.size() == 0) continue;
        		
        		//update the plan status
        		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_RUNNING);
        		plan.setCurrentStatusMessage("Processing '"+objectName+"'");
        		planCtrl.putSynchronizationPlan(revisionID, plan);
        		
				
				for (Iterator<SynchronizationPlanObjectLine> iterator2 = lines.iterator(); iterator2.hasNext(); ) {
					SynchronizationPlanObjectLine line = iterator2.next();
					
					//A cache of already synchronized objects
					ArrayList<String> alreadySynchronizedIDs = new ArrayList<String>();
					
					//First pass :  Update central with remote by fetching all remote items
					String[] remoteIDs= remotePort.synchronizationGetUnsynchronizedObjectsIDs(new WSSynchronizationGetUnsynchronizedObjectsIDs(
						line.getDestinationRevisionID(),
						objectName,
						line.getInstancePattern(),
						null
					)).getStrings();
					
					if (remoteIDs != null ) {
    					for (int i = 0; i < remoteIDs.length; i++) {
    						org.apache.log4j.Logger.getLogger(this.getClass()).debug(
    							"executeFullSynchronization() ---------------First Pass Processing Remote " +
    							line.getDestinationRevisionID()+"/"+objectName+": "+remoteIDs[i]+" ["+line.getAlgorithm()+"]"
    						);
    						//Synchronize the objects
    						synchronizeObjects(
    							plan, 
    							remotePort, 
    							planCtrl,
    							objectCtrl, 
    							line.getAlgorithm(), 
    							line.getSourceRevisionID(), 
    							line.getDestinationRevisionID(), 
    							objectName, 
    							remoteIDs[i],
    							SYNCH_TYPE_FULL
    						);
    						//put in cache
    						alreadySynchronizedIDs.add(remoteIDs[i]);
    						
    					}// for IDS
					}					
					
					org.apache.log4j.Logger.getLogger(this.getClass()).trace(
						"executeFullSynchronization() Second Pass search '"+line.getSourceRevisionID()+"/"+objectName+"'  pattern '"+line.getInstancePattern()+"'"
					);
					
					//Second Pass: Update remote with central by fetching all local items
					ArrayList<String> localIDs = planCtrl.synchronizationGetAllUnsynchronizedObjectsIDs(
						line.getSourceRevisionID(), 
						objectName, 
						line.getInstancePattern(), 
						null
					);
					
					for (Iterator<String> iterator3 = localIDs.iterator(); iterator3.hasNext(); ) {
						String localID = iterator3.next();
						
						//if already done, skip
						if (alreadySynchronizedIDs.contains(localID)) continue;
						
						org.apache.log4j.Logger.getLogger(this.getClass()).debug(
							"executeFullSynchronization() ---------------Second Pass Processing Local " +
							line.getSourceRevisionID()+"/"+objectName+": "+localID+" ["+line.getAlgorithm()+"]"
						);

						//Synchronize the objects
						synchronizeObjects(
							plan, 
							remotePort, 
							planCtrl,
							objectCtrl, 
							line.getAlgorithm(), 
							line.getSourceRevisionID(), 
							line.getDestinationRevisionID(), 
							objectName, 
							localID,
							SYNCH_TYPE_FULL
						);
						
					}// for IDS
					
					
				}//for lines
			}//for objects
        	
        	
        	///////////////
        	// ITEMS
        	///////////////
            //get the xml server wrapper            

        	//if we have no line, skip the Items
			if (plan.getItemsSynchronizations() != null) {
        	
            	for (Iterator<SynchronizationPlanItemLine> iterator = plan.getItemsSynchronizations().iterator(); iterator.hasNext(); ) {
    				SynchronizationPlanItemLine line = iterator.next();

    				//reload the plan status to see if STOPPING is required
            		String statusCode = planCtrl.getSynchronizationPlan(revisionID, new SynchronizationPlanPOJOPK(plan.getName())).getCurrentStatusCode();
            		if (SynchronizationPlanPOJO.STATUS_STOPPING.equals(statusCode)) {
            			//stop requested
            			throw new XtentisException("Stop requested by user");
            		}
             		String info = 
            				"Processing Items concepts matching '"+line.getConceptName()+"', instances ids matching '"+line.getIdsPattern()+"'"
        					+"  LOCAL Cluster '"+line.getLocalClusterPOJOPK().getUniqueId()+"' and revision '"+line.getLocalRevisionID()+"'"
        					+" <--> REMOTE Cluster '"+line.getRemoteClusterPOJOPK().getUniqueId()+"' and revision '"+line.getRemoteRevisionID()+"'"  
        					+" ["+line.getAlgorithm()+"]";
            		org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeFullSynchronization() "+info);
            		//check data cluster
            		checkSyncDataCluster(remotePort, line);
            		//update the plan status
            		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_RUNNING);
            		plan.setCurrentStatusMessage(info);
            		planCtrl.putSynchronizationPlan(revisionID, plan);
    				
            		//A cache of already synchronized items
					ArrayList<String> alreadySynchronizedPKs = new ArrayList<String>();
    				
					//First pass :  Update central with remote by fetching all remote items
					WSItemPK[] remotePKs = remotePort.synchronizationGetUnsynchronizedItemPKs(new WSSynchronizationGetUnsynchronizedItemPKs(
						line.getRemoteRevisionID(),
						new WSDataClusterPK(line.getRemoteClusterPOJOPK().getUniqueId()),
						line.getConceptName(),
						line.getIdsPattern(),
						null, //plan.getName()  - FULL Synch
						0,
						Integer.MAX_VALUE
					)).getWsItemPK();
					
					if (remotePKs != null) {
						for (int i = 0; i < remotePKs.length; i++) {
							ItemPOJOPK localPK = new ItemPOJOPK(
								new DataClusterPOJOPK(line.getLocalClusterPOJOPK()),
								remotePKs[i].getConceptName(),
								remotePKs[i].getIds()
							);
							
							synchronizeItems(
								plan, 
								remotePort, 
								planCtrl, 
								synchroItemCtrl, 
								line.getAlgorithm(), 
								line.getLocalRevisionID(), 
								line.getRemoteClusterPOJOPK(),
								line.getRemoteRevisionID(),
								localPK, 
								SYNCH_TYPE_FULL
							);
							
							alreadySynchronizedPKs.add(localPK.getUniqueID());
							
						}
					}
					
					//Second Pass: Update remote with central by fetching all local items
					ArrayList<ItemPOJOPK> localPKs = planCtrl.synchronizationGetAllUnsynchronizedItemPOJOPKs(
						line.getLocalRevisionID(),
						line.getLocalClusterPOJOPK(),
						line.getConceptName(),
						line.getIdsPattern(),
						null, //new SynchronizationPlanPOJOPK(plan.getName()),  - FULL Synch
						0,
						Integer.MAX_VALUE
					);
					
					for (Iterator<ItemPOJOPK> iterator3 = localPKs.iterator(); iterator3.hasNext(); ) {
						ItemPOJOPK localPK = iterator3.next();
						
						//if already done, skip
						if (alreadySynchronizedPKs.contains(localPK.getUniqueID())) continue;
						

						//Synchronize the objects
						synchronizeItems(
							plan, 
							remotePort, 
							planCtrl, 
							synchroItemCtrl, 
							line.getAlgorithm(), 
							line.getLocalRevisionID(),
							line.getRemoteClusterPOJOPK(),
							line.getRemoteRevisionID(),
							localPK, 
							SYNCH_TYPE_FULL
						);
						
					}// for IDS
    				
    			}
			} //ITEMS
        	
        	
    		//update the plan status
    		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_COMPLETED);
    		plan.setCurrentStatusMessage("Completed on "+sdf.format(new Date()));
    		plan.setLastRunStopped(System.currentTimeMillis());
    		planCtrl.putSynchronizationPlan(revisionID, plan);
        	
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to execute the Synchronization Plan '"+plan.getPK().toString()+"'"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    	
    }
    private void checkSyncDataCluster(XtentisPort remotePort, SynchronizationPlanItemLine line)throws Exception{
   		//local cluster exist check aiming see 8908
    	XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
		if(line.getAlgorithm().equals(SynchronizationPlanPOJO.LOCAL_WINS)){            			
			DataClusterPOJO dataclusterLocal=Util.getDataClusterCtrlLocal().existsDataCluster(line.getLocalClusterPOJOPK());
			if(dataclusterLocal==null){
				throw new XtentisException("Local Cluster:"+ line.getLocalClusterPOJOPK().getUniqueId() +" with revision:"+line.getLocalRevisionID() +" does not exist!");
			}	
			//default create remote db data cluster collection
			remotePort.putDBDataCluster(new WSPutDBDataCluster(line.getRemoteRevisionID(),line.getRemoteClusterPOJOPK().getUniqueId()));
		}
		//remote cluster exist check
		if(line.getAlgorithm().equals(SynchronizationPlanPOJO.REMOTE_WINS)){          
			WSBoolean b=remotePort.existsDBDataCluster(new WSExistsDBDataCluster(line.getRemoteRevisionID(),line.getRemoteClusterPOJOPK().getUniqueId()));
			if(!b.is_true()){
				throw new XtentisException("Remote Cluster:"+ line.getRemoteClusterPOJOPK().getUniqueId() +" with revision:"+ line.getRemoteRevisionID()+" does not exist!");
			}
			//default create local db data cluster collection
			server.createCluster(line.getLocalRevisionID(), line.getLocalClusterPOJOPK().getUniqueId());
			//store the data cluster
			new DataClusterPOJO(line.getLocalClusterPOJOPK().getUniqueId(),"","").store(line.getLocalRevisionID());
		}
		//end

    }
    /**
     * A differential Synchronization: objects and items not marked as synchronized during the last run of this plan are evaluated
     * @param revisionID
     * @param plan
     * @throws XtentisException
     */
    private void executeDifferentialSynchronization(String revisionID, SynchronizationPlanPOJO plan) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("executeDifferentialSynchronization() Plan "+revisionID+"/"+plan.getName());
    	
    	try {
    		
        	//Get the remote port
        	XtentisPort remotePort = getWSPort(plan.getRemoteSystemURL(), plan.getRemoteSystemUsername(), plan.getRemoteSystemPassword());
        	remotePort.ping(new WSPing("to"));//ping test
        	
        	//Get the Synchronization Object Ctrl
        	SynchronizationObjectCtrlLocal objectCtrl = Util.getSynchronizationObjectCtrlLocal();
        	
        	//Get the Synchronization Item Ctrl
        	SynchronizationItemCtrlLocal synchroItemCtrl = Util.getSynchronizationItemCtrlLocal();
        	
        	//Get the SYnchronization Plan Ctrl
    		SynchronizationPlanCtrlLocal planCtrl = Util.getSynchronizationPlanCtrlLocal();
        	
        	Set<String> xtentisObjectNames = plan.getXtentisObjectsSynchronizations().keySet();
        	for (Iterator<String> iterator = xtentisObjectNames.iterator(); iterator.hasNext(); ) {
				String objectName = iterator.next();
				ArrayList<SynchronizationPlanObjectLine> lines = plan.getXtentisObjectsSynchronizations().get(objectName).getList();
				if (lines == null || lines.size() == 0) continue;
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeDifferentialSynchronization() Plan '"+plan.getName()+"': checking Object "+objectName);
				
				//reload the plan status to see if STOPPING is required
				String statusCode = planCtrl.getSynchronizationPlan(revisionID, new SynchronizationPlanPOJOPK(plan.getName())).getCurrentStatusCode();
        		if (SynchronizationPlanPOJO.STATUS_STOPPING.equals(statusCode)) {
        			//stop requested
        			throw new XtentisException("Stop requested by user");
        		} else if (SynchronizationPlanPOJO.STATUS_FAILED.equals(statusCode)) {
        			//stop requested
        			throw new XtentisException("The plan failed but was still running");
        		} else if (SynchronizationPlanPOJO.STATUS_COMPLETED.equals(statusCode)) {
        			//stop requested
        			throw new XtentisException("The plan completed but was still running !");
        		}
        		
        		//update the plan status
        		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_RUNNING);
        		plan.setCurrentStatusMessage("Processing '"+objectName+"'");
        		planCtrl.putSynchronizationPlan(revisionID, plan);
				
				//if we have no line, skip the object
			
				for (Iterator<SynchronizationPlanObjectLine> iterator2 = lines.iterator(); iterator2.hasNext(); ) {
					SynchronizationPlanObjectLine line = iterator2.next();
					
					//Fetch all changed/unsynchronized remote objects
					String[] remoteIDs= remotePort.synchronizationGetUnsynchronizedObjectsIDs(new WSSynchronizationGetUnsynchronizedObjectsIDs(
						line.getDestinationRevisionID(),
						objectName,
						line.getInstancePattern(),
						plan.getName()
					)).getStrings();
					
					//Fetch all changed/unsynchronized local objects
					ArrayList<String> localIDs = planCtrl.synchronizationGetAllUnsynchronizedObjectsIDs(
						line.getSourceRevisionID(), 
						objectName, 
						line.getInstancePattern(), 
						plan.getName()
					);
					
					//First pass :  Run remote IDS
					if (remoteIDs != null ) {
					
    					for (int i = 0; i < remoteIDs.length; i++) {
    						
    						org.apache.log4j.Logger.getLogger(this.getClass()).debug(
    							"executeDifferentialSynchronization() ---------------First Pass Processing Remote " +
    							line.getDestinationRevisionID()+"/"+objectName+": "+remoteIDs[i]+" ["+line.getAlgorithm()+"]"
    						);
    						
    						//check if this object id also changed on the local side
    						if (localIDs.contains(remoteIDs[i])) {
    							//run a full sync
    							synchronizeObjects(
    								plan, 
    								remotePort, 
    								planCtrl,
    								objectCtrl, 
    								line.getAlgorithm(), 
    								line.getSourceRevisionID(), 
    								line.getDestinationRevisionID(), 
    								objectName, 
    								remoteIDs[i],
    								SYNCH_TYPE_FULL
    							);
    						} else {
    							//overwrite the local
    							synchronizeObjects(
    								plan, 
    								remotePort,
    								planCtrl,
    								objectCtrl, 
    								line.getAlgorithm(), 
    								line.getSourceRevisionID(), 
    								line.getDestinationRevisionID(), 
    								objectName, 
    								remoteIDs[i],
    								SYNCH_TYPE_OVERWRITE_LOCAL
    							);
    						}
    						
    						//remove the ID from the list of LocalIDs to process
    						localIDs.remove(remoteIDs[i]);
    						
    					}// for remote IDs
					}
					
					//Second Pass: process the remaining local objects into the remote machine 
					
					for (Iterator<String> iterator3 = localIDs.iterator(); iterator3.hasNext(); ) {
						String localID = iterator3.next();
						
						org.apache.log4j.Logger.getLogger(this.getClass()).debug(
							"executeDifferentialSynchronization() ---------------Second Pass Processing Local " +
							line.getSourceRevisionID()+"/"+objectName+": "+localID+" ["+line.getAlgorithm()+"]"
						);
						
						//By definition remote objects need to be overwritten
						synchronizeObjects(
							plan, 
							remotePort,
							planCtrl,
							objectCtrl, 
							line.getAlgorithm(), 
							line.getSourceRevisionID(), 
							line.getDestinationRevisionID(), 
							objectName, 
							localID,
							SYNCH_TYPE_OVERWRITE_REMOTE
						);
						
					}// for IDS
					
				}//for lines
			}//for objects
        	
        	
        	///////////////
        	// ITEMS
        	///////////////
        	
        	//if we have no line, skip the Items
			if (plan.getItemsSynchronizations() != null) {
        	
            	for (Iterator<SynchronizationPlanItemLine> iterator = plan.getItemsSynchronizations().iterator(); iterator.hasNext(); ) {
    				SynchronizationPlanItemLine line = iterator.next();
    				
    				//reload the plan status to see if STOPPING is required
            		String statusCode = planCtrl.getSynchronizationPlan(revisionID, new SynchronizationPlanPOJOPK(plan.getName())).getCurrentStatusCode();
            		if (SynchronizationPlanPOJO.STATUS_STOPPING.equals(statusCode)) {
            			//stop requested
            			throw new XtentisException("Stop requested by user");
            		}
            		
            		String info = 
            				"Processing Items concepts matching '"+line.getConceptName()+"', instances ids matching '"+line.getIdsPattern()
        					+"  Local/Remote revisions: '"+line.getLocalRevisionID()+"/"+line.getRemoteRevisionID()+"'" 
        					+" ["+line.getAlgorithm()+"]";
            		org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeFullSynchronization() "+info);
            		//check data cluster
            		checkSyncDataCluster(remotePort, line);
            		//update the plan status
            		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_RUNNING);
            		plan.setCurrentStatusMessage(info);
            		planCtrl.putSynchronizationPlan(revisionID, plan);
    				
            		//A cache of already synchronized items
					ArrayList<String> alreadySynchronizedPKs = new ArrayList<String>();
    				
					WSItemPK[] remotePKs = remotePort.synchronizationGetUnsynchronizedItemPKs(new WSSynchronizationGetUnsynchronizedItemPKs(
						line.getRemoteRevisionID(),
						new WSDataClusterPK(line.getRemoteClusterPOJOPK().getUniqueId()),
						line.getConceptName(),
						line.getIdsPattern(),
						plan.getName(),
						0,
						Integer.MAX_VALUE
					)).getWsItemPK();
					
					ArrayList<ItemPOJOPK> localPKs = planCtrl.synchronizationGetAllUnsynchronizedItemPOJOPKs(
						line.getLocalRevisionID(),
						line.getLocalClusterPOJOPK(),
						line.getConceptName(),
						line.getIdsPattern(),
						new SynchronizationPlanPOJOPK(plan.getName()),
						0,
						Integer.MAX_VALUE
					);
					
					
					//First pass :  Update central with remote by fetching all remote items
					
					org.apache.log4j.Logger.getLogger(this.getClass()).debug(
						"executeDifferentialSynchronization() ---------------First Pass Processing Remote " +
						line.getRemoteRevisionID()+" / "+line.getConceptName()+": "+line.getIdsPattern()+" ["+line.getAlgorithm()+"]"
					);
					
					if ((remotePKs != null) && remotePKs.length>0) {
						for (int i = 0; i < remotePKs.length; i++) {
							ItemPOJOPK remotePK = new ItemPOJOPK(
								new DataClusterPOJOPK(remotePKs[i].getWsDataClusterPK().getPk()),
								remotePKs[i].getConceptName(),
								remotePKs[i].getIds()
							);
							
							if (localPKs.contains(remotePK)) {
    							synchronizeItems(
    								plan, 
    								remotePort, 
    								planCtrl, 
    								synchroItemCtrl, 
    								line.getAlgorithm(), 
    								line.getLocalRevisionID(), 
    								line.getRemoteClusterPOJOPK(),
    								line.getRemoteRevisionID(),
    								remotePK, 
    								SYNCH_TYPE_FULL
    							);
    							//remove the PK from the locals
    							localPKs.remove(remotePK);
							} else {
								synchronizeItems(
    								plan, 
    								remotePort, 
    								planCtrl, 
    								synchroItemCtrl, 
    								line.getAlgorithm(), 
    								line.getLocalRevisionID(), 
    								line.getRemoteClusterPOJOPK(),
    								line.getRemoteRevisionID(),
    								remotePK, 
    								SYNCH_TYPE_OVERWRITE_LOCAL
    							);
							}
							
						}
					} else {
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("executeDifferentialSynchronization() No remote items changed");
					}
					
					//Second Pass: Update remote with remaining central items
					
					org.apache.log4j.Logger.getLogger(this.getClass()).debug(
						"executeDifferentialSynchronization() ---------------Second Pass Processing Local " +
						line.getLocalRevisionID()+" / "+line.getConceptName()+": "+line.getIdsPattern()+" ["+line.getAlgorithm()+"]"
					);
					
					for (Iterator<ItemPOJOPK> iterator3 = localPKs.iterator(); iterator3.hasNext(); ) {
						ItemPOJOPK localPK = iterator3.next();
						
						//if already done, skip
						if (alreadySynchronizedPKs.contains(localPK.getUniqueID())) continue;

						//Synchronize the objects
						synchronizeItems(
							plan, 
							remotePort, 
							planCtrl, 
							synchroItemCtrl, 
							line.getAlgorithm(), 
							line.getLocalRevisionID(), 
							line.getRemoteClusterPOJOPK(),
							line.getRemoteRevisionID(),
							localPK, 
							SYNCH_TYPE_OVERWRITE_REMOTE
						);
						
					}// for IDS
    				
    			}//for lines
			} //ITEMS
        	
    		//update the plan status
    		plan.setCurrentStatusCode(SynchronizationPlanPOJO.STATUS_COMPLETED);
    		plan.setCurrentStatusMessage("Completed on "+sdf.format(new Date()));
    		plan.setLastRunStopped(System.currentTimeMillis());
    		planCtrl.putSynchronizationPlan(revisionID, plan);
        	
        	
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to execute the Synchronization Plan "+plan.getPK().toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    	
    }
    
    /**
     * Synchronizes two objects using a particular conflict resolution algorithm
     * @param plan
     * @param remotePort
     * @param planCtrl
     * @param objectCtrl
     * @param algorithm
     * @param localRevisionID
     * @param remoteRevisionID
     * @param objectName
     * @param objectUniqueID
     * @param synchType
     * @throws XtentisException
     */
    private void synchronizeObjects(
		SynchronizationPlanPOJO plan,
		XtentisPort remotePort,
		SynchronizationPlanCtrlLocal planCtrl,
		SynchronizationObjectCtrlLocal objectCtrl,
		String algorithm,
		String localRevisionID,
		String remoteRevisionID,
		String objectName,
		String objectUniqueID,
		int synchType
	) throws XtentisException {
		
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace(
    		"synchronizeObjects() Synchronizing "+localRevisionID+"/"+objectName+": "+objectUniqueID
    	);
    	
		//Check if we have an existing Synchronization for this object
		SynchronizationObjectPOJO synchro = objectCtrl.existsSynchronizationObject(
			new SynchronizationObjectPOJOPK(localRevisionID, objectName, objectUniqueID)
		);
		
		//If this object was never synchronized or all synchronizations completed
		if (synchro == null ) {
			//lock it by creating a Synchronization Object in the Pending state
			synchro = new SynchronizationObjectPOJO();
			synchro.setLastRunPlan(plan.getName());
			synchro.setLocalRevisionID(localRevisionID);
			synchro.setObjectName(objectName);
			synchro.setObjectUniqueID(objectUniqueID);
			synchro.setStatus(SynchronizationObjectPOJO.STATUS_EXECUTED);
		} else {
			//we have an existing synchronization object
			
			if (synchro.getStatus() == SynchronizationObjectPOJO.STATUS_RESOLVED) {
				//re-sync anyway
				
			} else if (synchro.getStatus() == SynchronizationObjectPOJO.STATUS_PENDING) {
				//skip
				org.apache.log4j.Logger.getLogger(this.getClass()).warn(
					"Plan '"+plan.getName()+"': skipped item '"+objectName+"/"+objectUniqueID+"' which was synchronized by plan '"+synchro.getLastRunPlan()+"'"
				);
				return;
			} else if (synchro.getStatus() == SynchronizationObjectPOJO.STATUS_MANUAL) {
				//this should not happen --> force it as EXECUTED
				synchro.setStatus(SynchronizationObjectPOJO.STATUS_EXECUTED);
				synchro.setRemoteIntances(new HashMap<String, SynchronizationRemoteInstance>());
			} else if (synchro.getStatus() == SynchronizationObjectPOJO.STATUS_EXECUTED) {
				//this should not happen -- go for it anyway
				synchro.setRemoteIntances(new HashMap<String, SynchronizationRemoteInstance>());
			}
		}
		
		//save initial synchro status
		String initialLastRunPlan = synchro.getLastRunPlan();
		int initialStatus = synchro.getStatus();
		
		//fetch the xml from the remote system
		String remoteXML = null;
		
		if (synchType != SYNCH_TYPE_OVERWRITE_REMOTE) {
            try {
    	        remoteXML = remotePort.synchronizationGetObjectXML(new WSSynchronizationGetObjectXML(
    	        	remoteRevisionID,
    	        	objectName,
    	        	objectUniqueID
    	        )).getValue();
            } catch (RemoteException e) {
    	        String err = "Plan '"+plan.getName()+"': unable to fetch remote object '"+objectName+"/"+objectUniqueID+"' " +
    	        		"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"'";
    	        org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    			throw new XtentisException(e);
            }
		}
		
		//perform the actual synchro
    	//mark the synchro as pending
    	synchro.setLastRunPlan(plan.getName());
        synchro.setStatus(SynchronizationObjectPOJO.STATUS_PENDING);
        SynchronizationRemoteInstance remoteInstance = new SynchronizationRemoteInstance(
        	plan.getRemoteSystemName(),
        	remoteRevisionID,
        	remoteXML,
        	System.currentTimeMillis()
        );
        synchro.getRemoteIntances().put(remoteInstance.getKey(), remoteInstance);
        String newXML;
        try {

	        objectCtrl.putSynchronizationObject(synchro);

	        //load the xml of the local system
	        String localXML = null;
	        if (synchType != SYNCH_TYPE_OVERWRITE_LOCAL) {
	        	localXML = planCtrl.synchronizationGetMarshaledObject(localRevisionID, objectName, objectUniqueID);
	        }
	        
	        //The "winning" XML
	        String winner=null;
	        
	        //Determine the winning XML depending on the synch type
	        switch (synchType) {
	        	case SYNCH_TYPE_OVERWRITE_LOCAL:
	        		winner = remoteXML;
	        		break;
	        	case SYNCH_TYPE_OVERWRITE_REMOTE:
	        		winner = localXML;
	        		break;
	        	case SYNCH_TYPE_FULL:
	    	        if ((localXML == null) && (remoteXML == null)) {
	    	        	//hummm - should we log an error?
	    	        	String warn = "Plan '"+plan.getName()+"': unable to fully synchronize remote object '"+objectName+"/"+objectUniqueID+"' " +
	        				"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"' " +
	        				"with object in local revision ID '"+localRevisionID+"': no XML found on both ends";
	    	        	org.apache.log4j.Logger.getLogger(this.getClass()).warn(warn);
	    	        } else if (localXML == null) {
	    	        	//The item does not exist on the local system, create it
	    	        	winner = remoteXML;
	    	        } else if (remoteXML == null) {
	    	        	//The item does not exist on the remote system, create it
	    	        	winner = localXML;
	    	        } else {
	    	        	//the object exists locally
	    	        	//since this is a full synch, we try to resolve conflict systematically
	    	        	winner = resolveObjectConflict(algorithm, objectName, objectUniqueID, localXML, remoteXML);
	    	        }
	        		break;
	        	default:
	        		//this should not happen
    	        	String err = "Plan '"+plan.getName()+"': unable to fully synchronize remote object '"+objectName+"/"+objectUniqueID+"' " +
    					"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"' " +
    					"with object in local revision ID '"+localRevisionID+"': no XML found on both ends";
	        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	        		throw new XtentisException(err);
	        }

	        
	        //Unmarshal the winning XML into an object
	        ObjectPOJO pojo = ObjectPOJO.unmarshal((Class<? extends ObjectPOJO>)ObjectPOJO.getObjectClass(objectName), winner);
	        //mark it as synchronized
	        pojo.setLastSynch(plan.getName());
	        newXML = pojo.marshal();
	        synchro.setResolvedXML(newXML);
	        
	        synchro.setStatus(SynchronizationObjectPOJO.STATUS_RESOLVED);
	        
	        try {
	            //write the xml remotely - in case this fails, the synchro will remain as RESOLVED 
	            remotePort.synchronizationPutObjectXML(
	            	new WSSynchronizationPutObjectXML(remoteRevisionID, objectName, objectUniqueID, newXML)
	            ).getValue();
	            //mark the remote instance with the new XML
	            remoteInstance.setXml(newXML);
            } catch (Throwable t) {
            	String err = "Plan '"+plan.getName()+"': unable to fully synchronize remote object '"+objectName+"/"+objectUniqueID+"' " +
					"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"' " +
					"with object in local revision ID '"+localRevisionID+"': the remote system could not be updated";
            	org.apache.log4j.Logger.getLogger(this.getClass()).warn(err);
            	//the synch failed
	            pojo.setLastSynch(null);
	            //mark the remote instance with the remote XML
	            remoteInstance.setXml(remoteXML);
            }
	        
	        //write the XML locally
	        planCtrl.synchronizationPutMarshaledObject(localRevisionID, objectName, objectUniqueID, pojo.marshal());
	        
	        
        } catch (XtentisException e) {
        	throw(e);
        } catch (Exception e) {
        	//log an exception to stop the plan
        	String err = "Plan '"+plan.getName()+"': unable to synchronize remote object '"+objectName+"/"+objectUniqueID+"' " +
    			"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"' with object in local revision ID '"+localRevisionID+"'";
        	org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
        	throw new XtentisException(err);
        } finally {
    		try {
    			//we are done
    			if (synchro.getRemoteIntances().size() ==1) {
    				//if we are the only one, release the Synchronization Object
    				objectCtrl.removeSynchronizationObject(new SynchronizationObjectPOJOPK(synchro.getPK()));
    			} else {
    				//remove this instance
    				synchro.getRemoteIntances().remove(remoteInstance.getKey());
    				//roll back status
    				synchro.setLastRunPlan(initialLastRunPlan);
    				synchro.setStatus(initialStatus);
    				//save
    				objectCtrl.putSynchronizationObject(synchro);
    			}
    		} catch (Throwable t) {
    			//leave the synchro in state RESOLVED
    			objectCtrl.putSynchronizationObject(synchro);
    		}
        }
	}
    
    
    /**
     * Synchronizes two objects using a particular conflict resolution algorithm
     * @param plan
     * @param remotePort
     * @param planCtrl
     * @param synchroItemCtrl
     * @param algorithm
     * @param localRevisionID
     * @param remoteRevisionID
     * @param objectName
     * @param objectUniqueID
     * @param synchType
     * @throws XtentisException
     */
    private void synchronizeItems(
		SynchronizationPlanPOJO plan,
		XtentisPort remotePort,
		SynchronizationPlanCtrlLocal planCtrl,
		SynchronizationItemCtrlLocal synchroItemCtrl,
		String algorithm,
		String localRevisionID,
		DataClusterPOJOPK remoteDataClusterPOJOPK,
		String remoteRevisionID,
		ItemPOJOPK itemPOJOPK,
		int synchType
	) throws XtentisException {
		
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug(
    		"Synchronizing LOCAL item '"+itemPOJOPK.getUniqueID()+" in revision '"+localRevisionID+"'"
    		+" <---> REMOTE item of cluster "+remoteDataClusterPOJOPK.getUniqueId()+" in revision '"+remoteRevisionID+"'"
    		+ " using alog. ["+algorithm+"] and" + 
    		" synchro. type '"+(synchType == SYNCH_TYPE_FULL ? "FULL" : (synchType == SYNCH_TYPE_OVERWRITE_LOCAL ? "OVERWRITE LOCAL": "OVERWRITE REMOTE"))+"'"
    	);
    	
		//if this is not an overwrite of the remote XML, fetch the xml from the remote system
		ItemPOJO remotePOJO = null;
        try {
	        String remoteXML = remotePort.synchronizationGetItemXML(new WSSynchronizationGetItemXML(
	        	remoteRevisionID,
	        	new WSItemPK(
	        		new WSDataClusterPK(remoteDataClusterPOJOPK.getUniqueId()), 
	        			itemPOJOPK.getConceptName(), 
	        			itemPOJOPK.getIds()
	        		)
	        )).getValue();
	        remotePOJO = remoteXML == null ? null : ItemPOJO.parse(remoteXML);
        } catch (RemoteException e) {
	        String err = "Plan '"+plan.getName()+"': unable to fetch remote item '"+itemPOJOPK.getUniqueID()+"' " +
	        		"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"'";
	        org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(e);
        }
		
        ItemPOJO localPOJO = null;
    	String localXML = planCtrl.synchronizationGetMarshaledItem(
    		localRevisionID, 
    		itemPOJOPK
    	);
    	localPOJO = localXML == null ? null : ItemPOJO.parse(localXML);
    	
		//Check if we have an existing Synchronization for this object
		SynchronizationItemPOJO synchro = synchroItemCtrl.existsSynchronizationItem(
			new SynchronizationItemPOJOPK(localRevisionID, itemPOJOPK)
		);
		
		//If this object was never synchronized or all synchronizations completed
		if (synchro == null ) {
			//lock it by creating a Synchronization Object in the Pending state
			synchro = new SynchronizationItemPOJO();
			synchro.setLastRunPlan(plan.getName());
			synchro.setLocalRevisionID(localRevisionID);
			synchro.setItemPOJOPK(itemPOJOPK);
			synchro.setStatus(SynchronizationObjectPOJO.STATUS_EXECUTED);
		} else {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("-------- existing SynchronizationItem");
    		//check if this synchro was already resolved
    		if (synchro.getStatus() == SynchronizationObjectPOJO.STATUS_RESOLVED) {
    			//check if local XML is the same
    			boolean sameLocal = false;
    			if (synchType == SYNCH_TYPE_OVERWRITE_REMOTE) {
    				//this is a differential synch and the local XML changed
    				sameLocal = false;
    			} else if (synchType == SYNCH_TYPE_OVERWRITE_LOCAL) {
    				//this is a differential synch and the local XML is the same
    				sameLocal = true;
    			} else if (synchType == SYNCH_TYPE_FULL) {
    				//this is a full synch, read the state of the local
    				sameLocal = plan.getName().equals(localPOJO.getPlanPK().getUniqueId());
    			}
    			//if local is identical, check if the remote changed
    			if (sameLocal) {
    				//The local XML is the result of the synchro, check if the remote XML is identical
    				SynchronizationRemoteInstance remoteInstance = synchro.getRemoteIntances().get(
    					plan.getRemoteSystemName()+"/"+(remoteRevisionID == null ? "" : remoteRevisionID)
    				);
    				if (remoteInstance != null) {
    					if (remotePOJO == null || remotePOJO.getProjectionAsString().equals(remoteInstance.getXml())) {
    						//remote is the same or null, the synch was already run, automatically or manually: try overwriting the remote
    						synchType = SYNCH_TYPE_OVERWRITE_REMOTE;
    						if (localPOJO == null) {
    							localPOJO = new ItemPOJO(
    								itemPOJOPK.getDataClusterPOJOPK(),
    								itemPOJOPK.getConceptName(),
    								itemPOJOPK.getIds(),
    								System.currentTimeMillis(),
    								synchro.getResolvedProjection()
    							);
    						}
    					}
    				}
    			}
    			//all other cases, keep synch as such
    		} else if (synchro.getStatus() == SynchronizationObjectPOJO.STATUS_PENDING) {
    			//skip
    			org.apache.log4j.Logger.getLogger(this.getClass()).warn(
    				"Plan '"+plan.getName()+"': skipped item '"+itemPOJOPK.getUniqueID()+"' which is being synchronized by plan '"+synchro.getLastRunPlan()+"'"
    			);
    			return;
    		} else if (synchro.getStatus() == SynchronizationObjectPOJO.STATUS_MANUAL) {
    			//the synchro is already waiting manual resolution
    			//add the remote instance to the list to reconcile
    			if (remotePOJO != null) {
    				SynchronizationRemoteInstance remoteInstance = new SynchronizationRemoteInstance(
    					plan.getRemoteSystemName(),
    					remoteRevisionID,
    					remotePOJO.getProjectionAsString(),
    					System.currentTimeMillis()
    				);
    				synchro.getRemoteIntances().put(remoteInstance.getKey(), remoteInstance);
    				//save the snchro object
    				synchroItemCtrl.putSynchronizationItem(synchro);
    				org.apache.log4j.Logger.getLogger(this.getClass()).debug("-------- Adding to list of conflicts");
    			}
    			//done
    			return;
    			
    		} else if (synchro.getStatus() == SynchronizationObjectPOJO.STATUS_EXECUTED) {
    			//this is a new synchro or was executed, but not deleted
    			synchro.setRemoteIntances(new HashMap<String, SynchronizationRemoteInstance>());
    		}
		}//if synchronization Item
		
		//save initial synchro status
		String initialLastRunPlan = synchro.getLastRunPlan();
		int initialStatus = synchro.getStatus();
		
		
		//perform the actual synchro
    	//mark the synchro as pending
    	synchro.setLastRunPlan(plan.getName());
        synchro.setStatus(SynchronizationObjectPOJO.STATUS_PENDING);
        SynchronizationRemoteInstance remoteInstance = new SynchronizationRemoteInstance(
        	plan.getRemoteSystemName(),
        	remoteRevisionID,
        	remotePOJO == null ? null : remotePOJO.getProjectionAsString(),
        	System.currentTimeMillis()
        );
        synchro.getRemoteIntances().put(remoteInstance.getKey(), remoteInstance);
        
        try {
        	//save pending synchronization plan
	        synchroItemCtrl.putSynchronizationItem(synchro);
	        
	        //The "winning" Item
	        ItemPOJO winner=null;
	        
	        //Determine the winning XML depending on the synch type
	        switch (synchType) {
	        	case SYNCH_TYPE_OVERWRITE_LOCAL:
	        		org.apache.log4j.Logger.getLogger(this.getClass()).debug("-------- Synch overwite REMOTE wins");
	        		winner = remotePOJO;
	        		break;
	        	case SYNCH_TYPE_OVERWRITE_REMOTE:
	        		org.apache.log4j.Logger.getLogger(this.getClass()).debug("-------- Synch overwite LOCAL wins");
	        		winner = localPOJO;
	        		break;
	        	case SYNCH_TYPE_FULL:
	    	        if ((localPOJO == null) && (remotePOJO == null)) {
	    	        	//hummm - should we log an error?
	    	        	String warn = "Plan '"+plan.getName()+"': unable to fully synchronize remote item '"+itemPOJOPK.getUniqueID()+"' " +
	        				"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"' " +
	        				"with object in local revision ID '"+localRevisionID+"': no XML found on both ends";
	    	        	org.apache.log4j.Logger.getLogger(this.getClass()).warn(warn);
	    	        } else if (localPOJO == null) {
	    	        	org.apache.log4j.Logger.getLogger(this.getClass()).debug("-------- Synch full, local is null, REMOTE wins");
	    	        	//The item does not exist on the local system, create it
	    	        	winner = remotePOJO;
	    	        } else if (remotePOJO == null) {
	    	        	org.apache.log4j.Logger.getLogger(this.getClass()).debug("-------- Synch full, remote is null, LOCAL wins");
	    	        	//The item does not exist on the remote system, create it
	    	        	winner = localPOJO;
	    	        } else {
	    	        	//the object exists locally
	    	        	//since this is a full synch, we try to resolve conflict systematically
	    	        	winner = resolveItemConflict(
	    	        		plan,
	    	        		algorithm, 
	    	        		localRevisionID, 
	    	        		localPOJO, 
	    	        		plan.getRemoteSystemName(), 
	    	        		remoteRevisionID, 
	    	        		remotePOJO
	    	        	);
	    	        	if (winner == null) {
	    	        		org.apache.log4j.Logger.getLogger(this.getClass()).debug("-------- Synch full, goiong to MANUAL");
	    	        		//could not resolve --> go for manual
	    	        		synchro.setStatus(SynchronizationItemPOJO.STATUS_MANUAL);
	    	        		synchroItemCtrl.putSynchronizationItem(synchro);
	    	        		return;
	    	        	}
	    	        	org.apache.log4j.Logger.getLogger(this.getClass()).debug("-------- Synch full, conflict RESOLVED");
	    	        }
	        		break;
	        	default:
	        		//this should not happen
    	        	String err = "Plan '"+plan.getName()+"': unable to fully synchronize remote item '"+itemPOJOPK.getUniqueID()+"' " +
    					"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"' " +
    					"with item in local revision ID '"+localRevisionID+"': no XML found on both ends";
	        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	        		throw new XtentisException(err);
	        }

	        //we have a winner, update
	        if (winner != null) {
    	        //Set the statuses
    	        winner.setPlanPK(new SynchronizationPlanPOJOPK(plan.getName()));
    	        synchro.setStatus(SynchronizationObjectPOJO.STATUS_RESOLVED);
    	        synchro.setResolvedProjection(winner.getProjectionAsString());
    	        
    	        //write the xml remotely - in case this fails, the synchro will remain as RESOLVED 
    	        try {
    	        	ItemPOJO remoteWinner = winner;
    	        	remoteWinner.setDataClusterPK(remoteDataClusterPOJOPK);
    	        	
    	        	//do check
    	        	boolean isExistBeforeSynchro=false;
    	        	WSString checkGetItemXML=remotePort.synchronizationGetItemXML(new WSSynchronizationGetItemXML(remoteRevisionID,
    	    	            new WSItemPK(
    	    	            		new WSDataClusterPK(itemPOJOPK.getDataClusterPOJOPK().getUniqueId()),
    	    	            		itemPOJOPK.getConceptName(),
    	    	            		itemPOJOPK.getIds()
    	    	            )));
    	    	    if(checkGetItemXML.getValue()==null||checkGetItemXML.getValue().equals("")||checkGetItemXML.getValue().equals("null"))isExistBeforeSynchro=true;
    	    	    
    	            remotePort.synchronizationPutItemXML(
    	            	new WSSynchronizationPutItemXML(remoteRevisionID, remoteWinner.serialize())
    	            );
    	            
    	            //update remote report
    	            String userName="User of "+((Stub)remotePort)._getProperty(Stub.ENDPOINT_ADDRESS_PROPERTY);
    	            String operationType=UpdateReportPOJO.OPERATIONTYPE_UPDATEE;
    	            if(isExistBeforeSynchro)operationType=UpdateReportPOJO.OPERATIONTYPE_CREATE;
        	        ItemPOJO updateReportItem = getUpdateReportItem(remoteWinner,remoteRevisionID, userName,operationType);
    				remotePort.synchronizationPutItemXML(
        	            	new WSSynchronizationPutItemXML(remoteRevisionID, updateReportItem.serialize())
        	            );

    	            remoteInstance.setXml(remoteWinner.getProjectionAsString());
                } catch (Exception e) {
                	String err = "Plan '"+plan.getName()+"': unable to fully synchronize remote item '"+itemPOJOPK.getUniqueID()+"' " +
    					"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"' " +
    					"with item in local revision ID '"+localRevisionID+"': unable to update the remote system";
                	org.apache.log4j.Logger.getLogger(this.getClass()).warn(err);
    	            //the synchro failed
    	            winner.setPlanPK(null);
                }
                
    	        //write the XML locally
                ItemPOJO localWinner = winner;
                localWinner.setDataClusterPK(itemPOJOPK.getDataClusterPOJOPK());
    	        planCtrl.synchronizationPutMarshaledItem(localRevisionID, localWinner.serialize());

    	        //update local report 
    	        String userName="User of http://localhost:8080/talend/TalendPort"; 
    	        String operationType=UpdateReportPOJO.OPERATIONTYPE_UPDATEE;
    	        if(localWinner.load(localRevisionID,itemPOJOPK)==null){
    	        	operationType=UpdateReportPOJO.OPERATIONTYPE_CREATE;
    	        }
    	        ItemPOJO updateReportItem = getUpdateReportItem(localWinner,localRevisionID, userName,operationType);
				updateReportItem.store(localRevisionID);
				
	        }
	        
	        
        } catch (XtentisException e) {
        	throw(e);
        } catch (Exception e) {
        	//log an exception to stop the plan
        	String err = "Plan '"+plan.getName()+"': unable to synchronize remote item '"+itemPOJOPK.getUniqueID()+"' " +
    			"of revision '"+remoteRevisionID+"' from system '"+plan.getRemoteSystemName()+"' with object in local revision ID '"+localRevisionID+"'";
        	org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
        	throw new XtentisException(err);
        } finally {
    		try {
    			//we are done
    			if (synchro.getStatus() == SynchronizationItemPOJO.STATUS_MANUAL) {
    				//just save
    				synchroItemCtrl.putSynchronizationItem(synchro);
    			} else {
    				//we have success or bad failure, remove ourselves
        			if (synchro.getRemoteIntances().size() ==1) {
        				//if we are the only one, release the Synchronization Object
        				synchroItemCtrl.removeSynchronizationItem(new SynchronizationItemPOJOPK(synchro.getPK()));
        			} else {
        				//remove this instance
        				synchro.getRemoteIntances().remove(remoteInstance.getKey());
        				//roll back status and plan
        				synchro.setLastRunPlan(initialLastRunPlan);
        				synchro.setStatus(initialStatus);
        				//save
        				synchroItemCtrl.putSynchronizationItem(synchro);
        			}
    			}
    		} catch (Throwable t) {
    			//leave the synchro in state RESOLVED
    			synchroItemCtrl.putSynchronizationItem(synchro);
    		}
        }
	}

	/**
	 * @param logWinner
	 * @param revison
	 * @param userName
	 * @return
	 */
	private ItemPOJO getUpdateReportItem(ItemPOJO logWinner, String revison,String userName,String operationType) {
		UpdateReportPOJO updateReportPOJO=new UpdateReportPOJO(logWinner.getConceptName(), 
				                                               Util.joinStrings(logWinner.getItemIds(), "."), 
				                                               operationType, 
				                                               UpdateReportPOJO.SOURCE_DATASYNCHRONIZATION, 
				                                               System.currentTimeMillis(),
				                                               logWinner.getDataClusterPOJOPK().getUniqueId(),
				                                               logWinner.getDataModelName(),
				                                               userName,
				                                               revison,
				                                               null);
 	      
		ItemPOJO updateReportItem=new ItemPOJO(new DataClusterPOJOPK("UpdateReport"),"Update",updateReportPOJO.obtainIds(),updateReportPOJO.getTimeInMillis(),updateReportPOJO.serialize());
		return updateReportItem;
	}
    
	
	
	/**
	 * Resolve a conflict between two objects<br/>
	 * By default the local server wins over the remote server<br/>
	 * 
	 * The currently supported algorithms are <ul>
	 * <li>Local Wins</li>
	 * <li>Remote Wins</li>
	 * </ul>
	 * 
	 * TODO: implement algorithms as plugins
	 * @param algorithm
	 * @param objectName
	 * @param uniqueID
	 * @param localXML
	 * @param remoteXML
	 * 
	 * @return
	 * 		The "winning" xml
	 * 
	 * @throws XtentisException
	 */
	private static String resolveObjectConflict(String algorithm, String objectName, String uniqueID, String localXML, String remoteXML) throws XtentisException {
		if ("Remote Wins".equals(algorithm)) return remoteXML;
		return localXML;
	}

	
	/**
	 * Resolve a conflict between two items<br/>
	 * By default the local server wins over the remote server<br/>
	 * 
	 * The currently supported algorithms are <ul>
	 * <li>Manual</li>
	 * <li>Local Wins</li>
	 * <li>Remote Wins</li>
	 * <li>TIS call</li>
	 * </ul>
	 * 
	 * TODO: implement algorithms as plugins
	 * @param algorithm
	 * @param objectName
	 * @param uniqueID
	 * @param localXML
	 * @param remoteXML
	 * 
	 * @return
	 * 		The "winning" xml
	 * 
	 * @throws XtentisException
	 */
	private static ItemPOJO resolveItemConflict(
		SynchronizationPlanPOJO plan,
		String algorithm, 
		String localRevisionID, 
		ItemPOJO localPOJO, 
		String remoteSystemName, 
		String remoteRevisionID,
		ItemPOJO remotePOJO
	) throws XtentisException {
		
		if ("Manual".equals(algorithm)) {
			if (localPOJO==null || remotePOJO == null) return null;
			if (remotePOJO.getProjectionAsString().equals(localPOJO.getProjectionAsString())) {
				return localPOJO;
			}
			return null;
		}
		
		if ("Remote Wins".equals(algorithm)) 
			return remotePOJO;
		
		if ("TIS Call".equals(algorithm))
			return TISCall(
				plan,
				algorithm, 
        		localRevisionID, 
        		localPOJO, 
        		remoteSystemName, 
        		remoteRevisionID, 
        		remotePOJO	
			);
			
		return localPOJO;
	}

	
	private static ItemPOJO TISCall(
		SynchronizationPlanPOJO plan,
		String algorithm, 
		String localRevisionID, 
		ItemPOJO localPOJO, 
		String remoteSystemName, 
		String remoteRevisionID,
		ItemPOJO remotePOJO
	) throws XtentisException {
		
		return null;
	}
    
    /**
     * Build a Web Service Port using the artifacts generated for the servers side
     * @param endpointAddress
     * @param username
     * @param password
     * @return
     * 		The Xtentis port
     * @throws XtentisException
     */
	private static XtentisPort getWSPort(String endpointAddress, String username, String password) throws XtentisException{
		try {
		                
	        QName serviceQname = new QName("urn-com-amalto-xtentis-webservice", "XtentisService");
	        QName portQname = new QName("urn-com-amalto-xtentis-webservice", "XtentisPort");

	        org.apache.log4j.Logger.getLogger(SynchronizationPlanCtrlLocal.class).debug("getWSPort() "+SynchronizationPlanCtrlLocal.class.getResource("/META-INF/wsdl/webservices.wsdl"));
	        URL wsdlURL = SynchronizationPlanCtrlLocal.class.getResource("/META-INF/wsdl/webservices.wsdl");
	        URL mappingURL = SynchronizationPlanCtrlLocal.class.getResource("/META-INF/mapping.xml");
	        
	        ServiceFactory factory = ServiceFactory.newInstance();
	        
	        //This is JBOSS specific. It allows for specifying the mapping.xml file
	        ServiceFactoryImpl jbossFactory = (ServiceFactoryImpl) factory;
	        Service service = jbossFactory.createService(wsdlURL, serviceQname, mappingURL);
	        
	        
	        XtentisPort endpoint = (XtentisPort) service.getPort(portQname, XtentisPort.class);
	        
	        Stub stub = (Stub) endpoint;
	        stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
	        if (username != null) {
	        	stub._setProperty(Stub.USERNAME_PROPERTY, username);
	        	stub._setProperty(Stub.PASSWORD_PROPERTY, password);
	        }
	        
	        return endpoint;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisException(
					"Unable to access endpoint at: "+endpointAddress
					+": "+e.getLocalizedMessage());
		}
	}
	
	
	

	
		
}