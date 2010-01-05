package com.amalto.core.objects.backgroundjob.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.XtentisException;



/**
 * @author bgrieder
 * 
 * @ejb.bean name="BackgroundJobCtrl"
 *           display-name="Name for BackgroundJobCtrl"
 *           description="Description for BackgroundJobCtrl"
 *           jndi-name="amalto/remote/core/backgroundjobctrl"
 * 		  local-jndi-name = "amalto/local/core/backgroundjobctrl"
 *           type="Stateless"
 *           view-type="both"
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
public class BackgroundJobCtrlBean implements SessionBean{
  
	public static final long serialVersionUID = 1267454958272L;
	
	
    /**
     * BackgroundJobCtrlBean.java
     * Constructor
     * 
     */
    public BackgroundJobCtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	//context=ctx;
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
     * Creates or updates a BackgroundJob
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJOPK putBackgroundJob(BackgroundJobPOJO backgroundJob) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("putBackgroundJob() "+backgroundJob.getDescription());
    	
        try {
            
            ObjectPOJOPK pk = backgroundJob.store();
            if (pk == null) throw new XtentisException("Unable to create the Background Job. Please check the XML Server logs");
            
            return new BackgroundJobPOJOPK(pk);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Background Job "+backgroundJob.getId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
    
     
    /**
     * Get Background Job
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJO getBackgroundJob(BackgroundJobPOJOPK pk) throws XtentisException{

        try {
        	BackgroundJobPOJO sp =  ObjectPOJO.load(BackgroundJobPOJO.class,pk);
        	if (sp == null) throw new XtentisException("The Background Job id "+pk.getUniqueId()+" does not exist in storage");
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Background Job "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get a BackgroundJob - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJO existsBackgroundJob(BackgroundJobPOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(BackgroundJobPOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Background Job exists:  "+pk.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
    	   return null;
	    }
    }
    

    /**
     * Remove an  Background Job
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJOPK removeBackgroundJob(BackgroundJobPOJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Removing "+pk.getUniqueId());

        try {
        	return new BackgroundJobPOJOPK(ObjectPOJO.remove(BackgroundJobPOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the BackgroundJob "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    
    
    
    
    /**
	 * Retrieve all BackgroundJob PKs
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<BackgroundJobPOJOPK> getBackgroundJobPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(BackgroundJobPOJO.class,regex);
    	ArrayList<BackgroundJobPOJOPK> l = new ArrayList<BackgroundJobPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new BackgroundJobPOJOPK(iter.next()));
		}
    	return l;
    }
    
		
}