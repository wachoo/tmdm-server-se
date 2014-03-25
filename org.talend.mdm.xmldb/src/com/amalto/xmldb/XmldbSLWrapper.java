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
package com.amalto.xmldb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.exist.xmldb.XmldbURI;
import org.talend.mdm.commmon.util.bean.ItemCacheKey;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.BinaryResource;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.XmlServerException;

/**
 * An XML DB Implementation of the wrapper that works with eXist Open
 * 
 * @author Bruno Grieder
 */
public class XmldbSLWrapper extends AbstractXmldbSLWrapper {

    private static Logger LOG = Logger.getLogger(XmldbSLWrapper.class);

    private static String SERVERNAME = "localhost";

    private static String SERVERPORT = "8080";

    private static String ADMIN_USERNAME = "admin";

    private static String ADMIN_PASSWORD = "1bc29b36f623ba82aaf6724fd3b16718";

    private static String DRIVER = "org.exist.xmldb.DatabaseImpl";

    private static String DBID = "exist";

    private static String DBURL = "exist/xmlrpc/db";

    private static String ISUPURL = "exist/";

    // be pessimistic
    private static boolean SERVER_STATE_OK = false;

    /** A cache of collections to speed up search */
    protected HashMap<String, org.xmldb.api.base.Collection> clusters = new HashMap<String, org.xmldb.api.base.Collection>();

    static {
        registerDataBase();
    }

