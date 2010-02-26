package com.amalto.core.ejb;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.talend.mdm.commmon.util.bean.ItemCacheKey;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.configurationinfo.ejb.ConfigurationInfoPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingEngineV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerPluginV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.util.BAMLogger;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;


/**
 * @author Bruno Grieder
 * 
 */
public abstract class ObjectPOJO implements Serializable{
   
	/* cached the Object pojos to improve performance*/
	private static Hashtable<ItemCacheKey, String> cachedPojo=new Hashtable<ItemCacheKey, String>();
	private static final int MAX_CACHE_SIZE=5000;
	public static String getCluster(Class<? extends ObjectPOJO> objectClass) {
		return getCluster(objectClass.getName());
	}
	
	public static String getCluster(String objectClassName) {
		String[] names = objectClassName.split("\\.");
		return "amaltoOBJECTS"+names[names.length-1].replaceAll("POJO.*", "");
	}
	
	
	private static Object[][] OBJECT_TYPES = 
		new Object[][] {
				{"Data Cluster",DataClusterPOJO.class},
				{"Data Model",DataModelPOJO.class},
				{"Role",RolePOJO.class},
				{"Routing Rule",RoutingRulePOJO.class},
				{"Service",ServiceBean.class},
				{"Stored Procedure",StoredProcedurePOJO.class},
//				{"Transformer Plugin",TransformerPluginPOJO.class},
//				{"Transformer",TransformerPOJO.class},
				{"Transformer Plugin V2",TransformerPluginV2POJO.class},
				{"Transformer V2",TransformerV2POJO.class},
				{"View",ViewPOJO.class},
				{"Menu",MenuPOJO.class},
				{"Background Job",BackgroundJobPOJO.class},
				{"Configuration Info", ConfigurationInfoPOJO.class},
				{"Routing Order V2 Active", ActiveRoutingOrderV2POJO.class},
				{"Routing Order V2 Failed", FailedRoutingOrderV2POJO.class},
				{"Routing Order V2 Completed", CompletedRoutingOrderV2POJO.class},
				{"Routing Engine V2", RoutingEngineV2POJO.class},
				{"Universe", UniversePOJO.class},
				{"Synchronization Plan", SynchronizationPlanPOJO.class},
				{"Synchronization Conflict", SynchronizationItemPOJO.class},
				{"Item",ItemPOJO.class}
		};
	
	
	private static Object[][] OBJECT_ROOT_ELEMENT_NAMES = 
		new Object[][] {
				{"Data Cluster","data-cluster-pOJO"},
				{"Data Model","data-model-pOJO"},
				{"Role","role-pOJO"},
				{"Routing Rule","routing-rule-pOJO"},
				{"Service","service"},
				{"Stored Procedure","stored-procedure-pOJO"},
//				{"Transformer Plugin",TransformerPluginPOJO.class},
//				{"Transformer",TransformerPOJO.class},
				{"Transformer Plugin V2","transformer-plugin-v2-pOJO"},
				{"Transformer V2","transformer-v2-pOJO"},
				{"View","view-pOJO"},
				{"Menu","menu-pOJO"},
				{"Background Job","background-pOJO"},
				{"Configuration Info", "configuration-pOJO"},
				{"Routing Order V2 Active", "active-routing-order-v2-pOJO"},
				{"Routing Order V2 Failed", "failed-routing-order-v2-pOJO"},
				{"Routing Order V2 Completed", "completed-routing-order-v2-pOJO"},
				{"Routing Engine V2", "routing-engine-v2-pOJO"},
				{"Universe", "universe-pOJO"},
				{"Synchronization Plan", "synchronization-plan-pOJO"},
				{"Synchronization Conflict", "synchronization-item-pOJO"}
		};
	
