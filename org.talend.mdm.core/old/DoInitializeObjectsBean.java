package com.amalto.core.ejb;


import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


/**
 * @author bgrieder
 * 
 * @ejb.bean name="DoInitializeObjects"
 *           display-name="Name for Initialize"
 *           description="Description for Initialize"
 *           jndi-name="amalto/remote/core/doinitializeobjects"
 * 		  local-jndi-name = "amalto/local/core/doinitializeobjects"
 *           type="Stateless"
 *           view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 * 
 * @ejb.ejb-ref 
 * 		ejb-name = "XmlServerSLWrapper" 
 * 		ref-name = "ejb/XmlServerSLWrapperLocal" 
 * 		view-type = "local"
 * 
 */
public class DoInitializeObjectsBean implements SessionBean {
  
    
    /**
     * DoPreProcessBean.java
     * Constructor
     * 
     */
    public DoInitializeObjectsBean() {
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
     * zap really everything on XySL
     * @throws CreateException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */ 
    public long zapXmlServer() throws XtentisException{
        
    	XmlServerSLWrapperLocal xy=null;
        try {
            long startT = System.currentTimeMillis();
            
            //get the xyleme wrapper
            xy = Util.getXmlServerSLWrapperLocal();
            
            String[] clusters = xy.getAllClusters();
            if (clusters!=null) {
	            for (int i = 0; i < clusters.length; i++) {
	            	try { xy.deleteCluster(clusters[i]);} catch(Exception e) {};
				}
            }
            
            //return the execution time
            long time=System.currentTimeMillis() -startT;
            org.apache.log4j.Category.getInstance(this.getClass()).debug("zapXmlServer() "+ time+"ms");
            return time;
            
	    } catch (Exception e) {
    	    String err = "Unable to zap the xml Server "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    } finally {
	    	try {xy.remove();} catch (Exception e) {}
	    }

    }
    
    /**
     * zap all objects and create basic conf
     * @throws CreateException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public long initializeObjects(String xmlSchema) throws XtentisException{
        
    	
    	XmlServerSLWrapperLocal xy=null;
        try {
            long startT = System.currentTimeMillis();
            
            //get propertie to the log
			Properties props = System.getProperties();
			Set keys = props.keySet();
			for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				org.apache.log4j.Category.getInstance(this.getClass()).debug(key+" = "+props.get(key));
			}

            
            //get the xyleme wrapper
            xy = Util.getXmlServerSLWrapperLocal();
        
            String[] objectsClusters=new String[] {
                    "amaltoOBJECTSconcepts",
                    "amaltoOBJECTSdestinations",
                    "amaltoOBJECTSinboundadaptors",
                    "amaltoOBJECTSoutboundadaptors",
                    "amaltoOBJECTSsources",
                    "amaltoOBJECTSdestinations",                    
                    "amaltoOBJECTStransforms",
                    "amaltoOBJECTSdataclusters",
                    "amaltoOBJECTSinboundplugins",
					"amaltoOBJECTSoutboundplugins",
					"amaltoOBJECTSmappings",
					"amaltoOBJECTSdocuments",
					"amaltoOBJECTSjobs",
					"amaltoOBJECTSviews",
					"amaltoOBJECTSdatamodels",
					"amaltoOBJECTSroutingrules",
					"amaltoOBJECTSroutingqueues"
                    };
            
            //zap objectsclusters only
            for (int i = 0; i < objectsClusters.length; i++) {
                String cluster = objectsClusters[i];
                try { xy.deleteCluster(cluster);} catch(Exception e) {};
            }              
            //create objects clusters
            for (int i = 0; i < objectsClusters.length; i++) {
                String cluster = objectsClusters[i];
                xy.createCluster(cluster,null);
            }              

            
            //V2-Upload XMLSChema
            DataModelPOJO dataModel = new DataModelPOJO("XMLSCHEMA---");
            dataModel.setDescription("XML Schema xsd");
            dataModel.setSchema(xmlSchema);
            Util.getDataModelCtrlLocal().putDataModel(dataModel);
            
            /*
            //V2- Create initial document scheme
            DataModelUtil.getLocalHome().create(
            		new DataModelValue(
            				"MODEL",
							"Default Initial Data Model",
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
							"<xsd:schema " +
							"	elementFormDefault=\"qualified\"" +
							"	xml:lang=\"EN\"" +
							"	xmlns=\"urn:amalto\"" +
							"	targetNamespace=\"urn:amalto\"" +
							"	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
							"</xsd:schema>"
            				)
					);
			*/
            //V2 - create a cache for documents
            /*
            try { xy.deleteCluster("CACHE");} catch(Exception e) {};
            Util.getDataClusterCtrlLocal().putDataCluster(
            		new DataClusterPOJO(
            				"CACHE",
							"Default document cache",
							"" //no vocabulary
            				)            		
            		);
            */ //NO MORE CACHE
            
            //return the execution time
            long time=System.currentTimeMillis() -startT;
            org.apache.log4j.Category.getInstance(this.getClass()).debug("initialize() "+ time+"ms");
            
            return time;
            
	    } catch (Exception e) {
    	    String err = "Unable to initialize the core objects "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    } 
    }
    
    
 
}