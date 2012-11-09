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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
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
 *          name="XmlServerSLWrapper" 
 *          display-name="XML:DB Stateless Wrapper"
 *          description="Description for XML:DB Stateless Wrapper"
 *          jndi-name="amalto/remote/xmldb/xmlserverslwrapper" 
 *          local-jndi-name = "amalto/local/xmldb/xmlserverslwrapper" 
 *          type="Stateless"
 *          view-type="both"
 * 
 * @ejb.permission
 *  view-type = "remote"
 *  role-name = "administration"
 * @ejb.permission
 *  view-type = "local"
 *  unchecked = "true"
 * 
 *
 *  
 * 
 * @ejb.remote-facade
 */
public class XmlServerSLWrapperBean implements SessionBean {
    
    private static final Logger LOG = Logger.getLogger(XmlServerSLWrapperBean.class);
    
    private static String SERVERCLASS;
    
    {
        SERVERCLASS = MDMConfiguration.getConfiguration().getProperty("xmlserver.class"); //$NON-NLS-1$
        if ((SERVERCLASS==null) || SERVERCLASS.length() == 0) SERVERCLASS = "com.amalto.xmldb.XmldbSLWrapper"; //$NON-NLS-1$
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
        if(LOG.isTraceEnabled())
            LOG.trace("isUpAndRunning() "); //$NON-NLS-1$
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
     *      The ID of the revision, <code>null</code> for the head
     * @return the list of cluster IDs
     *  
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String[] getAllClusters(String revisionID) throws XtentisException {
        try {
            return server.getAllClusters(revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
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
     *          The ID of the revision, <code>null</code> for the head
     * @param clusterName
     *          The name of the cluster
     * @return the milliseconds to perform the operation
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public long deleteCluster(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.deleteCluster(revisionID, clusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * Delete All clusters for a particular revision
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the milliseconds to perform the operation
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public long deleteAllclusterNames(String revisionID) throws XtentisException {
        try {
            return server.deleteAllClusters(revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * Create a cluster for a particular revision
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param clusterName
     *          The name of the cluster
     * @return the milliseconds to perform the operation
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public long createCluster(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.createCluster(revisionID, clusterName);   
        } catch (XmlServerException e) {
            throw new XtentisException(e);
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
     *          The full path of the file
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the milliseconds to perform the operation
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID) throws XtentisException {
        try {
            return server.putDocumentFromFile(fileName, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * Reads a document from a file and stores it in the DB
     * @param fileName
     *          The full path of the file
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param documentType
     *          "DOCUMENT" for and XML document, "BINARY" otherwise
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
            throw new XtentisException(e);
        }
    }


    /**
     * Read a document from s String an store it in the DB as "DOCUMENT"
     * @param xmlString
     *          The xml string
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the time to store the document
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID) throws XtentisException {
        try {
            return server.putDocumentFromString(xmlString, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * Read a document from a String and store it in the DB
     * @param string
     *          The string to store
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param documentType
     *          "DOCUMENT" for and XML document, "BINARY" otherwise
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
            throw new XtentisException(e);
        }
    }
    
    /**
     * Stores a document from its DOM root element
     * @param root
     *          The DOM root element
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the time to store the document
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XtentisException {
        try {
            return server.putDocumentFromDOM(root, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
    * Load a document using a SAX parser.
    *
    * @param dataClusterName The unique ID of the cluster
    * @param docReader A SAX reader
    * @param input A SAX input
    * @param revisionId The revision id (<code>null</code> for head).
    * @throws com.amalto.xmlserver.interfaces.XmlServerException If anything goes wrong in underlying storage
    */
   public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId) throws com.amalto.core.util.XtentisException {
         try {
            return server.putDocumentFromSAX(dataClusterName, docReader, input, revisionId);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
   }

    /**
     * Gets an XML document from the DB<br>
     * The XML instruction will have an encoding specified as UTF-16
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the document as A string
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XtentisException {
        try {
            return server.getDocumentAsString(revisionID, clusterName, uniqueID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }
    /**
     * 
     * @param revision
     * @param cluster
     * @return
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public boolean existCluster(String revision,String cluster)throws XtentisException{
        try {
            if (cluster.endsWith(StorageAdmin.STAGING_SUFFIX)) {
                cluster = StringUtils.substringBeforeLast(cluster, "#");
            }
            return server.existCluster(revision, cluster);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }
    /**
     * Gets an XML document from the DB<br>
     * The XML instruction will have the encoding specified in the encoding parameter<br>
     * If encoding is null, the document will not have an XML instruction
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param encoding
     *          The encoding of the XML instruction (e.g. UTF-16, UTF-8, etc...).
     * @return the document as A string
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XtentisException {
        try {
            return server.getDocumentAsString(revisionID, clusterName, uniqueID, encoding);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }
    
    /**
     * Gets the bytes of a document from the DB<br>
     * For an xml "DOCUMENT" type, the bytes will be encoded using UTF-16
     * 
     * The XML instruction will have an encoding specified as UTF-16
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param documentType
     *          "DOCUMENT" for and XML document, "BINARY" otherwise
     * @return the document as A string
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType) throws XtentisException {
        try {
            return server.getDocumentBytes(revisionID, clusterName, uniqueID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * The list of documents unique ids in a cluster of a particular revision
     * 
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the list of document unique IDs
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.getAllDocumentsUniqueID(revisionID, clusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * Delete an XML  document
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
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
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the time to perform the delete
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType) throws XtentisException {
        try {
            return server.deleteDocument(revisionID, clusterName, uniqueID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    
    
    /**
     * Delete Xtentis Objects matching a particular condition<br> 
     * @param objectRootElementNameToRevisionID
     *          A map that gives the revision ID of an Object XML Root Element Name
     * @param objectRootElementNameToClusterName
     *          An ordered map that gives the cluster name of an Object XML Root Element Name
     * @param objectRootElementName
     *          The objectType (its name)
     * @param whereItem
     *          The condition
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
            throw new XtentisException(e);
        }
    }
    
    /**
     * Delete Items matching a particular condition<br> 
     * @param conceptPatternsToClusterName
     *          An ordered map that gives the cluster name of a Concept when matching the first pattern found
     * @param conceptPatternsToRevisionID
     *          An ordered map that gives the revision ID of a Concept when matching the first pattern found
     * @param conceptName
     *          The Concept of the items being deleted
     * @param whereItem
     *          The condition
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
            throw new XtentisException(e);
        }
    }

    

    /**
     * Move a document between two clusters of particular revision
     * @param uniqueID
     *          The unique ID of the document
     * @param sourceclusterName
     *          The unique ID of the source cluster
     * @param sourceRevisionID
     *          The ID of the source revision
     * @param targetclusterName
     *          The unique ID of the target cluster
     * @param targetRevisionID
     *          The ID of the target revision
     * @return the time to perform the move
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID, String targetclusterName) throws XtentisException {
        try {
            return server.moveDocumentById(sourceRevisionID, sourceclusterName, uniqueID, targetRevisionID, targetclusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * Count Items based on conditions
     * @param conceptPatternsToRevisionID
     *          A map that gives the revision ID of a pattern matching a concept name Concept (isItemQuery is true) or Xtentis Object (isItemQuery is false)
     * @param conceptPatternsToClusterName
     *          An ordered map that gives the cluster name of a Concept when matching the first pattern found
     * @param conceptName
     *          The name of the concept 
     * @param whereItem 
     *          The condition to apply
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
            throw new XtentisException(e);
        }
    }
    
    /**
     * Count Xtentis Objects based on conditions
     * @param objectRootElementNameToRevisionID
     *          A map that gives the revision ID of an Xtentis Object based on its XML Root Element Name 
     * @param objectRootElementNameToClusterName
     *          An ordered map that gives the cluster name of an Object based on its XML Root Element Name
     * @param mainObjectRootElementName
     *          An optional object XML root element name that will serve as the main pivot<br/>
     *          If not specified, the pivots will be in ordered of those in the viewableBusinessElements
     * @param whereItem 
     *          The condition to apply
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
            throw new XtentisException(e);
        }
    }
    
    /**
     * Performs a query on the db with optional parameters<br>
     * The parameters are specified as %n in the query where n is the parameter number starting with 0
     * @param revisionID
     *          The ID of the revision, <code>null</code> to run from the head
     * @param clusterName
     *          The unique ID of the cluster,  <code>null</code> to run from the head of the revision ID
     * @param query 
     *          The query in the native language
     * @param parameters 
     *          The parameter values to replace the %n in the query before execution
     * @return the result of the Query as a Collection of Strings
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XtentisException {
        try {
            return server.runQuery(revisionID, clusterName, query, parameters);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }
    
    /**
     * Performs a query on the db with optional parameters<br>
     * The parameters are specified as %n in the query where n is the parameter number starting with 0
     * @param revisionID
     *          The ID of the revision, <code>null</code> to run from the head
     * @param clusterName
     *          The unique ID of the cluster,  <code>null</code> to run from the head of the revision ID
     * @param query 
     *          The query in the native language
     * @param parameters 
     *          The parameter values to replace the %n in the query before execution
     * @return the result of the Query as a Collection of Strings
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ArrayList<String> runQuery(String revisionID, String clusterName,
            String query, String[] parameters, final int start, final int limit, final boolean withTotalCount)
            throws XtentisException {
        try {
            return server.runQuery(revisionID, clusterName, query, parameters,start,limit,withTotalCount);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XtentisException {
        try {
            return server.getItemPKsByCriteria(criteria);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }
    /**
     * Builds a query in the native language of the DB (for instance XQuery) based on conditions
     * @param objectRootElementNameToRevisionID
     *          A map that gives the revision ID of an Object XML Root Element Name
     * @param objectRootElementNameToClusterName
     *          An ordered map that gives the cluster name of an Object XML Root Element Name
     * @param viewableObjectElements 
     *          The full XPath (starting with the Object root element name) 
     *          of the elements and their sub elements that constitute the top elements of the returned documents
     * @param mainObjectRootElementName
     *          An optional object type that will serve as the main pivot<br/>
     *          If not specified, the pivots will be in ordered of those in the viewableObjectElements
     * @param whereItem 
     *          The condition to apply
     * @param orderBy
     *          The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction
     *          If orderBy is not <code>null</code>, the direction. One of 
     * @param start
     *          The index of the first element to return (start at 0)
     * @param limit
     *          The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no limit
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
            throw new XtentisException("Unable to get the Xtentis Objects Query ", e);
        }
    }

    /**
     * Builds a query in the native language of the DB (for instance XQuery) based on conditions
     * 
     * @param objectRootElementNameToRevisionID A map that gives the revision ID of an Object XML Root Element Name
     * @param objectRootElementNameToClusterName An ordered map that gives the cluster name of an Object XML Root
     * Element Name
     * @param viewableObjectElements The full XPath (starting with the Object root element name) of the elements and
     * their sub elements that constitute the top elements of the returned documents
     * @param mainObjectRootElementName An optional object type that will serve as the main pivot<br/>
     * If not specified, the pivots will be in ordered of those in the viewableObjectElements
     * @param whereItem The condition to apply
     * @param orderBy The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction If orderBy is not <code>null</code>, the direction. One of
     * @param start The index of the first element to return (start at 0)
     * @param limit The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no
     * limit
     * @param withTotalCount whether get totalCount
     * @return the XQuery in the native language of the database
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
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

    /**
     * Builds an Items query in the native language of the DB (for instance XQuery) based on conditions
     * @param conceptPatternsToRevisionID
     *          A map that gives the revision ID of a pattern matching a concept name Concept (isItemQuery is true) or Xtentis Object (isItemQuery is false)
     * @param conceptPatternsToClusterName
     *          An ordered map that gives the cluster name of a Concept when matching the first pattern found
     * @param forceMainPivot
     *          An optional pivot that will appear first in the list of pivots in the query<br>:
     *          This allows forcing cartesian products: for instance Order Header vs Order Line 
     * @param viewableFullPaths 
     *          The Full xPaths (starting with concept name) of the elements and their sub elements 
     *          that constitute the top elements of the returned documents
     * @param whereItem 
     *          The condition to apply
     * @param orderBy
     *          The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction
     *          If orderBy is not <code>null</code>, the direction. One of 
     * @param start
     *          The index of the first element to return (start at 0)
     * @param limit
     *          The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no limit
     * @param spellThreshold
     *          Spell check the whereItem if threshold is greater than zero. The setting is ignored is this not an item query.
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
            if(LOG.isDebugEnabled())
                LOG.debug("getQuery():\n "+q); //$NON-NLS-1$
            return q;
        } catch (XtentisException e) {
            throw(e);
        } catch (Exception e) {
            throw new XtentisException("Unable to build the query", e);
        }
    }
    /**
     * Builds an Items query in the native language of the DB (for instance XQuery) based on conditions
     * @param conceptPatternsToRevisionID
     *          A map that gives the revision ID of a pattern matching a concept name Concept (isItemQuery is true) or Xtentis Object (isItemQuery is false)
     * @param conceptPatternsToClusterName
     *          An ordered map that gives the cluster name of a Concept when matching the first pattern found
     * @param forceMainPivot
     *          An optional pivot that will appear first in the list of pivots in the query<br>:
     *          This allows forcing cartesian products: for instance Order Header vs Order Line 
     * @param viewableFullPaths 
     *          The Full xPaths (starting with concept name) of the elements and their sub elements 
     *          that constitute the top elements of the returned documents
     * @param whereItem 
     *          The condition to apply
     * @param orderBy
     *          The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction
     *          If orderBy is not <code>null</code>, the direction. One of 
     * @param start
     *          The index of the first element to return (start at 0)
     * @param limit
     *          The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no limit
     * @param spellThreshold
     *          Spell check the whereItem if threshold is greater than zero. The setting is ignored is this not an item query.
     * @param metaDataTypes
     *          
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
        int spellThreshold,
        boolean firstTotalCount,
        Map<String, ArrayList<String>> metaDataTypes
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
                limit,
                firstTotalCount,
                metaDataTypes
            );
            if(LOG.isDebugEnabled())
                LOG.debug("getQuery():\n "+q); //$NON-NLS-1$
            return q;
        } catch (XtentisException e) {
            throw(e);
        } catch (Exception e) {
            throw new XtentisException("Unable to build the query", e);
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
            throw new XtentisException("Unable to get the Xtentis Objects Query", e);
        }
    }
    
    /**
     * @param clusterName
     * @param conceptName
     * @param PKXpaths
     * @param FKXpath
     * @param labelXpath
     * @param fatherPK
     * @param itemsRevisionIDs
     * @param defaultRevisionID
     * @param whereItem
     * @param start
     * @param limit
     * @return
     * @throws XmlServerException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    
    public String getChildrenItemsQuery(
            String clusterName, 
            String conceptName,
            String[] PKXpaths,
            String FKXpath,
            String labelXpath,
            String fatherPK,
            LinkedHashMap<String, String> itemsRevisionIDs,
            String defaultRevisionID,
            IWhereItem whereItem,
            int start, 
            int limit
    ) throws XtentisException{
        try {
            return server.getChildrenItemsQuery(
                    clusterName, 
                    conceptName, 
                    PKXpaths, 
                    FKXpath,
                    labelXpath, 
                    fatherPK, 
                    itemsRevisionIDs, 
                    defaultRevisionID,
                    whereItem,
                    start,
                    limit);
                
        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Items Query", e);
        }
    }
    
    /**
     * 
     * Spell check is implemented by replacing any word that has matches in the dictionary
     * by an or clause with all the matches
     * 
     * @param item
     *          The {@link IWhereItem} to spell check
     * @param spellThreshold
     *          The sell Threshold - 0 or less de-activates spell
     * @return the spellcheck whereItem
     * @throws XtentisException
     */
    private IWhereItem spellCheckWhere(
        LinkedHashMap<String, String> conceptPatternsToRevisionID,
        LinkedHashMap<String, String> conceptPatternsToClusterName,
        IWhereItem item, int spellThreshold
    ) throws XtentisException{
        if(LOG.isTraceEnabled())
            LOG.trace("spellCheckWhere() " //$NON-NLS-1$
                    + (((item != null) && (item instanceof WhereCondition)) ? ((WhereCondition) item).toString() : "")); //$NON-NLS-1$
        
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
                            dataclusterName = conceptPatternsToClusterName.get(pattern);
                            break;
                        }
                    }

                    //fetch the cluster
                    String cacheKey = revisionID+"$..$"+dataclusterName; //$NON-NLS-1$
                    DataClusterPOJO dataCluster = clusterCache.get(cacheKey);
                    if (dataCluster == null) {
                        dataCluster = ObjectPOJO.load(revisionID, DataClusterPOJO.class, new DataClusterPOJOPK(dataclusterName));
                        clusterCache.put(cacheKey, dataCluster);
                    }
                    
                    //spell check the cluster
                    if ((wc.getRightValueOrPath().indexOf("*")==-1) && (wc.getRightValueOrPath().length()>2)) { //$NON-NLS-1$
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
            throw new XtentisException("Unable to spell check the where conditions", e);
        }
    }
    /**
     * @return
     * @throws XmlServerException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */     
    public boolean supportTransaction() {
        return server.supportTransaction();
    }
    /**
     * @return
     * @throws XmlServerException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */     
    public void start(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.start(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }
    /**
     * @return
     * @throws XmlServerException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */ 
    public void commit(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.commit(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }
    /**
     * @return
     * @throws XmlServerException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */ 
    public void rollback(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.rollback(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }
    /**
     * @return
     * @throws XmlServerException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */    
    public void end(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.end(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public void close(  ) throws com.amalto.core.util.XtentisException {
        try {
            server.close();
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public List<String> globalSearch( String dataCluster, String keyword, int start, int end ) throws com.amalto.core.util.XtentisException {
        try {
            return server.globalSearch(dataCluster, keyword, start, end);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public void exportDocuments(String revisionId, String clusterName, int start, int end, boolean includeMetadata,  OutputStream outputStream) throws com.amalto.core.util.XtentisException {
        try {
            server.exportDocuments(revisionId, clusterName, start, end, includeMetadata, outputStream);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    /**
     * @return boolean
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public boolean supportStaging(String dataCluster) {
        if (dataCluster == null || dataCluster.trim().length() == 0) {
            return false;
        }
        Server server = ServerContext.INSTANCE.get();
        Storage storage = server.getStorageAdmin().get(dataCluster + StorageAdmin.STAGING_SUFFIX, null);
        if (storage == null) {
            return false;
        }
        return true;
    }
}