	private static final HashMap<Class<?>, String> OBJECTS_CLASSES_TO_NAMES_MAP = new HashMap<Class<?>, String>();
	private static final HashMap<String, Class<?>> OBJECTS_NAMES_TO_CLASSES_MAP = new HashMap<String, Class<?>>();
	private static final HashMap<String, String> OBJECTS_NAMES_TO_ROOT_NAMES_MAP = new HashMap<String, String>();
	
	
	private static void initObjectsClasses2NamesMap() {
		for (int i = 0; i < OBJECT_TYPES.length; i++) {
			String objectName = (String) OBJECT_TYPES[i][0];
			Class<?> objectClass = (Class<?>) OBJECT_TYPES[i][1];
			OBJECTS_CLASSES_TO_NAMES_MAP.put(objectClass, objectName);
		}
	}
	public static HashMap<Class<?>, String>getObjectsClasses2NamesMap() {
		if (OBJECTS_CLASSES_TO_NAMES_MAP.size()==0) initObjectsClasses2NamesMap();
		return new HashMap<Class<?>, String>(OBJECTS_CLASSES_TO_NAMES_MAP);
	}
	public static String getObjectName(Class<?> clazz) {
		if (OBJECTS_CLASSES_TO_NAMES_MAP.size()==0) initObjectsClasses2NamesMap();
		return OBJECTS_CLASSES_TO_NAMES_MAP.get(clazz);
	}

	
	private static void initObjectsNames2ClassesMap() {
		for (int i = 0; i < OBJECT_TYPES.length; i++) {
			String objectName = (String) OBJECT_TYPES[i][0];
			Class<?> objectClass = (Class<?>) OBJECT_TYPES[i][1];
			OBJECTS_NAMES_TO_CLASSES_MAP.put(objectName,objectClass);
		}
	}
	public static HashMap<String, Class<?>>getObjectsNames2ClassesMap() {
		if (OBJECTS_NAMES_TO_CLASSES_MAP.size()==0) initObjectsNames2ClassesMap();
		return new HashMap<String, Class<?>>(OBJECTS_NAMES_TO_CLASSES_MAP);
	}
	public static Class<?> getObjectClass(String name) throws XtentisException{
		if (OBJECTS_NAMES_TO_CLASSES_MAP.size()==0) initObjectsNames2ClassesMap();
		try {
			return OBJECTS_NAMES_TO_CLASSES_MAP.get(name);
		} catch (Exception e) {
			String err = "No class found for Object "+name;
			org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
			throw new XtentisException(err);
		}
	}

	private static void initObjectsNames2RootNamesMap() {
		for (int i = 0; i < OBJECT_ROOT_ELEMENT_NAMES.length; i++) {
			String objectName = (String) OBJECT_ROOT_ELEMENT_NAMES[i][0];
			String rootElementName = (String) OBJECT_ROOT_ELEMENT_NAMES[i][1];
			OBJECTS_NAMES_TO_ROOT_NAMES_MAP.put(objectName, rootElementName);
		}
	}
	public static HashMap<String, String>getObjectsNames2RootNamesMap() {
		if (OBJECTS_NAMES_TO_ROOT_NAMES_MAP.size()==0) initObjectsNames2RootNamesMap();
		return new HashMap<String, String>(OBJECTS_NAMES_TO_ROOT_NAMES_MAP);
	}
	public static String getObjectRootElementName(String name) throws XtentisException{
		if (OBJECTS_NAMES_TO_ROOT_NAMES_MAP.size()==0) initObjectsNames2RootNamesMap();
		try {
			return OBJECTS_NAMES_TO_ROOT_NAMES_MAP.get(name);
		} catch (Exception e) {
			String err = "No element name found for Object "+name;
			org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
			throw new XtentisException(err);
		}
	}

	
    
	private transient String lastError = "";
	
	private String lastSynch = null;
	
	
    /**
     * 
     */
    public ObjectPOJO() {
        super();
    }
    
	
    /**
     * Returns the last XML Server Error
     * @return the error
     */
    public String getLastError() {
		return lastError;
	}

    /**
     * Set the last XML Server Error
     * @param the error
     */
	protected void setLastError(String lastError) {
		this.lastError = lastError;
	}
    
	public String getLastSynch() {
    	return lastSynch;
    }

	public void setLastSynch(String lastSynchronizationPlanRun) {
    	this.lastSynch = lastSynchronizationPlanRun;
    }

	/**
	 * The PK
	 * @return the pk, null if undefined
	 */
	public abstract ObjectPOJOPK getPK();



	
    /**
     * Loads an object in the user Universe after checking the user roles 
     * @param objectClass
     * @param objectPOJOPK
     * @return
     * 		The loaded Object
     * @throws XtentisException
     */
    public static <T extends ObjectPOJO> T load(Class<T> objectClass, ObjectPOJOPK objectPOJOPK) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(ObjectPOJO.class).trace("load() "+objectPOJOPK.getUniqueId());

