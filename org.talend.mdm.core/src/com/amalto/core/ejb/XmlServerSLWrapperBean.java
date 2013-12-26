// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.server.XmlServer;
import com.amalto.core.storage.Storage;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.XmlServerException;

/**
 * All applications must call the methods of this wrapper only They never directly call the underlying API
 * 
 * @author bgrieder
 * 
 * @ejb.bean name="XmlServerSLWrapper" display-name="XML:DB Stateless Wrapper"
 * description="Description for XML:DB Stateless Wrapper" jndi-name="amalto/remote/xmldb/xmlserverslwrapper"
 * local-jndi-name = "amalto/local/xmldb/xmlserverslwrapper" type="Stateless" view-type="both"
 * 
 * @ejb.permission view-type = "remote" role-name = "administration"
 * @ejb.permission view-type = "local" unchecked = "true"
 * 
 * 
 * 
 * 
 * @ejb.remote-facade
 */
public class XmlServerSLWrapperBean implements SessionBean, XmlServer {

    private static final Logger LOG = Logger.getLogger(XmlServerSLWrapperBean.class);

    private static String SERVER_CLASS;

    static {
        SERVER_CLASS = MDMConfiguration.getConfiguration().getProperty("xmlserver.class"); //$NON-NLS-1$
        if ((SERVER_CLASS == null) || SERVER_CLASS.length() == 0) {
            SERVER_CLASS = "com.amalto.core.storage.SQLWrapper"; //$NON-NLS-1$
        }
    }

    IXmlServerSLWrapper server = null;

    /**
     * XmlServerSLWrapperBean.java Constructor
     */
    public XmlServerSLWrapperBean() {
        super();
    }