    private static void registerDataBase() {

        // Make sure the DB is not already registered
        Database[] databases = DatabaseManager.getDatabases();
        if ((databases != null) && (databases.length > 0)) {
            return;
        }

        Properties properties = MDMConfiguration.getConfiguration();

        try {
            SERVERNAME = properties.getProperty("xmldb.server.name") == null ? SERVERNAME : properties
                    .getProperty("xmldb.server.name");
            SERVERPORT = properties.getProperty("xmldb.server.port") == null ? SERVERPORT : properties
                    .getProperty("xmldb.server.port");
            ;
            ADMIN_USERNAME = properties.getProperty("xmldb.administrator.username") == null ? ADMIN_USERNAME : properties
                    .getProperty("xmldb.administrator.username");
            ;
            ADMIN_PASSWORD = properties.getProperty("xmldb.administrator.password") == null ? ADMIN_PASSWORD : properties
                    .getProperty("xmldb.administrator.password");
            ;
            DRIVER = properties.getProperty("xmldb.driver") == null ? DRIVER : properties.getProperty("xmldb.driver");
            ;
            DBID = properties.getProperty("xmldb.dbid") == null ? DBID : properties.getProperty("xmldb.dbid");
            ;
            DBURL = properties.getProperty("xmldb.dburl") == null ? DBURL : properties.getProperty("xmldb.dburl");
            ;
            ISUPURL = properties.getProperty("xmldb.isupurl") == null ? ISUPURL : properties.getProperty("xmldb.isupurl");
            ;
        } catch (Exception e) {
        }

        try {
            // register DBManager
            if (LOG.isTraceEnabled()) {
                LOG.trace("registerDBManager() registering");
            }
            Class<? extends Database> cl = (Class<? extends Database>) Class.forName(DRIVER);
            Database database = cl.newInstance();
            if (LOG.isTraceEnabled()) {
                LOG.trace("registerDBManager() Driver instantiated");
            }
            DatabaseManager.registerDatabase(database);
            if (LOG.isDebugEnabled()) {
                LOG.debug("registerDBManager() Driver registered");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected QueryBuilder newQueryBuilder() {
        return new ExistQueryBuilder();
    }

    /**
     * Build the XML DB URL from the revisionID and clusterName
     * 
     * @param revisionID
     * @param cluster
     * @return
     */
    protected String getFullURL(String revisionID, String cluster) {
        if (!MDMConfiguration.isExistDb()) {
            return CommonUtil.getPath(revisionID, cluster);
        } else {
            if (revisionID != null) {
                revisionID = revisionID.replaceAll("\\[HEAD\\]|HEAD", "");
            }
            return "xmldb:" + DBID + "://" + SERVERNAME + ":" + SERVERPORT + "/" + DBURL
                    + ((revisionID == null) || "".equals(revisionID) ? "" : "/" + "R-" + revisionID)
                    + ((cluster == null) || "".equals(cluster) ? "" : "/" + cluster);
        }
    }

    /**
     * Retrieve the appropriate collection for a given universeID and cluster name
     * 
     * @param revisionID
     * @param clusterName
     * @param create if <code>true</code>, will create the collection if it does not exist
     * @return
     * @throws XmlServerException
     */
    protected org.xmldb.api.base.Collection getCollection(String revisionID, String clusterName, boolean create)
            throws XmlServerException {
        if (revisionID != null && revisionID.equals("null")) {
            revisionID = null;
        }
        if (revisionID != null) {
            revisionID = revisionID.replaceAll("\\[HEAD\\]|HEAD", "");
        }
        String key = ((revisionID == null) || "".equals(revisionID) ? "__HEAD__" : revisionID)
                + ((clusterName == null) ? "__ROOT__" : clusterName);

        if (LOG.isTraceEnabled()) {
            LOG.trace("getCollection() R-" + key);
        }

        // registerDBManager();
        org.xmldb.api.base.Collection col = clusters.get(key);
        if (col == null) {

            try {
                col = DatabaseManager.getCollection(getFullURL(revisionID, clusterName), ADMIN_USERNAME, ADMIN_PASSWORD);
                if (col == null) {
                    if (!create) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("The cluster '" + clusterName + "' cannot be found in " //$NON-NLS-1$ //$NON-NLS-2$
                                    + (revisionID == null ? "HEAD" : "revision " + revisionID)); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        return null;
                    }
                    // get the revision
                    col = DatabaseManager.getCollection(getFullURL(revisionID, null), ADMIN_USERNAME, ADMIN_PASSWORD);
                    if (col == null) {
                        // create the revision
                        col = DatabaseManager.getCollection(getFullURL(null, null), ADMIN_USERNAME, ADMIN_PASSWORD);
                        CollectionManagementService service = (CollectionManagementService) col.getService(
                                "CollectionManagementService", "1.0");
                        col = service.createCollection("R-" + revisionID);
                    }
                    // create the cluster
                    CollectionManagementService service = (CollectionManagementService) col.getService(
                            "CollectionManagementService", "1.0");
                    col = service.createCollection(clusterName);
                }
                clusters.put(key, col);
            } catch (Exception e) {
                String err = "getCollection failed on cluster " + clusterName;
                LOG.info(err, e);
                throw new XmlServerException(e);
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("getCollection() re-using cached collection");
            }
        }
        return col;
    }

    /***************************************************************************
     * 
     * D E T E C T
     * 
     **************************************************************************/
    /**
     * Is the server up
     */
    @Override
    public boolean isUpAndRunning() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("isUpAndRunning() Server State OK ? " + SERVER_STATE_OK + "   Proceesing Upgrade ? " + PROCESSING_UPGRADE);
        }
        if (SERVER_STATE_OK) {
            return true;
        }
        if (PROCESSING_UPGRADE) {
            return false;
        }

        // No testing --> assume it works
        if ("".equals(ISUPURL)) {
            return true;
        }

        String uriString = "http://" + SERVERNAME + ":" + SERVERPORT + "/" + ISUPURL;
        if (LOG.isDebugEnabled()) {
            LOG.debug("isUpAndRunning() " + uriString);
        }
        try {
            HttpClient client = new HttpClient();
            HttpClientParams params = new HttpClientParams();
            params.setSoTimeout(1000);
            params.setConnectionManagerTimeout(200);
            client.setParams(params);

            URI uri = new URI(uriString, false, "utf-8");
            HostConfiguration config = new HostConfiguration();
            config.setHost(uri);

            GetMethod method = new GetMethod(uriString);
            PROCESSING_UPGRADE = false;
            method.setFollowRedirects(true);

            if (LOG.isDebugEnabled()) {
                LOG.debug("isUpAndRunning() here");
            }

            int status = client.executeMethod(config, method);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Server returned status : " + status + " at uri: " + uriString);
            }
            if (status >= 400) {
                return false;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Server is running at : " + uriString);
            }

            // check if need upgrade
            checkMe();

            if (PROCESSING_UPGRADE) {
                return false;
            }

            return SERVER_STATE_OK;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Not UpAndRunning() at " + uriString + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage());
            }
            return false;
        }
    }

    /***************************************************************************
     * 
     * C L U S T E R S
     * 
     **************************************************************************/

    /**
     * Get all Clusters (Collections in XML:DB talk)
     * 
     * @throws XmlServerException
     * 
     */
    @Override
    public String[] getAllClusters(String revisionID) throws XmlServerException {
        try {
            return getCollection(revisionID, null, true).listChildCollections();
        } catch (Exception e) {
            String err = "Unable to retrieve all clusters on " + getFullURL(revisionID, null) + ": " + e.getClass().getName()
                    + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            return null;
        }
    }

    /**
     * Delete Cluster
     * 
     * @throws XmlServerException
     * 
     */
    @Override
    public long deleteCluster(String revisionID, String clusterName) throws XmlServerException {
        try {
            long startT = System.currentTimeMillis();

            XmldbURI uri = XmldbURI.xmldbUriFor("xmldb:" + DBID + "://" + SERVERNAME + ":" + SERVERPORT + "/" + DBURL);

            XmlRpcClient client = new XmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String url = "http://" + uri.getAuthority() + uri.getContext();
            config.setServerURL(new URL(url));
            config.setBasicUserName(ADMIN_USERNAME);
            config.setBasicPassword(ADMIN_PASSWORD);
            client.setConfig(config);

            client.execute("removeCollection", new Object[] { clusterName });

            // org.xmldb.api.base.Collection col = getCollection(revisionID, null, true);
            // CollectionManagementService service =
            // (CollectionManagementService)col.getService("CollectionManagementService", "1.0");
            // service.removeCollection(clusterName);
            String key = ((revisionID == null) || "".equals(revisionID) ? "__HEAD__" : revisionID)
                    + ((clusterName == null) ? "__ROOT__" : clusterName);
            clusters.remove(key);
            // //clear items from the cache
            // for(ItemCacheKey key1: itemsCache.keySet()){
            // if(key1.getRevisionID().equals((revisionID == null) || "".equals(revisionID)?"__HEAD__":revisionID) &&
            // key1.getDataClusterID().equals(clusterName==null?"__ROOT__":clusterName)){
            // itemsCache.remove(key1);
            // }
            // }
            long time = System.currentTimeMillis() - startT;
            return time;
        } catch (Exception e) {
            String err = "Unable to delete cluster " + clusterName + " on " + getFullURL(revisionID, null) + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            return -1;
        }
    }

    /**
     * Delete All Clusters
     * 
     * @throws XmlServerException
     * 
     */
    @Override
    public long deleteAllClusters(String revisionID) throws XmlServerException {

        try {
            long startT = System.currentTimeMillis();
            org.xmldb.api.base.Collection col = getCollection(revisionID, null, true);
            CollectionManagementService service = (CollectionManagementService) col.getService("CollectionManagementService",
                    "1.0");
            String[] clusterNames = col.listChildCollections();
            if (clusterNames != null) {
                for (String clusterName : clusterNames) {
                    service.removeCollection(clusterName);
                }
            }
            // clear cache
            clusters.clear();
            // clear items from the cache
            // for(ItemCacheKey key1: itemsCache.keySet()){
            // if(key1.getRevisionID().equals((revisionID == null) || "".equals(revisionID)?"__HEAD__":revisionID) ){
            // itemsCache.remove(key1);
            // }
            // }
            long time = System.currentTimeMillis() - startT;
            return time;
        } catch (Exception e) {
            String err = "Unable to delete all clusters  on " + getFullURL(revisionID, null) + ": " + e.getClass().getName()
                    + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            return -1;
        }
    }

    /**
     * Create a Cluster - default options
     * 
     * @throws XmlServerException
     * 
     */
    @Override
    public long createCluster(String revisionID, String clusterName) throws XmlServerException {
        try {
            long startT = System.currentTimeMillis();
            getCollection(revisionID, clusterName, true);
            long time = System.currentTimeMillis() - startT;
            return time;
        } catch (Exception e) {
            String err = "Unable to create the cluster " + clusterName + " on " + getFullURL(revisionID, clusterName) + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            return -1;
        }
    }

    /***************************************************************************
     * 
     * D O C U M E N T S
     * 
     **************************************************************************/
    /**
     * Stores a document in a file
     * 
     * @throws XmlServerException
     * 
     */
    @Override
    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID, String documentType)
            throws XmlServerException {

        try {
            long startT = System.currentTimeMillis();

            // encode uniqueID
            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");

            boolean binary = true;
            if (IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType)) {
                binary = false;
            }

            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            Resource document;
            if (binary) {
                document = col.createResource(encodedID, "BinaryResource");
            } else {
                document = col.createResource(encodedID, "XMLResource");
            }
            File f = new File(fileName);
            if (!f.canRead()) {
                throw new IOException("Cannot read file " + fileName);
            }
            document.setContent(f);
            col.storeResource(document);
            // put item to cache
            ItemCacheKey key = new ItemCacheKey(revisionID, uniqueID, clusterName);
            // itemsCache.put(key, document.getContent().toString());
            long time = System.currentTimeMillis() - startT;
            return time;
        } catch (Exception e) {
            String err = "Unable to put the document from file " + fileName + " in  cluster " + clusterName + " on "
                    + getFullURL(revisionID, clusterName) + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            return -1;
        }

    }

    /**
     * Stores a document from a string
     * 
     * @throws XmlServerException
     * 
     */
    @Override
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID,
            String documentType) throws XmlServerException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("putDocumentFromString() [R-" + revisionID + "/" + clusterName + "/" + uniqueID + "]");
        }

        if (!existCluster(revisionID, clusterName)) {
            throw new XmlServerException("Cluster '" + clusterName + "' (revision: '" + revisionID + "') does not exist");
        }

        long startT = System.currentTimeMillis();
        try {
            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");

            boolean binary = true;
            if (IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType)) {
                binary = false;
            }

            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            Resource document;
            if (binary) {
                document = col.createResource(encodedID, "BinaryResource");
            } else {
                document = col.createResource(encodedID, "XMLResource");
            }
            // remove xml declaration
            xmlString = xmlString.replaceFirst("<\\?xml.*\\?>", "");
            document.setContent(xmlString);
            col.storeResource(document);
        } catch (XmlServerException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to put the document from string in cluster " + clusterName + " on "
                    + getFullURL(revisionID, clusterName) + ": ";
            LOG.error(err, e);
            return -1;
        }

        return System.currentTimeMillis() - startT;
    }

    /**
     * Stores a document from a DOM {@link Element}
     * 
     * @throws XmlServerException
     * 
     */
    @Override
    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID)
            throws XmlServerException {
        if (!existCluster(revisionID, clusterName)) {
            throw new XmlServerException("Cluster '" + clusterName + "' (revision: '" + revisionID + "') does not exist");
        }

        long startT = System.currentTimeMillis();
        try {

            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");

            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            XMLResource document = (XMLResource) col.createResource(encodedID, "XMLResource");
            document.setContentAsDOM(root);
            col.storeResource(document);
            // put item to cache
            // ItemCacheKey key=new ItemCacheKey(revisionID,uniqueID,clusterName);
            // itemsCache.put(key, document.getContent().toString());
        } catch (XmlServerException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to put the document from string in cluster " + clusterName + " on "
                    + getFullURL(revisionID, clusterName) + ": ";
            LOG.info(err, e);
            return -1;
        }
        long time = System.currentTimeMillis() - startT;

        return time;
    }

    @Override
    public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId)
            throws XmlServerException {
        // TODO Implement this
        throw new NotImplementedException();
    }

    @Override
    public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType)
            throws XmlServerException {
        try {

            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            // col.setProperty(OutputKeys.INDENT, "yes");
            // encode uniqueID
            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");

            Resource res;
            if (IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType)) {
                col.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                res = col.getResource(encodedID);
                if (res == null) {
                    return null;
                }
                String xml = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n" + ((XMLResource) res).getContent();
                return xml.getBytes("UTF-16");
            } else {
                res = col.getResource(encodedID);
                if (res == null) {
                    return null;
                }
                BinaryResource binRes = (BinaryResource) res;

                if (binRes.getContent() instanceof byte[]) {
                    if (binRes.getContent() != null) {
                        return (byte[]) binRes.getContent();
                    }
                }

                InputStream is = (InputStream) binRes.getContent();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8 * 1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }

                return baos.toByteArray();
            }
        } catch (Exception e) {
            String err = "Unable to get as bytes the document " + uniqueID + " on " + getFullURL(revisionID, clusterName)
                    + " - type " + documentType;
            LOG.info(err, e);
            return null;
        }
    }

    @Override
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding)
            throws XmlServerException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("getDocumentAsString() " + revisionID + "/" + clusterName + "/" + uniqueID + "  encoding=" + encoding);
        }

        XMLResource res = null;
        try {
            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true); // change it to false
            // col.setProperty(OutputKeys.INDENT, "yes");
            col.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // encode uniqueID
            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");
            res = (XMLResource) col.getResource(encodedID);

            if (res == null || res.getContent() == null) {
                return null;
            }
            // store xml in cache
            // itemsCache.put(key1, res.getContent().toString());
            return (encoding == null ? "" : "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n") + res.getContent();
        } catch (Exception e) {
            String err = "Unable to get the document " + uniqueID + " on " + getFullURL(revisionID, clusterName) + "\n" + res;
            LOG.info(err, e);
            return null;
        }

    }

    @Override
    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException {

        try {
            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            // col.setProperty(OutputKeys.INDENT, "yes");
            col.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            String[] encodedIDs = col.listResources();
            if (encodedIDs == null) {
                return null;
            }
            String[] decodedIDs = new String[encodedIDs.length];
            for (int i = 0; i < encodedIDs.length; i++) {
                decodedIDs[i] = URLDecoder.decode(encodedIDs[i], "UTF-8");
            }
            return decodedIDs;

        } catch (Exception e) {
            String err = "Unable to get the documents on " + getFullURL(revisionID, clusterName) + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            return null;
        }
    }

    @Override
    public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType)
            throws XmlServerException {

        org.xmldb.api.base.Collection col = null;

        long startT = System.currentTimeMillis();
        try {
            boolean binary = (!IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType));
            col = getCollection(revisionID, clusterName, true);

            // encode uniqueID
            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");

            Resource res;
            if (binary) {
                res = col.createResource(encodedID, "BinaryResource");
            } else {
                res = col.createResource(encodedID, "XMLResource");
            }
            if (col.getResource(encodedID) != null) {
                col.removeResource(res);
            } else {
                throw new XmlServerException("Could not find the document to delete.");
            }
        } catch (Exception e) {
            String err = "Unable to delete the document " + uniqueID + " on " + getFullURL(revisionID, clusterName) + ": "
                    + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XmlServerException(err, e);
        }
        long time = System.currentTimeMillis() - startT;
        return time;
    }

    @Override
    public int deleteItems(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem)
            throws XmlServerException {

        org.xmldb.api.base.Collection col = null;
        try {
            LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
            pivots.put(conceptName, "pivot");

            // determine revision
            String revisionID = null;
            Set<String> patterns = conceptPatternsToRevisionID.keySet();
            for (String pattern : patterns) {
                if (conceptName.matches(pattern)) {
                    revisionID = conceptPatternsToRevisionID.get(pattern);
                    break;
                }
            }
            // determine cluster
            String clusterName = null;
            patterns = conceptPatternsToClusterName.keySet();
            for (String pattern : patterns) {
                if (conceptName.matches(pattern)) {
                    clusterName = conceptPatternsToClusterName.get(pattern);
                    break;
                }
            }
            if (clusterName == null) {
                throw new XmlServerException("Unable to find a cluster for concept '" + conceptName + "'");
            }

            // Replace for QueryBuilder
            // String xquery ="for $pivot in " + getXQueryCollectionName(revisionID,
            // clusterName)+"/ii/p"+conceptName+(whereItem !=null ? "\nwhere "+buildWhere("", pivots,
            // whereItem,true)+"\n" : "") + "\nreturn base-uri($pivot)";

            ArrayList<String> elements = new ArrayList<String>();
            elements.add(conceptName.startsWith("/") ? conceptName : "/" + conceptName);
            String xquery = getItemsUriQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, clusterName, elements,
                    whereItem);

            Collection<String> res = runQuery(null, null, xquery, null);

            // set at head of db
            col = getCollection(revisionID, clusterName, true);

            for (String uri : res) {
                // String[] paths = uri.split("/");
                // String encodedID = paths[paths.length-1];
                Resource resource = col.createResource(uri, "XMLResource");
                col.removeResource(resource);
                // remove item from cache
                // ItemCacheKey key=new ItemCacheKey(null,uri,null);
                // itemsCache.remove(key);
            }

            return res.size();

        } catch (XmlServerException xe) {
            String err = "Unable to delete '" + conceptName + "' items. " + xe.getMessage();
            LOG.info(err, xe);
            throw new XmlServerException(err);
        } catch (Exception e) {
            String err = "Unable to delete '" + conceptName + "' items.";
            LOG.info(err, e);
            throw new XmlServerException(err);
        }

    }

    @Override
    public int deleteXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String objectRootElementName, IWhereItem whereItem)
            throws XmlServerException {

        org.xmldb.api.base.Collection col = null;
        try {
            LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
            pivots.put(objectRootElementName, "pivot");

            // determine revision
            String revisionID = objectRootElementNameToRevisionID.get(objectRootElementName);

            // determine cluster
            String clusterName = objectRootElementNameToClusterName.get(objectRootElementName);
            if (clusterName == null) {
                throw new XmlServerException("Unable to find a cluster for Xtentis Object Root Element Name '"
                        + objectRootElementName + "'");
            }

            // Replace for QueryBuilder
            // String xquery =
            // "for $pivot in " +
            // getXQueryCollectionName(revisionID, clusterName)+"/"+objectRootElementName+
            // (whereItem !=null ? "\nwhere "+buildWhere("", pivots, whereItem,true)+"\n" : "") +
            // "\nreturn base-uri($pivot)";
            String xquery = "for $pivot in " + queryBuilder.getXQueryCollectionName(revisionID, clusterName) + "/"
                    + objectRootElementName + (whereItem != null ? "\nwhere " + buildWhere(pivots, whereItem, null) + "\n" : "")
                    + "\nreturn base-uri($pivot)";

            Collection<String> res = runQuery(null, null, xquery, null);

            // set at head of db
            col = getCollection(revisionID, clusterName, true);

            for (String uri : res) {
                // String[] paths = uri.split("/");
                // String encodedID = paths[paths.length-1];
                Resource resource = col.createResource(uri, "XMLResource");
                col.removeResource(resource);
                // remove item from cache
                // ItemCacheKey key=new ItemCacheKey(null,uri,null);
                // itemsCache.remove(uri);
            }

            return res.size();

        } catch (XmlServerException xe) {
            String err = "Unable to delete Xtentis Objects of Root Element Name '" + objectRootElementName + "'. "
                    + xe.getMessage();
            LOG.info(err, xe);
            throw new XmlServerException(err);
        } catch (Exception e) {
            String err = "Unable to delete Xtentis Objects of Root Element Name '" + objectRootElementName + "'.";
            LOG.info(err, e);
            throw new XmlServerException(err);
        }

    }

    /**
     * Direct Query in the native language supported by the XML server
     * 
     * @throws XmlServerException
     * 
     */
    @Override
    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters)
            throws XmlServerException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("runQuery() Cluster: " + revisionID + "/" + clusterName + "\nQuery: \n" + query);
        }
        try {

            // replace parameters in the procedure
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    String param = parameters[i];
                    query = query.replaceAll("([^\\\\])%" + i + "([^\\d])", "$1" + param + "$2");
                }
            }

            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            // service.setProperty("indent", "yes");
            // service.setProperty("highlight-matches", "both");

            ResourceSet resourceSet = service.query(query);
            ResourceIterator iter = resourceSet.getIterator();

            ArrayList<String> result = new ArrayList<String>();

            while (iter.hasMoreResources()) {
                String content = (String) iter.nextResource().getContent();
                // content = HIGHLIGHT_START_PATTERN.matcher(content).replaceAll("__h");
                // content = HIGHLIGHT_END_PATTERN.matcher(content).replaceAll("h__");
                result.add(content);
            }
            // Release resources
            resourceSet.clear();

            return result;

        } catch (Exception e) {
            String err = "Unable to perform single find for query: \"" + query + "\"" + " on "
                    + getFullURL(revisionID, clusterName) + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            throw new XmlServerException(err, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doPassivate()
     */
    @Override
    public void doPassivate() throws XmlServerException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("doPassivate() ");
        }
        try {
            Set<String> keys = this.clusters.keySet();
            for (String clusterName : keys) {
                org.xmldb.api.base.Collection collection = clusters.get(clusterName);
                collection.close();
            }
            this.clusters = new HashMap<String, org.xmldb.api.base.Collection>();
        } catch (Exception e) {
            throw new XmlServerException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doRemove()
     */
    @Override
    public void doRemove() throws XmlServerException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("doRemove() ");
        }
        try {
            Set<String> keys = this.clusters.keySet();
            for (String clusterName : keys) {
                org.xmldb.api.base.Collection collection = clusters.get(clusterName);
                try {
                    collection.close();
                } catch (Exception x) {
                }
            }
            this.clusters = new HashMap<String, org.xmldb.api.base.Collection>();
        } catch (Exception e) {
            throw new XmlServerException(e);
        }
    }

    /***********************************************************************
     * 
     * Helper Methods
     * 
     ***********************************************************************/

    protected static Pattern pathWithoutConditions = Pattern.compile("(.*?)[\\[|/].*");

    /**
     * Returns the first part - eg. the concept - from the path
     * 
     * @param path
     * @return the Concept
     */
    public static String getRootElementNameFromPath(String path) {
        if (!path.endsWith("/")) {
            path += "/";
        }
        Matcher m = pathWithoutConditions.matcher(path);
        if (m.matches()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    protected static boolean PROCESSING_UPGRADE = false;

    /**
     * This is called once on the first get Collection call to check that the server is OK Upgrade has been removed
     * since we are starting from a clean state
     * 
     * @throws XmlServerException
     */
    protected void checkMe() throws XmlServerException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkMe() ");
        }

        // processing upgrade code

        // if processing - wait
        // while(PROCESSING_UPGRADE) try {Thread.sleep(2000);} catch (InterruptedException e){};

        SERVER_STATE_OK = true;

    }

    @Override
    public void clearCache() {
        clusters.clear();
    }

    @Override
    public void close() throws XmlServerException {
        // Nothing to clean up.
    }

    @Override
    public boolean existCluster(String revisionID, String clusterName) throws XmlServerException {
        if (clusterName == null || clusterName.trim().length() == 0) {
            return false;
        }
        org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, false);
        if (col == null) {
            return false;
        }
        return true;
    }
}