    	try {
	    	//for the user have a role of administration , or role of write on instance or role of read on instance
    		ILocalUser user = LocalUser.getLocalUser();
	    	if (! user.userCanRead(objectClass, objectPOJOPK.getUniqueId())) {
	    	    String err = 
	    	    	"Unauthorized read access by " +
	    	    	"user '"+user.getUsername()+"' on object "+ObjectPOJO.getObjectName(objectClass)+" ["+objectPOJOPK.getUniqueId()+"] ";
	    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
	    		throw new XtentisException(err);    				
	    	}
	    	
	    	//get the universe
	    	UniversePOJO universe = user.getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+user.getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
	    		throw new XtentisException(err);
	    	}
	    	//Determine revision ID
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(getObjectName(objectClass));
	    	
	    	BAMLogger.log("DATA MANAGER", user.getUsername(), "read", objectClass, objectPOJOPK, true);
	    	
	    	return load(revisionID, objectClass, objectPOJOPK);
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to load the Object  "+  objectPOJOPK.getUniqueId()+" in Cluster "+getCluster(objectClass)
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    /**
     * Loads an object of a particular revision ID<br/>
     * NO Check is done on user rights
     * @param objectClass
     * @param objectPOJOPK
     * @return the instance of the object
     * @throws XtentisException
     */
    public static <T extends ObjectPOJO> T load(String revisionID, Class<T> objectClass, ObjectPOJOPK objectPOJOPK) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(ObjectPOJO.class).trace("load() "+revisionID+"/"+objectClass+" ["+objectPOJOPK.getUniqueId()+"]");
    	
