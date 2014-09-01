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
package com.amalto.core.ejb;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.schema.manage.AppinfoSourceHolder;
import com.amalto.core.schema.manage.AppinfoSourceHolderPK;
import com.amalto.core.schema.manage.SchemaCoreAgent;
import org.talend.mdm.server.api.Item;
import org.talend.mdm.server.api.XmlServer;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.Marshaller;
import org.talend.mdm.commmon.util.bean.ItemCacheKey;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemPOJO implements Serializable {

    /**
     * FIXME The newInstance() is deprecated and the newFactory() method should be used instead. However since no
     * changes in behavior are defined by this replacement method, keep deprecated method to ensure there's no
     * class loading issues for now (see TMDM-3604).
     **/
    private static final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

    private static Pattern pLoad = Pattern.compile(".*?(<c>.*?</taskId>|<c>.*?</t>).*?(<p>(.*)</p>|<p/>).*", Pattern.DOTALL); //$NON-NLS-1$

    private static Map cachedPojo;

    public static Logger LOG = Logger.getLogger(ItemPOJO.class);

    private static int MAX_CACHE_SIZE = 5000;

    public static Pattern pathWithoutConditions = Pattern.compile("(.*?)[\\[|/].*");

    private String dataModelName;// used for binding data model

    private String dataModelRevision;// used for binding data model

    private String conceptName;

    private DataClusterPOJOPK dataClusterPOJOPK;

    private SynchronizationPlanPOJOPK planPK;

    private long insertionTime;

    private String[] itemIds;

    private Element projection;

    private String taskId;

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

    public ItemPOJO() {
    }

    public ItemPOJO(DataClusterPOJOPK clusterPK, String concept, String[] ids, long time, Element projection) {
        this.conceptName = concept;
        this.dataClusterPOJOPK = clusterPK;
        this.insertionTime = time;
        this.itemIds = ids;
        this.projection = projection;
        this.planPK = null;
    }

    public ItemPOJO(DataClusterPOJOPK clusterPK, String concept, String[] ids, long time, String projectionAsString) {
        this.conceptName = concept;
        this.dataClusterPOJOPK = clusterPK;
        this.insertionTime = time;
        this.itemIds = ids;
        this.projectionString = projectionAsString;
        this.planPK = null;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    public String getDataModelRevision() {
        return dataModelRevision;
    }

    public void setDataModelRevision(String dataModelRevision) {
        this.dataModelRevision = dataModelRevision;
    }

    /**
     * @return Returns the conceptName.
     */
    public String getConceptName() {
        return conceptName;
    }

    /**
     * @param conceptName The conceptName to set.
     */
    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    /**
     * @return Returns the dataClusterPK.
     */
    public DataClusterPOJOPK getDataClusterPOJOPK() {
        return dataClusterPOJOPK;
    }

    /**
     * @param dataClusterPOJOPK The dataClusterPK to set.
     */
    public void setDataClusterPK(DataClusterPOJOPK dataClusterPOJOPK) {
        this.dataClusterPOJOPK = dataClusterPOJOPK;
    }

    /**
     * @return Returns the insertionTime.
     */
    public long getInsertionTime() {
        return insertionTime;
    }

    /**
     * @param insertionTime The insertionTime to set.
     */
    public void setInsertionTime(long insertionTime) {
        this.insertionTime = insertionTime;
    }

    /**
     * If the item was not changed since last synchronization, this will contain the name of the plan that synchronized
     * it
     * 
     * @return The {@link SynchronizationPlanPOJOPK}
     */
    public SynchronizationPlanPOJOPK getPlanPK() {
        return planPK;
    }

    public void setPlanPK(SynchronizationPlanPOJOPK planPK) {
        this.planPK = planPK;
    }

    /**
     * @return Returns the itemIds.
     */
    public String[] getItemIds() {
        return itemIds;
    }

    /**
     * @param itemIds The itemIds to set.
     */
    public void setItemIds(String[] itemIds) {
        this.itemIds = itemIds;
    }

    private String projectionString = null;

    /**
     * 
     * @return The projection as a String
     * @throws XtentisException
     */
    public String getProjectionAsString() throws XtentisException {
        if (projectionString == null) {
            try {
                projectionString = Util.nodeToString(projection);
            } catch (Exception e) {
                String err = "Unable to serialize the Item " + this.getItemPOJOPK().getUniqueID() + ": "
                        + e.getLocalizedMessage();
                LOG.error(err, e);
                throw new XtentisException(err);
            }
        }
        return projectionString.replaceAll("<\\?xml.*?\\?>", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param str The projection to set.
     */
    public void setProjectionAsString(String str) throws XtentisException {
        this.projectionString = str;
        try {
            if (str != null && str.length() > 0) {
                projection = Util.parse(this.projectionString, null).getDocumentElement();
            }
        } catch (Exception e) {
            String err = "Unable to parse the Item " + this.getItemPOJOPK().getUniqueID() + ". " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * @return Returns the projection.
     */
    public Element getProjection() throws XtentisException {
        if (projection == null) {
            try {
                projection = Util.parse(this.projectionString, null).getDocumentElement();
            } catch (Exception e) {
                String err = "Unable to parse the Item " + this.getItemPOJOPK().getUniqueID() + ". " + e.getClass().getName()
                        + ": " + e.getLocalizedMessage();
                LOG.error(err, e);
                throw new XtentisException(err);
            }
        }
        return projection;
    }

    /**
     * @param projection The projection to set.
     */
    public void setProjection(Element projection) {
        this.projection = projection;
        projectionString = null;
    }

    /**
     * The PK
     * 
     * @return the pk, null if undefined
     */
    public ItemPOJOPK getItemPOJOPK() {
        if (getDataClusterPOJOPK() == null || getConceptName() == null || getItemIds() == null) {
            return null;
        }
        return new ItemPOJOPK(getDataClusterPOJOPK(), getConceptName(), getItemIds());
    }

    /**
     * Loads an Item. User rights are checked.
     * 
     * @return the {@link ItemPOJO}
     * @throws XtentisException
     */
    public static ItemPOJO load(ItemPOJOPK itemPOJOPK) throws XtentisException {
        // Check authorizations
        ILocalUser user = LocalUser.getLocalUser();
        checkAccess(user, itemPOJOPK, false, "read");
        // get the universe and revision ID
        UniversePOJO universe = getNonNullUniverse(user);
        String revisionID = universe.getConceptRevisionID(itemPOJOPK.getConceptName());

        // load the item
        return load(revisionID, itemPOJOPK);

    }

    public static ItemPOJO adminLoad(ItemPOJOPK itemPOJOPK) throws XtentisException {
        ILocalUser user = LocalUser.getLocalUser();
        
        // get the universe and revision ID
        UniversePOJO universe = getNonNullUniverse(user);
        String revisionID = universe.getConceptRevisionID(itemPOJOPK.getConceptName());

        // load the item
        return load(revisionID, itemPOJOPK);
    }

    /**
     * Loads an Item<br/>
     * @return the {@link ItemPOJO}
     */
    public static ItemPOJO load(String revisionID, ItemPOJOPK itemPOJOPK) throws XtentisException {
        return load(revisionID, itemPOJOPK, true);
    }

    /**
     * Loads an Item<br/>
     * @return the {@link ItemPOJO}
     */
    public static ItemPOJO load(String revisionID, ItemPOJOPK itemPOJOPK, boolean checkRights) throws XtentisException {
        XmlServer server = Util.getXmlServerCtrlLocal();

        try {
            // retrieve the item
            String id = itemPOJOPK.getUniqueID();
            ItemCacheKey key = new ItemCacheKey(revisionID, id, itemPOJOPK.getDataClusterPOJOPK().getUniqueId());
            String item = (String) cachedPojo.get(key);
            if (item == null) {
                item = server.getDocumentAsString(revisionID, itemPOJOPK.getDataClusterPOJOPK().getUniqueId(), id);
                // TODO Store in cache in case when there's no inheritance.
            }
            if (item == null) {
                return null;
            }
            ItemPOJO newItem = new ItemPOJO();
            // Build the result
            newItem.setDataClusterPK(itemPOJOPK.getDataClusterPOJOPK());
            newItem.setConceptName(itemPOJOPK.getConceptName());
            newItem.setItemIds(itemPOJOPK.getIds());
            Matcher m = pLoad.matcher(item);
            if (m.matches()) {
                String h = "<header>" + m.group(1) + "</header>"; //$NON-NLS-1$ //$NON-NLS-2$
                Element header = Util.parse(h).getDocumentElement();
                // used for binding data model
                String dm = Util.getFirstTextNode(header, "dmn");//$NON-NLS-1$
                if (dm == null) {
                    dm = Util.getFirstTextNode(header, "dm");//$NON-NLS-1$
                }
                newItem.setDataModelName(dm); //$NON-NLS-1$
                if (Util.getFirstTextNode(header, "dmr") != null) { //$NON-NLS-1$
                    newItem.setDataModelRevision(Util.getFirstTextNode(header, "dmr")); //$NON-NLS-1$
                }
                //if <t> after <taskId> then see 0021697
                String time = null;
                if (!m.group(1).contains("<t>")) { //$NON-NLS-1$
                    Pattern tp = Pattern.compile("<t>(.*?)</t>"); //$NON-NLS-1$
                    Matcher tm = tp.matcher(item);
                    if (tm.find()) {
                        time = tm.group(1);
                    }
                } else {
                    time = Util.getFirstTextNode(header, "t"); //$NON-NLS-1$
                }
                if (time != null) {
                    try {
                        newItem.setInsertionTime(Long.parseLong(time)); //$NON-NLS-1$
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
                String plan = Util.getFirstTextNode(header, "sp"); //$NON-NLS-1$
                if (plan != null) {
                    newItem.setPlanPK(new SynchronizationPlanPOJOPK(plan));
                } else {
                    newItem.setPlanPK(null);
                }
                String taskId = Util.getFirstTextNode(header, "taskId"); //$NON-NLS-1$
                if (taskId != null) {
                    newItem.setTaskId(taskId);
                } else {
                    newItem.setTaskId(""); //$NON-NLS-1$
                }
                if (m.group(2) == null || m.group(2).equals("<p/>")) { //$NON-NLS-1$
                    newItem.setProjectionAsString(""); //$NON-NLS-1$
                } else {
                    newItem.setProjectionAsString(m.group(3));
                }
            } else {
                newItem.setProjectionAsString(item);
            }
            // check user rights
            if (checkRights && newItem.getDataModelName() != null) {
                try {
                    AppinfoSourceHolder appinfoSourceHolder = new AppinfoSourceHolder(new AppinfoSourceHolderPK(
                            newItem.getDataModelName(), newItem.getConceptName()));
                    SchemaCoreAgent.getInstance().analyzeAccessRights(
                            new DataModelID(newItem.getDataModelName(), newItem.getDataModelRevision()),
                            newItem.getConceptName(), appinfoSourceHolder);
                    String itemContentString = newItem.getProjectionAsString();
                    HashSet<String> roles = LocalUser.getLocalUser().getRoles();
                    Document cleanedDocument = SchemaCoreAgent.getInstance().executeHideCheck(itemContentString, roles,
                            appinfoSourceHolder, false);
                    if (cleanedDocument != null) {
                        newItem.setProjectionAsString(Util.nodeToString(cleanedDocument));
                    }
                } catch (Exception e) {
                    String err = "Unable to check user rights of the item " + itemPOJOPK.getUniqueID() + ": "
                            + e.getClass().getName() + ": " + e.getLocalizedMessage();
                    LOG.error(err, e);
                    throw e;
                }
            }
            return newItem;
        } catch (Exception e) {
            String err = "Unable to load the item  " + itemPOJOPK.getUniqueID() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new RuntimeException(err, e);
        }
    }

    /**
     * Removes an item
     * @return The {@link ItemPOJOPK} of the item removed
     */
    public static ItemPOJOPK remove(ItemPOJOPK itemPOJOPK) throws XtentisException {
        if (itemPOJOPK == null) {
            return null;
        }
        ILocalUser user = LocalUser.getLocalUser();
        checkAccess(user, itemPOJOPK, true, "delete"); //$NON-NLS-1$
        // get the universe and revision ID
        UniversePOJO universe = getNonNullUniverse(user);
        String revisionID = universe.getConceptRevisionID(itemPOJOPK.getConceptName());
        XmlServer server = Util.getXmlServerCtrlLocal();
        try {
            // remove the doc
            String clusterName = itemPOJOPK.getDataClusterPOJOPK().getUniqueId();
            server.deleteDocument(revisionID, clusterName, itemPOJOPK.getUniqueID());
            ItemCacheKey key = new ItemCacheKey(revisionID, itemPOJOPK.getUniqueID(), clusterName);
            cachedPojo.remove(key);
            return itemPOJOPK;
        } catch (Exception e) {
            String err = "Unable to remove the item " + itemPOJOPK.getUniqueID() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * drop an item to items-trash
     */
    public static DroppedItemPOJOPK drop(ItemPOJOPK itemPOJOPK, String partPath) throws XtentisException {
        // validate input
        if (itemPOJOPK == null) {
            return null;
        }
        if (partPath == null || partPath.length() == 0) {
            partPath = "/"; //$NON-NLS-1$
        }
        ILocalUser user = LocalUser.getLocalUser();
        checkAccess(user, itemPOJOPK, true, "drop"); //$NON-NLS-1$
        // get the universe and revision ID
        UniversePOJO universe = getNonNullUniverse(user);
        String revisionID = universe.getConceptRevisionID(itemPOJOPK.getConceptName());
        // get XmlServerSLWrapperLocal
        XmlServer server = Util.getXmlServerCtrlLocal();
        try {
            // init MDMItemsTrash Cluster
            if (ObjectPOJO.load(null, DataClusterPOJO.class, new DataClusterPOJOPK("MDMItemsTrash")) == null) { //$NON-NLS-1$
                // create record
                DataClusterPOJO dataCluster = new DataClusterPOJO("MDMItemsTrash", "Holds logical deleted items", null);
                ObjectPOJOPK pk = dataCluster.store(null);
                if (pk == null) {
                    throw new XtentisException("Unable to create the Data Cluster. Please check the XML Server logs");
                }
                // create cluster
                boolean exist = server.existCluster(null, pk.getUniqueId());
                if (!exist) {
                    server.createCluster(null, pk.getUniqueId());
                }
                // log
                LOG.info("Init MDMItemsTrash Cluster");
            }
            String dataClusterName = itemPOJOPK.getDataClusterPOJOPK().getUniqueId();
            String uniqueID = itemPOJOPK.getUniqueID();
            StringBuilder xmlDocument = new StringBuilder();
            Document sourceDoc = null;
            NodeList toDeleteNodeList = null;
            String xml = server.getDocumentAsString(revisionID, dataClusterName, uniqueID, null);
            if (xml == null) {
                return null;
            }
            // get to delete item content
            if (partPath.equals("/")) {  //$NON-NLS-1$
                xmlDocument.append(xml);
            } else {
                String xPath = "/ii/p" + partPath; //$NON-NLS-1$
                sourceDoc = Util.parse(xml);
                toDeleteNodeList = Util.getNodeList(sourceDoc, xPath);
                if (toDeleteNodeList.getLength() == 0) {
                    throw new XtentisException("\nThe target content is not exist or have been deleted already.");
                }
                for (int i = 0; i < toDeleteNodeList.getLength(); i++) {
                    Node node = toDeleteNodeList.item(i);
                    xmlDocument.append(Util.nodeToString(node));
                }
            }
            // make source left doc && validate
            if (!partPath.equals("/")) { //$NON-NLS-1$
                if (toDeleteNodeList != null) {
                    Node lastParentNode;
                    Node formatSiblingNode;
                    for (int i = 0; i < toDeleteNodeList.getLength(); i++) {
                        Node node = toDeleteNodeList.item(i);
                        lastParentNode = node.getParentNode();
                        formatSiblingNode = node.getNextSibling();
                        if (lastParentNode != null) {
                            lastParentNode.removeChild(node);
                        }
                        if (formatSiblingNode != null && formatSiblingNode.getNodeValue() != null
                                && formatSiblingNode.getNodeValue().matches("\\s+")) {
                            lastParentNode.removeChild(formatSiblingNode);
                        }
                    }
                }
                // validate
                String leftSourceDoc = Util.nodeToString(sourceDoc);
                ItemPOJO itemPOJO = parse(leftSourceDoc);
                if (itemPOJO.getDataModelName() != null) {
                    DataModelPOJO dataModelPOJO = ObjectPOJO.load(itemPOJO.getDataModelRevision(), DataModelPOJO.class,
                            new DataModelPOJOPK(itemPOJO.getDataModelName()));
                    if (dataModelPOJO != null) {
                        Element projection;
                        try {
                            projection = itemPOJO.getProjection();
                        } catch (Exception e) {
                            throw new XtentisException("\nThe remaining item can not be empty!");
                        }

                        if (projection != null)
                            Util.validate(itemPOJO.getProjection(), dataModelPOJO.getSchema());
                    }
                }
            }
            // str 2 pojo
            DroppedItemPOJO droppedItemPOJO = new DroppedItemPOJO(revisionID,
                    itemPOJOPK.getDataClusterPOJOPK(),
                    uniqueID,
                    itemPOJOPK.getConceptName(),
                    itemPOJOPK.getIds(),
                    partPath,
                    xmlDocument.toString(),
                    user.getUsername(),
                    System.currentTimeMillis());
            // Marshal
            StringWriter sw = new StringWriter();
            Marshaller.marshal(droppedItemPOJO, sw);
            // copy item content
            server.start("MDMItemsTrash"); //$NON-NLS-1$
            long res = server.putDocumentFromString(sw.toString(), droppedItemPOJO.obtainDroppedItemPK().getUniquePK(),
                    "MDMItemsTrash", null); //$NON-NLS-1$
            server.commit("MDMItemsTrash"); //$NON-NLS-1$
            if (res == -1) {
                server.rollback("MDMItemsTrash"); //$NON-NLS-1$
                return null;
            }
            // delete source item
            try {
                if (partPath.equals("/")) { //$NON-NLS-1$
                    server.deleteDocument(revisionID, dataClusterName, uniqueID);
                } else {
                    String xmlString = Util.nodeToString(sourceDoc);
                    server.start(dataClusterName);
                    server.putDocumentFromString(xmlString, uniqueID, dataClusterName, revisionID);
                    server.commit(dataClusterName);
                }
            } catch (Exception e) {
                server.deleteDocument(null, "MDMItemsTrash", droppedItemPOJO.obtainDroppedItemPK().getUniquePK()); //$NON-NLS-1$
                throw new XtentisException(e);
            } finally {
                // update the cache
                ItemCacheKey key = new ItemCacheKey(revisionID, uniqueID, dataClusterName);
                cachedPojo.remove(key);
            }
            return droppedItemPOJO.obtainDroppedItemPK();
        } catch (SAXException e) {
            String err = "The remaining item did not obey the rules of data model.\nYou can modify the data model, and try it again.\n\n"
                    + e.getLocalizedMessage();
            throw new XtentisException(err);
        } catch (Exception e) {
            String err = "Unable to drop the item " + itemPOJOPK.getUniqueID() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Stores the item in DB. Users rights will be checked
     * 
     * @return The {@link ItemPOJOPK} of the stored item
     * @throws XtentisException
     */
    public ItemPOJOPK store() throws XtentisException {
        return store(true);
    }
    
    public ItemPOJOPK store(boolean putInCache) throws XtentisException {
        ItemPOJOPK itemPK = getItemPOJOPK();
        if (itemPK == null) {
            return null;
        }
        ILocalUser user = LocalUser.getLocalUser();
        checkAccess(user,getItemPOJOPK(), true, "write"); //$NON-NLS-1$
        // get the universe and revision ID
        UniversePOJO universe = getNonNullUniverse(user);    
        String revisionID = universe.getConceptRevisionID(itemPK.getConceptName());
        // used for binding data model
        if (getDataModelName() != null) {
            String objectName = ObjectPOJO.getObjectsClasses2NamesMap().get(DataModelPOJO.class);
            String dataModelRevisionID = universe.getXtentisObjectsRevisionIDs().get(objectName);
            if (dataModelRevisionID != null) {
                this.dataModelRevision = dataModelRevisionID;
            }
        }
        return store(revisionID, putInCache);
    }

    /**
     * Stores the item in DB.<br/>
     * Users rights will NOT be checked
     * 
     * @return The {@link ItemPOJOPK} of the stored item
     * @throws XtentisException
     */
    public ItemPOJOPK store(String revisionID) throws XtentisException {
       return store(revisionID,true); 
    }
    
    /**
     * Stores the item in DB.<br/>
     * Users rights will NOT be checked
     * 
     * @param revisionID Revision id or <code>null</code> for HEAD.
     * @param putInCache <code>true</code> to store object in cache.
     * @return The {@link ItemPOJOPK} of the stored item
     * @throws XtentisException In case of internal exception.
     */
    public ItemPOJOPK store(String revisionID, boolean putInCache) throws XtentisException {
        ItemPOJOPK itemPK = getItemPOJOPK();
        try {
            String xml = serialize();
            if(LOG.isTraceEnabled()) {
                LOG.trace("store() " + itemPK.getUniqueID() + "\n" + xml); //$NON-NLS-1$ //$NON-NLS-2$
            }
            String uniqueId = itemPK.getUniqueID();
            String clusterId = getDataClusterPOJOPK().getUniqueId();
            XmlServer server = Util.getXmlServerCtrlLocal();
            if (-1 == server.putDocumentFromString(xml,uniqueId ,clusterId, revisionID)) {
                return null;
            }
            // invalidate the cache
            if(putInCache) {
                ItemCacheKey key = new ItemCacheKey(revisionID, uniqueId, clusterId);
                cachedPojo.remove(key);
            }
            return itemPK;
        } catch (Exception e) {
            String err = "Unable to store the item " + itemPK.getUniqueID() + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Parses a marshaled item back into an {@link ItemPOJO}
     * @return the {@link ItemPOJO}
     */
    public static ItemPOJO parse(String marshaledItem) throws XtentisException {
        try {
            Pattern p = Pattern.compile(".*?(<c>.*?)(<p>(.*)</p>|<p/>).*", Pattern.DOTALL); //$NON-NLS-1$
            ItemPOJO newItem = new ItemPOJO();
            Matcher m = p.matcher(marshaledItem);
            if (m.matches()) {
                String h = "<header>" + m.group(1) + "</header>"; //$NON-NLS-1$ //$NON-NLS-2$
                Element header = Util.parse(h).getDocumentElement();
                newItem.setConceptName(Util.getFirstTextNode(header, "n")); //$NON-NLS-1$
                // used for binding data model
                if (Util.getFirstTextNode(header, "dmn") != null) { //$NON-NLS-1$
                    newItem.setDataModelName(Util.getFirstTextNode(header, "dmn")); //$NON-NLS-1$
                }
                if (Util.getFirstTextNode(header, "dmr") != null) { //$NON-NLS-1$
                    newItem.setDataModelRevision(Util.getFirstTextNode(header, "dmr")); //$NON-NLS-1$
                }
                newItem.setDataClusterPK(new DataClusterPOJOPK(Util.getFirstTextNode(header, "c"))); //$NON-NLS-1$
                newItem.setItemIds(Util.getTextNodes(header, "i")); //$NON-NLS-1$
                newItem.setInsertionTime(Long.parseLong(Util.getFirstTextNode(header, "t"))); //$NON-NLS-1$
                String plan = Util.getFirstTextNode(header, "sp"); //$NON-NLS-1$
                if (plan != null) {
                    newItem.setPlanPK(new SynchronizationPlanPOJOPK(plan));
                } else {
                    newItem.setPlanPK(null);
                }
                if (m.group(2) == null || m.group(2).equals("<p/>")) { //$NON-NLS-1$
                    newItem.setProjectionAsString(""); //$NON-NLS-1$
                } else {
                    newItem.setProjectionAsString(m.group(3));
                }
                return newItem;
            } else {
                throw new XtentisException("Cannot parse item read from XML Server");
            }
        } catch (Exception e) {
            String err = "Unable to parse the item \n" + marshaledItem;
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Serializes the object to an xml string
     * 
     * @return the xml string
     * 
     * Note: dmn & dmr tags are used for binding data model
     * @throws com.amalto.core.util.XtentisException In case of serialization exception.
     */
    @SuppressWarnings("nls")
    public String serialize() throws XtentisException {
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter streamWriter = null;
        try {
            streamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
            streamWriter.writeStartElement("ii"); //$NON-NLS-1$
            {
                streamWriter.writeStartElement("c"); //$NON-NLS-1$
                streamWriter.writeCharacters(dataClusterPOJOPK.getUniqueId());
                streamWriter.writeEndElement();
                streamWriter.writeStartElement("n"); //$NON-NLS-1$
                streamWriter.writeCharacters(conceptName);
                streamWriter.writeEndElement();
                if (dataModelName != null) {
                    streamWriter.writeStartElement("dmn"); //$NON-NLS-1$
                    streamWriter.writeCharacters(dataModelName);
                    streamWriter.writeEndElement();
                }
                if (dataModelRevision != null) {
                    streamWriter.writeStartElement("dmr"); //$NON-NLS-1$
                    streamWriter.writeCharacters(dataModelRevision);
                    streamWriter.writeEndElement();
                }
                if (planPK != null) {
                    streamWriter.writeStartElement("sp"); //$NON-NLS-1$
                    streamWriter.writeCharacters(planPK.getUniqueId());
                    streamWriter.writeEndElement();
                }
                String[] ids = getItemIds();
                for (String id : ids) {
                    if (id != null) {
                        streamWriter.writeStartElement("i"); //$NON-NLS-1$
                        streamWriter.writeCharacters(id.trim());
                        streamWriter.writeEndElement();
                    }
                }
                streamWriter.writeStartElement("t"); //$NON-NLS-1$
                streamWriter.writeCharacters(String.valueOf(insertionTime));
                streamWriter.writeEndElement();
                if (taskId != null) {
                    streamWriter.writeStartElement("taskId"); //$NON-NLS-1$
                    streamWriter.writeCharacters(taskId);
                    streamWriter.writeEndElement();
                }
                streamWriter.writeStartElement("p");//$NON-NLS-1$
                {
                    streamWriter.writeCharacters(" "); //$NON-NLS-1$
                    streamWriter.flush();
                    String xml = getProjectionAsString();
                    stringWriter.append(xml);
                }
                streamWriter.writeEndElement();
            }
            streamWriter.writeEndElement();
            streamWriter.flush();
        } catch (XMLStreamException e) {
            throw new XtentisException(e);
        } finally {
            try {
                if (streamWriter != null) {
                    streamWriter.close();
                }
            } catch (XMLStreamException e) {
                LOG.error("Error during xml writer close.", e);
            }
        }
        return stringWriter.toString();
    }

    /**
     * Retrieve all {@link ItemPOJOPK}s of items matching a particular concept Pattern and instance pattern, and that
     * are unsynchronized against a particular plan<br/>
     * The user must have the "administration" role to perform this task
     *
     * @return a Collection of ObjectPOJOPK
     * @throws XtentisException
     */
    public static ArrayList<ItemPOJOPK> findAllUnsynchronizedPKs(DataClusterPOJOPK dataClusterPOJOPK,
                                                                 String conceptName,
                                                                 String instancePattern,
                                                                 SynchronizationPlanPOJOPK planPK,
                                                                 int start,
                                                                 int limit) throws XtentisException {
        try {
            // check if we are admin
            ILocalUser user = LocalUser.getLocalUser();
            if (!user.getRoles().contains("administration")) { //$NON-NLS-1$
                String err = "Only an user with the 'administration' role can call the synchronization methods";
                LOG.error(err);
                throw new XtentisException(err);
            }
            // query the xml server wrapper
            Item itemBean = Util.getItemCtrl2Local();
            List<IWhereItem> conditions = new LinkedList<IWhereItem>();
            if (instancePattern != null && !".*".equals(instancePattern)) {
                IWhereItem idCondition = new WhereCondition(conceptName + "/i", //$NON-NLS-1$
                        WhereCondition.CONTAINS,
                        instancePattern,
                        WhereCondition.PRE_NONE);
                conditions.add(idCondition);
            }
            if (planPK != null && planPK.getIds() != null) {
                IWhereItem planCondition = new WhereCondition(conceptName + "/sp", //$NON-NLS-1$
                        WhereCondition.EQUALS,
                        planPK.getUniqueId(),
                        WhereCondition.PRE_NOT);
                conditions.add(planCondition);
            }
            IWhereItem whereItem = new WhereAnd(conditions);
            ArrayList<String> elements = new ArrayList<String>();
            elements.add(conceptName + "/i"); //$NON-NLS-1$
            ArrayList<String> ids = itemBean.xPathsSearch(dataClusterPOJOPK,
                    null,
                    elements,
                    whereItem,
                    -1,
                    start,
                    limit,
                    false);
            ArrayList<ItemPOJOPK> results = new ArrayList<ItemPOJOPK>(ids.size());
            for (String id : ids) {
                results.add(new ItemPOJOPK(dataClusterPOJOPK, conceptName, id.split("."))); //$NON-NLS-1$
            }
            return results;
        } catch (Exception e) {
            String err = "Error Finding All Unsynchronized PKs" + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    @Override
    public String toString() {
        try {
            return serialize();
        } catch (XtentisException e) {
            String err = "Unable to serialize the item: " + e.getMessage();
            LOG.error("ERROR SYSTRACE toString() " + err, e);
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Returns the first part - eg. the concept - from the path
     * @return the Concept
     */
    public static String getConceptFromPath(String path) {
        if (!path.endsWith("/")) {
            path += "/"; //$NON-NLS-1$
        }
        Matcher m = pathWithoutConditions.matcher(path);
        if (m.matches()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    public static void clearCache() {
        cachedPojo.clear();
    }

    public static Map getCache() {
        return cachedPojo;
    }

    private static void checkAccess(ILocalUser user, ItemPOJOPK itemPOJOPK, boolean mutableAccess, String accessLabel) throws XtentisException {
        assert user != null;
        boolean authorizedAccess;
        String username = user.getUsername();
        if(user.isAdmin(ItemPOJO.class)) {
            authorizedAccess = true;
        } else if (MDMConfiguration.getAdminUser().equals(username) || LocalUser.UNAUTHENTICATED_USER.equals(username)) {
            authorizedAccess = true;
        } else if (XSystemObjects.isExist(XObjectType.DATA_CLUSTER, itemPOJOPK.getDataClusterPOJOPK().getUniqueId())) {
            authorizedAccess = true;
        } else {
            ItemPOJO itemPOJO = adminLoad(itemPOJOPK);
            if(mutableAccess) {
                authorizedAccess = user.userItemCanWrite(itemPOJO, itemPOJOPK.getDataClusterPOJOPK().getUniqueId(), itemPOJOPK.getConceptName());
            } else {
                authorizedAccess = user.userItemCanRead(itemPOJO);
            }
        }
        if (!authorizedAccess) {
            String err = "Unauthorized " + accessLabel + " access by " + "user " + username + " on Item '" + itemPOJOPK.getUniqueID() + "'";
            LOG.error(err);
            throw new XtentisException(err);
        }
    }
        
    private static UniversePOJO getNonNullUniverse(ILocalUser user) throws XtentisException {
        // get the universe and revision ID
        UniversePOJO universe = user.getUniverse();
        if (universe == null) {
            String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
            LOG.error(err);
            throw new XtentisException(err);
        }
        return universe;
    }
}