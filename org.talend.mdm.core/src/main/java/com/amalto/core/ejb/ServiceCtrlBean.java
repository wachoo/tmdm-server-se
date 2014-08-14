package com.amalto.core.ejb;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.amalto.core.ejb.local.ServiceLocal;
import com.amalto.core.ejb.local.ServiceLocalHome;
import com.amalto.core.ejb.remote.ServicePK;
import com.amalto.core.ejb.remote.ServiceValue;
import com.amalto.core.util.XtentisException;

/**
 * @ejb.bean name="ServiceCtrl" display-name="Name for ServiceCtrl" description="Description for ServiceCtrl"
 * local-jndi-name = "amalto/local/core/servicectrl" type="Stateless" view-type="local" generate = "false"
 * 
 * @ejb.remote-facade
 * @ejb.permission view-type = "remote" role-name = "administration"
 * @ejb.permission view-type = "local" unchecked = "true"
 * @ejb.ejb-ref ejb-name = "ServiceBean" ref-name = "ejb/ServiceBean" view-type = "local"
 */
public abstract class ServiceCtrlBean implements SessionBean {

    private static final Logger LOGGER = Logger.getLogger(ServiceCtrlBean.class);

    public ServiceCtrlBean() {
    }

    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws javax.ejb.CreateException {
    }

    public void ejbPostCreate() throws javax.ejb.CreateException {
    }

