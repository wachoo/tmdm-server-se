package com.amalto.core.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.RemoveException;

import com.amalto.core.metadata.LongString;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.remote.ServicePK;
import com.amalto.core.ejb.remote.ServiceValue;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


/**
 * @ejb.bean 	name="Service"
 *           	display-name="Service"
 *           	description="Service"
 *           	jndi-name="amalto/remote/core/service"
 * 		  		local-jndi-name = "amalto/local/core/service"
 *           	type="BMP"
 *           	view-type="local"
 *           	reentrant="true"
 * @ejb.value-object
 * @ejb.pk
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 */
public abstract class ServiceBean implements EntityBean {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(ServiceBean.class);

    private static final String CLUSTER = "amaltoOBJECTSservices"; //$NON-NLS-1$

    EntityContext context;

    public ServiceBean() {
        super();
    }

    public void setEntityContext(EntityContext ctx) throws EJBException, RemoteException {
        context = ctx;
    }

    public void unsetEntityContext() throws EJBException, RemoteException {
        context = null;
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    /**
     * @ejb.create-method view-type = "local"
     */
    public ServicePK ejbCreate(ServiceValue vo) throws javax.ejb.CreateException {
        try {
            // get the universe and revision ID
            UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
            // get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            setServiceName(vo.getServiceName());
            setConfiguration(vo.getConfiguration());
            setServiceData(vo.getServiceData());
            //create the doc
            server.start(CLUSTER);
            long res = server.putDocumentFromString(
                    serialize(),
                    getServiceName(),
                    CLUSTER,
                    revisionID);
            if (res == -1) {
                server.rollback(CLUSTER);
                throw new CreateException("Check the XML Server Wrapper logs");
            } else {
                server.commit(CLUSTER);
            }
            return vo.getPrimaryKey();
        } catch (XtentisException e) {
            throw new CreateException(e.getMessage());
        } catch (Exception e) {
            String err = "Unable to create the service: "
                    + vo.getServiceName()
                    + ": " + e.getClass().getName()
                    + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new CreateException(err);
        }
    }

    /**
     * Post Create method
     */
    public void ejbPostCreate(ServiceValue vo) throws javax.ejb.CreateException {
    }

    public void ejbLoad() throws EJBException, RemoteException {
        ServicePK pk = (ServicePK) context.getPrimaryKey();
        try {
            // get the universe and revision ID
            UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            //retrieve the Service
            String Service = server.getDocumentAsString(revisionID, CLUSTER, pk.getServiceName());
            if (Service == null) {
                throw new EJBException("Service not found: " + pk.getServiceName() + " in cluster " + CLUSTER);
            }
            //parse the results
            Document d = Util.parse(Service);
            //Build the result
            Element root = d.getDocumentElement();
            setServiceName(pk.getServiceName());
            if (MDMConfiguration.isSqlDataBase()) {
                setConfiguration(Util.getFirstTextNode(root, "configuration")); //$NON-NLS-1$
                setServiceData(Util.getFirstTextNode(root, "service-data")); //$NON-NLS-1$
            } else {
                setConfiguration(Util.getFirstTextNode(root, "configuration")); //$NON-NLS-1$
                setServiceData(Util.getFirstTextNode(root, "servicedata")); //$NON-NLS-1$
            }
        } catch (XtentisException e) {
            throw new EJBException(e.getMessage(), e);
        } catch (Exception e) {
            String err = "Unable to load the service: " + getServiceName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new EJBException(err, e);
        }
    }

    public void ejbRemove() throws RemoveException, EJBException, RemoteException {
        ServicePK pk = getServiceValue().getPrimaryKey();
        try {
            //get the universe and revision ID
            UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            //remove the doc
            long res = server.deleteDocument(revisionID, CLUSTER, pk.getServiceName());
            if (res == -1) {
                throw new EJBException("Check the Xml Server Wrapper logs");
            }
        } catch (XtentisException e) {
            throw new RemoveException(e.getMessage());
        } catch (Exception e) {
            String err = "Unable to remove the service " + getServiceName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new RemoveException(err);
        }
    }

    public void ejbStore() throws EJBException, RemoteException {
        try {
            //get the universe and revision ID
            UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            //store
            server.start(CLUSTER);
            if (-1 == server.putDocumentFromString(
                    serialize(),
                    getServiceName(),
                    CLUSTER,
                    revisionID)) {
                throw new EJBException("Check the Xml Sever Wrapper logs");
            }
            server.commit(CLUSTER);
        } catch (XtentisException e) {
            throw new EJBException(e.getMessage(), e);
        } catch (Exception e) {
            String err = "Unable to store the service " + getServiceName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new EJBException(err, e);
        }

    }

    public ServicePK ejbFindByPrimaryKey(ServicePK primaryKey) throws FinderException {
        try {
            //get the universe and revision ID
            UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            server.createCluster(revisionID, CLUSTER);
            //attempt to retrieve the Service
            String document = server.getDocumentAsString(
                    revisionID,
                    CLUSTER,
                    primaryKey.getServiceName());
            if (document == null) {
                throw new ObjectNotFoundException("The service '" + primaryKey.getServiceName() + "' does not exist" +
                                            (revisionID == null ? "" : " for revision " + revisionID));
            }
            return primaryKey;
        } catch (XtentisException e) {
            throw new FinderException(e.getMessage());
        } catch (Exception e) {
            String err = "Unable to fetch the Service " + primaryKey.getServiceName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new FinderException(err);
        }
    }

    public ServicePK ejbFindIfExists(ServicePK primaryKey) throws FinderException {
        try {
            //get the universe and revision ID
            UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            server.createCluster(revisionID, CLUSTER);
            //attempt to retrieve the Service
            String document = server.getDocumentAsString(revisionID, CLUSTER, primaryKey.getServiceName());
            return document == null ? null : primaryKey;
        } catch (XtentisException e) {
            return null;
        } catch (Exception e) {
            String debug = "This service does not exist: " + primaryKey.getServiceName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.debug("ejbFindIfExists() " + debug, e);
            return null;
        }
    }

    public Collection<ServicePK> ejbFindAll() throws FinderException {
        try {
            List<ServicePK> l = new ArrayList<ServicePK>();
            //get the universe and revision ID
            UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(ServiceBean.class));
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            server.createCluster(revisionID, CLUSTER);
            //retrieve all the documents
            String[] uris = server.getAllDocumentsUniqueID(revisionID, CLUSTER);
            if (uris != null) {
                for (String uri : uris) {
                    l.add(new ServicePK(uri));
                }
            }
            return l;
        } catch (XtentisException e) {
            throw new FinderException(e.getMessage());
        } catch (Exception e) {
            String err = "Unable to retrieve all the services"
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new FinderException(err);
        }
    }

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.value-object
     * @ejb.persistence
     * @ejb.pk-field
     */
    @LongString
    public abstract String getServiceName();

