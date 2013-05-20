package com.amalto.core.ejb;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.exolab.castor.xml.util.XMLClassDescriptorResolverImpl;
import org.talend.mdm.commmon.util.bean.ItemCacheKey;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.xml.sax.InputSource;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.configurationinfo.ejb.ConfigurationInfoPOJO;
import com.amalto.core.objects.customform.ejb.CustomFormPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingEngineV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerPluginV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.util.BAMLogger;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;

public abstract class ObjectPOJO implements Serializable {

    // Don't change this id, it forces compatibility with pre-5.3 versions (even if structure slightly changed).
    public static final long serialVersionUID = 3157316606545297572l;

    private static Logger LOG = Logger.getLogger(ObjectPOJO.class);

    private static final XMLClassDescriptorResolver cdr = new XMLClassDescriptorResolverImpl();

    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    /**
     * Cache the records to improve performance: this is a <b>READ</b> cache only -> only records read from
     * underlying storage should be cached (don't cache what user provide to {@link #store()} for instance).
     */
    private static Map cachedPojo;

    private static int MAX_CACHE_SIZE = 5000;

    private static Object[][] OBJECT_ROOT_ELEMENT_NAMES =
            new Object[][]{
                    {"Data Cluster", "data-cluster-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Data Model", "data-model-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Role", "role-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Routing Rule", "routing-rule-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Service", "service"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Stored Procedure", "stored-procedure-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Transformer Plugin V2", "transformer-plugin-v2-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Transformer V2", "transformer-v2-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"View", "view-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Menu", "menu-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Background Job", "background-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Configuration Info", "configuration-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Routing Order V2 Active", "active-routing-order-v2-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Routing Order V2 Failed", "failed-routing-order-v2-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Routing Order V2 Completed", "completed-routing-order-v2-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Routing Engine V2", "routing-engine-v2-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Universe", "universe-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Synchronization Plan", "synchronization-plan-pOJO"}, //$NON-NLS-1$ //$NON-NLS-2$
                    {"Synchronization Conflict", "synchronization-item-pOJO"} //$NON-NLS-1$ //$NON-NLS-2$
            };

    public static Object[][] OBJECT_TYPES =
            new Object[][]{
                    {"Data Cluster", DataClusterPOJO.class}, //$NON-NLS-1$
                    {"Data Model", DataModelPOJO.class}, //$NON-NLS-1$
                    {"Role", RolePOJO.class}, //$NON-NLS-1$
                    {"Routing Rule", RoutingRulePOJO.class}, //$NON-NLS-1$
                    {"Service", ServiceBean.class}, //$NON-NLS-1$
                    {"Stored Procedure", StoredProcedurePOJO.class}, //$NON-NLS-1$
                    {"Transformer Plugin V2", TransformerPluginV2POJO.class}, //$NON-NLS-1$
                    {"Transformer V2", TransformerV2POJO.class}, //$NON-NLS-1$
                    {"View", ViewPOJO.class}, //$NON-NLS-1$
                    {"Menu", MenuPOJO.class}, //$NON-NLS-1$
                    {"Background Job", BackgroundJobPOJO.class}, //$NON-NLS-1$
                    {"Configuration Info", ConfigurationInfoPOJO.class}, //$NON-NLS-1$
                    {"Routing Order V2 Active", ActiveRoutingOrderV2POJO.class}, //$NON-NLS-1$
                    {"Routing Order V2 Failed", FailedRoutingOrderV2POJO.class}, //$NON-NLS-1$
                    {"Routing Order V2 Completed", CompletedRoutingOrderV2POJO.class}, //$NON-NLS-1$
                    {"Routing Engine V2", RoutingEngineV2POJO.class}, //$NON-NLS-1$
                    {"Universe", UniversePOJO.class}, //$NON-NLS-1$
                    {"Synchronization Plan", SynchronizationPlanPOJO.class}, //$NON-NLS-1$
                    {"Synchronization Conflict", SynchronizationItemPOJO.class}, //$NON-NLS-1$
                    {"Custom Layout", CustomFormPOJO.class}, //$NON-NLS-1$
                    {"Item", ItemPOJO.class} //$NON-NLS-1$
            };

    private static final Map<Class<?>, String> OBJECTS_CLASSES_TO_NAMES_MAP = new HashMap<Class<?>, String>();

