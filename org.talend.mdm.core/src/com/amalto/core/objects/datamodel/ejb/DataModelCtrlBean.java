package com.amalto.core.objects.datamodel.ejb;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import com.amalto.core.server.DataModel;
import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.ServerContext;
import com.amalto.core.util.Util;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @ejb.bean name="DataModelCtrl"
 *			display-name="Name for DataModelCtrl"
 *			description="Description for DataModelCtrl"
 *          jndi-name="amalto/remote/core/datamodelctrl"
 * 		  	local-jndi-name = "amalto/local/core/datamodelctrl"
 *          type="Stateless"
 *          view-type="both"
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
public class DataModelCtrlBean implements SessionBean, DataModel {

    public static final long serialVersionUID = 1264958272;

    private static final Logger LOGGER = Logger.getLogger(DataModelCtrlBean.class);

    private static final XPath X_PATH = XPathFactory.newInstance().newXPath();

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    private static final String EMPTY_SCHEMA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + //$NON-NLS-1$
            "<xsd:schema " + //$NON-NLS-1$
            "	elementFormDefault=\"qualified\"" + //$NON-NLS-1$
            "	xml:lang=\"EN\"" + //$NON-NLS-1$
            "	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + //$NON-NLS-1$
            "</xsd:schema>";

    static {
        DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
        X_PATH.setNamespaceContext(XSDNamespaceContext.INSTANCE);
    }

    public DataModelCtrlBean() {
    }

    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws javax.ejb.CreateException {
    }

    public void ejbPostCreate() throws javax.ejb.CreateException {
    }