    @Override
    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    }

    @Override
    public void ejbRemove() throws EJBException, RemoteException {
        try {
            if (server instanceof IXmlServerEBJLifeCycle) {
                ((IXmlServerEBJLifeCycle) server).doRemove();
            }
        } catch (Exception e) {
            throw new EJBException(e.getLocalizedMessage());
        }
    }

    @Override
    public void ejbActivate() throws EJBException, RemoteException {
        try {
            if (server instanceof IXmlServerEBJLifeCycle) {
                ((IXmlServerEBJLifeCycle) server).doActivate();
            }
        } catch (Exception e) {
            throw new EJBException(e.getLocalizedMessage());
        }
    }

    @Override
    public void ejbPassivate() throws EJBException, RemoteException {
        try {
            if (server instanceof IXmlServerEBJLifeCycle) {
                ((IXmlServerEBJLifeCycle) server).doPassivate();
            }
        } catch (Exception e) {
            throw new EJBException(e.getLocalizedMessage());
        }
    }

    public void ejbCreate() throws javax.ejb.CreateException {
        try {
            server = (IXmlServerSLWrapper) Class.forName(SERVER_CLASS).newInstance();
            if (server instanceof IXmlServerEBJLifeCycle) {
                ((IXmlServerEBJLifeCycle) server).doCreate();
            }
        } catch (Exception e) {
            throw new CreateException(e.getLocalizedMessage());
        }
    }

    public void ejbPostCreate() throws javax.ejb.CreateException {
        try {
            if (server instanceof IXmlServerEBJLifeCycle) {
                ((IXmlServerEBJLifeCycle) server).doPostCreate();
            }
        } catch (Exception e) {
            throw new CreateException(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean isUpAndRunning() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("isUpAndRunning() "); //$NON-NLS-1$
        }
        return server.isUpAndRunning();
    }

    @Override
    public String[] getAllClusters(String revisionID) throws XtentisException {
        try {
            return server.getAllClusters(revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public void clearCache() throws XtentisException {
        server.clearCache();
    }

    @Override
    public long deleteCluster(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.deleteCluster(revisionID, clusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long deleteAllclusterNames(String revisionID) throws XtentisException {
        try {
            return server.deleteAllClusters(revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long createCluster(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.createCluster(revisionID, clusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID)
            throws XtentisException {
        try {
            return server.putDocumentFromFile(fileName, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID, String documentType)
            throws XtentisException {
        try {
            return server.putDocumentFromFile(fileName, uniqueID, clusterName, revisionID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID)
            throws XtentisException {
        try {
            return server.putDocumentFromString(xmlString, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long putDocumentFromString(String string, String uniqueID, String clusterName, String revisionID, String documentType)
            throws XtentisException {
        try {
            return server.putDocumentFromString(string, uniqueID, clusterName, revisionID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XtentisException {
        try {
            return server.putDocumentFromDOM(root, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId)
            throws com.amalto.core.util.XtentisException {
        try {
            return server.putDocumentFromSAX(dataClusterName, docReader, input, revisionId);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XtentisException {
        try {
            return server.getDocumentAsString(revisionID, clusterName, uniqueID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public boolean existCluster(String revision, String cluster) throws XtentisException {
        try {
            if (cluster.endsWith(StorageAdmin.STAGING_SUFFIX)) {
                cluster = StringUtils.substringBeforeLast(cluster, "#");
            }
            return server.existCluster(revision, cluster);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding)
            throws XtentisException {
        try {
            return server.getDocumentAsString(revisionID, clusterName, uniqueID, encoding);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType)
            throws XtentisException {
        try {
            return server.getDocumentBytes(revisionID, clusterName, uniqueID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.getAllDocumentsUniqueID(revisionID, clusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long deleteDocument(String revisionID, String clusterName, String uniqueID) throws XtentisException {
        return deleteDocument(revisionID, clusterName, uniqueID, IXmlServerSLWrapper.TYPE_DOCUMENT);
    }

    @Override
    public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType)
            throws XtentisException {
        try {
            return server.deleteDocument(revisionID, clusterName, uniqueID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public int deleteXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String objectRootElementName, IWhereItem whereItem)
            throws XtentisException {
        try {
            return server.deleteXtentisObjects(objectRootElementNameToRevisionID, objectRootElementNameToClusterName,
                    objectRootElementName, whereItem);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public int deleteItems(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem)
            throws XtentisException {
        try {
            return server.deleteItems(conceptPatternsToRevisionID, conceptPatternsToClusterName, conceptName, whereItem);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID,
            String targetclusterName) throws XtentisException {
        try {
            return server.moveDocumentById(sourceRevisionID, sourceclusterName, uniqueID, targetRevisionID, targetclusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long countItems(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem)
            throws XtentisException {
        try {
            return server.countItems(conceptPatternsToRevisionID, conceptPatternsToClusterName, conceptName, whereItem);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public long countXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, IWhereItem whereItem)
            throws XtentisException {
        try {
            return server.countXtentisObjects(objectRootElementNameToRevisionID, objectRootElementNameToClusterName,
                    mainObjectRootElementName, whereItem);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters)
            throws XtentisException {
        try {
            return server.runQuery(revisionID, clusterName, query, parameters);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters, final int start,
            final int limit, final boolean withTotalCount) throws XtentisException {
        try {
            return server.runQuery(revisionID, clusterName, query, parameters, start, limit, withTotalCount);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XtentisException {
        try {
            return server.getItemPKsByCriteria(criteria);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public String getXtentisObjectsQuery(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
            ArrayList<String> viewableObjectElements, IWhereItem whereItem, String orderBy, String direction, int start, int limit)
            throws XtentisException {
        try {
            return server.getXtentisObjectsQuery(objectRootElementNameToRevisionID, objectRootElementNameToClusterName,
                    mainObjectRootElementName, viewableObjectElements, whereItem, orderBy, direction, start, limit);
        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Objects Query ", e);
        }
    }

    @Override
    public String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID,
            LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
            ArrayList<String> viewableObjectElements, IWhereItem whereItem, String orderBy, String direction, int start,
            int limit, boolean withTotalCount) throws XtentisException {
        try {
            return server.getXtentisObjectsQuery(objectRootElementNameToRevisionID, objectRootElementNameToClusterName,
                    mainObjectRootElementName, viewableObjectElements, whereItem, orderBy, direction, start, limit,
                    withTotalCount);
        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Objects Query ", e);
        }
    }

    @Override
    public String getItemsQuery(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String forceMainPivot,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit,
            int spellThreshold) throws XtentisException {
        try {
            String q = server.getItemsQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot,
                    viewableFullPaths, whereItem, orderBy, direction, start, limit);
            if (LOG.isDebugEnabled()) {
                LOG.debug("getQuery():\n " + q); //$NON-NLS-1$
            }
            return q;
        } catch (Exception e) {
            throw new XtentisException("Unable to build the query", e);
        }
    }

    @Override
    public String getItemsQuery(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String forceMainPivot,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit,
            int spellThreshold, boolean firstTotalCount, Map<String, ArrayList<String>> metaDataTypes) throws XtentisException {
        try {
            String q = server.getItemsQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot,
                    viewableFullPaths, whereItem, orderBy, direction, start, limit, firstTotalCount, metaDataTypes);
            if (LOG.isDebugEnabled()) {
                LOG.debug("getQuery():\n " + q); //$NON-NLS-1$
            }
            return q;
        } catch (Exception e) {
            throw new XtentisException("Unable to build the query", e);
        }
    }

    @Override
    public String getPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap<String, String[]> pivotWithKeys,
            LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, String[] indexPaths, IWhereItem whereItem,
            String[] pivotDirections, String[] indexDirections, int start, int limit) throws XtentisException {
        try {
            return server.getPivotIndexQuery(clusterName, mainPivotName, pivotWithKeys, itemsRevisionIDs, defaultRevisionID,
                    indexPaths, whereItem, pivotDirections, indexDirections, start, limit);
        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Objects Query", e);
        }
    }

    @Override
    public String getChildrenItemsQuery(String clusterName, String conceptName, String[] PKXpaths, String FKXpath,
            String labelXpath, String fatherPK, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID,
            IWhereItem whereItem, int start, int limit) throws XtentisException {
        try {
            return server.getChildrenItemsQuery(clusterName, conceptName, PKXpaths, FKXpath, labelXpath, fatherPK,
                    itemsRevisionIDs, defaultRevisionID, whereItem, start, limit);

        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Items Query", e);
        }
    }

    @Override
    public boolean supportTransaction() {
        return server.supportTransaction();
    }

    @Override
    public void start(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.start(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public void commit(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.commit(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public void rollback(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.rollback(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public void end(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.end(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public void close() throws com.amalto.core.util.XtentisException {
        try {
            server.close();
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public List<String> globalSearch(String dataCluster, String keyword, int start, int end)
            throws com.amalto.core.util.XtentisException {
        try {
            return server.globalSearch(dataCluster, keyword, start, end);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    @Override
    public void exportDocuments(String revisionId, String clusterName, int start, int end, boolean includeMetadata,
            OutputStream outputStream) throws com.amalto.core.util.XtentisException {
        try {
            server.exportDocuments(revisionId, clusterName, start, end, includeMetadata, outputStream);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * @return boolean
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    @Override
    public boolean supportStaging(String dataCluster) {
        if (dataCluster == null || dataCluster.trim().length() == 0) {
            return false;
        }
        Server server = ServerContext.INSTANCE.get();
        Storage storage = server.getStorageAdmin().get(dataCluster + StorageAdmin.STAGING_SUFFIX, null);
        return storage != null;
    }
}