    public abstract void setServiceName(String name);

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.value-object
     * @ejb.persistence
     */
    @LongString
    public abstract String getConfiguration();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setConfiguration(String configuration);

    /**
     * Any Data hat is not configuration
     *
     * @ejb.interface-method view-type="local"
     * @ejb.value-object
     * @ejb.persistence
     */
    @LongString
    public abstract String getServiceData();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setServiceData(String serviceData);

    /**
     * @ejb.interface-method view-type="local"
     */
    @LongString
    public abstract ServiceValue getServiceValue();

    /**
     * Serializes the object to an xml string
     *
     * @return the xml string
     */
    private String serialize() throws XtentisException {
        String service;
        if (MDMConfiguration.isSqlDataBase()) {
            service = "<service>" +  //$NON-NLS-1$
                    "	<service-name><![CDATA[" + getServiceName() + "]]></service-name>" +   //$NON-NLS-1$ //$NON-NLS-2$
                    "	<configuration><![CDATA[" + (getConfiguration() == null ? "" : getConfiguration()) + "]]></configuration>" + //$NON-NLS-1$ //$NON-NLS-2$
                    "	<service-data><![CDATA[" + (getServiceData() == null ? "" : getServiceData()) + "]]></service-data>" + //$NON-NLS-1$ //$NON-NLS-2$
                    "</service>"; //$NON-NLS-1$

        } else {
            service = "<service>" + //$NON-NLS-1$
                    "	<name><![CDATA[" + getServiceName() + "]]></name>" + //$NON-NLS-1$ //$NON-NLS-2$
                    "	<configuration><![CDATA[" + (getConfiguration() == null ? "" : getConfiguration()) + "]]></configuration>" + //$NON-NLS-1$ //$NON-NLS-2$
                    "	<servicedata><![CDATA[" + (getServiceData() == null ? "" : getServiceData()) + "]]></servicedata>" + //$NON-NLS-1$ //$NON-NLS-2$
                    "</service>"; //$NON-NLS-1$
        }
        return service;
    }


}
