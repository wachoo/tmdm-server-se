package com.amalto.xmldb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;
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
public class XmldbSLWrapper implements IXmlServerSLWrapper,IXmlServerEBJLifeCycle {
	
	protected static String SERVERNAME = "localhost";
	protected static String SERVERPORT = "8080";
	protected static String ADMIN_USERNAME = "admin";
	protected static String ADMIN_PASSWORD = "1bc29b36f623ba82aaf6724fd3b16718";
	protected static String DRIVER = "org.exist.xmldb.DatabaseImpl";
	protected static String DBID = "exist";
	protected static String DBURL = "exist/xmlrpc/db";
	protected static String ISUPURL = "exist/";
	
	//be pessimistic
	protected static boolean SERVER_STATE_OK = false;
//	private final static String CONFIG_FILE = "amaltoConfig.xml"; 
	
	
	
	static {
		registerDataBase();
	}
	
	private static void registerDataBase() {
		
		//Make sure the DB is not already registered
		Database[] databases = DatabaseManager.getDatabases();
		if ((databases!=null) && (databases.length>0)) return;
		
		Properties properties = MDMConfiguration.getConfiguration();
		
		try {
			SERVERNAME = properties.getProperty("xmldb.server.name") == null ? SERVERNAME : properties.getProperty("xmldb.server.name");
			SERVERPORT = properties.getProperty("xmldb.server.port")== null ? SERVERPORT : properties.getProperty("xmldb.server.port");;
			ADMIN_USERNAME = properties.getProperty("xmldb.administrator.username") == null ? ADMIN_USERNAME : properties.getProperty("xmldb.administrator.username");;
			ADMIN_PASSWORD = properties.getProperty("xmldb.administrator.password") == null ? ADMIN_PASSWORD : properties.getProperty("xmldb.administrator.password");;
			DRIVER = properties.getProperty("xmldb.driver") == null ? DRIVER : properties.getProperty("xmldb.driver");;
			DBID = properties.getProperty("xmldb.dbid") == null ? DBID : properties.getProperty("xmldb.dbid");;
			DBURL = properties.getProperty("xmldb.dburl") == null ? DBURL : properties.getProperty("xmldb.dburl");;
			ISUPURL = properties.getProperty("xmldb.isupurl") == null ? ISUPURL : properties.getProperty("xmldb.isupurl");;
		} catch (Exception e) {}
				
		try {
			//register DBManager
    		org.apache.log4j.Logger.getLogger(XmldbSLWrapper.class).trace("registerDBManager() registering");
	        Class<? extends Database> cl = (Class<? extends Database>)Class.forName(DRIVER);			
	        Database database = cl.newInstance();
	        org.apache.log4j.Logger.getLogger(XmldbSLWrapper.class).trace("registerDBManager() Driver instantiated");
	        DatabaseManager.registerDatabase(database);
	        org.apache.log4j.Logger.getLogger(XmldbSLWrapper.class).debug("registerDBManager() Driver registered");			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/** A cache of collections to speed up search */
	private HashMap<String,org.xmldb.api.base.Collection> clusters = new HashMap<String,org.xmldb.api.base.Collection>();
	 

    /**
     * Build the XML DB URL from the  revisionID and clusterName
     * @param revisionID
     * @param cluster
     * @return
     */
    private String getFullURL(String revisionID, String cluster) {
   	 	return "xmldb:"+DBID+"://"+SERVERNAME+":"+SERVERPORT+"/"+DBURL
   	 			+((revisionID == null) || "".equals(revisionID) ? "": "/"+"R-"+revisionID)
   	 			+((cluster== null) || "".equals(cluster) ? "": "/"+cluster);
    }
    
    
    /**
     * Retrieve the appropriate collection for a given universeID and cluster name
     * @param revisionID
     * @param clusterName
     * @param create
     * 		if <code>true</code>, will create the collection if it does not exist
     * @return
     * @throws XmlServerException
     */
	protected org.xmldb.api.base.Collection getCollection(String revisionID, String clusterName, boolean create) throws XmlServerException{
		if(revisionID!=null) revisionID=revisionID.replaceAll("\\[HEAD\\]|HEAD", "");
		String key =
			((revisionID == null) || "".equals(revisionID) ? "__HEAD__" : revisionID)
			+((clusterName == null) ? "__ROOT__" : clusterName);
		
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getCollection() R-"+key);
		
		//registerDBManager();
		org.xmldb.api.base.Collection col = clusters.get(key);
		if (col == null) {
			
			try {
				col = DatabaseManager.getCollection(getFullURL(revisionID, clusterName),ADMIN_USERNAME,ADMIN_PASSWORD);
				if (col==null) {
					if (!create) throw new XmlServerException(
						"The cluster '"+clusterName+"' cannot be found in " +
						(revisionID == null ? "HEAD" : "revision "+revisionID)
					);
					//get the revision
					col = DatabaseManager.getCollection(getFullURL(revisionID, null),ADMIN_USERNAME,ADMIN_PASSWORD);
					if (col == null) {
						//create the revision
						col = DatabaseManager.getCollection(getFullURL(null, null),ADMIN_USERNAME,ADMIN_PASSWORD);
						CollectionManagementService service = (CollectionManagementService)col.getService("CollectionManagementService", "1.0");
						col = service.createCollection("R-"+revisionID);
					}
					//create the cluster
					CollectionManagementService service = (CollectionManagementService)col.getService("CollectionManagementService", "1.0");
					col = service.createCollection(clusterName);
				}
				clusters.put(key, col);
    		} catch (XmlServerException e) {
    			throw(e);
    		} catch (Exception e) {
    			String err = "getCollection failed on cluster "+clusterName;
    			org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
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
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("isUpAndRunning() Server State OK ? "+SERVER_STATE_OK+ "   Proceesing Upgrade ? "+PROCESSING_UPGRADE);
		if (SERVER_STATE_OK) return true;		
		if (PROCESSING_UPGRADE) return false;
		
		//No testing --> assume it works
		if ("".equals(ISUPURL)) return true;
		
		String uriString = "http://"+SERVERNAME+":"+SERVERPORT+"/"+ISUPURL;
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("isUpAndRunning() "+uriString);
		try {			
			HttpClient client = new HttpClient();
			HttpClientParams params = new HttpClientParams();
			params.setSoTimeout(1000);
			params.setConnectionManagerTimeout(200);
			client.setParams(params);
			
			URI uri = new URI(uriString,false,"utf-8");
			HostConfiguration config = new HostConfiguration();
			config.setHost(uri);

			GetMethod method = new GetMethod(uriString);			
			PROCESSING_UPGRADE = false;
			method.setFollowRedirects(true);
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("isUpAndRunning() here");
			
			int status = client.executeMethod(config, method);
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("Server returned status : "+status+" at uri: "+uriString);
			if (status >= 400) return false;
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("Server is running at : "+uriString);
			
			//check if need upgrade
			checkMe();
					
			if (PROCESSING_UPGRADE) return false;
			
			return SERVER_STATE_OK;
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(
					"Not UpAndRunning() at "+uriString+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
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
			String err = 
				"Unable to retrieve all clusters on " +getFullURL(revisionID, null) 
				+ ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
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
			org.xmldb.api.base.Collection col = getCollection(revisionID, null, true);
			CollectionManagementService service = (CollectionManagementService)col.getService("CollectionManagementService", "1.0");
			service.removeCollection(clusterName);
			long time = System.currentTimeMillis() - startT;
			return time;
		} catch (Exception e) {
			String err = "Unable to delete cluster " + clusterName + " on " +getFullURL(revisionID, null) + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
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
			org.xmldb.api.base.Collection col =getCollection(revisionID, null, true);
			CollectionManagementService service = (CollectionManagementService)col.getService("CollectionManagementService", "1.0");
			String[] clusterNames = col.listChildCollections();
			if (clusterNames != null) {
				for (int i = 0; i < clusterNames.length; i++) {
					service.removeCollection(clusterNames[i]);
				}
			}
			long time = System.currentTimeMillis() - startT;
			return time;
		} catch (Exception e) {
			String err = "Unable to delete all clusters  on " + getFullURL(revisionID, null)+ ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
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
			String err = "Unable to create the cluster " + clusterName + " on " + getFullURL(revisionID, clusterName) + ": " + e.getClass().getName() + ": "
					+ e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
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
	public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
		return putDocumentFromFile(
			fileName, 
			uniqueID, 
			clusterName,
			revisionID,
			IXmlServerSLWrapper.TYPE_DOCUMENT
		);
	
	}
	

	/**
	 * Stores a document in a file
	 * 
	 * @throws XmlServerException
	 * 
	 */
	public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID, String documentType) throws XmlServerException {

		try {
			long startT = System.currentTimeMillis();
			
			//encode uniqueID
			String encodedID = URLEncoder.encode(uniqueID,"UTF-8");
			
			boolean binary = true; 
			if (IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType)) binary = false;
			
			org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
			Resource document;
			if (binary)
				document = col.createResource(encodedID, "BinaryResource");
			else
				document = col.createResource(encodedID, "XMLResource");
	        File f = new File(fileName);
	        if(!f.canRead()) {
	            throw new IOException("Cannot read file " + fileName);
	        }
	        document.setContent(f);
	        col.storeResource(document);

			long time = System.currentTimeMillis() - startT;
			return time;
		} catch (Exception e) {
			String err = "Unable to put the document from file " + fileName + " in  cluster " + clusterName + " on " + getFullURL(revisionID, clusterName) + ": "
					+ e.getClass().getName() + ": " + e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
			return -1;
		}

	}

	/**
	 * Stores a document in a string - default options
	 * 
	 * @throws XmlServerException
	 * 

	 */
	public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID) throws XmlServerException {

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
	public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID, String documentType) throws XmlServerException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("putDocumentFromString() [R-"+revisionID+"/"+clusterName+"/"+uniqueID+"]");

		long startT = System.currentTimeMillis();
		try {

			String encodedID = URLEncoder.encode(uniqueID,"UTF-8");
			
			boolean binary = true;
			if (IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType)) binary = false;
			
			org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
			Resource document;
			if (binary)
				document = col.createResource(encodedID, "BinaryResource");
			else
				document = col.createResource(encodedID, "XMLResource");
			document.setContent(xmlString);
			col.storeResource(document);
	        
		} catch (XmlServerException e) {
			throw (e);
		} catch (Exception e) {
			String err = "Unable to put the document from string in cluster " + clusterName + " on " + getFullURL(revisionID, clusterName) + ": "; 
			org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
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
	public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
		
		long startT = System.currentTimeMillis();
		try {

			String encodedID = URLEncoder.encode(uniqueID,"UTF-8");
			
			org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
			XMLResource document = (XMLResource)col.createResource(encodedID, "XMLResource");
			document.setContentAsDOM(root);
			col.storeResource(document);
	        
		} catch (XmlServerException e) {
			throw (e);
		} catch (Exception e) {
			String err = "Unable to put the document from string in cluster " + clusterName + " on " + getFullURL(revisionID, clusterName) + ": "; 
			org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
			return -1;
		}
		long time = System.currentTimeMillis() - startT;
		
		return time;
	}
	
	

	public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException {
		try {
			
			org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
//			col.setProperty(OutputKeys.INDENT, "yes");
					
//			encode uniqueID
			String encodedID = URLEncoder.encode(uniqueID,"UTF-8");
			
			Resource res;
			if (IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType)) {
				col.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				res = col.getResource(encodedID);
				if (res==null) return null;
				String xml =  "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n"+((XMLResource)res).getContent();
				return xml.getBytes("UTF-16");
			} else {
				res = col.getResource(encodedID);
				if (res==null) return null;
				BinaryResource binRes= (BinaryResource)res;
				
				if(binRes.getContent() instanceof byte[]){
					if(binRes.getContent()!=null)return (byte[]) binRes.getContent();
				}
				
				InputStream is = (InputStream) binRes.getContent();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[8 * 1024];
				int len;
				while ((len=is.read(buffer))>0) baos.write(buffer, 0, len);
				
				return baos.toByteArray();
			}
		} catch (Exception e) {
			String err = "Unable to get as bytes the document "+uniqueID+" on " +getFullURL(revisionID, clusterName)+" - type "+documentType;
			  org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
			return null;
		}
    }
	
	public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException {
		return getDocumentAsString(revisionID, clusterName, uniqueID, "UTF-16");
	}

	public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XmlServerException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getDocumentAsString() "+revisionID+"/"+clusterName+"/"+uniqueID+"  encoding="+encoding);

		XMLResource res=null;
		try {
			
			org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
//			col.setProperty(OutputKeys.INDENT, "yes");
			col.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
					
//			encode uniqueID
			String encodedID = URLEncoder.encode(uniqueID,"UTF-8");
			
	        res = (XMLResource)col.getResource(encodedID);
	        
	        if (res==null||res.getContent()==null) return null;
	        
	        return (encoding == null ? "" : "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n")+res.getContent();
		} catch (Exception e) {
			String err = "Unable to get the document "+uniqueID+" on " +getFullURL(revisionID, clusterName)+"\n"+res;
			  org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
			return null;
		}

	}


