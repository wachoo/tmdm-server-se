package com.amalto.xmldb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.exist.xmldb.XmldbURI;
import org.talend.mdm.commmon.util.bean.ItemCacheKey;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.BinaryResource;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import com.amalto.commons.core.utils.XPathUtils;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
import com.amalto.xmlserver.interfaces.XmlServerException;

/**
 * An XML DB Implementation of the wrapper that works with eXist Open
 * 
 * @author Bruno Grieder
 */
public class XmldbSLWrapper implements IXmlServerSLWrapper, IXmlServerEBJLifeCycle {

    protected static String SERVERNAME = "localhost";

    protected static String SERVERPORT = "8080";

    protected static String ADMIN_USERNAME = "admin";

    protected static String ADMIN_PASSWORD = "1bc29b36f623ba82aaf6724fd3b16718";

    protected static String DRIVER = "org.exist.xmldb.DatabaseImpl";

    protected static String DBID = "exist";

    protected static String DBURL = "exist/xmlrpc/db";

    protected static String ISUPURL = "exist/";

    // be pessimistic
    protected static boolean SERVER_STATE_OK = false;
    // protected final static String CONFIG_FILE = "amaltoConfig.xml";

    static {
        if (MDMConfiguration.isExistDb()) {
            registerDataBase();
        }
    }

