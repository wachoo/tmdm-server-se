package com.amalto.core.ejb;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJBException;

import org.apache.commons.lang.StringEscapeUtils;
import org.exolab.castor.xml.Marshaller;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.schema.manage.AppinfoSourceHolder;
import com.amalto.core.schema.manage.AppinfoSourceHolderPK;
import com.amalto.core.schema.manage.SchemaManager;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


/**
 * @author Bruno Grieder
 * 
 */
public class ItemPOJO implements Serializable{
   
	public final static String LOGGING_EVENT="logging_event";
	
	private String dataModelName;//used for binding data model
	private String dataModelRevision;//used for binding data model
    private String conceptName;
    private DataClusterPOJOPK dataClusterPOJOPK;
    private SynchronizationPlanPOJOPK planPK;
    private long insertionTime;
    private String[] itemIds;
    private Element projection;
    
	/* cached the Object pojos to improve performance*/
	//private static Hashtable<ItemCacheKey, String> cachedPojo=new Hashtable<ItemCacheKey, String>();	

    /**
     * 
     */
    public ItemPOJO() {
        super();
    }
    

	/**
	 * 
	 * @param clusterPK
	 * @param concept
	 * @param ids
	 * @param time
	 * @param projection
	 */
	public ItemPOJO(DataClusterPOJOPK clusterPK, String concept, String[] ids, long time, Element projection) {
		this.conceptName = concept;
		this.dataClusterPOJOPK = clusterPK;
		this.insertionTime = time;
		this.itemIds = ids;
		this.projection = projection;
		this.planPK = null;
	}
	
	/**
	 * 
	 * @param clusterPK
	 * @param concept
	 * @param ids
	 * @param time
	 * @param projectionAsString
	 */
	public ItemPOJO(DataClusterPOJOPK clusterPK, String concept, String[] ids, long time, String projectionAsString) {
		this.conceptName = concept;
		this.dataClusterPOJOPK = clusterPK;
		this.insertionTime = time;
		this.itemIds = ids;
		this.projectionString = projectionAsString;
		this.planPK = null;
	}
	
	
	public String getDataModelName() {
		return dataModelName;
	}


	public void setDataModelName(String dataModelName) {
		this.dataModelName = dataModelName;
	}


	public String getDataModelRevision() {
		return dataModelRevision;
	}


	public void setDataModelRevision(String dataModelRevision) {
		this.dataModelRevision = dataModelRevision;
	}


	/**
	 * @return Returns the conceptName.
	 */
	public String getConceptName() {
		return conceptName;
	}