	public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException {

		try {
			org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);			
//			col.setProperty(OutputKeys.INDENT, "yes");
			col.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        String[] encodedIDs =  col.listResources();
	        if (encodedIDs == null) return null;
	        String[] decodedIDs = new String[encodedIDs.length];
	        for (int i = 0; i < encodedIDs.length; i++) {
        		decodedIDs[i]=URLDecoder.decode(encodedIDs[i],"UTF-8");
			}
	        return decodedIDs;
	        
		} catch (Exception e) {
			  String err = "Unable to get the documents on " +getFullURL(revisionID, clusterName)
			   +": "+e.getLocalizedMessage();
			  org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
			return null;
		}
	}


	
    public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException {

		org.xmldb.api.base.Collection col = null;
		
		long startT = System.currentTimeMillis();
		try {
			boolean binary = (! IXmlServerSLWrapper.TYPE_DOCUMENT.equals(documentType));
			col = getCollection(revisionID, clusterName, true);
			
//			encode uniqueID
			String encodedID = URLEncoder.encode(uniqueID,"UTF-8");
			
			Resource res;
			if (binary)
				res = col.createResource(encodedID, "BinaryResource");
			else
				res = col.createResource(encodedID, "XMLResource");
			col.removeResource(res);
		} catch (Exception e) {
			  String err = "Unable to delete the document "+uniqueID+"on " +getFullURL(revisionID, clusterName)
			   +": "+e.getLocalizedMessage();
			  org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
			return -1;
		}
		long time = System.currentTimeMillis() - startT;
		return time;
	}


	public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID, String targetclusterName) throws XmlServerException {
		String xml = getDocumentAsString(sourceRevisionID, sourceclusterName, uniqueID);
		if (xml==null) return -1;
		return putDocumentFromString(xml, uniqueID, targetclusterName, targetRevisionID);
	}

	
	public int deleteItems(
		LinkedHashMap<String, String> conceptPatternsToRevisionID, 
		LinkedHashMap<String, String> conceptPatternsToClusterName, 
		String conceptName, 
		IWhereItem whereItem
	) throws XmlServerException {
		
		org.xmldb.api.base.Collection col = null;
		try {
			HashMap<String,String> pivots = new HashMap<String,String>();
			pivots.put(conceptName,"pivot");
			
			//determine revision
			String revisionID = null;
			Set<String> patterns = conceptPatternsToRevisionID.keySet();
			for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
				String pattern = iterator.next();
				if (conceptName.matches(pattern)) {
					revisionID = conceptPatternsToRevisionID.get(pattern);
					break;
				}
			}
			//determine cluster
			String clusterName = null;
			patterns = conceptPatternsToClusterName.keySet();
			for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
				String pattern = iterator.next();
				if (conceptName.matches(pattern)) {
					clusterName = conceptPatternsToClusterName.get(pattern);
					break;
				}
			}
			if (clusterName == null) 
				throw new XmlServerException("Unable to find a cluster for concept '"+conceptName+"'");
			
			String xquery =
				"for $pivot in " +
				getXQueryCollectionName(revisionID, clusterName)+"/ii/p"+conceptName+
				(whereItem !=null ? "\nwhere "+buildWhere("", pivots, whereItem)+"\n" : "") +
				"\nreturn base-uri($pivot)";
			
			Collection<String> res = runQuery(null, null, xquery, null);
			
			//set at head of db
			col = getCollection(null, null, true);
						
			for (Iterator<String> iter = res.iterator(); iter.hasNext(); ) {
				String uri = iter.next();
//				String[] paths = uri.split("/");
//				String encodedID = paths[paths.length-1];
				Resource resource = col.createResource(uri, "XMLResource");
				col.removeResource(resource);
			}
			
			return res.size();
			
		} catch (XmlServerException xe) {
			String err = "Unable to delete '"+conceptName+"' items. "+xe.getMessage();
			org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,xe);
			throw new XmlServerException(err);
		} catch (Exception e) {
			String err = "Unable to delete '"+conceptName+"' items.";
			org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
			throw new XmlServerException(err);
		}

	}
	
	
	public int deleteXtentisObjects(
		HashMap<String, String> objectRootElementNameToRevisionID, 
		HashMap<String, String> objectRootElementNameToClusterName, 
		String objectRootElementName, 
		IWhereItem whereItem
	) throws XmlServerException {
		
		org.xmldb.api.base.Collection col = null;
		try {
			HashMap<String,String> pivots = new HashMap<String,String>();
			pivots.put(objectRootElementName,"pivot");
			
			//determine revision
			String revisionID = objectRootElementNameToRevisionID.get(objectRootElementName);

			//determine cluster
			String clusterName = objectRootElementNameToClusterName.get(objectRootElementName);
			if (clusterName == null) 
				throw new XmlServerException("Unable to find a cluster for Xtentis Object Root Element Name '"+objectRootElementName+"'");
			
			String xquery =
				"for $pivot in " +
				getXQueryCollectionName(revisionID, clusterName)+"/"+objectRootElementName+
				(whereItem !=null ? "\nwhere "+buildWhere("", pivots, whereItem)+"\n" : "") +
				"\nreturn base-uri($pivot)";
			
			Collection<String> res = runQuery(null, null, xquery, null);
			
			//set at head of db
			col = getCollection(null, null, true);
						
			for (Iterator<String> iter = res.iterator(); iter.hasNext(); ) {
				String uri = iter.next();
//				String[] paths = uri.split("/");
//				String encodedID = paths[paths.length-1];
				Resource resource = col.createResource(uri, "XMLResource");
				col.removeResource(resource);
			}
			
			return res.size();
			
		} catch (XmlServerException xe) {
			String err = "Unable to delete Xtentis Objects of Root Element Name '"+objectRootElementName+"'. "+xe.getMessage();
			org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,xe);
			throw new XmlServerException(err);
		} catch (Exception e) {
			String err = "Unable to delete Xtentis Objects of Root Element Name '"+objectRootElementName+"'.";
			org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
			throw new XmlServerException(err);
		}

	}

	
	/**
	 * Direct Query in the native language supported by the XML server 
	 * 
	 * @throws XmlServerException
	 * 
	 */
    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XmlServerException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("runQuery() Cluster: "+revisionID+"/"+clusterName+"\nQuery: \n"+query);
		try {

    		//replace parameters in the procedure
        	if (parameters!=null) {
        		for (int i = 0; i < parameters.length; i++) {
					String param = parameters[i];
					query=query.replaceAll("([^\\\\])%"+i+"([^\\d])", "$1"+param+"$2");
				}
        	}
			
			org.xmldb.api.base.Collection col = getCollection(revisionID, clusterName, true);
            XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
//            service.setProperty("indent", "yes");
//            service.setProperty("highlight-matches", "both");
                        
            ResourceSet resourceSet = service.query(query);
            ResourceIterator iter = resourceSet.getIterator();
            
        	ArrayList<String> result = new ArrayList<String>();
        	
	    	while (iter.hasMoreResources()) {
	    		String content = (String) iter.nextResource().getContent();
//	    		content = HIGHLIGHT_START_PATTERN.matcher(content).replaceAll("__h");
//	    		content = HIGHLIGHT_END_PATTERN.matcher(content).replaceAll("h__");
        		result.add(content);
        	}
        	return result;

		} catch (Exception e) {
			String err = "Unable to perform single find for query: \"" + query + "\"" + " on " +getFullURL(revisionID, clusterName) + ": " + e.getClass().getName() + ": "
					+ e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
			throw new XmlServerException(err);
		}
	}



    public String getItemsQuery(
    	LinkedHashMap<String, String> conceptPatternsToRevisionID, 
    	LinkedHashMap<String, String> conceptPatternsToClusterName, 
    	String forceMainPivot, 
    	ArrayList<String> viewableFullPaths, 
    	IWhereItem whereItem, 
    	String orderBy, 
    	String direction, 
    	int start, 
    	int limit
    ) throws XmlServerException {

    	try {
    		
	    	String xqFor ="";
	    	String xqWhere="";
	    	String xqOrderBy = "";
    		String xqReturn = "";
	    	
    		//build Pivots Map
    		LinkedHashMap<String,String> pivotsMap = new LinkedHashMap<String,String>();
    		int i=0;
			if (forceMainPivot != null) pivotsMap.put(forceMainPivot,"pivot"+(i++));
    			
	    	//Build the return using the vars
	    	Collection<String> c = viewableFullPaths;
	    	boolean moreThanOneViewable = viewableFullPaths.size()>1;
	    	for (Iterator<String> iter = c.iterator(); iter.hasNext(); ) {
				String bename = iter.next();
				String[] paths = bename.split("/");
				//retrieve the element name from the last path and remove any Xpath condition e.g. concept[toto='titi']
				String elementName = paths[paths.length-1].split("\\[")[0];
				String pivotPath = getPathFromPivots(bename, pivotsMap);
				//find pivot
				xqReturn+=
					(moreThanOneViewable ? "{" :"");
				if (elementName.startsWith("@")) { //attribute
					xqReturn+= "<"+elementName.substring(1)+">{string("+pivotPath+ ")}</"+elementName.substring(1)+">";
				} else {
					xqReturn+=
						"if ("+pivotPath+") then "+pivotPath+" else <"+elementName+"/>";
						//"if (empty("+pivotPath+"/*)) then <"+elementName+">{"+pivotPath+"/text()}</"+elementName+"> else  "+pivotPath;
				}
				xqReturn+=
					(moreThanOneViewable ? "}" :"");
	    	}
	    				
	    	// 	build from  WhereItem
	    	if (whereItem == null) 
	    		xqWhere="";
	    	else
	    		xqWhere = buildWhere("", pivotsMap, whereItem);
	    	
	    	//build order by
	    	if (orderBy == null) {
	    		orderBy = "";
	    	} else {
	    		orderBy = "order by "
	    					+getPathFromPivots(orderBy, pivotsMap)
	    					+(direction == null ? "" : " "+direction);
	    	}
	    	
			//build for
	    	for (Iterator<String> iter = pivotsMap.keySet().iterator(); iter.hasNext(); ) {
				String path = iter.next();
				//get the pivot namefor this path
				String pivotName = pivotsMap.get(path);
				//get the concept
				String conceptName = getRootElementNameFromPath(path);
				//determine revision
				String revisionID = null;
				Set<String> patterns = conceptPatternsToRevisionID.keySet();
				for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
					String pattern = iterator.next();
					if (conceptName.matches(pattern)) {
						revisionID = conceptPatternsToRevisionID.get(pattern);
						break;
					}
				}
				//determine cluster
				String clusterName = null;
				patterns = conceptPatternsToClusterName.keySet();
				for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
					String pattern = iterator.next();
					if (conceptName.matches(pattern)) {
						clusterName = conceptPatternsToClusterName.get(pattern);
						break;
					}
				}

				xqFor+="".equals(xqFor)?"for ": ", ";
//				xqFor+="$"+pivotName+" in "+getXQueryCollectionName(revisionID, clusterName)+"/ii/p/"+path;
//				Optimization - speeds up text queries but the risk is to query sub items with the same name
				xqFor+="$"+pivotName+" in "+getXQueryCollectionName(revisionID, clusterName)+"//"+path;
	    	}
	    	
	    	
	    	String query =
	    		xqFor
	    		+("".equals(xqWhere)? "" : "\nwhere "+xqWhere)
	    		+("".equals(xqOrderBy) ? "" : "\n"+xqOrderBy)
	    		+"\nreturn "+(moreThanOneViewable ? "<result>":"")+xqReturn+(moreThanOneViewable ? "</result>":""); 
	    	
	    	if (start>=0 && limit>0) {
	    		query = 
	    			"let $leres := \n"+query
	    			+"\n return subsequence($leres,"+(start+1)+","+limit+")";
	    	}
	    	
	    	return query;
	    	
	    } catch (Exception e) {
     	    String err = "Unable to build the Item XQuery";
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
     	    throw new XmlServerException(err);
	    } 
    }
    
    
    
    public long countItems(
    	LinkedHashMap<String, String> conceptPatternsToRevisionID, 
    	LinkedHashMap<String, String> conceptPatternsToClusterName, 
    	String fullPath, 
    	IWhereItem whereItem
    ) throws XmlServerException {
        
    	try {
    		String xquery = null;
    		
    		//if the where Item is null, try to make a simplified x path query
    		if (whereItem == null) {
				//get the concept
				String conceptName = getRootElementNameFromPath(fullPath);
				//determine revision
				String revisionID = null;
				Set<String> patterns = conceptPatternsToRevisionID.keySet();
				for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
					String pattern = iterator.next();
					if (conceptName.matches(pattern)) {
						revisionID = conceptPatternsToRevisionID.get(pattern);
						break;
					}
				}
				//determine cluster
				String clusterName = null;
				patterns = conceptPatternsToClusterName.keySet();
				for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
					String pattern = iterator.next();
					if (conceptName.matches(pattern)) {
						clusterName = conceptPatternsToClusterName.get(pattern);
						break;
					}
				}
				
				xquery = "count("+getXQueryCollectionName(revisionID, clusterName)+"/ii/p/"+fullPath+")";
    		} else {
    			xquery = "let $zcount := ";
    			xquery += getItemsQuery(
    				conceptPatternsToRevisionID, 
    				conceptPatternsToClusterName, 
    				null, 
    				new ArrayList<String>(Arrays.asList(new String[] {fullPath})), 
    				whereItem, 
    				null, 
    				null, 
    				0, 
    				-1
    			);
    			xquery +="\n return count($zcount)";
    		}
    		
    		ArrayList<String> results = runQuery(null, null, xquery, null);
    		
    		return Long.parseLong(results.get(0));
    		
        } catch (Exception e) {
     	    String err = "Unable to count the elements using path "+fullPath;
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
     	    throw new XmlServerException(err);
	    } 
    }

    
    public String getXtentisObjectsQuery(
    	HashMap<String, String> objectRootElementNameToRevisionID, 
    	HashMap<String, String> objectRootElementNameToClusterName, 
    	String mainObjectRootElementName, 
    	ArrayList<String> viewableFullPaths, 
    	IWhereItem whereItem, 
    	String orderBy, 
    	String direction, 
    	int start, 
    	int limit
    ) throws XmlServerException {
    	
    	try {
    		
	    	String xqFor ="";
	    	String xqWhere="";
	    	String xqOrderBy = "";
    		String xqReturn = "";
	    	
    		//build Pivots Map
    		LinkedHashMap<String,String> pivotsMap = new LinkedHashMap<String,String>();
    		int i=0;
			if (mainObjectRootElementName != null) pivotsMap.put(mainObjectRootElementName,"pivot"+(i++));
    			
	    	//Build the return using the vars
	    	Collection<String> c = viewableFullPaths;
	    	boolean moreThanOneViewable = viewableFullPaths.size()>1;
	    	for (Iterator<String> iter = c.iterator(); iter.hasNext(); ) {
				String bename = iter.next();
				String[] paths = bename.split("/");
				//retrieve the element name from the last path and remove any Xpath condition e.g. concept[toto='titi']
				String elementName = paths[paths.length-1].split("\\[")[0];
				String pivotPath = getPathFromPivots(bename, pivotsMap);
				//find pivot
				xqReturn+=
					(moreThanOneViewable ? "{" :"");
				if (elementName.startsWith("@")) { //attribute
					xqReturn+= "<"+elementName.substring(1)+">{string("+pivotPath+ ")}</"+elementName.substring(1)+">";
				} else {
					xqReturn+=
						"if ("+pivotPath+") then "+pivotPath+" else <"+elementName+"/>";
						//"if (empty("+pivotPath+"/*)) then <"+elementName+">{"+pivotPath+"/text()}</"+elementName+"> else  "+pivotPath;
				}
				xqReturn+=
					(moreThanOneViewable ? "}" :"");
	    	}
	    				
	    	// 	build from  WhereItem
	    	if (whereItem == null) 
	    		xqWhere="";
	    	else
	    		xqWhere = buildWhere("", pivotsMap, whereItem);
	    	
	    	//build order by
	    	if (orderBy == null) {
	    		orderBy = "";
	    	} else {
	    		orderBy = "order by "
	    					+getPathFromPivots(orderBy, pivotsMap)
	    					+(direction == null ? "" : " "+direction);
	    	}
	    	
			//build for
	    	for (Iterator<String> iter = pivotsMap.keySet().iterator(); iter.hasNext(); ) {
				String path = iter.next();
				//get the pivot name for this path
				String pivotName = pivotsMap.get(path);
				//get the concept
				String rootElementName = getRootElementNameFromPath(path);
				//determine revision
				String revisionID = objectRootElementNameToRevisionID.get(rootElementName);
				//determine cluster
				String clusterName = objectRootElementNameToClusterName.get(rootElementName);

				xqFor+="".equals(xqFor)?"for ": ", ";
				xqFor+="$"+pivotName+" in "+getXQueryCollectionName(revisionID, clusterName)+"/"+path;
	    	}
	    	
	    	
	    	String query =
	    		xqFor
	    		+("".equals(xqWhere)? "" : "\nwhere "+xqWhere)
	    		+("".equals(xqOrderBy) ? "" : "\n"+xqOrderBy)
	    		+"\nreturn "+(moreThanOneViewable ? "<result>":"")+xqReturn+(moreThanOneViewable ? "</result>":""); 
	    	
	    	if (start>=0 && limit>0) {
	    		query = 
	    			"let $leres := \n"+query
	    			+"\n return subsequence($leres,"+(start+1)+","+limit+")";
	    	}
	    	
	    	return query;
	    	
	    } catch (Exception e) {
     	    String err = "Unable to build the Item XQuery";
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
     	    throw new XmlServerException(err);
	    } 
    	
    	
    }
    
    public String getPivotIndexQuery(
			String clusterName, 
			String mainPivotName,
			LinkedHashMap<String, String[]> pivotWithKeys, 
			String[] indexPaths,
			IWhereItem whereItem, 
			String[] pivotDirections,
			String[] indexDirections, 
			int start, 
			int limit
	) throws XmlServerException{
    	
    	try {
    		
    		String query = "";
        	StringBuffer xq = new StringBuffer();
        	StringBuffer xqFor=new StringBuffer();
        	StringBuffer xqWhere=new StringBuffer();
        	StringBuffer xqOrderby=new StringBuffer();
        	StringBuffer xqReturn=new StringBuffer();
        	
        	HashSet<String> conceptMap=new HashSet<String>();
        	String[] pivotPaths=new String[pivotWithKeys.size()];
        	
        	//parse pivotWithKeys
        	int i=0;
        	for (Iterator iterator = pivotWithKeys.keySet().iterator(); iterator.hasNext();i++) {
    			String pivot = (String) iterator.next();
    			pivotPaths[i]=pivot;
    			String[] tmp=pivot.split("/");//TODO maybe care about other cases, like '//'
    			if(tmp.length>0)conceptMap.add(tmp[0]);
    		}
        	//for
        	if(conceptMap.size()>0){
        		xqFor.append("for ");
        		int j=0;
            	for (Iterator iterator = conceptMap.iterator(); iterator.hasNext();j++) {
        			String conceptName = (String) iterator.next();
        			xqFor.append("$").append(conceptName).append(" in collection(\"").append(clusterName).append("\")//").append(conceptName);
        			
        			if(j<conceptMap.size()-1){
        				xqFor.append(", ");
        			}else{
        				xqFor.append(" ");
        			}
        		}
        	}
        	
        	//where
        	if(pivotPaths.length>0){
        		xqWhere.append("where 1=1 ");
        		if(pivotPaths.length>1){
            		for (int k = 0; k < pivotPaths.length-1 ; k++) {
            			String[] k1keys=pivotWithKeys.get(pivotPaths[k+1]);
            			if(k1keys.length==1){
            				xqWhere.append(" and ($").append(pivotPaths[k]).append("=$").append(k1keys[0]).append(" or $").append(pivotPaths[k]).append("=concat('[',$").append(k1keys[0]).append(",']')) ");
            			}else if(k1keys.length>1){
            				xqWhere.append(" and $").append(pivotPaths[k]).append("=concat(");
            				for (int l = 0; l < k1keys.length; l++) {
            					if(l>0)xqWhere.append(",");
            					xqWhere.append("'['")
            					       .append(",$")
            					       .append(k1keys[l])
            					       .append(",']'");
        					}
            				xqWhere.append(") ");
            			}           
            		}
            	}
        	}
        	
            //build from  WhereItem
        	if (whereItem != null){
        			
        			HashMap<String,String> pivots=new HashMap<String,String>();
        			pivots.put(mainPivotName, mainPivotName);
    				xqWhere.append(buildWhere(" and ",pivots ,whereItem));
    				xqWhere.append(" ");
    			
        	}
    			
        	//order by
        	if(pivotPaths.length>0){
        		xqOrderby.append("order by ");
        		for (int m = pivotPaths.length-1; m > -1; m--) {
            		if(m<pivotPaths.length-1)xqOrderby.append(",");
            		xqOrderby.append("$").append(pivotPaths[m]).append(" ");
            		//add direction
            		if(pivotDirections!=null&&pivotDirections.length>0)xqOrderby.append(pivotDirections[m] == null ? "" : " "+pivotDirections[m]+" ");
        		}
        		
        		for (int m = 0; m < indexPaths.length; m++) {
        			xqOrderby.append(",").append("$").append(indexPaths[m]).append(" ");
        			//add direction
        			if(indexDirections!=null&&indexDirections.length>0)xqOrderby.append(indexDirections[m] == null ? "" : " "+indexDirections[m]+" ");
    			}
        	}
        	
        	//return
        	if(pivotPaths.length>0){
        		xqReturn.append("return ");
        		xqReturn.append("<result>");
        		for (int n = pivotPaths.length-1; n > -1; n--) {
        			xqReturn.append("{$").append(pivotPaths[n]).append("}");
    			}
        		for (int n = 0; n < indexPaths.length; n++) {
        			xqReturn.append("{$").append(indexPaths[n]).append("}");
    			}
        		
        		String[] mainKeys=pivotWithKeys.get(pivotPaths[0]);
        		for (int n = 0; n < mainKeys.length; n++) {
        			xqReturn.append("{$").append(mainKeys[n]).append("}");
    			}
        		xqReturn.append("</result>  ");
        	}
        	

        	xq.append(xqFor).append(xqWhere).append(xqOrderby).append(xqReturn);
        	query=xq.toString();
        	
        	if (start>=0 && limit>0) {
        		query = 
        			"let $list := \n"+query
        			+"\n return subsequence($list,"+(start+1)+","+limit+")";
        	}
        	
    		return query;
    		
    	} catch (Exception e) {
     	    String err = "Unable to build the PivotIndex XQuery";
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
     	    throw new XmlServerException(err);
	    }
    	
    }
	
	public long countXtentisObjects(
		HashMap<String, String> objectRootElementNameToRevisionID, 
		HashMap<String, String> objectRootElementNameToClusterName, 
		String objectFullPath, 
		IWhereItem whereItem
	) throws XmlServerException {
	        
    	try {
    		String xquery = null;
    		
    		//if the where Item is null, try to make a simplified x path query
    		if (whereItem == null) {
				//get the concept
				String rootElementName = getRootElementNameFromPath(objectFullPath);
				//determine revision
				String revisionID = objectRootElementNameToRevisionID.get(rootElementName);
				//determine cluster
				String clusterName = objectRootElementNameToClusterName.get(rootElementName);
				
				xquery = "count("+getXQueryCollectionName(revisionID, clusterName)+"/"+objectFullPath+")";
    		} else {
    			xquery = "let $zcount := ";
    			xquery += getXtentisObjectsQuery(
    				objectRootElementNameToRevisionID, 
    				objectRootElementNameToClusterName, 
    				null, 
    				new ArrayList<String>(Arrays.asList(new String[] {objectFullPath})),
    				whereItem, 
    				null, 
    				null, 
    				0, 
    				-1
    			);
    			xquery +="\n return count($zcount)";
    		}
    		
    		ArrayList<String> results = runQuery(null, null, xquery, null);
    		
    		return Long.parseLong(results.get(0));
    		
        } catch (Exception e) {
     	    String err = "Unable to count the objects using path '"+objectFullPath+"'";
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
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
	private String getPathFromPivots(String bename, HashMap<String,String> pivots)  throws XmlServerException{
		try {
			org.apache.log4j.Logger.getLogger(this.getClass()).trace("getPathFromPivots() "+bename+" - "+pivots.keySet());
			if (bename.startsWith("/")) bename = bename.substring(1);
			String beRoot = bename.split("/")[0];
			//find pivot
			Set<String> ps = pivots.keySet();
			String newPath = null;
			for (Iterator<String> iterator = ps.iterator(); iterator.hasNext(); ) {
				String pivot = iterator.next();
				String pivotRoot = pivot.split("/")[0];
				if (beRoot.equals(pivotRoot)) {
					//found
					newPath = "$"+pivots.get(pivot)+getPathFromPivot(pivot, bename);
					break;
				}
			}
			if (newPath == null) {
				//add pivot
				String var = "pivot"+pivots.size();
				pivots.put(bename, var);
				newPath = "$"+var;
			}
			return newPath;
	    } catch (Exception e) {
     	    String err = "Unable to get the path "+bename+" from the pivots"
     	    		+": "+e.getLocalizedMessage();
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
     	    throw new XmlServerException(err);
	    } 
	}	
	

	private String buildWhere(
			String where,
			HashMap<String,String> pivots,
			IWhereItem whereItem
		) throws XmlServerException{
		try {
			if (whereItem instanceof WhereLogicOperator) {
				Collection<IWhereItem> subItems = ((WhereLogicOperator)whereItem).getItems();
				if (subItems.size() == 0) throw new XmlServerException("The logic operator must contain at least one element");
				if (subItems.size() == 1) return  //unnecessary AND or OR 
					buildWhere(
						where,
						pivots,
						subItems.iterator().next()
				);
				int i=0;
				for (Iterator<IWhereItem> iter = subItems.iterator(); iter.hasNext(); ) {
					IWhereItem item = iter.next();
					if (++i>1) 
						if (((WhereLogicOperator)whereItem).getType() == WhereLogicOperator.AND)
							where+=" and (";
						else
							where+=" or (";
					else
						where+="(";
					where = buildWhere(where, pivots, item)+")";
				}//for
				return where;
					
			} else if(whereItem instanceof WhereCondition) {
				WhereCondition condition = (WhereCondition) whereItem;
				where+=buildWhereCondition(condition,pivots);
	            return where;
			} else {
				throw new XmlServerException("Unknown Where Type : "+whereItem.getClass().getName());
			}
	    } catch (Exception e) {
     	    String err = "Unable to build the XQuery Where Clause "
     	    		+": "+e.getLocalizedMessage();
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
     	    throw new XmlServerException(err);
	    } 
	}
	
	/**
	 * Build a where condition in XQuery using paths relative to the provided list of pivots
	 */
	public String buildWhereCondition(WhereCondition wc, HashMap<String,String> pivots) throws XmlServerException{
		try {
			
			//all this is EXIST specific
			
			String where = "";
			String operator = wc.getOperator();			

			//numeric detection
			boolean isNum = false;
			//handle case of String starting with a zero e.g. 00441065 or ending with . e.g. 12345.
			if (!(
					wc.getRightValueOrPath().matches(".*\\D")
					|| wc.getRightValueOrPath().startsWith("0")
					|| wc.getRightValueOrPath().endsWith(".")
					|| wc.getRightValueOrPath().startsWith("+")
					|| wc.getRightValueOrPath().startsWith("-")
			)){
				try { 
					Double.parseDouble(wc.getRightValueOrPath().trim()); 
					isNum = true;
				} catch (Exception e) {}
			}
			
			//String encoded = wc.getRightValueOrPath().replaceAll("\\&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			String encoded = StringEscapeUtils.escapeXml(wc.getRightValueOrPath());
			
			if(operator.equals(WhereCondition.CONTAINS)) {
				String predicate = wc.getStringPredicate();
				//check if the left path is an attribute or an element
				String path = wc.getLeftPath();
				if (path.endsWith("/")) path = path.substring(0, wc.getLeftPath().length()-1);
				String[] nodes = path.split("/");
				boolean isAttribute = nodes[nodes.length-1].startsWith("@");
				
				if ((predicate==null) || predicate.equals(WhereCondition.PRE_NONE)) {
					if (isAttribute) {
						where = 
							getPathFromPivots(wc.getLeftPath(), pivots)+" &= \""+encoded+"\" ";						
					} else {
						where = 
							"("+getPathFromPivots(wc.getLeftPath(), pivots)+"/descendant-or-self::* &= \""+encoded+"\") "+
							"or ("+getPathFromPivots(wc.getLeftPath(), pivots)+"/descendant-or-self::*/attribute() &= \""+encoded+"\") ";
					}
				} else	 if (predicate.equals(WhereCondition.PRE_AND)) {
					if (isAttribute) {
						where = 
							getPathFromPivots(wc.getLeftPath(), pivots)+" &= \""+encoded+"\" ";						
					} else {
						where =
							"("+getPathFromPivots(wc.getLeftPath(), pivots)+"/descendant-or-self::* &= \""+encoded+"\") "+
							"or ("+getPathFromPivots(wc.getLeftPath(), pivots)+"/descendant-or-self::*/attribute() &= \""+encoded+"\") ";
					}
				} else if (predicate.equals(WhereCondition.PRE_EXACTLY)) { 
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" eq \""+encoded+"\"";
				} else if (predicate.equals(WhereCondition.PRE_STRICTAND)) {
					where = "near("+getPathFromPivots(wc.getLeftPath(), pivots)+", \""+encoded+"\",1)";
				} else	if (predicate.equals(WhereCondition.PRE_OR)) {
					if (isAttribute) {
						where = 
							getPathFromPivots(wc.getLeftPath(), pivots)+" |= \""+encoded+"\" ";						
					} else {
						where = 
							"("+getPathFromPivots(wc.getLeftPath(), pivots)+"/descendant-or-self::* |= \""+encoded+"\") "+
							"or ("+getPathFromPivots(wc.getLeftPath(), pivots)+"/descendant-or-self::*/attribute() |= \""+encoded+"\") ";
					}
				} else	if (predicate.equals(WhereCondition.PRE_NOT)) {
					if (isAttribute) {
						where = 
							"not("+getPathFromPivots(wc.getLeftPath(), pivots)+" &= \""+encoded+"\") ";						
					} else {
						where = 
							"not("+
								"("+getPathFromPivots(wc.getLeftPath(), pivots)+"/descendant-or-self::* &= \""+encoded+"\") "+
								"or ("+getPathFromPivots(wc.getLeftPath(), pivots)+"/descendant-or-self::*/attribute() &= \""+encoded+"\") "+
							")";
					}
				}
				/* WAITING FOR FIX FROM EXIST
				if ((predicate==null) || predicate.equals(WhereCondition.PRE_NONE)) 
					where = "contains("+getPathFromPivots(wc.getLeftPath(), pivots)+",\""+encoded+"\")";
				else	 if (predicate.equals(WhereCondition.PRE_AND)) 
					where = "contains("+getPathFromPivots(wc.getLeftPath(), pivots)+",\""+encoded+"\")";
				else if (predicate.equals(WhereCondition.PRE_EXACTLY)) 
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" eq \""+encoded+"\"";
				else if (predicate.equals(WhereCondition.PRE_STRICTAND))
					where = "near("+getPathFromPivots(wc.getLeftPath(), pivots)+", \""+encoded+"\",1)";
				else	if (predicate.equals(WhereCondition.PRE_OR)) 
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" |= \""+encoded+"\"";
				else	if (predicate.equals(WhereCondition.PRE_NOT)) 
					where = "not(contains("+getPathFromPivots(wc.getLeftPath(), pivots)+",\""+encoded+"\"))";
				*/

			} else if(operator.equals(WhereCondition.STRICTCONTAINS)) { 
				where = "near("+getPathFromPivots(wc.getLeftPath(), pivots)+", \""+encoded+"\",1)"; 
			} else if(operator.equals(WhereCondition.STARTSWITH)) { 
				where = "near("+getPathFromPivots(wc.getLeftPath(), pivots)+", \""+encoded+"*\",1)";
			} else if(operator.equals(WhereCondition.JOINS)) { 
				where = getPathFromPivots(wc.getRightValueOrPath(),pivots)+" = "+getPathFromPivots(wc.getLeftPath(), pivots);
			} else	 if(operator.equals(WhereCondition.EQUALS)) {
				if (isNum) {
					where = "number("+getPathFromPivots(wc.getLeftPath(), pivots)+") eq "+encoded;
				} else  {
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" eq \""+encoded+"\"";
				}
			} else if(operator.equals(WhereCondition.NOT_EQUALS)) {
				if (isNum) {
					where = "number("+getPathFromPivots(wc.getLeftPath(), pivots)+") ne "+encoded;
				} else {
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" ne \""+encoded+"\"";
				}
			} else	 if(operator.equals(WhereCondition.GREATER_THAN)) {
				if (isNum) {
					where = "number("+getPathFromPivots(wc.getLeftPath(), pivots)+") gt "+encoded;
				} else {
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" gt \""+encoded+"\"";
				}
			} else	if(operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) { 
				if (isNum) {
					where = "number("+getPathFromPivots(wc.getLeftPath(), pivots)+") ge "+encoded;
				} else {
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" ge \""+encoded+"\"";
				}
			} else if(operator.equals(WhereCondition.LOWER_THAN)) { 
				if (isNum) {
					where = "number("+getPathFromPivots(wc.getLeftPath(), pivots)+") lt "+encoded;
				} else {
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" lt \""+encoded+"\"";
				}
			} else	if(operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) { 
				if (isNum) {
					where = "number("+getPathFromPivots(wc.getLeftPath(), pivots)+") le "+encoded;
				} else {
					where = getPathFromPivots(wc.getLeftPath(), pivots)+" le \""+encoded+"\"";
				}
			} else	if(operator.equals(WhereCondition.NO_OPERATOR)) {
				where = getPathFromPivots(wc.getLeftPath(), pivots);
			}

			return where;
			
	    } catch (Exception e) {
     	    String err = "Unable to build the Where Condition "
     	    		+": "+e.getLocalizedMessage();
     	    org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
     	    throw new XmlServerException(err);
	    } 
		
	}
	
	/**
	 * Build a relative path to the provided pivot using the absolute path provided.<br/>
	 * <br/>
	 * Say the pivot is referencing <code>Country/name</code>, the path <code>Country/name/EN</code>
	 * will become <code>$pivot0/EN</code> 
	 * 
	 */
	private String getPathFromPivot(String pivot, String path) throws XmlServerException {
		try {
			
			//org.apache.log4j.Logger.getLogger(this.getClass()).debug("getPathFromPivot() "+path+" from "+pivot);
			
			if ((pivot==null) || (path==null)) return null;
			
			if (pivot.startsWith("/")) pivot = pivot.substring(1);
			if (pivot.endsWith("/")) pivot = pivot.substring(0,pivot.length()-1);
			if (path.startsWith("/")) path = path.substring(1);
			if (path.endsWith("/")) path = path.substring(0,path.length()-1);
			
			String[] pivotPaths = pivot.split("/");
			String[] pathPaths = path.split("/");
			
			if (!pivotPaths[0].equals(pathPaths[0])) return null;
			
			String newPath = "";
			int matching = 0;
			for (int i = 1; i < pivotPaths.length; i++) {
				if (i<pathPaths.length)
					if (pivotPaths[i].equals(pathPaths[i]))
						matching++;
					else
						newPath+="/..";
				else
					newPath+="/..";
			}
			for (int i = matching+1; i < pathPaths.length; i++) {
				newPath+="/"+pathPaths[i];
			}
			
			//fix for eXist bug that has *sometimes* difficulties with "grand parents" (../..)
			//e.g. x/../../y  --> x/.././../y
			newPath = newPath.replaceAll("\\.\\./\\.\\.", ".././..");
			
			return newPath;
			
	    } catch (Exception e) {
	 	    String err = "Unable to get the path "+path+" from pivot "+pivot
	 	    		+": "+e.getLocalizedMessage();
	 	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+this.getClass()).info(err,e);
	 	    throw new XmlServerException(err);
	    } 
	}
	
    
	/*********************************************************************************
	 * Lifecycle
	 *********************************************************************************/
	
	/* (non-Javadoc)
	 * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doActivate()
	 */
	public void doActivate() throws XmlServerException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("doActivate() ");
		//registerDBManager();
		
	}


	/* (non-Javadoc)
	 * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doCreate()
	 */
	public void doCreate() throws XmlServerException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("doCreate() ");
		//registerDBManager();
		
	}


	/* (non-Javadoc)
	 * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doPassivate()
	 */
	public void doPassivate() throws XmlServerException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("doPassivate() ");
		try {
			Set<String> keys = this.clusters.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
				String clusterName = iter.next();
				org.xmldb.api.base.Collection collection = clusters.get(clusterName);
				collection.close();
			}
			this.clusters = new HashMap<String, org.xmldb.api.base.Collection>();
		} catch (Exception e) {
			throw new XmlServerException(e);
		}
		
	}


	/* (non-Javadoc)
	 * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doPostCreate()
	 */
	public void doPostCreate() throws XmlServerException {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle#doRemove()
	 */
	public void doRemove() throws XmlServerException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("doRemove() ");
		try {
			Set<String> keys = this.clusters.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
				String clusterName = iter.next();
				org.xmldb.api.base.Collection collection = clusters.get(clusterName);
				try {collection.close();} catch (Exception x) {}
			}
		} catch (Exception e) {
			throw new XmlServerException(e);
		}
	}	
	

	
	/***********************************************************************
	 * 
	 * Helper Methods
	 * 
	 ***********************************************************************/

	private static Pattern pathWithoutConditions = Pattern.compile("(.*?)[\\[|/].*");
	/**
	 * Returns the first part - eg. the concept - from the path
	 * @param path
	 * @return the Concept
	 */
    public static String getRootElementNameFromPath(String path) {
    	if (!path.endsWith("/")) path+="/";
    	Matcher m = pathWithoutConditions.matcher(path);
    	if (m.matches()) {
    		return m.group(1);
    	} else {
    		return null;
    	}
    }
	
	
    private static String getXQueryCollectionName(String revisionID, String clusterName) throws XmlServerException {
    	String collectionPath = 
    		(revisionID == null || "".equals(revisionID) ? "" : "R-"+revisionID+"/")
    		+(clusterName == null ? "" : clusterName);
    	
    	if ("".equals(collectionPath)) return "";
    	
    	String encoded = null;
        try {
	        encoded = URLEncoder.encode(collectionPath,"utf-8");
        } catch (UnsupportedEncodingException unlikely) {
	        String err = "Unable to encode the collection path '"+collectionPath+"'. UTF-8 is not suported ?!?!";
	        throw new XmlServerException(err);
        }
    	// java.net.URLEncoder encodes space (' ') as a plus sign ('+'),
    	// instead of %20 thus it will not be decoded properly by eXist when the
    	// request is parsed. Therefore replace all '+' by '%20'.
    	// If there would have been any plus signs in the original string, they would
    	// have been encoded by URLEncoder.encode()
    	// control = control.replace("+", "%20");//only works with JDK 1.5
    	encoded = encoded.replaceAll("\\+", "%20");
    	//%2F seems to be useless
    	encoded = encoded.replaceAll("%2F", "/");
   	
    	return "collection(\""+encoded+"\")";
    }
    
	protected static boolean PROCESSING_UPGRADE = false; 
	
	/**
	 * This is called once on the first get Collection call to check that the server is OK
	 * Upgrade has been removed since we are starting from a clean state
	 * @throws XmlServerException
	 */
	private void checkMe() throws XmlServerException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("checkMe() ");
		
		//processing upgrade code
		
		//if processing - wait
//		while(PROCESSING_UPGRADE) try {Thread.sleep(2000);} catch (InterruptedException e){};
		
		SERVER_STATE_OK = true;

	}
	
	
	

}