    protected static void registerDataBase() {

        // Make sure the DB is not already registered
        Database[] databases = DatabaseManager.getDatabases();
        if ((databases != null) && (databases.length > 0))
            return;

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
            org.apache.log4j.Logger.getLogger(XmldbSLWrapper.class).trace("registerDBManager() registering");
            Class<? extends Database> cl = (Class<? extends Database>) Class.forName(DRIVER);
            Database database = cl.newInstance();
            org.apache.log4j.Logger.getLogger(XmldbSLWrapper.class).trace("registerDBManager() Driver instantiated");
            DatabaseManager.registerDatabase(database);
            org.apache.log4j.Logger.getLogger(XmldbSLWrapper.class).debug("registerDBManager() Driver registered");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** A cache of collections to speed up search */
    protected HashMap<String, org.xmldb.api.base.Collection> clusters = new HashMap<String, org.xmldb.api.base.Collection>();

    /** A cache of items, the key is the item id */
    // protected Hashtable<ItemCacheKey, String> itemsCache=new Hashtable<ItemCacheKey, String>();
    /** Max size of the cache */
    protected static int CACHE_ITEM_MAX_SIZE = 20000;

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
            if (revisionID != null)
                revisionID = revisionID.replaceAll("\\[HEAD\\]|HEAD", "");
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
        if (revisionID != null && revisionID.equals("null"))
            revisionID = null;
        if (revisionID != null)
            revisionID = revisionID.replaceAll("\\[HEAD\\]|HEAD", "");
        String key = ((revisionID == null) || "".equals(revisionID) ? "__HEAD__" : revisionID)
                + ((clusterName == null) ? "__ROOT__" : clusterName);

        org.apache.log4j.Logger.getLogger(this.getClass()).trace("getCollection() R-" + key);

        // registerDBManager();
        org.xmldb.api.base.Collection col = clusters.get(key);
        if (col == null) {

            try {
                col = DatabaseManager.getCollection(getFullURL(revisionID, clusterName), ADMIN_USERNAME, ADMIN_PASSWORD);
                if (col == null) {
                    if (!create) {
                        if (org.apache.log4j.Logger.getLogger(this.getClass()).isDebugEnabled())
                            org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                                    "The cluster '" + clusterName + "' cannot be found in " //$NON-NLS-1$ //$NON-NLS-2$
                                            + (revisionID == null ? "HEAD" : "revision " + revisionID)); //$NON-NLS-1$ //$NON-NLS-2$
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
                org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
                throw new XmlServerException(e);
            }
        } else {
            org.apache.log4j.Logger.getLogger(this.getClass()).trace("getCollection() re-using cached collection");
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
    public boolean isUpAndRunning() {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace(
                "isUpAndRunning() Server State OK ? " + SERVER_STATE_OK + "   Proceesing Upgrade ? " + PROCESSING_UPGRADE);
        if (SERVER_STATE_OK)
            return true;
        if (PROCESSING_UPGRADE)
            return false;

        // No testing --> assume it works
        if ("".equals(ISUPURL))
            return true;

        String uriString = "http://" + SERVERNAME + ":" + SERVERPORT + "/" + ISUPURL;
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("isUpAndRunning() " + uriString);
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

            org.apache.log4j.Logger.getLogger(this.getClass()).debug("isUpAndRunning() here");

            int status = client.executeMethod(config, method);
            org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                    "Server returned status : " + status + " at uri: " + uriString);
            if (status >= 400)
                return false;
            org.apache.log4j.Logger.getLogger(this.getClass()).debug("Server is running at : " + uriString);

            // check if need upgrade
            checkMe();

            if (PROCESSING_UPGRADE)
                return false;

            return SERVER_STATE_OK;
        } catch (Exception e) {
            org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                    "Not UpAndRunning() at " + uriString + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage());
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
    public String[] getAllClusters(String revisionID) throws XmlServerException {
        try {
            return getCollection(revisionID, null, true).listChildCollections();
        } catch (Exception e) {
            String err = "Unable to retrieve all clusters on " + getFullURL(revisionID, null) + ": " + e.getClass().getName()
                    + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).info(err, e);
            return null;
        }
    }

    /**
     * Delete Cluster
     * 
     * @throws XmlServerException
     * 
     */
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
            org.apache.log4j.Logger.getLogger(this.getClass()).info(err, e);
            return -1;
        }
    }

    /**
     * Delete All Clusters
     * 
     * @throws XmlServerException
     * 
     */
    public long deleteAllClusters(String revisionID) throws XmlServerException {

        try {
            long startT = System.currentTimeMillis();
            org.xmldb.api.base.Collection col = getCollection(revisionID, null, true);
            CollectionManagementService service = (CollectionManagementService) col.getService("CollectionManagementService",
                    "1.0");
            String[] clusterNames = col.listChildCollections();
            if (clusterNames != null) {
                for (int i = 0; i < clusterNames.length; i++) {
                    service.removeCollection(clusterNames[i]);
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
            org.apache.log4j.Logger.getLogger(this.getClass()).info(err, e);
            return -1;
        }
    }

    /**
     * Create a Cluster - default options
     * 
     * @throws XmlServerException
     * 
     */
    public long createCluster(String revisionID, String clusterName) throws XmlServerException {
        try {
            long startT = System.currentTimeMillis();
            getCollection(revisionID, clusterName, true);
            long time = System.currentTimeMillis() - startT;
            return time;
        } catch (Exception e) {
            String err = "Unable to create the cluster " + clusterName + " on " + getFullURL(revisionID, clusterName) + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).info(err, e);
            return -1;
        }
    }

    /***************************************************************************
     * 
     * D O C U M E N T S
     * 
     **************************************************************************/
    /**
     * Stores a document from a file - default options
     * 
     * @throws XmlServerException
     * 
     */
    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID)
            throws XmlServerException {
        return putDocumentFromFile(fileName, uniqueID, clusterName, revisionID, IXmlServerSLWrapper.TYPE_DOCUMENT);

    }

    /**
     * Stores a document in a file
     * 
     * @throws XmlServerException
     * 
     */
    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID, String documentType)
            throws XmlServerException {

        try {
            long startT = System.currentTimeMillis();

            // encode uniqueID
            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");

            boolean binary = true;
            if (IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType))
                binary = false;

            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            Resource document;
            if (binary)
                document = col.createResource(encodedID, "BinaryResource");
            else
                document = col.createResource(encodedID, "XMLResource");
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
            org.apache.log4j.Logger.getLogger(this.getClass()).info(err, e);
            return -1;
        }

    }

    /**
     * Stores a document in a string - default options
     * 
     * @throws XmlServerException
     * 
     */
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID)
            throws XmlServerException {

        // Connection conx = getConnection();
        long time = putDocumentFromString(xmlString, uniqueID, clusterName, revisionID, IXmlServerSLWrapper.TYPE_DOCUMENT);
        return time;
    }

    /**
     * Stores a document from a string
     * 
     * @throws XmlServerException
     * 
     */
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID,
            String documentType) throws XmlServerException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace(
                "putDocumentFromString() [R-" + revisionID + "/" + clusterName + "/" + uniqueID + "]");

        long startT = System.currentTimeMillis();
        try {

            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");

            boolean binary = true;
            if (IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType))
                binary = false;

            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            Resource document;
            if (binary)
                document = col.createResource(encodedID, "BinaryResource");
            else
                document = col.createResource(encodedID, "XMLResource");
            // remove xml declaration
            xmlString = xmlString.replaceFirst("<\\?xml.*\\?>", "");
            document.setContent(xmlString);
            col.storeResource(document);
            // update item cache
            // ItemCacheKey key=new ItemCacheKey(revisionID,uniqueID,clusterName);
            // itemsCache.put(key, document.getContent().toString());
        } catch (XmlServerException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to put the document from string in cluster " + clusterName + " on "
                    + getFullURL(revisionID, clusterName) + ": ";
            org.apache.log4j.Logger.getLogger(this.getClass()).info(err, e);
            return -1;
        }
        long time = System.currentTimeMillis() - startT;

        return time;
    }

    /**
     * Stores a document from a DOM {@link Element}
     * 
     * @throws XmlServerException
     * 
     */
    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID)
            throws XmlServerException {

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
            org.apache.log4j.Logger.getLogger(this.getClass()).info(err, e);
            return -1;
        }
        long time = System.currentTimeMillis() - startT;

        return time;
    }

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
                if (res == null)
                    return null;
                String xml = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n" + ((XMLResource) res).getContent();
                return xml.getBytes("UTF-16");
            } else {
                res = col.getResource(encodedID);
                if (res == null)
                    return null;
                BinaryResource binRes = (BinaryResource) res;

                if (binRes.getContent() instanceof byte[]) {
                    if (binRes.getContent() != null)
                        return (byte[]) binRes.getContent();
                }

                InputStream is = (InputStream) binRes.getContent();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8 * 1024];
                int len;
                while ((len = is.read(buffer)) > 0)
                    baos.write(buffer, 0, len);

                return baos.toByteArray();
            }
        } catch (Exception e) {
            String err = "Unable to get as bytes the document " + uniqueID + " on " + getFullURL(revisionID, clusterName)
                    + " - type " + documentType;
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            return null;
        }
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException {
        return getDocumentAsString(revisionID, clusterName, uniqueID, "UTF-16");
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding)
            throws XmlServerException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace(
                "getDocumentAsString() " + revisionID + "/" + clusterName + "/" + uniqueID + "  encoding=" + encoding);

        XMLResource res = null;
        try {
            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true); // change it to false
            // col.setProperty(OutputKeys.INDENT, "yes");
            col.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // encode uniqueID
            String encodedID = URLEncoder.encode(uniqueID, "UTF-8");
            res = (XMLResource) col.getResource(encodedID);

            if (res == null || res.getContent() == null)
                return null;
            // store xml in cache
            // itemsCache.put(key1, res.getContent().toString());
            return (encoding == null ? "" : "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n") + res.getContent();
        } catch (Exception e) {
            String err = "Unable to get the document " + uniqueID + " on " + getFullURL(revisionID, clusterName) + "\n" + res;
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            return null;
        }

    }

    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException {

        try {
            org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            // col.setProperty(OutputKeys.INDENT, "yes");
            col.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            String[] encodedIDs = col.listResources();
            if (encodedIDs == null)
                return null;
            String[] decodedIDs = new String[encodedIDs.length];
            for (int i = 0; i < encodedIDs.length; i++) {
                decodedIDs[i] = URLDecoder.decode(encodedIDs[i], "UTF-8");
            }
            return decodedIDs;

        } catch (Exception e) {
            String err = "Unable to get the documents on " + getFullURL(revisionID, clusterName) + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            return null;
        }
    }

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
            if (binary)
                res = col.createResource(encodedID, "BinaryResource");
            else
                res = col.createResource(encodedID, "XMLResource");
            col.removeResource(res);
            // remove item from cache
            // ItemCacheKey key1=new ItemCacheKey(revisionID,uniqueID,clusterName);
            // itemsCache.remove(key1);
        } catch (Exception e) {
            String err = "Unable to delete the document " + uniqueID + "on " + getFullURL(revisionID, clusterName) + ": "
                    + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            return -1;
        }
        long time = System.currentTimeMillis() - startT;
        return time;
    }

    public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID,
            String targetclusterName) throws XmlServerException {
        String xml = getDocumentAsString(sourceRevisionID, sourceclusterName, uniqueID);
        if (xml == null)
            return -1;
        return putDocumentFromString(xml, uniqueID, targetclusterName, targetRevisionID);
    }

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
            for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext();) {
                String pattern = iterator.next();
                if (conceptName.matches(pattern)) {
                    revisionID = conceptPatternsToRevisionID.get(pattern);
                    break;
                }
            }
            // determine cluster
            String clusterName = null;
            patterns = conceptPatternsToClusterName.keySet();
            for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext();) {
                String pattern = iterator.next();
                if (conceptName.matches(pattern)) {
                    clusterName = conceptPatternsToClusterName.get(pattern);
                    break;
                }
            }
            if (clusterName == null)
                throw new XmlServerException("Unable to find a cluster for concept '" + conceptName + "'");

            // Replace for QueryBuilder
            // String xquery ="for $pivot in " + getXQueryCollectionName(revisionID,
            // clusterName)+"/ii/p"+conceptName+(whereItem !=null ? "\nwhere "+buildWhere("", pivots,
            // whereItem,true)+"\n" : "") + "\nreturn base-uri($pivot)";
            conceptName = conceptName.startsWith("/") ? conceptName : "/" + conceptName;
            String xquery = "for $pivot in " + QueryBuilder.getXQueryCollectionName(revisionID, clusterName) + "/ii/p"
                    + conceptName
                    + (whereItem != null ? "\nwhere " + QueryBuilder.buildWhere("", pivots, whereItem, null) + "\n" : "")
                    + "\nreturn base-uri($pivot)";

            Collection<String> res = runQuery(null, null, xquery, null);

            // set at head of db
            col = getCollection(revisionID, clusterName, true);

            for (Iterator<String> iter = res.iterator(); iter.hasNext();) {
                String uri = iter.next();
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
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, xe);
            throw new XmlServerException(err);
        } catch (Exception e) {
            String err = "Unable to delete '" + conceptName + "' items.";
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }

    }

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
            if (clusterName == null)
                throw new XmlServerException("Unable to find a cluster for Xtentis Object Root Element Name '"
                        + objectRootElementName + "'");

            // Replace for QueryBuilder
            // String xquery =
            // "for $pivot in " +
            // getXQueryCollectionName(revisionID, clusterName)+"/"+objectRootElementName+
            // (whereItem !=null ? "\nwhere "+buildWhere("", pivots, whereItem,true)+"\n" : "") +
            // "\nreturn base-uri($pivot)";
            String xquery = "for $pivot in " + QueryBuilder.getXQueryCollectionName(revisionID, clusterName) + "/"
                    + objectRootElementName
                    + (whereItem != null ? "\nwhere " + QueryBuilder.buildWhere("", pivots, whereItem, null) + "\n" : "")
                    + "\nreturn base-uri($pivot)";

            Collection<String> res = runQuery(null, null, xquery, null);

            // set at head of db
            col = getCollection(revisionID, clusterName, true);

            for (Iterator<String> iter = res.iterator(); iter.hasNext();) {
                String uri = iter.next();
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
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, xe);
            throw new XmlServerException(err);
        } catch (Exception e) {
            String err = "Unable to delete Xtentis Objects of Root Element Name '" + objectRootElementName + "'.";
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }

    }

    /**
     * Direct Query in the native language supported by the XML server
     * 
     * @throws XmlServerException
     * 
     */
    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters)
            throws XmlServerException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace(
                "runQuery() Cluster: " + revisionID + "/" + clusterName + "\nQuery: \n" + query);
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
                    + getFullURL(revisionID, clusterName) + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }
    }

    public String getItemsQuery(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String forceMainPivot,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit)
            throws XmlServerException {
        return getItemsQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot, viewableFullPaths,
                whereItem, orderBy, direction, start, limit, false, null);
    }

    public String getItemsQuery(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String forceMainPivot,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit,
            boolean totalCountOnfirstRow, Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        // Replace for QueryBuilder
        return QueryBuilder.getQuery(true, conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot,
                viewableFullPaths, whereItem, orderBy, direction, start, limit, totalCountOnfirstRow, metaDataTypes);
    }

    public long countItems(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String fullPath, IWhereItem whereItem)
            throws XmlServerException {

        try {
            String xquery = null;

            // if the where Item is null, try to make a simplified x path query
            if (whereItem == null) {
                // get the concept
                // Replace for QueryBuilder
                // String conceptName = getRootElementNameFromPath(fullPath);
                String conceptName = QueryBuilder.getRootElementNameFromPath(fullPath);
                // determine revision
                String revisionID = null;
                Set<String> patterns = conceptPatternsToRevisionID.keySet();
                for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext();) {
                    String pattern = iterator.next();
                    if (conceptName.matches(pattern)) {
                        revisionID = conceptPatternsToRevisionID.get(pattern);
                        break;
                    }
                }
                // determine cluster
                String clusterName = null;
                patterns = conceptPatternsToClusterName.keySet();
                for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext();) {
                    String pattern = iterator.next();
                    if (conceptName.matches(pattern)) {
                        clusterName = conceptPatternsToClusterName.get(pattern);
                        break;
                    }
                }
                // Replace for QueryBuilder
                // xquery = "count("+getXQueryCollectionName(revisionID, clusterName)+"/ii/p/"+fullPath+")";
                xquery = "count(" + QueryBuilder.getXQueryCollectionName(revisionID, clusterName) + "/ii/p/" + fullPath + ")";
            } else {
                xquery = "let $zcount := ";
                xquery += getItemsQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, null, new ArrayList<String>(
                        Arrays.asList(new String[] { fullPath })), whereItem, null, null, 0, -1);
                xquery += "\n return count($zcount)";
            }

            ArrayList<String> results = runQuery(null, null, xquery, null);

            return Long.parseLong(results.get(0));

        } catch (Exception e) {
            String err = "Unable to count the elements using path " + fullPath;
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }
    }

    public String getXtentisObjectsQuery(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit)
            throws XmlServerException {

        LinkedHashMap<String, String> copyObjectRootElementNameToRevisionID = null;
        LinkedHashMap<String, String> copyObjectRootElementNameToClusterName = null;

        if (objectRootElementNameToRevisionID instanceof LinkedHashMap) {
            copyObjectRootElementNameToRevisionID = (LinkedHashMap<String, String>) objectRootElementNameToRevisionID;
        } else {
            copyObjectRootElementNameToRevisionID = new LinkedHashMap<String, String>();
            copyObjectRootElementNameToRevisionID.putAll(objectRootElementNameToRevisionID);
        }
        if (objectRootElementNameToClusterName instanceof LinkedHashMap) {
            copyObjectRootElementNameToClusterName = (LinkedHashMap<String, String>) objectRootElementNameToClusterName;
        } else {
            copyObjectRootElementNameToClusterName = new LinkedHashMap<String, String>();
            copyObjectRootElementNameToClusterName.putAll(objectRootElementNameToClusterName);
        }

        return getXtentisObjectsQuery(copyObjectRootElementNameToRevisionID, copyObjectRootElementNameToClusterName,
                mainObjectRootElementName, viewableFullPaths, whereItem, orderBy, direction, start, limit, false);
    }

    public String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID,
            LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit,
            boolean totalCountOnfirstRow) throws XmlServerException {
        // Replace for QueryBuilder
        return QueryBuilder.getQuery(false, objectRootElementNameToRevisionID, objectRootElementNameToClusterName,
                mainObjectRootElementName, viewableFullPaths, whereItem, orderBy, direction, start, limit, totalCountOnfirstRow,
                null);
    }

    public String getPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap<String, String[]> pivotWithKeys,
            LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, String[] indexPaths, IWhereItem whereItem,
            String[] pivotDirections, String[] indexDirections, int start, int limit) throws XmlServerException {

        try {

            String query = "";
            StringBuffer xq = new StringBuffer();
            StringBuffer xqFor = new StringBuffer();
            StringBuffer xqWhere = new StringBuffer();
            StringBuffer xqOrderby = new StringBuffer();
            StringBuffer xqReturn = new StringBuffer();

            HashSet<String> conceptMap = new HashSet<String>();
            String[] pivotPaths = new String[pivotWithKeys.size()];

            // parse pivotWithKeys
            int i = 0;
            for (Iterator iterator = pivotWithKeys.keySet().iterator(); iterator.hasNext(); i++) {
                String pivot = (String) iterator.next();
                pivotPaths[i] = pivot;
                String[] tmp = pivot.split("/");// TODO maybe care about other cases, like '//'
                if (tmp.length > 0)
                    conceptMap.add(tmp[0]);
            }
            // for
            if (conceptMap.size() > 0) {
                xqFor.append("for ");
                int j = 0;
                for (Iterator iterator = conceptMap.iterator(); iterator.hasNext(); j++) {
                    String conceptName = (String) iterator.next();
                    String revisionID = CommonUtil.getConceptRevisionID(itemsRevisionIDs, defaultRevisionID, conceptName);

                    String collectionPath = CommonUtil.getPath(revisionID, clusterName);
                    xqFor.append("$").append(conceptName).append(" in collection(\"").append(collectionPath).append("\")/ii/p/")
                            .append(conceptName);

                    if (j < conceptMap.size() - 1) {
                        xqFor.append(", ");
                    } else {
                        xqFor.append(" ");
                    }
                }
            }

            // where
            if (pivotPaths.length > 0) {
                xqWhere.append("where (1=1) "); // ctoum 20100110
                if (pivotPaths.length > 1) {
                    for (int k = 0; k < pivotPaths.length - 1; k++) {
                        String[] k1keys = pivotWithKeys.get(pivotPaths[k + 1]);
                        if (k1keys.length == 1) {
                            xqWhere.append(" and ($").append(pivotPaths[k]).append("=$").append(k1keys[0]).append(" or $")
                                    .append(pivotPaths[k]).append("=concat('[',$").append(k1keys[0]).append(",']')) ");
                        } else if (k1keys.length > 1) {
                            xqWhere.append(" and $").append(pivotPaths[k]).append("=concat(");
                            for (int l = 0; l < k1keys.length; l++) {
                                if (l > 0)
                                    xqWhere.append(",");
                                xqWhere.append("'['").append(",$").append(k1keys[l]).append(",']'");
                            }
                            xqWhere.append(") ");
                        }
                    }
                }
            }

            // build from WhereItem
            if (whereItem != null) {
                // ctoum 20100110
                if (xqWhere.length() > 0) {
                    xqWhere.append(" and ");
                }
                LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
                pivots.put(mainPivotName, mainPivotName);
                xqWhere.append(buildWhere(" ", pivots, whereItem, false));
                xqWhere.append(" ");

            }

            // order by
            if (pivotPaths.length > 0) {
                xqOrderby.append("order by ");
                for (int m = pivotPaths.length - 1; m > -1; m--) {
                    if (m < pivotPaths.length - 1)
                        xqOrderby.append(",");
                    // see 0016991, using the first element as pivot
                    xqOrderby.append("$").append(pivotPaths[m] + "[0]").append(" ");
                    // add direction
                    if (pivotDirections != null && pivotDirections.length > 0)
                        xqOrderby.append(pivotDirections[m] == null ? "" : " " + pivotDirections[m] + " ");
                }

                for (int m = 0; m < indexPaths.length; m++) {
                    // see 0016991, using the first element as pivot
                    xqOrderby.append(",").append("$").append(indexPaths[m] + "[0]").append(" ");
                    // add direction
                    if (indexDirections != null && indexDirections.length > 0)
                        xqOrderby.append(indexDirections[m] == null ? "" : " " + indexDirections[m] + " ");
                }
            }

            // return
            if (pivotPaths.length > 0) {
                xqReturn.append("return ");
                xqReturn.append("<result>");

                xqReturn.append("<result-pivot>");
                for (int n = pivotPaths.length - 1; n > -1; n--) {
                    xqReturn.append("{if ($").append(pivotPaths[n]).append(") then $").append(pivotPaths[n]).append(" else <")
                            .append(parseNodeNameFromXpath(pivotPaths[n])).append("/>}");
                }
                xqReturn.append("</result-pivot>");

                xqReturn.append("<result-title>");
                for (int n = 0; n < indexPaths.length; n++) {
                    xqReturn.append("{if ($").append(indexPaths[n]).append(") then $").append(indexPaths[n]).append(" else <")
                            .append(parseNodeNameFromXpath(indexPaths[n])).append("/>}");
                }
                xqReturn.append("</result-title>");

                xqReturn.append("<result-key>");
                String[] mainKeys = pivotWithKeys.get(pivotPaths[0]);
                for (int n = 0; n < mainKeys.length; n++) {
                    xqReturn.append("{if ($").append(mainKeys[n]).append(") then $").append(mainKeys[n]).append(" else <")
                            .append(parseNodeNameFromXpath(mainKeys[n])).append("/>}");
                }
                xqReturn.append("</result-key>");

                xqReturn.append("</result>  ");
            }

            xq.append(xqFor).append(xqWhere).append(xqOrderby).append(xqReturn);
            query = xq.toString();

            if (start >= 0 && limit > 0) {
                query = "let $list := \n" + query + "\n return subsequence($list," + (start + 1) + "," + limit + ")";
            }
            // replace (1=1) and to ""
            query = query.replaceAll("\\(1=1\\) and", "");
            // replace () and to ""
            query = query.replaceAll("\\(\\) and", "");
            return query;

        } catch (Exception e) {
            String err = "Unable to build the PivotIndex XQuery";
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }

    }

    public String getChildrenItemsQuery(String clusterName, String conceptName, String[] PKXpaths, String FKXpath,
            String labelXpath, String fatherPK, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID,
            IWhereItem whereItem) throws XmlServerException {

        try {

            String query = "";
            StringBuffer xq = new StringBuffer();
            StringBuffer xqFor = new StringBuffer();
            StringBuffer xqWhere = new StringBuffer();
            StringBuffer xqOrderby = new StringBuffer();
            StringBuffer xqReturn = new StringBuffer();

            // for
            xqFor.append("for ");
            String revisionID = CommonUtil.getConceptRevisionID(itemsRevisionIDs, defaultRevisionID, conceptName);// revision
            // issue
            // String revisionID=null;
            String collectionPath = CommonUtil.getPath(revisionID, clusterName);
            xqFor.append("$").append(conceptName).append(" in collection(\"").append(collectionPath).append("\")/ii/p/")
                    .append(conceptName).append(" ");

            // where
            xqWhere.append("where (1=1)");

            if (FKXpath != null) {
                xqWhere.append(" and ($").append(FKXpath).append(" = '").append(fatherPK).append("'").append(" or $")
                        .append(FKXpath).append("=concat('[','").append(fatherPK).append("',']')) ");
            }

            // build from WhereItem
            if (whereItem != null) {

                LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
                pivots.put(conceptName, conceptName);

                String appendWhere = buildWhere(" ", pivots, whereItem, false);
                ;
                if (appendWhere != null && appendWhere.length() > 0 && !appendWhere.trim().equals("")) {
                    xqWhere.append(" and ");
                    xqWhere.append(appendWhere);
                }

                xqWhere.append(" ");

            }

            // order by

            // return
            xqReturn.append("return ");
            xqReturn.append("<result>");

            xqReturn.append("<result-key>");

            if (PKXpaths.length == 1) {
                xqReturn.append("{$").append(PKXpaths[0]).append("/text()}");
            } else if (PKXpaths.length > 1) {
                xqReturn.append("{concat(");
                for (int l = 0; l < PKXpaths.length; l++) {
                    if (l > 0)
                        xqReturn.append(",");
                    xqReturn.append("'['").append(",$").append(PKXpaths[l]).append("/text()").append(",']'");
                }
                xqReturn.append(")}");
            }
            xqReturn.append("</result-key>");

            xqReturn.append("<result-label>");
            xqReturn.append("{$").append(labelXpath).append("/text()}");
            xqReturn.append("</result-label>");

            xqReturn.append("</result>  ");

            xq.append(xqFor).append(xqWhere).append(xqOrderby).append(xqReturn);
            query = xq.toString();

            return query;

        } catch (Exception e) {
            String err = "Unable to build the getChildrenItems XQuery";
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }

    }

    protected String parseNodeNameFromXpath(String input) {
        if (input == null)
            return "";

        String output = input;
        int pos = input.lastIndexOf("/");
        if (pos != -1) {
            output = input.substring(pos + 1);
        }
        return output;
    }

    public long countXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String objectFullPath, IWhereItem whereItem)
            throws XmlServerException {

        try {
            String xquery = null;

            // if the where Item is null, try to make a simplified x path query
            if (whereItem == null) {
                // get the concept
                // Replace for QueryBuilder
                // String rootElementName = getRootElementNameFromPath(objectFullPath);
                String rootElementName = QueryBuilder.getRootElementNameFromPath(objectFullPath);
                // determine revision
                String revisionID = objectRootElementNameToRevisionID.get(rootElementName);
                // determine cluster
                String clusterName = objectRootElementNameToClusterName.get(rootElementName);

                // Replace for QueryBuilder
                // xquery = "count("+getXQueryCollectionName(revisionID, clusterName)+"/"+objectFullPath+")";
                xquery = "count(" + QueryBuilder.getXQueryCollectionName(revisionID, clusterName) + "/" + objectFullPath + ")";
            } else {
                xquery = "let $zcount := ";
                xquery += getXtentisObjectsQuery(objectRootElementNameToRevisionID, objectRootElementNameToClusterName, null,
                        new ArrayList<String>(Arrays.asList(new String[] { objectFullPath })), whereItem, null, null, 0, -1);
                xquery += "\n return count($zcount)";
            }

            ArrayList<String> results = runQuery(null, null, xquery, null);

            return Long.parseLong(results.get(0));

        } catch (Exception e) {
            String err = "Unable to count the objects using path '" + objectFullPath + "'";
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }
    }

    /**
     * Scans the pivots and build a relative path to one of the pivots using the absolute path provided.<br/>
     * If no pivot is found a new pivaot is created<br/>
     * <br/>
     * Say we have a pivot named pivot0 referencing <code>Country/name</code>, the path <code>Country/name/EN</code>
     * will become <code>$pivot0/EN</code>
     * 
     */
    protected String getPathFromPivots(String bename, HashMap<String, String> pivots) throws XmlServerException {
        try {
            org.apache.log4j.Logger.getLogger(this.getClass()).trace("getPathFromPivots() " + bename + " - " + pivots.keySet());
            if (bename.startsWith("/"))
                bename = bename.substring(1);
            String beRoot = bename.split("/")[0];
            // find pivot
            Set<String> ps = pivots.keySet();
            String newPath = null;
            for (Iterator<String> iterator = ps.iterator(); iterator.hasNext();) {
                String pivot = iterator.next();
                String pivotRoot = pivot.split("/")[0];
                // aiming modify pivotRoot maybe ConceptName[condition], fix bug 0008980
                if (!beRoot.equals(pivotRoot)) {
                    Pattern p = Pattern.compile("(.*?)\\[.*\\]");
                    Matcher m = p.matcher(pivotRoot);
                    if (m.matches()) {
                        if (m.group(1).equals(beRoot)) {
                            int pos = bename.indexOf('/');
                            if (pos != -1) {
                                newPath = "$" + pivots.get(pivot) + bename.substring(pos);
                                break;
                            }
                        }
                    }
                }
                // end
                if (beRoot.equals(pivotRoot)) {
                    // found
                    newPath = "$" + pivots.get(pivot) + getPathFromPivot(pivot, bename);
                    break;
                }
            }
            if (newPath == null) {
                // add pivot
                String var = "pivot" + pivots.size();
                pivots.put(bename, var);
                newPath = "$" + var;
            }
            return newPath;
        } catch (Exception e) {
            String err = "Unable to get the path " + bename + " from the pivots" + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }
    }

    protected String buildWhere(String where, LinkedHashMap<String, String> pivots, IWhereItem whereItem,
            boolean useValueComparisons) throws XmlServerException {
        try {
            if (whereItem instanceof WhereLogicOperator) {
                Collection<IWhereItem> subItems = ((WhereLogicOperator) whereItem).getItems();
                if (subItems.size() == 0)
                    throw new XmlServerException("The logic operator must contain at least one element");
                if (subItems.size() == 1)
                    return // unnecessary AND or OR
                    buildWhere(where, pivots, subItems.iterator().next(), useValueComparisons);
                int i = 0;
                for (Iterator<IWhereItem> iter = subItems.iterator(); iter.hasNext();) {
                    IWhereItem item = iter.next();
                    if (++i > 1)
                        if (item instanceof WhereCondition) {
                            if (WhereCondition.PRE_OR.equals(((WhereCondition) item).getStringPredicate())) {
                                where = where + " or (";
                            } else {
                                where = where + " and (";
                            }
                        } else if (((WhereLogicOperator) whereItem).getType() == WhereLogicOperator.AND)
                            where += " and (";
                        else
                            where += " or (";
                    else
                        where += "(";
                    where = buildWhere(where, pivots, item, useValueComparisons) + ")";
                }// for
                return where;

            } else if (whereItem instanceof WhereCondition) {
                WhereCondition condition = (WhereCondition) whereItem;
                where += buildWhereCondition(condition, pivots, useValueComparisons);
                return where;
            } else {
                throw new XmlServerException("Unknown Where Type : " + whereItem.getClass().getName());
            }
        } catch (Exception e) {
            String err = "Unable to build the XQuery Where Clause " + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }
    }

    /**
     * Build a where condition in XQuery using paths relative to the provided list of pivots
     */
    public String buildWhereCondition(WhereCondition wc, LinkedHashMap<String, String> pivots, boolean useValueComparisons)
            throws XmlServerException {
        try {

            // all this is EXIST specific

            String where = "";
            String operator = wc.getOperator();

            // numeric detection
            boolean isNum = false;
            boolean isXpathFunction = QueryBuilder.isValidatedFunction(wc.getRightValueOrPath());
            // handle case of String starting with a zero e.g. 00441065 or ending with . e.g. 12345.
            if (!(wc.getRightValueOrPath().matches(".*\\D") || wc.getRightValueOrPath().startsWith("0")
                    || wc.getRightValueOrPath().endsWith(".") || wc.getRightValueOrPath().startsWith("+") || wc
                    .getRightValueOrPath().startsWith("-"))) {
                try {
                    Double.parseDouble(wc.getRightValueOrPath().trim());
                    isNum = true;
                } catch (Exception e) {
                }
            }

            // String encoded = wc.getRightValueOrPath().replaceAll("\\&", "&amp;").replaceAll("<",
            // "&lt;").replaceAll(">", "&gt;");
            String encoded = isXpathFunction ? wc.getRightValueOrPath().trim() : StringEscapeUtils.escapeXml(wc
                    .getRightValueOrPath());
            // aiming modify convert "" & " " to *
            if (encoded != null && encoded.trim().length() == 0) {
                encoded = "*";
            }
            // change * to .*
            encoded = encoded.replaceAll("\\.\\*|\\*", "\\.\\*");
            if (".*".equals(encoded))
                return "";
            String factorPivots = getPathFromPivots(wc.getLeftPath(), pivots);
            if (operator.equals(WhereCondition.CONTAINS)) {
                String predicate = wc.getStringPredicate();
                // check if the left path is an attribute or an element
                String path = wc.getLeftPath();
                if (path.endsWith("/"))
                    path = path.substring(0, wc.getLeftPath().length() - 1);
                String[] nodes = path.split("/");
                boolean isAttribute = nodes[nodes.length - 1].startsWith("@");
                if ((predicate == null) || predicate.equals(WhereCondition.PRE_NONE)) {
                    if (isAttribute) {
                        where = " matches(" + factorPivots + " , \"" + encoded + "\",\"i\") ";// factorPivots+" &= \""+encoded+"\" ";
                    } else {
                        where = QueryBuilder.buildContains(factorPivots, encoded, isXpathFunction);
                        // "("+factorPivots+"/descendant-or-self::* &= \""+encoded+"\") "+
                        // "or ("+factorPivots+"/descendant-or-self::*/attribute() &= \""+encoded+"\") ";
                    }
                } else if (predicate.equals(WhereCondition.PRE_AND)) {
                    if (isAttribute) {
                        where = " matches(" + factorPivots + " , \"" + encoded + "\",\"i\") ";// factorPivots+" &= \""+encoded+"\" ";
                    } else {
                        where = QueryBuilder.buildContains(factorPivots, encoded, isXpathFunction);
                        // "("+factorPivots+"/descendant-or-self::* &= \""+encoded+"\") "+
                        // "or ("+factorPivots+"/descendant-or-self::*/attribute() &= \""+encoded+"\") ";
                    }
                } else if (predicate.equals(WhereCondition.PRE_EXACTLY)) {
                    where = factorPivots + " eq \"" + encoded + "\"";
                } else if (predicate.equals(WhereCondition.PRE_STRICTAND)) {
                    // where = "near("+factorPivots+", \""+encoded+"\",1)";
                    where = "matches(" + factorPivots + ", \"" + encoded + "\",\"i\") ";
                } else if (predicate.equals(WhereCondition.PRE_OR)) {
                    if (isAttribute) {
                        where = " matches(" + factorPivots + " , \"" + encoded + "\",\"i\") ";
                    } else {
                        if (isXpathFunction) {
                            where = " matches(" + factorPivots + " , " + encoded + ",\"i\") ";
                        } else {
                            where = " matches(" + factorPivots + " , \"" + encoded + "\",\"i\") ";
                            // "or matches("+factorPivots+"/descendant-or-self::*/attribute() , \""+encoded+"\") ";
                        }
                    }
                } else if (predicate.equals(WhereCondition.PRE_NOT)) {
                    if (isAttribute) {
                        where = "not matches(" + factorPivots + " , \"" + encoded + "\",\"i\") ";
                    } else {
                        if (isXpathFunction) {
                            where = "not(" + " matches(" + factorPivots + " , " + encoded + ",\"i\") " + ")";
                        } else {
                            where = "not(" + " matches(" + factorPivots + " , \"" + encoded + "\",\"i\") " +
                            // "or matches("+factorPivots+"/descendant-or-self::*/attribute() , \""+encoded+"\") "+
                                    ")";
                        }
                    }
                }
                /*
                 * WAITING FOR FIX FROM EXIST if ((predicate==null) || predicate.equals(WhereCondition.PRE_NONE)) where
                 * = "contains("+factorPivots+",\""+encoded+"\")"; else if (predicate.equals(WhereCondition.PRE_AND))
                 * where = "contains("+factorPivots+",\""+encoded+"\")"; else if
                 * (predicate.equals(WhereCondition.PRE_EXACTLY)) where = factorPivots+" eq \""+encoded+"\""; else if
                 * (predicate.equals(WhereCondition.PRE_STRICTAND)) where = "near("+factorPivots+", \""+encoded+"\",1)";
                 * else if (predicate.equals(WhereCondition.PRE_OR)) where = factorPivots+" |= \""+encoded+"\""; else if
                 * (predicate.equals(WhereCondition.PRE_NOT)) where = "not(contains("+factorPivots+",\""+encoded+"\"))";
                 */

            } else if (operator.equals(WhereCondition.STRICTCONTAINS)) {
                // where = "near("+factorPivots+", \""+encoded+"\",1)";
                where = "matches(" + factorPivots + ", \"" + encoded + "\",\"i\") ";
            } else if (operator.equals(WhereCondition.STARTSWITH)) {
                if (isXpathFunction) {
                    where = "matches(" + factorPivots + ", " + "concat(" + encoded + ",\".*\") ,\"i\") ";
                } else {
                    // where = "near("+factorPivots+", \""+encoded+"*\",1)";
                    where = "matches(" + factorPivots + ", \"" + encoded + ".*\",\"i\") ";
                }
            } else if (operator.equals(WhereCondition.CONTAINS_TEXT_OF)) {
                // where = getPathFromPivots(wc.getRightValueOrPath(),pivots)+" = "+factorPivots; //JOIN error
                String factorRightPivot = XPathUtils.factor(encoded, pivots) + "";
                where = "contains(" + factorPivots + ", " + factorRightPivot + "/text()) ";

            } else if (operator.equals(WhereCondition.EQUALS)) {
                String useOpe = "eq";
                if (!useValueComparisons)
                    useOpe = WhereCondition.EQUALS;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.NOT_EQUALS)) {
                String useOpe = "ne";
                if (!useValueComparisons)
                    useOpe = WhereCondition.NOT_EQUALS;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.GREATER_THAN)) {
                String useOpe = "gt";
                if (!useValueComparisons)
                    useOpe = WhereCondition.GREATER_THAN;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) {
                String useOpe = "ge";
                if (!useValueComparisons)
                    useOpe = WhereCondition.GREATER_THAN_OR_EQUAL;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.LOWER_THAN)) {
                String useOpe = "lt";
                if (!useValueComparisons)
                    useOpe = WhereCondition.LOWER_THAN;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) {
                String useOpe = "le";
                if (!useValueComparisons)
                    useOpe = WhereCondition.LOWER_THAN_OR_EQUAL;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.NO_OPERATOR)) {
                where = factorPivots;
            }

            return where;

        } catch (Exception e) {
            String err = "Unable to build the Where Condition " + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }

    }

    /**
     * Build a relative path to the provided pivot using the absolute path provided.<br/>
     * <br/>
     * Say the pivot is referencing <code>Country/name</code>, the path <code>Country/name/EN</code> will become
     * <code>$pivot0/EN</code>
     * 
     */
    protected String getPathFromPivot(String pivot, String path) throws XmlServerException {
        try {

            // org.apache.log4j.Logger.getLogger(this.getClass()).debug("getPathFromPivot() "+path+" from "+pivot);

            if ((pivot == null) || (path == null))
                return null;

            if (pivot.startsWith("/"))
                pivot = pivot.substring(1);
            if (pivot.endsWith("/"))
                pivot = pivot.substring(0, pivot.length() - 1);
            if (path.startsWith("/"))
                path = path.substring(1);
            if (path.endsWith("/"))
                path = path.substring(0, path.length() - 1);

            String[] pivotPaths = pivot.split("/");
            String[] pathPaths = path.split("/");

            if (!pivotPaths[0].equals(pathPaths[0]))
                return null;

            String newPath = "";
            int matching = 0;
            for (int i = 1; i < pivotPaths.length; i++) {
                if (i < pathPaths.length)
                    if (pivotPaths[i].equals(pathPaths[i]))
                        matching++;
                    else
                        newPath += "/..";
                else
                    newPath += "/..";
            }
            for (int i = matching + 1; i < pathPaths.length; i++) {
                newPath += "/" + pathPaths[i];
            }

            // fix for eXist bug that has *sometimes* difficulties with "grand parents" (../..)
            // e.g. x/../../y --> x/.././../y
            newPath = newPath.replaceAll("\\.\\./\\.\\.", ".././..");

            return newPath;

        } catch (Exception e) {
            String err = "Unable to get the path " + path + " from pivot " + pivot + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger("INFO SYSTRACE " + this.getClass()).info(err, e);
            throw new XmlServerException(err);
        }
    }

    /*********************************************************************************
     * Lifecycle
     *********************************************************************************/

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doActivate()
     */
    public void doActivate() throws XmlServerException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("doActivate() ");
        // registerDBManager();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doCreate()
     */
    public void doCreate() throws XmlServerException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("doCreate() ");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doPassivate()
     */
    public void doPassivate() throws XmlServerException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("doPassivate() ");
        try {
            Set<String> keys = this.clusters.keySet();
            for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
                String clusterName = iter.next();
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
     * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doPostCreate()
     */
    public void doPostCreate() throws XmlServerException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doRemove()
     */
    public void doRemove() throws XmlServerException {
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("doRemove() ");
        try {
            Set<String> keys = this.clusters.keySet();
            for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
                String clusterName = iter.next();
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
        if (!path.endsWith("/"))
            path += "/";
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
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("checkMe() ");

        // processing upgrade code

        // if processing - wait
        // while(PROCESSING_UPGRADE) try {Thread.sleep(2000);} catch (InterruptedException e){};

        SERVER_STATE_OK = true;

    }

    public void clearCache() {
        clusters.clear();
    }

    public boolean existCluster(String revisionID, String clusterName) throws XmlServerException {
        if (clusterName == null || clusterName.trim().length() == 0)
            return false;
        org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, false);
        if (col == null)
            return false;
        return true;
    }

    /**
     * Generates an xml string from a node with or without the xml declaration (not pretty formatted)
     * 
     * @param n the node
     * @return the xml string
     * @throws TransformerException
     */
    public static String nodeToString(Node n, boolean omitXMLDeclaration) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        if (omitXMLDeclaration) {
            transformer.setOutputProperty("omit-xml-declaration", "yes");
        } else {
            transformer.setOutputProperty("omit-xml-declaration", "no");
        }
        transformer.setOutputProperty("indent", "yes"); // TODO: why? impact on performance + memory usage
        transformer.transform(new DOMSource(n), new StreamResult(sw));
        if (sw == null) {
            return null;
        }
        return sw.toString().replaceAll("\r\n", "\n");
    }

    public boolean supportTransaction() {
        return false;
    }

    public void start() throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    public void commit() throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    public void rollback() throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    public void end() throws XmlServerException {
        throw new UnsupportedOperationException();
    }
}
