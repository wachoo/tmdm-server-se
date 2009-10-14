package com.amalto.core.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
import com.amalto.xmlserver.interfaces.WhereOr;
import com.amalto.xmlserver.interfaces.XmlServerException;

/**
 * All applications must call the methods of this wrapper only They never
 * directly call the underlying API
 * 
 * @author bgrieder
 * 
 * @ejb.bean 
 * 			name="XmlServerSLWrapper" 
 * 			display-name="XML:DB Stateless Wrapper"
 *      	description="Description for XML:DB Stateless Wrapper"
 *         	jndi-name="amalto/remote/xmldb/xmlserverslwrapper" 
 *         	local-jndi-name = "amalto/local/xmldb/xmlserverslwrapper" 
 *         	type="Stateless"
 *         	view-type="both"
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 *
 *  
 * 
 * @ejb.remote-facade
 */
public class XmlServerSLWrapperBean implements SessionBean {
	
    public static String SERVERCLASS;
	
	{
		SERVERCLASS = MDMConfiguration.getConfiguration().getProperty("xmlserver.class");
		if ((SERVERCLASS==null) || "".equals(SERVERCLASS)) SERVERCLASS = "com.amalto.xmldb.XmldbSLWrapper";
	}

	 
	//The underlying server
	//TODO: the underlying server is not serializable.....
	IXmlServerSLWrapper server = null;

