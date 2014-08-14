package com.amalto.core.objects.view.ejb;

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
 * @author Bruno Grieder
 * 
 * @ejb.bean name="ViewCtrl"
 *           display-name="Name for ViewCtrl"
 *           description="Description for ViewCtrl"
 *           jndi-name="amalto/remote/core/viewctrl"
 * 		  local-jndi-name = "amalto/local/core/viewctrl"
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
public class ViewCtrlBean implements SessionBean{
  
	public static final long serialVersionUID = 1264929387;
	
	
    /**
     * ViewCtrlBean.java
     * Constructor
     * 
     */
    public ViewCtrlBean() {
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
     * Creates or updates a View
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ViewPOJOPK putView(ViewPOJO view) throws XtentisException{  
        try {
            ObjectPOJOPK pk = view.store();
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new ViewPOJOPK(pk);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the View "+view.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
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
    public ViewPOJO getView(ViewPOJOPK pk) throws XtentisException{

        try {
        	ViewPOJO sp =  ObjectPOJO.load(ViewPOJO.class,pk);
        	if (sp == null) {
        		String err= "The View "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the View "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get a View - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ViewPOJO existsView(ViewPOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(ViewPOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this View exists:  "+pk.getUniqueId()
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
    public ViewPOJOPK removeView(ViewPOJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Removing "+pk.getUniqueId());

        try {
        	return new ViewPOJOPK(ObjectPOJO.remove(ViewPOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the View "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }    
    
    
    
    /**
	 * Retrieve all View PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<ViewPOJOPK> getViewPKs(String regex) throws XtentisException {
    	ArrayList<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(ViewPOJO.class,regex);
    	ArrayList<ViewPOJOPK> l = new ArrayList<ViewPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new ViewPOJOPK(iter.next()));
		}
    	return l;
    }

    /**
	 * Retrieve all Views 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public ArrayList<ViewPOJO> getAllViews(String regex) throws XtentisException {
    	ArrayList<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(ViewPOJO.class,regex);
    	ArrayList<ViewPOJO> l = new ArrayList<ViewPOJO>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
    		ViewPOJO pojo = ObjectPOJO.load(ViewPOJO.class, iter.next());
			l.add(pojo);
		}
    	return l;
    }

		
}