	/**
	 * @param conceptName The conceptName to set.
	 */
	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}


	/**
	 * @return Returns the dataClusterPK.
	 */
	public DataClusterPOJOPK getDataClusterPOJOPK() {
		return dataClusterPOJOPK;
	}

	/**
	 * @param dataClusterPOJOPK The dataClusterPK to set.
	 */
	public void setDataClusterPK(DataClusterPOJOPK dataClusterPOJOPK) {
		this.dataClusterPOJOPK = dataClusterPOJOPK;
	}


	/**
	 * @return Returns the insertionTime.
	 */
	public long getInsertionTime() {
		return insertionTime;
	}

	/**
	 * @param insertionTime The insertionTime to set.
	 */
	public void setInsertionTime(long insertionTime) {
		this.insertionTime = insertionTime;
	}

	/**
	 * If the item was not changed since last synchronization, 
	 * this will contain the name of the plan that synchronized it
	 * @return
	 * 		The {@link SynchronizationPlanPOJOPK}
	 */
	public SynchronizationPlanPOJOPK getPlanPK() {
    	return planPK;
    }

	public void setPlanPK(SynchronizationPlanPOJOPK planPK) {
    	this.planPK = planPK;
    }


	/**
	 * @return Returns the itemIds.
	 */
	public String[] getItemIds() {
		return itemIds;
	}

	/**
	 * @param itemIds The itemIds to set.
	 */
	public void setItemIds(String[] itemIds) {
		this.itemIds = itemIds;
	}

	private String projectionString = null;
	/**
	 * 
	 * @return The projection as a String
	 * @throws XtentisException
	 */
	public String getProjectionAsString() throws XtentisException{
		if (projectionString == null) {
			try {
				projectionString = Util.nodeToString(projection);
			} catch (Exception e) {
				String err = "Unable to serialize the Item "+this.getItemPOJOPK().getUniqueID()+": "+e.getLocalizedMessage();
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
				throw new XtentisException(err);
			}
		}
		return projectionString;
	}

	/**
	 * @param str The projection to set.
	 */
	public void setProjectionAsString(String str) throws XtentisException{
		this.projectionString = str;
		try {
			if(str!=null && str.length()>0)
				projection = Util.parse(this.projectionString).getDocumentElement();
			else
				return;
		} catch (Exception e) {
			String err = "Unable to parse the Item "+this.getItemPOJOPK().getUniqueID()+". "
			+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);
		}
	}


	/**
	 * @return Returns the projection.
	 */
	public Element getProjection() throws XtentisException{
		if (projection==null) {
			try {
				projection = Util.parse(this.projectionString).getDocumentElement();
			} catch (Exception e) {
				String err = "Unable to parse the Item "+this.getItemPOJOPK().getUniqueID()+". "
				+e.getClass().getName()+": "+e.getLocalizedMessage();
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
				throw new XtentisException(err);
			}
		}
		return projection;
	}

	/**
	 * @param projection The projection to set.
	 */
	public void setProjection(Element projection) {
		this.projection = projection;
		projectionString = null;
	}
	
	
	/**
	 * The PK
	 * @return the pk, null if undefined
	 */
	public ItemPOJOPK getItemPOJOPK() {
		if ((getDataClusterPOJOPK() == null) || (getConceptName()==null) || (getItemIds()==null)) return null;
		return new ItemPOJOPK(
				getDataClusterPOJOPK(),
				getConceptName(),
				getItemIds()
		);
	}
	


	private static Pattern pLoad = Pattern.compile(".*?(<c>.*?</t>).*?(<p>(.*)</p>|<p/>).*",Pattern.DOTALL);
	
    
    /**
     * Loads an Item. User rights are checked.
     * 
     * @param itemPOJOPK
     * @return
     * 	the {@link ItemPOJO}
     * @throws XtentisException
     */
    public static ItemPOJO load(ItemPOJOPK itemPOJOPK) throws XtentisException {
//    	org.apache.log4j.Logger.getLogger(ItemPOJO.class).trace("load() "+itemPOJOPK.getUniqueID());
        
    	//Check authorizations
    	boolean authorized = false;
    	LocalUser user = LocalUser.getLocalUser();
    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
    		authorized = true;
    	//} else if (user.userCanRead(ItemPOJO.class, itemPOJOPK.getUniqueID())) {
    	}else if(user.userItemCanRead(adminLoad(itemPOJOPK))){ //aiming modify see 10027
    		authorized = true;
    	}
    	
    	if (! authorized) {
    	    String err = 
    	    	"Unauthorized read access by " +
    	    	"user "+user.getUsername()+" on Item '"+itemPOJOPK.getUniqueID()+"'"; 
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
    		throw new XtentisException(err);    				
    	}
    	
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err);
    		throw new XtentisException(err);
    	}
    	String revisionID = universe.getConceptRevisionID(itemPOJOPK.getConceptName());
    	
    	//load the item
		return load(revisionID, itemPOJOPK);
	        	                                            

    }
    /**
     * 
     * @param itemPOJOPK
     * @return
     * @throws XtentisException
     */
    public static ItemPOJO adminLoad(ItemPOJOPK itemPOJOPK)throws XtentisException {
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err);
    		throw new XtentisException(err);
    	}
    	String revisionID = universe.getConceptRevisionID(itemPOJOPK.getConceptName());
    	
    	//load the item
		return load(revisionID, itemPOJOPK);    	
    }
    /**
     * Loads an Item<br/>
     * @param revisionID
     * @param itemPOJOPK
     * @return
     * 	the {@link ItemPOJO}
     * @throws XtentisException
     */
    public static ItemPOJO load(String revisionID, ItemPOJOPK itemPOJOPK) throws XtentisException {
    	return load(revisionID,itemPOJOPK,true);
    	 
    }

    /**
     * Loads an Item<br/>
     * @param itemPOJOPK
     * @param checkRights
     * @return
     * 	the {@link ItemPOJO}
     * @throws XtentisException
     */
    public static ItemPOJO load(String revisionID, ItemPOJOPK itemPOJOPK,boolean checkRights) throws XtentisException {
    	XmlServerSLWrapperLocal server=Util.getXmlServerCtrlLocal();

        try {
              //retrieve the item
            String urlid =getFilename(itemPOJOPK); 
//            ItemCacheKey key =new ItemCacheKey(revisionID,urlid, itemPOJOPK.getDataClusterPOJOPK().getUniqueId());
            String item=null;
            //the cache max size is 5000                        
//            if(cachedPojo.size()==5000){
//            	cachedPojo.clear();
//            }
//            if(cachedPojo.containsKey(key)){
//            	item=cachedPojo.get(key);
//            }else{
            	item = server.getDocumentAsString(revisionID, itemPOJOPK.getDataClusterPOJOPK().getUniqueId(), urlid);
//            	if(item!=null)cachedPojo.put(key, item);
//            }
                                    
            if (item==null) {
                return null;
            }
            
            ItemPOJO newItem = new ItemPOJO();
                      
            //Build the result
            newItem.setDataClusterPK(itemPOJOPK.getDataClusterPOJOPK());
            newItem.setConceptName(itemPOJOPK.getConceptName());
            newItem.setItemIds(itemPOJOPK.getIds());
            
            Matcher m = null;
            m = pLoad.matcher(item);
            if (m.matches()) {
            	String h = "<header>"+m.group(1)+"</header>";
            	Element header = Util.parse(h).getDocumentElement();
            	//used for binding data model
            	if(Util.getFirstTextNode(header, "dmn")!=null)newItem.setDataModelName(Util.getFirstTextNode(header, "dmn"));
            	if(Util.getFirstTextNode(header, "dmr")!=null)newItem.setDataModelRevision(Util.getFirstTextNode(header, "dmr"));
            	newItem.setInsertionTime(Long.parseLong(Util.getFirstTextNode(header, "t")));
            	String plan = Util.getFirstTextNode(header, "sp");
            	if (plan !=null)
            		newItem.setPlanPK(new SynchronizationPlanPOJOPK(plan));
            	else
            		newItem.setPlanPK(null);
            	
            	if(m.group(2)==null||m.group(2).equals("<p/>")){
            		newItem.setProjectionAsString("");
            	}else{
            		newItem.setProjectionAsString(m.group(3));
            	}
            	
            }else {
            	throw new XtentisException("Cannot parse item read from XML Server");
            }
            
             //check user rights
            if(checkRights&&newItem.getDataModelName()!=null){
            	try {
               
                	DataModelPOJO bindingDataModelPOJO =  ObjectPOJO.load(newItem.getDataModelRevision(), DataModelPOJO.class,new ObjectPOJOPK(newItem.getDataModelName()));
                	
    				AppinfoSourceHolder appinfoSourceHolder = new AppinfoSourceHolder(
    						new AppinfoSourceHolderPK(
    								newItem.getDataModelName(),
    								newItem.getConceptName())
    						);
    				SchemaManager.analyzeAnnotationsOfConcept(bindingDataModelPOJO, newItem.getConceptName(),appinfoSourceHolder);
    				
    				String itemContentString = newItem.getProjectionAsString();
    				HashSet<String> roles = LocalUser.getLocalUser().getRoles();
    				
    				Document cleanedDocument = SchemaManager.executeHideCheck(
    						itemContentString, roles, appinfoSourceHolder);
    				
    				if(cleanedDocument!=null)newItem.setProjectionAsString(Util
    						.nodeToString(cleanedDocument));
    			} catch (Exception e) {
    				String err = "Unable to check user rights of the item "+itemPOJOPK.getUniqueID()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	            org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err,e);
    			}
            }
            	
			return newItem;
	        	                                            
	    } catch (Exception e) {
    	    String err = "Unable to load the item  "+itemPOJOPK.getUniqueID()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err,e);
    	    throw new EJBException(err);
	    }
    }

    /**
     * Removes an item
     * @param itemPOJOPK
     * @return
     * 		The {@link ItemPOJOPK} of the item removed
     * @throws XtentisException
     */
    public static ItemPOJOPK remove(ItemPOJOPK itemPOJOPK) throws XtentisException {
    	
    	//org.apache.log4j.Logger.getLogger(this.getClass()).trace(
    	//        "remove() "+pk.getDataCluster().getName()+"."+Util.joinStrings(pk.getItemIds(), "."));
                
    	if (itemPOJOPK==null) return null;
    	
    	//for load we need to be admin, or have a role of admin , or role of write on instance or role of read on instance
    	boolean authorized = false;
    	LocalUser user = LocalUser.getLocalUser();
    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
    		authorized = true;
    	//} else if (user.userCanWrite(ItemPOJO.class, itemPOJOPK.getUniqueID())) {
    	}else if(XSystemObjects.isExist(XObjectType.DATA_CLUSTER, itemPOJOPK.getDataClusterPOJOPK().getUniqueId()) ||user.userItemCanWrite(adminLoad(itemPOJOPK),itemPOJOPK.getDataClusterPOJOPK().getUniqueId(),itemPOJOPK.getConceptName())){ //aiming modify see 10027
    		authorized = true;
    	}
    	
    	if (! authorized) {
    	    String err = 
    	    	"Unauthorized delete access by " +
    	    	"user "+user.getUsername()+" on Item '"+itemPOJOPK.getUniqueID()+"'"; 
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
    		throw new XtentisException(err);    				
    	}
    	
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err);
    		throw new XtentisException(err);
    	}
    	String revisionID = universe.getConceptRevisionID(itemPOJOPK.getConceptName());
    	
    	XmlServerSLWrapperLocal server=Util.getXmlServerCtrlLocal();
    	
    	
        try {    	   	
            //remove the doc
            long res = server.deleteDocument(
            		revisionID,
            		itemPOJOPK.getDataClusterPOJOPK().getUniqueId(),
            		getFilename(itemPOJOPK)
            );
            if (res==-1) return null;
//            ItemCacheKey key =new ItemCacheKey(revisionID,getFilename(itemPOJOPK), itemPOJOPK.getDataClusterPOJOPK().getUniqueId());
            //cachedPojo.remove(key);
            return itemPOJOPK;
            
	    } catch (Exception e) {
    	    String err = "Unable to remove the item "+itemPOJOPK.getUniqueID()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    }  

    }
    
    /**
     * @param itemPOJOPK
     * @param partPath
     * @return DroppedItemPOJOPK
     * @throws XtentisException
     * 
     * drop an item to items-trash
     */
    public static DroppedItemPOJOPK drop(ItemPOJOPK itemPOJOPK,String partPath) throws XtentisException {
    	
        //validate input
    	if (itemPOJOPK==null) return null;
    	if (partPath==null||partPath.equals(""))partPath="/";
    	
    	//for load we need to be admin, or have a role of admin , or role of write on instance or role of read on instance
    	boolean authorized = false;
    	LocalUser user = LocalUser.getLocalUser();
    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
    		authorized = true;
    	//} else if (user.userCanWrite(ItemPOJO.class, itemPOJOPK.getUniqueID())) {
    	}else if(XSystemObjects.isExist(XObjectType.DATA_CLUSTER, itemPOJOPK.getDataClusterPOJOPK().getUniqueId()) ||user.userItemCanWrite(adminLoad(itemPOJOPK), itemPOJOPK.getDataClusterPOJOPK().getUniqueId(),itemPOJOPK.getConceptName())){ //aiming modify see 10027    		
    		authorized = true;
    	}
    	
    	if (! authorized) {
    	    String err = 
    	    	"Unauthorized drop access by " +
    	    	"user "+user.getUsername()+" on Item '"+itemPOJOPK.getUniqueID()+"'"; 
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
    		throw new XtentisException(err);    				
    	}
    	String userName=user.getUsername();
    	
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err);
    		throw new XtentisException(err);
    	}
    	String revisionID = universe.getConceptRevisionID(itemPOJOPK.getConceptName());
    	
    	
    	//get XmlServerSLWrapperLocal
    	XmlServerSLWrapperLocal server=Util.getXmlServerCtrlLocal();
    	
		
        try {
        	//init MDMItemsTrash Cluster
    		if(ObjectPOJO.load(null,DataClusterPOJO.class,new DataClusterPOJOPK("MDMItemsTrash"))==null){
    			//create record
    			DataClusterPOJO dataCluster=new DataClusterPOJO("MDMItemsTrash","Holds logical deleted items",null); 
    			ObjectPOJOPK pk = dataCluster.store(null);
    		    if (pk == null) throw new XtentisException("Unable to create the Data Cluster. Please check the XML Server logs");
    		    
    		    //create cluster
    		    boolean exist=false;
    		    String[] clusters = server.getAllClusters(null);
    			if (clusters != null) {
    				exist = Arrays.asList(clusters).contains(pk.getUniqueId());
    			}
    			if (!exist) server.createCluster(null, pk.getUniqueId()); 
    			//log
    			org.apache.log4j.Logger.getLogger(ItemPOJO.class).info("Init MDMItemsTrash Cluster");
    		}
        	
        	String dataClusterName=itemPOJOPK.getDataClusterPOJOPK().getUniqueId();
        	String uniqueID=getFilename(itemPOJOPK);
        	
        	StringBuffer xmlDocument=new StringBuffer();
        	Document sourceDoc=null;
        	NodeList toDeleteNodeList=null;
        	String xml = server.getDocumentAsString(revisionID, dataClusterName, uniqueID,null);
        	if (xml==null) return null;
        	//get to delete item content
        	if(partPath.equals("/")){
        		
            	xmlDocument.append(xml);
            	
        	}else{
        		
        		String xPath ="/ii/p"+partPath;
        		
        		sourceDoc=Util.parse(xml);
        		toDeleteNodeList=Util.getNodeList(sourceDoc, xPath);
        		if(toDeleteNodeList.getLength()==0)throw new XtentisException("\nThe target content is not exist or have been deleted already.");
            	for (int i = 0; i < toDeleteNodeList.getLength(); i++) {
            		Node node=toDeleteNodeList.item(i);
            		xmlDocument.append(Util.nodeToString(node));
				}
        		
            	/* another way:
            	String query ="document('"+uniqueID+"')/ii/p"+partPath;
            	ArrayList<String> results=server.runQuery(revisionID, dataClusterName, query,null);
        		if (results==null||results.size()==0) return null; 
        		for (int i = 0; i < results.size(); i++) {
        			xmlDocument.append(results.get(i));
				}
        		*/
        	}
        	
        	//make source left doc && validate
        	if (partPath.equals("/")) {

			} else {
				if (toDeleteNodeList != null) {
					Node lastParentNode = null;
					Node formatSiblingNode = null;
					for (int i = 0; i < toDeleteNodeList.getLength(); i++) {
						Node node = toDeleteNodeList.item(i);
						lastParentNode = node.getParentNode();
						formatSiblingNode = node.getNextSibling();
						if (lastParentNode != null){
							lastParentNode.removeChild(node);
						}	
						if (formatSiblingNode != null && formatSiblingNode.getNodeValue() != null && formatSiblingNode.getNodeValue().matches("\\s+")){
							lastParentNode.removeChild(formatSiblingNode);
						}
					}
				}
				//validate
				String leftSourceDoc=Util.nodeToString(sourceDoc);
				ItemPOJO itemPOJO=parse(leftSourceDoc);
				if(itemPOJO.getDataModelName()!=null){
					DataModelPOJO dataModelPOJO=ObjectPOJO.load(itemPOJO.getDataModelRevision(), DataModelPOJO.class, new DataModelPOJOPK(itemPOJO.getDataModelName()));
					if(dataModelPOJO!=null){
						Element projection=null;
						try {
							projection = itemPOJO.getProjection();
						} catch (Exception e) {
							throw new XtentisException("\nThe remaining item can not be empty!");
						}
						
						if(projection!=null)Util.validate(itemPOJO.getProjection(), dataModelPOJO.getSchema());
					}
				}
				
			}
        	
        	//str 2 pojo
        	DroppedItemPOJO droppedItemPOJO=new DroppedItemPOJO(revisionID,itemPOJOPK.getDataClusterPOJOPK(),uniqueID,itemPOJOPK.getConceptName(),itemPOJOPK.getIds(),partPath,xmlDocument.toString(),userName,new Long(System.currentTimeMillis()));
        	
        	//Marshal
    		StringWriter sw = new StringWriter();
    		Marshaller.marshal(droppedItemPOJO, sw);
    		
        	//copy item content
        	long res=server.putDocumentFromString(sw.toString(), droppedItemPOJO.obtainDroppedItemPK().getUniquePK(), "MDMItemsTrash", null);
            if(res==-1)return null;
        	//delete source item
        	
            try {
				if (partPath.equals("/")) {

					server.deleteDocument(revisionID, dataClusterName,uniqueID);

				} else {
			
					server.putDocumentFromString(Util.nodeToString(sourceDoc),uniqueID, dataClusterName, revisionID);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// roll back
				server.deleteDocument(null, "MDMItemsTrash",droppedItemPOJO.obtainDroppedItemPK().getUniquePK());
				return null;
			}
            
            return droppedItemPOJO.obtainDroppedItemPK();
            
	    }
        catch (SAXException e) {
	    	String err = "The remaining item did not obey the rules of data model.\nYou can modify the data model, and try it again.\n\n"+e.getLocalizedMessage();
	    	throw new XtentisException(err);
	    }
	    catch (Exception e) {
    	    String err = "Unable to drop the item "+itemPOJOPK.getUniqueID()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    }  

    }

    /**
     * Stores the item in DB. Users rights will be checked
     * @return
     * 		The {@link ItemPOJOPK} of the stored item
     * @throws XtentisException
     */
    public ItemPOJOPK store() throws XtentisException {
        //org.apache.log4j.Logger.getLogger(this.getClass()).debug("store() "));
        
    	if (getItemPOJOPK()==null) return null;
    	    	
    	//for load we need to be admin, or have a role of admin , or role of write on instance or role of read on instance
    	boolean authorized = false;
    	LocalUser user = LocalUser.getLocalUser();
    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
    		authorized = true;
    	//} else if (user.userCanWrite(ItemPOJO.class, getItemPOJOPK().getUniqueID())) {
    	}else if(XSystemObjects.isExist(XObjectType.DATA_CLUSTER, getItemPOJOPK().getDataClusterPOJOPK().getUniqueId()) ||user.userItemCanWrite(adminLoad(getItemPOJOPK()),getItemPOJOPK().getDataClusterPOJOPK().getUniqueId(),getItemPOJOPK().getConceptName())){ //aiming modify see 10027    		
    		authorized = true;
    	}
    	
    	if (! authorized) {
    	    String err = 
    	    	"Unauthorized write access by " +
    	    	"user "+user.getUsername()+" on Item '"+getItemPOJOPK().getUniqueID()+"'"; 
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
    		throw new XtentisException(err);    				
    	}
    	
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err);
    		throw new XtentisException(err);
    	}
    	String revisionID = universe.getConceptRevisionID(getItemPOJOPK().getConceptName());
    	
    	//used for binding data model
    	if(this.getDataModelName()!=null){
    		
    		String objectName=ObjectPOJO.getObjectsClasses2NamesMap().get(DataModelPOJO.class);
    		String dataModelRevisionID = universe.getXtentisObjectsRevisionIDs().get(objectName);
    		if(dataModelRevisionID!=null)this.dataModelRevision=dataModelRevisionID;
    	}
    	
    	return store(revisionID);

    }
    
    /**
     * Stores the item in DB.<br/>
     * Users rights will NOT be checked
     * @return
     * 		The {@link ItemPOJOPK} of the stored item
     * @throws XtentisException
     */
    public ItemPOJOPK store(String revisionID) throws XtentisException {
    	
    	XmlServerSLWrapperLocal server=Util.getXmlServerCtrlLocal();
    	

        try {
        	
        	String xml = serialize();
    		org.apache.log4j.Logger.getLogger(this.getClass()).trace("store() "+getItemPOJOPK().getUniqueID()+"\n"+xml);
    		//check cluster exist or not
    		if(!server.existCluster(revisionID, getDataClusterPOJOPK().getUniqueId())){
    			return null;
    		}
            //store
            if ( -1 == server.putDocumentFromString(
            		xml, 
            		getFilename(getItemPOJOPK()),
            		getDataClusterPOJOPK().getUniqueId(),
            		revisionID
            	))
            	return null;
            //update the cache
//    		ItemCacheKey key =new ItemCacheKey(revisionID,getFilename(getItemPOJOPK()), getDataClusterPOJOPK().getUniqueId());
    		//cachedPojo.put(key, xml);                       
            return getItemPOJOPK();
	    } catch (Exception e) {
    	    String err = "Unable to store the item "+getItemPOJOPK().getUniqueID()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new EJBException(err);
	    } 

    }

    /**
     * Parses a marshaled item back into an {@link ItemPOJO}
     * @param marshaledItem
     * @return
     * 		the {@link ItemPOJO}
     * @throws XtentisException
     */
    public static ItemPOJO parse(String marshaledItem) throws XtentisException {
    	try {
    		ItemPOJO newItem = new ItemPOJO();
            Matcher m = null;
            m = pLoad.matcher(marshaledItem);
            if (m.matches()) {
            	String h = "<header>"+m.group(1)+"</header>";
            	Element header = Util.parse(h).getDocumentElement();
            	newItem.setConceptName(Util.getFirstTextNode(header, "n"));
            	//used for binding data model
            	if(Util.getFirstTextNode(header, "dmn")!=null)newItem.setDataModelName(Util.getFirstTextNode(header, "dmn"));
            	if(Util.getFirstTextNode(header, "dmr")!=null)newItem.setDataModelRevision(Util.getFirstTextNode(header, "dmr"));
            	newItem.setDataClusterPK(new DataClusterPOJOPK(Util.getFirstTextNode(header, "c")));
            	newItem.setItemIds(Util.getTextNodes(header, "i"));
            	newItem.setInsertionTime(Long.parseLong(Util.getFirstTextNode(header, "t")));
            	String plan = Util.getFirstTextNode(header, "sp");
            	if (plan !=null)
            		newItem.setPlanPK(new SynchronizationPlanPOJOPK(plan));
            	else
            		newItem.setPlanPK(null);
            	//newItem.setProjectionAsString(m.group(2));
            	if(m.group(2)==null||m.group(2).equals("<p/>")){
            		newItem.setProjectionAsString("");
            	}else{
            		newItem.setProjectionAsString(m.group(3));
            	}
            	return newItem;
            } else {
            	throw new XtentisException("Cannot parse item read from XML Server");
            }
        } catch (Exception e) {
        	String err = "Unable to parse the item \n"+marshaledItem;
        	org.apache.log4j.Logger.getLogger(ItemPOJO.class).error(err,e);
        	throw new XtentisException(err);
        }
        
    }
    
    /**
     * Serializes the object to an xml string
     * @return the xml string
     * 
     * Note: dmn&dmr tags are used for binding data model
     * 
     */
    public String serialize() throws XtentisException{
            String item =
                "<ii>" +
                "	<c>"+StringEscapeUtils.escapeXml(getDataClusterPOJOPK().getUniqueId())+"</c>" +
                "	<n>"+StringEscapeUtils.escapeXml(getConceptName())+"</n>" +
                "	<dmn>"+(getDataModelName()==null?"":StringEscapeUtils.escapeXml(getDataModelName()))+"</dmn>" +
                "	<dmr>"+(getDataModelRevision()==null?"":StringEscapeUtils.escapeXml(getDataModelRevision()))+"</dmr>" +
                "	<sp>"+(getPlanPK() == null ? "" : StringEscapeUtils.escapeXml(getPlanPK().getUniqueId()))+"</sp>";
            	String[] ids = getItemIds();
            	for (int i = 0; i < ids.length; i++) {
            		item+="	<i>"+(ids[i]==null? "" : StringEscapeUtils.escapeXml(ids[i].trim()))+"</i>";
    			}
                item+=
                "	<t>"+getInsertionTime()+"</t>";
                item+=        
                "	<p>"+getProjectionAsString()+"</p>" ; 
                item+=
                "</ii>";
            
        return item;
    }
    
    
	/**
     * Retrieve all {@link ItemPOJOPK}s of items matching a particular concept Pattern and instance pattern,
     * and that are unsynchronized against a particular plan<br/>
     * The user must have the "administration" role to perform this task
     * @param revisionID
     * @param conceptName
     * @param instancePattern
     * @param planPK
     * @return a Collection of ObjectPOJOPK
     * @throws XtentisException
     */
    public static ArrayList<ItemPOJOPK> findAllUnsynchronizedPKs(
    	String revisionID, 
    	DataClusterPOJOPK dataClusterPOJOPK,
    	String conceptName, 
    	String instancePattern, 
    	SynchronizationPlanPOJOPK planPK,
    	long start,
    	int limit
    ) throws XtentisException {
       	try {
       		
	    	//check if we are admin 
	    	LocalUser user = LocalUser.getLocalUser();
	    	if (!user.getRoles().contains("administration")) {
	    		String err = "Only an user with the 'administration' role can call the synchronization methods";
				org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
				throw new XtentisException(err);
	    	}
	    		    	
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
           
			String conceptPatternCondition = (conceptName == null || "".equals(conceptName)) ? "" : "[n/text() eq '"+conceptName+"']";
			String instancePatternCondition = (instancePattern == null || ".*".equals(instancePattern)) ? "" : "[matches(i/text(),'"+instancePattern+"')]";
			String synchronizationCondition = planPK == null ? "" : "[not (./sp/text() eq '"+planPK.getUniqueId()+"')]";
			String query =
				"let $a := /ii"+conceptPatternCondition+instancePatternCondition+synchronizationCondition+"\n"
				+"return subsequence($a,"+(start+1)+","+limit+")";
			
            //retrieve the objects
			ArrayList<String> res  = server.runQuery(
				revisionID, 
				dataClusterPOJOPK.getUniqueId(), 
				query, 
				null
			);
			
			ArrayList<ItemPOJOPK> list = new ArrayList<ItemPOJOPK>();
			for (Iterator<String> iterator = res.iterator(); iterator.hasNext(); ) {
				String marshaledItem = iterator.next();
				ItemPOJO pojo = parse(marshaledItem);
				list.add(pojo.getItemPOJOPK());
			}
			
			return list;
	    	
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Error Finding All Unsynchronized PKs"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    } 
    }
    
    /*************************************************************************************************************
     * 
     *  UTILITIES
     * 
     *************************************************************************************************************/
    
    
    @Override
    public String toString() {
    	try {
			return serialize();
		} catch (XtentisException e) {
			String err = "Unable to serialize the item: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE toString() "+err,e);
			throw new IllegalArgumentException(e.getMessage());
		}
    }

    public static String getFilename(ItemPOJOPK itemPOJOPK) throws UnsupportedEncodingException{
    	//return (new BASE64Encoder()).encode(getKeyAsString(itemPOJOPK).getBytes("utf-8"));
    	return itemPOJOPK.getUniqueID();
    }
    
    private static Pattern pathWithoutConditions = Pattern.compile("(.*?)[\\[|/].*");
	/**
	 * Returns the first part - eg. the concept - from the path
	 * @param path
	 * @return the Concept
	 */
    public static String getConceptFromPath(String path) {
    	if (!path.endsWith("/")) path+="/";
    	Matcher m = pathWithoutConditions.matcher(path);
    	if (m.matches()) {
    		return m.group(1);
    	} else {
    		return null;
    	}
    }

    public static String getBindingSchema(ItemPOJO itemPOJO) {
    	String schema=null;
    	try {
			String dataModelName=itemPOJO.getDataModelName();
			String dataModelRevision=itemPOJO.getDataModelRevision();
			if(dataModelName!=null&&dataModelName.length()>0){
				DataModelPOJO sp =  ObjectPOJO.load(dataModelRevision,DataModelPOJO.class,new DataModelPOJOPK(dataModelName));
	        	if (sp != null) {
	        		schema=sp.getSchema();
	        	}
			}
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		return schema;
	}
    
//    public static void clearCache(){
//    	//cachedPojo.clear();
//    }
}