    private static final Map<String, Class<? extends ObjectPOJO>> OBJECTS_NAMES_TO_CLASSES_MAP = new HashMap<String, Class<? extends ObjectPOJO>>();

    private static final Map<String, String> OBJECTS_NAMES_TO_ROOT_NAMES_MAP = new HashMap<String, String>();

    private transient String lastError = "";  //$NON-NLS-1$

    private String lastSynch = null;

    static {
        String max_cache_size = (String) MDMConfiguration.getConfiguration().get("max_cache_size"); //$NON-NLS-1$
        if (max_cache_size != null) {
            MAX_CACHE_SIZE = Integer.valueOf(max_cache_size);
        }
        if (MAX_CACHE_SIZE == 0) {
            cachedPojo = new EmptyMap(); // Disables MDM cache (useful when 2 MDM instance share same database).
        } else {
            cachedPojo = Collections.synchronizedMap(new LRUMap(MAX_CACHE_SIZE));
        }
    }

    public static String getCluster(Class<? extends ObjectPOJO> objectClass) {
        return getCluster(objectClass.getName());
    }

    public static String getCluster(String objectClassName) {
        String[] names = objectClassName.split("\\."); //$NON-NLS-1$
        return "amaltoOBJECTS" + names[names.length - 1].replaceAll("POJO.*", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private static void initObjectsClasses2NamesMap() {
        for (Object[] OBJECT_TYPE : OBJECT_TYPES) {
            String objectName = (String) OBJECT_TYPE[0];
            Class<?> objectClass = (Class<?>) OBJECT_TYPE[1];
            OBJECTS_CLASSES_TO_NAMES_MAP.put(objectClass, objectName);
        }
    }

    public static HashMap<Class<?>, String> getObjectsClasses2NamesMap() {
        if (OBJECTS_CLASSES_TO_NAMES_MAP.size() == 0) {
            initObjectsClasses2NamesMap();
        }
        return new HashMap<Class<?>, String>(OBJECTS_CLASSES_TO_NAMES_MAP);
    }

    public static String getObjectName(Class<?> clazz) {
        if (OBJECTS_CLASSES_TO_NAMES_MAP.size() == 0) {
            initObjectsClasses2NamesMap();
        }
        return OBJECTS_CLASSES_TO_NAMES_MAP.get(clazz);
    }

    private static void initObjectsNames2ClassesMap() {
        for (Object[] OBJECT_TYPE : OBJECT_TYPES) {
            String objectName = (String) OBJECT_TYPE[0];
            Class<? extends ObjectPOJO> objectClass = (Class<? extends ObjectPOJO>) OBJECT_TYPE[1];
            OBJECTS_NAMES_TO_CLASSES_MAP.put(objectName, objectClass);
        }
    }

    public static HashMap<String, Class<?>> getObjectsNames2ClassesMap() {
        if (OBJECTS_NAMES_TO_CLASSES_MAP.size() == 0) {
            initObjectsNames2ClassesMap();
        }
        return new HashMap<String, Class<?>>(OBJECTS_NAMES_TO_CLASSES_MAP);
    }

    public static Class<? extends ObjectPOJO> getObjectClass(String name) throws XtentisException {
        if (OBJECTS_NAMES_TO_CLASSES_MAP.size() == 0) {
            initObjectsNames2ClassesMap();
        }
        try {
            return OBJECTS_NAMES_TO_CLASSES_MAP.get(name);
        } catch (Exception e) {
            String err = "No class found for Object " + name;
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    private static void initObjectsNames2RootNamesMap() {
        for (Object[] OBJECT_ROOT_ELEMENT_NAME : OBJECT_ROOT_ELEMENT_NAMES) {
            String objectName = (String) OBJECT_ROOT_ELEMENT_NAME[0];
            String rootElementName = (String) OBJECT_ROOT_ELEMENT_NAME[1];
            OBJECTS_NAMES_TO_ROOT_NAMES_MAP.put(objectName, rootElementName);
        }
    }

    public static HashMap<String, String> getObjectsNames2RootNamesMap() {
        if (OBJECTS_NAMES_TO_ROOT_NAMES_MAP.size() == 0) {
            initObjectsNames2RootNamesMap();
        }
        return new HashMap<String, String>(OBJECTS_NAMES_TO_ROOT_NAMES_MAP);
    }

    public static String getObjectRootElementName(String name) throws XtentisException {
        if (OBJECTS_NAMES_TO_ROOT_NAMES_MAP.size() == 0) {
            initObjectsNames2RootNamesMap();
        }
        try {
            return OBJECTS_NAMES_TO_ROOT_NAMES_MAP.get(name);
        } catch (Exception e) {
            String err = "No element name found for Object " + name;
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    public ObjectPOJO() {
    }

    /**
     * Returns the last XML Server Error
     * @return the error
     */
    public String getLastError() {
        return lastError;
    }

    /**
     * Set the last XML Server Error
     * @param lastError the error
     */
    protected void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getLastSynch() {
        return lastSynch;
    }

    public void setLastSynch(String lastSynchronizationPlanRun) {
        this.lastSynch = lastSynchronizationPlanRun;
    }

    /**
     * The PK
     * @return the pk, null if undefined
     */
    public abstract ObjectPOJOPK getPK();

    /**
     * Loads an object in the user Universe after checking the user roles
     * @return The loaded Object
     * @throws XtentisException
     */
    public static <T extends ObjectPOJO> T load(Class<T> objectClass, ObjectPOJOPK objectPOJOPK) throws XtentisException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("load() " + objectPOJOPK.getUniqueId()); //$NON-NLS-1$
        }
        try {
            ILocalUser user = LocalUser.getLocalUser();
            //get the universe
            UniversePOJO universe = user.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + user.getUsername() + "'";
                LOG.error(err);
                throw new XtentisException(err);
            }
            // Determine revision ID
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(getObjectName(objectClass));
            if (BAMLogger.log) {
                BAMLogger.log("DATA MANAGER", user.getUsername(), "read", objectClass, objectPOJOPK, true); //$NON-NLS-1$ //$NON-NLS-2$
            }
            T loadedObject = load(revisionID, objectClass, objectPOJOPK);

            // for the user have a role of administration , or role of write on instance or role of read on instance
            if (!user.userCanRead(objectClass, objectPOJOPK.getUniqueId())) {
                String err =
                        "Unauthorized read access by " +
                                "user '" + user.getUsername() + "' on object " + ObjectPOJO.getObjectName(objectClass) + " [" + objectPOJOPK.getUniqueId() + "] ";
                LOG.error(err);
                throw new XtentisException(err);
            }
            return loadedObject;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to load the Object  " + objectPOJOPK.getUniqueId() + " in Cluster " + getCluster(objectClass)
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Loads an object of a particular revision ID<br/>
     * NO Check is done on user rights
     * @return the instance of the object
     * @throws XtentisException
     */
    public static <T extends ObjectPOJO> T load(String revisionID, Class<T> objectClass, ObjectPOJOPK objectPOJOPK) throws XtentisException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("load() " + revisionID + "/" + objectClass + " [" + objectPOJOPK.getUniqueId() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        String url = objectPOJOPK.getUniqueId();
        ItemCacheKey key = new ItemCacheKey(revisionID, url, getCluster(objectClass));
        try {
            //retrieve the item
            String item = (String) cachedPojo.get(key);
            if (item == null) {
                //get the xml server wrapper
                XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
                item = server.getDocumentAsString(revisionID, getCluster(objectClass), url, null);
                //aiming add see 9603 if System Object load fails try to load it from HEAD universe
                if (!(revisionID == null || revisionID.length() == 0) && item == null) {
                    if (XSystemObjects.isExist(url)) {
                        item = server.getDocumentAsString(null, getCluster(objectClass), url, null);
                    }
                }
                if (item != null) {
                    cachedPojo.put(key, item);
                }
            }
            if (item == null) {
                return null;
            }
            return unmarshal(objectClass, item);
        } catch (Exception e) {
            cachedPojo.remove(key); // Don't cache a xml element that failed the unmarshal.
            String err = "Unable to load the Object  " + objectClass.getName() + "[" + objectPOJOPK.getUniqueId() + "] in Cluster " + getCluster(objectClass)
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Remove the item from the DB
     * @return the Primary Key  of the object removed
     * @throws XtentisException
     */
    public static ObjectPOJOPK remove(Class<? extends ObjectPOJO> objectClass, ObjectPOJOPK objectPOJOPK) throws XtentisException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("remove() " + objectPOJOPK.getUniqueId()); //$NON-NLS-1$
        }
        if (objectPOJOPK == null) {
            return null;
        }
        try {
            //for delete we need to be admin, or have a role of admin , or role of write on instance
            boolean authorized = false;
            ILocalUser user = LocalUser.getLocalUser();
            if (user.getUsername() == null) return null;
            if (MDMConfiguration.getAdminUser().equals(user.getUsername())
                    || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { //$NON-NLS-1$ 
                authorized = true;
            } else if (user.isAdmin(objectClass)) {
                authorized = true;
            } else if (user.userCanWrite(objectClass, objectPOJOPK.getUniqueId())) {
                authorized = true;
            }
            if (!authorized) {
                String err =
                        "Unauthorized access on delete for " +
                                "user " + user.getUsername() + " of object " + ObjectPOJO.getObjectName(objectClass) + " [" + objectPOJOPK.getUniqueId() + "] ";
                LOG.error(err);
                throw new XtentisException(err);
            }
            //get the universe
            UniversePOJO universe = user.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + user.getUsername() + "'";
                LOG.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(getObjectsClasses2NamesMap().get(objectClass));
            if (BAMLogger.log) {
                BAMLogger.log("DATA MANAGER", user.getUsername(), "delete", objectClass, objectPOJOPK, authorized); //$NON-NLS-1$ //$NON-NLS-2$
            }
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            //remove the doc
            long res = server.deleteDocument(
                    revisionID,
                    getCluster(objectClass),
                    objectPOJOPK.getUniqueId()
            );
            if (res == -1) {
                return null;
            }
            //remove the cache
            ItemCacheKey key = new ItemCacheKey(revisionID, objectPOJOPK.getUniqueId(), getCluster(objectClass));
            cachedPojo.remove(key);
            return objectPOJOPK;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to remove the Object  " + objectPOJOPK.getUniqueId() + " from Cluster " + getCluster(objectClass)
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Store the current item in the DB
     *
     * @return the pk of the item
     * @throws XtentisException
     */
    public ObjectPOJOPK store() throws XtentisException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("store() " + getPK().getUniqueId()); //$NON-NLS-1$
        }
        try {
            //for storing we need to be admin, or have a role of admin , or role of write on instance
            boolean authorized = false;
            ILocalUser user = LocalUser.getLocalUser();
            if (user.getUsername() == null) {
                return null;
            }
            if (MDMConfiguration.getAdminUser().equals(user.getUsername())
                    || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) {
                authorized = true;
            } else if (user.userCanWrite(this.getClass(), this.getPK().getUniqueId())) {
                authorized = true;
            }
            if (!authorized) {
                String err =
                        "Unauthorized write access by " +
                                "user " + user.getUsername() + " on object " + ObjectPOJO.getObjectName(this.getClass()) + " [" + getPK().getUniqueId() + "] ";
                LOG.error(err);
                throw new XtentisException(err);
            }
            //get the universe and revision ID
            UniversePOJO universe = user.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + user.getUsername() + "'";
                LOG.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(getObjectsClasses2NamesMap().get(this.getClass()));
            if (BAMLogger.log) {
                BAMLogger.log("DATA MANAGER", user.getUsername(), "save", this.getClass(), getPK(), authorized); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return store(revisionID);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to store the Object  " + getPK().getUniqueId() + " in Cluster " + getCluster(this.getClass())
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Performs the actual marshaling and storage of the object<br/>
     * User rights are NOT checked when using this method
     *
     * @return The {@link ObjectPOJOPK} of the stored object
     * @throws XtentisException
     */
    public ObjectPOJOPK store(String revisionID) throws XtentisException {
        if (getPK() == null) {
            setLastError("Unable to store: the object PK is Null");
            return null;
        }

        try {
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            //Clear the synchronization Plan flag - this object is no more Synchronized
            this.lastSynch = null;
            //Marshal
            StringWriter sw = new StringWriter();
            Marshaller.marshal(this, sw);
            //store
            String dataClusterName = getCluster(this.getClass());
            server.start(dataClusterName);
            if (-1 == server.putDocumentFromString(
                    sw.toString(),
                    getPK().getUniqueId(),
                    dataClusterName,
                    revisionID)) {
                setLastError("Unable to store: check The XML Server Wrapper Logs");
                server.rollback(dataClusterName);
                return null;
            }
            server.commit(dataClusterName);

            setLastError(""); //$NON-NLS-1$ 
            // invalidate the cache for entry
            ItemCacheKey key = new ItemCacheKey(revisionID, getPK().getUniqueId(), dataClusterName);
            cachedPojo.remove(key);
            return getPK();
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to store the Object " + this.getClass().getName() + " --> " + getPK().getUniqueId()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    private static Set<String> getSystemObjectIDs(String cluster) {
        if (Util.isEnterprise()) {
            if ("amaltoOBJECTSDataCluster".equals(cluster)) { //$NON-NLS-1$
                Set<String> ret = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER).keySet();
                // ignore Revision MDMItemImages MDMMigration
                ret.remove(XSystemObjects.DC_REVISION.getName());
                ret.remove(XSystemObjects.DC_MDMItemImages.getName());
                ret.remove(XSystemObjects.DC_MDMMigration.getName());
                return ret;
            }
            if ("amaltoOBJECTSMenu".equals(cluster)) { //$NON-NLS-1$
                return XSystemObjects.getXSystemObjects(XObjectType.MENU).keySet();
            }
            if ("amaltoOBJECTSRole".equals(cluster)) { //$NON-NLS-1$
                return XSystemObjects.getXSystemObjects(XObjectType.ROLE).keySet();
            }
            if ("amaltoOBJECTSDataModel".equals(cluster)) { //$NON-NLS-1$
                return XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL).keySet();
            }
        } else {
            if ("amaltoOBJECTSDataCluster".equals(cluster)) { //$NON-NLS-1$
                Set<String> ret = XSystemObjects.getXSystemObjectsTOM(XObjectType.DATA_CLUSTER).keySet();
                // ignore Revision MDMItemImages MDMMigration
                ret.remove(XSystemObjects.DC_REVISION.getName());
                ret.remove(XSystemObjects.DC_MDMItemImages.getName());
                ret.remove(XSystemObjects.DC_MDMMigration.getName());
                return ret;
            }
            if ("amaltoOBJECTSMenu".equals(cluster)) { //$NON-NLS-1$
                return XSystemObjects.getXSystemObjectsTOM(XObjectType.MENU).keySet();
            }
            if ("amaltoOBJECTSRole".equals(cluster)) { //$NON-NLS-1$
                return XSystemObjects.getXSystemObjectsTOM(XObjectType.ROLE).keySet();
            }
            if ("amaltoOBJECTSDataModel".equals(cluster)) { //$NON-NLS-1$
                return XSystemObjects.getXSystemObjectsTOM(XObjectType.DATA_MODEL).keySet();
            }
        }
        return Collections.emptySet();
    }

    /**
     * Retrieve all the PKs - will fetch only the PKs for which the user is authorized
     * @return a Collection of ObjectPOJOPK
     * @throws XtentisException
     */
    public static ArrayList<ObjectPOJOPK> findAllPKs(Class<? extends ObjectPOJO> objectClass, String regex) throws XtentisException {
        try {
            int numItems = 0;

            //check if we are admin
            ILocalUser user = LocalUser.getLocalUser();

            //get the universe and revision ID
            UniversePOJO universe = user.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + user.getUsername() + "'";
                LOG.error(err);
                throw new XtentisException(err);
            }
            String revisionID = universe.getXtentisObjectsRevisionIDs().get(getObjectsClasses2NamesMap().get(objectClass));

            if ("".equals(regex) || "*".equals(regex) || ".*".equals(regex))  {//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                regex = null;
            }

            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();

            String cluster = getCluster(objectClass);
            //retrieve the item
            String[] ids = server.getAllDocumentsUniqueID(revisionID, cluster);
            //see 0013859            
            if (ids == null || ids.length == 0) {
                ids = new String[0];
            }
            //add system default object ids
            Set<String> allId = new HashSet<String>();
            allId.addAll(Arrays.asList(ids));

            ids = allId.toArray(new String[allId.size()]);
            //build PKs collection
            ArrayList<ObjectPOJOPK> list = new ArrayList<ObjectPOJOPK>();
            for (String id : ids) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("findAllPKs() matching " + id); //$NON-NLS-1$
                }
                boolean match = true;
                if (regex != null) {
                    match = id.matches(regex);
                }
                if (match) {
                    if (user.userCanRead(objectClass, id)) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("findAllPKs() Adding PK"); //$NON-NLS-1$
                        }
                        list.add(new ObjectPOJOPK(id));
                        numItems++;
                    }
                }
            }
            if (BAMLogger.log) {
                BAMLogger.log("DATA MANAGER", user.getUsername(), "find all", objectClass, new ObjectPOJOPK(numItems + " Items"), numItems > 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            return list;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to find all the Object identifiers for object " + getObjectName(objectClass) + " using regex " + regex
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }


    /**
     * Retrieve all PKs of an object type unsynchronized against a particular plan<br/>
     * The user must have the "administration" role to perform this task
     *
     * @return a Collection of ObjectPOJOPK
     * @throws XtentisException
     */
    public static ArrayList<String> findAllUnsynchronizedPKs(String objectName,
                                                             String instancePattern,
                                                             String synchronizationPlanName) throws XtentisException {
        try {
            // check if we are admin
            ILocalUser user = LocalUser.getLocalUser();
            if (!user.getRoles().contains("administration")) { //$NON-NLS-1$
                String err = "Only an user with the 'administration' role can call the synchronization methods";
                LOG.error(err);
                throw new XtentisException(err);
            }
            // get the xml server wrapper
            String clusterName = getCluster(getObjectClass(objectName));
            ItemCtrl2Local itemCtrl2Bean = Util.getItemCtrl2Local();
            List<IWhereItem> conditions = new LinkedList<IWhereItem>();
            if (instancePattern != null && !".*".equals(instancePattern)) {
                WhereCondition idCondition = new WhereCondition(objectName + "/i", //$NON-NLS-1$
                        WhereCondition.CONTAINS,
                        instancePattern,
                        WhereCondition.PRE_NONE);
                conditions.add(idCondition);
            }
            if (synchronizationPlanName != null && !synchronizationPlanName.isEmpty()) {
                WhereCondition planCondition = new WhereCondition(objectName + "/last-synch", //$NON-NLS-1$
                        WhereCondition.EQUALS,
                        synchronizationPlanName,
                        WhereCondition.PRE_NOT);
                conditions.add(planCondition);
            }
            IWhereItem whereItem = new WhereAnd(conditions);
            ArrayList<String> elements = new ArrayList<String>();
            elements.add(objectName + "/i"); //$NON-NLS-1$
            return itemCtrl2Bean.xPathsSearch(new DataClusterPOJOPK(clusterName),
                    null,
                    elements,
                    whereItem,
                    -1,
                    0,
                    -1,
                    false);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Error Finding All Unsynchronized PKs" + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Get the records for which the user is authorized and matching certain conditions
     *
     * @param objectClass    The class of the XtentisObject
     * @param idsPaths       The full path (starting with the object element root name) of the ids
     * @param whereItem      The condition
     * @param orderBy        An option full path to order by
     * @param direction      The direction if orderBy is not <code>null</code>. One of
     *                       {@link IXmlServerSLWrapper#ORDER_ASCENDING}, {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param start          The first item index (starts at zero)
     * @param limit          The maximum number of items to return
     * @param withTotalCount If true, return total search count as first result.
     * @return The list of results
     * @throws XtentisException
     */
    public static Collection<ObjectPOJOPK> findPKsByCriteriaWithPaging(Class<? extends ObjectPOJO> objectClass,
                                                                      String[] idsPaths,
                                                                      IWhereItem whereItem,
                                                                      String orderBy,
                                                                      String direction,
                                                                      int start,
                                                                      int limit,
                                                                      boolean withTotalCount) throws XtentisException {
        try {
            // check if we are admin
            ILocalUser user = LocalUser.getLocalUser();
            String userName = user.getUsername();
            boolean isAdmin = false;
            if (MDMConfiguration.getAdminUser().equals(userName) || LocalUser.UNAUTHENTICATED_USER.equals(userName)) {
                isAdmin = true;
            } else if (user.isAdmin(objectClass)) {
                isAdmin = true;
            }
            // get the universe and revision ID
            UniversePOJO universe = user.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '" + userName + "'"; //$NON-NLS-1$ //$NON-NLS-2$
                LOG.error(err);
                throw new XtentisException(err);
            }
            // Get the values from databases
            ItemCtrl2Local itemCtrl = Util.getItemCtrl2Local();
            DataClusterPOJOPK dataCluster = new DataClusterPOJOPK(ObjectPOJO.getCluster(objectClass));
            ArrayList<String> xPaths = new ArrayList<String>(Arrays.asList(idsPaths));
            ArrayList<String> results = itemCtrl.xPathsSearch(dataCluster,
                    null,
                    xPaths,
                    whereItem,
                    -1,
                    orderBy,
                    direction,
                    start,
                    limit,
                    withTotalCount);
            // no result --> we are done
            if (results == null) {
                return Collections.emptyList();
            }
            // Log
            if (LOG.isTraceEnabled()) {
                LOG.trace("findAllPKsByCriteriaWithPaging() Results size: " + results.size()); //$NON-NLS-1$  //$NON-NLS-2$
            }
            List<ObjectPOJOPK> pojoPks = new LinkedList<ObjectPOJOPK>();
            for (String result : results) {
                XMLEventReader reader = xmlInputFactory.createXMLEventReader(new StringReader(result));
                XMLEvent xmlEvent = reader.nextEvent();
                String[] idValues = new String[idsPaths.length];
                int idIndex = 0;
                while (xmlEvent.getEventType() != XMLEvent.END_DOCUMENT) {
                    switch (xmlEvent.getEventType()) {
                        case XMLEvent.CHARACTERS:
                            String data = xmlEvent.asCharacters().getData().trim();
                            if (!data.isEmpty()) {
                                idValues[idIndex++] = data;
                            }
                            break;
                    }
                    xmlEvent = reader.nextEvent();
                }
                // check authorizations
                if (isAdmin || user.userCanRead(objectClass, result)) {
                    pojoPks.add(new ObjectPOJOPK(idValues));
                }
            }
            return pojoPks;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to find all the Object identifiers Using Criteria for object " + getObjectName(objectClass) //$NON-NLS-1$
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage(); //$NON-NLS-1$ //$NON-NLS-2$
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }

    }

    /**
     * Find all Keys of an object for which the user is authorized and matching certain conditions
     *
     * @param objectClass The class of the XtentisObject
     * @param idsPaths    The full path (starting with the object element root name) of the ids
     * @param whereItem   The condition
     * @param orderBy     An option full path to order by
     * @param direction   The direction if orderBy is not <code>null</code>.
     *                    One of {@link IXmlServerSLWrapper#ORDER_ASCENDING}, {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @return An orders
     * @throws XtentisException
     */
    public static Collection<ObjectPOJOPK> findAllPKsByCriteria(
            Class<? extends ObjectPOJO> objectClass,
            String[] idsPaths,
            IWhereItem whereItem,
            String orderBy,
            String direction) throws XtentisException {
        return findPKsByCriteriaWithPaging(objectClass, idsPaths, whereItem, orderBy, direction, 0, Integer.MAX_VALUE, false);
    }

    /**
     * Returns a marshaled version of the object<br/>
     * Identical to calling {@link #marshal()} but does not throw an Exception,
     * returns <code>null</code> instead
     */
    @Override
    public String toString() {
        try {
            return marshal();
        } catch (XtentisException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Returns a marshaled version of the object<br/>
     *
     * @return The marshaled object
     * @throws XtentisException
     */
    public String marshal() throws XtentisException {
        //Marshal
        StringWriter sw = new StringWriter();
        try {
            Marshaller marshaller = new Marshaller(sw);
            marshaller.setResolver(cdr);
            marshaller.setValidation(false);

            marshaller.marshal(this);
        } catch (Throwable t) {
            String err = "Unable to marshal '" + this.getPK().getUniqueId() + "'";
            LOG.error(err, t);
            throw new XtentisException(err, t);
        }
        return sw.toString();
    }

    /**
     * Unmarshals an Object POJO to the Original Object
     *
     * @return The Object instance
     * @throws XtentisException
     */
    public static <T extends ObjectPOJO> T unmarshal(Class<T> objectClass, String marshaledItem) throws XtentisException {
        try {
            if (marshaledItem == null) {
                return null;
            }
            Unmarshaller unmarshaller = new Unmarshaller(objectClass);
            unmarshaller.setResolver(cdr);
            unmarshaller.setValidation(false);
            // see 0023397 can't unmarshaller WSPipeline if unmarshaller.setReuseObjects(true)
            unmarshaller.setReuseObjects(false);
            // Do not remove this line unless you know what you're doing
            unmarshaller.setWhitespacePreserve(true);
            return (T) unmarshaller.unmarshal(new InputSource(new StringReader(marshaledItem)));
        } catch (Throwable t) {
            String err = "Unable to unmarshal the object of class '" + objectClass + "' from \n" + marshaledItem;
            LOG.error(err, t);
            throw new XtentisException(err, t);
        }
    }

    public static void clearCache() {
        cachedPojo.clear();
    }

    public static Map getCache() {
        return cachedPojo;
    }

}
