package com.amalto.core.objects.datamodel.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.SchemaUtil;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

/**
 * @author Bruno Grieder
 * 
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
public class DataModelCtrlBean implements SessionBean{
  
	public static final long serialVersionUID = 1264958272;
	
	
    /**
     * DataModelCtrlBean.java
     * Constructor
     * 
     */
    public DataModelCtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	//context=ctx;
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
    }
    
    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {}
    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    
    
    
    /**
     * Creates or updates a DataModel
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public DataModelPOJOPK putDataModel(DataModelPOJO dataModel) throws XtentisException{  
    	
        try {
        	
        	if ((dataModel.getSchema()==null) || "".equals(dataModel.getSchema())) {
//        		put an empty schema
        		dataModel.setSchema(
        					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        					"<xsd:schema " +
        					"	elementFormDefault=\"qualified\"" +
        					"	xml:lang=\"EN\"" +
        					"	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
        					"</xsd:schema>"
        		);
        	} else {
        		//FIXME: implement xsom
        		// validate the schema against a schema for schema
        		//do schema validate in workbench
        		//String schema=checkSchema(dataModel.getSchema());
        		String schema=dataModel.getSchema();
        		//convert xsd base type
        		schema=SchemaUtil.convertBaseType(schema);
        		dataModel.setSchema(schema);
        	}
        	
        	
            ObjectPOJOPK pk = dataModel.store();
            if (pk == null) throw new XtentisException("Unable to create the Data Model. Please check the XML Server logs");
            
            return new DataModelPOJOPK(pk);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Data Model "+dataModel.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
    
     
    /**
     * Get Data Model
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public DataModelPOJO getDataModel(DataModelPOJOPK pk) throws XtentisException{

        try {
        	if(pk==null ||pk.getUniqueId()==null)  throw new XtentisException("The Data Model can't be empty!");
        	DataModelPOJO sp =  ObjectPOJO.load(DataModelPOJO.class,pk);
        	if (sp == null && pk.getUniqueId()!=null && !"null".equals(pk.getUniqueId())) {
        		String err= "The Data Model "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Data Model "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get a DataModel - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public DataModelPOJO existsDataModel(DataModelPOJOPK pk)    throws XtentisException{
        try {
        	return ObjectPOJO.load(DataModelPOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Data Model exists:  "+pk.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
    	   return null;
	    }
    }
    

    /**
     * Remove an  Data Model
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public DataModelPOJOPK removeDataModel(DataModelPOJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Removing "+pk.getUniqueId());

        try {
        	return new DataModelPOJOPK(ObjectPOJO.remove(DataModelPOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the DataModel "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    
    
    
    
    /**
	 * Retrieve all DataModel PKs
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<DataModelPOJOPK> getDataModelPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(DataModelPOJO.class,regex);
    	ArrayList<DataModelPOJOPK> l = new ArrayList<DataModelPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new DataModelPOJOPK(iter.next()));
		}
    	return l;
    }
    
    
    
    
    //TODO: rewrite using xsom
    
    
    /**
     * Checks the datamodel - returns the "corrected schema"
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String checkSchema(String schema) throws XtentisException{       
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("checkSchema() ");
        
        try {
        	//load the Schema for models. Do not check for permissions
        	DataModelPOJO schemaForSchemaPOJO = ObjectPOJO.load(
        		null,
        		DataModelPOJO.class, 
        		new DataModelPOJOPK("XMLSCHEMA---")
        	);
        	
        	String schemaForSchema;
        	if (schemaForSchemaPOJO == null) {
        		org.apache.log4j.Logger.getLogger(this.getClass()).info("Schema validity check disabled: schema for schema not available");
        		schemaForSchema = null;
        	} else {
        		schemaForSchema = schemaForSchemaPOJO.getSchema();
        	}
        	

        	//parse the new Schema
        	Document d = Util.parse(
        	        schema,
					schemaForSchema
        	 );
        	//check that templates used exist
        	String prefix = d.getDocumentElement().getPrefix();
            String[] called = Util.getAttributeNodeValue(
            		d.getDocumentElement(), 
					".//"+prefix+":element/@type",
					d.getDocumentElement()
			);
            HashSet<String> set = new HashSet<String>();
            for (int i = 0; i < called.length; i++) {
            	set.add(called[i]);
            }
            for (Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
            	String templateName = iter.next();
            	if (!templateName.startsWith(prefix+":"))
					if (getBusinessTemplateSchema(d, templateName ) == null)
						throw new XtentisException(
								"Invalid Data Model: a Business Template called "
								+templateName+ " is used but its definition cannot be found");	
			}    
            
            //check that all concepts have an xsd:unique declaration
            NodeList l = Util.getNodeList(d,"./"+prefix+":element");
            ArrayList<String> uniqueKeys = new ArrayList<String>();
            for (int i = 0; i< l.getLength(); i++) {
            	Element element =((Element)l.item(i)); 
            	NodeList uKs = element.getElementsByTagName(prefix+":unique");
            	// 2.12.0 Change support for Element ref
//            	if (uKs.getLength()!=1)
//            		throw new XtentisException("Business Concept "+element.getAttribute("name")+" must have a single unique key");
            	if (uKs.getLength()>=1) { //2.12.0
	            	String keyName = ((Element)uKs.item(0)).getAttribute("name");
	            	if (uniqueKeys.contains(keyName)) {
	            		String err = "Element "+element.getAttribute("name")+" has an unique key "+keyName+" which name is already used";
	            		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
	            		throw new XtentisException(err);
	            	}
	            	uniqueKeys.add(keyName);
            	} //2.12.0
            } 	          
            
            //get simple Keys
            String[] simpleKeys = Util.getAttributeNodeValue(
            		d.getDocumentElement(), 
					".//"+prefix+":key/@name",
					d.getDocumentElement()
			);
            ArrayList<String> allKeys = new ArrayList<String>();
            allKeys.addAll(uniqueKeys);
            for (int i = 0; i < simpleKeys.length; i++) {
            	allKeys.add(simpleKeys[i]);
			}

            
        	//check that all foreign keys refer  to an existing unique key
            
            //Grab all foreign keys
            NodeList fks = Util.getNodeList(d.getDocumentElement(), ".//"+prefix+":keyref");
            if (fks!=null) {
                int fkslen = fks.getLength();
                for (int i = 0; i < fkslen; i++) {
                	Element fkey = (Element) fks.item(i);
                	String fkeyRefer = fkey.getAttribute("refer");
                	String fkeyName = fkey.getAttribute("name");
                	
                	//check that it refers to an existing key
   	            	if (!allKeys.contains(fkeyRefer)) {
   	            		String err = "The foreign key \""+fkeyName+"\" refers to an unique key: \""+fkeyRefer+"\" which does not exist";
   	            		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
   	            		throw new XtentisException(err);
   	            	}
                	
   	            	//now delete it....
   	            	//FIXME: how can we improve this?
   	            	fkey.getParentNode().removeChild(fkey);
                	
				}

            }// if foreign keys
            
            //get the corrected schema as String
            schema = Util.nodeToString(d); 
            
    		//FIXES a bug in the validating parser(?) that puts nillable attributes in elements which are ref
    		//That is illegal according to W3C §3.3.3 / 2.2 (and Xerces) 
    		Pattern p  = Pattern.compile("(<([a-z]+:)?element.*?)nillable=['|\"].*?['|\"](.*?ref=.*?>)");
    		schema = p.matcher(schema).replaceAll("$1 $3");
            
            //return the corrected schema
            return schema;
            
        } catch (XtentisException e) {
        	throw e;
	    } catch (Exception e) {
	    	e.printStackTrace();
    	    String err = "Unable to check the Data Model "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    
    
    

    private Element getBusinessTemplateSchema(Document datamodel, String templateName)   throws XtentisException{
        
        try {            
                    
            //build the results
            NodeList l = Util.getNodeList(
	        		datamodel.getDocumentElement(),
					"./*[@name='"+templateName+"'  and ((local-name()='complexType') or (local-name()='simpleType'))]"
			);
            	
            int len = l.getLength();
            if (len==0) 
            {
            	return Util.getNameSpaceFromSchema(datamodel.getDocumentElement(), templateName);
            }

	        return (Element)l.item(0);            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Could not parse the Data DataModel to get the Business Template "+templateName+
								" from DataModel "+datamodel.getDocumentElement().getLocalName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }  
    
    
    /**
     * Put a Business Concept Schema 
     * @throws XtentisException
     * @return its name
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String putBusinessConceptSchema(DataModelPOJOPK pk , String conceptSchemaString)   throws XtentisException{
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("putBusinessConceptSchema() "+conceptSchemaString+" --> "+pk.getUniqueId());
        
        try {
        	//parse string
        	Element element = Util.parse(conceptSchemaString).getDocumentElement();
        	        	
            //get the element name
            String name = element.getAttribute("name");
            
        	//get the data model
            DataModelPOJO datamodel = getDataModel(pk);
            
            //parse it
            Document d = Util.parse(datamodel.getSchema());
            
            Element old = getBusinessConceptSchema(d,name);
            
            //check that types refered exist
            
            
            if (old==null) { 
            	//append the new type to the doc
            	d.getDocumentElement().appendChild(d.importNode(element,true));
            } else {
            	//replace the exising one
            	d.getDocumentElement().replaceChild(d.importNode(element,true),old);
            }
             	
            //get new schema and check it
            String newSchema = Util.nodeToString(d);
            newSchema = checkSchema(newSchema);
            
            //store everything
            datamodel.setSchema(newSchema);            
            datamodel.store();
            
	        return name;            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to put the Business Concept "+ pk.getUniqueId()+": \n"+conceptSchemaString+"\n"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    private Element getBusinessConceptSchema(Document datamodel, String conceptName)   throws XtentisException{
    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("getBusinessConceptSchema() "+conceptName);
        
        try {            
                    
            //build the results
        	String prefix = datamodel.getDocumentElement().getPrefix();
            NodeList l = Util.getNodeList(
	        		datamodel.getDocumentElement(),
					"./"+prefix+":element[@name='"+conceptName+"'][./"+prefix+":unique]"  //2.12.0 added: [./"+prefix+":unique]
			);
            int len = l.getLength();
            if (len==0) return null;
	        return (Element)l.item(0);            
            
        }catch (XtentisException e) {
        	throw new XtentisException(
        			"Could not parse the Data Model to get the Business Concept "+conceptName+": "
        			+e.getLocalizedMessage()
        	);
	    } catch (Exception e) {
    	    String err = "Could not parse the Data Model to get the Business Concept "+conceptName+
								" from DataModel "+datamodel.getDocumentElement().getLocalName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    

    
    /**
     * Delete a Business Concept
     * @throws XtentisException
     * @return its name
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String deleteBusinessConcept(DataModelPOJOPK pk , String businessConceptName)   throws XtentisException{
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("deleteBusinessConcept() ");
        
        try {
        	
//        	get the data model
            DataModelPOJO datamodel = getDataModel(pk);
            
            //parse it
            Document d = Util.parse(datamodel.getSchema());
            
            Element element = getBusinessConceptSchema(d,businessConceptName);
            
            if (element!=null) {
            	d.getDocumentElement().removeChild(element);
            }
            
            //store everything
            datamodel.setSchema(Util.nodeToString(d));
            datamodel.store();

            return businessConceptName;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Business Concept "+businessConceptName+" from the Data Model "+pk.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    
    
    
    /**
     * Find all Business Concepts names
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String[] getAllBusinessConceptsNames(DataModelPOJOPK pk)   throws XtentisException{
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("getAllBusinessConceptsNames() ");
        
        try {
        	//get the data model
            DataModelPOJO datamodel = getDataModel(pk);
            
            //parse it
            //Document d = Util.parse(datamodel.getSchema());
            
            //build the results
            List<String> resultNodes = Util.getALLNodesFromSchema(datamodel.getSchema());
            String[] results = resultNodes.toArray(new String[]{});
            Arrays.sort(results);
	        return results;            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get all the Business Concepts from the Data Model "+pk.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    
		
}