package com.amalto.core.objects.datacluster.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * @ejb.bean name="DataClusterCtrl"
 *           display-name="Name for DataClusterCtrl"
 *           description="Description for DataClusterCtrl"
 *           jndi-name="amalto/remote/core/dataclusterctrl"
 * 		  local-jndi-name = "amalto/local/core/dataclusterctrl"
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
public class DataClusterCtrlBean implements SessionBean, TimedObject {

    private static final long serialVersionUID = 4567895200L;

    private static final Logger LOGGER = Logger.getLogger(DataClusterCtrlBean.class);

    /**
     * DataClusterCtrlBean.java
     * Constructor
     */
    public DataClusterCtrlBean() {
        super();
    }

    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    /**
     * Create method
     *
     * @ejb.create-method view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    }

    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {
    }

    /**
     * Creates or updates a data cluster
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DataClusterPOJOPK putDataCluster(DataClusterPOJO dataCluster) throws XtentisException {
        try {
            ObjectPOJOPK pk = dataCluster.store();
            if (pk == null) {
                throw new XtentisException("Unable to create the Data Cluster. Please check the XML Server logs");
            }
            //create the actual physical cluster
            try {
                //get the universe and revision ID for Clusters
                UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
                if (universe == null) {
                    String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                    LOGGER.error(err);
                    throw new XtentisException(err);
                }
                String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(DataClusterPOJO.class));
                //get the xml server wrapper
                XmlServerSLWrapperLocal server;
                try {
                    server = Util.getXmlServerCtrlLocal();
                } catch (Exception e) {
                    String err = "Error creating cluster '" + dataCluster.getName() + "' : unable to access the XML Server wrapper";
                    LOGGER.error(err, e);
                    throw new XtentisException(err, e);
                }
                boolean exist = server.existCluster(revisionID, pk.getUniqueId());
                if (!exist) {
                    server.createCluster(revisionID, pk.getUniqueId());
                }
            } catch (Exception e) {
                String err = "Unable to physically create the data cluster " + pk.getUniqueId() + ": " + e.getClass().getName()
                        + ": " + e.getLocalizedMessage();
                try {
                    ObjectPOJO.remove(DataClusterPOJO.class, new ObjectPOJOPK(pk.getUniqueId()));
                } catch (Exception x) {
                    LOGGER.error(x.getMessage(), x);
                }
                throw new XtentisException(err, e);
            }
            return new DataClusterPOJOPK(pk);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to create/update the datacluster " + dataCluster.getName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Get datacluster
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DataClusterPOJO getDataCluster(DataClusterPOJOPK pk) throws XtentisException {
        try {
            if (pk.getUniqueId() == null) {
                throw new XtentisException("The Data Cluster should not be null!");
            }
            if (pk.getUniqueId().endsWith(StorageAdmin.STAGING_SUFFIX)) {
                pk = new DataClusterPOJOPK(StringUtils.substringBeforeLast(pk.getUniqueId(), "#"));
            }
            DataClusterPOJO dataCluster = ObjectPOJO.load(DataClusterPOJO.class, pk);
            if (dataCluster == null) {
                String err = "The Data Cluster " + pk.getUniqueId() + " does not exist.";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            return dataCluster;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the Data Cluster " + pk.toString()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err);
        }
    }

    /**
     * Get a DataCluster - no exception is thrown: returns null if not found
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DataClusterPOJO existsDataCluster(DataClusterPOJOPK pk) throws XtentisException {
        if (pk == null) {
            throw new IllegalArgumentException("Data cluster PK cannot be null.");
        }
        try {
            ILocalUser user = LocalUser.getLocalUser();
            if (user != null && !user.userCanRead(DataClusterPOJO.class, pk.getUniqueId())) {
                String err = "Unauthorized read access by "
                        + "user '" + user.getUsername()
                        + "' on object " + ObjectPOJO.getObjectName(DataClusterPOJO.class)
                        + " [" + pk.getUniqueId() + "] ";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            return ObjectPOJO.load(DataClusterPOJO.class, pk);
        } catch (XtentisException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exist data cluster check exception.", e);
            }
            return null;
        } catch (Exception e) {
            String info = "Could not check whether this Data Cluster \"" + pk.getUniqueId() + "\" exists:  "
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.debug("existsDataCluster() " + info, e);
            return null;
        }
    }

    /**
     * Remove a Data Cluster
     * The physical remove is performed on a separate Thred
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DataClusterPOJOPK removeDataCluster(DataClusterPOJOPK pk) throws XtentisException {
        //remove the actual physical cluster - do it asynchronously
        try {
            String dataClusterName = pk.getUniqueId();
            //get the universe and revision ID for Clusters - this assumes the user is kept across the timeout call...
            UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(DataClusterPOJO.class));
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            server.deleteCluster(revisionID, dataClusterName);
        } catch (Exception e) {
            String err = "Unable to physically delete the data cluster " + pk.getUniqueId() +
                    ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            try {
                ObjectPOJO.remove(DataClusterPOJO.class, new ObjectPOJOPK(pk.getUniqueId()));
            } catch (Exception x) {
                LOGGER.error("Could not remove data cluster object.", x);
            }
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
        return new DataClusterPOJOPK(ObjectPOJO.remove(DataClusterPOJO.class, pk));
    }

    /**
     * Retrieve all DataCluster PKs
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public Collection<DataClusterPOJOPK> getDataClusterPKs(String regex) throws XtentisException {
        Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(DataClusterPOJO.class, regex);
        ArrayList<DataClusterPOJOPK> l = new ArrayList<DataClusterPOJOPK>();
        for (ObjectPOJOPK currentDataCluster : c) {
            l.add(new DataClusterPOJOPK(currentDataCluster));
        }
        return l;
    }

    /**
     * Add this string words to the vocabulary - ignore xml tags
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public int addToVocabulary(DataClusterPOJOPK pk, String string) throws XtentisException {
        return 0;
    }

    /**
     * Spell checks a sentence and return possible spellings
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public Collection<String> spellCheck(DataClusterPOJOPK dcpk, String sentence, int treshold, boolean ignoreNonExistantWords) throws XtentisException {
        return Collections.emptyList();
    }

    public void ejbTimeout(Timer timer) {
    }
}