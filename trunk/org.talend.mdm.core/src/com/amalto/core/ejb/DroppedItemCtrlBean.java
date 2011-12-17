package com.amalto.core.ejb;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.util.XtentisException;

/**
 * @author Starkey Shu
 * 
 * @ejb.bean name="DroppedItemCtrl"
 *          display-name="Name for DroppedItemCtrl"
 *          description="Description for DroppedItemCtrl"
 *          jndi-name="amalto/remote/core/droppeditemctrl"
 * 		  	local-jndi-name = "amalto/local/core/droppeditemctrl"
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

@SuppressWarnings("deprecation")
public class DroppedItemCtrlBean implements SessionBean {
	
    /**
     * DroppedItemCtrlBean.java
     * Constructor
     * 
     */
    public DroppedItemCtrlBean() {
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
     * Recover a dropped item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ItemPOJOPK recoverDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("recovering "+droppedItemPOJOPK.getUniquePK());
    	try {
        	return DroppedItemPOJO.recover(droppedItemPOJOPK);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to recover the dropped item "+droppedItemPOJOPK.getUniquePK()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Find all dropped items pks
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public List<DroppedItemPOJOPK> findAllDroppedItemsPKs(String regex) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("finding all dropped items pks ");
    	try {
        	return DroppedItemPOJO.findAllPKs(regex);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to find all dropped items pks  "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Load a dropped item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public DroppedItemPOJO loadDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("loading "+droppedItemPOJOPK.getUniquePK());
    	try {
        	return DroppedItemPOJO.load(droppedItemPOJOPK);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to load the dropped item "+droppedItemPOJOPK.getUniquePK()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Remove a dropped item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public DroppedItemPOJOPK removeDroppedItem(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("removing "+droppedItemPOJOPK.getUniquePK());
    	try {
        	return DroppedItemPOJO.remove(droppedItemPOJOPK);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the dropped item "+droppedItemPOJOPK.getUniquePK()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
	

}