package com.amalto.core.objects.universe.ejb;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocal;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlUtil;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.WSGetUniverseByRevisionType;
import com.amalto.core.webservice.WSUniversePK;
import com.amalto.core.webservice.WSUniversePKArray;



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
    	
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().putUniverse(universe);
    }
    /**
     * Creates or updates a Universe
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public WSUniversePKArray getUniverseByRevision(String  name,String revision, String type) throws XtentisException{

    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().getUniverseByRevision(name, revision, type);

    }
    /**
     * Get Universe
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public UniversePOJO getUniverse(UniversePOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().getUniverse(pk);
    }
    
    /**
     * Get a Universe - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public UniversePOJO existsUniverse(UniversePOJOPK pk)    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().existsUniverse(pk);
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
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().removeUniverse(pk);
    }    
    
    /**
     * getAllCreatedRevisions
     * @throws XtentisException 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public Collection<RevisionItem> getAllCreatedRevisions(UniversePOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().getAllCreatedRevisions(pk);
    }
    
    /**
     * getAllQuotedRevisions
     * @throws XtentisException 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public Collection<RevisionItem> getAllQuotedRevisions(UniversePOJOPK pk) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().getAllQuotedRevisions(pk);
    }
    
    /**
     * getUniverseCreator
     * @throws XtentisException 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public UniversePOJOPK getUniverseCreator(RevisionPOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().getUniverseCreator(pk);
    }
    
    /**
     * getUniverseQuoter
     * @throws XtentisException 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public Collection<UniversePOJOPK> getUniverseQuoter(RevisionPOJOPK pk) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().getUniverseQuoter(pk);
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
    	return BeanDelegatorContainer.getUniqueInstance().getUniverseCtrlBeanDelegator().getUniversePKs(regex);
    }
	
		
}