    /**
     * Creates or updates a DataModel
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DataModelPOJOPK putDataModel(DataModelPOJO dataModel) throws XtentisException {
        try {
            if (dataModel.getSchema() == null || dataModel.getSchema().isEmpty()) {
                // put an empty schema
                dataModel.setSchema(EMPTY_SCHEMA);
            }
            ObjectPOJOPK pk = dataModel.store();
            if (pk == null) {
                throw new XtentisException("Unable to create the Data Model. Please check the XML Server logs");
            }
            return new DataModelPOJOPK(pk);
        } catch (XtentisException e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unable to create/update the Data Model '" + dataModel.getName() + "'";
            LOGGER.error(msg, e);
            throw new XtentisException(msg, e);
        }
    }

    /**
     * Get Data Model
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DataModelPOJO getDataModel(DataModelPOJOPK pk) throws XtentisException {
        if (pk == null || pk.getUniqueId() == null) {
            throw new XtentisException("The Data Model can't be empty!");
        }
        try {
            DataModelPOJO sp = ObjectPOJO.load(DataModelPOJO.class, pk);
            if (sp == null && pk.getUniqueId() != null && !"null".equals(pk.getUniqueId())) { //$NON-NLS-1$
                String err = "The Data Model '" + pk.getUniqueId() + "' does not exist.";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            return sp;
        } catch (XtentisException e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unable to get the Data Model '" + pk.toString() + "'";
            LOGGER.error(msg, e);
            throw new XtentisException(msg, e);
        }
    }

    /**
     * Get a DataModel - no exception is thrown: returns null if not found
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DataModelPOJO existsDataModel(DataModelPOJOPK pk) throws XtentisException {
        try {
            return ObjectPOJO.load(DataModelPOJO.class, pk);
        } catch (XtentisException e) {
            return null;
        } catch (Exception e) {
            String msg = "Error during existence check for '" + pk.getUniqueId() + "'";
            LOGGER.error(msg, e);
            return null;
        }
    }

    /**
     * Remove a Data Model
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public DataModelPOJOPK removeDataModel(DataModelPOJOPK pk) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing " + pk.getUniqueId());
        }
        try {
            return new DataModelPOJOPK(ObjectPOJO.remove(DataModelPOJO.class, pk));
        } catch (XtentisException e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unable to remove the DataModel '" + pk.toString() + "'";
            LOGGER.error(msg, e);
            throw new XtentisException(msg, e);
        }
    }

    /**
     * Retrieve all DataModel PKs
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public Collection<DataModelPOJOPK> getDataModelPKs(String regex) throws XtentisException {
        Collection<ObjectPOJOPK> dataModelPKs = ObjectPOJO.findAllPKs(DataModelPOJO.class, regex);
        ArrayList<DataModelPOJOPK> l = new ArrayList<DataModelPOJOPK>();
        for (ObjectPOJOPK dataModelPK : dataModelPKs) {
            l.add(new DataModelPOJOPK(dataModelPK));
        }
        return l;
    }

    /**
     * Checks the data model - returns the "corrected schema"
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String checkSchema(String schema) throws XtentisException {
        return schema;
    }

    /**
     * Put a Business Concept Schema
     *
     * @return its name
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String putBusinessConceptSchema(DataModelPOJOPK pk, String conceptSchemaString) throws XtentisException {
        try {
            DocumentBuilder builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            DataModelPOJO dataModel = getDataModel(pk);
            String schema = dataModel.getSchema();
            if (schema == null || schema.isEmpty()) {
                schema = EMPTY_SCHEMA;
            }
            Document schemaAsDOM = builder.parse(new InputSource(new StringReader(schema)));
            Document newConceptAsDOM = builder.parse(new InputSource(new StringReader(conceptSchemaString)));
            String conceptName;
            Node existingNode;
            synchronized (X_PATH) {
                conceptName = (String) X_PATH.evaluate("/xsd:element/@name", newConceptAsDOM, XPathConstants.STRING); //$NON-NLS-1$
                existingNode = (Node) X_PATH.evaluate("/xsd:schema/xsd:element[@name='" + conceptName + "']", schemaAsDOM, XPathConstants.NODE); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (existingNode != null) {
                existingNode.getParentNode().removeChild(existingNode);
            }
            schemaAsDOM.getDocumentElement().appendChild(schemaAsDOM.importNode(newConceptAsDOM.getDocumentElement(), true));
            dataModel.setSchema(Util.nodeToString(schemaAsDOM, true));
            dataModel.store();
            return conceptName;
        } catch (Exception e) {
            throw new XtentisException(e);
        }
    }

    /**
     * Delete a Business Concept
     *
     * @return its name
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String deleteBusinessConcept(DataModelPOJOPK pk, String businessConceptName) throws XtentisException {
        try {
            DocumentBuilder builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            DataModelPOJO dataModel = getDataModel(pk);
            String schema = dataModel.getSchema();
            if (schema == null || schema.isEmpty()) {
                return businessConceptName;
            }
            Document schemaAsDOM = builder.parse(new InputSource(new StringReader(schema)));
            Node existingNode;
            synchronized (X_PATH) {
                existingNode = (Node) X_PATH.evaluate("/xsd:schema/xsd:element[@name='" + businessConceptName + "']", schemaAsDOM, XPathConstants.NODE); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (existingNode != null) {
                existingNode.getParentNode().removeChild(existingNode);
            }
            dataModel.setSchema(Util.nodeToString(schemaAsDOM, true));
            dataModel.store();
            return businessConceptName;
        } catch (Exception e) {
            throw new XtentisException(e);
        }
    }

    /**
     * Find all Business Concepts names
     *
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String[] getAllBusinessConceptsNames(DataModelPOJOPK pk) throws XtentisException {
        MetadataRepositoryAdmin metadataRepositoryAdmin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
        MetadataRepository repository = metadataRepositoryAdmin.get(pk.getUniqueId());
        Collection<ComplexTypeMetadata> userComplexTypes = repository.getUserComplexTypes();
        String[] businessConceptNames = new String[userComplexTypes.size()];
        int i = 0;
        for (ComplexTypeMetadata currentType : userComplexTypes) {
            businessConceptNames[i++] = currentType.getName();
        }
        return businessConceptNames;
    }

    private static class XSDNamespaceContext implements NamespaceContext {

        private static NamespaceContext INSTANCE = new XSDNamespaceContext();

        @Override
        public String getNamespaceURI(String prefix) {
            if ("xsd".equals(prefix)) { //$NON-NLS-1$
                return XMLConstants.W3C_XML_SCHEMA_NS_URI;
            }
            return null;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(namespaceURI)) {
                return "xsd"; //$NON-NLS-1$
            }
            return null;
        }

        @Override
        public Iterator getPrefixes(String namespaceURI) {
            if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(namespaceURI)) {
                return Collections.singleton("xsd").iterator(); //$NON-NLS-1$
            }
            return Collections.emptyList().iterator();
        }
    }
}