        try {
            //get the xml server wrapper
            XmlServerSLWrapperLocal server =Util.getXmlServerCtrlLocal();
            //retrieve the item
            String urlid =  objectPOJOPK.getUniqueId();
            String item=null;
            ItemCacheKey key =new ItemCacheKey(revisionID,urlid, getCluster(objectClass));
            if(cachedPojo.size()==MAX_CACHE_SIZE)cachedPojo.clear();
            if(cachedPojo.containsKey(key)){
            	item=cachedPojo.get(key);            	
            }else{
            	item = server.getDocumentAsString(revisionID, getCluster(objectClass), urlid, null);
            	//aiming add see 9603 if System Object load faild try to load it from HEAD universe
            	if(!(revisionID==null || revisionID.length()==0) && item == null){
            		if(XSystemObjects.isExist(urlid)){
            			item = server.getDocumentAsString(null, getCluster(objectClass), urlid, null);
            		}
            	}
            	//end
            	if(item!=null)
            	cachedPojo.put(key, item);
            }
                                    
            if (item==null) {
                return null;
            }
            
            return unmarshal(objectClass, item); 
	        
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to load the Object  "+objectClass.getName()+"["+objectPOJOPK.getUniqueId()+"] in Cluster "+getCluster(objectClass)
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    
	/**
	 * Remove the item from the DB
	 * @param objectClass
	 * @param objectPOJOPK
	 * @return the Primary Key  of the object removed
	 * @throws XtentisException
	 */
    public static ObjectPOJOPK remove(Class<? extends ObjectPOJO> objectClass, ObjectPOJOPK objectPOJOPK) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(ObjectPOJO.class).trace("remove() "+objectPOJOPK.getUniqueId());
    	
    	try {
	    	//for delete we need to be admin, or have a role of admin , or role of write on instance 
	    	boolean authorized = false;
	    	ILocalUser user = LocalUser.getLocalUser();
	    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
	    		authorized = true;
	    	} else if (user.isAdmin(objectClass)) {
	    		authorized = true;
	    	} else if (user.userCanWrite(objectClass, objectPOJOPK.getUniqueId())) {
	    		authorized = true;
	    	}
	    	
	    	if (! authorized) {
	    	    String err = 
	    	    	"Unauthorized access on delete for " +
	    	    	"user "+user.getUsername()+" of object "+ObjectPOJO.getObjectName(objectClass)+" ["+objectPOJOPK.getUniqueId()+"] "; 
	    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
	    		throw new XtentisException(err);    				
	    	}
	    	
	    	//get the universe
	    	UniversePOJO universe = user.getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+user.getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(getObjectsClasses2NamesMap().get(objectClass));
	    	
	    	BAMLogger.log("DATA MANAGER", user.getUsername(), "delete", objectClass, objectPOJOPK,authorized);
	    	
	    	return internalRemove(revisionID, objectClass, objectPOJOPK);
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Object  "+  objectPOJOPK.getUniqueId()+" from Cluster "+getCluster(objectClass)
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    private static ObjectPOJOPK internalRemove(String revisionID, Class<? extends ObjectPOJO> objectClass, ObjectPOJOPK objectPOJOPK) throws XtentisException {
    	                
    	if (objectPOJOPK==null) {
    		return null;
    	}
    	
        try {
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            //remove the doc
            long res = server.deleteDocument(
            	revisionID,
            	getCluster(objectClass),
            	objectPOJOPK.getUniqueId()
            );
            if (res==-1) return null;
            
            //remove the cache
            ItemCacheKey key =new ItemCacheKey(revisionID,objectPOJOPK.getUniqueId(), getCluster(objectClass));
            cachedPojo.remove(key);
            return objectPOJOPK;
            
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the object "+objectPOJOPK.getUniqueId()+" from cluster "+getCluster(objectClass)
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    }  

    }

    /**
     * Store the current item in the DB
     * @return the pk of the item
     * @throws XtentisException
     */
    public ObjectPOJOPK store() throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("store() "+getPK().getUniqueId());
    	
       	try {
	    	//for storing we need to be admin, or have a role of admin , or role of write on instance 
	    	boolean authorized = false;
	    	ILocalUser user = LocalUser.getLocalUser();
	    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
	    		authorized = true;
	    	} else if (user.userCanWrite(this.getClass(), this.getPK().getUniqueId())) {
	    		authorized = true;
	    	}
	    	
	    	if (! authorized) {
	    	    String err = 
	    	    	"Unauthorized write access by " +
	    	    	"user "+user.getUsername()+" on object "+ObjectPOJO.getObjectName(this.getClass())+" ["+getPK().getUniqueId()+"] "; 
	    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
	    		throw new XtentisException(err);    				
	    	}
	    	
	    	//get the universe and revision ID
	    	UniversePOJO universe = user.getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+user.getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(getObjectsClasses2NamesMap().get(this.getClass()));
	    	
	    	BAMLogger.log("DATA MANAGER", user.getUsername(), "save", this.getClass(), getPK(),authorized);
	    	
	    	return store(revisionID);
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to store the Object  "+  getPK().getUniqueId()+" in Cluster "+getCluster(this.getClass())
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    
    /**
     * Performs the actual marshaling and storage of the object<br/>
     * User rights are NOT checked when using this method
     * 
     * @param revisionID
     * @return
     * 		The {@link ObjectPOJOPK} of the stored object
     * @throws XtentisException
     */
    public ObjectPOJOPK  store(String revisionID) throws XtentisException {
        
    	if (getPK()==null) {
    		setLastError("Unable to store: the object PK is Null");
    		return null;
    	}
    	
        try {
        	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();

 
			//Clear the synchronization Plan flag - this object is no more Synchronized
			this.lastSynch = null;
			
        	//Marshal
    		StringWriter sw = new StringWriter();
    		Marshaller.marshal(this, sw);
           
            //store
            if ( -1 ==  server.putDocumentFromString(
                	sw.toString(),
                	getPK().getUniqueId(),
					getCluster(this.getClass()),
					revisionID
				)) {
            	setLastError("Unable to store: check The XML Server Wrapper Logs");
            	return null;
            }
            	
            setLastError("");
            //update the cache
            ItemCacheKey key =new ItemCacheKey(revisionID,getPK().getUniqueId(), getCluster(this.getClass()));            
            cachedPojo.put(key, sw.toString());
            return getPK();
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to store the Object "+this.getClass().getName()+" --> "+getPK().getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    } 

    }
    
    
    


	/**
     * Retrieve all the PKs - will fetch only the PKs for which the user is authorized 
     * @param objectClass
     * @param regex
     * @return a Collection of ObjectPOJOPK
     * @throws XtentisException
     */
    public static ArrayList<ObjectPOJOPK> findAllPKs(Class<? extends ObjectPOJO> objectClass, String regex) throws XtentisException {
       	try {
       		int numItems = 0;
       		
	    	//check if we are admin 
	    	ILocalUser user = LocalUser.getLocalUser();
	    	
	    	//get the universe and revision ID
	    	UniversePOJO universe = user.getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+user.getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
	    		throw new XtentisException(err);
	    	}
	    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(getObjectsClasses2NamesMap().get(objectClass));
	    	

	    	if ("".equals(regex) || "*".equals(regex) || ".*".equals(regex)) regex = null;
	    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();

           
            //retrieve the item
            String[] ids = server.getAllDocumentsUniqueID(revisionID, getCluster(objectClass));
            
            
            if (ids==null) return new ArrayList<ObjectPOJOPK>();
            
            //build PKs collection
            ArrayList<ObjectPOJOPK> list = new ArrayList<ObjectPOJOPK>();
            for (int i = 0; i < ids.length; i++) {
            	if (org.apache.log4j.Logger.getLogger(ObjectPOJO.class).isTraceEnabled())
            		org.apache.log4j.Logger.getLogger(ObjectPOJO.class).trace("findAllPKs() matching "+ids[i]);
            	boolean match =true;
            	if (regex !=null) {
            		match = ids[i].matches(regex);
            	}
            	if (match)	{
            		if (user.userCanRead(objectClass, ids[i])) {
            			if (org.apache.log4j.Logger.getLogger(ObjectPOJO.class).isTraceEnabled())
            				org.apache.log4j.Logger.getLogger(ObjectPOJO.class).trace("findAllPKs() Adding PK");
            			list.add(new ObjectPOJOPK(ids[i]));
            			numItems++;
            		}
            	}
			}
            	    	
	    	BAMLogger.log("DATA MANAGER", user.getUsername(), "find all", objectClass, new ObjectPOJOPK(numItems+" Items"),numItems>0);
	    	
	    	return list;
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to find all the Object identifiers for object "+ getObjectName(objectClass)+" using regex "+regex
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    
    
	/**
     * Retrieve all PKs of an object type unsynchronized against a particular plan<br/>
     * The user must have the "administration" role to perform this task
     * @param revisionID
     * @param objectName
     * @param synchronizationPlanName
     * @return a Collection of ObjectPOJOPK
     * @throws XtentisException
     */
    public static ArrayList<String> findAllUnsynchronizedPKs(String revisionID, String objectName, String instancePattern, String synchronizationPlanName) throws XtentisException {
       	try {
       		
	    	//check if we are admin 
	    	ILocalUser user = LocalUser.getLocalUser();
	    	if (!user.getRoles().contains("administration")) {
	    		String err = "Only an user with the 'administration' role can call the synchronization methods";
				org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
				throw new XtentisException(err);
	    	}
	    		    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();

           
			String patternCondition = (instancePattern == null || ".*".equals(instancePattern)) ? "" : "[matches(./text(),'"+instancePattern+"')]";
			String synchronizationCondition = synchronizationPlanName == null ? "" : "[not (./last-synch/text() eq '"+synchronizationPlanName+"')]";
			String query = "/*"+synchronizationCondition+"/PK/unique-id"+patternCondition+"/text()";
			
            //retrieve the objects
			return server.runQuery(
				revisionID, 
				getCluster((Class<? extends ObjectPOJO>) getObjectClass(objectName)), 
				query, 
				null
			);
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Error Finding All Unsynchronized PKs"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    
    /**
     * Retrieve the marshaled version (e.g; the xml) of an object<br/>
     * The user must have the "administration" role to perform this task
     * @param revisionID
     * @param objectName
     * @param uniqueID
     * 		The unique ID of the object obtained using {@link #findAllUnsynchronizedPKs(String, String, String, String)}
     * @return
     *		The XML
     * @throws XtentisException
     */
    public static String getMarshaledObject(String revisionID, String objectName, String uniqueID) throws XtentisException{
       	try {
       		
	    	//check if we are admin 
	    	ILocalUser user = LocalUser.getLocalUser();
	    	if (!user.getRoles().contains("administration")) {
	    		String err = "Only an user with the 'administration' role can call the synchronization methods";
				org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
				throw new XtentisException(err);
	    	}
	    		    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = null;
			try {
				server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
			} catch (Exception e) {
				String err = "Error Finding All Unsynchronized PKs: unable to access the XML Server wrapper";
				org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
				throw new XtentisException(err);
			}
			
			String query = "/*[PK/unique-id/text() eq '"+uniqueID+"']";
           
			ArrayList<String> res= server.runQuery(
				revisionID, 
				getCluster((Class<? extends ObjectPOJO>) getObjectClass(objectName)), 
				query, 
				null
			);
			
			if (res==null || res.size()!=1) return null;
			return res.get(0);
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Error getting the marshaled object '"+objectName+"' of id '"+uniqueID+"' in revision '"+(revisionID == null ? "[HEAD]": revisionID)+"'"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    
    /**
     * Replaces/Creates an object using its marshaled version (e.g; the xml)<br/>
     * The user must have the "administration" role to perform this task
     * @param revisionID
     * @param objectName
     * @param uniqueID
     * 		The unique ID of the object obtained using {@link #findAllUnsynchronizedPKs(String, String, String, String)}
     * @param xml
     * 		The marshaled object
     * @throws XtentisException
     */
    public static void putMarshaledObject(String revisionID, String objectName, String uniqueID, String xml) throws XtentisException{
       	try {
       		
	    	//check if we are admin 
	    	ILocalUser user = LocalUser.getLocalUser();
	    	if (!user.getRoles().contains("administration")) {
	    		String err = "Only an user with the 'administration' role can call the synchronization methods";
				org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
				throw new XtentisException(err);
	    	}
	    		    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = null;
			try {
				server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
			} catch (Exception e) {
				String err = "Error Finding All Unsynchronized PKs: unable to access the XML Server wrapper";
				org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
				throw new XtentisException(err);
			}
			
           
			server.putDocumentFromString(
				xml, 
				uniqueID, 
				getCluster((Class<? extends ObjectPOJO>) getObjectClass(objectName)), 
				revisionID
			);
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Error creating/replacing the marshaled object '"+objectName+"' of id '"+uniqueID+"' in revision '"+(revisionID == null ? "[HEAD]": revisionID)+"'"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    

    /**
     * Find all Keys of an object for which the user is authorized and matching certain conditions
     * @param objectClass
     * 			The class of the XtentisObject
     * @param idsPaths
     * 			The full path (starting with the object element root name) of the ids
     * @param whereItem
     * 			The condition
     * @param orderBy
     * 			An option full path to order by
     * @param direction
     * 			The direction if orderBy is not <code>null</code>.
     * 			One of {@link IXmlServerSLWrapper#ORDER_ASCENDING}, {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @return
     * 		An orders
     * @throws XtentisException
     */
    public static ArrayList<ObjectPOJOPK> findAllPKsByCriteria(
    	Class<? extends ObjectPOJO> objectClass, 
    	String[] idsPaths, 
    	IWhereItem whereItem,
    	String orderBy,
    	String direction
    ) throws XtentisException {
       	try {
       		int numItems = 0;
       		
	    	//check if we are admin 
	    	boolean isAdmin = false;
	    	ILocalUser user = LocalUser.getLocalUser();
	    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
	    		isAdmin = true;
	    	} else if (user.isAdmin(objectClass)) {
	    		isAdmin = true;
	    	}
	    	
	    	//get the universe and revision ID
	    	UniversePOJO universe = user.getUniverse();
	    	if (universe == null) {
	    		String err = "ERROR: no Universe set for user '"+user.getUsername()+"'";
	    		org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
	    		throw new XtentisException(err);
	    	}
	    	
	    	//Root Elements Names to revision IDs and clusterNames
	    	HashMap<String, String> objectRootElementNameToRevisionID = new HashMap<String, String>();
	    	HashMap<String, String> objectRootElementNameToClusterName = new HashMap<String, String>();
	    	Set<String> objectNames = getObjectsNames2RootNamesMap().keySet();
	    	for (Iterator<String> iterator = objectNames.iterator(); iterator.hasNext(); ) {
				String objectName = iterator.next();
				String rootElementName = getObjectRootElementName(objectName); 
				objectRootElementNameToRevisionID.put(
					rootElementName,
					universe.getXtentisObjectsRevisionIDs().get(objectName)
				);
				objectRootElementNameToClusterName.put(
					rootElementName,
					getCluster((Class<? extends ObjectPOJO>) getObjectClass(objectName))
				);
			}
	    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = null;
			try {
				server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
			} catch (Exception e) {
				String err = "Error Finding All Instances of "+getObjectName(objectClass)+": unable to access the XML Server wrapper";
				org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
				throw new XtentisException(err);
			}
			
			//get the query
			String query = server.getXtentisObjectsQuery(
				objectRootElementNameToRevisionID, 
				objectRootElementNameToClusterName, 
				getObjectRootElementName(getObjectName(objectClass)), 
				new ArrayList<String>(Arrays.asList(idsPaths)), 
				whereItem, 
				orderBy, 
				direction, 
				0, 
				-1
			);
           
			//run the query
			Collection<String> res = server.runQuery(null, null, query, null);
			
			//Log
			org.apache.log4j.Logger.getLogger(ObjectPOJO.class).trace("findAllPKsByCriteria() QUERY\n"+query+"\nResults size: "+res.size());
			
			//no result --> we are done
			if (res==null) return new ArrayList<ObjectPOJOPK>(); 
			
			ArrayList<ObjectPOJOPK> list = new ArrayList<ObjectPOJOPK>();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			XPath xPath = XPathFactory.newInstance().newXPath();
			
            for (Iterator<String> iterator = res.iterator(); iterator.hasNext(); ) {
				String string = iterator.next();
				//rebuild IDs
				Document doc = builder.parse(new InputSource(new StringReader(string)));
				String[] ids = new String[idsPaths.length];
				for (int i = 0; i < ids.length; i++) {
					ids[i] = xPath.evaluate("/*/*["+(i+1)+"]/text()", doc);
				}
				//get ObjectPOJOPK
				ObjectPOJOPK pk = new ObjectPOJOPK(ids);

				//check authorizations
				boolean authorized=false;
				if (isAdmin) {
        			authorized = true;
        		} else if (user.userCanRead(objectClass, pk.getUniqueId())) {
    	    		authorized = true;
    	    	}
        		if (authorized) {
        			list.add(pk);
        			numItems++;
        		}
			}
                	
	    	BAMLogger.log("DATA MANAGER", user.getUsername(), "find all by criteria", objectClass, new ObjectPOJOPK(numItems+" Items"),numItems>0);
	    	
	    	return list;
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to find all the Object identifiers Using Criteria for object "+ getObjectName(objectClass)
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    /**
     * Returns a marshaled version of the object<br/>
     * Identical to calling {@link #marshal()} but does not throw an Exception, 
     * returns <code>null</code> instead
     */
    @Override
    public String toString() {
        try {
	        return marshal();
        } catch (XtentisException e) {
	        return null;
        }
    }

    /**
     * Returns a marshaled version of the object<br/>
     * @return
     * 		The marshaled object
     * @throws XtentisException
     */
    public String marshal() throws XtentisException{
    	//Marshal
		StringWriter sw = new StringWriter();
		try {
	        Marshaller.marshal(this, sw);
        } catch (Throwable t) {
        	String err = "Unable to marshal '"+this.getPK().getUniqueId()+"'";
	        org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,t);
	        throw new XtentisException(err);
        }
		return sw.toString();
    }
    	
    /**
     * 
     * Unmarshals an Object POJO to the Original Object
     * @param objectClass
     * @param marshaledItem
     * @return
     * 		The Object instance
     * @throws XtentisException
     */
    public static <T extends ObjectPOJO> T unmarshal(Class<T> objectClass, String marshaledItem) throws XtentisException{
    	try {
	        return (T)
	        	Unmarshaller.unmarshal(
	        			objectClass, 
	        			new InputSource(new StringReader(marshaledItem)
	        	)
	        );
        } catch (Throwable t) {
	        String err = "Unable to unmarshal the object of class '"+objectClass+"' from \n"+marshaledItem;
	        org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,t);
	        throw new XtentisException(err);
        } 
    }
    
    public static void clearCache(){
    	cachedPojo.clear();
    }
}
