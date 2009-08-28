package com.amalto.core.objects.universe.ejb;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="UniverseCtrl"
 *          display-name="Name for UniverseCtrl"
 *          description="Description for UniverseCtrl"
 *          jndi-name="amalto/remote/core/universectrl"
 * 		  	local-jndi-name = "amalto/local/core/universectrl"
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
public class UniverseCtrlBean implements SessionBean{
  
	public static final long serialVersionUID = 1L;
	
	private  RevisionPOJO revisePojo = new RevisionPOJO();

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
     * Creates or updates a Universe
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public UniversePOJOPK putUniverse(UniversePOJO universe) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putUniverse() "+universe.getName());
    	
        try {
            
        	if ("[HEAD]".equals(universe.getName())) {
        		String err = "[HEAD] is a reserved name for Universes";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
            
            if (universe.store() == null)
            	throw new XtentisException("Check the XML Server logs");
            
            revisePojo.load(universe.getPK().getUniqueId(), universe, false);
            universe = revisePojo.addMetaDataIntoUniverse(universe);
            
            ObjectPOJOPK pk = universe.store();
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new UniversePOJOPK(pk);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Universe "+universe.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
     
    /**
     * Get Universe
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public UniversePOJO getUniverse(UniversePOJOPK pk) throws XtentisException{

        try {
        	UniversePOJO sp =  ObjectPOJO.load(UniversePOJO.class,pk);
        	if (sp == null) {
        		String err= "The Universe "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Universe "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get a Universe - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public UniversePOJO existsUniverse(UniversePOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(UniversePOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Universe exists:  "+pk.getUniqueId()
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
    public UniversePOJOPK removeUniverse(UniversePOJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("Removing "+pk.getUniqueId());

        try {
        	ObjectPOJOPK universePk = ObjectPOJO.remove(UniversePOJO.class,pk);
            revisePojo.load(pk.getUniqueId(), null, true);
        	return new UniversePOJOPK(universePk);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Universe "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    
    
    /**
     * getAllCreatedRevisions
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public Collection<RevisionItem> getAllCreatedRevisions(UniversePOJOPK pk){
    	return revisePojo.getAllCreatedRevisions(pk);
    }
    
    /**
     * getAllQuotedRevisions
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public Collection<RevisionItem> getAllQuotedRevisions(UniversePOJOPK pk) {
    	return revisePojo.getAllQuotedRevisions(pk);
    }
    
    /**
     * getUniverseCreator
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public UniversePOJOPK getUniverseCreator(RevisionPOJOPK pk){
    	return revisePojo.getUniverseCreator(pk);
    }
    
    /**
     * getUniverseQuoter
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public Collection<UniversePOJOPK> getUniverseQuoter(RevisionPOJOPK pk) {
    	return revisePojo.getUniverseQuoter(pk);
    }
    /**
	 * Retrieve all Universe PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<UniversePOJOPK> getUniversePKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(UniversePOJO.class,regex);
    	ArrayList<UniversePOJOPK> l = new ArrayList<UniversePOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
    		UniversePOJOPK pojoPk = new UniversePOJOPK(iter.next());
    		revisePojo.load(pojoPk.getUniqueId(), null, false);
			l.add(pojoPk);
		}
    	return l;
    }
	
		
}