    /**
     * To be Implemented. Returns the unique JNDI name of the service. The JNDI name must be of the type
     * amalto/local/service/[NAME] where [NAME] matchs the pattern "[a-zA-Z][a-zA-Z0-9]*" and is unique accross services
     * 
     * @throws EJBException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public abstract String getJNDIName() throws XtentisException;

    /**
     * To be Implemented. Returns the description of the service. Can be null
     * 
     * @throws EJBException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public abstract String getDescription(String twoLettersLanguageCode) throws XtentisException;

    /**
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String getDocumentation(String twoLettersLanguageCode) throws XtentisException {
        return StringUtils.EMPTY;
    }

    /**
     * To be Implemented. Starts if needed the service Can be null
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public abstract void start() throws XtentisException;

    /**
     * To be Implemented. Stops if needed the service Can be null
     * 
     * @throws EJBException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public abstract void stop() throws XtentisException;

    /**
     * To be Implemented. Returns a status of the service Can be null
     * 
     * @throws EJBException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public abstract String getStatus() throws XtentisException;

    /**
     * To be implemented Runs the service. The object received in an HashMap made of -username - String -password -
     * String -contentType - String -charset - String -bytes - bytes[] -paramameters - HashMap
     * 
     * @throws EJBException
     * @return Serializable - a serializable Object to be passed backed to the connector
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public abstract Serializable receiveFromOutbound(HashMap<String, Serializable> map) throws XtentisException;

    /**
     * To be implemented Runs the service. The item received in an XML String
     * 
     * @param itemPK - the item that triggered a Routing Rule <hich created the Active Routing Order
     * @param routingOrderID - the routing Order ID of the routing rule that called - From 2.19.0, the Routing Order is
     * an ActiveRoutingOrderPOJO
     * @param parameters - the routing rules parameters
     * @return this value is appended at the end of the message field of the Routing Order
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public abstract String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String parameters)
            throws XtentisException;

    /**
     * To be implemented To request and get the response from other applications
     * 
     * @param command - used to call different pull method in service Object
     * @param parameters - incoming parameters, may be in xml format
     * @param schedulePlanID - the ID of schedule plan, if in schedule mode
     * @return Serializable - a serializable Object to be passed backed to the system
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public abstract Serializable fetchFromOutbound(String command, String parameters, String schedulePlanID)
            throws XtentisException;

    /**
     * Configuration received from outbound, typically a portlet The default implementation stores the configuration
     * string "as is"
     * 
     * @throws EJBException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public void putConfiguration(String configuration) throws XtentisException {
        storeConfiguration(configuration);
    }

    /**
     * Returns the XML schema for the configuration<br>
     * Can be null
     * 
     * @throws XtentisException
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String getConfigurationSchema() throws XtentisException {
        return null;
    }

    /**
     * return default the configuration<br>
     * Can be null
     * 
     * @throws XtentisException
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String getDefaultConfiguration() throws XtentisException {
        return null;
    }

    /**
     * Retrieves the configuration The default implementation renders the configuration string "as stored" and ignore
     * the optional parameter
     * 
     * @throws EJBException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String getConfiguration(String optionalParameter) throws XtentisException {
        return loadConfiguration();
    }

    /**
     * Saves the service data for this service The configuration can be an xml or a serialized object converted to a
     * Base64 String
     * 
     * @throws EJBException
     */
    public void storeConfiguration(String configuration) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("storeConfiguration() " + getServiceName());
        }
        try {
            ServiceLocalHome home = getServiceLocalHome();
            ServiceLocal service = home.findIfExists(new ServicePK(getJNDIName().replaceAll("/", "_").replaceAll(":", "_"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            if (service == null) {
                // attempt to create ie
                home.create(new ServiceValue(
                        getJNDIName().replaceAll("/", "_").replaceAll(":", "_"), configuration, StringUtils.EMPTY)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            } else {
                service.setConfiguration(configuration);
            }
        } catch (XtentisException e) {
            throw e;
        } catch (Exception e) {
            String err = "Unable to save the configuration of service " + getServiceName() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Configuration received from outbound, typically a portlet The default implementation stores the configuration
     * string "as is"
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public void putServiceData(String serviceData) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("putServiceData() to" + getServiceName() + ": " + serviceData);
        }
        storeServiceData(serviceData);
    }

    /**
     * Saves the service data for this service The configuration can be an xml or a serialized object converted to a
     * Base64 String The serviceData are typically received from a service
     * 
     * @throws EJBException
     * 
     */
    public void storeServiceData(String serviceData) throws XtentisException {
        try {
            ServiceLocalHome home = getServiceLocalHome();
            ServiceLocal service = home.findIfExists(new ServicePK(getJNDIName().replaceAll("/", "_").replaceAll(":", "_"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            if (service == null) {
                // attempt to create
                home.create(new ServiceValue(getJNDIName(), StringUtils.EMPTY, serviceData));
            } else {
                service.setServiceData(serviceData);
            }
        } catch (XtentisException e) {
            throw e;
        } catch (Exception e) {
            String err = "Unable to save the service data of service " + getServiceName() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Loads the configuration from the xml server
     * 
     * @throws EJBException
     * 
     */
    public String loadConfiguration() throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loadConfiguration() " + getServiceName());
        }
        try {
            ServiceLocalHome home = getServiceLocalHome();
            ServiceLocal service = home.findIfExists(new ServicePK(getJNDIName().replaceAll("/", "_").replaceAll(":", "_"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            if (service == null) {
                return null;
            } else {
                return service.getConfiguration();
            }
        } catch (XtentisException xe) {
            throw xe;
        } catch (Exception e) {
            String err = "Unable to load the configuration of service " + getServiceName() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }

    }

    private String getServiceName() throws XtentisException {
        String[] paths = getJNDIName().split("/"); //$NON-NLS-1$
        return paths[paths.length - 1];
    }

    /**
     * Helper class to retrieve the service local home
     * 
     * @return The {@link ServiceLocalHome}
     * @throws NamingException
     */
    protected ServiceLocalHome getServiceLocalHome() throws NamingException {
        return (ServiceLocalHome) new InitialContext().lookup(ServiceLocalHome.JNDI_NAME);
    }

    /********************************************************************************************
     * 
     * EJB Session beans getters
     * 
     ********************************************************************************************/

    protected Connection getConnection(String JNDIName) throws XtentisException {
        try {
            return ((ConnectionFactory) (new InitialContext()).lookup(JNDIName)).getConnection();
        } catch (Exception e) {
            String err = "JNDI lookup error: " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }
}