	/**
	 * XmlServerSLWrapperBean.java Constructor
	 * 
	 */
	public XmlServerSLWrapperBean() {
		super();
	}

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() throws EJBException, RemoteException {
    	try {
	    	if (server instanceof IXmlServerEBJLifeCycle) {
	    		((IXmlServerEBJLifeCycle)server).doRemove();
	    	}
    	} catch (Exception e) {
    		throw new EJBException(e.getLocalizedMessage());
    	}
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
    	try{
	    	if (server instanceof IXmlServerEBJLifeCycle) {
	    		((IXmlServerEBJLifeCycle)server).doActivate();
	    	}
    	} catch (Exception e) {
    		throw new EJBException(e.getLocalizedMessage());
    	}

    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
    	try {
	    	if (server instanceof IXmlServerEBJLifeCycle) {
	    		((IXmlServerEBJLifeCycle)server).doPassivate();
	    	}
    	} catch (Exception e) {
    		throw new EJBException(e.getLocalizedMessage());
    	}	    	
    }
    
    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    	try {
	        server = (IXmlServerSLWrapper) Class.forName(SERVERCLASS).newInstance();
	    	if (server instanceof IXmlServerEBJLifeCycle) {
	    		((IXmlServerEBJLifeCycle)server).doCreate();
	    	}
    	}catch (Exception e) {
	    	throw new CreateException(e.getLocalizedMessage());
	    }
    }
    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {
    	try {
	    	if (server instanceof IXmlServerEBJLifeCycle) {
	    		((IXmlServerEBJLifeCycle)server).doPostCreate();
	    	}
    	} catch (Exception e) {
    		throw new CreateException(e.getLocalizedMessage());
    	}
   	}
    
	/***************************************************************************
	 * 
	 * DETECTION
	 * 
	 **************************************************************************/
	/**
	 * Is the server up?
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public boolean isUpAndRunning() {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("isUpAndRunning() ");
		return server.isUpAndRunning();
	}

    
    
	/***************************************************************************
	 * 
	 * C L U S T E R S
	 * 
	 **************************************************************************/

	/**
	 * Get all clusters for a particular revision
	 * @param revisionID
	 * 		The ID of the revision, <code>null</code> for the head
	 * @return the list of cluster IDs
	 *  
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String[] getAllClusters(String revisionID) throws XtentisException {
		try {
			return server.getAllClusters(revisionID);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}
	/**
	 * clear the item cache
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public void clearCache() throws XtentisException {		
		server.clearCache();
	}
	
	/**
	 * Delete a cluster for particular revision
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @param clusterName
	 * 			The name of the cluster
	 * @return the milliseconds to perform the operation
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long deleteCluster(String revisionID, String clusterName) throws XtentisException {
		try {
			return server.deleteCluster(revisionID, clusterName);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	/**
	 * Delete All clusters for a particular revision
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @return the milliseconds to perform the operation
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long deleteAllclusterNames(String revisionID) throws XtentisException {
		try {
			return server.deleteAllClusters(revisionID);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	/**
	 * Create a cluster for a particular revision
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @param clusterName
	 * 			The name of the cluster
	 * @return the milliseconds to perform the operation
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long createCluster(String revisionID, String clusterName) throws XtentisException {
		try {
			return server.createCluster(revisionID, clusterName);	
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}	
	}


	/***************************************************************************
	 * 
	 * D O C U M E N T S
	 * 
	 **************************************************************************/
	
	/**
	 * Reads a document from a file and stores it in the DB 
	 * @param fileName
	 * 			The full path of the file
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @return the milliseconds to perform the operation
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID) throws XtentisException {
		try {
			return server.putDocumentFromFile(fileName, uniqueID, clusterName, revisionID);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	/**
	 * Reads a document from a file and stores it in the DB
	 * @param fileName
	 * 			The full path of the file
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @param documentType
	 * 			"DOCUMENT" for and XML document, "BINARY" otherwise
	 * @return the milliseconds to perform the operation
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long putDocumentFromFile(
		String fileName, 
		String uniqueID, 
		String clusterName,
		String revisionID,
		String documentType
	) throws XtentisException {
		try {
			return server.putDocumentFromFile(fileName, uniqueID, clusterName, revisionID, documentType);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}


	/**
	 * Read a document from s String an store it in the DB as "DOCUMENT"
	 * @param xmlString
	 * 			The xml string
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @return the time to store the document
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID) throws XtentisException {
		try {
			return server.putDocumentFromString(xmlString, uniqueID, clusterName, revisionID);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	/**
	 * Read a document from a String and store it in the DB
	 * @param string
	 * 			The string to store
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @param documentType
	 * 			"DOCUMENT" for and XML document, "BINARY" otherwise
	 * @return the time to store the document
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long putDocumentFromString(
		String string, 
		String uniqueID, 
		String clusterName, 
		String revisionID,
		String documentType 
	) throws XtentisException {
		try {
			return server.putDocumentFromString(string, uniqueID, clusterName, revisionID , documentType);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}
	
	/**
	 * Stores a document from its DOM root element
	 * @param root
	 * 			The DOM root element
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @return the time to store the document
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XtentisException {
		try {
			return server.putDocumentFromDOM(root, uniqueID, clusterName, revisionID);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}


	/**
	 * Gets an XML document from the DB<br>
	 * The XML instruction will have an encoding specified as UTF-16
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @return the document as A string
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XtentisException {
		try {
			return server.getDocumentAsString(revisionID, clusterName, uniqueID);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	/**
	 * Gets an XML document from the DB<br>
	 * The XML instruction will have the encoding specified in the encoding parameter<br>
	 * If encoding is null, the document will not have an XML instruction
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @param encoding
	 * 			The encoding of the XML instruction (e.g. UTF-16, UTF-8, etc...).
	 * @return the document as A string
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XtentisException {
		try {
			return server.getDocumentAsString(revisionID, clusterName, uniqueID, encoding);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}
	
	/**
	 * Gets the bytes of a document from the DB<br>
	 * For an xml "DOCUMENT" type, the bytes will be encoded using UTF-16
	 * 
	 * The XML instruction will have an encoding specified as UTF-16
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @param documentType
	 * 			"DOCUMENT" for and XML document, "BINARY" otherwise
	 * @return the document as A string
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType) throws XtentisException {
		try {
			return server.getDocumentBytes(revisionID, clusterName, uniqueID, documentType);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	/**
	 * The list of documents unique ids in a cluster of a particular revision
	 * 
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @return the list of document unique IDs
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XtentisException {
		try {
			return server.getAllDocumentsUniqueID(revisionID, clusterName);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	/**
	 * Delete an XML  document
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @return the time to perform the delete
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long deleteDocument(String revisionID, String clusterName, String uniqueID) throws XtentisException {
		return deleteDocument(revisionID, clusterName, uniqueID, IXmlServerSLWrapper.TYPE_DOCUMENT);
	}

	/**
	 * Delete a  document
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param clusterName
	 * 			The unique ID of the cluster
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> for the head
	 * @return the time to perform the delete
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType) throws XtentisException {
		try {
			return server.deleteDocument(revisionID, clusterName, uniqueID, documentType);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	
	
	/**
	 * Delete Xtentis Objects matching a particular condition<br> 
	 * @param objectRootElementNameToRevisionID
	 * 			A map that gives the revision ID of an Object XML Root Element Name
	 * @param objectRootElementNameToClusterName
	 * 			An ordered map that gives the cluster name of an Object XML Root Element Name
	 * @param objectRootElementName
	 * 			The objectType (its name)
	 * @param whereItem
	 * 			The condition
	 * @return the number of items deleted
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public int deleteXtentisObjects(
		HashMap<String, String> objectRootElementNameToRevisionID,
		HashMap<String, String> objectRootElementNameToClusterName,
		String objectRootElementName, 
		IWhereItem whereItem
	) throws XtentisException {
		try {
			return server.deleteXtentisObjects(objectRootElementNameToRevisionID, objectRootElementNameToClusterName, objectRootElementName, whereItem);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}
	
	/**
	 * Delete Items matching a particular condition<br> 
	 * @param conceptPatternsToClusterName
	 * 			An ordered map that gives the cluster name of a Concept when matching the first pattern found
	 * @param conceptPatternsToRevisionID
	 * 			An ordered map that gives the revision ID of a Concept when matching the first pattern found
	 * @param conceptName
	 * 			The Concept of the items being deleted
	 * @param whereItem
	 * 			The condition
	 * @return the number of items deleted
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public int deleteItems(
		LinkedHashMap<String, String> conceptPatternsToRevisionID, 
		LinkedHashMap<String, String> conceptPatternsToClusterName, 
		String conceptName, 
		IWhereItem whereItem
	) throws XtentisException {
		try {
			return server.deleteItems(conceptPatternsToRevisionID, conceptPatternsToClusterName, conceptName, whereItem);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	

	/**
	 * Move a document between two clusters of particular revision
	 * @param uniqueID
	 * 			The unique ID of the document
	 * @param sourceclusterName
	 * 			The unique ID of the source cluster
	 * @param sourceRevisionID
	 * 			The ID of the source revision
	 * @param targetclusterName
	 * 			The unique ID of the target cluster
	 * @param targetRevisionID
	 * 			The ID of the target revision
	 * @return the time to perform the move
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID, String targetclusterName) throws XtentisException {
		try {
			return server.moveDocumentById(sourceRevisionID, sourceclusterName, uniqueID, targetRevisionID, targetclusterName);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}

	/**
	 * Count Items based on conditions
	 * @param conceptPatternsToRevisionID
	 * 			A map that gives the revision ID of a pattern matching a concept name Concept (isItemQuery is true) or Xtentis Object (isItemQuery is false)
	 * @param conceptPatternsToClusterName
	 * 			An ordered map that gives the cluster name of a Concept when matching the first pattern found
	 * @param conceptName
	 * 			The name of the concept 
	 * @param whereItem 
	 * 			The condition to apply
	 * @return the number of items meeting the conditions
	 * @throws XtentisException
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long countItems(
		LinkedHashMap<String, String> conceptPatternsToRevisionID,
		LinkedHashMap<String, String> conceptPatternsToClusterName,
		String conceptName,
		IWhereItem whereItem
	) throws XtentisException {
		try {
	        return server.countItems(conceptPatternsToRevisionID, conceptPatternsToClusterName, conceptName, whereItem);
        } catch (XmlServerException e) {
	        throw new XtentisException(e.getMessage());
        }
	}
	
	/**
	 * Count Xtentis Objects based on conditions
	 * @param objectRootElementNameToRevisionID
	 * 			A map that gives the revision ID of an Xtentis Object based on its XML Root Element Name 
	 * @param objectRootElementNameToClusterName
	 * 			An ordered map that gives the cluster name of an Object based on its XML Root Element Name
	 * @param mainObjectRootElementName
	 * 			An optional object XML root element name that will serve as the main pivot<br/>
	 * 			If not specified, the pivots will be in ordered of those in the viewableBusinessElements
	 * @param whereItem 
	 * 			The condition to apply
	 * @return the number of items meeting the conditions
	 * @throws XtentisException
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public long countXtentisObjects(
		HashMap<String, String> objectRootElementNameToRevisionID,
		HashMap<String, String> objectRootElementNameToClusterName,
		String mainObjectRootElementName,
		IWhereItem whereItem
	) throws XtentisException {
		try {
	        return server.countXtentisObjects(objectRootElementNameToRevisionID, objectRootElementNameToClusterName, mainObjectRootElementName, whereItem);
        } catch (XmlServerException e) {
	        throw new XtentisException(e.getMessage());
        }
	}
	
	/**
	 * Performs a query on the db with optional parameters<br>
	 * The parameters are specified as %n in the query where n is the parameter number starting with 0
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> to run from the head
	 * @param clusterName
	 * 			The unique ID of the cluster,  <code>null</code> to run from the head of the revision ID
	 * @param query 
	 * 			The query in the native language
	 * @param parameters 
	 * 			The parameter values to replace the %n in the query before execution
	 * @return the result of the Query as a Collection of Strings
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XtentisException {
		try {
			return server.runQuery(revisionID, clusterName, query, parameters);
		} catch (XmlServerException e) {
			throw new XtentisException(e.getMessage());
		}
	}
	
	/**
	 * Builds a query in the native language of the DB (for instance XQuery) based on conditions
	 * @param objectRootElementNameToRevisionID
	 * 			A map that gives the revision ID of an Object XML Root Element Name
	 * @param objectRootElementNameToClusterName
	 * 			An ordered map that gives the cluster name of an Object XML Root Element Name
	 * @param viewableObjectElements 
	 * 			The full XPath (starting with the Object root element name) 
	 * 			of the elements and their sub elements that constitute the top elements of the returned documents
	 * @param mainObjectRootElementName
	 * 			An optional object type that will serve as the main pivot<br/>
	 * 			If not specified, the pivots will be in ordered of those in the viewableObjectElements
	 * @param whereItem 
	 * 			The condition to apply
	 * @param orderBy
	 * 			The path of the element to order by. <code>null</code> to avoid ordering
	 * @param direction
	 * 			If orderBy is not <code>null</code>, the direction. One of 
	 * @param start
	 * 			The index of the first element to return (start at 0)
	 * @param limit
	 * 			The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no limit
	 * @return the XQuery in the native language of the database
	 * @throws XtentisException
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String getXtentisObjectsQuery(
		HashMap<String, String> objectRootElementNameToRevisionID,
		HashMap<String, String> objectRootElementNameToClusterName,
		String mainObjectRootElementName,
		ArrayList<String> viewableObjectElements,	
		IWhereItem whereItem,
		String orderBy,
		String direction,
		int start,
		int limit
	) throws XtentisException {
		try {
	        return server.getXtentisObjectsQuery(
	        	objectRootElementNameToRevisionID, 
	        	objectRootElementNameToClusterName,
	        	mainObjectRootElementName,
	        	viewableObjectElements, 
	        	whereItem, 
	        	orderBy, 
	        	direction, 
	        	start, 
	        	limit
	        );
        } catch (XmlServerException e) {
	        throw new XtentisException("Unable to get the Xtentis Objects Query "+e.getMessage());
        }
	}
	
	/**
	 * Builds an Items query in the native language of the DB (for instance XQuery) based on conditions
	 * @param conceptPatternsToRevisionID
	 * 			A map that gives the revision ID of a pattern matching a concept name Concept (isItemQuery is true) or Xtentis Object (isItemQuery is false)
	 * @param conceptPatternsToClusterName
	 * 			An ordered map that gives the cluster name of a Concept when matching the first pattern found
	 * @param forceMainPivot
	 * 			An optional pivot that will appear first in the list of pivots in the query<br>:
	 * 			This allows forcing cartesian products: for instance Order Header vs Order Line 
	 * @param viewableFullPaths 
	 * 			The Full xPaths (starting with concept name) of the elements and their sub elements 
	 * 			that constitute the top elements of the returned documents
	 * @param whereItem 
	 * 			The condition to apply
	 * @param orderBy
	 * 			The path of the element to order by. <code>null</code> to avoid ordering
	 * @param direction
	 * 			If orderBy is not <code>null</code>, the direction. One of 
	 * @param start
	 * 			The index of the first element to return (start at 0)
	 * @param limit
	 * 			The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no limit
	 * @param spellThreshold
	 * 			Spell check the whereItem if threshold is greater than zero. The setting is ignored is this not an item query.
	 * @return the xquery in the native language of the db
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String getItemsQuery(
		LinkedHashMap<String, String> conceptPatternsToRevisionID,
		LinkedHashMap<String, String> conceptPatternsToClusterName,
		String forceMainPivot,
		ArrayList<String> viewableFullPaths,	
		IWhereItem whereItem,
		String orderBy,
		String direction,
		int start,
		int limit,
		int spellThreshold
	)throws XtentisException {

		try {
			
			//Spell check the where Item is this is an item query an the threshold is greater than zero
			if ((spellThreshold>0) && (whereItem != null)) {
				whereItem = spellCheckWhere(conceptPatternsToRevisionID, conceptPatternsToClusterName, whereItem, spellThreshold);
			}
			
			String q =  server.getItemsQuery(
				conceptPatternsToRevisionID, 
				conceptPatternsToClusterName, 
				forceMainPivot,
				viewableFullPaths, 
				whereItem, 
				orderBy, 
				direction, 
				start, 
				limit
			);
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getQuery():\n "+q);
			return q;
		} catch (XtentisException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisException("Unable to build the query: "+e.getLocalizedMessage());
		}
	}
	
	/**
	 * @param clusterName
	 * @param mainPivotName
	 * @param pivotWithKeys
	 * @param itemsRevisionIDs
	 * @param defaultRevisionID
	 * @param indexPaths
	 * @param whereItem
	 * @param pivotDirections
	 * @param indexDirections
	 * @param start
	 * @param limit
	 * @return
	 * @throws XtentisException
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String getPivotIndexQuery(
			String clusterName, 
			String mainPivotName,
			LinkedHashMap<String, String[]> pivotWithKeys,
			LinkedHashMap<String, String> itemsRevisionIDs,
			String defaultRevisionID,
			String[] indexPaths,
			IWhereItem whereItem, 
			String[] pivotDirections,
			String[] indexDirections, 
			int start, 
			int limit
	) throws XtentisException{
		try {
	        return server.getPivotIndexQuery(
	        	clusterName, 
	        	mainPivotName,
	        	pivotWithKeys,
	        	itemsRevisionIDs,
	        	defaultRevisionID,
	        	indexPaths, 
	        	whereItem, 
	        	pivotDirections, 
	        	indexDirections,
	        	start, 
	        	limit
	        );
        } catch (XmlServerException e) {
	        throw new XtentisException("Unable to get the Xtentis Objects Query "+e.getMessage());
        }
	}
	
	/**
	 * 
	 * Spell check is implemented by replacing any word that has matches in the dictionary
	 * by an or clause with all the matches
	 * 
	 * @param clusterName
	 * 			The cluster unique ID
	 * @param item
	 * 			The {@link IWhereItem} to spell check
	 * @param spellThreshold
	 * 			The sell Threshold - 0 or less de-activates spell
	 * @return the spellcheck whereItem
	 * @throws XtentisException
	 */
	private IWhereItem spellCheckWhere(
		LinkedHashMap<String, String> conceptPatternsToRevisionID,
		LinkedHashMap<String, String> conceptPatternsToClusterName,
		IWhereItem item, int spellThreshold
	) throws XtentisException{
		org.apache.log4j.Logger.getLogger(this.getClass()).trace(
				"spellCheckWhere() "
				+(((item!=null) && (item instanceof WhereCondition)) ? ((WhereCondition)item).toString():"")
		);
		
		HashMap<String, DataClusterPOJO> clusterCache = new HashMap<String, DataClusterPOJO>();
		
		try {
			if (item==null) return null;
			if (spellThreshold <=0) return item;
			
			if (item instanceof WhereLogicOperator) {
				WhereLogicOperator op = new WhereLogicOperator(((WhereLogicOperator) item).getType());
				Collection<IWhereItem> c = ((WhereLogicOperator) item).getItems();
				for (Iterator<IWhereItem> iter = c.iterator(); iter.hasNext(); ) {
					op.add(spellCheckWhere(conceptPatternsToRevisionID, conceptPatternsToClusterName, iter.next(), spellThreshold));	
				}
				return op;
			} else if (item instanceof WhereCondition) {
				WhereCondition wc = (WhereCondition) item;
				if (wc.isSpellCheck() &&  (!WhereCondition.JOINS.equals(wc.getOperator()))) {
					Collection<String> sentences=new ArrayList<String>();
					//get the data revision ID
					String conceptName = ItemPOJO.getConceptFromPath(wc.getLeftPath());
					String revisionID = null;
					Set<String> revisionPatterns = conceptPatternsToRevisionID.keySet();
					for (Iterator<String> iterator = revisionPatterns.iterator(); iterator.hasNext(); ) {
						String pattern = iterator.next();
						if (conceptName.matches(pattern)) {
							revisionID = conceptPatternsToRevisionID.get(pattern);
							break;
						}
					}
					//get the data cluster
					String dataclusterName = null;
					Set<String> dataClusterPatterns = conceptPatternsToClusterName.keySet();
					for (Iterator<String> iterator = dataClusterPatterns.iterator(); iterator.hasNext(); ) {
						String pattern = iterator.next();
						if (conceptName.matches(pattern)) {
							dataclusterName = conceptPatternsToRevisionID.get(pattern);
							break;
						}
					}

					//fetch the cluster
					String cacheKey = revisionID+"$..$"+dataclusterName;
					DataClusterPOJO dataCluster = clusterCache.get(cacheKey);
					if (dataCluster == null) {
						dataCluster = ObjectPOJO.load(revisionID, DataClusterPOJO.class, new DataClusterPOJOPK(dataclusterName));
						clusterCache.put(cacheKey, dataCluster);
					}
					
					//spell check the cluster
					if ((wc.getRightValueOrPath().indexOf("*")==-1) && (wc.getRightValueOrPath().length()>2)) {
						sentences =  dataCluster.spellCheck(wc.getRightValueOrPath(),spellThreshold, true);
					}
					if (sentences.size() == 0) sentences.add(wc.getRightValueOrPath());
	        		if (sentences.size() == 1) {
	        			return new WhereCondition(
								wc.getLeftPath(),
								wc.getOperator(),
								sentences.iterator().next(),
								wc.getStringPredicate(),
								false
						);
	        		} else {
	        			//build an OR
		        		WhereOr or  = new WhereOr();
		        		for (Iterator<String> iter = sentences.iterator(); iter.hasNext(); ) {
							String s = iter.next();
							or.add(new WhereCondition(
									wc.getLeftPath(),
									wc.getOperator(),
									s,
									wc.getStringPredicate(),
									false
							));
						}
		        		return or;
	        		}
				} else {
					return new WhereCondition(
							wc.getLeftPath(),
							wc.getOperator(),
							wc.getRightValueOrPath(),
							wc.getStringPredicate(),
							false
					);
				}
			} else {
				throw new XtentisException("Unknown element of whereCondition: "+item.getClass().getName());
			}
		} catch (Exception e) {
			throw new XtentisException("Unable to spell check the where conditions: "+e.getLocalizedMessage());
		}
	}
	
}