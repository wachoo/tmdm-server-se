package com.amalto.core.objects.role.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.XtentisException;



/**
 * @author bgrieder
 * 
 * @ejb.bean name="RoleCtrl"
 *          display-name="Name for RoleCtrl"
 *          description="Description for RoleCtrl"
 *          jndi-name="amalto/remote/core/rolectrl"
 * 		  	local-jndi-name = "amalto/local/core/rolectrl"
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
public class RoleCtrlBean implements SessionBean{
  
	public static final long serialVersionUID = 1267989472;
	
	
    /**
     * RoleCtrlBean.java
     * Constructor
     * 
     */
    public RoleCtrlBean() {
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
     * Creates or updates a Role
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RolePOJOPK putRole(RolePOJO role) throws XtentisException{  
    	return BeanDelegatorContainer.getUniqueInstance().getRoleCtrlBeanDelegator().putRole(role);
    }
    
     
    /**
     * Get Role
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RolePOJO getRole(RolePOJOPK pk) throws XtentisException{

    	return BeanDelegatorContainer.getUniqueInstance().getRoleCtrlBeanDelegator().getRole(pk);
    }
    
    /**
     * Get a Role - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RolePOJO existsRole(RolePOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getRoleCtrlBeanDelegator().existsRole(pk);
    }
    

    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public RolePOJOPK removeRole(RolePOJOPK pk) 
    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getRoleCtrlBeanDelegator().removeRole(pk);
    }    
    
    
    
    /**
	 * Retrieve all Role PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<RolePOJOPK> getRolePKs(String regex) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getRoleCtrlBeanDelegator().getRolePKs(regex);
    }
	
		
}