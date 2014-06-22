// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.objects.customform.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.RoleSpecification;
import com.amalto.core.util.XtentisException;

/**
 * @author achen
 * 
 * @ejb.bean name="CustomFormCtrl" display-name="CustomFormCtrl" description="CustomFormCtrl"
 * jndi-name="amalto/remote/core/customformctrl" local-jndi-name = "amalto/local/core/customformctrl" type="Stateless"
 * view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission view-type = "remote" role-name = "administration"
 * @ejb.permission view-type = "local" unchecked = "true"
 * 
 */
public class CustomFormCtrlBean implements SessionBean {


    /*
     * (non-Javadoc)
     * 
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() throws EJBException, RemoteException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {
        // TODO Auto-generated method stub

    }

    /**
     * Creates or updates a CustomForm
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public CustomFormPOJOPK putCustomForm(CustomFormPOJO customForm) throws XtentisException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("putCustomForm() ");

        try {

            ObjectPOJOPK pk = customForm.store();
            if (pk == null)
                throw new XtentisException("Check the XML Server logs");

            return (CustomFormPOJOPK) pk;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to create/update the CustomForm " + customForm.getPK().getUniqueId() + ": "
                    + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
            throw new XtentisException(err);
        }

    }

    /**
     * Get CustomForm
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public CustomFormPOJO getCustomForm(CustomFormPOJOPK pk) throws XtentisException {

        try {
            CustomFormPOJO sp = ObjectPOJO.load(CustomFormPOJO.class, pk);
            if (sp == null) {
                String err = "The CustomForm " + pk.getUniqueId() + " does not exist.";
                org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
                throw new XtentisException(err);
            }
            return sp;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the CustomForm " + pk.toString() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
            throw new XtentisException(err);
        }
    }

    /**
     * Get CustomForm according to the current user's role
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public CustomFormPOJO getUserCustomForm(CustomFormPOJOPK cpk) throws XtentisException {

        try {            
            List<String> ids= new ArrayList<String>();
            String objectType = "Custom Layout";//$NON-NLS-1$
            ILocalUser user = LocalUser.getLocalUser();
            HashSet<String> roleNames = user.getRoles();
            for (Iterator<String> iter = roleNames.iterator(); iter.hasNext(); ) {
                String roleName = iter.next();
                if ("administration".equals(roleName)||"authenticated".equals(roleName)) continue; //$NON-NLS-1$ //$NON-NLS-2$
                
                //load Role
                RolePOJO role = ObjectPOJO.load(RolePOJO.class, new RolePOJOPK(roleName));
                //get Specifications for the View Object
                RoleSpecification specification = role.getRoleSpecifications().get(objectType);
                if(specification!=null){
                    Set<String> keys=specification.getInstances().keySet();
                    for(String id:keys){
                        ids.add(id);
                    }
                }
            }
            List<CustomFormPOJO> list = new ArrayList<CustomFormPOJO>();
            for (String pk : ids) {
                CustomFormPOJO pojo = ObjectPOJO.load(CustomFormPOJO.class, new ObjectPOJOPK(pk.split("\\.\\.")));//$NON-NLS-1$
                if (pojo!=null && pojo.getDatamodel().equals(cpk.getDatamodel())
                        && pojo.getEntity().equals(cpk.getEntity())) {
                    list.add(pojo);
                }
            }
            if (list.size() > 0) {
                return list.get(0);
            }
            return null;
            // return ObjectPOJO.load(CustomFormPOJO.class, cpk);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the CustomForm associated to the current user's Role: "
                    + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
            throw new XtentisException(err);
        }
    }

    /**
     * Get a CustomForm - no exception is thrown: returns null if not found
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public CustomFormPOJO existsCustomForm(CustomFormPOJOPK pk) throws XtentisException {

        try {
            return ObjectPOJO.load(CustomFormPOJO.class, pk);
        } catch (XtentisException e) {
            return null;
        } catch (Exception e) {
            String info = "Could not check whether this CustomForm exists:  " + pk.getUniqueId() + ": " + e.getClass().getName()
                    + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
            return null;
        }
    }

    /**
     * Remove an item
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public CustomFormPOJOPK removeCustomForm(CustomFormPOJOPK pk) throws XtentisException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("Removing " + pk.getUniqueId());

        try {
            return new CustomFormPOJOPK(ObjectPOJO.remove(CustomFormPOJO.class, pk));
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to remove the CustomForm " + pk.toString() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
            throw new XtentisException(err);
        }
    }

    /**
     * Retrieve all CustomForm PKS
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public Collection<CustomFormPOJOPK> getCustomFormPKs(String regex) throws XtentisException {
        Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(CustomFormPOJO.class, regex);
        ArrayList<CustomFormPOJOPK> l = new ArrayList<CustomFormPOJOPK>();
        for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext();) {
            l.add(new CustomFormPOJOPK(iter.next()));
        }
        return l;
    }

}
